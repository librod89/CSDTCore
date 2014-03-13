/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pCSDT.Scripting;

import java.security.InvalidParameterException;
import org.jdom.*;
import pCSDT.Scripting.SCElem.*;

/**
 *
 * @author Jason
 * This represents a single event exported by an IObject or an IEngine.  Events
 * are fired (invoked) in conditions set by the programmers.
 */
public class PEvent implements Comparable<PEvent>
{
    IStatementList m_listener = new PStatementList();
    PEventList m_evtList = null;  // the PEventList assoicated with this PEvent
    IStatement[] m_args;

    // Coordinates of this event, for representation purposes.  May be null.
    Point3D m_coords = null;

    /**
     * Constructs a new PEvent
     * @param name The name of this event
     * @param args Argument description of this event
     * Note that, once described, the types listed by args are binding.  Calls
     * to Invoke made after this are validated against the initial description,
     * and an exception is thrown if the arity or types don't match.
     *
     * The arguments are passed in from the framework consumer to script handers
     * that may reference these arguments.  PVariable is used, and not PType,
     * because the event arguments must have names that are exposed to the user.
     */
    public PEvent(PEventList evtList, IStatement... args)
    {
        m_evtList = evtList;

        PEventListUnbound elu = m_evtList.m_ub;

        if (elu == null) {
            m_args = new IStatement[0];
        }
        else {
            String[] argNames = elu.GetArgNames();
            PType[] argTypes = elu.GetArgTypes();
            String[] argVals = elu.GetArgVals();

            // if there are too many terms, throw an error
            if (argNames == null || argNames.length == 0) {
                m_args = new IStatement[0];
            }
            else if (argNames.length < args.length) {
                throw new IllegalArgumentException("Too many arguments");
            }
            else {
                m_args = new PStatement[argNames.length];
                if (argNames.length > args.length) {
                    System.arraycopy(args, 0, m_args, 0, args.length);
                    for (int i = args.length; i < m_args.length; i++) {
                        if (argNames.length == argVals.length) {
                            String trimArgVals = argVals[i].trim();
                            ePType type = argTypes[i].GetType();
                            m_args[i] = m_evtList.m_obj.GetPEngine().ProduceStatement(trimArgVals, type);
                        }
                        else {
                            m_args[i] = new PStatementConst("");
                        }
                    }
                }
                else {
                    System.arraycopy(args, 0, m_args, 0, args.length);
                }
            }
        }
    }
	
    public String GetName() {
        if (m_evtList != null && m_evtList.m_ub != null)
            return m_evtList.m_ub.GetName();
        return null;
    }

    public String GetDesc() {
        if (m_evtList != null && m_evtList.m_ub != null)
            return m_evtList.m_ub.GetDesc();
        return null;
    }

    /**
     * Get the argument list of this PEvent
     * @return the argument list in IStatement array
     */
    public IStatement[] GetArgs() {
        return m_args;
    }

    public void SetArg(int i, IStatement s) {
        m_args[i] = s;
    }

    public IStatement GetDefaultArg(int i) {
        return m_evtList.templateEvent.m_args[i].clone();
    }

    /**
     * Get the scope stack of this PEvent, which is the one possessed by the
     * parent object
     * @return the scope stack
     */
    public PScopeStack GetScopeStack() {
        return m_evtList.m_obj.stk;
    }

    /**
     * Update the event list of this PEvent
     * @param l the new PEventList to be associated
     */
    public void SetEventList(PEventList l) {
        // de-register from original event list
        DeRegister();
        // register to the new event list
        m_evtList = l;
        Register();
    }

    /**
     * Obtain the event list of this PEvent
     * @return the PEventList of this PEvent
     */
    public PEventList GetEventList() {
        return m_evtList;
    }

    /**
     * Return the PObject associated with this PEvent
     * @return the PObject associated with this PEvent
     */
    public PObject GetPObject() {
        if (m_evtList == null) {
            return null;
        }
        return m_evtList.m_obj;
    }
    
