/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Presentation;

import javax.swing.*;
import java.awt.*;
import java.net.*;

/**
 *
 * @author kotulc
 */
public class ScriptletLine extends JPanel {

    static Image img;

    public ScriptletLine()
    {
        if (img == null) {
            try {
                URL myurl = this.getClass().getResource("/resource/layouts" +
                        "/imgs/Line.png");
                Toolkit tk = this.getToolkit();
                img = tk.getImage(myurl);
            }
            catch(Exception ex)
            {System.out.println(ex.toString());}
        }

        this.setOpaque(false);

        Dimension size = new Dimension(220, 20+Math.max(25,25));
        //this.setPreferredSize(size);
        this.setMinimumSize(size);

        SpringLayout springlayout = new SpringLayout();
        this.setLayout(springlayout);
        //Size is hardcoded for now
        resource.layouts.SpringUtilities.makeCompactGrid(this,
                0, 2, 200, 5, 5, 5);
    }

    /**
     * This method tells how to draw this component.
     * @param g Graphic object
     */
    @Override
    public void paintComponent (Graphics g)
    {
       g.drawImage(img, 0, 0, this.getWidth(), this.getHeight()+5, this);
       super.paintComponent(g);
       this.repaint();
    }
}
