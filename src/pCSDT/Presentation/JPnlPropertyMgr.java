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
public class JPnlPropertyMgr extends JPnlTemplateMgr
{
    PObject m_pObj;
      
    public JPnlPropertyMgr(LayoutInfo l, JPnlScriptlet jpnlScriptlet)
    {
        super(l, jpnlScriptlet);
        setBackground(new Color(125,125,125));
        InitControls();
    }

    private void InitControls()
    {
        int i = 0;
        if (m_pObj != null) {
            PProperty[] props = m_pObj.GetProperties();
            if(props.length > 0) {
                for(PProperty cur : props)
                {
                    String runTimeProps = cur.GetUnboundProperty().GetRunTimeProperties();
                    if (runTimeProps.equals("E"))
                        AddExpression(new JPnlLineProperty(new PStatementProperty(cur), m_l), i++);
                    else if (runTimeProps.equals("R"))
                        AddExpression(new JPnlLineReadOnlyProperty(new PStatementReadOnlyProperty(cur), m_l), i++);
                }
            }
        }
        ////AddExpression(new JPnlLineAssign(new PStatementAssign(new PStatementConst(1)), m_l), i++);

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
