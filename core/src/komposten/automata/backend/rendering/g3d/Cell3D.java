package komposten.automata.backend.rendering.g3d;

import com.badlogic.gdx.graphics.Color;

import komposten.automata.backend.rendering.Vertex;

public class Cell3D
{
	public static final int VERTICES_PER_CELL = 36;
	
	private Vertex[] vertices;
	private boolean visible;
	
	public Cell3D(float x, float y, float z, float size, Color color)
	{
		vertices = new Vertex[VERTICES_PER_CELL];
		
		//TODO Cell3D; Fix tex coords.
		float alpha = color.a;
		
		//Bottom
		float red = color.r * 0.2f;
		float green = color.g * 0.2f;
		float blue = color.b * 0.2f;
		vertices[0] = new Vertex(x, y+size, z, 0, 0, red, green, blue, alpha); //Lower back left
		vertices[1] = new Vertex(x+size, y+size, z, 1, 0, red, green, blue, alpha); //Lower back right
		vertices[2] = new Vertex(x+size, y, z, 1, 1, red, green, blue, alpha); //Lower front right
		vertices[3] = new Vertex(x, y+size, z, 0, 0, red, green, blue, alpha); //Lower back left
		vertices[4] = new Vertex(x+size, y, z, 1, 1, red, green, blue, alpha); //Lower front right
		vertices[5] = new Vertex(x, y, z, 0, 1, red, green, blue, alpha); //Lower front left

		//Top
		red = color.r;
		green = color.g;
		blue = color.b;
		vertices[6] = new Vertex(x, y, z+size, 0, 1, red, green, blue, alpha); //Upper front left
		vertices[7] = new Vertex(x+size, y, z+size, 1, 1, red, green, blue, alpha); //Upper front right
		vertices[8] = new Vertex(x, y+size, z+size, 0, 0, red, green, blue, alpha); //Upper back left
		vertices[9] = new Vertex(x+size, y+size, z+size, 1, 0, red, green, blue, alpha); //Upper back right
		vertices[10] = new Vertex(x, y+size, z+size, 0, 0, red, green, blue, alpha); //Upper back left
		vertices[11] = new Vertex(x+size, y, z+size, 1, 1, red, green, blue, alpha); //Upper front right

		//Left
		red = color.r * 0.8f;
		green = color.g * 0.8f;
		blue = color.b * 0.8f;
		vertices[12] = new Vertex(x, y+size, z+size, 0, 0, red, green, blue, alpha); //Upper back left
		vertices[13] = new Vertex(x, y, z, 0, 1, red, green, blue, alpha); //Lower front left
		vertices[14] = new Vertex(x, y, z+size, 0, 1, red, green, blue, alpha); //Upper front left
		vertices[15] = new Vertex(x, y+size, z+size, 0, 0, red, green, blue, alpha); //Upper back left
		vertices[16] = new Vertex(x, y+size, z, 0, 0, red, green, blue, alpha); //Lower back left
		vertices[17] = new Vertex(x, y, z, 0, 1, red, green, blue, alpha); //Lower front left
		
		//Right
		red = color.r * 0.4f;
		green = color.g * 0.4f;
		blue = color.b * 0.4f;
		vertices[18] = new Vertex(x+size, y+size, z, 1, 0, red, green, blue, alpha); //Lower back right
		vertices[19] = new Vertex(x+size, y+size, z+size, 1, 0, red, green, blue, alpha); //Upper back right
		vertices[20] = new Vertex(x+size, y, z, 1, 1, red, green, blue, alpha); //Lower front right
		vertices[21] = new Vertex(x+size, y, z+size, 1, 1, red, green, blue, alpha); //Upper front right
		vertices[22] = new Vertex(x+size, y, z, 1, 1, red, green, blue, alpha); //Lower front right
		vertices[23] = new Vertex(x+size, y+size, z+size, 1, 0, red, green, blue, alpha); //Upper back right
		
		//Front
		red = color.r * 0.6f;
		green = color.g * 0.6f;
		blue = color.b * 0.6f;
		vertices[24] = new Vertex(x, y, z, 0, 1, red, green, blue, alpha); //Lower front left
		vertices[25] = new Vertex(x+size, y, z, 1, 1, red, green, blue, alpha); //Lower front right
		vertices[26] = new Vertex(x, y, z+size, 0, 1, red, green, blue, alpha); //Upper front left
		vertices[27] = new Vertex(x+size, y, z+size, 1, 1, red, green, blue, alpha); //Upper front right
		vertices[28] = new Vertex(x, y, z+size, 0, 1, red, green, blue, alpha); //Upper front left
		vertices[29] = new Vertex(x+size, y, z, 1, 1, red, green, blue, alpha); //Lower front right
		
		//Back
		red = color.r * 0.6f;
		green = color.g * 0.6f;
		blue = color.b * 0.6f;
		vertices[30] = new Vertex(x, y+size, z, 0, 0, red, green, blue, alpha); //Lower back left
		vertices[31] = new Vertex(x, y+size, z+size, 0, 0, red, green, blue, alpha); //Upper back left
		vertices[32] = new Vertex(x+size, y+size, z, 1, 0, red, green, blue, alpha); //Lower back right
		vertices[33] = new Vertex(x+size, y+size, z+size, 1, 0, red, green, blue, alpha); //Upper back right
		vertices[34] = new Vertex(x+size, y+size, z, 1, 0, red, green, blue, alpha); //Lower back right
		vertices[35] = new Vertex(x, y+size, z+size, 0, 0, red, green, blue, alpha); //Upper back left
	}
	
	
	public void setVisible(boolean visible)
	{
		this.visible = visible;
	}
	
	
	public boolean isVisible()
	{
		return visible;
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
}
