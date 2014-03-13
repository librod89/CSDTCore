/*
 * JPnlEventMgr.java
 *
 * Created on March 1, 2009, 12:16 PM
 */

package pCSDT.Presentation;
import java.awt.*;
import java.awt.event.MouseEvent;
import javax.swing.JLayeredPane;

import pCSDT.Scripting.*;
import pCSDT.Scripting.SCElem.*;

/**
 *
 * @author  Jason
 */
public class JPnlEventMgr extends JPnlTemplateMgr {
    /** Creates new form JPnlEventMgr */
    public JPnlEventMgr(LayoutInfo l, JPnlScriptlet jpnlScriptlet)
    {
	super(l, jpnlScriptlet);
        ////jpnlScriptlet.SetEventMgr(this);
        setBackground(new Color(125,125,125));
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.GridBagLayout());
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables


    ///
    // Base class overrides:
    ///
    @Override
    public void mouseReleased(MouseEvent e)
    {
        if(m_dragSource == null) {
            return;
        }
        JLayeredPane layered = getRootPane().getLayeredPane();
        layered.remove(m_dragSource.m_codelet);
	if(lastDrag != null)
	{
            lastDrag.StopDrag(GetRelativePoint(lastDrag, e), m_dragSource.m_codelet);
            lastDrag = null;
	}
        else {
            // put lineEvent back to EventMgr
            JPnlLine line = m_dragSource.m_codelet;
            if(line instanceof JPnlLineEvent)
            {
                JPnlLineEvent lineEvt = (JPnlLineEvent)line;
                // remove the (x,y) coordiates associated with the lineEvt
                // and deregister the PEvent from the PEventList
                lineEvt.GetPEvent().SetCoords(null);
                lineEvt.GetPEvent().ClearScript();
                lineEvt.GetPEvent().DeRegister();
                updateUI();
            }
        }
	m_dragSource = null;
	layered.repaint();
    }

    @Override
    public void SetUpDragSource(DragSource ds) {
        JPnlLine line = ds.m_codelet;
        if(line instanceof JPnlLineEvent)
	{
            JPnlLineEvent lineEvt = (JPnlLineEvent)line;
            // remove the (x,y) coordiates associated with the lineEvt
            lineEvt.GetPEvent().SetCoords(null);
            updateUI();
	}
    }

    @Override
    public void StopDrag(Point3D pt, JPnlLine line)
    {
        if(line instanceof JPnlLineEvent)
	{
            JPnlLineEvent lineEvt = (JPnlLineEvent)line;
            // remove the (x,y) coordiates associated with the lineEvt
            lineEvt.GetPEvent().SetCoords(null);
            // remove the (x,y) coordiates associated with the lineEvt
            // and deregister the PEvent from the PEventList
            lineEvt.ClearLineList();
            lineEvt.GetPEvent().ClearScript();
            lineEvt.GetPEvent().DeRegister();
            updateUI();
	}
    }

    @Override
    public void BindObject(PObject obj)
    {
        // Reset all children:
	removeAll();

        if(obj == null)
            return;

        c.gridy = 0;
        c.fill = GridBagConstraints.NONE;

        for (PEventList evtList: obj.GetEventLists()) {
            JPnlLineEvent lEvt = new JPnlLineEvent(evtList.GetTemplateEvent().clone(), m_l);
            add(lEvt, c);
            lEvt.InitControlsCompact();
            c.gridy++;
        }
        updateUI();
    }
}
