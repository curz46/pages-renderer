#version 330 core
#extension GL_ARB_explicit_uniform_location: enable

layout (location = 0) in vec2 in_Position;
layout (location = 1) in vec4 in_Color;
layout (location = 2) in float in_Depth;
layout (location = 3) in vec4 in_Bounds;

flat out vec4 pass_Color;
flat out float pass_Depth;
flat out vec4 pass_Bounds;

void main() {
	gl_Position = vec4(in_Position, 0.0f, 1.0f);

	pass_Color = in_Color;
	pass_Depth = in_Depth;
	pass_Bounds = in_Bounds;
}
