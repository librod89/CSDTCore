/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting;

import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.*;
import org.jdom.output.XMLOutputter;
import org.jdom.input.SAXBuilder;
import java.io.*;
import java.util.*;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import java.net.*;
import javax.imageio.ImageIO;
import org.apache.commons.codec.binary.Base64;
import pCSDT.Presentation.GUI;
import pCSDT.Presentation.GUI.eRenderState;
import pCSDT.Presentation.JPnlObjMgr;
import pCSDT.Utility;
import java.util.StringTokenizer;

/**
 *
 * @author Jason
 * This interface is implemented by the engine base class for any consumer.
 * It is used to manage state for an entire instance, describe all the base
 * objects that are supported by the engine, and provide a common way of
 * animating the entire scene.
 * 
 * Now, this class does inherit from PObject.  The reason is that it shares a
 * lot in common with PObject.  It's got events, methods, and optionally,
 * properties--so much of the backing machinery for reflecting these types is
 * exactly the same.  Technically the engine itself could be treated as a
 * first-class object, but as there is no language construct provided to the
 * CSDT GUI to get a reference to the engine, this case need not be considered.
 */
public abstract class PEngine extends PObject {

    ////@AutomatableProperty(name="Uniscale", desc="Uniform scale", DesignTimeBehavior="A", RunTimeBehavior="H")
    ////public float m_uniscale = 1f / 15f;
    
    public static URL codebaseURL = null;
    
    /*
     * Store image saved by user in xml file
     */
    protected String objImg_base64 = "";
    
    // record the project codename and version
    // to be filled by individual projects
    protected String codename = null;
    protected String version = null;
    
    protected static ArrayList<PObject> objsList;
    

    String j2seVersion = "1.5+";
    String[][] properties = {{"sun.java2d.noddraw", "true"}};
    String[][] jars = {};
    String[][] exts = {};
    
    protected String[][] graphicsJars;
    protected String[][] graphicsExts;
    protected String[][] graphicsOsSpecNativeLibs;
    protected String[][] appJars;
    protected String[][] appExts;

    protected String appletName;
    protected String appletMainClass;
    protected String appletWidth;
    protected String appletHeight;
    protected String appletSingleDemoXmlUrl;
    protected String appletDemoPropertiesUrl;
    protected String appletTemplateBackgroundPropertiesUrl;

    // default background color
    public String defaultBgColor = "255,255,255";
    // default background image file
    public String defaultBgFile = "";
    // filename for image of object
//    public static String objectImgSource = "";
    //Control which objectImgSource belongs to which plaits
    public static HashMap<PObject, String> image_map = new HashMap<PObject, String>();

    public static HashMap<PObject, Boolean> default_map = new HashMap<PObject, Boolean>();
    @AutomatableProperty(name="Draw Grid?", desc="Draw Grid?", DesignTimeBehavior="A", RunTimeBehavior="H")
    public boolean bDrawGrid = true;

    @AutomatableProperty(name="Pixel Allowance (x)", desc="Pixel Allowance (x)", DesignTimeBehavior="A", RunTimeBehavior="H")
    public int pixelAllowance_x = 6;

    @AutomatableProperty(name="Pixel Allowance (y)", desc="Pixel Allowance (y)", DesignTimeBehavior="A", RunTimeBehavior="H")
    public int pixelAllowance_y = 6;

    @AutomatableProperty(name="Grid Color", desc = "Grid color in R,G,B format", DesignTimeBehavior="A", RunTimeBehavior="H")
    public String grid_color = "217,217,217";

    @AutomatableProperty(name="Grid Number Color", desc = "Grid number color in R,G,B format", DesignTimeBehavior="A", RunTimeBehavior="H")
    public String grid_number_color = "255,0,0";

    @AutomatableProperty(name="Grid Number Precision", desc="Number of decimal points to show for the axis numbers", DesignTimeBehavior="A", RunTimeBehavior="H")
    public int grid_number_dp = 0;

    @AutomatableProperty(name="Background image path", desc="Background image path", DesignTimeBehavior="H", RunTimeBehavior="H")
    public String bgImgPath = "";
    
