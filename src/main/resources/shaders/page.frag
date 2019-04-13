#version 330

uniform sampler2D texture_diffuse;

in vec2 pass_TextureCoord;
flat in float pass_Depth;

out vec4 out_Color;

void main() {
	out_Color = texture(texture_diffuse, pass_TextureCoord);
	gl_FragDepth = pass_Depth;
//    out_Color = vec4(vec3(pass_Depth), 1.0f);
}
