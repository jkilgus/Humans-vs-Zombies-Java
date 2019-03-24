/**
 * HvZGame.java
 * Copyright 2011-2014, Craig A. Damon
 * all rights reserved
 */
package edu.vtc.cis2260.hvz;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * HvZGame - the basic simulation
 * @author Craig A. Damon
 *
 */
public class HvZGame
{
	/**
	 * run the simulation
	 * @param args ignored for now
	 */
	public static void main(String[] args)
	{
		HvZGame game = new HvZGame(500,500,2,20);
		game.run();
	}
	
	/**
	 * create the game
	 * @param w  width of the playing field, > 0
	 * @param h height of the playing field, > 0
	 * @param numZ number of zombies
	 * @param numH number of initial humans
	 */
	public HvZGame(int w,int h,int numZ,int numH)
	{
		_field = new PlayingField(this,w,h);
		_structures = new HashMap<>();
		_activePlayers = new HashSet<>();
		_inactivePlayers = new HashSet<>();
		_humans = new HashSet<>();
		_zombies = new HashSet<>();
		_random = new Random();
		_numH = numH;
		_numZ = numZ;
		
		// create default set up
		createSetup(numZ, numH);
	}
	
	/**
	 * reset the game back to its initial conditions
	 *
	 */
	public void resetGame()
	{
		_activePlayers.clear();
		_inactivePlayers.clear();
		_humans.clear();
		_zombies.clear();
		createSetup(_numZ,_numH);
	}
	
	/** create all the parts of the game
	 * @param w the width of the field
	 * @param h the height of the field
	 * @param numZ the number of initial zombies
	 * @param numH the number of initial humans
	 */
	private void createSetup(int numZ, int numH)
	{
		int w = _field.getWidth();
		int h = _field.getHeight();
		
		addStructure(new Structure(20, 20, 100, 40, "Home", this,4));
		addStructure(new Structure(w-100-20, 20, 100, 40, "Store", this,3));
		addStructure(new Structure((w-100)/2, h-40-20, 100, 40, "Work", this,2));

		// add the humans
		for (int i = 0; i < numH; i++)
			new Player(true,this);
	
		// and the zombies
		for (int i = 0; i < numZ; i++)
			new Player(false,this);
	}
	
	/**
	 * actually run the game
	 *
	 */
	public void run()
	{
		_field.gameOver(false);
		Thread thread = new Thread(new RunGame(this));
		thread.start();
	}
	
	/**
	 * run 1 time step of the simulation
	 *
	 */
	private void run1Step()
	{
		for (Player p : new ArrayList<>(_inactivePlayers))
			{
				p.createMission(_random.nextFloat());
			}
		
		for (Player p : new ArrayList<>(_activePlayers))
			{
				p.act();
			}
	  _field.redisplay();
	}
	
	
	/**
	 * add a structure to the simulation
	 * @param structure the structure to add to the simulation
	 */
  void addStructure(Structure structure)
	{
		_structures.put(structure.getName(),structure);
	}
	
	/**
	 * get the playing field
	 * @return the field
	 */
   public PlayingField getField()
   {
  	   return _field;
   }
   
 	/** get all the structures in the game
 	 * @return the structures as an iterable that can be used in a foreach loop with no predictable order
 	 */
 	public Iterable<Structure> getStructures()
 	{
 		return _structures.values();
 	}
 	
	/** get the indicated structure
	 * @param name the name of the structure, never null
	 * @return the structure or null if not found
	 */
	public Structure findStructure(String name)
	{
		return _structures.get(name);
	}
 	
	/** get the indicated structure
	 * @param name the name of the structure, never null
	 * @return the structure or null if not found
	 */
	public boolean hasStructure(String name)
	{
		return _structures.containsKey(name);
	}

  
 	/** get all the active players in the game
 	 * @return an iterable that can be used in a foreach loop with no predictable order
 	 */
 	public Iterable<Player> getActivePlayers()
 	{
 		return _activePlayers;
 	}
  
 	/** get all the inactive players (inside structure) in the game, should be all humans
 	 * @return an iterable that be used in a foreach loop with no predictable order
 	 */
 	public Iterable<Player> getInactivePlayers()
 	{
 		return _inactivePlayers;
 	}

   

