package mondegogroup.ics.uci.mingming.research.spaceshipgame;


import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;


public class SpaceshipGameView extends SurfaceView implements SurfaceHolder.Callback{
    private final String TAG = "SpaceshipGameView";
   
	private SurfaceHolder mHolder;
	
	private Bitmap mSpaceshipBitmap;
	private Bitmap mAsteroidBitmap;
	private Bitmap mShieldBitmap;
	private Bitmap mSpaceshipProtectedBitmap;
	private Bitmap mSpaceshipProtectedBitmap2;
    private Spaceship mPlayer;
	
    private shield mShield;
    public float shieldX;
    public float shieldY;
    public final float shieldSpeed = 4;
    
    public static float posx;   //spaceship location x
    public static float posy;   //spaceship location y
    private final float playerSizeWidth;
    private final float playerSizeHeight;
    private final float screenWidth;
    private final float screenHeight;
    private AnimationThread mThread;
    
    private ArrayList<Asteroid> Bombs;
    private Timer mTimer;
    private Timer shieldTimer;
    private boolean newshield = true;
    
    private final int init_asteroid_num = 4;
    private int asteroid_num = init_asteroid_num;
    private final float speed_range_init = 2;
    private float speed_range = speed_range_init;
    private final int max_speed_range = 10;
    private int timeinterval = 2000;
    private int shieldtimeinterval = 3000;
    private final int min_timeinterval = 100;
    final Context mContext;

    public String WinOrLose = "Start";
    public String CurrentLevel = "0" ; 
    private int gameWinLine = 60;
    
    private int gameLevel = 1;
    
    public int playerScores = 0;
    
    
    private int ScoreIncremental = 100;
    
    private final int protectionTime = 1000; // 2 seconds
    
    private Paint myPaint;
    
    private Paint gameLevelPaint;
    
    private final int fontSize = 30;
    
    private final int textToRightMargin = 200;
    private final int textToLeftMargin = 10;
    
    private final float increasediffratio = 0.15f;
    
    private int NumOfLifes = 3;  //initial lifes
    
    private int UpLevelScore = 1000;  // upgrade level score
    
	@SuppressLint("NewApi")
	public SpaceshipGameView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub

