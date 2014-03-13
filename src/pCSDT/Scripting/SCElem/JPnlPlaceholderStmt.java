/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting.SCElem;

import java.awt.*;
import java.net.URL;
import javax.swing.JPanel;

/**
 * This is a placeholder component used for bodies where a drag spot of 
 * statement is needed, but may be empty.
 *
 * @author Jason Sanchez
 */
public class JPnlPlaceholderStmt extends JPanel
{
    static Image img;
    GridBagConstraints constraints = new GridBagConstraints();
    static final Dimension minDim = new Dimension(0, 15);

     /**
     * This is a convenience method that allows compact conditional loading.
     *
     * @param img The function first tests to see if this is non-null.  If it is null, the
     * method simply returns {@code img}.  This is provided for convenience only.
     * @param src This is the image source to be loaded if {@code img} is null.
     * @return The resource loaded from {@code src}, or null if the resource could not be loaded.
     */
    private Image GetImage(String src)
    {
	try
	{
            URL myurl = JPnlPlaceholderStmt.class.getResource(src);
            Toolkit tk = getToolkit();
            return tk.getImage(myurl);
	}
	catch(Exception ex) {
            ex.printStackTrace();
        }
	return null;
    }

    public JPnlPlaceholderStmt()
    {
        if (img == null) {
            img = GetImage("/resource/layouts/imgs/codeletEmpty.png");
        }
	setPreferredSize(minDim);
        setOpaque(false);
        // Create a new layout with one row and one column:
	GridBagLayout l = new GridBagLayout();
	l.columnWeights = new double[]{1.0};
	l.columnWidths = new int[]{1};
	l.rowWeights = new double[]{1.0};
	l.rowHeights = new int[]{1};
	setLayout(l);
        constraints.fill = GridBagConstraints.BOTH;
    }

    @Override
    public Component add(Component c)
    {
        super.add(c, constraints);
        // Lower bound the dimension and return:
	Dimension d = c.getPreferredSize();
	d.width = Math.max(minDim.width, d.width-5);
	d.height = Math.max(minDim.height, d.height-5);
        setPreferredSize(d);
	return c;
    }

    @Override
    public void paintComponent(Graphics g)
    {
        g.drawImage(img, 0, 0, getWidth(), 5, 0, 0, 360, 10, this);
        g.drawImage(img, 0, 5, getWidth(), getHeight()-5, 0, 11, 360, 70, this);
        g.drawImage(img, 0, getHeight()-5, getWidth(), getHeight(), 0, 71, 360, 80, this);

       super.paintComponent(g);
    }
}
