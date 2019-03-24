/**
 * Structure.java
 * Copyright 2011-2014, Craig A. Damon
 * all rights reserved
 */
package edu.vtc.cis2260.hvz;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Structure - a building (safety) in the zombie simulation
 * @author Craig A. Damon
 *
 */
public class Structure extends GameElement
{
	/** indicate that the door should be on the right hand side (as displayed) of the structure */
  public static final int DOOR_ON_RIGHT = 1;
	/** indicate that the door should be on the top (as displayed) of the structure */
  public static final int DOOR_ON_TOP = 2;
	/** indicate that the door should be on the left hand side (as displayed) of the structure */
  public static final int DOOR_ON_LEFT = 3;
	/** indicate that the door should be on the bottom (as displayed) of the structure */
  public static final int DOOR_ON_BOTTOM = 4;
  
	/**
	 * create the structure
	 * @param x the left hand side of the structure in playing field units, >= 0
	 * @param y the top of the structure in playing field units, >= 0
	 * @param w the width of the unit in playing field units, > 0
	 * @param h the width of the unit in playing field units, > 0
	 * @param name the name of the structure, never null
	 * @param game the entire game simulation, never null
	 * @param doorSide one of DOOR_SIDE_TOP,DOOR_SIDE_BOTTOM,DOOR_SIDE_RIGHT,DOOR_SIDE_BOTTOM
	 */
	protected Structure(int x, int y, int w, int h, String name, HvZGame game,int doorSide)
		{
			super(x, y, game);
			_height = h;
			_width = w;
			_name = name;
			_inhabitants = new ArrayList<>();
			_doorSide = doorSide;
		}
	
	/**
	 * display this structure
	 * @param g never null
	 * @param field never null
	 * @see edu.vtc.cis2260.hvz.GameElement#display(Graphics, edu.vtc.cis2260.hvz.PlayingField)
	 */
	@Override
	public void display(Graphics g, PlayingField field)
	{
		field.displayStructure(g,this,_doorSide);
	}

	/** note that someone is now in this structure
	 * @param player never null
	 */
	public void addInhabitant(Player player)
	{
     _inhabitants.add(player);		
	}

	/** note that someone is no longer in this structure
	 * @param player never null
	 */
	public void removeInhabitant(Player player)
	{
     _inhabitants.remove(player);		
	}
	
	/**
	 * get the x coordinate of the door
	 * @return the coordinate, >= 0, < field.getWidth()
	 */
	public int getDoorX()
	{
		switch (_doorSide)
		{
			case DOOR_ON_RIGHT:
				return getX()+getWidth()+5;
			case DOOR_ON_TOP:
			case DOOR_ON_BOTTOM:
				return getX()+getWidth()/2-15;
			case DOOR_ON_LEFT:
				return getX()-20;
			default:
				System.out.println("Invalid doorside "+_doorSide+" on "+this);
				break;
		}
		return 0;
	}
	
	/**
	 * get the y coordinate of the door into/out of this structure
	 * @return the coordinate in playing field units, >= 0, < field.getHeight
	 */
	public int getDoorY()
	{
		switch (_doorSide)
		{
			case DOOR_ON_TOP:
				return getY()-15;
			case DOOR_ON_RIGHT:
			case DOOR_ON_LEFT:
				return getY()+getHeight()/2-5;
			case DOOR_ON_BOTTOM:
				return getY()+getHeight()+5;
			default:
				System.out.println("Invalid doorside "+_doorSide+" on "+this);
				break;
		}
		return 0;
	}

	
	/**
	 * @return the width in playing field units, > 0
	 */
	public int getWidth()
	{
		return _width;
	}
	
	/**
	 * @return the height in playing field units, > 0
	 */
	public int getHeight()
	{
		return _height;
	}
	
	/**
	 * get the right side of the structure
	 * @return the coordinate of the right side in playing field units, >= 0, < field.getWidth()
	 */
	public int getRight()
	{
		return getX()+_width;
	}
	
