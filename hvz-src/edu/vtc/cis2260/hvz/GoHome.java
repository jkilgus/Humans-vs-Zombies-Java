/**
 * GoHome.java
 * Copyright 2011-2014, Craig A. Damon
 * all rights reserved
 */
package edu.vtc.cis2260.hvz;

/**
 * GoHome - a human missing to get home
 * @author Craig A. Damon
 *
 */
public class GoHome extends Mission
{
	/**
	 * create the mission
	 * @param player the player trying to get home, never null
	 */
	public GoHome(Player player)
	{
		super(player);
		_target = player.getGame().findStructure("Home");
		_completed = false;
	}

	/**
	 * get the destination
	 * @return the structure being headed to, or null if already there
	 * @see edu.vtc.cis2260.hvz.Mission#getDestination()
	 */
	@Override
	public Structure getDestination()
	{
		if (_completed)
			return null;
		return _target;
	}

	/**
	 * realize that the player arrived at a structure
	 * @param structure the structure arrived at, never null
	 * @see edu.vtc.cis2260.hvz.Mission#arrivedAt(edu.vtc.cis2260.hvz.Structure)
	 */
	@Override
	public void arrivedAt(Structure structure)
	{
    if (_target == structure)
    	{
    		_completed = true;
    	}
    else
    	{
    		// what are we doing here? this isn't where we are heading
    		getPlayer().exitStructure(structure);
    	}
	}

	/**
	 * note that some time has passed while working on this mission
	 * @see edu.vtc.cis2260.hvz.Mission#timePassed()
	 */
	@Override
	public void timePassed()
	{
    // nothing to do here
	}

	/**
	 * has this mission already been completed
	 * @return true if it is done, false if not
	 * @see edu.vtc.cis2260.hvz.Mission#isMissionCompleted()
	 */
	@Override
	public boolean isMissionCompleted()
	{
		return _completed;
	}
	
	private final Structure _target;  // never null, always HOME
	private boolean _completed;

}
