package komposten.automata.backend.rendering;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;

public class GridPixmap extends AbstractMesh
{
	private Pixmap pixmap;
	private Texture texture;

	public GridPixmap(float width, float height, int targetSize)
	{
		super(width, height, targetSize);
		createCells();
		refreshTexture();

		System.out.println("===CELLS: [" + columns + ", " + rows + "]=" + getCellCount());
	}


	private void createCells()
	{
		pixmap = new Pixmap((int)width, (int)height, Format.RGBA8888);

		for (int r = 0; r < rows; r++)
		{
			for (int c = 0; c < columns; c++)
			{
				int x = (int) (c * cellWidth);
				int y = (int) (r * cellHeight);
				
				pixmap.setColor(Color.WHITE);
				pixmap.fillRectangle(x, y, (int)cellWidth, (int)cellHeight);
			}
		}
	}
	
	
	@Override
	public void setColor(Color color, int row, int column)
	{
		int x = (int) (column * cellWidth);
		int y = (int) (row * cellHeight);
		
		pixmap.setColor(color);
		pixmap.fillRectangle(x, y, (int)cellWidth, (int)cellHeight);
	}
	
	
	@Override
	public void setColor(Color color, int index)
	{
		setColor(color, getRow(index), getColumn(index));
	}
	
	
	public Texture getTexture()
	{
		return texture;
	}
	
	
	public void refreshTexture()
	{
		if (texture != null)
		{
			texture.dispose();
		}
		
		texture = new Texture(pixmap);
	}
	
	
	@Override
	public void dispose()
	{
		pixmap.dispose();
		texture.dispose();
	}
}
