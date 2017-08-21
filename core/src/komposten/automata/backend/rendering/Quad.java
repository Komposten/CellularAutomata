package komposten.automata.backend.rendering;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;


public class Quad extends Mesh
{
	private float[] vertexArray;
	private short[] indexArray;


	public Quad(float width, float height, boolean isStatic)
	{
		this(width, height, isStatic, Color.WHITE);
	}


	public Quad(float width, float height, boolean isStatic, Color color)
	{
		super(isStatic, 4, 6, Vertex.VERTEX_ATTRIBUTES);

		createVertices(width, height, color);
		createIndices();
		setVertices(vertexArray);
		setIndices(indexArray);
	}


	private void createVertices(float width, float height, Color color)
	{
		Vertex[] vertices = new Vertex[4];

		float r = color.r;
		float g = color.g;
		float b = color.b;
		float a = color.a;

		vertices[0] = new Vertex(0, 0, 0, 0, 1, r, g, b, a);
		vertices[1] = new Vertex(width, 0, 0, 1, 1, r, g, b, a);
		vertices[2] = new Vertex(width, height, 0, 1, 0, r, g, b, a);
		vertices[3] = new Vertex(0, height, 0, 0, 0, r, g, b, a);

		createVertexArray(vertices);
	}


	private void createVertexArray(Vertex[] vertices)
	{
		vertexArray = new float[vertices.length * Vertex.VALUES_PER_VERTEX];

		for (int i = 0; i < vertices.length; i++)
		{
			int index = i * Vertex.VALUES_PER_VERTEX;

			vertexArray[index + 0] = vertices[i].x;
			vertexArray[index + 1] = vertices[i].y;
			vertexArray[index + 2] = vertices[i].z;
			vertexArray[index + 3] = vertices[i].u;
			vertexArray[index + 4] = vertices[i].v;
			vertexArray[index + 5] = vertices[i].r;
			vertexArray[index + 6] = vertices[i].g;
			vertexArray[index + 7] = vertices[i].b;
			vertexArray[index + 8] = vertices[i].a;
		}
	}


	private void createIndices()
	{
		indexArray = new short[] { 0, 1, 3, 2, 1, 3 };
	}
}
