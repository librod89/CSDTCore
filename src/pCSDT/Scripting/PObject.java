/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting;

import java.awt.geom.Rectangle2D;
import java.lang.reflect.*;
import java.lang.annotation.*;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Element;
import pCSDT.Scripting.Annotations.*;
import pCSDT.Utility;

/**
 *
 * @author Jason
 */
@AutomatableClass(name="Object", desc="")
public abstract class PObject implements Comparable<PObject> {

    static int count = 0;

    // Naming convention, and default name:
    String namingConvention = "object";
    String classDesc = "";

    // The parent engine:
    protected PEngine m_pEngine = null;

    // Reset dictionary.  This dictionary is filled before any Start events are fired,
    // and is updated each time this happens.
    HashMap<PProperty, PVariant> m_valSet = null;

    // Name, description, and local object constructor:
    @AutomatableProperty(name="Name", desc="Name of the object", RunTimeBehavior="H")
    public String m_name;
    @AutomatableProperty(name="Description", desc="Description of the object", RunTimeBehavior="H")
    public String m_desc;
    /*
    @AutomatableProperty(name="Last Caller", desc="Name of the last caller", DesignTimeBehavior="H", RunTimeBehavior="R")
    public String m_callerName = "";
     *
     */
    // record the time elapsed in simulation world time
    @AutomatableProperty(name="Time elapsed", desc="Time elapsed ", DesignTimeBehavior="H", RunTimeBehavior="R")
    public float timeElapsed = 0;
    @AutomatableProperty(name="Draw Order", desc="Draw order", DesignTimeBehavior="H", RunTimeBehavior="H")
    public int drawingOrder = -1;

    PEventList eDoForeverEventList; // = new PEventList(this);
    PEventListNull m_nullEvents; // = new PEventListNull(this);
    
    boolean isMouseOver = false;  // tell if a mouse is currently over it

    PScopeStack stk = new PScopeStack();  // scope starts at PObject level

    // <editor-fold defaultstate="collapsed" desc="Accessor/mutator methods">
    public PEngine GetPEngine() {return m_pEngine;}
    public String GetName() {return m_name;}
    public String GetDesc() {return m_desc;}
    public String GetClassName() {return getClass().getName();}
    public String GetNamingConvention() {return namingConvention;}
    public String GetClassDesc() {return classDesc;}
    public boolean IsSelected() {return m_selected;}

    public void SetSelected(boolean selected) {m_selected = selected;}
    public void SetEngine(PEngine pEngine) {m_pEngine = pEngine;}
    public void SetNamingConvention(String name) {namingConvention = name;}
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Reflection operations and basic accessors">
    // Stored methods and events:
    public PMethod[] m_Methods = null;
    public PEventList[] m_EventLists = null;
    public PProperty[] m_Properties = null;

    // Static singleton maps for unbound methods and events:
    static HashMap s_methods = new HashMap();
    static HashMap s_eventLists = new HashMap();
    static HashMap s_properties = new HashMap();

    public PObject()
    {
	this("");
    }

    public PObject(String desc)
    {
	this(Utility.MakeName(), desc);
    }

    public PObject(String name, String desc)
    {
        Class c = getClass();

        // Initialize all of our explicit stuff:
	if(name.isEmpty())
            name = Utility.MakeName();
        m_name = name;
        m_desc = desc;

        // Do the same with the properties:
        PPropertyUnbound[] ubProps = GetProperties(c);
        m_Properties = new PProperty[ubProps.length];
        for(int i = 0; i < ubProps.length; i++)
            m_Properties[i] = new PProperty(ubProps[i], this);

        // Do the same with eventlist:
        PEventListUnbound[] e = GetEventLists(c);
        m_EventLists = new PEventList[e.length];
        for (int i = 0; i < e.length; i++) {
            m_EventLists[i] = new PEventList(e[i], this);
        }
        
        // Lastly, construct some bound methods:
        PMethodUnbound[] m = GetMethods(c);
        m_Methods = new PMethod[m.length];
        for(int i = 0; i < m.length; i++)
            m_Methods[i] = new PMethod(m[i], this);

        count++;

        // init all events here
        m_beginEventList = new PEventList(this);

        /*
        m_onCallEventList = new PEventList(this);
        */
        
        m_onReceiveMessageList = new PEventList(this);
        m_onReceiveMessageList.AttachInvokeVerifier(new PEventInvokeVerifierOnReceiveMessage());

        m_onReceiveMessageFromList = new PEventList(this);
        m_onReceiveMessageFromList.AttachInvokeVerifier(new PEventInvokeVerifierOnReceiveMessage());

        eDoForeverEventList = new PEventList(this);
        // fill eDoForeverEventList with exactly one event
        eDoForeverEventList.AddPEvent(new PEvent(eDoForeverEventList));

        m_nullEvents = new PEventListNull(this);

        // put up the first stack at PObject level
        stk.Push();



    }

