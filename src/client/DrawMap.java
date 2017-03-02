package client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import common.World;


public class DrawMap {

	Color[][] colorMap;
	static World world;
	
	private Color cForest = new Color(111, 232, 86);
	private Color cPlains = new Color(177, 255, 95);
	private Color cMountain = new Color(102, 88, 69);
	private Color cSwamp = new Color(89, 84, 43);
	private Color cIsland = new Color(255, 228, 45);
	
    private class CustomPanel extends JPanel {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public CustomPanel() {
            super();
        }


        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            world = GUIControl.getWorld();
            
            for(int i = 0; i<world.getHeight(); i++){
            	for(int j = 0; j<world.getWidth(); j++){
            		//Resets Color
            		g.setColor(Color.black);
            		if(world.getBiomeName(i, j).equals("forest")) {
            			g.setColor(cForest);
            		}else if(world.getBiomeName(i, j).equals("plains")) {
            			g.setColor(cPlains);
            		}else if(world.getBiomeName(i, j).equals("mountain")) {
            			g.setColor(cMountain);
            		}else if(world.getBiomeName(i, j).equals("swamp")) {
            			g.setColor(cSwamp);
            		}else if(world.getBiomeName(i, j).equals("island")) {
            			g.setColor(cIsland);
            		}
            		
            		if(world.getBiomeName(i, j).equals("empty")){
            			g.clearRect(j*25 +100, i*25 +100, 25, 25);
            		} else {
            			g.fillRect(j*25 +100, i*25 +100, 25, 25);
            		}

            	}
            }
        }
    }

    JFrame frame;
    private JScrollPane sp;
    private CustomPanel mp;

    /**
     * Launch the application.
     */


    /**
     * Create the application.
     */
    public DrawMap() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();

        mp = new CustomPanel();
        sp = new JScrollPane(mp);
        frame.add(sp);
    }

}