package komposten.automata.backend.rendering;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;


public class GridMesh extends AbstractMesh
{
	private Mesh mesh;

	private Cell[] cells;
	
	private float[] vertexArray;


	public GridMesh(float width, float height, int targetSize)
	{
		super(width, height, targetSize);
		createCells();
		createMesh();
	}


	private void createCells()
	{
		cells = new Cell[columns * rows];

//		System.out.println(columnVertices + ", " + rowVertices + ", " + cellWidth + ", " + cellHeight);

		//Create cells.
		for (int r = 0; r < rows; r++)
		{
			for (int c = 0; c < columns; c++)
			{
				int index = r*columns + c;
				
				float x = c * cellWidth;
				float y = r * cellHeight;
				
				Cell cell = new Cell(x, y, cellWidth, cellHeight, Color.BLACK);
				
				cells[index] = cell;
			}
		}
		

		System.out.println("===CELLS: [" + columns + ", " + rows + "]=" + cells.length);
//		System.out.println("===lower left: " + vertices[0].toString(true) + ", upper right: " + vertices[vertices.length-1].toString(true));
//		printArray(vertices, columnVertices);
	}
	
	
	private void createVertexArray()
	{
		int valuesPerCell = 6 * Vertex.VALUES_PER_VERTEX;
		vertexArray = new float[cells.length * valuesPerCell];
		
		int[] indexArray = new int[] { 0, 1, 3, 2, 1, 3 };
		
		for (int i = 0; i < cells.length; i++)
		{
			Cell cell = cells[i];
			int index = i * valuesPerCell;
			
			for (int j = 0; j < 6; j++)
			{
				int k = index + j*Vertex.VALUES_PER_VERTEX;
				Vertex vertex = cell.getVertices()[indexArray[j]];
				
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


	private void createMesh()
	{
		createVertexArray();
		
//		System.out.println("Creating mesh...");
		if (mesh == null)
			mesh = new Mesh(false, true, vertexArray.length, 0, Vertex.VERTEX_ATTRIBUTES);
		
		mesh.setVertices(vertexArray);
		
//		System.out.println("Mesh created!");
		
//		System.out.println("Vertex objects: " + (cells.length * Cell.VERTICES_PER_CELL) + ", floats: " + vertexArray.length + ", in mesh: " + mesh.getNumVertices());
	}
	
	
	public Mesh getMesh()
	{
		return mesh;
	}
	
	
	@Override
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
