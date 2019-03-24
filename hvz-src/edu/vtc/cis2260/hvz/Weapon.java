/**
 * Weapon.java
 * Copyright 2011-2014, Craig A. Damon
 * all rights reserved
 */
package edu.vtc.cis2260.hvz;

import java.util.Iterator;

/**
 * Weapon - allow a human to destroy zombies
 * @author Craig A. Damon
 *
 */
public abstract class Weapon
{
	/**
	 * initialize the weapon
	 * @param owner the player who will own this weapon, never null
	 */
	protected Weapon(Player owner)
	{
		_owner = owner;
	}
	
	/**
	 * get the player who owns this weapon
	 * @return the player, never null
	 */
	public Player getPlayer()
	{
		return _owner;
	}
	
	/**
	 * select valid targets
	 *
	 */
	public abstract void chooseTargets();
	
	/**
	 * fire at the selected targets (if available)
	 *
	 */
	public abstract void fire();
	
	/**
	 * does this weapon still have ammo
	 * @return true if there is no ammo left, false if there still is ammo
	 */
	public abstract boolean isEmpty();
	
	/**
	 * reload ammo into the gun
	 *
	 */
	public abstract void reload();
	
	/**
	 * return the number of shots left
	 * @return the number of shots
	 */
	public abstract int getRemainingShots();
	
	/**
	 * get the players that are being targeted
	 * @return an iterator yielding all targets to be shot
	 */
	public abstract Iterator<Player> getCurrentTargets();
	
	
	private final Player _owner;  // never null, always a human
}
