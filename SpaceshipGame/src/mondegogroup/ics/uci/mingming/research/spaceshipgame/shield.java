package mondegogroup.ics.uci.mingming.research.spaceshipgame;
import android.graphics.Bitmap;
import android.graphics.Canvas;

public class shield {

	public float x;
	public float y;
	public float speed;
	public Bitmap mBitmap;
	
	public shield(Bitmap bit)
	{
		mBitmap = bit;
	}
	
	public shield(float _x, float _y, Bitmap _mBitmap)
	{
		x = _x;
		y = _y;
		speed = 5;
		mBitmap = _mBitmap;
	}
	
	public shield(float _x, float _y, float _speed, Bitmap _mBitmap)
	{
		x = _x;
		y = _y;
		speed = _speed;
		mBitmap = _mBitmap;
	}
	
	public void updatePos()
	{
		y += speed;
	}
	
	public void updatePos(float _X, float _Y)
	{
		x = _X;
		y = _Y;
	}
		
	public void draw(Canvas canvas)
	{
		canvas.drawBitmap(mBitmap, x , y, null);
	}
	
	public float getY()
	{
		return y;
	}
	
	public float getX()
	{
		return x;
	}
	
	public float getWidth()
	{
		if(mBitmap != null)
			return mBitmap.getWidth();
		else
			return 0;
	}
	
	public float getHeight()
	{
		if(mBitmap != null)
			return mBitmap.getHeight();
		else
			return 0;
	}
}
