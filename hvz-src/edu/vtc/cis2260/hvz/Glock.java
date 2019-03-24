package edu.vtc.cis2260.hvz;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * The Glock17 is the most common it carries 17
 * rounds of 9mm ammo and has the ability to jam.
 * Has chance of headshot based on distance.
 * Some jams can be cleared in the field
 * More rare jams require tools inside to fix
 * @author think
 *
 */
public class Glock extends Weapon {

	/**
	 * Every glock has a List of targets and an owner
	 * @param owner
	 */
	protected Glock(Player owner) {
		super(owner);
		_targets = new ArrayList<>();
	}

	/**
	 * Cycles through the list of opponents on the field
	 * to acquire a target before shooting at it
	 */
	@Override
	public void chooseTargets() {
		_targets.clear();
		for (Player z: getPlayer().findOpponents()) {
			_targets.add(z);
		}
		repOK();
	}

	/**
	 * Fires the weapon for every player in the target list
	 * until the weapon runs out of ammo or jams
	 */
	@Override
	public void fire() {
		// Pistols are still inacurrate but at least
		// Glocks hold a lot more ammo than revolvers
		_rand = Math.random();
		if (_rand > 0.3 && _capacity > 0) {
			for (Player p : _targets) {
				// If the Zombie has more than 4 health and is closer than 150
				// it takes 5 headshot damage.
				if (p.getCurrentHealth() > 4 && p.distanceTo(getPlayer()) < 150 && _capacity > 0) {	
					if (p.getCurrentHealth() == 5  && _capacity > 0) {
						getPlayer().getGame().killZombie(p);
						System.out.println(getPlayer().toString() + " with Headshot using Glock." + _capacity + " bullets left.");
						_capacity--;
						wait(1);
					}
					p.setCurrentHealth(-5);
					System.out.println(getPlayer().toString() + " Fired Glock and head shotted " + p.toString() + " for 5. " + p.getCurrentHealth() + "hp left." + _capacity + " bullets left.");
					_capacity--;
					wait(1);
				}
				// If the zombie doesn't take a headshot, it only takes 1 damage
				else if (p.getCurrentHealth() > 1 && _capacity > 0) {						
					p.setCurrentHealth(-1);
					_capacity--;
					System.out.println(getPlayer().toString() + " Fired Glock and hit " + p.toString() + " for 1. " + p.getCurrentHealth() + "hp left." + _capacity + " bullets left.");
					// Pause 1 second between each shot
					wait(1);
				// If the zombie only has 1 health left then 1 damage kills it
				} else if (p.getCurrentHealth() == 1 && _capacity > 0) {
					getPlayer().getGame().killZombie(p);
					System.out.print(getPlayer().toString() + " using a Glock \n" + _capacity + " bullets left.");
					_capacity--;
					// Pause 1 second between each shot
					wait(1);
				}
			}
		} else if (_rand < 0.05  && _capacity > 0) {
			// Ideally you would also have a field serviceable jam state that
			// you didn't have to go inside to fix and just caused a few seconds delay.
			System.out.println(getPlayer().toString() + "'s gun Jammed! Cycling action.");
			if (_capacity > 1) {
				_capacity--;
				wait(3);
			} else if (_capacity == 0) {
				System.out.println("this shouldn't happen");
			}
		// Wow this auto pistol holds so much more ammo than the revolver!
		// why would anybody ever want to use a revolver? Auto pistols
		// have a chance of jamming. That's really their only downside.
		// The wait is longer because the user struggles before realizing
		// the weapon is jammed and won't easily clear. Capacity is set to
		// zero which basically means he will have to go inside to fix the jam with tools.
		// Todo: Implement mission to fix jam
		} else if (_rand < 0.025  && _capacity > 0) {
			System.out.println(getPlayer().toString() + "'s gun Jammed!");
			_capacity = 0;
			wait(5);
		} else if (_capacity > 0){
			System.out.println(getPlayer().toString() + " Fired Glock and Missed!");
			_capacity--;
			wait(1);
		} else {
			System.out.println(getPlayer().toString() + " is out of ammo.");
		}
	}

	/**
	 * Checks to see if the gun is empty
	 */
	@Override
	public boolean isEmpty() {
		if (_capacity <= 0) {
			return true;
		} else {
			return false;
		}
	}
	
	// Time to wait in seconds between shots
	public void wait(int s) {
		try {
			TimeUnit.SECONDS.sleep(s);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// Reloads the gun if it can be
	@Override
	public void reload() {
		if (_capacity < 17) {
			_capacity = 17;
		}
		repOK();
	}
	
	// Returns the number of shots left in the gun
	@Override
	public int getRemainingShots() {
		return _capacity;
	}

	// Returns the next target in the list
	@Override
	public Iterator<Player> getCurrentTargets() {
		return _targets.iterator();
	}
	
	// Verify rep invariants
	private void repOK() {
		assert this._capacity >= 0 && this._capacity <= 17;
		assert this._targets.size() >= 0;
	}
	
	private int _capacity = 17; // Number of shots in gun. Must be between 0 and 17.
	private List<Player> _targets; // Targets on the field. Never null. Always >= 0
	private double _rand; // Random number for hit chance from 0 to 1
}
