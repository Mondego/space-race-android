package mondegogroup.ics.uci.mingming.research.spaceshipgame;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class AnimationThread extends Thread {

	private SpaceshipGameView mView;
	private SurfaceHolder mHolder;
	
	public boolean updating;
	
	public AnimationThread(SurfaceHolder holder, SpaceshipGameView view)
	{
		super();
		this.mHolder = holder;
		this.mView = view;
	}
	
	private boolean running;
	public void setRunning(boolean running)
	{
		this.running = running;
	}
	
	
	public void setUpdating(boolean updating)
	{
		this.updating = updating;
	}
	
	@Override
	public void run()
	{
		Canvas canvas;
		while(running){
			canvas = null;
			// try locking the canvas for exclusive pixel editing
			// in the surface
			if(updating)
			{
				try {
					canvas = this.mHolder.lockCanvas();
					synchronized (mHolder) {
						// update game state 
						this.mView.Update();
						// render state to the screen
						// draws the canvas on the panel
						this.mView.render(canvas);
					}
				} finally {
					// in case of an exception the surface is not left in 
					// an inconsistent state
					if (canvas != null) {
						mHolder.unlockCanvasAndPost(canvas);					
					}
				}
			}

		}
	}
}
