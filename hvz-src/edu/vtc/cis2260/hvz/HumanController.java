/**
 * HumanController.java
 * Copyright 2011-2014, Craig A. Damon
 * all rights reserved
 */
package edu.vtc.cis2260.hvz;

import java.util.SortedSet;

/**
 * HumanController - controls a human actions. Driven primarily by missions, otherwise stays in same structure.
 * @author Craig A. Damon
 *
 */
public class HumanController extends PlayerController
{

	/**
	 * initialize the controller for a human
	 * @param game the simulation, never null
	 * @param p the player being controlled, never null
	 */
	public HumanController(HvZGame game,Player p)
		{
			super(game,p);
			_seenZombies = false;
		}

	/**
	 * get the type of the player being controlled
	 * @return 'H'
	 * @see edu.vtc.cis2260.hvz.PlayerController#getType()
	 */
	@Override
	public char getType()
	{
		return 'H';
	}
	
	/**
	 * get the weapon this person has, if any
	 * @return the currentWeapon may be null
	 */
	public Weapon getCurrentWeapon()
	{
		return _currentWeapon;
	}

	/**
	 * @param currentWeapon the currentWeapon to set
	 */
	public void setCurrentWeapon(Weapon currentWeapon)
	{
		_currentWeapon = currentWeapon;
	}


	/**
	 * get the mission this person is currently on, if any
	 * @return the currentMission maybe null
	 */
	public Mission getCurrentMission()
	{
		return _currentMission;
	}

	/**
	 * set the mission the player is on
	 * @param newMission the currentMission to set
	 */
	public void setCurrentMission(Mission newMission)
	{
		_currentMission = newMission;
		System.out.println(getPlayer()+" is going to: " + _currentMission.getDestination());
		if (getPlayer().currentlyInside() != null)
			getPlayer().exitStructure(getPlayer().currentlyInside());
	}

	/**
	 * note that the player has entered a structure
	 * @param structure being entered, never null
	 * @see edu.vtc.cis2260.hvz.PlayerController#enteredStructure(edu.vtc.cis2260.hvz.Structure)
	 */
	@Override
	public void enteredStructure(Structure structure)
	{
		if (_currentMission != null)
			_currentMission.arrivedAt(structure);
		if (_currentWeapon != null)
			_currentWeapon.reload();
	}

	/**
	 * note that the person left the structure
	 * @param structure being exited, never null
	 * @see edu.vtc.cis2260.hvz.PlayerController#leftStructure(edu.vtc.cis2260.hvz.Structure)
	 */
	@Override
	public void leftStructure(Structure structure)
	{
		//
	}

	/**
	 * perform the action that this human should be doing
	 * @see edu.vtc.cis2260.hvz.PlayerController#act()
	 */
	@Override
	public void act()
	{
		SortedSet<Player> zombies = findOpponents();
		if (!zombies.isEmpty())
			{
				_seenZombies = true;
				if (_currentWeapon != null && !_currentWeapon.isEmpty())
					{
						_currentWeapon.chooseTargets();
						if (_currentWeapon.getCurrentTargets().hasNext())
							{
								_currentWeapon.fire();
								return;
							}
					}
			}
		if (_currentMission != null)
			{
				Structure target = _currentMission.getDestination();
				if (target != null)
					{
						if (getPlayer().getX() == target.getDoorX() && getPlayer().getY() == target.getDoorY())
							{
								getPlayer().enterStructure(target);
								if (_currentMission.isMissionCompleted())
									{
										_currentMission = null;
									}
								return;
							}
						getPlayer().setDirection(getPlayer().angleTo(target));
						if (getPlayer().distanceTo(target) < 2)
							getPlayer().move(1);
						else
					    getPlayer().move(2);
					}
			}
	}

	/**
	 * find zombies
	 * @return the set of zombies observable from this human
	 * @see edu.vtc.cis2260.hvz.PlayerController#findOpponents()
	 */
	@Override
	public SortedSet<Player> findOpponents()
	{
		return getGame().findZombies(getPlayer(),120,300,240,50);
	}
	
	
	/**
	 * possibly create a random mission
	 * @param random a number from 0 to 1
	 * @see edu.vtc.cis2260.hvz.PlayerController#createMission(float)
	 */
	@Override
	public void createMission(float random)
	{
		if (_seenZombies && getCurrentWeapon() == null)
			{  // need protection more than anything
				setCurrentMission(new BuyWeapon(this));
				return;
			}
		if (_currentMission != null)
			{
				_currentMission.timePassed();
				return;
			}
		if (random > 0.05f)
			return;
		Mission mission;
		if (random < 0.02f && !getPlayer().currentlyInside().getName().equals("Store"))
			{
				mission = new GoToStore(getPlayer());
			}
		else if (getPlayer().currentlyInside().getName().equals("Home"))
			{
				mission = new GoToWork(getPlayer());
			}
		else
			{
				mission = new GoHome(getPlayer());
			}
		setCurrentMission(mission);
	}
	
	private Weapon _currentWeapon;    // may be null
	private Mission _currentMission;   // may be null
	private boolean _seenZombies;
}
