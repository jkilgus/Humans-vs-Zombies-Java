/**
 * Display.java
 * Copyright 2011-2014, Craig A. Damon
 * all rights reserved
 */
package edu.vtc.cis2260.hvz;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * Display - an actual visible display
 * @author Craig A. Damon
 *
 */
public class Display extends JFrame
{ 
	// java likes these for objects that implement Serializable
	private static final long serialVersionUID = -746893135768898959L;


	/**
	 * @param field the area where the simulation will run, never null
	 * @throws HeadlessException
	 */
	public Display(PlayingField field) throws HeadlessException
		{
			super("Humans vs Zombies");
			_field = new FieldDisplay(field);
			_scores = new ScorePanel(field.getGame());
			getContentPane().setLayout(new BorderLayout());
			getContentPane().add(_field,BorderLayout.CENTER);
			getContentPane().add(_scores,BorderLayout.SOUTH);
			pack();
 	    addWindowListener(new WindowAdapter(){
				@Override
				public void windowClosing(WindowEvent e)
				{
	        System.exit(0);				
				}
	      });
			setVisible(true);
		}


	/** draw a structure on the display
	 * @param g the rendering environment, never null
	 * @param x the left hand side of the structure, >= 0, <= field.getWidth(), in playing field units
	 * @param y the top of the structure, >= 0, <= field.getHeight(), in playing field units
	 * @param width the width of the structure, > 0, in playing field units
	 * @param height the height of the structure, > 0, in playing field units
	 * @param name the name of the structure, never null
	 * @param doorSide the side with a door, 1 = right, 2 = top, 3 = left, 4 = bottom
	 */
	public void displayStructure(Graphics g, int x, int y, int width, int height, String name,int doorSide)
	{
     _field.displayStructure(g,x,y,width,height, name,doorSide);		
	}


	/** draw the representation for a player on the simulation
	 * @param g the rendering environment
	 * @param x the x position of the player in playing field units, >= 0, < field.getWidth()
	 * @param y the y position of the player in playing field units, >= 0, < field.getWidth()
	 * @param dir the direction the player is facing, in degrees
	 * @param type the type of the player, either 'H' for human or 'Z' for zombie
	 * @param number the id number of the player, > 0
	 * @param width the width of the player in playing field units, > 0
	 * @param height the height of the player in playing field units, > 0
	 * @param name the name of the player, never null
	 */
	public void displayPlayer(Graphics g, int x, int y, int dir, char type, int number)
	{
     _field.displayPlayer(g,x,y,dir,type,number);		
	}
	
	/**
	 * redraw the score panel
	 *
	 */
	public void updateScores()
	{
		_scores.updateScores();
	}
	

	/** note that the simulation might have ended, updating the display appropriately
	 * @param ended if true, everything is done
	 */
	public void gameOver(boolean ended)
	{
		_scores.gameEnded(ended);
	}


	private final FieldDisplay _field;  // never null
	private final ScorePanel _scores;  // never null

	
	/**
	 * FieldDisplay - a component that will show a playing field
	 * @author Craig A. Damon
	 *
	 */
	private final class FieldDisplay extends JComponent
	{
		private static final long serialVersionUID = 7075366977433663675L;

		/**
		 * @param field the field for the simulation, never null
		 */
		public FieldDisplay(PlayingField field)
			{
				_field = field;
				_baseFont = new Font("SansSerif",Font.BOLD,10);
				Dimension size = new Dimension(_field.getWidth(),_field.getHeight());
				setMinimumSize(size);
				setPreferredSize(size);
			}
		
		/**
		 * draw this display
		 * @param g the graphics to use to draw, never null
		 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
		 */
		@Override
		public void paintComponent(Graphics g)
		{
			g.setColor(Color.GREEN);
			g.fillRect(0, 0, getWidth(), getHeight());
			_field.display(g);
		}
		
		static final private int PLAYER_WIDTH=30;
		static final private int PLAYER_HEIGHT=15;
		
		/** draw a single player
		 * @param g the graphics to use to draw, never null
		 * @param x the x coordinate in field coordinates
		 * @param y the y coordinate in field coordinates
		 * @param dir the direction the player is facing, in degrees
		 * @param type the type of the player, either 'H' or 'Z'
		 * @param number the id number of the player
		 */
		public void displayPlayer(Graphics g, int x, int y, int dir, char type,int number)
		{
			if (type == 'H')
				g.setColor(Color.BLUE);
			else
				g.setColor(Color.RED);
			String name = String.valueOf(type)+String.valueOf(number);
			int pointSize = 10;
			Font font = _baseFont.deriveFont(Font.BOLD,pointSize);
			g.setFont(font);
			int width = g.getFontMetrics().stringWidth(name)+8;
			if (width < PLAYER_WIDTH)
				width = PLAYER_WIDTH;
			int left = convertX(x)-width/2;
			int top = convertY(y)-PLAYER_HEIGHT/2;
			g.drawRect(left,top,width,PLAYER_HEIGHT);
			g.drawString(name,left+2,top+pointSize);
			int dirX1 = left+width-2;
			int dirY1 = top+PLAYER_HEIGHT/2;
			int dirX2 = dirX1+(int)(Math.cos(dir)*2+0.5);
			int dirY2 = dirY1+(int)(Math.sin(dir)*2+0.5);
			g.drawLine(dirX1,dirY1,dirX2,dirY2);
		}