    @AutomatableProperty(name="Background image binary", desc="Background image binary", DesignTimeBehavior="H", RunTimeBehavior="H")
    public PBinaryImage bgImg = new PBinaryImage();

    @AutomatableProperty(name="Texture Start X", desc="Texture Start X", DesignTimeBehavior="A", RunTimeBehavior="H")
    public float textStartx = 0;

    @AutomatableProperty(name="Texture Start Y", desc="Texture Start Y", DesignTimeBehavior="A", RunTimeBehavior="H")
    public float textStarty = 0;

    @AutomatableProperty(name="Texture Length", desc="Texture Length", DesignTimeBehavior="A", RunTimeBehavior="H")
    public float textLength = 15;

    @AutomatableProperty(name="Texture Height", desc="Texture Height", DesignTimeBehavior="A", RunTimeBehavior="H")
    public float textHeight = 15;

    @AutomatableProperty(name="Texture Alpha", desc="Texture Alpha (0-1)", DesignTimeBehavior="A", RunTimeBehavior="H")
    public float textAlpha = 0.5f;

    @AutomatableProperty(name="Better graphics?", desc = "Better graphics - antialiasing, image background, etc.", DesignTimeBehavior="A", RunTimeBehavior="H")
    public boolean m_advGraphics = false;

    @AutomatableProperty(name="Speedup Factor", desc="Speedup factor", DesignTimeBehavior="A", RunTimeBehavior="H")
    public float speedupFactor = 1.6f;

    protected String bg_color = defaultBgColor;

    // The transformation matrix used by the engine
    protected Matrix4x4 T = new Matrix4x4();
    protected GUI m_gui = null;
    Class[] m_objTypes;

    // The currently selected object:
    PObject m_selObj;

    // A list of custom statement factories
    // one can insert a custom factory by its AddStatementPrototypeFactory(.)
    // and likewise remove a custom factory by its
    // RemoveStatementPrototypeFactory(.) method.
    ArrayList<AbstractStatementFactory> customStmtFactories =
            new ArrayList<AbstractStatementFactory>(0);

    // internal statement factories
    ArrayList<AbstractStatementFactory> defaultStmtFactories =
            new ArrayList<AbstractStatementFactory>(0);
    AbstractStatementFactory finalStmtFactory;

    public PEngine(Class[] objTypes)
    {
       this("", "", objTypes);
    }
    
    /**
     * The key constructor
     * @param name
     * @param desc
     * @param objTypes
     */
    public PEngine(String name, String desc, Class[] objTypes)
    {
        super(name, desc);
        m_objTypes = objTypes;
        m_pEngine = this;

        // insert default statement factories
        finalStmtFactory = new StatementFactoryDefault();

    }

