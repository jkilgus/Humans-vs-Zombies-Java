/**
 * Mission.java
 * Copyright 2011, Craig A. Damon
 * all rights reserved
 */
package edu.vtc.cis2260.hvz;

/**
 * Mission - an activity that a human must do
 * @author Craig A. Damon
 *
 */
public abstract class Mission
{
	/** 
	 * initialize this mission
	 * @param player the player being sent on the mission, never null
	 */
	protected Mission(Player player)
	{
		_player = player;
	}
	
	/**
	 * get the player whose mission this is
	 * @return the player never null
	 */
	public Player getPlayer()
	{
		return _player;
	}
	
	/**
	 * get the active game
	 * @return the game never null
	 */
	public HvZGame getGame()
	{
		return _player.getGame();
	}
	
	/**
	 * get the destination where the player is heading now
	 * @return the structure being headed to, null if the player is not currently heading anywhere
	 */
	public abstract Structure getDestination();
	
	/**
	 * let the mission know that we have arrived at a structure 
	 * @param structure the structure arrived at, never null
	 */
	public abstract void arrivedAt(Structure structure);
	
	/**
	 * let the mission know that another unit of time has passed
	 *
	 */
	public abstract void timePassed();
	
	/**
	 * has the mission completed
	 * @return true if this mission is completed
	 */
	public abstract boolean isMissionCompleted();
	
	private final Player _player;  // never null
}