	/**
	 * get the bottom of the structure
	 * @return the coordinate of the bottom in playing field units, >= 0, < field.getWidth()
	 */
	public int getBottom()
	{
		return getY()+_height;
	}

	
	/**
	 * @return the name never null
	 */
	public String getName()
	{
		return _name;
	}
	
	/**
	 * 
	 * @return the name of the structure
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return _name;
	}

	
	/** compute the angle to the door from the player
	 * @param player never null, not inside this structure, computer the angle around the corner if necessary
	 * @return the angle in degrees
	 */
	public int angleFrom(Player player)
	{
		int targetX = getDoorX();
		int targetY = getDoorY();
		switch (_doorSide)
		{
			case DOOR_ON_RIGHT:
				if (player.getX() < getRight())
					{
						if (player.getY() <= getY())
							{  // go for top corner
								targetX = getRight();
								targetY = getY();
								break;
							}
						else if (player.getY() >= getBottom())
							{
								targetX = getRight();
								targetY = getBottom();
								break;
							}
						else if (player.getY() >= getY()+getHeight()/2)
							{
								targetX = getX();
								targetY = getBottom();
								break;
							}
						else
							{
								targetX = getX();
								targetY = getY();
								break;
							}
					}
				break;
			case DOOR_ON_TOP:
				if (player.getY() > getY())
					{
						if (player.getX() <= getX())
							{  // go for left corner
								targetX = getX();
								targetY = getY();
								break;
							}
						else if (player.getX() >= getRight())
							{
								targetX = getRight();
								targetY = getY();
								break;
							}
						else if (player.getX() >= getX()+getWidth()/2)
							{
								targetX = getRight();
								targetY = getBottom();
								break;
							}
						else
							{
								targetX = getX();
								targetY = getBottom();
								break;
							}
					}
				break;
			case DOOR_ON_LEFT:
				if (player.getX() > getX())
					{
						if (player.getY() <= getY())
							{  // go for top corner
								targetX = getX();
								targetY = getY();
								break;
							}
						else if (player.getY() >= getBottom())
							{
								targetX = getX();
								targetY = getBottom();
								break;
							}
						else if (player.getY() >= getY()+getHeight()/2)
							{
								targetX = getRight();
								targetY = getBottom();
								break;
							}
						else
							{
								targetX = getRight();
								targetY = getY();
								break;
							}
					}
				break;
			case DOOR_ON_BOTTOM:  // door is on bottom
				if (player.getY() < getBottom())
					{
						if (player.getX() <= getX())
							{  // go for left corner
								targetX = getX();
								targetY = getBottom();
								break;
							}
						else if (player.getX() >= getRight())
							{
								targetX = getRight();
								targetY = getBottom();
								break;
							}
						else if (player.getX() >= getX()+getWidth()/2)
							{
								targetX = getRight();
								targetY = getY();
								break;
							}
						else
							{
								targetX = getX();
								targetY = getY();
								break;
							}
					}
				break;
			default:
				System.out.println("Invalid doorside "+_doorSide+" on "+this);
				break;
		}
		int xdelt = targetX-player.getX();
		int ydelt = player.getY() - targetY;  // backwards because of screwy display graphics coord system
		if (xdelt == 0)
			{
				if (ydelt >= 0)
					return 90;
				return 270;
			}
		if (ydelt == 0)
			{
				if (xdelt >= 0)
					return 0;
				return 180;
			}
		double tan = ((double)ydelt)/xdelt;
		double radians = Math.atan(tan);
		if (radians < 0)
			radians += 2*Math.PI;
		double degrees = radians*180/Math.PI;
		if (xdelt < 0)
			{
				if (ydelt > 0)
				  degrees -= 180;
				else
					degrees += 180;
			}
		return (int)(degrees+0.5);
	}

	private final int _width;  // > 0
	private final int _height;  // > 0
	private final String _name; // never null, never empty
	private final Collection<Player> _inhabitants; // never null, may be empty, contains no nulls
	private final int _doorSide;  // 1 is right, 2 is top, 3 is left, 4 is bottom
}
