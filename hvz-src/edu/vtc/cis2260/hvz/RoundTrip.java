/**
 * RoundTrip.java
 * Copyright 2011-2014, Craig A. Damon
 * all rights reserved
 */
package edu.vtc.cis2260.hvz;

/**
 * RoundTrip - go on a mission to go somewhere and then return
 * @author Craig A. Damon
 *
 */
public abstract class RoundTrip extends Mission
{

	/**
	 * initialize the round trip
	 * @param player the player being sent, never null, must currently be inside a structure
	 * @param destName the name of the structure going to
	 * @param waitTime the time in simulation ticks, to wait at the destination before returning
	 */
	public RoundTrip(Player player,String destName,int waitTime)
		{
			super(player);
			_targetStructure = getGame().findStructure(destName);
			_returnStructure = player.currentlyInside();
		  _remainingTimeAtTarget = waitTime;
			_headingOut = true;
		}

	/**
	 * where are we heading for right now, if anywhere
	 * @return the structure or null if we aren't heading anywhere right now
	 * @see edu.vtc.cis2260.hvz.Mission#getDestination()
	 */
	@Override
	public Structure getDestination()
	{
		if (_headingOut)
			return _targetStructure;
		else if (_atTarget)
			return null;
		else if (_headingHome)
			return _returnStructure;
		else  // better be completed
		  return null;
	}

	/**
	 * note that we arrived at a structure
	 * @param structure
	 * @see edu.vtc.cis2260.hvz.Mission#arrivedAt(edu.vtc.cis2260.hvz.Structure)
	 */
	@Override
	public void arrivedAt(Structure structure)
	{
		if (_headingOut)
			{
				if (_targetStructure == structure)
					{
						_headingOut = false;
						_atTarget = true;
					}
			}
		else if (_headingHome)
			{
				if (_returnStructure == structure)
					{
						_headingHome = false;
						_completed = true;
					}
			}
	}

	/**
	 * note that some time has passed
	 * @see edu.vtc.cis2260.hvz.Mission#timePassed()
	 */
	@Override
	public void timePassed()
	{
		if (_atTarget)
			{
				if (--_remainingTimeAtTarget <= 0)
					{
						// time to head back
						_atTarget = false;
						_headingHome = true;
						getPlayer().exitStructure(_targetStructure);
					}
			}

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
	
  private final Structure _targetStructure;  // never null
  private final Structure _returnStructure;  // never null
  private boolean _headingOut;   // !( _atTarget or _headingHome or _completed)
  private boolean _atTarget;     // !( _headingOut or _headingHome or _completed)
  private int _remainingTimeAtTarget;  // > 0 if _headingOut or _atTarget, == 0 if _headingHome or _completed
  private boolean _headingHome;  // !( _headingOut or _atTarget or _completed)
  private boolean _completed;    // !( _headingOut or _atTarget or _headingHome)
}
