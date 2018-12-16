#version 330 core
#extension GL_ARB_explicit_uniform_location: enable

layout (location = 0) in vec2 in_Position;
layout (location = 1) in vec2 in_TextureCoord;
layout (location = 2) in float in_TextureIndex;

uniform layout (location = 0) vec2 u_CameraFixed;
uniform layout (location = 1) vec2 u_CameraSize;
uniform layout (location = 2) vec2 u_CameraDelta;

out vec2 pass_TextureCoord;
out float pass_TextureIndex;

void main(void) {
    vec2 out_Position = ((in_Position - u_CameraFixed + u_CameraDelta) / u_CameraSize) * 2;
    gl_Position = vec4(out_Position, 0.0f, 1.0f);

 	pass_TextureCoord = in_TextureCoord;
 	pass_TextureIndex = in_TextureIndex;
 }