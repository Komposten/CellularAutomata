package komposten.automata.backend.rendering;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Disposable;

public abstract class AbstractMesh implements Disposable
{
	protected float width;
	protected float height;
	protected int columns;
	protected int rows;
	protected float cellWidth;
	protected float cellHeight;
	
	public AbstractMesh(float width, float height, int targetSize)
	{
		this.width = width;
		this.height = height;
		columns = (int) Math.floor(width / targetSize);
		rows = (int) Math.floor(height / targetSize);
		cellWidth = width / columns;
		cellHeight = height / rows;
	}
	
	
	public int getColumnCount()
	{
		return columns;
	}
	
	
	public int getRowCount()
	{
		return rows;
	}
	
	
	public int getCellCount()
	{
		return columns * rows;
	}
	
	
	public int getIndex(int row, int column)
	{
		return row * columns + column;
	}
	
	
	public int getRow(int index)
	{
		return index / columns;
	}
	
	
	public int getColumn(int index)
	{
		return index % columns;
	}
	
	
	public void setColor(Color color, int row, int column)
	{
		setColor(color, getIndex(row, column));
	}
	
	
	public abstract void setColor(Color color, int index);
}
