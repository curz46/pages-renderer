package me.dylancurzon.dontdie.gfx.page;

import me.dylancurzon.dontdie.gfx.Renderer;
import me.dylancurzon.dontdie.gfx.opengl.Texture;
import me.dylancurzon.dontdie.gfx.opengl.VertexBuffer;
import me.dylancurzon.dontdie.sprite.Sprite;
import me.dylancurzon.dontdie.sprite.SpritePacker;
import me.dylancurzon.dontdie.sprite.TextSprite;
import me.dylancurzon.dontdie.sprite.TextSpriteProvider;
import me.dylancurzon.dontdie.util.ShaderUtil;
import me.dylancurzon.pages.element.text.MutableTextElement;
import me.dylancurzon.pages.util.Vector2d;
import me.dylancurzon.pages.util.Vector2i;
import org.lwjgl.opengl.ARBShaderObjects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

public class TextElementRenderer extends Renderer {

    private final Set<TextSpriteProvider> supportedProviders;
    private final List<FlattenedElement> flattenedElements = new ArrayList<>();

    private int program;

    private SpritePacker packer;
    private Texture packerTexture;

    private VertexBuffer positions;
    private VertexBuffer textureCoords;
    private VertexBuffer depthBuffer;
    private VertexBuffer boundsBuffer;

    private int vertices;

    public TextElementRenderer(TextSpriteProvider... supportedProviders) {
        this.supportedProviders = Set.of(supportedProviders);
    }

    public void addElement(FlattenedElement textElement) {
        if (!(textElement.getMutableElement() instanceof MutableTextElement)) {
            throw new IllegalArgumentException(
                "Argument textElement#getMutableElement doesn ot refer to a MutableTextElement: "
                    + textElement.getMutableElement());
        }
        flattenedElements.add(textElement);
    }

    public void clearElements() {
        flattenedElements.clear();
    }

    @Override
    public void prepare() {
        program = ShaderUtil.createShaderProgram("page_text");
        positions = VertexBuffer.make();
        textureCoords = VertexBuffer.make();
        depthBuffer = VertexBuffer.make();
        boundsBuffer = VertexBuffer.make();

        Set<Sprite> sprites = supportedProviders.stream()
            .flatMap(provider -> Arrays.stream(provider.getSprites()))
            .collect(Collectors.toSet());
        packer = new SpritePacker(sprites);
        packerTexture = Texture.make(packer);

        update();
    }

    @Override
    public void cleanup() {
        // TODO: Destroy shader
        positions.destroy();
        textureCoords.destroy();
        depthBuffer.destroy();
        boundsBuffer.destroy();
        packerTexture.destroy();
        packerTexture = null;
        packer = null;
    }

    @Override
    public void render() {
        ARBShaderObjects.glUseProgramObjectARB(program);
        packerTexture.bind();

        positions.bind();
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
        textureCoords.bind();
        glEnableVertexAttribArray(1);
        // TODO: Investigate why GL_INT here doesn't work
        // (makes all characters a Z)
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        depthBuffer.bind();
        glEnableVertexAttribArray(2);
        // TODO: I'm just making this float as well because debugging this would probably take several hours which
        //       I don't have.
        glVertexAttribPointer(2, 1, GL_FLOAT, false, 0, 0);
        boundsBuffer.bind();
        glEnableVertexAttribArray(3);
        glVertexAttribPointer(3, 4, GL_FLOAT, false, 0, 0);

        glDrawArrays(GL_QUADS, 0, vertices);

        ARBShaderObjects.glUseProgramObjectARB(0);
    }