		/** actually draw the structure
		 * @param g the graphics to use to draw, never null
		 * @param x the x coordinate in field coordinates
		 * @param y the y coordinate in field coordinates
		 * @param width the width in field units, > 0
		 * @param height the height in field units, > 0
		 * @param name the name of the structure, never null, never empty
		 * @param doorSide the side where there is a door, like Structure.DOOR_SIDE_RIGHT
		 */
		public void displayStructure(Graphics g, int x, int y, int width,int height, String name,int doorSide)
		{
			g.setColor(Color.DARK_GRAY);
			int left = convertX(x);
			int top = convertY(y);
			int w = convertX(width);
			int h = convertY(height);
			g.fillRect(left,top, w, h);
			//System.out.println("Drawing "+name+" at "+left+"("+x+"),"+top+"("+y+") for "+w+"("+width+")X"+h+"("+height+"), window is "+getWidth()+"X"+getHeight());
			g.setColor(Color.WHITE);
			int doorLength = 10;
			int doorThick = 4;
			switch (doorSide)
			{
			case Structure.DOOR_ON_RIGHT:
				g.fillRect(left+w-doorThick/2, top+(h-doorLength)/2, doorThick, doorLength);
				break;
			case Structure.DOOR_ON_TOP:
				g.fillRect(left+(w-doorLength)/2, top-doorThick/2, doorLength, doorThick);
				break;
			case Structure.DOOR_ON_LEFT:
				g.fillRect(left-doorThick/2, top+(h-doorLength)/2, doorThick, doorLength);
				break;
			case Structure.DOOR_ON_BOTTOM:
				g.fillRect(left+(w-doorLength)/2, top+h-doorThick/2, doorLength, doorThick);
				break;
			default:
				System.out.println("Invalid door placement"+doorSide); // not necessary if using enum
				break;
			}
			int pointSize = 18;
			Font font = _baseFont.deriveFont(Font.BOLD,pointSize);
			g.setFont(font);
			int textWidth = g.getFontMetrics().stringWidth(name);
			g.drawString(name, left+(w-textWidth)/2, top+pointSize);
		}
		
		/**
		 * convert from playing field units to display units for horizontal direction
		 * @param x >= 0, < field.getWidth
		 * @return >= 0 < getWidth()
		 */
		private int convertX(int x)
		{
			return (int)(x*(float)getWidth()/_field.getWidth()+0.5f);
		}
		
		
		/**
		 * convert from playing field units to display units for vertical direction
		 * @param y >= 0, < field.getHeight
		 * @return >= 0 < getHeight()
		 */
		private int convertY(int y)
		{
			return (int)(y*(float)getHeight()/_field.getHeight()+0.5f);
		}

		@SuppressWarnings("hiding")
	  private final PlayingField _field;  // never null
		private final Font _baseFont;  // never null
	}

	/**
	 * 
	 * ScorePanel - a display panel for showing the progress of the simulation
	 * @author Craig A. Damon
	 *
	 */
  private static class ScorePanel extends Box
  {
  	  private static final long serialVersionUID = 8073145651293753256L;
			
  	  /**
  	   * create a display for the indicated game
  	   * @param game never null
  	   */
  	  public ScorePanel(HvZGame game)
  	   {
  	  	   super(BoxLayout.X_AXIS);
  	  	   _game = game;
  	  	   add(Box.createHorizontalStrut(20)); // set a minimum pad on the left
       add(Box.createHorizontalGlue());  // then absorb extra space
       add(new JLabel("Humans:"));
       add(Box.createHorizontalStrut(5));
  	  	   _humans = new JTextField(4);
  	  	   add(_humans);
  	  	   add(Box.createHorizontalGlue());
  	  	   add(Box.createHorizontalStrut(20));
  	  	   add(Box.createHorizontalGlue());
  	       add(new JLabel("Zombies:"));
  	       add(Box.createHorizontalStrut(5));
  	  	  	   _zombies = new JTextField(4);
  	  	  	   add(_zombies);
  	  	  	   add(Box.createHorizontalGlue());
  	  	  	   add(Box.createHorizontalStrut(20));
  	  	  	   _replay = new JButton("Replay");
  	  	  	   _replay.setEnabled(false);
  	  	  	   _replay.addActionListener((event)->{
  	  	  	  	    	    _game.resetGame();
  	  	  	  	    	    _game.run();
  	  	  	  	     }
  	  	  	   );
  	  	  	   add(_replay);
  	  	  	   add(Box.createHorizontalStrut(20));
  	   }
  	   
  	   /** note that the game may have ended
			 * @param ended if true, it's over
			 */
			public void gameEnded(boolean ended)
			{
				_replay.setEnabled(ended);
			}

			/**
  	    * update the scores on this panel
  	    *
  	    */
  	   public void updateScores()
  	   {
	  	   _humans.setText(String.valueOf(_game.numHumans()));
	  	   _zombies.setText(String.valueOf(_game.numZombies()));
  	   }
  	   
  	   
  	   private final JTextField _humans;  // never null
  	   private final JTextField _zombies;  // never null
  	   private final JButton _replay;  // never null
  	   private final HvZGame _game;  // never null
  }
}
