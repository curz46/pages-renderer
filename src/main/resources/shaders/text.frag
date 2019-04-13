#version 330 core

uniform sampler2DArray texture_diffuse;

in vec2 pass_TextureCoord;
flat in int pass_TextureIndex;
flat in float in_Depth;

out vec4 out_Color;

void main() {
	out_Color = texture(texture_diffuse, vec3(pass_TextureCoord, pass_TextureIndex));
    gl_FragDepth = in_Depth;
}
