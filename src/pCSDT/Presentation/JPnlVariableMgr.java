/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Presentation;

import java.awt.*;
import pCSDT.Scripting.*;
import pCSDT.Scripting.SCElem.*;

/**
 *
 * @author Jason Sanchez
 */
public class JPnlVariableMgr extends JPnlTemplateMgr
{
    PObject m_pObj;
        
    public JPnlVariableMgr(LayoutInfo l, JPnlScriptlet jpnlScriptlet)
    {
        super(l, jpnlScriptlet);
        setBackground(new Color(125,125,125));
        InitControls();
    }

    private void InitControls()
    {
        int i = 0;
        
        AddExpression(new JPnlLineVariable(new PStatementVariable("a"), m_l), i++);

        AddExpression(new JPnlLineAssign(new PStatementAssign(new PStatementConst(1)), m_l), i++);

        // Update the panel
        updateUI();
    }

    @Override
    public void BindObject(PObject obj)
    {
	m_pObj = obj;
	removeAll();
	InitControls();
    }
}
