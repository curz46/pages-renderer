#version 330 core
#extension GL_ARB_explicit_uniform_location: enable

layout (location = 0) in vec2 in_Position;
layout (location = 1) in vec4 in_Color;

flat out vec4 pass_Color;

void main() {
	gl_Position = vec4(in_Position, 0.0f, 1.0f);

	pass_Color = in_Color;
}
