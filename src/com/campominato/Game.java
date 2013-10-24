package com.campominato;

import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnClickListener;
import android.os.Handler;


public class Game extends Activity
{
	  public static final String KEY_DIFFICULTY = "com.campominato.difficulty";
	  public static final int DIFFICULTY_EASY = 0;
	  public static final int DIFFICULTY_MEDIUM = 1;
	  public static final int DIFFICULTY_HARD = 2;
	  static final int easyRows = 7;
	  static final int mediumRows = 11;
	  static final int easyColumns = 7;
	  static final int mediumColumns = 7;
	  static final int easyMines = 7;
	  static final int mediumMines = 12;
	  int diff;
	  int totalCols;
	  int totalRows;
	  int totalMines; 
	  int totalCoveredTiles = 0;
	  int tilePadding = 1;
	  int tileWH = 78;
	  int secondsPassed = 0;
	  Tile[][] tiles;
	  TableLayout mineField;
	  TextView timerText;
	  Handler timer = new Handler();
  
  
  @Override
	  protected void onCreate(Bundle savedInstanceState)
	  {
	  		super.onCreate(savedInstanceState);
	  		setContentView(R.layout.game);
	  		diff = getIntent().getIntExtra(KEY_DIFFICULTY, DIFFICULTY_EASY);
	  		mineField = (TableLayout) findViewById(R.id.MineField);   
	  		timerText = (TextView) findViewById(R.id.Timer);
	  		createGameBoard(diff);
	  		showGameBoard();
	  }
	  
	  public void createGameBoard(int diff)
	  {
		  	//set total rows and columns based on the difficulty
		  	if (diff == 0) 
		  	{
		  			totalRows = easyRows;
		  			totalCols = easyColumns;
		  			totalMines = easyMines;
		  	}
		  	else if (diff == 1)
		  	{
		  			totalRows = mediumRows;
		  			totalCols = mediumColumns;
		  			totalMines = mediumMines;
		  	}
	    
		  	//setup the tiles array
		  	tiles = new Tile[totalRows][totalCols];
	    
		  	for(int row = 0; row < totalRows;row++)
		  	{
		  			for(int col = 0; col < totalCols;col++)
		  			{
		  					//create a tile
		  					tiles[row][col] = new Tile(this);
		  					//set the tile defaults
		  					tiles[row][col].setDefaults(); 
			        
		  					final int curRow = row;
		  					final int curCol = col;
			        
		  					//add a click listener
		  					tiles[row][col].setOnClickListener(new OnClickListener()
		  					{
		  							@Override
		  							public void onClick(View view)
		  							{
		  									boolean timerStarted = false;
		  									if(!timerStarted)
		  									{
		  											timerStarted = true;
		  											startTimer();
		  									}
		  									boolean minesSet = false;
		  									if(!minesSet)
		  											minesSet = true;
		  									
			            
		  									if(!tiles[curRow][curCol].isFlag())
		  									{
		  											if(tiles[curRow][curCol].isMine())
		  													loseGame();
		  											
		  											else if(checkWonGame())
		  													winGame();
			        						
		  											else
		  													uncoverTiles(curRow, curCol);
			        						
			              
		  									}
		  							}
		  					});
	        
		  					//add a long click listener
		  					tiles[row][col].setOnLongClickListener(new OnLongClickListener()
		  					{
		  							@Override
		  							public boolean onLongClick(View view)
		  							{
		  								return true;
		  							}
		  					});
		  			}
		  	}
		  	setupMineField(totalRows, totalCols);
	  }
	  
	  public void showGameBoard()
	  {  
	
		  	//for every row
		  	for(int row=0;row<totalRows;row++)
		  	{
		  			//create a new table row
		  			TableRow tableRow = new TableRow(this);
	      
		  			//set the height and width of the row
		  			tableRow.setLayoutParams(new LayoutParams((tileWH * tilePadding) * totalCols, tileWH * tilePadding));
	      
		  			//for every column
		  			for(int col=0;col<totalCols;col++)       
		  			{
		  					//set the width and height of the tile
		  					tiles[row][col].setLayoutParams(new LayoutParams(tileWH * tilePadding,  tileWH * tilePadding)); 
		  					//add some padding to the tile
		  					tiles[row][col].setPadding(tilePadding, tilePadding, tilePadding, tilePadding);
		  					//add the tile to the table row
		  					tableRow.addView(tiles[row][col]);
	        
		  			}
		  			//add the row to the minefield layout
		  			mineField.addView(tableRow,new TableLayout.LayoutParams((tileWH * tilePadding) * totalCols, tileWH * tilePadding)); 
		  	}
	  }
					
