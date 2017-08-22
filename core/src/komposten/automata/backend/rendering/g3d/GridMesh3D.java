package komposten.automata.backend.rendering.g3d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.FloatArray;

import komposten.automata.backend.rendering.Vertex;
import komposten.automata.backend.rendering.g3d.Cell3D.Face;


public class GridMesh3D implements Disposable
{
	private Mesh mesh;
	private Cell3D[] cells;
	private float[] vertexArray;
	private FloatArray floatArray;
	
	private int cellCount;
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
		this.cellCount = width * height * depth;
		
		long cellCountLong = (long)width * (long)height * depth;
		long vertexCount = cellCountLong * Cell3D.VERTICES_PER_CELL;
		long byteCount = vertexCount * Vertex.VERTEX_ATTRIBUTES.vertexSize;
		
		if (cellCountLong > Integer.MAX_VALUE || vertexCount > Integer.MAX_VALUE
				|| byteCount > Integer.MAX_VALUE)
		{
			int maxCellCount = Integer.MAX_VALUE / Vertex.VERTEX_ATTRIBUTES.vertexSize / Cell3D.VERTICES_PER_CELL;
			throw new IllegalArgumentException("Cannot create a grid of size [" + rows
					+ "x" + columns + "x" + layers + "], it has too many cells ("
					+ cellCountLong + ">" + maxCellCount + ")!");
		}
		
		createCells();
		createMesh();
	}


	private void createCells()
	{
		cells = new Cell3D[cellCount];

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
					
					updateCell(index, x, y, z);
				}
			}
		}
		
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
			mesh = new Mesh(false, true, getCellCount() * Cell3D.VERTICES_PER_CELL, 0, Vertex.VERTEX_ATTRIBUTES);
		
		vertexArray = floatArray.toArray();
		mesh.setVertices(vertexArray);
		
		
		System.out.println("Vertex objects: " + (cells.length * Cell3D.VERTICES_PER_CELL) + ", floats: " + vertexArray.length + ", in mesh: " + mesh.getNumVertices());
	}
	
	
	private void createVertexArray()
	{
		int valuesPerCell = Cell3D.VERTICES_PER_CELL * Vertex.VALUES_PER_VERTEX;
		int maximumValues = cells.length * valuesPerCell;
		
		floatArray = new FloatArray(maximumValues);
		
		for (int i = 0; i < cells.length; i++)
		{
			Cell3D cell = cells[i];
			
			if (!cell.isVisible())
				continue;
			
			int index = i * valuesPerCell;
			
			for (int j = 0; j < Cell3D.VERTICES_PER_CELL; j++)
			{
				if (!cell.isVertexVisible(j))
					continue;
				
				int k = index + j*Vertex.VALUES_PER_VERTEX;
				Vertex vertex = cell.getVertices()[j];
				
				floatArray.add(vertex.x);
				floatArray.add(vertex.y);
				floatArray.add(vertex.z);
				floatArray.add(vertex.u);
				floatArray.add(vertex.v);
				floatArray.add(vertex.r);
				floatArray.add(vertex.g);
				floatArray.add(vertex.b);
				floatArray.add(vertex.a);
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
		return cellCount;
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
	
	
	public void updateCell(Color color, boolean visible, int index)
	{
		cells[index].setColor(color);
		cells[index].setVisible(visible);
		updateCell(index);
	}
	
	
	public void updateCell(Color color, boolean visible, int x, int y, int z)
	{
		int index = getIndex(x, y, z);
		cells[index].setColor(color);
		cells[index].setVisible(visible);
		
		updateCell(index, x, y, z);
	}
	
	
	public void updateCell(int index)
	{
		//TODO call updateCell(int, int, int)
	}
	
	
	public void updateCell(int index, int x, int y, int z)
	{
		Cell3D cell = cells[index];
		Cell3D adjacent = null;
		
		//Right neighbour
		if (x+1 < width)
		{
			adjacent = cells[getIndex(x+1, y, z)];
			if (adjacent != null)
			{
				adjacent.setFaceVisible(Face.Left, !cell.isVisible());
				cell.setFaceVisible(Face.Right, !cell.isVisible());
			}
		}
		
		//Left neighbour
		if (x-1 >= 0)
		{
			adjacent = cells[getIndex(x-1, y, z)];
			if (adjacent != null)
			{
				adjacent.setFaceVisible(Face.Right, !cell.isVisible());
				cell.setFaceVisible(Face.Left, !cell.isVisible());
			}
		}
		
		//Front neighbour
		if (z+1 < depth)
		{
			adjacent = cells[getIndex(x, y, z+1)];
			if (adjacent != null)
			{
				adjacent.setFaceVisible(Face.Back, !cell.isVisible());
				cell.setFaceVisible(Face.Front, !cell.isVisible());
			}
		}
		
		//Back neighbour
		if (z-1 >= 0)
		{
			adjacent = cells[getIndex(x, y, z-1)];
			if (adjacent != null)
			{
				adjacent.setFaceVisible(Face.Front, !cell.isVisible());
				cell.setFaceVisible(Face.Back, !cell.isVisible());
			}
		}
		
		//Top neighbour
		if (y+1 < height)
		{
			adjacent = cells[getIndex(x, y+1, z)];
			if (adjacent != null)
			{
				adjacent.setFaceVisible(Face.Bottom, !cell.isVisible());
				cell.setFaceVisible(Face.Top, !cell.isVisible());
			}
		}
		
		//Bottom neighbour
		if (y-1 >= 0)
		{
			adjacent = cells[getIndex(x, y-1, z)];
			if (adjacent != null)
			{
				adjacent.setFaceVisible(Face.Top, !cell.isVisible());
				cell.setFaceVisible(Face.Bottom, !cell.isVisible());
			}
		}
	}
	
	
	public void refreshMesh()
	{
		createVertexArray();
//		updateFloatArray();
		vertexArray = floatArray.toArray();
		mesh.updateVertices(0, vertexArray);
	}
	
	
	@Override
	public void dispose()
	{
		mesh.dispose();
	}
}