		mContext = context.getApplicationContext();
		mHolder = getHolder();
		mHolder.addCallback(this);
		//init bitmaps after spaceship, asteroid, shield
		mSpaceshipBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.spaceship);
		mAsteroidBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.asteroid);
		mShieldBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.shield);
		mSpaceshipProtectedBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.spaceshipprotected);
		mSpaceshipProtectedBitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.spaceshipprotected2);
		//player's size
		playerSizeWidth = mSpaceshipBitmap.getWidth();
		playerSizeHeight = mSpaceshipBitmap.getHeight();
		
		//screen size
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point mSize = new Point();
        display.getSize(mSize);
        screenWidth = mSize.x;
        screenHeight = mSize.y;
        
        //set initial pos of the spaceship
        posx = screenWidth /2 - playerSizeWidth/2;
        posy = screenHeight  - playerSizeHeight;
		
        //set shield's initial location
        shieldX = (float)Math.random() * (screenWidth-mShieldBitmap.getWidth()/2);
        shieldY = -(float)Math.random() * screenHeight * 2; //somewhere above the top of the screen
        
        //the line to win the game: pass the line, then win
        gameWinLine = (int) (screenHeight / 20);
        
        //initialize the bombs locations
		Bombs = new ArrayList<Asteroid>();		
		for(int i = 0; i < init_asteroid_num; i++)
		{
			Asteroid a = new Asteroid((float)Math.random()*screenWidth, (float)Math.random()*screenHeight/2,(float)Math.random()*speed_range, mAsteroidBitmap);
			Bombs.add(a);
		}
				
		mPlayer = new Spaceship(posx,posy,mSpaceshipBitmap);
		
		mShield = new shield(shieldX,shieldY,mShieldBitmap);
		
		myPaint = new Paint();
		myPaint.setColor(Color.RED);
		myPaint.setStyle(Paint.Style.FILL_AND_STROKE); 
				
		gameLevelPaint = new Paint();
		gameLevelPaint.setColor(Color.WHITE);
		gameLevelPaint.setStyle(Paint.Style.FILL_AND_STROKE); 
		gameLevelPaint.setTextSize(fontSize);
		
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateAsteroids();
            }

        }, 1500, timeinterval);
        
        /*
        shieldTimer = new Timer();
        shieldTimer.schedule(new TimerTask(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				updateShield();
			}
        	
        },3000,shieldtimeinterval);
		*/
        
		mThread = new AnimationThread(mHolder,this);
		mThread.setUpdating(true);
	}
	
	private final int asteroidRange = -10;
	public void updateAsteroids()
	{
		//init asteorid
		for(int i = 0; i < init_asteroid_num; i++)
		{
			Asteroid a = new Asteroid((float)Math.random()*screenWidth, (float)Math.random()* asteroidRange,(float)Math.random()*speed_range, mAsteroidBitmap);
			Bombs.add(a);
		}
	}
		 
	public void updateShield()
	{
		if(newshield)
		{
			//Log.i(TAG,"reinit shield");
			newshield = false;
		}
	}
	
	/**
	 * update game elements' locations
	 * */
	 public void Update()
	 {
		 // update player
		 if(playerScores > UpLevelScore)
		 {
			 playerScores -= UpLevelScore;
			 NumOfLifes ++;
			
		 }
		 
		 //keep player inside of screen
		 if (posx < 0)
			 posx = 0;
		 else if(posx > screenWidth - playerSizeWidth/2)
			 posx = screenWidth - playerSizeWidth/2;
		 
		 // test whether player reaches the finish line
		 if (posy <= gameWinLine - playerSizeHeight/2)
		 {
			 // game is over! you win
			 posy = gameWinLine - playerSizeHeight/2;
			 
			 WinOrLose = "Congrats! You Win!";
			 CurrentLevel = "Level " + gameLevel;
			 
			 //increase the difficult of the game
			 asteroid_num += 2;
			 speed_range = speed_range + 0.8f < max_speed_range? speed_range + 0.8f: max_speed_range;
			 
			 timeinterval = (timeinterval - 10)> min_timeinterval ? (timeinterval - 10):min_timeinterval;
			 
			 //strategy to increase user's scores
			 playerScores += ScoreIncremental;
			 
			 //game level 
			 gameLevel++;
			 
			 //gameOver();
			 init();
		 }
		 else if(posy > screenHeight - playerSizeHeight/2)
			 posy = screenHeight - playerSizeHeight/2;
		 
		 mPlayer.updatePos(posx, posy);
		 
		 // update asteroids
		 for(int i = 0; i < Bombs.size(); i++)
		 {
			 Asteroid a = Bombs.get(i);
			 a.updatePos();
			 //remove bombs that move out of the bottom line
			 if(a.getY() > screenHeight)
			 {
				 Bombs.remove(i);
			 }
		 }
		 
		 // if player is under proetection, then check whether it hits the asteroid
		 if(!mPlayer.getProtected())
		 {
			 //detect whether player hits the asteroid
			 Boolean hit = false;
			 
			 for(int i = 0; i < Bombs.size(); i++)
			 {
				 hit = mPlayer.hit(Bombs.get(i));
				 if(hit == true)
				 {
					 WinOrLose = "Sorry! The spaceship is crushed!";
					 CurrentLevel = "Level " + gameLevel;
					 NumOfLifes --;
			         if(NumOfLifes <=0)
			         {
			        	 gameOver();
			         }
			         else
			         {
			        	 init();
			         }
					 break;
				 }
			 }
		 }
		 
		 boolean hitshield = mPlayer.hit(mShield);
		 if(hitshield) // hit shield and get protected
		 {
			 shieldY = 2* screenHeight; //make the shield disappear
			 mPlayer.updateBitmap(mSpaceshipProtectedBitmap); //update player's spaceship
			 mPlayer.setProtected(true);  //set it protected
			 //count down the protection time
		     Timer countdownTimer = new Timer();
		     countdownTimer.schedule(new TimerTask(){
				@Override
				public void run() {
					// TODO Auto-generated method stub
					 mPlayer.updateBitmap(mSpaceshipProtectedBitmap2); // intermediate shape of the spaceship
				     Timer countdownTimer2 = new Timer();
				     countdownTimer2.schedule(new TimerTask(){
						@Override
						public void run() {
							// TODO Auto-generated method stub
							 mPlayer.updateBitmap(mSpaceshipBitmap);
							 mPlayer.setProtected(false);
						}}, protectionTime);
				}}, protectionTime);
		 }
		 
		 if(shieldY >= screenHeight)
		 {
			// Log.i(TAG,"shield out: " + shieldY);
			 shieldY = -(float)Math.random() * screenHeight * Math.max(0.5f,2 - increasediffratio);
			 shieldX = (float)Math.random() * (screenWidth-mShieldBitmap.getWidth()/2);				 
			 //newshield = true;				 
		 }
		 else
		 {
			// Log.i(TAG,"shield pos: " + + shieldY);
			 shieldY += shieldSpeed;				 
		 }
		 mShield.updatePos(shieldX,shieldY);
		 
		 /*
		 if(!newshield)
		 {
			 if(shieldY >= screenHeight)
			 {
				// Log.i(TAG,"shield out: " + shieldY);
				 shieldY = -(float)Math.random() * screenHeight;
				 shieldX = (float)Math.random() * screenWidth;				 
				 newshield = true;				 
			 }
			 else
			 {
				// Log.i(TAG,"shield pos: " + + shieldY);
				 shieldY += shieldSpeed;				 
			 }
			 mShield.updatePos(shieldX,shieldY);			 
		 }	
		 */	 
	 }
	 
	 
	 public boolean gameover = false;	 
	 public void gameOver()
	 {		 
		 mThread.setUpdating(false);
		
		 //reset user's data
		 NumOfLifes = 3;
		 gameover = true;
	     playerScores = 0;
	     gameLevel = 1;
	     asteroid_num = init_asteroid_num;
		 speed_range = speed_range_init;
	 }
	 
	/**
	 * init the game
	 * */
	public void init()
	 {	
		//set player's initial location to the center of the bottom of the screen
        posx = screenWidth /2 - playerSizeWidth/2;
        posy = screenHeight  - playerSizeHeight;
		 		 
		//init asteorid
		 if(Bombs.size() > 0)
			 Bombs.clear();
		 
		 for(int i = 0; i < init_asteroid_num; i++)
		 {
			Asteroid a = new Asteroid((float)Math.random()*screenWidth, (float)Math.random()*screenHeight/2,(float)Math.random()*speed_range, mAsteroidBitmap);
			Bombs.add(a);
		 }
				
		//init shield
        shieldX = (float)Math.random() * screenWidth;
        shieldY = -(float)Math.random() * screenHeight;
        newshield = true;
	                     
		mThread.setUpdating(true);	
	 }
	 
	 public void render( Canvas canvas)
	 {
		 if(canvas != null)
		 {
			 canvas.drawColor(Color.BLACK); //draw background 
			 canvas.drawLine(0, gameWinLine, screenWidth, gameWinLine, myPaint); // draw game finish line
			 mPlayer.draw(canvas); // draw player
			 for(int i = 0; i < Bombs.size(); i++)
				 Bombs.get(i).draw(canvas);
			 mShield.draw(canvas); // draw shield
			 for(int i = 0; i < NumOfLifes; i++)
				 canvas.drawBitmap(mSpaceshipBitmap, screenWidth/2 - playerSizeWidth + i * playerSizeWidth, 0, null);
			 canvas.drawText("Level: " + gameLevel, textToLeftMargin,fontSize, gameLevelPaint);
			 canvas.drawText("Score: " + playerScores, screenWidth-textToRightMargin, fontSize, gameLevelPaint);
		 }
	 }

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub		
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
       mThread.setRunning(true);
       mThread.start();
        
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		mThread.setRunning(false);
	}

}
