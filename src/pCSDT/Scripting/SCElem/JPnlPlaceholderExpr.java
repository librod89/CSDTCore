/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting.SCElem;

import java.awt.*;
import javax.swing.JPanel;

/**
 * This is a placeholder component used for bodies where a drag spot is needed, but may
 * be empty.
 *
 * @author Jason Sanchez
 */
public class JPnlPlaceholderExpr extends JPanel
{
	GridBagConstraints constraints = new GridBagConstraints();
	static final Dimension minDim = new Dimension(0, 15);

	public JPnlPlaceholderExpr()
	{
		setPreferredSize(minDim);
                setBackground(Color.BLACK);
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
		d.width = Math.max(minDim.width, d.width);
		d.height = Math.max(minDim.height, d.height);
		setPreferredSize(d);
		return c;
	}
}
