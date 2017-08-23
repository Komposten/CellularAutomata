package komposten.automata.backend.rendering.g3d;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.FloatArray;

import komposten.automata.backend.rendering.Vertex;
import komposten.automata.backend.rendering.g3d.Cell3D.Face;


public class GridMesh3D2 implements Disposable
{
	private static class CellType
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
	private FloatArray vertexArray = new FloatArray(); //TODO GridMesh3D2; For even less memory usage, fill a float[] directly. Set its size based on vertexCount (which is currently always 0, is there a fast, good-looking way to update it?).

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
		long vertexCount = cellCountLong * Cell3D.VERTICES_PER_CELL;
		long byteCount = vertexCount * Vertex.VERTEX_ATTRIBUTES.vertexSize;
		
		if (cellCountLong > Integer.MAX_VALUE || vertexCount > Integer.MAX_VALUE
				|| byteCount > Integer.MAX_VALUE)
		{
			int maxCellCount = Integer.MAX_VALUE / Vertex.VERTEX_ATTRIBUTES.vertexSize / Cell3D.VERTICES_PER_CELL;
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
		
		int vertexCount = cellCount * Cell3D.VERTICES_PER_CELL;
		int floatCount = vertexCount * Vertex.VALUES_PER_VERTEX;
		System.out.println("===CELLS:\n" + "  " + width + " * " + height + " * "
				+ depth + " => " + cellCount + " cells.\n" + "  " + cellCount + " * "
				+ Cell3D.VERTICES_PER_CELL + " = " + vertexCount + " vertices.\n" + "  "
				+ vertexCount + " * " + Vertex.VALUES_PER_VERTEX + " = " + floatCount
				+ " floats.");
	}


	private short[] createData(short cellType, Color color)
	{
		return new short[] { cellType, 0, (short) (color.r*255), (short) (color.g*255), (short) (color.b*255) };
	}


