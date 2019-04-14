#version 330 core
#extension GL_ARB_explicit_uniform_location: enable

flat in vec4 pass_Color;
flat in float pass_Depth;
flat in vec4 pass_Bounds;

out vec4 out_Color;

bool withinBounds(vec2 position, vec4 bounds) {
    return position.x >= bounds.x
        && position.y >= bounds.y
        && position.x <= bounds.z
        && position.y <= bounds.w;
}

void main(void) {
    if (!withinBounds(gl_FragCoord.xy, pass_Bounds)) {
        discard;
    }
	// Override out_Color with our texture pixel
	out_Color = pass_Color;
	gl_FragDepth = pass_Depth;
}