package com.campominato;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;


public class Tile extends Button
{
  private boolean isMine;
  private boolean isFlag;
  private boolean isQuestionMark;
  private boolean isCovered;
  private int noSurroundingMines;
  
  public Tile(Context context)
  {
    super(context);
  }
  
  public Tile(Context context, AttributeSet attrs)
  {
    super(context, attrs);
  }

  public Tile(Context context, AttributeSet attrs, int defStyle)
  {
    super(context, attrs, defStyle);
  }
  
  public void setDefaults()
  {
    isMine = false;
    isFlag = false;
    isQuestionMark = false;
    isCovered = true;
    noSurroundingMines = 0;
    
    this.setBackgroundResource(R.drawable.tile);
  }
  
  public void setMine(boolean mine)
  {
    isMine = true;
    isCovered = false;
    
    this.setBackgroundResource(R.drawable.mine);
  }
  
  public void setFlag(boolean flag)
  {
    isFlag = true;
    
    this.setBackgroundResource(R.drawable.flag);
  }
  
  public void setQuestionMark(boolean flag)
  {
    
  }
  
  public void setUncovered()
  {
    isCovered = false;
    
    this.setBackgroundResource(R.drawable.tileb);
  }
  
  public void setSurroundingNumber(int number)
  {
    
  }
  
  public void updateSurroundingMineCount()
  {
    noSurroundingMines++;
    String img =  "mines"+noSurroundingMines;
    int drawableId = getResources().getIdentifier(img, "drawable", "com.codedazzle.minesweeper");
    this.setBackgroundResource(drawableId);
  }
  
  public void openTile()
  {
    
  }
  
  //set the tile as a mine
  public void plantMine()
  {
	  
  }
  
  public void triggerMine()
  {
    
  }
  
  public boolean isMine()
  {
	  return false;
  }
  
  public boolean isFlag()
  {
	  return false;
  }
  
  public void getNoSurroundingMines()
  {
    
  }
  
  
}

					
