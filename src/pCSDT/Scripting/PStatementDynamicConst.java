/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting;

import java.util.ArrayList;
import pCSDT.Scripting.SCElem.*;
import org.jdom.Element;

/**
 *
 * @author Jason
 * This represents a statement with a constant value.  Note that it is legal for
 * PVariant to contain a variable, but the variable must be resolved before the
 * statement is executed for it to be executed properly.
 */
public class PStatementDynamicConst<T> extends PStatement {
    // This static constant corresponds to a PStatementConst wrapped around
    // a PVariant.Void
    String spListName;  // if it is from a special list, store its name here
    AbstractValueList<T> m_valueList;  // the object that returns the list of values
    T value;  // selected value (more accurate)

    protected PStatementDynamicConst() {}

    /**
     * Composes a new constant method with the given value
     * @param value The variant on which this constant is bound.  Note that the
     * type of this variant must not be Variable, or the constructor call will
     * fail.
     */
    public PStatementDynamicConst(AbstractValueList<T> valueList)
    {
        super(valueList.GetType());
        m_valueList = valueList;
        m_retType = m_valueList.GetType();
        // set default value to the first element in the list
        if (valueList.GetValueList().size() > 0) {
            value = m_valueList.GetValueList().get(0);
        }
    }

    public PStatementDynamicConst(AbstractValueList<T> valueList, int i)
    {
        super(valueList.GetType());
        m_valueList = valueList;
        value = m_valueList.GetValueList().get(i);
        m_retType = m_valueList.GetType();
    }

    public PStatementDynamicConst(AbstractValueList<T> valueList, T t)
    {
        super(valueList.GetType());
        m_valueList = valueList;
        int idx = 0;
        if (t != null) {
            idx = m_valueList.GetValueList().indexOf(t);
            if (idx != -1) {
                value = m_valueList.GetValueList().get(idx);
            }
            else {
                value = m_valueList.GetValueList().get(0);
            }
        }
        else {
            value = m_valueList.GetValueList().get(0);
        }
        m_retType = m_valueList.GetType();
    }
    
    /**
     * 
     * @param value The new constant value for this statement
     */
    public void SetSelectedIndex(int i)
    {
        value = m_valueList.GetValueList().get(i);
    }

    public int GetSelectedIndex()
    {
        return m_valueList.GetValueList().indexOf(value);
    }
    
    /**
     * @return The PType of the inner value
     */
    public PType GetType() {return m_retType;}

    public ArrayList<T> GetValueList() {return m_valueList.GetValueList();}

    /**
     * 
     * @return The inner value wrapped by this PStatementConst
     */
    public PVariant GetValue() {
        return new PVariant(value);
    }

    public void SetValue(T v) {
        value = v;
    }

    @Override
    public JPnlLine GetGui(LayoutInfo info) {
        return new JPnlLineDropDownConst(this, info);
    }
    
    @Override
    public PVariant Execute(PScopeStack stack) {return GetValue();}

    @Override
    public Element GetXml(String tagName)
    {
	Element elem = new Element(tagName);
	elem.setAttribute("type", "dynamicConst");
        elem.setAttribute("option-type", m_valueList.GetType().toString());
        String name = m_valueList.GetName();
        if (name.startsWith("@")) {
            elem.setAttribute("option-values", name);
        }
        else {
            elem.setAttribute("option-values", m_valueList.GetValueList().toString());
        }
        elem.setText(Integer.toString(GetSelectedIndex()));
        return elem;
    }

    @Override
    public boolean SetXml(PEngine context, Element elem)
    {
        value = m_valueList.GetValueList().get(Integer.parseInt(elem.getText()));
        m_retType = m_valueList.GetType();
	return true;
    }

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
    public PStatementDynamicConst<T> clone()
    {
        // appear okay to use the same valueList
        return new PStatementDynamicConst<T>(m_valueList, value);
    }

    public boolean HasReturnValue() {return true;}
    public boolean HasSideEffect() {return false;}
}
