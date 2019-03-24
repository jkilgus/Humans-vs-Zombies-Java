/**
 * BuyWeapon.java
 * Copyright 2011-2014, Craig A. Damon
 * all rights reserved
 */
package edu.vtc.cis2260.hvz;

/**
 * BuyWeapon - a mission to go buy a gun (at the store)
 * @author Craig A. Damon
 *
 */
public class BuyWeapon extends GoToStore
{

	/**
	 * create the mission to buy the weapon
	 * @param human the controller for the person buying the weapon, never null
	 */
	public BuyWeapon(HumanController human)
		{
			super(human.getPlayer());
			_human = human;
		}


	/**
	 * note that the person on the mission arrived at a structure
	 * After arrival, buys best weapon they can afford
	 * @param structure never null
	 * @see edu.vtc.cis2260.hvz.Mission#arrivedAt(edu.vtc.cis2260.hvz.Structure)
	 */
	@Override
	public void arrivedAt(Structure structure)
	{
		_money = getPlayer().getCurrentMoney();
		
		if (structure.getName().equals("Store")) {
			if (_money > 9) {
				_human.setCurrentWeapon(new Rifle(_human.getPlayer()));
				getPlayer().setCurrentMoney(-10);
				System.out.println(getPlayer().toString() + " bought a Rifle");
			} else if (_money > 6) {
				_human.setCurrentWeapon(new Glock(_human.getPlayer()));
				getPlayer().setCurrentMoney(-7);
				System.out.println(getPlayer().toString() + " bought a Glock.");
			} else if (_money > 2) {
				_human.setCurrentWeapon(new SixShooter(_human.getPlayer()));
				getPlayer().setCurrentMoney(-3);
				System.out.println(getPlayer().toString() + " bought a Six-Shooter.");
			} else {
				_human.setCurrentWeapon(new BareKnuckles(_human.getPlayer()));
				System.out.println(getPlayer().toString() + " can only afford bare fists.");
			}
		}	

    super.arrivedAt(structure);
	}
	
	/**
	 * has the mission completed yet?
	 * @return true if it has been completed, false if not
	 * @see edu.vtc.cis2260.hvz.Mission#isMissionCompleted()
	 */
	@Override
	public boolean isMissionCompleted()
	{
		return _completed;
	}
	private boolean _completed;

	private int _money; // The amount of money the player has to buy a gun. Should be >= 0
	private final HumanController _human;  // the (controller for) the person doing this, never null
}
