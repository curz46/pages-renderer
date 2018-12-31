#version 330 core

layout (location = 0) in vec4 in_Position;
layout (location = 1) in vec2 in_TextureCoord;

out vec2 pass_TextureCoord;

void main(void) {
    gl_Position = in_Position;

 	pass_TextureCoord = in_TextureCoord;
 }