#version 330 core

layout (location = 0) in vec2 in_Position;
layout (location = 1) in vec2 in_TextureCoord;

out vec2 pass_TextureCoord;

void main() {
	gl_Position = vec4(in_Position, 0.0f, 1.0f);

	pass_TextureCoord = in_TextureCoord;
}
