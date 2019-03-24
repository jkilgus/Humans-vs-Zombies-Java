/**
 * ZombieController.java
 * Copyright 2011-2014, Craig A. Damon
 * all rights reserved
 */
package edu.vtc.cis2260.hvz;

import java.util.Random;
import java.util.SortedSet;

/**
 * ZombieController - wander aimlessly until smelling brains, then seek brains
 * @author Craig A. Damon
 *
 */
public class ZombieController extends PlayerController
{

	/**
	 * initialize the controller
	 * @param game the simulation, never null
	 * @param p the zombie being controlled, never null
	 */
	protected ZombieController(HvZGame game, Player p)
		{
			super(game, p);
			_random = new Random();
		}
	

	/**
	 * get the type
	 * @return 'Z'
	 * @see edu.vtc.cis2260.hvz.PlayerController#getType()
	 */
	@Override
	public char getType()
	{
		return 'Z';
	}


	/**
	 * the zombie entered the structure, so exit immediately
	 * @param structure the structure being entered
	 * @see edu.vtc.cis2260.hvz.PlayerController#enteredStructure(edu.vtc.cis2260.hvz.Structure)
	 */
	@Override
	public void enteredStructure(Structure structure)
	{
		// shouldn't happen
		getPlayer().exitStructure(structure);
	}

	/**
	 * the zombie exited the structure it never should have entered
	 * @param structure the structure never null
	 * @see edu.vtc.cis2260.hvz.PlayerController#leftStructure(edu.vtc.cis2260.hvz.Structure)
	 */
	@Override
	public void leftStructure(Structure structure)
	{
		// ignore
	}

	/**
	 * perform zombie activities
	 * @see edu.vtc.cis2260.hvz.PlayerController#act()
	 */
	@Override
	public void act()
	{
		SortedSet<Player> humans = findOpponents();
		if (!humans.isEmpty())
			{
				_timeSinceLastHuman = 10;
				Player human = humans.first();  // closest human is first
				if (getPlayer().distanceTo(human) < 5)  // biting range
					{
						human.zombify();
						return;
					}
				getPlayer().setDirection(getPlayer().angleTo(human));
				getPlayer().move(1);
			}
		if (_timeSinceLastHuman > 0)
			{
				// just wait for the human until you get bored
				_timeSinceLastHuman--;
				return;
			}
		int newDir = getPlayer().getDirection()+_random.nextInt(30)-15; // change direction up to 15 degrees
		if (newDir < 0)
			newDir += 360;
		getPlayer().setDirection(newDir);
		getPlayer().move(1);
	}


	/** 
	 * find all the visible humans
	 * @return the humans in order sorted by closest to farthest
	 * @see edu.vtc.cis2260.hvz.PlayerController#findOpponents()
	 */
	@Override
	public SortedSet<Player> findOpponents()
	{
		return getGame().findHumans(getPlayer(),90,250,360,50);
	}
	
	private int _timeSinceLastHuman; // >= 0, 
	private final Random _random; // never null
}
