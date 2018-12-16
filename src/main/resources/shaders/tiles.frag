#version 330 core

uniform sampler2DArray texture_diffuse;

in vec2 pass_TextureCoord;
in float pass_TextureIndex;

out vec4 out_Color;

void main(void) {
	// Override out_Color with our texture pixel
	out_Color = texture(texture_diffuse, vec3(pass_TextureCoord, pass_TextureIndex));
}