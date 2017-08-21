#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

varying LOWP vec4 vColor;
varying      vec2 vTexCoords;

uniform sampler2D u_texture;

void main()
{
  vec4 fColor = texture2D(u_texture, vTexCoords);
  
  gl_FragColor = vColor * fColor;//vColor * vec4(vec3(1)-fColor.rgb, 1);
}
