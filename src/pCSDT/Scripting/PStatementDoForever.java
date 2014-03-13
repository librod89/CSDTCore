/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting;

import org.jdom.Element;
import pCSDT.Scripting.SCElem.*;

/**
 * This contains a list of statements that are to be called in every simulation
 * step.
 * @author tylau
 */
public class PStatementDoForever extends PStatementControl {

    public PStatementDoForever()
    {
        this(new PStatementList());
    }

    public PStatementDoForever(IStatementList body)
    {
        super(PType.Void);
        args = new PStatement[1];
        args[0] = body;
    }

    @Override
    public PStatementDoForever clone() {
        return new PStatementDoForever(GetBodies()[0].clone());
    }

    @Override
    public IStatement GetCondition() {
        return null;
    }

    @Override
    public void SetCondition(IStatement s) {
        return;  // no condition to be set
    }

    @Override
    public IStatementList[] GetBodies() {
        return new IStatementList[]{(IStatementList)args[0]};
    }

    @Override
    public void SetBody(int i, IStatement s) {
        if (i == 0) {
            args[0] = s;
        }
    }

    public Element GetXml(String tagName) {
        Element root = new Element(tagName);
        root.setAttribute("type", "do-forever");
        root.addContent(args[0].GetXml("Do"));
        return root;
    }

    public boolean SetXml(PEngine context, Element elem) {
        Element list = elem.getChild("Do");
        if (list == null) {
            return false;
        }
        args[0] = PStatement.FromXml(context, list);
        return args[0] != null;
    }

    public PVariant Execute(PScopeStack scope) throws Exception {
        // copy its statement list to the owner PObject
        // locate the parent PEvent
        // create an event in eDoForeverEventList if not yet created
        if (m_obj.eDoForeverEventList.GetPEvents() == null || m_obj.eDoForeverEventList.GetPEvents().length == 0) {
            m_obj.eDoForeverEventList.AddPEvent(new PEvent(m_obj.eDoForeverEventList));
        }
        if (m_obj.eDoForeverEventList.GetPEvents() != null && m_obj.eDoForeverEventList.GetPEvents().length == 1) {
            m_obj.eDoForeverEventList.GetPEvents()[0].m_listener.AppendChild(args[0]);
            m_obj.eDoForeverEventList.GetPEvents()[0].m_listener.AssociateNullIdentityMethodTo(m_obj);
        }
        // early termination of event execution here
        return PVariant.Void;
    }

    public JPnlLine GetGui(LayoutInfo info) {
        return new JPnlLineDoForever(this, info);
    }

}
