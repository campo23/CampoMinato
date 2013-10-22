package com.campominato;

import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnClickListener;


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
  static final int easyMines = 10;
  static final int mediumMines = 40;
  int diff;
  int totalCols;
  int totalRows;
  int totalMines;
  Tile[][] tiles;
  int tilePadding = 1;
  int tileWH = 78;
  TableLayout mineField;
  
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
	  super.onCreate(savedInstanceState);
	  setContentView(R.layout.game);
	  diff = getIntent().getIntExtra(KEY_DIFFICULTY, DIFFICULTY_EASY);
	  mineField = (TableLayout) findViewById(R.id.MineField);   
	  createGameBoard(diff);
	  showGameBoard();
  }
  
  public void createGameBoard(int diff)
  {
    //set total rows and columns based on the difficulty
    totalRows = easyRows;
    totalCols = easyColumns;
    totalMines = easyMines;
    switch(diff)
    {
      case 0:
        break;
      case 1:
        totalRows = mediumRows;
        totalCols = mediumColumns;
        totalMines = mediumMines;
        break;
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
            }
            boolean minesSet = false;
			if(!minesSet)
            {
              minesSet = true;
            }
            
            if(!tiles[curRow][curCol].isFlag())
            {
              if(tiles[curRow][curCol].isMine())
              {
                loseGame();
              }
              else if(wonGame())
              {
                winGame();
              }
              else
              {
                uncoverTiles();
              }
              
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
      
      if(mineRow == row && mineCol == col) //cicked tile
      {
        i--;
      }
      else if(tiles[mineRow][mineCol].isMine()) //already a mine
      {
        i--;
      }
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
      {
        timerText.setText("00" + curTime);
      }
      else if (secondsPassed < 100)
      {
        timerText.setText("0" + curTime);
      }
      else
      {
        timerText.setText(curTime);
      }
      timer.postAtTime(this, currentMilliseconds);
      //run again in 1 second
      timer.postDelayed(updateTimer, 1000);
    }
  };

  
  public void winGame() {}
  
  public boolean wonGame() {return false;}
  
  public void uncoverTiles() {}
  
  public void loseGame() {}

}