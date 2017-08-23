package komposten.automata.backend.rendering.g3d;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.FloatArray;

import komposten.automata.backend.rendering.g3d.VertexFactory.Face;


public class GridMesh3D2 implements Disposable
{
	public static class CellType
	{
		public static final short Normal = 0;
	}
	
	private static final int TYPE = 0;
	private static final int FACE_MASK = 1;
	private static final int R = 2;
	private static final int G = 3;
	private static final int B = 4;
	
	private Mesh mesh;
	private Map<Integer, short[]> cells;
	private FloatArray vertexArray = new FloatArray(); //TODO GridMesh3D2; For even less memory and cpu usage, fill a float[] directly. Set its size based on vertexCount (which is currently always 0, is there a fast, good-looking way to update it?).

	private int cellCount;
	private int vertexCount;
	private int width; //X
	private int height; //Y
	private int depth; //Z
	private int cellSize;
	
	
	/**
	 * @param width Width of the grid, measured in cells.
	 * @param height Height of the grid, measured in cells.
	 * @param depth Depth of the grid, measured in cells.
	 * @param cellSize The size of a cell.
	 */
	public GridMesh3D2(int width, int height, int depth, int cellSize)
	{
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.cellSize = cellSize;
		this.cellCount = width * height * depth;
		
		long cellCountLong = (long)width * (long)height * depth;
		long vertexCount = cellCountLong * VertexFactory.VERTICES_PER_CELL;
		long byteCount = vertexCount * VertexFactory.VERTEX_ATTRIBUTES.vertexSize;
		
		if (cellCountLong > Integer.MAX_VALUE || vertexCount > Integer.MAX_VALUE
				|| byteCount > Integer.MAX_VALUE)
		{
			int maxCellCount = Integer.MAX_VALUE / VertexFactory.VERTEX_ATTRIBUTES.vertexSize / VertexFactory.VERTICES_PER_CELL;
			throw new IllegalArgumentException("Cannot create a grid of size [" + width
					+ "x" + height + "x" + depth + "], it has too many cells ("
					+ cellCountLong + ">" + maxCellCount + ")!");
		}
		
		createCells();
		createMesh();
	}


	private void createCells()
	{
		cells = new HashMap<>();

		RandomXS128 random = new RandomXS128();
		
		for (int x = 0; x < width; x++)
		{
			for (int y = 0; y < height; y++)
			{
				for (int z = 0; z < depth; z++)
				{
					Color colour = new Color(random.nextFloat(), random.nextFloat(), random.nextFloat(), 1);
					addCell(CellType.Normal, colour, x, y, z);
				}
			}
		}
		
		int vertexCount = cellCount * VertexFactory.VERTICES_PER_CELL;
		int floatCount = vertexCount * VertexFactory.VALUES_PER_VERTEX;
		System.out.println("===CELLS:\n" + "  " + width + " * " + height + " * "
				+ depth + " => " + cellCount + " cells.\n" + "  " + cellCount + " * "
				+ VertexFactory.VERTICES_PER_CELL + " = " + vertexCount + " vertices.\n" + "  "
				+ vertexCount + " * " + VertexFactory.VALUES_PER_VERTEX + " = " + floatCount
				+ " floats.");
	}


	private void createMesh()
	{
		float[] vertexArray = createVertexArray();
		
		if (mesh == null)
			mesh = new Mesh(false, true, getCellCount() * VertexFactory.VERTICES_PER_CELL, 0, VertexFactory.VERTEX_ATTRIBUTES);
		mesh.setVertices(vertexArray);
		
//		System.out.println("Vertex objects: " + (cellCount * VertexFactory.VERTICES_PER_CELL) + ", floats: " + vertexArray.length + ", in mesh: " + mesh.getNumVertices());
	}
	
	
	private float[] createVertexArray()
	{
		vertexArray.clear();
		int vertexIndex = 0;
		for (Entry<Integer, short[]> entry : cells.entrySet())
		{
			int index = entry.getKey();
			short[] data = entry.getValue();
			int[] coords = getCoordinates(index);
			
			vertexIndex = VertexFactory.addFaces(data[FACE_MASK], coords[0], coords[1], coords[2], cellSize, data[R], data[G], data[B], vertexArray, vertexIndex);
		}
		
		return vertexArray.toArray();
	}
	
	
	public Mesh getMesh()
	{
		return mesh;
	}
	

