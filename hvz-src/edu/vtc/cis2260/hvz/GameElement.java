/**
 * GameElement.java
 * Copyright 2011-2014, Craig A. Damon
 * all rights reserved
 */
package edu.vtc.cis2260.hvz;

import java.awt.Graphics;

/**
 * GameElement - an element that appears on the board
 * @author Craig A. Damon
 *
 */
public abstract class GameElement
{
	/**
	 * initialize this as a game element
	 * @param x the x coordinate in field units, >= 0, < field.getWidth()
	 * @param y the y coordinate in field units, >= 0, < field.getHeight()
	 * @param game never null
	 */
	protected GameElement(int x,int y,HvZGame game)
	{
		_x = x;
		_y = y;
		_game = game;
	}
	
	/**
	 * initialize an element not actually on the field yet
	 * @param game never null
	 */
	protected GameElement(HvZGame game)
	{
		_x = -1;
		_y = -1;
		_game = game;
	}

	/**
	 * display yourself
	 * @param g the rendering environment, never null
	 * @param field never null
	 */
	public abstract void display(Graphics g, PlayingField field);
	
	/**
	 * move the element (or add it to the game display)
	 * @param x the new x position in field units, >= 0, < field.getWidth()
	 * @param y the new y position in field units, >= 0, < field.getHeight()
	 */
  public void setPosition(int x,int y)
	{
		_x = x;
		_y = y;
	}
  
  /**
   * remove an element from the active display
   *
   */
  public void clearPosition()
	{
		_x = -1;
		_y = -1;
	}

	/**
	 * get the x position
	 * @return the x in field units == -1 if not currently displayed, >= 0, < field.getWidth() otherwise
	 */
   public int getX()
   {
  	   return _x;
   }
   
   /**
    * get the y position
	 * @return the y in field units == -1 if not currently displayed, >= 0, < field.getHeight() otherwise
    */
   public int getY()
   {
  	   return _y;
   }
   
   /**
    * get the game
    * @return the game never null
    */
   public HvZGame getGame()
   {
  	   return _game;
   }
   
   
   private int _x;   // either between 0 and _game.getWidth()-1 or -1 for not placed on field
   private int _y;   // either between 0 and _game.getHeight()-1 or -1 for not placed on field
   private final HvZGame _game;  // never null
}
