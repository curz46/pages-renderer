package me.dylancurzon.testgame;

import me.dylancurzon.testgame.designer.DesignerGame;

import java.io.IOException;

public class Bootstrapper {

    public static void main(String[] args) throws IOException {
        DesignerGame designer = new DesignerGame();
        designer.launch();

//        final SpritePacker packer = new SpritePacker(Sprites.getSprites());
//        final ByteBuffer buffer = packer.createBuffer();
////        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
//
//        final byte[] bytes = buffer.array();
//        DataBuffer dataBuffer = new DataBufferByte(bytes, bytes.length);
//
////3 bytes per pixel: red, green, blue
//        WritableRaster raster = Raster.createInterleavedRaster(dataBuffer, 32, 32, 3 * 32, 3, new int[] {0, 1, 2}, (Point)null);
//        ColorModel cm = new ComponentColorModel(ColorModel.getRGBdefault().getColorSpace(), false, true, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
//        BufferedImage image = new BufferedImage(cm, raster, true, null);
//
//        ImageIO.write(image, "png", new File("output.png"));
    }

}
