package komposten.automata.backend.rendering.g3d;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.utils.Disposable;

import komposten.automata.backend.rendering.g3d.VertexFactory.Face;


//NEXT_TASK IDEAS:
//  Add an offset so the mesh can be placed at a different location.
//  Add an "addAdjacentMesh()"-method that can be used to set neighbours, so updateCellFaces() can know if an outer face (e.g. Face.Left at x == 0) is hidden by another mesh or not.


public class GridMesh3D2 implements Disposable
{
	public static class CellType
	{
		public static final short Normal = 0;
	}
	

	public final class Coordinate
	{
		public int x;
		public int y;
		public int z;


		public Coordinate()
		{
			this(0, 0, 0);
		}


		public Coordinate(int x, int y, int z)
		{
			set(x, y, z);
		}


		public Coordinate set(int x, int y, int z)
		{
			this.x = x;
			this.y = y;
			this.z = z;
			return this;
		}
		
		
		@Override
		public int hashCode()
		{
			return width * (y * depth + z) + x;
		}
		
		
		@Override
		public boolean equals(Object obj)
		{
			if (obj instanceof Coordinate)
			{
				Coordinate other = (Coordinate)obj;
				return x == other.x && y == other.y && z == other.z;
			}
			
			return false;
		}
	}
	
	private Mesh mesh;
	private Map<Coordinate, CellData> cells;
	private float[] vertexArray;
	private Coordinate vector = new Coordinate();

	private int cellCount;
	private int faceCount;
	private int width; //X
	private int height; //Y
	private int depth; //Z
	private int cellSize;
	
	/** True if the vertex array should be re-created. */
	private boolean isDirty;
	
	
	/**
	 * @param width Width of the grid, measured in cells.
	 * @param height Height of the grid, measured in cells.
	 * @param depth Depth of the grid, measured in cells.
	 * @param cellSize The size of a cell.
	 * @param fill If the grid should be empty (no vertices) or filled with cells.
	 */
	public GridMesh3D2(int width, int height, int depth, int cellSize, boolean fill)
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
		