    // <editor-fold defaultstate="collapsed" desc="Serialization support routines">
    //Saving
    @Override
    public Element GetXml()
    {
        //System.out.println("GetXML");
        Element ePCSDT = new Element("pCSDT");
 
        // Record all requirements:
        Element eJNLPInfo = new Element("JNLPInfo");
        Element eProject = new Element("project");
        String scan = "";
        File f = null;
        if (codename != null) {
            try {
                if(codebaseURL != null){
                    f = new File(codebaseURL + "demos/identify.txt");
                }
                else{
                    f = new File("C:/Users/Public/demos/identify.txt");
                }
                Scanner sc = new Scanner(f, "UTF-8");
                while(sc.hasNext()) scan += sc.next();
                codename=scan.substring(0, 2);
            } catch (IOException ex) {
                Logger.getLogger(PEngine.class.getName()).log(Level.SEVERE, null, ex);
            }
                
            eProject.setAttribute("codename", codename);
        }
        if (version != null) {
            version = scan.substring(3, 7);
            eProject.setAttribute("version", version);
        }
        eJNLPInfo.addContent(eProject);

        Element eJNLP = new Element("JNLP");

        Element eUniversalResources = new Element("resources");
        Element eJ2se = new Element("j2se");
        if (j2seVersion != null) {
            eJ2se.setAttribute("version", j2seVersion);
            eUniversalResources.addContent(eJ2se);
        }
        if (properties != null) {
            for (String[] propPair: properties) {
                Element eProperty = new Element("property");
                eProperty.setAttribute("name", propPair[0]);
                eProperty.setAttribute("value", propPair[1]);
                eUniversalResources.addContent(eProperty);
            }
        }
        if (jars != null) {
            for (String[] jarPair: jars) {
                Element eJar = new Element("jar");
                eJar.setAttribute("href", jarPair[0]);
                eJar.setAttribute("main", jarPair[1]);
                eUniversalResources.addContent(eJar);
            }
        }
        if (graphicsJars != null) {
            for (String[] jarPair: graphicsJars) {
                Element eJar = new Element("jar");
                eJar.setAttribute("href", jarPair[0]);
                eJar.setAttribute("main", jarPair[1]);
                eUniversalResources.addContent(eJar);
            }
        }
        if (appJars != null) {
            for (String[] jarPair: appJars) {
                Element eJar = new Element("jar");
                eJar.setAttribute("href", jarPair[0]);
                eJar.setAttribute("main", jarPair[1]);
                eUniversalResources.addContent(eJar);
            }
        }
        if (exts != null) {
            for (String[] extPair: exts) {
                Element eExt = new Element("extension");
                eExt.setAttribute("name", extPair[0]);
                eExt.setAttribute("href", extPair[1]);
                eUniversalResources.addContent(eExt);
            }
        }
        if (graphicsExts != null) {
            for (String[] extPair: graphicsExts) {
                Element eExt = new Element("extension");
                eExt.setAttribute("name", extPair[0]);
                eExt.setAttribute("href", extPair[1]);
                eUniversalResources.addContent(eExt);
            }
        }
        if (appExts != null) {
            for (String[] extPair: appExts) {
                Element eExt = new Element("extension");
                eExt.setAttribute("name", extPair[0]);
                eExt.setAttribute("href", extPair[1]);
                eUniversalResources.addContent(eExt);
            }
        }
        eJNLP.addContent(eUniversalResources);

        if (graphicsOsSpecNativeLibs != null) {
            for (String[] nativeLibPair: graphicsOsSpecNativeLibs) {
                Element eRes = new Element("resources");
                eRes.setAttribute("os", nativeLibPair[0]);
                eRes.setAttribute("arch", nativeLibPair[1]);
                Element eNativeLib = new Element("nativelib");
                eNativeLib.setAttribute("href", nativeLibPair[2]);
                eRes.addContent(eNativeLib);
                eJNLPInfo.addContent(eRes);
            }
        }

        Element eAppletDesc = new Element("applet-desc");
        if (appletName != null)
            eAppletDesc.setAttribute("name", appletName);
        if (appletMainClass != null)
            eAppletDesc.setAttribute("main-class", appletMainClass);
        if (appletWidth != null)
            eAppletDesc.setAttribute("width", appletWidth);
        if (appletHeight != null)
            eAppletDesc.setAttribute("height", appletHeight);
        if (appletSingleDemoXmlUrl != null){
            Element eSingleDemoXmlUrl = new Element("param");
            eSingleDemoXmlUrl.setAttribute("name", "SingleDemoXmlUrl");
            eSingleDemoXmlUrl.setAttribute("value", appletSingleDemoXmlUrl);
            eAppletDesc.addContent(eSingleDemoXmlUrl);
        }
        if (appletDemoPropertiesUrl != null) {
            Element eDemoPropertyUrl = new Element("param");
            eDemoPropertyUrl.setAttribute("name", "DemoPropertiesUrl");
            eDemoPropertyUrl.setAttribute("value", appletDemoPropertiesUrl);
            eAppletDesc.addContent(eDemoPropertyUrl);
        }
        if (appletTemplateBackgroundPropertiesUrl != null) {
            Element eTemplateBackgroundPropertiesUrl = new Element("param");
            eTemplateBackgroundPropertiesUrl.setAttribute("name", "TemplateBackgroundPropertiesUrl");
            eTemplateBackgroundPropertiesUrl.setAttribute("value", appletTemplateBackgroundPropertiesUrl);
            eAppletDesc.addContent(eTemplateBackgroundPropertiesUrl);
        }
        eJNLP.addContent(eAppletDesc);
        eJNLPInfo.addContent(eJNLP);
        ePCSDT.addContent(eJNLPInfo);

        Element e = new Element("Engine");
	e.setAttribute("type", GetEngineName());

	// Store the name of the selected object, if it exists:
	if(m_selObj != null)
            if(m_selObj == this)
		// When the selected object is the engine, we set the name to the empty string
                e.setAttribute("selected", "");
            else
		e.setAttribute("selected", m_selObj.GetName());

        // Record all properties:
        Element eProps = new Element("Properties");
        for(PProperty p : GetProperties())
            eProps.addContent(p.GetXml());
        e.addContent(eProps);

        // Record all events:
        Element eEventLists = new Element("EventLists");
        for(PEventList evtList : GetEventLists())
            eEventLists.addContent(evtList.GetXml());
        e.addContent(eEventLists);

        // Record all objects:
        Element eObjs = new Element("Objects");
        for(PObject o : objs){
            for(PProperty p : o.GetProperties()){
                if(p.GetName().equals(o + ".Object image bytes")){
                    try {
                        //Save image set by user
                        try {
                            if(image_map.get(o).startsWith("img/")){
                                default_map.put(o, true);
                                objImg_base64 = p.GetValue().toString();
                            } else objImg_base64 = SetObjectImage(image_map.get(o));
                        } catch (IOException ex) {
                            Logger.getLogger(PEngine.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        p.SetValue(objImg_base64);
                    } catch (IllegalAccessException ex) {
                        Logger.getLogger(PEngine.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if(p.GetName().equals(o + ".Costumes")){
                    //Save costumes as one string
                    ArrayList<String> images = JPnlObjMgr.ObjectImageMap.get(o);
                    String temp = "";
                    for(String s : images){
                        try {
                            if(s.startsWith("img/")){
                                default_map.put(o, true);
                                for(PProperty pp : o.GetProperties()){
                                    if(pp.GetName().equals(o + ".Object image bytes")){
                                        temp += "," + pp.GetValue().toString();
                                    }
                                }
                            } else temp = temp + "," + SetObjectImage(s);
                        } catch (IOException ex) {
                            Logger.getLogger(PEngine.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    try {
                        p.SetValue(temp);
                    } catch (IllegalAccessException ex) {
                        Logger.getLogger(PEngine.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if(p.GetName().equals(o + ".isDefault")){
                    try {
                        p.SetValue(default_map.get(o));
                    } catch (IllegalAccessException ex) {
                        Logger.getLogger(PEngine.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            eObjs.addContent(o.GetXml());
        }
        e.addContent(eObjs);

        ePCSDT.addContent(e);

	return ePCSDT;
    }

    class DeferredXmlAssign
    {
	public PObject m_obj;
	public Element m_elem;
	public DeferredXmlAssign(PObject obj, Element elem)
	{
            m_obj = obj;
            m_elem = elem;
	}
    }

    /**
     *
     * @param name The name of the object
     * @return An object with the specified name, or null if no such object exists
     */
    public PObject FindObjectByName(String name)
    {
	// Special case for an empty name:
	if(name.isEmpty())
            return this;
	for(PObject pObj : objs)
            if(pObj.GetName().compareTo(name) == 0)
		return pObj;
	return null;
    }

    //Loading
    @Override
    public boolean SetXml(PEngine context, Element element)
    {
        Element elem = element.getChild("Engine");
	// Confirm the engine types match:
	String engType = elem.getAttributeValue("type");
	if(engType == null || engType.compareTo(GetEngineName()) != 0)
            return false;

	// Reset everything currently in the engine:
	Clear();
        JPnlObjMgr.ObjectImageMap.clear();

	// First, create all objects before setting any XML:
        // defer adding objects to the engine until
        // the properties of all objects are loaded, so that there is a way for
        // application developers to do object initialization based on the
        // property values
        ArrayList<PObject> objList = new ArrayList<PObject>(0);
	Element objs = elem.getChild("Objects");
	ArrayList<DeferredXmlAssign> defer = new ArrayList<DeferredXmlAssign>();
	for(Object obj : objs.getChildren())
        {
            Element child;
            if(obj instanceof Element)
		child = (Element)obj;
            else continue;

            // For safety reasons, we only initialize objects which appear in
            // our list of scriptable objects:
            String type = child.getAttributeValue("type");
            Class c = GetObjectType(type);
            if(c == null) {
                System.out.println("PEngine: class c is null");
		continue;
            }
            try
            {
		PObject pObj = PObject.Construct(
					c,
					child.getAttributeValue("name"),
					child.getAttributeValue("desc"),
                                        this
				);
                objList.add(pObj);
                AddObjectXml(pObj);
		defer.add(new DeferredXmlAssign(pObj, child));
            }
            catch(Exception e) {
                e.printStackTrace();
            }
	}

        // Now that all objects are present, we can try setting XML.
	for(DeferredXmlAssign cur : defer)
            if(!cur.m_obj.SetXml(context, cur.m_elem))
		DelObject(cur.m_obj);
        
        // all properties of the PObjects are loaded
        // proceed to calling DeferredInitialize()
        // Application developers can
        for (PObject pObj: objList)            
            pObj.DeferredInitialize();
        
        //Set Global Variable
        objsList = objList;
        //Get the bytes saved in the xml file
        for (PObject pObj: objList){
            for(PProperty p : pObj.GetProperties()){
                if(p.GetName().equals(pObj + ".Object image bytes")){
                    objImg_base64 = p.GetValue().toString();
                    try {
                        image_map.put(pObj, GetObjectImage());
                    } catch (IOException ex) {
                        Logger.getLogger(PEngine.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if(p.GetName().equals(pObj + ".Costumes")){
                    //Load images into JPnlObjMgr
                    String s = p.GetValue().toString();
                    ArrayList<String> images = new ArrayList<String>(0);
                    StringTokenizer tokens = new StringTokenizer(s, ",");
                    while (tokens.hasMoreTokens()) {
                        objImg_base64 = tokens.nextToken().trim();
                        try {
                            images.add(GetObjectImage());
                        } catch (IOException ex) {
                            Logger.getLogger(PEngine.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    JPnlObjMgr.ObjectImageMap.put(pObj, images);
                }
                if(p.GetName().equals(pObj + ".isDefault")){
                    default_map.put(pObj, p.GetValue().bValue);
                }
                if(p.GetName().equals(pObj + ".identify")){
                   //What to do with identify?
                }
            }
        }
        //Load image saved by user
        for (PObject pObj: objList){
            for(PProperty p : pObj.GetProperties()){
                //Set the icon property to the new image file created in GetObjectImage()
                if(p.GetName().equals(pObj + ".icon")){
                    try {
                        p.SetValue(image_map.get(pObj));
                    } catch (IllegalAccessException ex) {
                        Logger.getLogger(PEngine.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

        }
        
        // reload objects
        m_gui.ReloadObjMgr();

        // load this PEngine's stuff here
        super.SetXml(this, elem);

	// set default selected object
	Attribute attrib = elem.getAttribute("selected");
        if(attrib != null)
        {
            String selectedObj = attrib.getValue();
            PObject pObj = FindObjectByName(selectedObj);
            if(pObj != null) {
                SetSelectedObject(pObj);
                m_gui.SetSelection(pObj);
            }
	}

        return true;
    }        

    public void SaveXml(String tgtName) throws IOException
    {
	Document doc = new Document(GetXml());
	XMLOutputter out = new XMLOutputter();

	FileWriter w = new FileWriter(tgtName);
	out.setFormat(Format.getPrettyFormat());
	out.output(doc, w);
    }

    public void LoadXml(URL url) throws IOException, JDOMException
    {
        InputStream is = url.openStream();
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(is);
        SetXml(this, doc.getRootElement());
        is.close();
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Object manipulation methods and members">
    /**
     * Object storage
     */
    public Vector<PObject> objs = new Vector<PObject>();

    /**
     *
     * @return The name of the engine, defaulting to the name of the class.
     */
    public String GetEngineName() {return getClass().getName();}

    /**
     *
     * @return The list of object factories supported by this engine
     */
    public Class[] GetObjectTypes() {return m_objTypes;}
    
    /**
     * Returns a list of names of the objects instantiated by
     * the Engine     
     */
    final public Vector<PObject> GetObjects() {return objs;}
   
    /**
     * Gets an object at a specified index
     * @param i The index at which to get an object
     * @return The object at that index
     */
    final public PObject GetObject(int i) {return objs.get(i);}

    /**
     * Gets an object by name, rather than by index
     * @param objType The type of the object to be located
     * @param name The name of the object to be returned
     * @return The found object, or NULL if not located
     */
    final public PObject GetObject(Class objType, String name)
    {
	if(objType == this.getClass())
            return this;
	for(PObject obj : objs)
            if(obj.getClass().isAssignableFrom(objType) &&
               obj.GetName().compareTo(name) == 0)
		return obj;
	return null;
    }

    final public PObject GetObject(String name)
    {
	for(PObject obj : objs)
            if(obj.GetName().compareTo(name) == 0)
                return obj;
        if (name.compareTo(this.GetName()) == 0)
            return this;
	return null;
    }

    /**
     *
     * @param name The name of the object type to recover
     * @return The located type, or null if no such type exists
     */
    final public Class GetObjectType(String name)
    {
	if(this.getClass().getName().compareTo(name) == 0)
            return this.getClass();
	for(Class c : m_objTypes)
            if(name.compareTo(c.getName()) == 0)
		return c;
	return null;
    }

    /**
     * Adds a new object to be managed by the engine
     * @param obj The object to be added
     */
    public void AddObject(PObject obj)
    {
	objs.add(obj);
	OnAddObject(obj);
	obj.SetEngine(this);
        obj.DeferredInitialize();
    }

    /**
     * Adds a new object to be managed by the engine through loading Xml
     * @param obj The object to be added
     */
    public void AddObjectXml(PObject obj)
    {
	objs.add(obj);
	OnAddObject(obj);
	obj.SetEngine(this);
    }

    /**
     * Removes an object from the managed object sets
     * @param obj The object to be removed
     */
    public void DelObject(PObject obj)
    {
	OnDelObject(obj);
	objs.remove(obj);
    }

    /**
     * Removes all objects assoicated with this PEngine
     */
    public void DelAllObjects()
    {
        for (PObject obj: objs)
            OnDelObject(obj);
        objs.removeAllElements();
    }

    /**
     * Called after an object has been added
     * @param obj An object that was added
     */
    public void OnAddObject(PObject obj) {}
    
    /**
     * Called before an object is deleted
     * @param obj An object that is about to be deleted
     */
    public void OnDelObject(PObject obj) {}
    
    /**
     * Sets the selected object.  The engine should unconditionally set the
     * selected object, and rely on the framework to clear the selected object when
	 * necessary.
     * @param obj The object to be selected.  May potentially be null, if nothing should
     * be selected.
	 * @deprecated Override OnObjectSelectedinstead.
     */
    @Deprecated
    public void SetSelectedObjects(PObject[] obj)
    {
    }

    /**
     * Called when an object is selected
     * @param selObj The newly selected object
     */
    public void OnObjectSelected(PObject selObj) {}

    /**
     * Sets the selected object
     * @param selObj The newly selected object
     */
    public final void SetSelectedObject(PObject selObj)
    {
        OnObjectSelected(selObj);
	SetSelectedObjects(new PObject[]{selObj});
	m_selObj = selObj;
    }

    /**
     * Resets the engine and all child objects
     */
    @Override
    public final void Reset()
    {
        this.ClearDrawing();
        for(PObject obj : objs)
            try {obj.Reset();}
            catch(Exception e)
            {
                System.out.print("Exception occured: " + e.getMessage());
                e.printStackTrace();
            }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Object picking routines">
    /**
     * The transformation matrix describes how to convert world coordinates to screen coordinates,
     * assuming that the extent of the screen is [-1...1] in the x and y axes, with (0, 0) describing
     * the origin.
     *
     * @returns The transformation matrix for the engine, used for picking
     */
    public Matrix4x4 GetTransformMatrix() {return T;}

    /**
     * Attempts to recover an object based on its specified screen coordinates
     * @param x The x-coordinate, in logical screen coordinates
     * @param y The y-coordinate, in logical screen coordinates
     * @return The object at the specified screen coordinates
     * Note that, by default, the GUI class passes an x and y pixel offset from the top-left
     * corner of the canvas.
     */
    abstract public PObject Pick(double x, double y);
    
    /**
     * Attempts to recover an object based on its specified screen coordinates
     * A side effect is to set the isMosueOver status of the PObjects
     * @param x The x-coordinate, in logical screen coordinates
     * @param y The y-coordinate, in logical screen coordinates
     * @return A Vector containing object at the specified screen coordinates
     * Note that, by default, the GUI class passes an x and y pixel offset from the top-left
     * corner of the canvas.
     */
    abstract public Vector<PObject> PickAll(double x, double y);
    
    /**
     *
     * @return The default object.
     */
    public PObject GetDefaultObject()
    {
	return this;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Event virtuals and invocation routines">
    /**
     * This method simply calls OnBegin and then invokes the Begin event for each object in the engine,
     * and then for the engine itself
     */
    public void InvokeBegin()
    {
        
        /*
        // Store all of the reset values before we begin:
        for(PObject obj : objs)
            obj.StoreResetVals();
         *
         */
        // reset timeElapsed
        timeElapsed = 0;
        // propagate the change to all the associated objects
        for (PObject o: GetObjects()) o.timeElapsed = 0;

        GetGui().SetTimeLabelText(0);

        // Start everything:
	synchronized(objs)
	{
            for(PObject obj : objs)
                try
		{
                    obj.OnBegin();
		}
		catch(Exception e)
		{
                    System.out.print("Exception occured: " + e.getMessage());
                    e.printStackTrace();
		}
	}
        OnBegin();

        // call the PObjects' PEventBegin after all objects are initialized
        // properly
        synchronized(objs)
        {
            for(PObject obj : objs)
                try
		{
                    obj.GetBeginEventList().Invoke();
		}
		catch(Exception e)
		{
                    System.out.print("Exception occured: " + e.getMessage());
                    e.printStackTrace();
		}
        }
        try { GetBeginEventList().Invoke(); }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method calls OnEnd for every object in the engine, and then for the engine itself
     */
    public void InvokeEnd()
    {
        for(PObject obj : objs)
            obj.OnEnd();
        OnEnd();
    }

     /**
     * This method calls OnClear for every object in the engine, and then for the engine itself
     */
    public void InvokeClear()
    {
        for(PObject obj : objs)
            obj.OnClear();
        OnClear();
    }
    
    /**
     * This method calls OnCreate for every object in the engine, and then for the engine itself
     */
    public void InvokeCreate()
    {
        for(PObject obj : objs)
            obj.OnCreate();
        OnCreate();
    }
	// </editor-fold>

    // set background texture with URL
    public void SetBackgroundImage(boolean bRelative, String fileName)
    {
        // update internal BufferedImage
        if (fileName != null && !fileName.equals("")) {
            try {
                // update bgImg
                if (bRelative) {
                    InputStream i = getClass().getResourceAsStream(fileName);
                    if (i != null) {
                        bgImgPath = fileName;
                        bgImg.SetImage("");
                    }
                    else {
                        bgImgPath = "";
                        bgImg.SetImage("");
                    }
                }
                else {
                    if (fileName.startsWith("http://")) {
                        bgImgPath = "";
                        bgImg.SetImage(ImageIO.read(Utility.FormatURL(fileName)), "png");
                    }
                    else {
                        bgImgPath = "";
                        bgImg.SetImage(ImageIO.read(new File(fileName)), "png");
                    }
                }
           }
           catch (Exception e) {
               e.printStackTrace();
           }
        }
        GetGui().SetPEngineTextureBeUpdated(true);
    }
    
    /*
     * Put an image into xml file
     */
    public String SetObjectImage(String objectImgSource) throws IOException{
        File file = new File(objectImgSource);
        //System.out.println("SetObjectImage = " + file);
        file.setReadOnly();
        byte[] imgData = null;
        int i = 0;
        try {
            DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
            try {
                int UPPER = in.available();
                imgData = new byte[UPPER];
                while (i < UPPER) {
                    imgData[i]=in.readByte();
                    i++;
                }
            } catch (IOException ex) {
                Logger.getLogger(PEngine.class.getName()).log(Level.SEVERE, null, ex);
            }
            in.close();
            return Base64.encodeBase64String(imgData);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PEngine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (EOFException ex){
            Logger.getLogger(PEngine.class.getName()).log(Level.SEVERE, null, ex);                
        }
        return null;
    }
    
    /*
     * Get image from xml file
     * 
     */
    public String GetObjectImage() throws IOException {
        File file = null;
        file = File.createTempFile("image", ".png");
        
        byte[] imgData = null;
        imgData = Base64.decodeBase64(objImg_base64);
        try {
            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));           
            out.write(imgData);
            out.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        return file.toString();
    }

    public String GetBackgroundColor() {
        return bg_color;
    }
    
    public void SetBackgroundColor(String s) {
        bg_color = s;
    }

    // return reasonable forgiving length under current scale
    public float GetForgivingDistanceX()
    {
        //int width = this.GetGui().getCanvasSize().width;
        return 0;
    }

    public float GetForgivingDistanceY()
    {
        //int height = this.GetGui().getCanvasSize().height;
        return 0;
    }

    /**
     * Steps animation of the entire application by a specified time parameter
     * @param dt The time parameter, in s, by which to advance the scene
     */
    @Override
    public void Step(double dt) {
        super.Step(dt);
        GUI gui = GetGui();
        if (gui.GetRenderState() == eRenderState.Animating) {
            timeElapsed += (float)(dt*speedupFactor);
            // propagate the timeElapsed info to the associated objects
            for (PObject o: GetObjects()) o.timeElapsed = timeElapsed;
        }
        gui.SetTimeLabelText(timeElapsed);
    }

    public void Clear()
    {
        InvokeClear();
        DelAllObjects();
        for(PEventList evtList : GetEventLists())
            evtList.ClearScript();
        SetBackgroundImage(true, defaultBgFile);
    }

    /**
     * This method is for subclass to override, defining how the graphic effect
     * should be removed.
     */
    public abstract void ClearDrawing();
    
    public GUI GetGui() {return m_gui;}
    public void SetGui(GUI gui) {m_gui = gui;}

    /**
     * Add a new statement prototype factory
     * @param f
     */
    public void AddStatementFactory(AbstractStatementFactory f) {
        customStmtFactories.add(f);
    }

    /**
     * Remove a given statement prototype factory
     * @param f
     * @return
     */
    public boolean RemoveStatementFactory(AbstractStatementFactory f) {
        return customStmtFactories.remove(f);
    }

    /**
     * Given the key, produce a statement if the key matches the generation
     * criteria of the statement factories
     * @param key the keyword to be matched by statement factory
     * @param type the type to be matched by statement factory
     * @return an IStatement from the matched statement factory
     */
    public IStatement ProduceStatement(String key, ePType type) {
        // custom ones have highest precedence
        for (AbstractStatementFactory f: customStmtFactories) {
            IStatement s = f.GenerateStatement(key, type);
            if (s != null) return s;
        }
        // next comes the built-in
        for (AbstractStatementFactory f: defaultStmtFactories) {
            IStatement s = f.GenerateStatement(key, type);
            if (s != null) return s;
        }
        // finally the last resort factory
        return finalStmtFactory.GenerateStatement(key, type);
    }

    @Override
    public String IsInputValid(String str, String propName) {
        String superReturn = super.IsInputValid(str, propName);
        if (superReturn != null) {
            return superReturn;
        }
        if (propName.equals("textAlpha")) {
            if (Float.parseFloat(str)<=0 || Float.parseFloat(str)>=1) {
                return "Radius should be between 0 and 1.";
            }
        }
        return null;
    }
    
    @AutomatableMethod(displayPos = 20, name="Stop Simulation", argNames={}, argDesc={}, argVals={})
    public void StopSimulation()
    {
        GetGui().EndRendering();
        InvokeEnd();
    }
}