    public PObject(String name, String desc, PEngine pEngine)
    {
        this(name, desc);
        m_pEngine = pEngine;
    }

    /**
     * To be called by some core methods, to initialize the PObject after all
     * attributes are loaded.
     * Please override if needed
     */
    public void DeferredInitialize() {}

    /**
     *  Built-in initialization event
     */
    @AutomatableEventList(name="On Begin", desc="Fired when the animation begins to play")
    public PEventList m_beginEventList;

    /**
     *  Built-in onCall event
     */
    /*
    @AutomatableEventList(name="On Being Called", desc="Fired when some object call the Launch() codelet with its name as parameter")
    public PEventList m_onCallEventList;
    */
    
    /**
     * Built-in onReceiveMessage event
     */
    @AutomatableEventList(name="On Get Msg", desc="Fired when receiving a message", argNames={""}, argDesc={""}, argTypes={"String"})
    public PEventList m_onReceiveMessageList;

    @AutomatableEventList(name="On Get Msg From", desc="Fired when receiving a message from a specified object", argNames={"msg","from"}, argDesc={"message", "object"}, argTypes={"String", "PObject"}, argVals={"", "@ValueListObjectNames"})
    public PEventList m_onReceiveMessageFromList;

    public int GetCount() {
        return count;
    }
    
    /**
     * @return The initialization event for the entire engine class
     */
    public PEventList GetBeginEventList() {return m_beginEventList;}

    /**
     * @return The OnCall event for the entire engine class
     */
    /*
    public PEventList GetOnCallEventList() {return m_onCallEventList; }
    */

    /**
     * 
     * @return A new object of the descendant type of this class
     * @throws java.lang.InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalAccessException
     * @throws ClassCastException
     */
    private static PObject Construct(Class clazz, String name, String desc) throws
            InstantiationException,
            IllegalAccessException,
            InvocationTargetException,
            ClassCastException
    {
        // Make sure the descendant makes sense
        if(!PObject.class.isAssignableFrom(clazz))
            throw new ClassCastException("The passed class MUST inherit from PObject");
        
        try
        {
            PObject pObj = (PObject)clazz.getConstructor(String.class, String.class).newInstance(name, desc);
            Annotation[] annotations = clazz.getAnnotations();
            for (Annotation annotation: annotations) {
                AutomatableClass myAnnotation = (AutomatableClass)annotation;
                pObj.namingConvention = myAnnotation.name();
                pObj.classDesc = myAnnotation.desc();
            }
            // copy class desc if m_desc is empty
            if (pObj.m_desc == null || pObj.m_desc.equals(""))
                pObj.m_desc = pObj.classDesc;
            return pObj;
        }
        catch(InstantiationException e)
        {
            if(e.getCause() instanceof NoSuchMethodException)
                // Indicates that the constructor junked up--
                // Bad sources/method names, etc
                return null;
            throw e;
        }
        catch(NoSuchMethodException e)
        {
            System.out.print("Construction of class ");
            System.out.print(clazz.getName());
            System.out.println(" failed; could not find constructor of arity 2");
            return null;
        }
    }

    public static PObject Construct(Class clazz, String name, String desc,
                                    PEngine pEngine) throws
            InstantiationException,
            IllegalAccessException,
            InvocationTargetException,
            ClassCastException
    {
        PObject obj = PObject.Construct(clazz, name, desc);
        obj.m_pEngine = pEngine;
        return obj;
    }

    /**
     * Get the PScopeStack of this object
     * @return the PScopeStack of this object
     */
    public PScopeStack GetScope() {
        return stk;
    }
    
