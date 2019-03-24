/**
 * PlayingField.java
 * Copyright 2011-2014, Craig A. Damon
 * all rights reserved
 */
package edu.vtc.cis2260.hvz;

import java.awt.Graphics;

/**
 * PlayingField - the field where the simulation takes place.
 * The width and height here define the coordinate system for everything else.
 * The size may be set arbitrarily.
 * @author Craig A. Damon
 *
 */
public class PlayingField
{
	/**
	 * create the playing field
	 * @param game the simulation itself, never null
	 * @param w the width in playing field units, > 0
	 * @param h the height in playing field units, > 0
	 */
  public PlayingField(HvZGame game,int w,int h)
  {
  	  _game = game;
  	  _h = h;
  	  _w = w;
  	  _display = new Display(this);
  }

	/** display this on the current graphics device
	 * @param g the drawing context, never null
	 */
	public void display(Graphics g)
	{
		for (Structure structure : _game.getStructures())
			{
				structure.display(g,this);
			}
		
		for (Player p : _game.getActivePlayers())
			{
				p.display(g,this);
			}
	}

	/** force the field to re-draw itself
	 * 
	 */
	public void redisplay()
	{
		_display.updateScores();
		_display.repaint();
	}
	
	/** note that this game has ended
	 * @param ended true if the game has actually ended, false if it is being started again
	 */
	public void gameOver(boolean ended)
	{
		_display.gameOver(ended);
	}


	
	/** display a structure on the playing field
	 * @param g the drawing context, never null
	 * @param structure the structure to draw, never null
	 * @param doorSide see the constants on Structure like Structure.DOOR_SIDE_RIGHT
	 */
	public void displayStructure(Graphics g,Structure structure,int doorSide)
	{
		_display.displayStructure(g,structure.getX(),structure.getY(),structure.getWidth(),structure.getHeight(), structure.getName(),doorSide);
	}
	
	/** display a player on the playing field
	 * @param g the drawing context never null
	 * @param p the player being displayed, never null
	 */
	public void displayPlayer(Graphics g,Player p)
	{
		_display.displayPlayer(g,p.getX(),p.getY(),p.getDirection(),p.getType(), p.getNumber());
	}


	/**
	 * get the width of the playing field
	 * @return the width in field units, > 0
	 */
	public int getWidth()
	{
		return _w;
	}
	
	/**
	 * get the height of the playing field
	 * @return the height in field units, > 0
	 */
	public int getHeight()
	{
		return _h;
	}
	
	/**
	 * get the simulation itself
	 * @return the simulation, never null
	 */
	public HvZGame getGame()
	{
		return _game;
	}
	
	private final int _w;
	private final int _h;
	private final HvZGame _game;
	private final Display _display;
}
