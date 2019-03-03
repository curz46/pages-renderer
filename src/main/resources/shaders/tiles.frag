#version 330 core

uniform sampler2D texture_diffuse;

in vec2 pass_TextureCoord;
//flat in float pass_TextureIndex;

out vec4 out_Color;

void main(void) {
	// Override out_Color with our texture pixel
	out_Color = texture(texture_diffuse, pass_TextureCoord);
//	out_Color = vec4(0, pass_TextureCoord.y, pass_TextureCoord.x, 1.0f);
//    out_Color = vec4(0.07f * (pass_TextureIndex + 1), 0, 0, 1.0f);
//    out_Color = vec4(pass_TextureIndex / 2000.0, 0, 0, 1);
//    out_Color = vec4(0.5f * pass_TextureCoord.x * 100, 0.5f * pass_TextureCoord.y * 100, 0, 1.0f);
}