    /**
     * Recovers a list of unbound automatable methods from the given class type
     * @param c The class time from which to recover the methods
     * @return A list of unbound method objects
     */
    public static PMethodUnbound[] GetMethods(Class c)
    {
        PMethodUnbound[] retVal = (PMethodUnbound[])s_methods.get(c);
        if(retVal != null)
            return retVal;
        
        // Construct the events:
        Method[] m = c.getMethods();
        ArrayList<PMethodUnbound> rsList = new ArrayList<PMethodUnbound>();
        for(int i = m.length; i-- != 0;)
        {
            Method cur = m[i];
            
            AutomatableMethod anno = (AutomatableMethod)cur.getAnnotation(AutomatableMethod.class);
            if(anno == null)
                continue;
            
            if(anno.name().isEmpty())
                rsList.add(new PMethodUnbound(cur.getName(), cur));
            else
                rsList.add(new PMethodUnbound(anno.name(), cur));
        }
        Collections.reverse(rsList);
        retVal = new PMethodUnbound[rsList.size()];
        for(int i = 0; i < rsList.size(); i++)
            retVal[i] = rsList.get(i);
        s_methods.put(c, retVal);
        return retVal;
    }
    
    /**
     * Recovers a list of unbound automatable events from the given class type
     * @param c The class time from which to recover the events
     * @return A list of unbound event objects
     */
    public static PEventListUnbound[] GetEventLists(Class c)
    {
        PEventListUnbound[] retVal = (PEventListUnbound[])s_eventLists.get(c);
        if(retVal != null)
            return retVal;

        // Construct the events:
        ArrayList<PEventListUnbound> rsList = new ArrayList<PEventListUnbound>();

        for (Class curC = c; curC != null; curC = curC.getSuperclass()) {
            Field[] f = curC.getDeclaredFields();
            ArrayList<PEventListUnbound> rsSubList = new ArrayList<PEventListUnbound>();
            for (int i = f.length; i-- != 0;) {
                Field cur = f[i];
                AutomatableEventList anno = (AutomatableEventList)cur.getAnnotation(AutomatableEventList.class);
                if (anno == null)
                    // without the annotation, we do not care about the type
                    continue;
                PEventListUnbound ub = new PEventListUnbound(cur, anno);
                if(cur.getAnnotation(DefaultEventList.class) != null)
                    ub.SetDefault(true);
                // Passed all tests, make a new unbound event
                rsSubList.add(ub);
            }
            Collections.reverse(rsSubList);
            rsList.addAll(rsSubList);
        }

        retVal = new PEventListUnbound[rsList.size()];
        for(int i = 0; i < rsList.size(); i++)
            retVal[i] = rsList.get(i);
        s_eventLists.put(c, retVal);
        return retVal;
    }
    
    /**
     * Recovers a list of unbound automatable properties from the given class type
     * @param c The class time from which to recover the properties
     * @return A list of unbound property objects
     */
    public static PPropertyUnbound[] GetProperties(Class c)
    {
        PPropertyUnbound[] retVal = (PPropertyUnbound[])s_properties.get(c);
        if(retVal != null)
            return retVal;

        // Construct the events:
        ArrayList<PPropertyUnbound> rsList = new ArrayList<PPropertyUnbound>();

        for(Class curC = c; curC != null; curC = curC.getSuperclass())
        {
            Field[] f = curC.getDeclaredFields();

            ArrayList<PPropertyUnbound> rsSubList = new ArrayList<PPropertyUnbound>();
            for(int i = f.length; i-- != 0;)
            {
                Field cur = f[i];
                int modifiers = cur.getModifiers();
                // if it is not public field, ignore
                if (!Modifier.isPublic(modifiers)) {
                    continue;
                }
                AutomatableProperty anno = (AutomatableProperty)cur.getAnnotation(AutomatableProperty.class);
                if(anno == null)
                    // Without the annotation, we do not care about the type.
                    continue;
                try
                {
                    // Can we change into a PType?
                    PType t = PType.FromClass(cur.getType());
                    rsSubList.add(new PPropertyUnbound(t, cur, anno));
                }
                catch(Exception e) {e.printStackTrace();}
            }
            Collections.reverse(rsSubList);
            rsList.addAll(rsSubList);
        }

        retVal = new PPropertyUnbound[rsList.size()];
        for(int i = 0; i < rsList.size(); i++)
            retVal[i] = rsList.get(i);
        s_properties.put(c, retVal);
        return retVal;
    }

    public PProperty GetProperty(String name)
    {
        PProperty[] props = GetProperties();
        for(int i = 0; i < props.length; i++)
            if(props[i].GetUnboundProperty().GetName().compareTo(name) == 0) {
                ////props[i].m_obj = this;
                return props[i];
            }
        return null;
    }

    public PEventList GetEventList(String name)
    {
        PEventList[] evtLists = GetEventLists();
        for(int i = 0; i < evtLists.length; i++)
            if(evtLists[i].GetUnboundEventList().GetName().compareTo(name) == 0)
                return evtLists[i];
        return null;
    }

