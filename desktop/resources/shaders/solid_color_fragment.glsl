#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

uniform sampler2D u_texture;

// Solid color
uniform vec4 u_color;
// Vertex color
varying vec4 v_color;
// Texture coordinate
varying vec2 v_texCoord;

void main() {
	vec2 T = v_texCoord.xy;

	// u_color.a gauges the amount of the solid color vs original color (1: solid, 0: original)
	gl_FragColor = vec4(
   		u_color.r * u_color.a + v_color.r * (texture2D(u_texture, v_texCoord.xy).r * (1.0 - u_color.a)),
   		u_color.g * u_color.a + v_color.g * (texture2D(u_texture, v_texCoord.xy).g * (1.0 - u_color.a)),
   		u_color.b * u_color.a + v_color.b * (texture2D(u_texture, v_texCoord.xy).b * (1.0 - u_color.a)),
   		texture2D(u_texture, v_texCoord.xy).a);
   	gl_FragColor.rgb *= gl_FragColor.a;
}