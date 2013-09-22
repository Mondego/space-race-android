package mondegogroup.ics.uci.mingming.research.spaceshipgame;
import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Asteroid {

	public float x;
	public float y;
	public float speed;
	public Bitmap mBitmap;
	
	Asteroid(Bitmap bit)
	{
		mBitmap = bit;
	}
	
	Asteroid(float _x, float _y, Bitmap _mBitmap)
	{
		x = _x;
		y = _y;
		speed = 5;
		mBitmap = _mBitmap;
	}
	
	Asteroid(float _x, float _y, float _speed, Bitmap _mBitmap)
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