    public PMethod GetMethod(String name)
    {
        PMethod[] methods = GetMethods();
        for(int i = 0; i < methods.length; i++)
            if(methods[i].m_name.compareTo(name) == 0)
                return methods[i];
        return null;
    }

    /**
     *
     * @return An array of methods bound to this object
     */
    public final PMethod[] GetMethods() {return m_Methods;}

    /**
     *
     * @return An array of events bound to this object
     */
    public PEventList[] GetEventLists()
    {
        ////if(m_EventLists == null)
        if(true)
        {
            // Iterate through all the PEventUnbound's for this class, binding the
            // corresponding events as we go, and setting up the Events list, too:
            PEventListUnbound[] ubEventLists = GetEventLists(getClass());
            m_EventLists = new PEventList[ubEventLists.length];
            for(int i = 0; i < ubEventLists.length; i++)
            {
                PEventListUnbound cur = ubEventLists[i];
                try
                {
                    m_EventLists[i] = (PEventList)cur.Get(this);
                    m_EventLists[i].SetUbEventList(cur);
                }
                catch(Exception e) {m_EventLists[i] = null;}
            }
        }
        return m_EventLists;
    }

    /**
     *
     * @return An array of properties bound to this object
     */
    public PProperty[] GetProperties() {return m_Properties;}
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Pick handlers and spatial positioners">
	// Selection flag, true if the object is selected
	boolean m_selected;

    /**
     * Attempts to determine if the given x and y coordinates fall within the
     * object's bounds.  The coordinates are specified relative to GetPosition3 in the
     * rotation specified by GetRotation.  By default this method will return a miss.
     * @param m The object-relative, rotated position of the mouse
     * @param c The direction the user is looking (view vector)
     * @return A hit test enumeration member indicating whether a hit succeeded
     */
    public HTResult HitTest(Vector3 m, Vector3 c)
    {
	return HTResult.Miss;
    }

    /**
     *
     * @return The position of this object in 3-space
     * A convenience method that wraps GetX, GetY, and GetZ in a Vector3 for
     * use with matrix operations.
     */
    public final Vector3 GetPosition3()
    {
        return new Vector3(GetX(), GetY(), GetZ());
    }

    /**
     *
     * @return The x-position of this object
     */
    public double GetX() {return 0;}

    /**
     *
     * @return The y-position of this object
     */
    public double GetY() {return 0;}

    /**
     *
     * @return The z-position of this object
     */
    public double GetZ() {return 0;}

    /**
     * @return The bounding box for this object, in the same coordinate system as the
     * returned X,Y,Z coordinates
     */
    public Rectangle2D.Double GetBoundingBox()
    {
	Rectangle2D.Double retVal = new Rectangle2D.Double();
	retVal.x = 0;
	retVal.y = 0;
	retVal.width = 0;
	retVal.height = 0;
	return retVal;
    }

    /**
     *
     * @return The rotation of the object, in 3-space
     */
    public Rotation3 GetRotation() {return Rotation3.I;}

    /**
     *
     * @return True if this object has a location
     */
    boolean HasLocation() {return true;}
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Iteration and automation methods">
    /**
     * Steps the object's implementation forward by one time unit.
     * @param dt The amount of time that has passed, in seconds.
     */
    public void Step(double dt) {
        try {
            eDoForeverEventList.Invoke();
        }
        catch (Exception e) {
        }
    };
    
