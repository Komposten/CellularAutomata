package komposten.automata.backend.rendering;

import com.badlogic.gdx.graphics.Color;

public class Cell
{
	public static final int VERTICES_PER_CELL = 4;
	public static final int TRIANGLES_PER_CELL = 2;
	
	private Vertex[] vertices;
	private Triangle[] triangles;
	
	public Cell(float x, float y, float width, float height, Color color)
	{
		vertices = new Vertex[VERTICES_PER_CELL];
		vertices[0] = new Vertex(x, y, 0, 0, 1, color.r, color.g, color.b, color.a); //Lower left
		vertices[1] = new Vertex(x+width, y, 0, 1, 1, color.r, color.g, color.b, color.a); //Lower right
		vertices[2] = new Vertex(x+width, y+height, 0, 1, 0, color.r, color.g, color.b, color.a); //Upper right
		vertices[3] = new Vertex(x, y+height, 0, 0, 0, color.r, color.g, color.b, color.a); //Upper left
		
		triangles = new Triangle[TRIANGLES_PER_CELL];
		triangles[0] = new Triangle(0, 1, 3);
		triangles[1] = new Triangle(2, 1, 3);
	}
	
	
	public void setColor(Color color)
	{
		for (int i = 0; i < vertices.length; i++)
		{
			Vertex vertex = vertices[i];
			
			vertex.r = color.r;
			vertex.g = color.g;
			vertex.b = color.b;
			vertex.a = color.a;
		}
	}
	
	
	public Vertex[] getVertices()
	{
		return vertices;
	}
	
	
	public Triangle[] getTriangles()
	{
		return triangles;
	}
}
