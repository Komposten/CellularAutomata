package komposten.automata.backend.rendering.g3d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.utils.Disposable;

import komposten.automata.backend.rendering.Vertex;


public class GridMesh3D implements Disposable
{
	private Mesh mesh;
	private Cell3D[] cells;
	private float[] vertexArray;
	
	private int width; //X
	private int height; //Y
	private int depth; //Z
	private int cellSize;
	
	


	public GridMesh3D(int rows, int columns, int levels, int cellSize)
	{
		this.width = columns;
		this.height = rows;
		this.depth = levels;
		this.cellSize = cellSize;
		
		createCells();
		createMesh();
	}


	private void createCells()
	{
		cells = new Cell3D[width * height * depth];

		RandomXS128 random = new RandomXS128();
		//Create cells.
		for (int x = 0; x < width; x++)
		{
			for (int y = 0; y < height; y++)
			{
				for (int z = 0; z < depth; z++)
				{
					int index = getIndex(x, y, z);
					
					float xPos = x * cellSize;
					float yPos = y * cellSize;
					float zPos = z * cellSize;
					
					Cell3D cell = new Cell3D(xPos, yPos, zPos, cellSize, new Color(random.nextFloat(), random.nextFloat(), random.nextFloat(), 1));
					
					cells[index] = cell;
				}
			}
		}
		

		System.out.println("===CELLS: [" + width + ", " + height + ", " + depth + "]=" + cells.length);
//		System.out.println("===lower left: " + vertices[0].toString(true) + ", upper right: " + vertices[vertices.length-1].toString(true));
//		printArray(vertices, columnVertices);
	}


	private void createMesh()
	{
		createVertexArray();
		
//		System.out.println("Creating mesh...");
		if (mesh == null)
			mesh = new Mesh(false, true, vertexArray.length, 0, Vertex.VERTEX_ATTRIBUTES);
		
		mesh.setVertices(vertexArray);
		
//		System.out.println("Mesh created!");
		
		System.out.println("Vertex objects: " + (cells.length * Cell3D.VERTICES_PER_CELL) + ", floats: " + vertexArray.length + ", in mesh: " + mesh.getNumVertices());
	}
	
	
	private void createVertexArray()
	{
		int valuesPerCell = Cell3D.VERTICES_PER_CELL * Vertex.VALUES_PER_VERTEX;
		vertexArray = new float[cells.length * valuesPerCell];
		
		for (int i = 0; i < cells.length; i++)
		{
			Cell3D cell = cells[i];
			int index = i * valuesPerCell;
			
			for (int j = 0; j < Cell3D.VERTICES_PER_CELL; j++)
			{
				int k = index + j*Vertex.VALUES_PER_VERTEX;
				Vertex vertex = cell.getVertices()[j];
				
				vertexArray[k+0] = vertex.x;
				vertexArray[k+1] = vertex.y;
				vertexArray[k+2] = vertex.z;
				vertexArray[k+3] = vertex.u;
				vertexArray[k+4] = vertex.v;
				vertexArray[k+5] = vertex.r;
				vertexArray[k+6] = vertex.g;
				vertexArray[k+7] = vertex.b;
				vertexArray[k+8] = vertex.a;
			}
		}
	}
	
	
	public Mesh getMesh()
	{
		return mesh;
	}
	
	
	public int getIndex(int x, int y, int z)
	{
		return width * (y * depth + z) + x;
	}
	
	
	public void setColor(Color color, int index)
	{
		cells[index].setColor(color);
		//FIXME GridMesh3; After updating a cell, add it to a "dirty" list. When refreshMesh() is called, don't recreate the entire vertex array. Instead just locate the vertices of the dirty cells and update them (then call setVertices()).
	}
	
	
	public void refreshMesh()
	{
		createVertexArray();
		mesh.updateVertices(0, vertexArray);
	}
	
	
	@Override
	public void dispose()
	{
		mesh.dispose();
	}
}