    /**
     * Offers a way to reset any internal state information for the object.  By default,
     * Reset will attempt to invoke RestoreResetVals, which puts back the values of any
     * automatable properties.  If you need to override this method to supply your own
     * reset behavior, you should invoke the base method to ensure automatable properties
     * are reset--otherwise you will need to reset automatable properties yourself.
     */
    public void Reset()
    {
        // disable this func as it seems to avoid changes of automatable
        // properties after simulation is run once
        ////RestoreResetVals();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Sub-object elements">
    /**
     * Attaches a subobject on this object
     * @param obj The object to which to add--must have been constructed
     * The object that is added must have been constructed by one of the factory
     * methods returned by GetObjectMakers on an instance of a PObject.
     * @return True if the sub-object was accepted
     */
    public boolean AddSubObj(PObject obj) {return false;}
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Null event routines">
    public PEventNull CreateNullEvent()
    {
        PEventNull evt = new PEventNull(m_nullEvents);
        m_nullEvents.AddPEvent(evt);
        return evt;
    }

    public void RemoveNullEvent(PEventNull evt)
    {
        m_nullEvents.RemovePEvent(evt);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Event virtuals">
    /**
     * This event is fired by the framework when the user starts the simulation, but before
     * any user-defined codelets are executed
     */
    public void OnBegin() {}

    /**
     * This event is fired by the framework when the user stops the simulation
     */
    public void OnEnd() {
        // clear everything in eDoForever event
        for (PEvent e: eDoForeverEventList.GetPEvents()) {
            e.m_listener.ClearAll();
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Serialization support routines">
    /**
     *
     * @return The Xml element corresponding to this object
     */
    public Element GetXml()
    {
	Element root = new Element("Object");
	root.setAttribute("name", m_name);
	root.setAttribute("desc", m_desc);
	root.setAttribute("type", GetClassName());

	// Serialize all bound events:
	Element evtLists = new Element("EventLists");
	for(PEventList pEvtList : GetEventLists())
            evtLists.addContent(pEvtList.GetXml());
        Element nevtLists = new Element("NullEventLists");
	nevtLists.addContent(m_nullEvents.GetXml());
	root.addContent(evtLists);

	// Serialize all bound properties:
	Element props = new Element("Properties");
	for(PProperty pProp : GetProperties())
            props.addContent(pProp.GetXml());
	root.addContent(props);
	return root;
    }

    /**
     * Initializes this object from data specified in an Xml element
     * @param e The element from which data is to be loaded
     * @return True if the object was successfully set from the Xml source
     */
    public boolean SetXml(PEngine context, Element elem)
    {
	Element eventLists = elem.getChild("EventLists");
	if(eventLists != null)
	{
            // Deserialize all Event children first:
            for(Object j : eventLists.getChildren("EventList"))
            {
                Element eventList;
                if(j instanceof Element)
                    eventList = (Element)j;
		else continue;

		String name = eventList.getAttributeValue("name");
		if(name.isEmpty())
                    continue;
		PEventList pEvtList = GetEventList(name);
		if(pEvtList == null)
                    continue;
		pEvtList.SetXml(context, eventList);
            }

            // Now deserialize all null events:
            Object j = eventLists.getChild("NullEventList");
            Element nEventList;
            if(j instanceof Element) {
                nEventList = (Element)j;
                m_nullEvents.SetXml(context, nEventList);
            }
	}

	Element props = elem.getChild("Properties");
	if(props != null)
	{
            for(Object j : props.getChildren())
            {
                Element prop;
                if(j instanceof Element)
                    prop = (Element)j;
		else continue;

		String name = prop.getAttributeValue("name");
                if(name.isEmpty())
                    continue;
		PProperty pProp = GetProperty(name);
		if(pProp == null)
                    continue;
                
                //System.out.println(name); XML Error
		pProp.SetXml(prop);
                
            }
	}
	return true;
    }
    // </editor-fold>

    /**
     * This event is fired by the framework when the user Clears the Engine
     */
    public void OnClear() {}


    /**
     * This event is fired by the framework when the user Creates an object
     */
    public void OnCreate() {}

    /**
     *
     * @return A map of all of the properties and their current values
     */
    public HashMap<PProperty, PVariant> GetValueMap()
    {
        HashMap<PProperty, PVariant> retVal = new HashMap<PProperty, PVariant>();
        for(PProperty prop : GetProperties())
            retVal.put(prop, prop.GetValue());
        return retVal;
    }

    /**
     * Stores all of the current values in the reset map
     */
    public void StoreResetVals()
    {
        m_valSet = GetValueMap();
    }

    /**
     * Restores the values in the hash map
     */
    public void RestoreResetVals()
    {
	if(m_valSet == null) return;  /* added by msk */
	if(m_valSet.keySet() == null) return;  /* added by msk */

        for(PProperty p : m_valSet.keySet())
            try {p.SetValue(m_valSet.get(p));}
            catch(Exception e) {
                e.printStackTrace();
            }
    }

    /**
     * Provide validation of an attribute input
     * return null string if valid, some string otherwise
     * @param str
     * @param propName
     * @return
     */
    public String IsInputValid(String str, String propName) {
        return null;
    }

    public void SetIsMouseOver(boolean b) {
        isMouseOver = b;
    }

    public boolean IsMouseOver() {
        return isMouseOver;
    }

    public void SetTempName()
    {
        m_name = namingConvention + count;
    }

    @Override
    public PObject clone()
    {
        // Create the return object
	PObject pObject;
	try {
            pObject = PObject.Construct(getClass(), "", m_desc);
        }
	catch(Exception e) {
            e.printStackTrace();
            return null;
        }

        // Copy event statement trees.  No special handling is required for Begin, Call, or other
	// root object events as these will appear with all other user-defined events in m_Events.
	PEventList[] src = GetEventLists();
        PEventList[] dst = pObject.GetEventLists();
        for (int i = 0; i < src.length; i++)
            dst[i].CopyPEvents(src[i]);

	// Copy over the variants for property value.  These variants are guarenteed not to be
	// references to the real property value.
        // NOTE: except Name...
        for (int i = 0; i < m_Properties.length; i++) {
            try {
                pObject.m_Properties[i].SetValue(m_Properties[i].GetValue());
            }
            catch(Exception e)
            {
                // Any exception here would mean a serious error--that, somehow, two instances
                // of the same class have different property listings and/or types, or that one
                // property has a polymorphic primitive type.
                System.out.println("Error:  Class image appears to have changed during runtime; recompilation may be needed.");
                return null;
            }
        }
        pObject.SetTempName();
        // All done.
        return pObject;
    }
    
    ///
    // Base class overrides:
    ///
    @Override
    public String toString() {return m_name;}

    /*
    // default PObject metod
    // effective only if m_pEngine is properly assigned
    @AutomatableMethod(name = "Call Object", argNames={""}, argDesc={""}, argVals={"DefaultObj"})
    public void InvokePObject(String objName)
    {
        if (m_pEngine != null) {
            for(PObject pObj : m_pEngine.objs)
            {
                if (pObj.m_name.equals(objName)) {
                    try {
                        pObj.m_callerName = GetName();
                        pObj.m_onCallEventList.Invoke();
                        pObj.m_callerName = "";
                    }
                    catch (Exception e) {
                    }
                }
            }
            // also work on its PEngine
            if (m_pEngine.m_name.equals(objName)) {
                try {
                    m_pEngine.m_callerName = GetName();
                    m_pEngine.m_onCallEventList.Invoke();
                    m_pEngine.m_callerName = "";
                }
                catch (Exception e) {}
            }
        }
        else {
            try {
                m_callerName = GetName();
                m_onCallEventList.Invoke();
                m_callerName = "";
            }
            catch (Exception e) {}
        }
    }
     * 
     */

    @AutomatableMethod(displayPos = 21, name="Broadcast Message", argNames={""}, argDesc={""}, argVals={""})
    public void BroadcastMessage(String msg)
    {
        String myName = GetName();
        try {
            if (m_pEngine != null) {
                for (PObject pObj: m_pEngine.objs) {
                    pObj.m_onReceiveMessageList.Invoke(msg);
                    pObj.m_onReceiveMessageFromList.Invoke(msg, myName);
                }
                m_pEngine.m_onReceiveMessageList.Invoke(msg);
                m_pEngine.m_onReceiveMessageFromList.Invoke(msg, myName);
            }
            else {
                m_onReceiveMessageList.Invoke(msg);
                m_onReceiveMessageFromList.Invoke(msg, myName);
            }
        } catch (InvalidParameterException ex) {
            Logger.getLogger(PObject.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassCastException ex) {
            Logger.getLogger(PObject.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(PObject.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @AutomatableMethod(displayPos = 22, name="Send Message", argNames={"To", "Msg"}, argDesc={"recipient name", "message"}, argVals={"@ValueListObjectNames", ""})
    public void SendMessage(PObject to, String msg)
    {
        try {
            if (m_pEngine != null) {
                for (PObject pObj: m_pEngine.objs) {
                    if (pObj == to) {
                        pObj.m_onReceiveMessageList.Invoke(msg);
                        pObj.m_onReceiveMessageFromList.Invoke(msg, this);
                    }
                }
                if (m_pEngine == to) {
                    m_pEngine.m_onReceiveMessageList.Invoke(msg);
                    m_pEngine.m_onReceiveMessageFromList.Invoke(msg, this);
                }
            }
            else {
                if (this == to) {
                    m_onReceiveMessageList.Invoke(msg);
                    m_onReceiveMessageFromList.Invoke(msg, this);
                }
            }
        } catch (InvalidParameterException ex) {
            Logger.getLogger(PObject.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassCastException ex) {
            Logger.getLogger(PObject.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(PObject.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int compareTo(PObject o) {
        if (drawingOrder < o.drawingOrder) return 1;
        else if (drawingOrder > o.drawingOrder) return -1;
        else return 0;
    }
}
