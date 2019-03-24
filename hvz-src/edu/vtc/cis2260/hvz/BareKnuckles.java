package edu.vtc.cis2260.hvz;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BareKnuckles extends Weapon {

	/**
	 * Bare knuckles are the cheapest weapon
	 * 50% chance of hitting
	 * 1 damage per hit
	 * @param owner
	 */
	protected BareKnuckles(Player owner) {
		super(owner);
		_targets = new ArrayList<>();
	}

	/**
	 * Cycles through opponents to target for next shot
	 */
	@Override
	public void chooseTargets() {
		_targets.clear();
		for (Player z : getPlayer().findOpponents()) {
			_targets.add(z);
		}
		repOK();
	}

	/**
	 * Fires the weapon for each target on list
	 * the capacity is 3 because I don't think
	 * you would get more than 3 swings before
	 * a zombie got you
	 */
	@Override
	public void fire() {
		_rand = Math.random();
		if (_rand > 0.5 && _capacity > 0) {
			for (Player p : _targets) {
				if (p.getCurrentHealth() > 1  && _capacity > 0) {
					p.setCurrentHealth(-1);
					System.out.print(getPlayer().toString() + " punched " + p.toString() + " for 1. " + p.getCurrentHealth() + "hp left.\n");
					_capacity--;
					wait(1);
				} else if (p.getCurrentHealth() == 1  && _capacity > 0) {
					getPlayer().getGame().killZombie(p);
					System.out.println(getPlayer().toString() + " using his bare hands! Wow!");
					_capacity--;
					wait(1);
				}
			}
		} else if (_capacity > 0){
			System.out.println(getPlayer().toString() + " desperately punched at a zombie and missed.");
			_capacity--;
			wait(1);
		}
		repOK();
	}

	// Check if out of ammo
	@Override
	public boolean isEmpty() {
		if (_capacity <= 0) {
			return true;
		} else {
			return false;
		}
	}

	// Reload if you can
	@Override
	public void reload() {
		if (_capacity < 3) {
			_capacity = 3;
		}
		repOK();
	}
	
	// Wait between swings so multiple zombies
	// aren't attacked instantaniously.
	public void wait(int s) {
		try {
			TimeUnit.SECONDS.sleep(s);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// Returns remaining shots
	@Override
	public int getRemainingShots() {
		return _capacity;
	}

	// Returns next target in list
	@Override
	public Iterator<Player> getCurrentTargets() {
		return _targets.iterator();
	}
	
	// Verify rep invariants
	private void repOK() {
		assert this._capacity >= 0 && this._capacity <= 3;
		assert this._targets.size() >= 0;
	}
	
	// I'd wager you'd get about 3 good swings at
	// a zombie on average before it got you
	// 0 to 3
	private int _capacity = 3;
	// List of targets to swing at >= 0
	private List<Player> _targets;
	private double _rand; // Chance of hit 0 to 1
}
