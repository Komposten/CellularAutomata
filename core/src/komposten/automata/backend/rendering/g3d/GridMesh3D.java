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
	
	
	public GridMesh3D(int rows, int columns, int layers, int cellSize)
	{
		this.width = columns;
		this.height = rows;
		this.depth = layers;
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
					
					Color colour = new Color(random.nextFloat(), random.nextFloat(), random.nextFloat(), 1);
					Cell3D cell = new Cell3D(xPos, yPos, zPos, cellSize, colour);
					
					cells[index] = cell;
				}
			}
		}
		
		int cellCount = cells.length;
		int vertexCount = cellCount * Cell3D.VERTICES_PER_CELL;
		int floatCount = vertexCount * Vertex.VALUES_PER_VERTEX;
		System.out.println("===CELLS:\n" + "  " + width + " * " + height + " * "
				+ depth + " => " + cellCount + " cells.\n" + "  " + cellCount + " * "
				+ Cell3D.VERTICES_PER_CELL + " = " + vertexCount + " vertices.\n" + "  "
				+ vertexCount + " * " + Vertex.VALUES_PER_VERTEX + " = " + floatCount
				+ " floats.");
	}


	private void createMesh()
	{
		createVertexArray();
		
		if (mesh == null)
			mesh = new Mesh(false, true, vertexArray.length, 0, Vertex.VERTEX_ATTRIBUTES);
		
		mesh.setVertices(vertexArray);
		
		
//		System.out.println("Vertex objects: " + (cells.length * Cell3D.VERTICES_PER_CELL) + ", floats: " + vertexArray.length + ", in mesh: " + mesh.getNumVertices());
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
	
	
	public int getColumnCount()
	{
		return width;
	}
	
	
	public int getRowCount()
	{
		return height;
	}
	
	
	public int getLayerCount()
	{
		return depth;
	}
	
	
	public int getCellCount()
	{
		return cells.length;
	}
	
	
	public int getCellSize()
	{
		return cellSize;
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
