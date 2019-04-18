#version 330 core
#extension GL_ARB_explicit_uniform_location: enable

uniform sampler2D texture_diffuse;

in vec2 pass_TextureCoord;

//layout (location = 0) uniform int currentFrame;

out vec4 out_Color;

void main(void) {
	// Override out_Color with our texture pixel
	out_Color = texture(texture_diffuse, pass_TextureCoord);
}