 	/** choose a random structure
 	 * @param chanceOutside the chance that no structure is chosen, from 0 (always outside) to 1 (always inside)
 	 * @return the structure or null if outside chosen or there are no structures
 	 */
 	public Structure chooseRandomStructure(double chanceOutside)
 	{
 		if (_random.nextDouble() < chanceOutside)
 			return null;
 		int i = _random.nextInt(_structures.size());
 		for (Structure structure : _structures.values())
 			{
 				if (i-- == 0)
 					return structure;
 			}
 		return null;  // no structures to return
 	}

   

 	/** generate a random starting point for a player
 	 * @param player the player being placed, never null
 	 */
 	public void randomlyPlace(Player player)
 	{
 		int x;
 		int y;
 		do {
 		  x = _random.nextInt(_field.getWidth());
 		  y = _random.nextInt(_field.getHeight());
 		} 
 		while (insideStructure(x,y));
 		player.setPosition(x,y);
 		player.setDirection(_random.nextInt(360));
 	}
 	
 	/**
 	 * test whether a position is inside any structure
 	 * @param x the x coordinate in field units
 	 * @param y the y coordinate in field units
 	 * @return true if (x,y) is inside some structure
 	 */
 	public boolean insideStructure(int x,int y)
 	{
 		for (Structure s : _structures.values())
 			{
 				if (x < s.getX() || x >= s.getX()+s.getWidth())
 					continue;
 				if (y < s.getY() || y >= s.getY()+s.getHeight())
 					continue;
        return true;
 			}
 		return false;
 	}
 	
 	/**
 	 * is the game over
 	 * @return true if the game has ended, false if it should continue
 	 */
 	public boolean checkGameEnd()
 	{
 		if (numZombies() == 0)
 			{
 				System.out.println("Humans win");
 				_field.gameOver(true);
 				return true;
 			}
 		if (numHumans() == 0)
 			{
 				System.out.println("Zombies win");
 				_field.gameOver(true);
 				return true;
 			}
 		return false;
 	}
 	
 	/**
 	 * the current number of zombies in the simulation
 	 * @return the number of zombies >= 0
 	 */
 	public int numZombies()
 	{
 		return _zombies.size();
 	}
 	
 	/**
 	 * get the number of remaining humans
 	 * @return the number of humans, >= 0
 	 */
 	public int numHumans()
 	{
 		return _humans.size();
 	}
	
 	/**
 	 * add a player as a zombie
 	 * @param p the player, never null, should have a zomvie controller
 	 */
  void addZombie(Player p)
 	{
 		_zombies.add(p);
 	}
 	
  /**
   * kill a zombie
   * @param p the player who was a zombie, never null
   */
 	public void killZombie(Player p)
 	{
 		System.out.println(p + " was killed by: ");
 		_zombies.remove(p); 
 		_activePlayers.remove(p);
 	}
 	
 	/**
 	 * add a human to the simulation
 	 * @param p the human, never null, always p.isHuman()
 	 */
  void addHuman(Player p)
 	{
 		_humans.add(p);
 	}
 	
  /**
   * note that a human has been zombified
   * @param p the previous human who has been zombified, never null
   */
  void zombifyHuman(Player p)
 	{
 		_humans.remove(p);
 		_zombies.add(p);
 	}
  
   /**
    * with advanced crispr gene editing techniques
    * zombies can now be cured!
    * @param z
    */
   void humafyZombie(Player z)
   {
	   _zombies.remove(z);
	   _humans.add(z);
   }

 	/**
 	 * note that a player is now active
 	 * @param player never null
 	 */
 	public void activate(Player player)
 	{
 		_inactivePlayers.remove(player);
 		_activePlayers.add(player);
 	}
 	
 	/**
 	 * note that a player is no longer active (now in a structure)
 	 * @param player never null
 	 */
 	public void deactivate(Player player)
 	{
 		_activePlayers.remove(player);
 		_inactivePlayers.add(player);
 	}
 	
