/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Presentation;


/**
 *
 * @author Jason
 * This is the abstract base for all of the manager panels.  It's needed to
 * set up subpanels for all of the current Java classes, and also to select
 * one particular Java class when the time comes.
 */
public abstract class JPnlMgr extends javax.swing.JPanel
{
    public JPnlMgr()
    {
    }
    
    /**
     * This is called when a particular class's information is to be presented
     * to the user.  Note that this could be called with a PEngine, and maybe,
     * with instances of classes of disparate types.
     * @param objs The objects to be presented.
     */
    public abstract void BindObject(pCSDT.Scripting.PObject objs);
}
