#version 330 core

layout (location = 0) in vec2 in_Position;
layout (location = 1) in vec2 in_TextureCoord;
layout (location = 2) in float in_Depth;
layout (location = 3) in vec4 in_Bounds;

out vec2 pass_TextureCoord;
flat out float pass_Depth;
flat out vec4 pass_Bounds;

void main() {
	gl_Position = vec4(in_Position, 0.0f, 1.0f);

	pass_TextureCoord = in_TextureCoord;
	pass_Depth = in_Depth;
	pass_Bounds = in_Bounds;
}
