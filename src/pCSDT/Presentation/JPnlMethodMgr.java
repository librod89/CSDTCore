/*
 * JPnlMethodMgr.java
 *
 * Created on March 1, 2009, 12:16 PM
 */

package pCSDT.Presentation;
import java.awt.Color;
import java.awt.GridBagConstraints;
import pCSDT.Scripting.*;
import pCSDT.Scripting.SCElem.*;

/**
 *
 * @author  Jhilmil
 * This class provides management operations for all methods supported by a
 * particular class.
 */
public class JPnlMethodMgr extends JPnlTemplateMgr
{
    public JPnlLineMethod[] codelets = new JPnlLineMethod[0]; //List of codelets

    PObject selectedPObj = null;

    /**
     *
     * @param aPnlScrpt Creates a method manager, with a reference to the scriptlet panel.
     */
    public JPnlMethodMgr(LayoutInfo l, JPnlScriptlet jpnlScriptlet)
    {
        super(l, jpnlScriptlet);
        setBackground(new Color(125,125,125));
    }

    ///
    // Base class overrides:
    ///

    @Override
    public void BindObject(PObject obj)
    {
	selectedPObj = obj;
	for(JPnlLineMethod codeletx:codelets)
            remove(codeletx);

        PMethod[] methods = obj.GetMethods();
	codelets = new JPnlLineMethod[methods.length];

        c.fill = GridBagConstraints.NONE;
        int num = 0;
	for(int i=0; i < methods.length; i++)
	{
          if(GUI.useCodeletDisplayOrder){
                
                c.gridy = methods[i].GetDisplayPos();
          }else{
                c.gridy = i;
          }
           
            JPnlLineMethod newCodelet = new JPnlLineMethod(
                    new PStatementMethod(methods[i]),
                    m_l
		);
            newCodelet.InitControls();
            newCodelet.SetUserInputsEnabled(false);
            add(newCodelet, c);
            codelets[i] = newCodelet;
	}
	doLayout();
	repaint();
	revalidate();
    }
}
