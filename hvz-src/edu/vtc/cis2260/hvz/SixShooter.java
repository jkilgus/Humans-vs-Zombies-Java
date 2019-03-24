package edu.vtc.cis2260.hvz;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.*;

/**
 * A six shot revolver for humans
 * Has chance of missing
 * Chance of headshot based on distance
 * Cannot be jammed
 * @author think
 *
 */
public class SixShooter extends Weapon {

	/**
	 * Every six shooter has a list of targets associated with it
	 * as well as an owner
	 * @param owner - The human player who has the six shooter
	 */
	protected SixShooter(Player owner) {
		super(owner);
		_targets = new ArrayList<>();
	}

	/**
	 * Cycles through the list of opponents on the field
	 * to acquire as a target before shooting at it
	 */
	@Override
	public void chooseTargets() {
		_targets.clear();
		for (Player z : getPlayer().findOpponents()) {
			_targets.add(z);
			repOK();
		}
	}

	/**
	 * Fires the weapon for every player in the target list
	 * until the weapon runs out of ammunition
	 */
	@Override
	public void fire() {
		// Pistols are terribly inaccurate in real life
		// unless you are very experienced
		// 25% of the time it works every time
		_rand = Math.random();
		if (_rand > 0.3 && _capacity > 0) {
			for (Player p : _targets) {
				// If the zombie has more than 7 health and is closer than 150
				// then it takes 8 headshot damage
				if (p.getCurrentHealth() > 7 && p.distanceTo(getPlayer()) < 150  && _capacity > 0) {
					if (p.getCurrentHealth() == 8  && _capacity > 0) {
						getPlayer().getGame().killZombie(p);
						System.out.println(getPlayer().toString() + " with Headshot from SixShooter.");
						_capacity--;
						wait(1);
					}
					p.setCurrentHealth(-8);
					System.out.print(getPlayer().toString() + " Fired SixShooter and head shotted " + p.toString() + " for 8. " + p.getCurrentHealth() + "hp left.\n");
					_capacity--;
					wait(1);
				// If the zombie isn't headshotted it takes 2 damage
				} else if (p.getCurrentHealth() > 2  && _capacity > 0) {
					p.setCurrentHealth(-2);
					System.out.println(getPlayer().toString() + " Fired SixShooter and hit " + p.toString() + " for 2. " + p.getCurrentHealth() + " hp left.");
					_capacity--;
					wait(1);
				} else if (p.getCurrentHealth() == 2  && _capacity > 0){
					getPlayer().getGame().killZombie(p);
					System.out.println(getPlayer().toString() + " using a Six-Shooter");
					_capacity--;
					// Pause 1 second between each shot
					wait(1);
				} else {
					// This shouldn't happen.
				}
			}
		} else if (_capacity > 0){
			System.out.println(getPlayer().toString() + " Fired Six-Shooter and Missed!");
			_capacity--;
			wait(1);
		}
		repOK();
	}

	/**
	 * Checks to see if the gun is out of ammo or not
	 */
	@Override
	public boolean isEmpty() {
		if (_capacity <= 0) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Time to wait in seconds
	 * Used to pause between gun shots
	 * so that multiple zombies don't just instantly die
	 * @param s
	 */
	public void wait(int s) {
		try {
			TimeUnit.SECONDS.sleep(s);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reloads the gun if it can be
	 */
	@Override
	public void reload() {
		if (_capacity < 6) {
			_capacity = 6;
		}
		repOK();
	}

	/**
	 * Returns the amount of bullets left in the gun
	 */
	@Override
	public int getRemainingShots() {
		return _capacity;
	}

	/**
	 * Returns the next target in the list
	 */
	@Override
	public Iterator<Player> getCurrentTargets() {
		return _targets.iterator();
	}
	
	// Verifies rep invariants
	private void repOK() {
		assert this._capacity <= 6 && this._capacity >= 0;
		assert this._targets.size() >= 0;
	}
	
	// The bullet capacity of the six-shooter 0 to 6
	private int _capacity = 6;
	// List of targets
	private List<Player> _targets;
	// Random number for chance of missing the shot
	private double _rand;
}
