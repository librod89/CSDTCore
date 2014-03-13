/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * JPnlControlManager.java
 *
 * Created on Jun 22, 2010, 8:24:28 AM
 */

package pCSDT.Presentation;
import java.awt.Color;
import java.awt.GridBagConstraints;
import pCSDT.Scripting.*;
import pCSDT.Scripting.SCElem.*;

/**
 *
 * @author tylau
 */
public class JPnlControlMgr extends JPnlTemplateMgr
{
    public JPnlControlMgr(LayoutInfo l, JPnlScriptlet jpnlScriptlet)
    {
        super(l, jpnlScriptlet);
        setBackground(new Color(125,125,125));
        c.fill = GridBagConstraints.NONE;
        
        // Add each of the control codelets:
        JPnlLineWhile whileLine = new JPnlLineWhile(l);
        c.gridy = 0;
        add(whileLine, c);
        whileLine.InitControls();
        whileLine.setEditable(false);

        JPnlLineIf ifLine2 = new JPnlLineIf(new PStatementIf(new PStatementNull(), new PStatementList(), null), l);
        c.gridy = 1;
        add(ifLine2, c);
        ifLine2.InitControls();
        ifLine2.setEditable(false);

        JPnlLineIf ifLine = new JPnlLineIf(l);
        c.gridy = 2;
        add(ifLine, c);
        ifLine.InitControls();
        ifLine.setEditable(false);

    	JPnlLineRepeatN repeatN = new JPnlLineRepeatN(l);
        c.gridy = 3;
        add(repeatN, c);
        repeatN.InitControls();
        repeatN.SetConditionEnabled(false);
        repeatN.setEditable(false);

        JPnlLineDoForever doForever = new JPnlLineDoForever(l);
        c.gridy = 4;
        add(doForever, c);
        doForever.InitControls();
        doForever.setEditable(false);

        // update the panel
        doLayout();
        updateUI();
    }
}
