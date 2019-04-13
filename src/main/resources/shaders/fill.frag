#version 330 core
#extension GL_ARB_explicit_uniform_location: enable

flat in vec4 pass_Color;
flat in float pass_Depth;

out vec4 out_Color;

void main(void) {
	// Override out_Color with our texture pixel
	out_Color = pass_Color;
	gl_FragDepth = pass_Depth;
//    out_Color = vec4(vec3(pass_Depth), 1.0f);
}