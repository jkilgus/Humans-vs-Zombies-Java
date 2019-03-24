/**
 * Player.java
 * Copyright 2011-2014, Craig A. Damon
 * all rights reserved
 */
package edu.vtc.cis2260.hvz;

import java.awt.Graphics;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Player - a participant in the simulation, could be human or zombie
 * starts the game with 0 to 10 _money
 * starts the game with 10 _health
 * @author Craig A. Damon
 *
 */
public class Player extends GameElement
{

	/**
	 * DistComp - the distance comparator to sort players on proximity to a given player
	 * @author Craig A. Damon
	 *
	 */
	public static class DistComp implements Comparator<Player>
	{  
		/**
		 * create the comparison based on distance from view
		 * @param viewer the player determining the distances
		 */
		public DistComp(Player viewer)
		{
			_viewer = viewer;
		}

		/**
		 * @param p1 one player, never null
		 * @param p2 the other player, never null
		 * @return an indicator of which player is closer to the initial (viewer) player
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(Player p1, Player p2)
		{
			if (p1 == p2)
				return 0;
			int dist1 = p1.distanceTo(_viewer);
			int dist2 = p2.distanceTo(_viewer);
			if (dist1 < dist2)
				return -1;
			if (dist1 > dist2)
				return 1;
			if (p1.getNumber() < p2.getNumber())
				return -1;
			return 1;
		}

		private final Player _viewer;
	}
	
	/**
	 * @param isHuman true if this player is human, false if zombie
	 * @param game the simulation, never null
	 */
	public Player(boolean isHuman, HvZGame game)
		{
			super(0,0,game);
			// Start the game with 0 to 10 dollars
			_money = _randomInt;
			// Start the game with 10 hit points
			_health = 10;
			_number = _nextNumber++;
			if (isHuman)
				{
					_controller = new HumanController(game,this);
					Structure structure = game.chooseRandomStructure(0);
					if (structure != null)
					  {
					  	  enterStructure(structure);
					  	  game.addHuman(this);
					  	  game.deactivate(this);
					  }
					else
						{
							game.randomlyPlace(this);
				  	    game.addHuman(this);
				  	    game.activate(this);
						}
				}
			else
				{
					_controller = new ZombieController(game,this);
					game.randomlyPlace(this);
		  	    game.addZombie(this);
		  	    game.activate(this);
				}
		}

	/** note that a player has entered a structure (and disappeared from the field)
	 * @param structure never null
	 */
	public void enterStructure(Structure structure)
	{
		getGame().deactivate(this);
		_currentStructure = structure;
		structure.addInhabitant(this);
		_controller.enteredStructure(structure);
		clearPosition();
	}


	/** exit the structure (and reappear in the simulation)
	 * @param structure never null, must be in this structure
	 */
	public void exitStructure(Structure structure)
	{
		getGame().activate(this);
		_currentStructure = null;
		structure.removeInhabitant(this);
		_controller.leftStructure(structure);
		setPosition(structure.getDoorX(), structure.getDoorY());
	}

	/**
	 * draw the player on the display
	 * @param g the drawing context, never null
	 * @param field the field where this is being displayed
	 * @see edu.vtc.cis2260.hvz.GameElement#display(Graphics, edu.vtc.cis2260.hvz.PlayingField)
	 */
	@Override
	public void display(Graphics g, PlayingField field)
	{
		field.displayPlayer(g,this);
	}
	

	/** get the unique number describing this instance
	 * @return the number
	 */
	public int getNumber()
	{
		return _number;
	}
	
	/** return the direction in degrees
	 * @return the direction this player is facing
	 */
	public int getDirection()
	{
		return _direction;
	}
	
	/**
	 * set the direction this player is facing
	 * @param dir the direction in degrees
	 */
	public void setDirection(int dir)
	{
		_direction = dir;
	}

	/** get the type of player, either 'H' or 'Z'
	 * @return the type
	 */
	public char getType()
	{
		return _controller.getType();
	}
	
	/**
	 * you've been bit
	 *
	 */
	public void zombify()
	{
		System.out.println(this+" became a zombie");
		_controller = new ZombieController(getGame(), this);
		getGame().zombifyHuman(this);
	}
	
	/**
	 * you've been saved
	 */
	public void humafy()
	{
		System.out.println(this+" became a human");
		_controller = new HumanController(getGame(), this);
		getGame().humafyZombie(this);
	}
	