	private void createMesh()
	{
		float[] vertexArray = createVertexArray();
		
		if (mesh == null)
			mesh = new Mesh(false, true, getCellCount() * Cell3D.VERTICES_PER_CELL, 0, Vertex.VERTEX_ATTRIBUTES);
		mesh.setVertices(vertexArray);
		
//		System.out.println("Vertex objects: " + (cellCount * Cell3D.VERTICES_PER_CELL) + ", floats: " + vertexArray.length + ", in mesh: " + mesh.getNumVertices());
	}
	
	
	private float[] createVertexArray()
	{
		int vertexIndex = 0;
		for (Entry<Integer, short[]> entry : cells.entrySet())
		{
			int index = entry.getKey();
			short[] data = entry.getValue();
			
			int[] coords = getCoordinates(index);
			
			vertexIndex = addVertices(data[FACE_MASK], coords[0], coords[1], coords[2], data[R], data[G], data[B], vertexArray, vertexIndex);
		}
		
		return vertexArray.toArray();
	}
	
	
	private int addVertices(short faces, int cellX, int cellY, int cellZ, short r, short g,
			short b, FloatArray vertexArray, int offset)
	{
		float x = cellX * cellSize;
		float y = cellY * cellSize;
		float z = cellZ * cellSize;
		
		float r2 = r/255f;
		float g2 = g/255f;
		float b2 = b/255f;
		
		float red, green, blue;
		
		if ((faces & Face.Back.bitmask) != 0)
		{
			red = r2 * 0.6f;
			green = g2 * 0.6f;
			blue = b2 * 0.6f;
			offset = addVertex(x, y+cellSize, z, 1, 0, red, green, blue, vertexArray, offset);
			offset = addVertex(x+cellSize, y+cellSize, z, 0, 0, red, green, blue, vertexArray, offset);
			offset = addVertex(x+cellSize, y, z, 0, 1, red, green, blue, vertexArray, offset);
			offset = addVertex(x, y+cellSize, z, 1, 0, red, green, blue, vertexArray, offset);
			offset = addVertex(x+cellSize, y, z, 0, 1, red, green, blue, vertexArray, offset);
			offset = addVertex(x, y, z, 1, 1, red, green, blue, vertexArray, offset);
		}
		
		if ((faces & Face.Front.bitmask) != 0)
		{
			red = r2 * 0.6f;
			green = g2 * 0.6f;
			blue = b2 * 0.6f;
			offset = addVertex(x, y, z+cellSize, 0, 1, red, green, blue, vertexArray, offset);
			offset = addVertex(x+cellSize, y, z+cellSize, 1, 1, red, green, blue, vertexArray, offset);
			offset = addVertex(x, y+cellSize, z+cellSize, 0, 0, red, green, blue, vertexArray, offset);
			offset = addVertex(x+cellSize, y+cellSize, z+cellSize, 1, 0, red, green, blue, vertexArray, offset);
			offset = addVertex(x, y+cellSize, z+cellSize, 0, 0, red, green, blue, vertexArray, offset);
			offset = addVertex(x+cellSize, y, z+cellSize, 1, 1, red, green, blue, vertexArray, offset);
		}
		
		if ((faces & Face.Left.bitmask) != 0)
		{
			red = r2 * 0.8f;
			green = g2 * 0.8f;
			blue = b2 * 0.8f;
			offset = addVertex(x, y+cellSize, z+cellSize, 1, 0, red, green, blue, vertexArray, offset);
			offset = addVertex(x, y, z, 0, 1, red, green, blue, vertexArray, offset);
			offset = addVertex(x, y, z+cellSize, 1, 1, red, green, blue, vertexArray, offset);
			offset = addVertex(x, y+cellSize, z+cellSize, 1, 0, red, green, blue, vertexArray, offset);
			offset = addVertex(x, y+cellSize, z, 0, 0, red, green, blue, vertexArray, offset);
			offset = addVertex(x, y, z, 0, 1, red, green, blue, vertexArray, offset);
		}
		
		if ((faces & Face.Right.bitmask) != 0)
		{
			red = r2 * 0.4f;
			green = g2 * 0.4f;
			blue = b2 * 0.4f;
			offset = addVertex(x+cellSize, y+cellSize, z, 1, 0, red, green, blue, vertexArray, offset);
			offset = addVertex(x+cellSize, y+cellSize, z+cellSize, 0, 0, red, green, blue, vertexArray, offset);
			offset = addVertex(x+cellSize, y, z, 1, 1, red, green, blue, vertexArray, offset);
			offset = addVertex(x+cellSize, y, z+cellSize, 0, 1, red, green, blue, vertexArray, offset);
			offset = addVertex(x+cellSize, y, z, 1, 1, red, green, blue, vertexArray, offset);
			offset = addVertex(x+cellSize, y+cellSize, z+cellSize, 0, 0, red, green, blue, vertexArray, offset);
		}
		
		if ((faces & Face.Bottom.bitmask) != 0)
		{
			red = r2 * 0.2f;
			green = g2 * 0.2f;
			blue = b2 * 0.2f;
			offset = addVertex(x, y, z, 0, 1, red, green, blue, vertexArray, offset);
			offset = addVertex(x+cellSize, y, z, 1, 1, red, green, blue, vertexArray, offset);
			offset = addVertex(x, y, z+cellSize, 0, 0, red, green, blue, vertexArray, offset);
			offset = addVertex(x+cellSize, y, z+cellSize, 1, 0, red, green, blue, vertexArray, offset);
			offset = addVertex(x, y, z+cellSize, 0, 0, red, green, blue, vertexArray, offset);
			offset = addVertex(x+cellSize, y, z, 1, 1, red, green, blue, vertexArray, offset);
		}
		
		if ((faces & Face.Top.bitmask) != 0)
		{
			red = r2;
			green = g2;
			blue = b2;
			offset = addVertex(x, y+cellSize, z, 0, 0, red, green, blue, vertexArray, offset);
			offset = addVertex(x, y+cellSize, z+cellSize, 0, 1, red, green, blue, vertexArray, offset);
			offset = addVertex(x+cellSize, y+cellSize, z, 1, 0, red, green, blue, vertexArray, offset);
			offset = addVertex(x+cellSize, y+cellSize, z+cellSize, 1, 1, red, green, blue, vertexArray, offset);
			offset = addVertex(x+cellSize, y+cellSize, z, 1, 0, red, green, blue, vertexArray, offset);
			offset = addVertex(x, y+cellSize, z+cellSize, 0, 1, red, green, blue, vertexArray, offset);
		}
		
		
		return offset;
	}


