package komposten.automata.backend.rendering;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;


public class IndexGridMesh extends AbstractMesh
{
	private Mesh mesh;
	
	private Cell[] cells;
	
	private float[] vertexArray;
	private short[] indexArray;


	public IndexGridMesh(float width, float height, int targetSize)
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
		int valuesPerCell = Cell.VERTICES_PER_CELL * Vertex.VALUES_PER_VERTEX;
		vertexArray = new float[cells.length * valuesPerCell];
		
		for (int i = 0; i < cells.length; i++)
		{
			Cell cell = cells[i];
			int index = i * valuesPerCell;
			
			for (int j = 0; j < Cell.VERTICES_PER_CELL; j++)
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

	
	private void createIndexArray()
	{
		int indicesPerCell = Cell.TRIANGLES_PER_CELL * Triangle.INDICES_PER_TRIANGLE;
		indexArray = new short[cells.length * indicesPerCell];
		
		for (int i = 0; i < cells.length; i++)
		{
			Cell cell = cells[i];
			int index = i * indicesPerCell;
			
			for (int j = 0; j < Cell.TRIANGLES_PER_CELL; j++)
			{
				int k = index + j*Triangle.INDICES_PER_TRIANGLE;
				Triangle triangle = cell.getTriangles()[j];
				
				indexArray[k+0] = (short) (triangle.vertex1+i*Cell.VERTICES_PER_CELL);
				indexArray[k+1] = (short) (triangle.vertex2+i*Cell.VERTICES_PER_CELL);
				indexArray[k+2] = (short) (triangle.vertex3+i*Cell.VERTICES_PER_CELL);
			}
		}
		
//		System.out.println("INDEX ARRAY");
//		for (float f : indexArray)
//		{
//			System.out.print(f + ", ");
//		}
//		System.out.println();
	}


	private void createMesh()
	{
		createVertexArray();
		createIndexArray();
		
		System.out.println("Creating mesh...");
		if (mesh == null)
			mesh = new Mesh(false, true, vertexArray.length, indexArray.length, Vertex.VERTEX_ATTRIBUTES);
		
		mesh.setVertices(vertexArray);
		mesh.setIndices(indexArray);
		
		System.out.println("Mesh created!");
		
//		System.out.println("Vertex objects: " + (cells.length * Cell.VERTICES_PER_CELL) + ", floats: " + vertexArray.length + ", in mesh: " + mesh.getNumVertices());
//		System.out.println("Triangles: " + (cells.length * Cell.TRIANGLES_PER_CELL) + ", indices: " + indexArray.length + ", in mesh: "  + mesh.getNumIndices());
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
		mesh.setVertices(vertexArray);
	}
	
	
	@Override
	public void dispose()
	{
		mesh.dispose();
	}
}