    /**
     * Change the association of the associated PStatments: if currently they 
     * are under null, switch them to be under newObj now
     * @param newObj
     */
    public void AssociateNullIdentityMethodTo(PObject newObj)
    {
	m_listener.AssociateNullIdentityMethodTo(newObj);
    }

    /**
     * Register itself to the parent event list
     */
    public void Register() {
        m_evtList.AddPEvent(this);
    }

    /**
     * De-register itself from the parent event list
     */
    public void DeRegister() {
        m_evtList.RemovePEvent(this);
    }

    /**
     * Events have a coordinate that must be stored with the event in order to allow the event
     * to be absolutely positioned within a control.  This coordinate allows support for that
     * operation.
     * @return The coordinate where the event should be stored
     */
    public Point3D GetCoords()
    {
	return m_coords;
    }

    public void SetCoords(Point3D pt)
    {
	m_coords = pt;
        // dirty the event list whenever its PEvent gets coordinate update
        m_evtList.bDirty = true;
    }

    /**
     * Sets this event's listener.
     * @param statement The statement to execute when this event is fired.
     */
    public void SetListener(IStatementList listener)
    {
	assert listener != null;
	m_listener = listener;
    }

    /**
     *
     * @return The array of all listeners for this object
     */
    public IStatementList GetListener()
    {
	assert m_listener != null;
	return m_listener;
    }

    /**
     * Copies the listener from the passed event, and assigns it to this listener
     * @param evt The event whose listener is to be copied
     */
    public void CopyListener(PEvent evt)
    {
	IStatementList l = evt.GetListener();
	if(l == null)
            return;
	SetListener(l.clone());
    }

    /**
     * @return True if there are no children bound to this event
     */
    public boolean IsEmpty()
    {
        return m_listener == null || m_listener.GetChildren().length == 0;
    }

    /**
     * Invokes the event
     * @param params The parameters of this invocation; passed by PEngine
     * @throws InvalidParameterException if the arity of the passed arguments
     *  does not match the expected arity
     * @throws ClassCastException If there are any types that cannot be cast
     *  to the types that the underlying event expects
     * @throws Exception If something went wrong with the execution of one of
     *  the scripts bound on this event.
     *
     * As this method is intended to be called from the consumer application,
     * it is provided in a manner that enables automatic boxing and binding of
     * native types to the CSDT framework types.  The system iterates through
     * the list of parameters one at a time, matching up types and variable
     * names accordingly.  If the types aren't exactly what is described by the
     *
     */
    public void Invoke(Object... params) throws
			InvalidParameterException,
			ClassCastException,
			Exception
    {
        // Arity verification:
	////if(params != null && m_argTypes != null && params.length != m_argTypes.length) {
        ////    throw new InvalidParameterException();
        ////}

        // I don't think it is needed.
        // parameters should be from m_args

        /*
        if(params != null && m_args != null && params.length != m_args.length) {
            System.out.println("params.length: " + params.length);
            System.out.println("m_args.length: " + m_args.length);
            throw new InvalidParameterException();
        }
         * 
         */


        // I don't think the above has ever used
        /*
	// Begin converting/binding our parameters:
	ArrayList<PVariable> boundArgs = new ArrayList<PVariable>(params.length);
        
        for(int i = 0; i < params.length; i++)
	{
            PVariable rSrcVar = m_argTypes[i];
            PVariable rCurVar = new PVariable(rSrcVar.GetName(), new PVariant(params[i]));

            // Verify that the types match...
            if(rCurVar.GetType() != rSrcVar.GetType())
                throw new ClassCastException();

            // ...and assign our bound args slot.
            boundArgs.add(rCurVar);
	}
         *
         */

        // check whether this specific PEvent instance should be fired with
        // PEventInvokeVerifiers of PEvent
        for (IEventInvokeVerifier v: GetEventList().pInvokeVerifiers) {
            if (!v.VerifyInvoke(this, params)) return;
        }
	// Now that all of our parameters have been converted, validated,
	// and bound, we can start executing the statements that have been
	// registered to fire when this event is invoked.
        m_listener.AssociateNullIdentityMethodTo(m_evtList.m_obj);
        
	PScopeStack stk = m_evtList.m_obj.stk;  // obtain the stack from the parent PObject

        try
	{
            m_listener.Execute(stk);
	}
	catch(IllegalArgumentException e)
	{
	}
    }

