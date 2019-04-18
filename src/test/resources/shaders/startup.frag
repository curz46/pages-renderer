#version 330 core
#extension GL_ARB_explicit_uniform_location: enable

uniform sampler2D texture_diffuse;

in vec2 pass_TextureCoord;

layout (location = 0) uniform vec2 u_CircleDelta;

out vec4 out_Color;

float get_distance(vec2 circleCoord) {
	vec2 delta = circleCoord - gl_FragCoord.xy;
	return sqrt(pow(abs(delta.x), 2) + pow(abs(delta.y), 2));
}

bool is_white(vec4 color) {
    return color.r == 1.0f;
}

void main(void) {
    // TODO: This circle rendering is not ideal in terms of performance, but should suffice since
    // this is only a startup sequence

//    float circleRadius = 85;



    // Always render a white Circle behind the text
    vec2 statCircleCoord = vec2(256 / 2, 192 / 2);
    float statCircleRadius = 80;
    vec2 bounceCircleCoord = vec2((0.5f + u_CircleDelta.x) * 256, (0.45f + u_CircleDelta.y) * 192);
    float bounceCircleRadius = 80;

//    out_Color = vec4(get_distance(statCircleCoord), 0, 0, 1.0f);

    bool isStatCircle = get_distance(statCircleCoord) <= statCircleRadius;
    bool isBounceCircle = get_distance(bounceCircleCoord) <= bounceCircleRadius;
    bool isText = is_white(texture(texture_diffuse, pass_TextureCoord));

    if (isStatCircle) {
//	    out_Color = (!isBounceCircle != isText)
//	        ? vec4(1.0f, 1.0f, 1.0f, 1.0f)
//	        : vec4(0.0f, 0.0f, 0.0f, 0.0f);
//        out_Color = (isBounceCircle && isText)
//            ? vec4(1.0f, 1.0f, 1.0f, 1.0f)
//            : vec4(0.0f, 0.0f, 0.0f, 0.0f);
        if (!isBounceCircle || isText) {
            out_Color = vec4(1.0f, 1.0f, 1.0f, 1.0f);
        } else if (isText) {
            out_Color = vec4(0.0f, 0.0f, 0.0f, 0.0f);
        }
    } else {
        out_Color = vec4(0.0f, 0.0f, 0.0f, 0.0f);
    }

    if (isBounceCircle && isText) {
        out_Color = vec4(1.0f, 1.0f, 1.0f, 1.0f);
    }
}