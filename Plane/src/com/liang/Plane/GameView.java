package com.liang.Plane;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.liang.Plane.GlobleTypes.Types;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * 飞行棋--开始游戏--view类
 * @author 梁进劲
 * @date 2012-07-08
 * @declare 版权所有 &copy 梁进劲
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback{
	/**SurfaceHolder*/
	private SurfaceHolder holder = null;
	/**ApplicationDate 运行时共享数据*/
	private ApplicationDate appDate = null;
	/**activity context*/
	private Context context = null;
	
	private Paint paint = null;
	
	//布局数据
	/**上面游戏界面高度 08%*/
	private int aboveHeight = 0; 
	/**下面相关选项高度 screenHeight - aboveHeight*/
	private int belowHeight = 0;
	/**下面左边选框宽度 60%*/
	private int belowLeftWidth = 0;
	/**下面右边框宽度 screenWidth - belowLeftWidth*/
	private int belowRightWidth = 0;
	
	/**关卡类*/
	private GameTypes gameTypes = null;
	/**
	 * 单元格数据，以数组&quot;下标&quot;为编号，记录当单元格的起始点。<br/>
	 * CELL_LIST_X_KEY :x坐标<br/>CELL_LIST_Y_KEY:y坐标
	 */
	private List<Map<String,Integer>> cellList = null;
	
	/**游戏路径数据*/
	private List<Cell> gameDate = null;
	/**色子资源*/
	private int seResource = R.drawable.s1;
	/**是否正在滚动色子*/
	private boolean seRocking = false;
	/**我的位置0开始*/
	private int myPosition = 0;
	/**对手位置0开始**/
	private int opPosition = 0;
	/**终点下标*/
	private int endPosition = 0;
	
	/**当前为我摇色子*/
	private final int CURRENT_MY_ROCK = 0;
	/**当前为对手摇色子*/
	private final int CURRENT_OP_ROCK = 1;
	/**当前应为谁摇色子*/
	private int current_rock = CURRENT_MY_ROCK;
	
	/**我是否暂停*/
	private boolean myIsPause = false;
	/**对手是否暂停*/
	private boolean opIsPause = false;
	
	/**可容纳的提示的数量*/
	private int allowTipRows = 0;
	/**提示消息队列，手控最多为allowTipRows行*/
	private List<String> tipQueue = null;
	
	
	/**
	 * 构造
	 * @param context
	 */
	public GameView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		appDate = (ApplicationDate)context.getApplicationContext();
		
		holder = getHolder();
		holder.addCallback(this);
		this.setFocusable(true);
		
		paint = new Paint();
		paint.setStrokeWidth(5f);
		
		tipQueue = new ArrayList<String>();
	}
	
	/**开始绘制*/
	public void drawGame(){
		Canvas canvas = holder.lockCanvas();
		
		Bitmap bitmap = null;
		Bitmap tempBitmap = null;
		
		//画背景色
		bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.game_bg);
		tempBitmap = Bitmap.createScaledBitmap(bitmap,appDate.getScreenWidth(), appDate.getScreenHeight(),true);
		bitmap.recycle();
		canvas.drawBitmap(tempBitmap, 0, 0, paint);
		tempBitmap.recycle();
		
		//画边框
		paint.setColor(Color.CYAN);
		canvas.drawLine(0, aboveHeight, appDate.getScreenWidth(), aboveHeight, paint);
		canvas.drawLine(belowLeftWidth, aboveHeight, belowLeftWidth, appDate.getScreenHeight(), paint);
		
		//画路径
		Cell cell = null;
		for(int i=0 ; i<gameDate.size() ; i++){
			cell = gameDate.get(i);
			float x = cellList.get(cell.getId()).get(GlobleTypes.CELL_LIST_X_KEY);
			float y = cellList.get(cell.getId()).get(GlobleTypes.CELL_LIST_Y_KEY);
			
			if(i==0){//画起点
				bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.start_pos);
				tempBitmap = Bitmap.createScaledBitmap(bitmap,gameTypes.getCellWidth(), gameTypes.getCellHeight(),true);
				bitmap.recycle();
				canvas.drawBitmap(tempBitmap, x, y, paint);
				tempBitmap.recycle();
			}else if(i == (gameDate.size() -1)){//画终点
				bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.end_pos);
				tempBitmap = Bitmap.createScaledBitmap(bitmap,gameTypes.getCellWidth(), gameTypes.getCellHeight(),true);
				bitmap.recycle();
				canvas.drawBitmap(tempBitmap, x, y, paint);
				tempBitmap.recycle();
			}else{ //画中间点
				
				if(cell.getType() == Types.TYPE_FORWARD){//前进
					bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.go_pos);
				}else if(cell.getType() == Types.TYPE_BACK){//后退
					bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.back_pos);
				}else if(cell.getType() == Types.TYPE_PAUSE){//暂停
					bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pause_pos);
				}else{//没事
					bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.free_pos);
				}
				tempBitmap = Bitmap.createScaledBitmap(bitmap,gameTypes.getCellWidth(), gameTypes.getCellHeight(),true);
				bitmap.recycle();
				canvas.drawBitmap(tempBitmap, x, y, paint);
				tempBitmap.recycle();
			}
			
			paint.setColor(Color.RED);
			canvas.drawText(String.valueOf(i+1), x, y+10, paint);
			
		}
		
		
		//画角色一
		cell = gameDate.get(myPosition);
		float x = cellList.get(cell.getId()).get(GlobleTypes.CELL_LIST_X_KEY);
		float y = cellList.get(cell.getId()).get(GlobleTypes.CELL_LIST_Y_KEY);
		bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.head1);
		tempBitmap = Bitmap.createScaledBitmap(bitmap,gameTypes.getCellWidth()/2, gameTypes.getCellHeight()/2,true);
		bitmap.recycle();
		canvas.drawBitmap(tempBitmap, x+gameTypes.getCellWidth()/4, y, paint);
		tempBitmap.recycle();
		
		//画角色二
		cell = gameDate.get(opPosition);
		x = cellList.get(cell.getId()).get(GlobleTypes.CELL_LIST_X_KEY);
		y = cellList.get(cell.getId()).get(GlobleTypes.CELL_LIST_Y_KEY);
		bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.head2);
		tempBitmap = Bitmap.createScaledBitmap(bitmap,gameTypes.getCellWidth()/2, gameTypes.getCellHeight()/2,true);
		bitmap.recycle();
		canvas.drawBitmap(tempBitmap, x+gameTypes.getCellWidth()/4, y+gameTypes.getCellHeight()/2, paint);
		tempBitmap.recycle();
		
		
		//画色子
		bitmap = BitmapFactory.decodeResource(context.getResources(), seResource);
		tempBitmap = Bitmap.createScaledBitmap(bitmap,50,50,true);
		bitmap.recycle();
		canvas.drawBitmap(tempBitmap,
							belowLeftWidth + (appDate.getScreenWidth() - belowLeftWidth - 50)/2, 
							aboveHeight + (appDate.getScreenHeight() - aboveHeight - 50)/2, 
							paint);
		tempBitmap.recycle();
		
		//画我的状态
		cell = gameDate.get(myPosition);
		x = cellList.get(cell.getId()).get(GlobleTypes.CELL_LIST_X_KEY);
		y = cellList.get(cell.getId()).get(GlobleTypes.CELL_LIST_Y_KEY);
		bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.head1);
		tempBitmap = Bitmap.createScaledBitmap(bitmap,gameTypes.getCellWidth()/4, gameTypes.getCellHeight()/4,true);
		bitmap.recycle();
		canvas.drawBitmap(tempBitmap, 5,aboveHeight+5, paint);
		canvas.drawText("我:"+(myPosition+1)+"  <==>",5+tempBitmap.getWidth() , aboveHeight+5+10, paint);
		tempBitmap.recycle();
		
		//画我对手状态
		cell = gameDate.get(opPosition);
		x = cellList.get(cell.getId()).get(GlobleTypes.CELL_LIST_X_KEY);
		y = cellList.get(cell.getId()).get(GlobleTypes.CELL_LIST_Y_KEY);
		bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.head2);
		tempBitmap = Bitmap.createScaledBitmap(bitmap,gameTypes.getCellWidth()/4, gameTypes.getCellHeight()/4,true);
		bitmap.recycle();
		canvas.drawBitmap(tempBitmap, 5+tempBitmap.getWidth()+60,aboveHeight+5, paint);
		canvas.drawText("机器:"+(opPosition+1)+"",5+tempBitmap.getWidth()+60+tempBitmap.getWidth() , aboveHeight+5+10, paint);
		tempBitmap.recycle();
		
		
		//画提示
		float textSize = paint.getTextSize();
		
		for(int i=0 ; i<tipQueue.size() ; i++){
//			canvas.drawText("遇到狼，退一步"+i,5 , aboveHeight+5+textSize*(i+2), paint);tipPush
			canvas.drawText(tipQueue.get(i),5 , aboveHeight+5+textSize*(i+2), paint);
		}
		
		holder.unlockCanvasAndPost(canvas);
		
	}
	
	/**画画主线程*/
	private class DrawThread extends Thread{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			drawGame();
		}
	}
	
	/**滚动色子线程*/
	private class SeRock extends Thread{
		
		/**单元格数据*/
		private Cell cell = null;
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			int times = 1;
			int sleepSpec = 300;//休眠间隔
			int MAX_TIME = 2; //最大循环次数
			for(int i=0 ; i<MAX_TIME ; i++){
				try{
					seRocking = true;
					
					times = (int)System.currentTimeMillis()%6 + 1;
					times = times>6?1:times;
					
					if(times == 1){
						seResource = R.drawable.s1;
					}else if(times == 2){
						seResource = R.drawable.s2;
					}else if(times == 3){
						seResource = R.drawable.s3;
					}else if(times == 4){
						seResource = R.drawable.s4;
					}else if(times == 5){
						seResource = R.drawable.s5;
					}else if(times == 6){
						seResource = R.drawable.s6;
					}
					
					
					new DrawThread().start();
					sleep(sleepSpec);
				}catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				seRocking = false;
			}
			System.out.println("最后色子结果..."+times);
			
			
			
			if(current_rock == CURRENT_MY_ROCK){ //为我计算结果
				myPosition +=times;
				
				//潜在算法危险，如果两个格死跳，那将造成卡机。
				while(true){
					if(myPosition >= endPosition){ //到达终点
						myPosition = endPosition;
						tipPush("我"+gameDate.get(myPosition).getDesc()+":"+gameDate.get(myPosition).getGostep());
						System.out.println("我胜出...");
						break;
					}
					
					cell = gameDate.get(myPosition);
					if(cell.getType() == Types.TYPE_FORWARD){//向前
						myPosition += cell.getGostep();
						System.out.println(cell.getDesc()+":"+cell.getGostep());
						tipPush("我"+gameDate.get(myPosition).getDesc()+":"+gameDate.get(myPosition).getGostep());
					}else if(cell.getType() == Types.TYPE_BACK){//向后
						myPosition -= cell.getGostep();
						System.out.println(cell.getDesc()+":"+cell.getGostep());
						if(myPosition <= 0){
							myPosition = 0;
							tipPush("我:回到起点");
							System.out.println("回到了起点...");
							break;
						}
					}else if(cell.getType() == Types.TYPE_PAUSE){//暂停一次
						myIsPause = true;
						System.out.println(cell.getDesc()+":"+cell.getGostep());
						tipPush("我"+gameDate.get(myPosition).getDesc());
						break;
					}else if(cell.getType() == Types.TYPE_NON){//没事
						System.out.println("没事.AA..");
						break;
					}else if(cell.getType() == Types.TYPE_BEGIN){//起点
						System.out.println("回到了起点.AA..");
						tipPush("我:回到起点");
						break;
					}else if(cell.getType() == Types.TYPE_END){//终点
						tipPush("我"+gameDate.get(myPosition).getDesc()+":"+gameDate.get(myPosition).getGostep());
						System.out.println("我胜出.AA..");
						break;
					}else{//异常类型
						break;
					}

				}
				
				if(opIsPause){//对手暂停一次
					current_rock = CURRENT_MY_ROCK;
					myIsPause = false;
					opIsPause = false;
				}else{//下一个轮到对手
					current_rock = CURRENT_OP_ROCK;
					myIsPause = false;
					opIsPause = false;
					if(myPosition < endPosition)
						new SeRock().start(); //对手自动摇色
				}
			}else{//为对手计算结果
				opPosition +=times;
				
				//潜在算法危险，如果两个格死跳，那将造成卡机。
				while(true){
					if(opPosition >= endPosition){ //到达终点
						opPosition = endPosition;
						tipPush("对手:"+gameDate.get(opPosition).getDesc()+":"+gameDate.get(opPosition).getGostep());
						System.out.println("对手胜出...");
						break;
					}
					cell = gameDate.get(opPosition);
					if(cell.getType() == Types.TYPE_FORWARD){//向前
						opPosition += cell.getGostep();
						System.out.println(cell.getDesc()+":对手"+cell.getGostep());
						tipPush("对手:"+gameDate.get(opPosition).getDesc()+":"+gameDate.get(opPosition).getGostep());
					}else if(cell.getType() == Types.TYPE_BACK){//向后
						opPosition -= cell.getGostep();
						System.out.println(cell.getDesc()+":对手"+cell.getGostep());
						if(opPosition <= 0){
							opPosition = 0;
							System.out.println("对手回到了起点...");
							tipPush("对手:回到起点");
							break;
						}
					}else if(cell.getType() == Types.TYPE_PAUSE){//暂停一次
						System.out.println(cell.getDesc()+":对手"+cell.getGostep());
						tipPush("对手:"+gameDate.get(opPosition).getDesc());
						break;
					}else if(cell.getType() == Types.TYPE_NON){//没事
						System.out.println("没事.BB..");
						break;
					}else if(cell.getType() == Types.TYPE_BEGIN){//起点
						System.out.println("回到了起点.BB..");
						tipPush("对手:回到起点");
						break;
					}else if(cell.getType() == Types.TYPE_END){//终点
						System.out.println("对手胜出.BB..");
						tipPush("对手:"+gameDate.get(opPosition).getDesc()+":"+gameDate.get(opPosition).getGostep());
						break;
					}else{//异常类型
						break;
					}
				}
				
				if(myIsPause){//我暂停一次
					current_rock = CURRENT_OP_ROCK;
					myIsPause = false;
					opIsPause = false;
					new SeRock().start(); //对手自动摇色
				}else{//下一个轮到对手
					current_rock = CURRENT_MY_ROCK;
					myIsPause = false;
					opIsPause = false;
				}
			}
			
			new DrawThread().start();
			
		}
		
	}
	
	/**
	 * 提示入队
	 * @param tip 提示
	 */
	private void tipPush(String tip){
		if(tipQueue.size() >= allowTipRows)
			tipQueue.remove(0);
		tipQueue.add(tipQueue.size(), tip);
	}
	
	/**
	 * 据点击的范围，判断点击事件
	 * @param actionX x点
	 * @param actionY y点
	 */
	public void touchHandler(float actionX,float actionY){
		
		if(actionX > belowLeftWidth && actionY > aboveHeight){//点击了色子区域
			if(!seRocking)
				new SeRock().start();
			System.out.println("点击了色子区域...");
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		
		if(event.getAction() == MotionEvent.ACTION_UP){
			float actionX = event.getX();
			float actionY = event.getY();
			touchHandler(actionX, actionY);
			System.out.println("点中:..."+actionX+":"+actionY);
		}
		
		return true;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		System.out.println("surface change ...."+width+":"+height);
		appDate.setScreenWidth(width);
		appDate.setScreenHeight(height);
		
		aboveHeight = (int)(height * 0.8D);
		belowLeftWidth = (int)(width * 0.6D);
		
		belowHeight = appDate.getScreenHeight() - aboveHeight;
		belowRightWidth = appDate.getScreenWidth() - belowLeftWidth;
		
		gameTypes = new GameTypes(width, aboveHeight);
		cellList = gameTypes.getCellList();//获取所有格数据
		gameDate= gameTypes.getGameByType();
		
		endPosition = gameDate.size() - 1;
		endPosition = endPosition>0?endPosition:0;
		
		//画提示
		float textSize = paint.getTextSize();
		//一列可容纳多少行字
		allowTipRows = (int)(belowHeight/textSize);
		allowTipRows -=2;//减一行用来做显示状态 一行分界线
		
		new DrawThread().start();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		System.out.println("surface create .......");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		System.out.println("surface destory......");
	}
	
}
