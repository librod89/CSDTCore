/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting;

import org.jdom.*;
import pCSDT.Scripting.SCElem.*;

/**
 * A statement representing a read-only property
 *
 * @author Jason Sanchez
 */
public class PStatementReadOnlyProperty extends PStatement
{
    ////PObject m_obj;
    PProperty m_prop;

    public PStatementReadOnlyProperty()
    {
    }

    public PStatementReadOnlyProperty(PObject obj)
    {
        m_obj = obj;
	m_prop = null;
     }

    public PStatementReadOnlyProperty(PProperty prop)
    {
        m_prop = prop;
        m_obj = m_prop.GetObject();
    }

    public PObject GetObject() {return m_obj;}

    /**
     *
     * @return The property bound to this object
     */
    public PProperty GetProperty() {return m_prop;}

    /**
     *
     * @param prop The new property bound to this statement
     */
    public void SetProperty(PProperty prop)
    {
        m_prop = prop;
	m_obj = m_prop == null ? null : m_prop.GetObject();
    }

    /**
     * Sets the object without assigning a property.  The property will be then set to null.
     * @param obj The object to assign this property statement
     */
    public void SetObject(PObject obj)
    {
	m_obj = obj;
	m_prop = null;
    }

    public Element GetXml(String tagName)
    {
	Element root = new Element(tagName);
	root.setAttribute("object", m_prop.GetObject().GetName());
	root.setAttribute("prop", m_prop.GetSimpleName());
        root.setAttribute("readonly", "true");
        return root;
    }

    public boolean SetXml(PEngine context, Element elem)
    {
	Attribute object = elem.getAttribute("object");
	Attribute prop = elem.getAttribute("prop");
	if(object == null || prop == null) {
            return false;
        }
	PObject obj = context.GetObject(object.getValue());
	if(obj == null) {
            return false;
        }
        else {
            m_obj = obj;
        }
	m_prop = obj.GetProperty(prop.getValue());
        if (m_prop == null) {
            return false;
        }
        else {
            this.m_retType = m_prop.GetType();
        }
	return true;
    }

    public PVariant Execute(PScopeStack scope) throws Exception
    {
        return m_prop.GetValue();
    }

    public JPnlLineReadOnlyProperty GetGui(LayoutInfo info)
    {
	return new JPnlLineReadOnlyProperty(this, info);
    }

    @Override
    public void AssociateNullIdentityMethodTo(PObject newObj) {
        m_obj = newObj;
        m_prop.m_obj = m_obj;
    }
    
    @Override
    public PStatementReadOnlyProperty clone()
    {
	return new PStatementReadOnlyProperty(m_prop);
    }

    public boolean HasReturnValue() {return true;}
    public boolean HasSideEffect() {return false;}
}
