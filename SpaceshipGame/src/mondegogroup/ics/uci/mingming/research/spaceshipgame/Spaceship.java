package mondegogroup.ics.uci.mingming.research.spaceshipgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Spaceship {

	public float x;
	public float y;
	public Bitmap mBitmap;
	public boolean mProtected = false;
	
	Spaceship(Bitmap bit)
	{
		mBitmap = bit;
	}
	
	Spaceship(float _x, float _y, Bitmap _mBitmap)
	{
		x = _x;
		y = _y;
		mBitmap = _mBitmap;
		
	}
	
	public void updateBitmap(Bitmap _newBitmap)
	{
		mBitmap = _newBitmap;
	}
	
	public boolean getProtected()
	{
		return mProtected;
	}
	
	public void setProtected(boolean _protected)
	{
		mProtected = _protected;
	}
	
	public void updateDeltaPos(float deltaX, float deltaY)
	{
		x += deltaX;
		y += deltaY;
	}
	
	public void updatePos(float _X, float _Y)
	{
		x = _X;
		y = _Y;
	}
	
	public void setPos(float _x, float _y)
	{
		x = _x;
		y = _y;
	}
		
	public void draw(Canvas canvas)
	{
		canvas.drawBitmap(mBitmap, x , y, null);
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
	
	public Boolean hit(Asteroid a)
	{
		float maxX = x + getWidth();
		float minX = x;
		float minY = y;
		float maxY = y + getHeight();
		
		
		if(maxX < a.getX() + a.getWidth())
			maxX = a.getX() + a.getWidth();
		if(minX > a.getX())
			minX = a.getX();
		
		
		if(maxY < a.getY() + a.getHeight())
			maxY = a.getY()+a.getHeight();
		if(minY > a.getY())
			minY = a.getY();
		
		
		if((maxX-minX < getWidth() + a.getWidth())  && (maxY - minY < getHeight() + a.getHeight()))
				return true;
		else
			return false;

	}
	
	public Boolean hit(shield a)
	{
		float maxX = x + getWidth();
		float minX = x;
		float minY = y;
		float maxY = y + getHeight();
		
		
		if(maxX < a.getX() + a.getWidth())
			maxX = a.getX() + a.getWidth();
		if(minX > a.getX())
			minX = a.getX();
		
		
		if(maxY < a.getY() + a.getHeight())
			maxY = a.getY()+a.getHeight();
		if(minY > a.getY())
			minY = a.getY();
		
		
		if((maxX-minX < getWidth() + a.getWidth())  && (maxY - minY < getHeight() + a.getHeight()))
		    return true;
		else
			return false;

	}
}
