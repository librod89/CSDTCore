/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting;

import org.jdom.Element;

/**
 * This represents the list of PEventNull
 * @author tylau
 */
public class PEventListNull extends PEventList {
    public PEventListNull(PObject obj) {
        super(obj);
    }


    /**
     * Obtain the XML representation of this PEventList
     * @return
     */
    @Override
    public Element GetXml()
    {
	Element root = new Element("EventListNull");
	////root.setAttribute("name", m_ub.GetName());
        for (PEvent e: pEvts) {
            root.addContent(e.GetXml());
        }
	return root;
    }

    @Override
    public boolean SetXml(PEngine context, Element elem)
    {
	for(Object i : elem.getChildren())
        {
            Element evt;
            if(i instanceof Element)
                evt = (Element)i;
            else continue;

            PEvent e = new PEvent(this);
            e.SetXml(context, evt);
            AddPEvent(e);
	}
	return true;
    }
}