		createCells(fill);
		createMesh();
	}


	private void createCells(boolean fill)
	{
		cells = new HashMap<>();

		if (fill)
		{
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
		int index = 0;
		vertexArray = new float[faceCount * VertexFactory.VERTICES_PER_FACE * VertexFactory.VALUES_PER_VERTEX];

		for (Entry<Coordinate, CellData> entry : cells.entrySet())
		{
			Coordinate coords = entry.getKey();
			CellData data = entry.getValue();
			
			if (data.vertexData == null)
			{
				data.updateVertices(coords.x, coords.y, coords.z, cellSize);
			}
			
			data.index = index;
			
			for (float f : data.vertexData)
			{
				vertexArray[index] = f;
				index++;
			}
		}
		
		isDirty = false;
		
		return vertexArray;
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
	

	/**
	 * Converts an index value to an x, y and z coordinate triplet. <br />
	 * The {@link Coordinate} object that is returned is reused by this
	 * {@link GridMesh3D2} instance for other purposes. Do <i>not</i> use it to
	 * store values, since it <i>will</i> be changed without warning.
	 */
	public Coordinate getCoordinates(int index)
	{
		vector.y = index / (width * depth);
		index -= (vector.y * width * depth);
		vector.z = index / width;
		vector.x = index % width;
		
		return vector;
	}
	
	
	/**
	 * Adds a cell to the mesh at the specified coordinates, unless such a cell
	 * already exists.
	 * 
	 * @return <code>false</code> if a cell already existed at the specified
	 *         coordinates, <code>true</code> if a cell was added.
	 */
	public boolean addCell(short cellType, Color color, int x, int y, int z)
	{
		if (cells.containsKey(vector.set(x, y, z)))
		{
			return false;
		}
		
		Coordinate coords = new Coordinate(x, y, z);
		CellData data = createData(cellType, color);
		cells.put(coords, data);
		updateCellFaces(x, y, z);
		isDirty = true;
		return true;
	}


	private CellData createData(short cellType, Color color)
	{
		//NEXT_TASK GridMesh3D2; Consider switching to float to avoid color conversion calculations (faster, but more RAM).
		CellData data = new CellData();
		data.type = cellType;
		data.faceMask = 0;
		data.index = 0;
		data.r = (short) (color.r * 255);
		data.g = (short) (color.g * 255);
		data.b = (short) (color.b * 255);
		
		return data;
	}
	
	
	public boolean hasCell(int x, int y, int z)
	{
		return cells.containsKey(vector.set(x, y, z));
	}
	
	
	public void updateCell(short cellType, Color color, int index)
	{
		Coordinate coords = getCoordinates(index);
		updateCell(cellType, color, coords.x, coords.y, coords.z);
	}
	
	
	public void updateCell(short cellType, Color color, int x, int y, int z)
	{
		if (cells.containsKey(vector.set(x, y, z)))
		{
			CellData data = cells.get(vector);

			data.type = cellType;
			data.setColor(color);
			
			if (!isDirty)
			{
				VertexFactory.updateColors(data.faceMask, data.r, data.g, data.b, vertexArray, data.index);
			}
		}
	}
	
	
	public void removeCell(int index)
	{
		Coordinate coords = getCoordinates(index);
		
		removeCell(coords.x, coords.y, coords.z);
	}


	public void removeCell(int x, int y, int z)
	{
		if (cells.remove(vector.set(x, y, z)) == null)
		{
			return;
		}
		
		updateCellFaces(x, y, z);
		isDirty = true;
	}
	
	
	private void updateCellFaces(int x, int y, int z)
	{
		CellData cell = cells.get(vector.set(x, y, z));
		CellData adjacent = null;
		
		//Right neighbour
		adjacent = (x+1 < width) ? cells.get(vector.set(x+1, y, z)) : null;
		updateFaces(cell, Face.Right, adjacent, Face.Left);
		
		//Left neighbour
		adjacent = (x-1 >= 0) ? cells.get(vector.set(x-1, y, z)) : null;
		updateFaces(cell, Face.Left, adjacent, Face.Right);
		
		//Front neighbour
		adjacent = (z+1 < depth) ? cells.get(vector.set(x, y, z+1)) : null;
		updateFaces(cell, Face.Front, adjacent, Face.Back);
		
		//Back neighbour
		adjacent = (z-1 >= 0) ? cells.get(vector.set(x, y, z-1)) : null;
		updateFaces(cell, Face.Back, adjacent, Face.Front);
		
		//Top neighbour
		adjacent = (y+1 < height) ? cells.get(vector.set(x, y+1, z)) : null;
		updateFaces(cell, Face.Top, adjacent, Face.Bottom);
		
		//Bottom neighbour
		adjacent = (y-1 >= 0) ? cells.get(vector.set(x, y-1, z)) : null;
		updateFaces(cell, Face.Bottom, adjacent, Face.Top);
	}
	
	
	private void updateFaces(CellData cell, Face cellFace, CellData adjacent, Face adjacentFace)
	{
		if (adjacent != null)
		{
			if (cell != null)
			{
				adjacent.faceMask &= ~adjacentFace.bitmask;
				cell.faceMask &= ~cellFace.bitmask;
				
				adjacent.vertexData = null;
				cell.vertexData = null;
				
				faceCount -= 1; //Only remove adjcent's face, since the newly added cell (cell) is not yet included in the count.
			}
			else
			{
				adjacent.faceMask |= adjacentFace.bitmask;
				adjacent.vertexData = null;
				faceCount += 1; //We add a face to adjacent.
			}
		}
		else if (cell != null)
		{
			cell.faceMask |= cellFace.bitmask;
			cell.vertexData = null;
			faceCount += 1;
		}
		else
		{
			faceCount -= 1; //We have removed cell, and adjacent doesn't exist -> remove the face that was towards adjacent.
		}
	}
	
	
	public void refreshMesh()
	{
		if (isDirty)
		{
			createVertexArray();
		}
		mesh.setVertices(vertexArray);
	}
	
	
	@Override
	public void dispose()
	{
		mesh.dispose();
	}
}
