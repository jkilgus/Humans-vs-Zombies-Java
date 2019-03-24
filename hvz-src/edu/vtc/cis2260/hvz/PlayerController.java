/**
 * PlayerController.java
 * Copyright 2011-2014, Craig A. Damon
 * all rights reserved
 */
package edu.vtc.cis2260.hvz;

import java.util.SortedSet;

/**
 * PlayerController - controls a players actions
 * @author Craig A. Damon
 *
 */
public abstract class PlayerController
{
	/** initialize the controller
	 * @param game the simulation, never null
	 * @param p the player being controlled, never null
	 */
   protected PlayerController(HvZGame game,Player p)
   {
  	   _player = p;
  	   _game = game;
   }
   
   /**
    * get the player being controlled
    * @return the player never null
    */
   public Player getPlayer()
   {
  	   return _player;
   }
   
   /**
    * get the simulation
    * @return the simulation, never null
    */
   public HvZGame getGame()
   {
  	  return _game;
   }
   
 	/** note that the controlled player entered a structure
 	 * @param structure the structure entered, never null
 	 */
 	public abstract void enteredStructure(Structure structure);
  
 	/** note that the controlled player exited a structure
 	 * @param structure the structure that the player was in, never null
 	 */
 	public abstract void leftStructure(Structure structure);
 	
  
 	/** do the action this controller is trying to accomplish for the player
 	 */
 	public abstract void act();
 	
	/** return 'H' or 'Z'
	 * @return H for human or Z for zombie
	 */
	abstract public char getType();
	
	/** create a mission, if any, for this controller
	 * @param random a number from 0 to 1 used to control the selection
	 */
	public void createMission(float random)
	{
		// do nothing
	}


	/**
	 * find the opponents in view
	 * @return the opponents, sorted from closest to farthest
	 */
	abstract SortedSet<Player> findOpponents();
   
   private final Player _player;  // never null
   private final HvZGame _game;  // never null
}