    public void update() {
        int vertices = 0;
        for (FlattenedElement flattenedElement : flattenedElements) {
            MutableTextElement textElement = ((MutableTextElement) flattenedElement.getMutableElement());
            TextSprite textSprite = (TextSprite) textElement.getSprite();
            vertices += textSprite.getSprites().length * 4;
        }

        float[] positionsData = new float[vertices * 2];
        float[] coordsData = new float[vertices * 2];
        float[] depthData = new float[vertices];
        float[] boundsData = new float[vertices * 4];

        int iPosition = 0;
        int iCoord = 0;
        int iDepth = 0;
        int iBounds = 0;

        elements: for (FlattenedElement flattenedElement : flattenedElements) {
            MutableTextElement textElement = ((MutableTextElement) flattenedElement.getMutableElement());
            TextSprite textSprite = (TextSprite) textElement.getSprite();
            Vector2i spritePosition = flattenedElement.getPosition();

            // TODO: Hardcoding virtual size!
            Vector2i boundA = flattenedElement.getBoundA().orElse(Vector2i.of(0, 0));
            Vector2i boundB = flattenedElement.getBoundB().orElse(Vector2i.of(256, 192));
            // TODO: I have no idea why 192 - Y is necessary, but based on testing this is correct.
            float[] bounds = {boundA.getX(), 192 - boundB.getY(), boundB.getX(), 192 - boundA.getY()};
//            float[] bounds = toClipSpace(boundA, boundB);

            for (int i = 0; i < textSprite.getSprites().length; i++) {
                Sprite sprite = textSprite.getSprites()[i];
                Vector2i pos1 = spritePosition
                    .add(Vector2i.of((sprite.getWidth() + textSprite.getSpacing()) * i, 0));
                Vector2i pos2 = spritePosition
                        .add(Vector2i.of((sprite.getWidth() + textSprite.getSpacing()) * i, 0))
                        .add(Vector2i.of(sprite.getWidth(), sprite.getHeight()));
                float[][] glPositions = {
//                    (float) pos1.getX(), (float) pos1.getY(),
                    toClipSpace(pos1),
//                    (float) pos2.getX(), (float) pos1.getY(),
                    toClipSpace(Vector2i.of(pos2.getX(), pos1.getY())),
//                    (float) pos2.getX(), (float) pos2.getY(),
                    toClipSpace(pos2),
//                    (float) pos1.getX(), (float) pos2.getY()
                    toClipSpace(Vector2i.of(pos1.getX(), pos2.getY()))
                };
//                for (float glPos : glPositions) {
//                    positionsData[iPosition++] = glPos;
//                }
//                float[][] fillPositions = {
//                    toClipSpace(spritePosition.add(Vector2i.of(
//                        (sprite.getWidth() + textSprite.getSpacing()) * i,
//                        0
//                    ))),
//                    toClipSpace(spritePosition.add(Vector2i.of(
//                        (sprite.getWidth() + textSprite.getSpacing()) * i + sprite.getWidth(),
//                        0
//                    ))),
//                    toClipSpace(spritePosition.add(Vector2i.of(
//                        (sprite.getWidth() + textSprite.getSpacing()) * i + sprite.getWidth(),
//                        sprite.getHeight()
//                    ))),
//                    toClipSpace(spritePosition.add(Vector2i.of(
//                        (sprite.getWidth() + textSprite.getSpacing()) * i,
//                        sprite.getHeight()
//                    )))
//                };
//                float[][] glPositions = {
//                    {-1.0f, -1.0f},
//                    {1.0f, -1.0f},
//                    {1.0f, 1.0f},
//                    {-1.0f, 1.0f}
//                };
                for (int j = 0; j < glPositions.length; j++) {
                    for (int k = 0; k < 2; k++) {
                        positionsData[iPosition++] = glPositions[j][k];
                    }
                }

                Vector2i position = packer.getSpritePosition(sprite).orElseThrow(() -> new IllegalStateException(
                    "Sprite of TextSprite not found in supported TextSpriteProviders..."));
                float startX = ((float) position.getX()) / packer.getWidth();
                float startY = ((float) position.getY()) / packer.getHeight();
                float endX = ((float) (position.getX() + sprite.getWidth())) / packer.getWidth();
                float endY = ((float) (position.getY() + sprite.getHeight())) / packer.getHeight();
//                float startX = ((float) 0) / packer.getWidth();
//                float startY = ((float) 0) / packer.getHeight();
//                float endX = ((float) (100)) / packer.getWidth();
//                float endY = ((float) (100)) / packer.getHeight();
                float[] texCoords = {
                    startX, startY,
                    endX, startY,
                    endX, endY,
                    startX, endY
                };
                for (float texCoord : texCoords) {
                    coordsData[iCoord++] = texCoord;
                }

                float depth = textElement.getZIndex() / 100.f;
                for (int j = 0; j < 4; j++) {
                    depthData[iDepth++] = depth;
                }

                // 4 vertices for each Sprite
                for (int j = 0; j < 4; j++) {
                    // 4 floats for each Vertex
                    for (int k = 0; k < 4; k++) {
                        boundsData[iBounds++] = bounds[k];
                    }
                }
            }
        }

        positions.bind();
        positions.upload(positionsData);
        textureCoords.bind();
        textureCoords.upload(coordsData);
        depthBuffer.bind();
        depthBuffer.upload(depthData);
        boundsBuffer.bind();
        boundsBuffer.upload(boundsData);
        VertexBuffer.unbind();

        this.vertices = vertices;
    }

    private float[] toClipSpace(Vector2i virtualPosition) {
        int x = virtualPosition.getX();
        int y = virtualPosition.getY();
        return new float[]{
            (x + 0) / 256.0f * 2.0f - 1.0f, -((y + 0) / 192.0f * 2.0f - 1.0f),
        };
    }

}
