/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting;
import org.jdom.*;
import pCSDT.Scripting.SCElem.*;

/**
 *
 * @author Jason
 * This represents a property for an object.  A property is essentially a pair
 * of methods following the accessor/mutator form, and is implemented exactly
 * that way.  This class is for convenience only.
 */
public class PProperty implements IAssignable
{
    PObject m_obj;
    PPropertyUnbound m_ub;
    private static final IStatement[] sc_noChildren = new IStatement[0];

    public PProperty(PPropertyUnbound ub, PObject obj)
    {
        m_ub = ub;
        m_obj = obj;
    }

    public class PPropertyGet
    {
        public Element getElementXML() {return null;}
        
        public PVariant Execute(PScopeStack scope) throws Exception
        {
            return new PVariant(m_ub.Get(m_obj));
        }

        public PType GetReturnType() {return m_ub.GetType();}

        public IStatement[] GetChildren() {return sc_noChildren;}
    }
    
    public class PPropertySet
    {
        IStatement[] args = new IStatement[1];
        
        public Element getElementXML() {return null;}
        
        public PVariant Execute(PScopeStack scope) throws Exception
        {
            m_ub.Set(m_obj, args[0].Execute(scope));
            return PVariant.Void;
        }

        public PType GetReturnType() {return m_ub.GetType();}

        public IStatement[] GetChildren() {return args;}
    }
    
    // Accessor methods:
    public PPropertyUnbound GetUnboundProperty() {return m_ub;}

    @Override
    public PVariant GetValue(PScopeStack stk)
    {
	return GetValue();
    }

    public PVariant GetValue()
    {
        try
        {
            return new PVariant(m_ub.Get(m_obj));
        }
        catch(Exception e) {return PVariant.Void;}
    }

    
    /**
     * Utility value assignment
     * @param val The value to assign; must be of compatible types
     * @throws java.lang.IllegalAccessException If something went wrong during assignment
     */
    public void SetValue(Object val)
        throws IllegalAccessException
    {
        m_ub.Set(m_obj, val);
    }
    
    /**
     * 
     * @param val
     * @throws java.lang.ClassCastException If the input variant is not of compatible type
     * @throws java.lang.IllegalAccessException If something went wrong during assignment
     */
    public void SetValue(PVariant val)
		throws ClassCastException, IllegalAccessException
    {
        ////System.out.println("PProperty.SetValue(): type: " +  val.m_type + " val = " + val);
	if(val == null)
            val = PVariant.Void;
        // the first condition checks if the two types match
        // the second condition allows Integer to be assigned to a Float
        // the third condition allows anything to be assigned to a string type
	if(val.GetType() != m_ub.GetType()
                && !(m_ub.GetType() == PType.Float && val.GetType() == PType.Integer)
                && !(m_ub.GetType() == PType.String)) {
            throw new ClassCastException();
        }
	SetValue(val.Wrap());
    }

    /**
     * Attempts to set the bound value, but includes a casting operation
     * that attempts to resolve type compatibility
     * @param val The value to resolve
     */
    public void SetWithCast(Object val)
    {
	;
    }

    public Element GetXml()
    {
        Element root = new Element("Property");
        root.setAttribute("name", m_ub.GetName());
        root.setText(GetValue().toString());
	return root;
    }

    public boolean SetXml(Element elem)
    {
        try {
            // set value based on the target's type, if possible
            if (m_ub.m_type != null) {
                SetValue(PVariant.FromString(elem.getText(), m_ub.m_type));
            }
            else {
                SetValue(PVariant.FromString(elem.getText()));
            }
        }
	catch(Exception e) {
            e.printStackTrace();
            return false;
        }
	return true;
    }

    public PObject GetObject() {return m_obj;}

    ///
    // Base class overrides:
    ///
    @Override
    public PType GetType(PScopeStack stk) {return GetType();}
    public PType GetType() {return m_ub.GetType();}

    @Override
    public String toString() {return m_ub.toString();}

    @Override
    public PVariant Assign(PScopeStack stk, PVariant val) {return Assign(val);}

    public String GetName() {return m_obj.GetName() + "." + m_ub.GetName();}

    public String GetSimpleName() {return m_ub.GetName();}

    public JPnlLine GetGui(LayoutInfo info)
    {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public Element GetXml(String tagName)
    {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean SetXml(PEngine context, Element elem)
    {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public PVariant Assign(PVariant val)
    {
        try
        {
            SetValue(val.Wrap());
            return GetValue();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return PVariant.Void;
    }

    /**
     * clone a PProperty
     * @return the cloned PProperty object
     */
    @Override
    public PProperty clone()
    {
        return new PProperty(m_ub, m_obj);
    }
}
