/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import org.jdom.*;

/**
 * This holds a list of PEvent of the same kind.
 * When it is triggered, all PEvent associated with this list will be triggered.
 * @author tylau
 */
public class PEventList {
    PObject m_obj = null;  // the PObject associated with the PObject
    PEvent templateEvent = null;  // template PEvent
    ArrayList<PEvent> pEvts = new ArrayList<PEvent>(0);  // the list of PEvent instances it is to trigger
    boolean bDirty = false;  // indicate whether pEvts needs to be sorted before accessing its elements

    PEventListUnbound m_ub = null;

    // the list of Invoke Verifier for its PEvent
    ArrayList<IEventInvokeVerifier> pInvokeVerifiers = new ArrayList<IEventInvokeVerifier>(0);

    public PEventList(PObject obj) {
        m_obj = obj;
    }

    public PEventList(PEventListUnbound ub, PObject obj) {
        m_ub = ub;
        m_obj = obj;
    }

    // Accessor methods:
    public PEventListUnbound GetUnboundEventList()
    {
	return m_ub;
    }

    /**
     * Attaches the unbound event to this PEvent.  There is typically no need
     * for a consumer to call this method directly, as the base PObject class
     * will locate and bind PEvent members to their PEventUnbound counterparts
     * as necessary, so long as they are annotated with PropertyEvent
     * @param ub
     */
    public void SetUbEventList(PEventListUnbound ub)
    {
	m_ub = ub;
    }

    /**
     * Get a copy of its template PEvent
     * @return a copy of the template PEvent
     */
    public PEvent GetTemplateEvent() {
        if (templateEvent == null)
            templateEvent = new PEvent(this);
        return templateEvent;
    }

    /**
     * Insert an PEvent to the list
     * @param evt PEvent to be inserted
     * @return whether evt is added by this method call
     */
    public boolean AddPEvent(PEvent evt) {
        if (!pEvts.contains(evt)) {
            pEvts.add(evt);
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Deregister a PEvent from this list
     * @param evt PEvent to be removed
     * @return whether evt is removed from the list by this method call
     */
    public boolean RemovePEvent(PEvent evt) {
        return pEvts.remove(evt);
    }

    public PEvent[] GetPEvents() {
        PEvent[] list = new PEvent[pEvts.size()];
        pEvts.toArray(list);
        return list;
    }

    /**
     * Copy the set of PEvents from another PEventList
     */
    public void CopyPEvents(PEventList l) {
        // make sure the list is clean before
        pEvts.clear();
        PEvent[] list = l.GetPEvents();
        if (list.length == 0) {
            return;
        }
        for (PEvent e: list) {
            PEvent evt = e.clone();
            // update associated event list
            evt.SetEventList(this);
        }
    }

    // addition and removal of invoke verifiers
    public void AttachInvokeVerifier(IEventInvokeVerifier v) {
        pInvokeVerifiers.add(v);
    }

    public boolean DetachInvokeVerifier(IEventInvokeVerifier v) {
        return pInvokeVerifiers.remove(v);
    }

    public void ClearInvokeVerifiers() {
        pInvokeVerifiers.clear();
    }

    /**
     * Associate null identify methods in the PEvents' scripts to a particular
     * object
     * @param newObj
     */
    public void AssociateNullIdentityMethodTo(PObject newObj)
    {
        for (PEvent e: pEvts) {
            e.GetListener().AssociateNullIdentityMethodTo(newObj);
        }
    }

    /**
     * Invoke the PEvents registered to this list
     * @param params
     */
    public void Invoke(Object... params) throws InvalidParameterException,
            ClassCastException, Exception {
        // sort the list according to m_coords of the pEvent
        if (bDirty) {
            Collections.sort(pEvts);
            bDirty = false;
        }
        for (PEvent e: pEvts) {
            e.Invoke(params);
        }
    }

    /**
     * Clear all the scripts associated with all PEvents in this list
     */
    public void ClearScript()
    {
        for (PEvent e: pEvts) {
            e.ClearScript();
        }
    }

    /**
     * Obtain the XML representation of this PEventList
     * @return
     */
    public Element GetXml()
    {
	Element root = new Element("EventList");
	root.setAttribute("name", m_ub.GetName());
        for (PEvent e: pEvts) {
            root.addContent(e.GetXml());
        }
	return root;
    }

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
