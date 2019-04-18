#version 330 core

uniform sampler2D texture_diffuse;

in vec2 pass_TextureCoord;
flat in float pass_Depth;
flat in vec4 pass_Bounds;

out vec4 out_Color;

bool withinBounds(vec2 position, vec4 bounds) {
    return position.x >= bounds.x
        && position.y >= bounds.y
        && position.x <= bounds.z
        && position.y <= bounds.w;
}

void main() {
    if (!withinBounds(gl_FragCoord.xy, pass_Bounds)) {
        discard;
    }
	out_Color = texture(texture_diffuse, pass_TextureCoord);
    gl_FragDepth = pass_Depth;
}