 	/**
 	 * find all the zombies visible to the indicated player
 	 * @param player the player looking, never null
 	 * @param visionAngle the angle (in degrees) of their primary vision, a value of 180 sees everything in front or directly to either side
 	 * @param visionDistance the max distance where they can distinguish zombies
 	 * @param periphAngle the angle (in degrees) of the peripheral vision, a value of 360 sees everywhere
 	 * @param periphDistance the max distance where their peripheral can distinguish zombies
 	 * @return a collection yielding the zombies in order of closest to farthest
 	 */
 	public SortedSet<Player> findZombies(Player player,int visionAngle,int visionDistance,int periphAngle,int periphDistance)
 	{
 		SortedSet<Player> zombies = new TreeSet<>(new Player.DistComp(player));
 		if (player.currentlyInside() != null)
 			return zombies;
 		for (Player z : _zombies)
 			{
 				int angle = player.angleTo(z);
 				int distanceTo = player.distanceTo(z);
 				int relAngle = player.getDirection()-angle;
 				if (relAngle < 0)
 					relAngle = -relAngle;
 				if (relAngle*2 < visionAngle && distanceTo < visionDistance)
 					zombies.add(z);
 				else if (relAngle*2 < periphAngle && distanceTo < periphDistance)
 					zombies.add(z);
 			}
		return zombies;
 	}
 	
 	/**
 	 * find all the humans visible to the indicated player
 	 * @param player the player viewing the others, never null
 	 * @param visionAngle the angle (in degrees) of their primary vision, a value of 180 sees everything in front or directly to either side
 	 * @param visionDistance the max distance where they can distinguish humans
 	 * @param periphAngle the angle (in degrees) of the peripheral vision, a value of 360 sees everywhere
 	 * @param periphDistance the max distance where their peripheral can distinguish humans
 	 * @return a collection yielding the humans in no particular order
 	 */
 	public SortedSet<Player> findHumans(Player player,int visionAngle,int visionDistance,int periphAngle,int periphDistance)
 	{
 		SortedSet<Player> humans = new TreeSet<>(new Player.DistComp(player));
 		if (player.currentlyInside() != null)
 			return humans;
 		for (Player h : _humans)
 			{
 				if (h.currentlyInside() != null)
 					continue;
 				int angle = player.angleTo(h);
 				int distanceTo = player.distanceTo(h);
 				int relAngle = player.getDirection()-angle;
 				if (relAngle > 180)
 					relAngle -= 360;
 				if (relAngle < -180)
 					relAngle += 360;
 				if (relAngle < 0)
 					relAngle = -relAngle;
 				if (relAngle*2 < visionAngle && distanceTo < visionDistance)
 					humans.add(h);
 				else if (relAngle*2 < periphAngle && distanceTo < periphDistance)
 					humans.add(h);
 			}
		return humans;
 	}

   
  private final PlayingField _field;  // never null
  private final Map<String,Structure> _structures;  // never null, contains all structure
 	private final Collection<Player> _activePlayers;   // never null, all active players are updated each cycle,
	private final Collection<Player> _inactivePlayers; // never null, any players are either in active or inactive
	private final Collection<Player> _zombies;  // never null, contains all zombies in game,  _zombies subset of _active
	private final Collection<Player> _humans;   // never null, contains all humans in game  _zombies U _humans = _active U _inactive
	private int _numZ;   // the number of zombies initially created
	private int _numH;   // the number of humans initially created
	private final Random _random;  // always active
	
	/**
	 * RunGame - the simulation runtime controller, running in its own thread
	 * @author Craig A. Damon
	 *
	 */
	private static class RunGame implements Runnable
	{
		/**
		 * initialize the thread
		 * @param game never null
		 */
    public RunGame(HvZGame game)
    {
    	  _game = game;
    }
    
		/**
		 * actually run the simulation
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run()
		{
			while (true)
				{
					_game.run1Step();
					if (_game.checkGameEnd())
						break;
					try
						{
							Thread.sleep(100);  // make it show up briefly before doing more
						}
					catch (InterruptedException e)
						{
							break;
						}
				}
			
		}
		
		private final HvZGame _game;
	}
}
