/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting.SCElem;

import javax.swing.*;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 *
 * @author Jason
 */
public class JPnlSingleChild extends JPanel {
    GridBagLayout l;
    static final GridBagConstraints s_c = new GridBagConstraints(
        0, 0, 1, 1,
        1, 1,
        GridBagConstraints.CENTER,
        GridBagConstraints.BOTH,
        new Insets(0, 0, 0, 0), 0, 0
    );

    public JPnlSingleChild()
    {
        setLayout(l = new GridBagLayout());
    }

    public void setChild(JComponent c)
    {
        while(getComponentCount() != 0)
            remove(getComponent(0));
        add(c, s_c);
    }
}
