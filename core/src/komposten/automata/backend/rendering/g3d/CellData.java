package komposten.automata.backend.rendering.g3d;

import com.badlogic.gdx.graphics.Color;

public class CellData
{
	public int index;
	public short type;
	public short faceMask;
	public short r;
	public short g;
	public short b;
	public float[] vertexData;
	
	
	public void updateVertices(int x, int y, int z, int cellSize)
	{
		int faces = Integer.bitCount(faceMask);
		
		int floats = faces*VertexFactory.VERTICES_PER_FACE*VertexFactory.VALUES_PER_VERTEX;
		
		if (vertexData == null || vertexData.length != floats)
		{
			vertexData = new float[floats];
		}
		
		VertexFactory.addFaces(faceMask, x, y, z, cellSize, r, g, b, vertexData, 0);
	}
	
	
	public void setColor(Color color)
	{
		r = (short) (color.r * 255);
		g = (short) (color.g * 255);
		b = (short) (color.b * 255);
		
		VertexFactory.updateColors(faceMask, r, g, b, vertexData, 0);
	}
}
