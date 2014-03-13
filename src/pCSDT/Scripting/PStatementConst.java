/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting;

import pCSDT.Scripting.SCElem.*;
import org.jdom.Element;

/**
 *
 * @author Jason
 * This represents a statement with a constant value.  Note that it is legal for
 * PVariant to contain a variable, but the variable must be resolved before the
 * statement is executed for it to be executed properly.
 */
public class PStatementConst extends PStatement {
    // This static constant corresponds to a PStatementConst wrapped around
    // a PVariant.Void
    public static final PStatementConst Void = new PStatementConst(PVariant.Void);

    public PStatementConst()
    {
	m_value = PVariant.Void;
    }

    /**
     * Composes a new constant method with the given value
     * @param value The variant on which this constant is bound.  Note that the
     * type of this variant must not be Variable, or the constructor call will
     * fail.
     */
    public PStatementConst(PVariant value)
    {
        super(value.GetType());
        m_value = value;
        //System.out.println("PStatementConst constructor: set m_value to " + m_value);
    }

    public PStatementConst(int value) {this(new PVariant(value));}
    public PStatementConst(String value) {this(new PVariant(value));}
    public PStatementConst(double value) {this(new PVariant(value));}
    public PStatementConst(boolean value) {this(new PVariant(value));}

    PVariant m_value;
    
    /**
     * 
     * @param value The new constant value for this statement
     */
    public void SetValue(PVariant value)
    {
        m_value = value;
        m_retType = m_value.GetType();
    }
    
    /**
     * @return The PType of the inner value
     */
    public PType GetType() {return m_retType;}

    /**
     * 
     * @return The inner value wrapped by this PStatementConst
     */
    public PVariant GetValue() {return m_value;}

    @Override
    public JPnlLine GetGui(LayoutInfo info) {return new JPnlLineConst(this, info);}
    
    @Override
    public PVariant Execute(PScopeStack stack) {return GetValue();}

    @Override
    public Element GetXml(String tagName)
    {
	Element elem = new Element(tagName);
	elem.setAttribute("type", "const");
	elem.setText(m_value.toString());
        return elem;
    }

    @Override
    public boolean SetXml(PEngine context, Element elem)
    {
        m_value = PVariant.FromString(elem.getText());
        if(m_value == null)
            return false;
	m_retType = m_value.GetType();
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
    public PStatementConst clone()
    {
       return new PStatementConst(
			new PVariant(m_value)
		);
    }

    public boolean HasReturnValue() {return true;}
    public boolean HasSideEffect() {return false;}
}
