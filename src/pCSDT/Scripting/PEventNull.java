/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting;

import org.jdom.*;
import pCSDT.Scripting.SCElem.*;

/**
 * This is a null event.  It exists to root statement trees that are not bound to any
 * particular event.
 *
 * @author Jason Sanchez
 */
public class PEventNull extends PEvent
{
    public PEventNull(PEventList evtList, PStatement... args) {
        super(evtList, args);
    }

    /**
     * The GetXml routine is overridden, here, because this is a null event and needs a
     * special tag name.  Additionally, null events have no name, so we don't add that
     * attribute on.
     * @return An XML element representing this event
     */
    @Override
    public Element GetXml()
    {
	Element root = new Element("NullEvent");
	root.setAttribute("x", String.valueOf(m_coords.x));
	root.setAttribute("y", String.valueOf(m_coords.y));
        root.setAttribute("z", String.valueOf(m_coords.z));
	root.addContent(m_listener.GetXml("Listener"));
	return root;
    }

    @Override
    public boolean SetXml(PEngine context, Element elem)
    {
        Attribute x = elem.getAttribute("x");
	Attribute y = elem.getAttribute("y");
        Attribute z = elem.getAttribute("z");
	if(x != null && y != null)
            try
            {
                m_coords = new Point3D(
				Integer.parseInt(x.getValue()),
				Integer.parseInt(y.getValue()),
                                Integer.parseInt(z.getValue())
				);
            }
            catch(Exception e) {m_coords = null;}
	else
            m_coords = null;

	for(Object i : elem.getChildren())
        {
            Element statement;
            if(i instanceof Element)
                statement = (Element)i;
            else continue;

            PStatementList l = new PStatementList();
            l.SetXml(context, statement);
            SetListener(l);
	}
	return true;
    }

    @Override
    public JPnlLineEventNull GetGui(LayoutInfo l)
    {
	return new JPnlLineEventNull(this, l);
    }

    @Override
    public PEventNull clone()
    {
	PEventNull obj = new PEventNull(m_evtList);
	obj.SetListener(m_listener.clone());
        return obj;
    }

    @Override
    public String toString() {return "";}
}
