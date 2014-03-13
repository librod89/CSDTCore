/*
 *
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting;

import org.jdom.Element;
import pCSDT.Scripting.SCElem.*;

/**
 *
 * @brief This is a variable whose name has not yet been resolved.  The name is resolved
 * dynamically at runtime.
 *
 * The variable name is the externally visible class, whereas the actual PVariable class
 * only has an existence at runtime.  PVariableName indicates a persistent reference to a
 * variable, whereas the PVariable indicates a particular instantiation of that variable
 * at some point in time.
 *
 * @author sanchj3
 */
public class PStatementVariable extends PStatementAssignable implements INamedAssignable
{
    String m_name;
    PType m_lastType = PType.Unknown;

    public PStatementVariable() {}
    
    public PStatementVariable(String name)
    {
	m_name = name;
    }

    public PVariant Assign(PScopeStack stk, PVariant rhs)
    {
	IAssignable retVal = stk.Resolve(m_name);
	if(retVal == null)
            return PVariant.Void;
	return retVal.Assign(stk, rhs);
    }

    public PVariant GetValue(PScopeStack stk)
    {
	IAssignable retVal = stk.Resolve(m_name);
	if(retVal == null)
	{
            m_lastType = PType.Void;
            return PVariant.Void;
	}
	PVariant var = retVal.GetValue(stk);
	m_lastType = var.GetType();
	return var;
    }

    public PType GetType(PScopeStack stk)
    {
	IAssignable retVal = stk.Resolve(m_name);
	if(retVal == null)
            return PType.Unknown;
	return retVal.GetType(stk);
    }

    @Override
    public IStatement[] GetChildren() {return null;}

    @Override
    public JPnlLineVariable GetGui(LayoutInfo layoutInfo)
    {
	return new JPnlLineVariable(this, layoutInfo);
    }

    @Override
    public Element GetXml(String tagName) {
	Element retVal = new Element(tagName);
	retVal.setAttribute("type", "variable");
	retVal.setAttribute("name", m_name);
	return retVal;
    }

    @Override
    public boolean SetXml(PEngine context, Element elem)
    {
	m_name = elem.getAttributeValue("name");
	return m_name.length() != 0;
    }

    @Override
    public PVariant Execute(PScopeStack scope) throws Exception {return GetValue(scope);}

    @Override
    public PType GetReturnType() {return m_lastType;}

    @Override
    public String GetName() {return m_name;}

    @Override
    public void SetName(String name) {m_name = name;}

    /**
     * Change the association of methods originally to oldObj to newObj
     * @param oldObj the old PObject
     * @param newObj the new PObject
     */
    @Override
    public void AssociateNullIdentityMethodTo(PObject newObj) {
        m_obj = newObj;
    }

    @Override
    public PStatementVariable clone() {return new PStatementVariable(m_name);}

    public boolean HasSideEffect() {return false;}
    public boolean HasReturnValue() {return true;}
}