	private int addVertex(float x, float y, float z, float u, float v, float r, float g, float b, FloatArray vertexArray, int offset)
	{
//		vertexArray[offset++] = x;
//		vertexArray[offset++] = y;
//		vertexArray[offset++] = z;
//		vertexArray[offset++] = u;
//		vertexArray[offset++] = v;
//		vertexArray[offset++] = r;
//		vertexArray[offset++] = g;
//		vertexArray[offset++] = b;
//		vertexArray[offset++] = 1;
		vertexArray.add(x);
		vertexArray.add(y);
		vertexArray.add(z);
		vertexArray.add(u);
		vertexArray.add(v);
		vertexArray.add(r);
		vertexArray.add(g);
		vertexArray.add(b);
		vertexArray.add(1);
		return offset;
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
		cells.remove(index);
		updateCell(index, x, y, z); //TODO In updateCell() (or a new method), tell existing neighbours that we have deleted [x, y, z].
	}
	
	
	public void updateCell(int index, int x, int y, int z)
	{
		short[] cell = cells.get(index);
		short[] adjacent = null;
		
		//Right neighbour
		if (x+1 < width)
		{
			adjacent = cells.get(getIndex(x+1, y, z));
			if (adjacent != null)
			{
				adjacent[FACE_MASK] &= ~Face.Left.bitmask;
				cell[FACE_MASK] &= ~Face.Right.bitmask;
			}
			else
			{
				cell[FACE_MASK] |= Face.Right.bitmask;
			}
		}
		else
		{
			cell[FACE_MASK] |= Face.Right.bitmask;
		}
		
		//Left neighbour
		if (x-1 >= 0)
		{
			adjacent = cells.get(getIndex(x-1, y, z));
			if (adjacent != null)
			{
				adjacent[FACE_MASK] &= ~Face.Right.bitmask;
				cell[FACE_MASK] &= ~Face.Left.bitmask;
			}
			else
			{
				cell[FACE_MASK] |= Face.Left.bitmask;
			}
		}
		else
		{
			cell[FACE_MASK] |= Face.Left.bitmask;
		}
		
		//Front neighbour
		if (z+1 < depth)
		{
			adjacent = cells.get(getIndex(x, y, z+1));
			if (adjacent != null)
			{
				adjacent[FACE_MASK] &= ~Face.Back.bitmask;
				cell[FACE_MASK] &= ~Face.Front.bitmask;
			}
			else
			{
				cell[FACE_MASK] |= Face.Front.bitmask;
			}
		}
		else
		{
			cell[FACE_MASK] |= Face.Front.bitmask;
		}
		
		//Back neighbour
		if (z-1 >= 0)
		{
			adjacent = cells.get(getIndex(x, y, z-1));
			if (adjacent != null)
			{
				adjacent[FACE_MASK] &= ~Face.Front.bitmask;
				cell[FACE_MASK] &= ~Face.Back.bitmask;
			}
			else
			{
				cell[FACE_MASK] |= Face.Back.bitmask;
			}
		}
		else
		{
			cell[FACE_MASK] |= Face.Back.bitmask;
		}
		
		//Top neighbour
		if (y+1 < height)
		{
			adjacent = cells.get(getIndex(x, y+1, z));
			if (adjacent != null)
			{
				adjacent[FACE_MASK] &= ~Face.Bottom.bitmask;
				cell[FACE_MASK] &= ~Face.Top.bitmask;
			}
			else
			{
				cell[FACE_MASK] |= Face.Top.bitmask;
			}
		}
		else
		{
			cell[FACE_MASK] |= Face.Top.bitmask;
		}
		
		//Bottom neighbour
		if (y-1 >= 0)
		{
			adjacent = cells.get(getIndex(x, y-1, z));
			if (adjacent != null)
			{
				adjacent[FACE_MASK] &= ~Face.Top.bitmask;
				cell[FACE_MASK] &= ~Face.Bottom.bitmask;
			}
			else
			{
				cell[FACE_MASK] |= Face.Bottom.bitmask;
			}
		}
		else
		{
			cell[FACE_MASK] |= Face.Bottom.bitmask;
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
