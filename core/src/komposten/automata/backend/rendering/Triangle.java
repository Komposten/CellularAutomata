package komposten.automata.backend.rendering;

public class Triangle
{
	public static final short INDICES_PER_TRIANGLE = 3;
	short vertex1;
	short vertex2;
	short vertex3;
	
	
	public Triangle(int vertex1, int vertex2, int vertex3)
	{
		this((short)vertex1, (short)vertex2, (short)vertex3);
	}


	public Triangle(short vertex1, short vertex2, short vertex3)
	{
		this.vertex1 = vertex1;
		this.vertex2 = vertex2;
		this.vertex3 = vertex3;
	}


	@Override
	public String toString()
	{
		return "[" + vertex1 + "; " + vertex2 + "; " + vertex3 + "]";
	}
}
