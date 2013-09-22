package mondegogroup.ics.uci.mingming.research.spaceshipgame;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class SpaceshipGame extends Activity {
    final String TAG = "SpaceShipGameActivity";
    
	private SensorDataManager SensorDataM;
	private boolean connected;
	private SpaceshipGameView mView;
	
	Timer myTimer;
	TimerTask myTask;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
				
		//unBindService();
		
		connected = false;
		
		if (!connected)
			doBindService();
		
        // Next, we disable the application's title bar...
        requestWindowFeature( Window.FEATURE_NO_TITLE );
        // ...and the notification bar. That way, we can use the full screen.
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
                                WindowManager.LayoutParams.FLAG_FULLSCREEN );
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); 
		mView = new SpaceshipGameView(this);
		setContentView(mView);
		//mView.setBackgroundColor(Color.WHITE);
        myTimer = new Timer();
        myTask = new TimerTask() {
            @Override
            public void run() {
                UpdateAngle();
            }

        };
        myTimer.schedule(myTask, 1000, 30);
	}
	
    Boolean  tag = false;
	
	public void gameOver(String WinOrLose,String CurrentLevel)
	{
		mView.gameover = false;
		 //pop-up window to ask whether to continue
		 final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		 alertDialogBuilder.setTitle(CurrentLevel);

			// set dialog message
			alertDialogBuilder
				.setMessage("Game is Over! Do you want to continue?")
				.setCancelable(false)
				.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						// if this button is clicked, just close
						// the dialog box and do nothing
						dialog.cancel();
						tag = true;
					}
				  })
				.setNegativeButton("No",new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog,int id) {
						// if this button is clicked, close
						unBindService();
						
				    	if(myTimer != null)
				    	{
				    		myTask.cancel();
				    		myTimer.cancel();
				    		myTimer = null;
				    		//Log.i(TAG,"cancel the timer...");
				    	}
				    	
						// current activity
						SpaceshipGame.this.finish();
					}
				});

			this.runOnUiThread(new Thread(){
				public void run()
				{
					// create alert dialog
					AlertDialog alertDialog = alertDialogBuilder.create();

					// show it
					alertDialog.show();
				}
			});

	}

	public void reInit()
	{
		doBindService();
		mView.init();	
	}
		
	private boolean isBinded = false;
	// ///////////////////////////////////
	// Function: Service bind /////
	// /////////////////////////////////
	private void doBindService() {
		isBinded = bindService(new Intent(this, SensorDataManager.class), mConnection, Context.BIND_AUTO_CREATE);
		connected = true;
	}
	
	private void unBindService()
	{
		//Log.i(TAG,"in unBindService...");
		if(isBinded)
		{
			//Log.i(TAG,"begin unBindService...");
			this.unbindService(mConnection);
			connected = false;
		}
	}
	
    /*
	@Override
	protected void onDestroy()
	{
		unBindService();
		super.onDestroy();
	}
	*/
	
	// ///////////////////////////////////
	// Function: Service Connection/////
	// /////////////////////////////////

	private ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder binder) {
			// TODO Auto-generated method stub
			SensorDataM = ((SensorDataManager.MyBinder) binder).getSensorDataManager();
			/*
			Toast.makeText(SpaceshipGame.this, "Service Connected",
					Toast.LENGTH_SHORT).show();
					*/

		}

		public void onServiceDisconnected(ComponentName className) {
			// TODO Auto-generated method stub
			SensorDataM = null;
			/*
			Toast.makeText(SpaceshipGame.this, "Service DisConnected",
					Toast.LENGTH_SHORT).show();
					*/
		}
	};
	
	//final float ratio = 25f;
	final float ratio_acc = 350f;
    public void UpdateAngle()
    {
    	//Log.i(TAG,"update angle...");
    	
    	if(SensorDataM != null)
    	{
    		synchronized(tag)
    		{
    			if(!tag)
    			{
		    		//SpaceshipGameView.posx += SensorDataM.getAngleData()[1] * ratio;
		    		//SpaceshipGameView.posy += SensorDataM.getAngleData()[0] * ratio;
		    		SpaceshipGameView.posx -= SensorDataM.getDeltaAcc()[0] * ratio_acc;
		    		SpaceshipGameView.posy += SensorDataM.getDeltaAcc()[1] * ratio_acc;
    			}
    			
    		}	    	
    	}
  	
    	if(mView.gameover)
    	{
    		gameOver(mView.WinOrLose,mView.CurrentLevel);
    		mView.gameover = false;
    	}
    	 	
    	synchronized(tag)
    	{
    		//Log.i(TAG,"tag:" + tag);
	    	if(tag)
	    	{
	    		tag = false;
	    		unBindService();
	    		reInit();	    		
	    	}
    	}
    }
    
    /*
    public boolean onKeyDown(int keyCode, KeyEvent event) 
    {
    	Log.i(TAG,"On key down...");
    	switch(keyCode)
    	{
	    	case KeyEvent.KEYCODE_BACK:
	        	if(myTimer != null)
	        	{
	        		//unBindService();
	        		myTask.cancel();
	        		myTimer.cancel();
	        		myTimer = null;
	        		Log.i(TAG,"cancel the timer...");
	        	}
	        	this.finish();
	    		break;
		    default:
			    break;
    	}
    	
    	return super.onKeyDown(keyCode, event);
    }
*/
    
   
    @Override
    public void onBackPressed() {
    	//Log.i(TAG,"back key pressed...");
    	super.finish();
	
    	if(myTimer != null)
    	{
    		myTask.cancel();
    		myTimer.cancel();
    		myTimer = null;
    		//Log.i(TAG,"cancel the timer...");
    	}
    	mView.gameover = false;  	
    	unBindService();
		// current activity
		SpaceshipGame.this.finish();
    }

    
}
