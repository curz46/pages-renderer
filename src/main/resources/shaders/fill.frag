#version 330 core
#extension GL_ARB_explicit_uniform_location: enable

flat in vec4 pass_Color;

out vec4 out_Color;

void main(void) {
	// Override out_Color with our texture pixel
	out_Color = pass_Color;
}