	/**
	 * @return The width of the grid, measured in cells.
	 */
	public int getWidth()
	{
		return width;
	}
	

	/**
	 * @return The height of the grid, measured in cells.
	 */
	public int getHeight()
	{
		return height;
	}
	

	/**
	 * @return The depth of the grid, measured in cells.
	 */
	public int getDepth()
	{
		return depth;
	}
	
	
	public int getCellCount()
	{
		return cellCount;
	}
	

	/**
	 * @return The size of a cell.
	 */
	public int getCellSize()
	{
		return cellSize;
	}
	
	
	public int getIndex(int x, int y, int z)
	{
		return width * (y * depth + z) + x;
	}
	
	
	private int[] temp = new int[3];
	public int[] getCoordinates(int index)
	{
		temp[1] = index / (width * depth);
		index -= (temp[1] * width * depth);
		temp[2] = index / width;
		temp[0] = index % width;
		
		return temp;
	}
	
	
	public void addCell(short cellType, Color color, int x, int y, int z)
	{
		int index = getIndex(x, y, z);
		short[] data = createData(cellType, color);
		cells.put(index, data);
		updateCell(index, x, y, z);
	}


	private short[] createData(short cellType, Color color)
	{
		return new short[] { cellType, 0, (short) (color.r*255), (short) (color.g*255), (short) (color.b*255) };
	}
	
	
	public void updateCell(short cellType, Color color, int x, int y, int z)
	{
		int index = getIndex(x, y, z);
		
		if (cells.containsKey(index))
		{
			short[] data = cells.get(index);

			data[TYPE] = cellType;
			data[R] = (short) (color.r * 255);
			data[G] = (short) (color.g * 255);
			data[B] = (short) (color.b * 255);
			updateCell(index, x, y, z);
		}
	}
	
	
	public void removeCell(int x, int y, int z)
	{
		int index = getIndex(x, y, z);
		
		if (cells.remove(index) == null)
		{
			return;
		}
		
		updateCell(index, x, y, z);
	}
	
	
	private void updateCell(int index, int x, int y, int z)
	{
		short[] cell = cells.get(index);
		short[] adjacent = null;
		
		//Right neighbour
		adjacent = (x+1 < width) ? cells.get(getIndex(x+1, y, z)) : null;
		updateFaces(cell, Face.Right, adjacent, Face.Left);
		
		//Left neighbour
		adjacent = (x-1 >= 0) ? cells.get(getIndex(x-1, y, z)) : null;
		updateFaces(cell, Face.Left, adjacent, Face.Right);
		
		//Front neighbour
		adjacent = (z+1 < depth) ? cells.get(getIndex(x, y, z+1)) : null;
		updateFaces(cell, Face.Front, adjacent, Face.Back);
		
		//Back neighbour
		adjacent = (z-1 >= 0) ? cells.get(getIndex(x, y, z-1)) : null;
		updateFaces(cell, Face.Back, adjacent, Face.Front);
		
		//Top neighbour
		adjacent = (y+1 < height) ? cells.get(getIndex(x, y+1, z)) : null;
		updateFaces(cell, Face.Top, adjacent, Face.Bottom);
		
		//Bottom neighbour
		adjacent = (y-1 >= 0) ? cells.get(getIndex(x, y-1, z)) : null;
		updateFaces(cell, Face.Bottom, adjacent, Face.Top);
	}
	
	
	private void updateFaces(short[] cell, Face cellFace, short[] adjacent, Face adjacentFace)
	{
		if (adjacent != null)
		{
			if (cell != null)
			{
				adjacent[FACE_MASK] &= ~adjacentFace.bitmask;
				cell[FACE_MASK] &= ~cellFace.bitmask;
			}
			else
			{
				adjacent[FACE_MASK] |= adjacentFace.bitmask;
			}
		}
		else if (cell != null)
		{
			cell[FACE_MASK] |= cellFace.bitmask;
		}
	}
	
	
	public void refreshMesh()
	{
		mesh.setVertices(createVertexArray());
	}
	
	
	@Override
	public void dispose()
	{
		mesh.dispose();
	}
}
