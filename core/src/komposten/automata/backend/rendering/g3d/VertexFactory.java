package komposten.automata.backend.rendering.g3d;

import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.FloatArray;

public class VertexFactory
{
	public static final VertexAttributes VERTEX_ATTRIBUTES = new VertexAttributes(
			new VertexAttribute(Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
			new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "0"),
			new VertexAttribute(Usage.ColorUnpacked, 4, ShaderProgram.COLOR_ATTRIBUTE),
			new VertexAttribute(Usage.Normal, 3, ShaderProgram.NORMAL_ATTRIBUTE));

	public static final int VALUES_PER_VERTEX = 12;
	public static final int VERTICES_PER_FACE = 6;
	public static final int FACES_PER_CELL = 6;
	public static final int VERTICES_PER_CELL = VERTICES_PER_FACE * FACES_PER_CELL;

	
	public enum Face
	{
		Front(0, 2),
		Back(1, 1),
		Left(2, 4),
		Right(3, 8),
		Top(4, 32),
		Bottom(5, 16);
		
		public int index;
		public int bitmask;
		
		private Face(int index, int bitmask)
		{
			this.index = index;
			this.bitmask = bitmask;
		}
	}
	
	//TODO VertexFactory; At some point, remove colours from vertices (will save almost 50% memory).
	
	
	/**
	 * Creates vertices for the faces specified by <code>faces</code> to the provided array.
	 * @param faces A bitmask for the faces to add.
	 * @param cellX The x-coordinate of the cell to add faces for.
	 * @param cellY The y-coordinate of the cell to add faces for.
	 * @param cellZ The z-coordinate of the cell to add faces for.
	 * @param cellSize The size of the cell to add faces for.
	 * @param r The red colour of the cell.
	 * @param g The green colour of the cell.
	 * @param b The blue colour of the cell.
	 * @param vertexArray The float array to store the vertices in.
	 * @param offset The position in the float array to start putting the vertices in.
	 * @return The new offset value (<code>offset + visible_faces*vertices_per_face*values_per_vertex</code>).
	 */
	public static int addFaces(short faces, int cellX, int cellY, int cellZ, float cellSize, short r, short g,
			short b, FloatArray vertexArray, int offset)
	{
		float x = cellX * cellSize;
		float y = cellY * cellSize;
		float z = cellZ * cellSize;
		
		float red = r/255f;
		float green = g/255f;
		float blue = b/255f;
		
		float nx = 0;
		float ny = 0;
		float nz = 0;
		
		if ((faces & Face.Front.bitmask) != 0)
		{
			nx = ny = nz = 0;
			nz = 1;
			offset = addVertex(x, y, z+cellSize, 0, 1, red, green, blue, vertexArray, offset, nx, ny, nz);
			offset = addVertex(x+cellSize, y, z+cellSize, 1, 1, red, green, blue, vertexArray, offset, nx, ny, nz);
			offset = addVertex(x, y+cellSize, z+cellSize, 0, 0, red, green, blue, vertexArray, offset, nx, ny, nz);
			offset = addVertex(x+cellSize, y+cellSize, z+cellSize, 1, 0, red, green, blue, vertexArray, offset, nx, ny, nz);
			offset = addVertex(x, y+cellSize, z+cellSize, 0, 0, red, green, blue, vertexArray, offset, nx, ny, nz);
			offset = addVertex(x+cellSize, y, z+cellSize, 1, 1, red, green, blue, vertexArray, offset, nx, ny, nz);
		}
		
		if ((faces & Face.Back.bitmask) != 0)
		{
			nx = ny = nz = 0;
			nz = -1;
			offset = addVertex(x, y+cellSize, z, 1, 0, red, green, blue, vertexArray, offset, nx, ny, nz);
			offset = addVertex(x+cellSize, y+cellSize, z, 0, 0, red, green, blue, vertexArray, offset, nx, ny, nz);
			offset = addVertex(x+cellSize, y, z, 0, 1, red, green, blue, vertexArray, offset, nx, ny, nz);
			offset = addVertex(x, y+cellSize, z, 1, 0, red, green, blue, vertexArray, offset, nx, ny, nz);
			offset = addVertex(x+cellSize, y, z, 0, 1, red, green, blue, vertexArray, offset, nx, ny, nz);
			offset = addVertex(x, y, z, 1, 1, red, green, blue, vertexArray, offset, nx, ny, nz);
		}
		
		if ((faces & Face.Left.bitmask) != 0)
		{
			nx = ny = nz = 0;
			nx = -1;
			offset = addVertex(x, y+cellSize, z+cellSize, 1, 0, red, green, blue, vertexArray, offset, nx, ny, nz);
			offset = addVertex(x, y, z, 0, 1, red, green, blue, vertexArray, offset, nx, ny, nz);
			offset = addVertex(x, y, z+cellSize, 1, 1, red, green, blue, vertexArray, offset, nx, ny, nz);
			offset = addVertex(x, y+cellSize, z+cellSize, 1, 0, red, green, blue, vertexArray, offset, nx, ny, nz);
			offset = addVertex(x, y+cellSize, z, 0, 0, red, green, blue, vertexArray, offset, nx, ny, nz);
			offset = addVertex(x, y, z, 0, 1, red, green, blue, vertexArray, offset, nx, ny, nz);
		}
		
		if ((faces & Face.Right.bitmask) != 0)
		{
			nx = ny = nz = 0;
			nx = 1;
			offset = addVertex(x+cellSize, y+cellSize, z, 1, 0, red, green, blue, vertexArray, offset, nx, ny, nz);
			offset = addVertex(x+cellSize, y+cellSize, z+cellSize, 0, 0, red, green, blue, vertexArray, offset, nx, ny, nz);
			offset = addVertex(x+cellSize, y, z, 1, 1, red, green, blue, vertexArray, offset, nx, ny, nz);
			offset = addVertex(x+cellSize, y, z+cellSize, 0, 1, red, green, blue, vertexArray, offset, nx, ny, nz);
			offset = addVertex(x+cellSize, y, z, 1, 1, red, green, blue, vertexArray, offset, nx, ny, nz);
			offset = addVertex(x+cellSize, y+cellSize, z+cellSize, 0, 0, red, green, blue, vertexArray, offset, nx, ny, nz);
		}
		
		if ((faces & Face.Top.bitmask) != 0)
		{
			nx = ny = nz = 0;
			ny = 1;
			offset = addVertex(x, y+cellSize, z, 0, 0, red, green, blue, vertexArray, offset, nx, ny, nz);
			offset = addVertex(x, y+cellSize, z+cellSize, 0, 1, red, green, blue, vertexArray, offset, nx, ny, nz);
			offset = addVertex(x+cellSize, y+cellSize, z, 1, 0, red, green, blue, vertexArray, offset, nx, ny, nz);
			offset = addVertex(x+cellSize, y+cellSize, z+cellSize, 1, 1, red, green, blue, vertexArray, offset, nx, ny, nz);
			offset = addVertex(x+cellSize, y+cellSize, z, 1, 0, red, green, blue, vertexArray, offset, nx, ny, nz);
			offset = addVertex(x, y+cellSize, z+cellSize, 0, 1, red, green, blue, vertexArray, offset, nx, ny, nz);
		}
		
		if ((faces & Face.Bottom.bitmask) != 0)
		{
			nx = ny = nz = 0;
			ny = -1;
			offset = addVertex(x, y, z, 0, 1, red, green, blue, vertexArray, offset, nx, ny, nz);
			offset = addVertex(x+cellSize, y, z, 1, 1, red, green, blue, vertexArray, offset, nx, ny, nz);
			offset = addVertex(x, y, z+cellSize, 0, 0, red, green, blue, vertexArray, offset, nx, ny, nz);
			offset = addVertex(x+cellSize, y, z+cellSize, 1, 0, red, green, blue, vertexArray, offset, nx, ny, nz);
			offset = addVertex(x, y, z+cellSize, 0, 0, red, green, blue, vertexArray, offset, nx, ny, nz);
			offset = addVertex(x+cellSize, y, z, 1, 1, red, green, blue, vertexArray, offset, nx, ny, nz);
		}
		
		
		return offset;
	}


	private static int addVertex(float x, float y, float z, float u, float v, float r, float g, float b, FloatArray vertexArray, int offset, float nx, float ny, float nz)
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
		vertexArray.add(nx);
		vertexArray.add(ny);
		vertexArray.add(nz);
		return offset;
	}
}