	  public void setupMineField(int row, int col)
	  {
		  	Random random = new Random();
		  	int mineRow;
		  	int mineCol;
		  	for(int i = 0; i < totalMines; i++)
		  	{
		  			mineRow = random.nextInt(totalRows);
		  			mineCol = random.nextInt(totalCols);
	      
		  			if(mineRow == row && mineCol == col) 
		  					//cicked tile
		  					i--;
		  			
		  			else if(tiles[mineRow][mineCol].isMine()) 
		  					//already a mine
		  					i--;
		  			
		  			else
		  			{
		  					//plant a new mine
		  					tiles[mineRow][mineCol].plantMine();
		  					//go one row and col back
		  					int startRow = mineRow-1;
		  					int startCol = mineCol-1;
		  					//check 3 rows across and 3 down
		  					int checkRows = 3;
		  					int checkCols = 3;
		  					if(startRow < 0) //if it is on the first row
		  					{
		  							startRow = 0;
		  							checkRows = 2;
		  					}
		  					else if(startRow+3 > totalRows) //if it is on the last row
		  							checkRows = 2;
	        
		  					if(startCol < 0)
		  					{
							        startCol = 0;
							        checkCols = 2;
		  					}
		  					else if(startCol+3 > totalCols) //if it is on the last row
		  							checkCols = 2;
	        
		  					for(int j=startRow;j<startRow+checkRows;j++) //3 rows across
		  					{
		  							for(int k=startCol;k<startCol+checkCols;k++) //3 rows down
		  							{
		  									if(!tiles[j][k].isMine()) //if it isn't a mine
		  											tiles[j][k].updateSurroundingMineCount();
		  							}
		  					}
		  			}
		  	}
	  }
	  
	  public void startTimer()
	  {
		  	if(secondsPassed == 0)
		  	{
		  			timer.removeCallbacks(updateTimer);
		  			timer.postDelayed(updateTimer, 1000);
		  	}
	  }
	  
	  public void stopTimer()
	  {
		  	timer.removeCallbacks(updateTimer);
	  }
	  
	  private Runnable updateTimer = new Runnable()
	  {
		  	public void run()
		  	{
		  			long currentMilliseconds = System.currentTimeMillis();
		  			++secondsPassed;
		  			String curTime = Integer.toString(secondsPassed);
		  			//update the text view
		  			if (secondsPassed < 10)
		  					timerText.setText("00" + curTime);
		  			
		  			else if (secondsPassed < 100)
		  					timerText.setText("0" + curTime);
		  			
		  			else
		  					timerText.setText(curTime);
	      
		  			timer.postAtTime(this, currentMilliseconds);
		  			//run again in 1 second
		  			timer.postDelayed(updateTimer, 1000);
		  	}
	  };
	  
	 /* public void endGame()
	  {
	    imageButton.setBackgroundResource(R.drawable.smile);
	    
	    // remove the table rows from the minefield table layout
	    mineField.removeAllViews();
	    
	    // reset variables
	    timerStarted = false;
	    minesSet = false;
	  }
	*/
	  
	  public void winGame() {}
	  
	  public void uncoverTiles(int row, int col)
	  {
		    //if the tile is a mine, or a flag return
		    if(tiles[row][col].isMine() || tiles[row][col].isFlag())
		    		return;
	    
		    tiles[row][col].openTile();
	    
		    if(tiles[row][col].getNoSurroundingMines() > 0)
		    		return;
	    
		    //go one row and col back
		    int startRow = row-1;
		    int startCol = col-1;
		    //check 3 rows across and 3 down
		    int checkRows = 3;
		    int checkCols = 3;
		    if(startRow < 0) //if it is on the first row
		    {
		    		startRow = 0;
		    		checkRows = 2;
		    }
		    else if(startRow+3 > totalRows) //if it is on the last row
		    		checkRows = 2;
	        
		    if(startCol < 0)
		    {
		    		startCol = 0;
		    		checkCols = 2;
		    }
		    else if(startCol+3 > totalCols) //if it is on the last row
		    		checkCols = 2;
	        
		    for(int i=startRow;i<startRow+checkRows;i++) //3 or 2 rows across
		    {
		    		for(int j=startCol;j<startCol+checkCols;j++) //3 or 2 rows down
		    		{
		    				if(tiles[i][j].isCovered())
		    						uncoverTiles(i,j);
		    		}
		    }
	  }
	  
	  public boolean checkWonGame()
	  {
		  	if(totalCoveredTiles == totalMines)
		  			return true;
		  	else
		  			return false;
	  }
	  
	  public void loseGame()
	  { /*
	    	stopTimer();
	    	imageButton.setBackgroundResource(R.drawable.lose);
	    
	    	for(int i=0;i<totalRows;i++)
	    	{
	      			for(int j=0;j<totalCols;j++)
	      			{
	        				//if the tile is covered
	        				if(tiles[i][j].isCovered())
	        				{
	          						//if there is no flag or mine
	          						if(!tiles[i][j].isFlag() && !tiles[i][j].isMine())
	            							tiles[i][j].openTile();
	          						
	          						//if there is a mine but no flag
	          						else if(tiles[i][j].isMine() && !tiles[i][j].isFlag())
	            							tiles[i][j].openTile();
	          				}
	          
	        		}
	      	}*/
	  }
		
}