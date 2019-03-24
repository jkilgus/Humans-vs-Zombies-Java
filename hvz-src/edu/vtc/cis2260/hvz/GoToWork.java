/**
 * GoToWork.java
 * Copyright 2011-2014, Craig A. Damon
 * all rights reserved
 */
package edu.vtc.cis2260.hvz;

/**
 * GoToWork - the mission to go to work for a while and then return
 * @author Craig A. Damon
 *
 */
public class GoToWork extends RoundTrip
{

	/**
	 * create the mission to go to work and return
	 * @param player the player being sent to work, never null
	 */
	public GoToWork(Player player)
		{
			super(player,"Work",100);
			_money = getPlayer().getCurrentMoney();
			getPlayer().setCurrentMoney(5);
		}
	// The amount of money the player has
	int _money;
}