    public Element GetXml()
    {
	Element root = new Element("Event");
	root.setAttribute("name", m_evtList.m_ub.GetName());
	if(m_coords != null)
	{
            root.setAttribute("x", String.valueOf(m_coords.x));
            root.setAttribute("y", String.valueOf(m_coords.y));
            root.setAttribute("z", String.valueOf(m_coords.z));
	}
        for (IStatement s: m_args) {
            root.addContent(s.GetXml("Arg"));
        }
	root.addContent(m_listener.GetXml("Listener"));
	return root;
    }

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

        int i = 0;
        for(Object obj : elem.getChildren())
        {
            Element statement;
            if(obj instanceof Element)
                statement = (Element)obj;
            else continue;

            String tagName = statement.getName();
            if (tagName.equals("Listener")) {
                PStatementList l = new PStatementList();
                l.SetXml(context, statement);
                SetListener(l);
            }
            else if (tagName.equals("Arg")) {
                IStatement p = PStatement.FromXml(context, statement);
                if (p == null) return false;
                m_args[i++] = p;
            }
	}
	return true;
    }

    ///
    // Get various stuff from the associated PEventList
    ///
    public String[] GetArgNames()
    {
        if(m_evtList == null || m_evtList.m_ub == null)
            return null;
	return m_evtList.m_ub.GetArgNames();
    }

    public String[] GetArgDesc()
    {
        if(m_evtList == null || m_evtList.m_ub == null)
            return null;
	return m_evtList.m_ub.GetArgDesc();
    }

    public PType[] GetArgTypes()
    {
        if(m_evtList == null || m_evtList.m_ub == null)
            return null;
	return m_evtList.m_ub.GetArgTypes();
    }

    public String[] GetArgVals()
    {
        if(m_evtList == null || m_evtList.m_ub == null)
            return null;
	return m_evtList.m_ub.GetArgDesc();
    }

    ///
    // Base class overrides:
    ///
    @Override
    public String toString()
    {
	if(m_evtList == null || m_evtList.m_ub == null)
            return "";
	return m_evtList.m_ub.toString();
    }

    /**
     * Clear the script (IStatementList) associated with this event
     */
    public void ClearScript()
    {
        m_listener = new PStatementList();
	m_coords = null;
    }

    /**
     * Returns the GUI for this event
     * @param l The layout information for the event
     * @return
     */
    public JPnlLineEvent GetGui(LayoutInfo l)
    {
	return new JPnlLineEvent(this, l);
    }

    /**
     * duplicate a PEvent
     * @return
     */
    @Override
    public PEvent clone()
    {
        ////PEvent evt = new PEvent(m_evtList, m_argTypes);
        IStatement[] clonedArgs = null;
        if (m_args != null) clonedArgs = new IStatement[m_args.length];
        for (int i = 0; i < clonedArgs.length; i++) {
            clonedArgs[i] = m_args[i].clone();
        }
        PEvent evt = new PEvent(m_evtList, clonedArgs);
	evt.SetListener(m_listener.clone());
        ////evt.Register();
        if (m_coords != null) {
            evt.m_coords = (Point3D)m_coords.clone();
        }
        return evt;
    }

    public int compareTo(PEvent evt) {
        // compare y first
        if (m_coords.y < evt.m_coords.y) {
            return -1;
        }
        else if (m_coords.y > evt.m_coords.y) {
            return 1;
        }
        // then x
        if (m_coords.x < evt.m_coords.x) {
            return -1;
        }
        else if (m_coords.x > evt.m_coords.x) {
            return 1;
        }
        // then z
        if (m_coords.z < evt.m_coords.z) {
            return -1;
        }
        else if (m_coords.z > evt.m_coords.z) {
            return 1;
        }
        return 0;
    }

    public boolean CheckAgainst(IEventInvokeVerifier v) {
        return v.VerifyInvoke(this);
    }
}
