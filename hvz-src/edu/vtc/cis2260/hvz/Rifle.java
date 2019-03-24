package edu.vtc.cis2260.hvz;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * The tried and true hunting rifle. Who needs rapid fire
 * or high capacity when 1 well aimed shot will do the trick?
 * Modeled after the M107 this gun fires 10 rounds
 * 2 seconds between each shot because bolt action
 * chance of headshot within certain range
 * headshot range is farther than that of pistols
 * very  high accuracy
 * does not jam
 * @author think
 *
 */
public class Rifle extends Weapon {

	protected Rifle(Player owner) {
		super(owner);
		_targets = new ArrayList<>();
	}

	// List of targets to shoot at
	@Override
	public void chooseTargets() {
		_targets.clear();
		for (Player z: getPlayer().findOpponents()) {
			_targets.add(z);
		}

	}
	
	// Fires at list of opponents while ammo allows
	@Override
	public void fire() {
		// Rifles do a lot of damage and are very accurate
		// but have a slow rate of fire
		_rand = Math.random();
		if (_rand > 0.05 && getRemainingShots() > 0  && _capacity > 0) {
			for (Player p : _targets) {
				// If the Zombie has more than 7 health and is closer than 275
				// it takes 7 headshot damage
				if (p.getCurrentHealth() > 7 && p.distanceTo(getPlayer()) < 275  && _capacity > 0) {	
					if (p.getCurrentHealth() == 8) {
						getPlayer().getGame().killZombie(p);
						System.out.println(getPlayer().toString() + "with Headshot using Rifle.");
						setRemainingShots(-1);
						wait(2);
					}
					p.setCurrentHealth(-7);
					System.out.println(getPlayer().toString() + " Fired Rifle and head shotted " + p.toString() + " for 5. " + p.getCurrentHealth() + "hp left.");
					setRemainingShots(-1);
					wait(2);
				}
				// If the zombie doesn't take a headshot, it only takes 3 damage
				else if (p.getCurrentHealth() > 3  && _capacity > 0) {						
					p.setCurrentHealth(-3);
					System.out.println(getPlayer().toString() + " Fired Rifle and hit " + p.toString() + " for 3. " + p.getCurrentHealth() + "hp left.");
					setRemainingShots(-1);
					// Pause 1 seconds between each shot
					wait(2);
				// If the zombie has 3 or less health it dies
				} else if (p.getCurrentHealth() <= 3  && _capacity > 0) {
					getPlayer().getGame().killZombie(p);
					System.out.print(getPlayer().toString() + " using a Rifle \n");
					setRemainingShots(-1);
					// Pause 2 seconds between each shot
					wait(2);
				}
			}
		} else if (_capacity > 0){
			System.out.println(getPlayer().toString() + " Fired Rifle and Missed!");
			setRemainingShots(-1);
			wait(1);
		}
		repOK();
	}

	// Checks to see if gun is empty
	@Override
	public boolean isEmpty() {
		if (_capacity <= 0) {
			return true;
		} else {
			return false;
		}
	}
	
	// Time to wait in seconds for pausing between shots to cycle
	public void wait(int s) {
		try {
			TimeUnit.SECONDS.sleep(s);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// Reloads the weapon
	@Override
	public void reload() {
		if (_capacity < 10) {
			_capacity = 10;
		}
	}

	// Gets and sets remaining shots. This was here to find a bug
	// turns out I didn't need it but it's probably a good way
	// to do things anyway.
	@Override
	public int getRemainingShots() {
		return _capacity;
	}
	
	public void setRemainingShots(int s) {
		_capacity += s;
	}

	
	// Gets next target on the list
	@Override
	public Iterator<Player> getCurrentTargets() {
		return _targets.iterator();
	}
	
	// Verifies rep infariants
	public void repOK() {
		assert _capacity <= 10;
		assert this._targets.size() >= 0;
	}
	
	private int _capacity = 10; // Capacity of weapon. 0 to 10
	private List<Player> _targets; // List of targets >= 0
	private double _rand; // Random number for hit chance
}
