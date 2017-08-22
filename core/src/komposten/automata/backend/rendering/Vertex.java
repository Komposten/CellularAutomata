package komposten.automata.backend.rendering;

import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class Vertex
{
	public static final VertexAttributes VERTEX_ATTRIBUTES = new VertexAttributes(
			new VertexAttribute(Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
			new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "0"),
			new VertexAttribute(Usage.ColorUnpacked, 4, ShaderProgram.COLOR_ATTRIBUTE));

	public static final int VALUES_PER_VERTEX = 9;
	
	public float x;
	public float y;
	public float z;
	public float u;
	public float v;
	public float r;
	public float g;
	public float b;
	public float a;


	public Vertex(float x, float y, float z, float u, float v, float r, float g, float b, float a)
	{
		set(x, y, z, u, v, r, g, b, a);
	}
	
	
	public void set(float x, float y, float z, float u, float v, float r, float g, float b, float a)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.u = u;
		this.v = v;
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}


	public String toString(boolean onlyPos)
	{
		if (onlyPos)
		{
			return "[" + x + "; " + y + "]";
		}
		else
		{
			return "[" + x + "; " + y + "; " + z + " | " + u + "; " + v + "]";
		}
	}
}