	/** move a distance in the current direction
	 * @param dist
	 */
	public void move(int dist)
	{
		double radians = _direction*Math.PI/180;
		double cos = Math.cos(radians);
		int x;
		if (cos < 0)
			x = getX() + (int)(cos*dist-0.5);
		else
		  x = getX() + (int)(cos*dist+0.5);
		double sin = Math.sin(radians);
		int y;
		if (sin < 0)
			y = getY() - (int)(sin*dist-0.5);
		else
			y = getY() - (int)(sin*dist+0.5);		
		
		if (x < 0)
			x = 0;
		else if (x >= getGame().getField().getWidth())
			x = getGame().getField().getWidth();
		if (y < 0)
			y = 0;
		else if (y >= getGame().getField().getHeight())
			y = getGame().getField().getHeight();
		if (getGame().insideStructure(x, y))
			return;  // don't move if it would go into the building
//		int oldX = getX();
//		int oldY = getY();
		setPosition(x,y);

		//System.out.println(this+" moved "+dist+" in "+_direction+" from "+oldX+","+oldY+" to "+getX()+","+getY());
	}

	
	/**
	 * return the angle to another player, 0 == due left, 90 == straight above, etc.
	 * @param player the other player, never null
	 * @return the angle in degrees or -1 if meaningless (one or the other is inside)
	 */
	public int angleTo(Player player)
	{
		if (getX() < 0 || getY() < 0 || player.getX() < 0 || player.getY() < 0)
			return -1;
		int xdelt = player.getX()-getX();
		int ydelt = getY()-player.getY();
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

	/**
	 * the distance to another player
	 * @param player the other player, never null
	 * @return the distance, rounded, or -1 if not meaningful
	 */
	public int distanceTo(Player player)
	{
		if (getX() < 0 || getY() < 0 || player.getX() < 0 || player.getY() < 0)
			return -1;
		int xdelt = getX()-player.getX();
		int ydelt = getY()-player.getY();
		return (int)(Math.sqrt(xdelt*xdelt+ydelt*ydelt)+0.5);
	}

	/**
	 * return the angle to the door of a structure, 0 == due left, 90 == straight above, etc.
	 * @param structure the structure being targeted, never null
	 * @return the angle in degrees or -1 if meaningless (one or the other is inside)
	 */
	public int angleTo(Structure structure)
	{
		if (getX() < 0 || getY() < 0)
			return -1;
		return structure.angleFrom(this);
	}

	/**
	 * the distance to a structure
	 * @param structure the structure being targeted, never null
	 * @return the distance, rounded, or -1 if not meaningful
	 */
	public int distanceTo(Structure structure)
	{
		if (getX() < 0 || getY() < 0 )
			return -1;
		int xdelt = getX()-structure.getDoorX();
		int ydelt = getY()-structure.getDoorY();
		return (int)(Math.sqrt(xdelt*xdelt+ydelt*ydelt)+0.5);
	}
	
	/**
	 * the display string
	 * @return a string starting with H (human) or Z (zombie)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return String.valueOf(getType())+_number;
	}

	/** do whatever you should be doing
	 * 
	 */
	public void act()
	{
		_controller.act();
	}


	/** create a new mission for this player
	 * @param random from 0 to 1
	 */
	public void createMission(float random)
	{
		_controller.createMission(random);
	}

 	
 	/**
 	 * what structure is this player inside, if any
 	 * @return the structure or null if the player is not in a structure
 	 */
 	public Structure currentlyInside()
 	{
 		return _currentStructure;
 	}

	/** find all players that are near of the opposite type
	 * @return the set in order of proximity never null, contains no nulls
	 */
	public SortedSet<Player> findOpponents()
	{
		return _controller.findOpponents();
	}
	
	// Getters and setters for money and health
	public int getCurrentMoney() {
		return _money;
	}
	public void setCurrentMoney(int m) {
		_money += m;
	}
	public int getCurrentHealth() {
		return _health;
	}
	public void setCurrentHealth(int h) {
		_health += h;
	}

	private Structure _currentStructure;     // null if not inside a building
	private PlayerController _controller;    // never null, describes what kind of player this is
	private final int _number;    // unique number from 1+
	private int _direction;   // from 0 .. 359, in  degrees, 0 is straight right, 90 straight up
	static private int _nextNumber = 1;
	private int _money; // starts randomly from 0 to 10 for buying guns. will increase with work
	private int _health; // starts at 10 hit points which serve as zombie health for being shot at
	private int _randomInt = ThreadLocalRandom.current().nextInt(0, 10 + 1); // random int from 0 to 10 for starting money

}
