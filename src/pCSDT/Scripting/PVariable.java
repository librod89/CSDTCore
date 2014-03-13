/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting;


/**
 *
 * @author Jason
 */
public class PVariable implements IAssignable {
    public PVariable(String name, PVariant val)
    {
        m_val = val;
        m_name = name;
    }

    /**
     * This is the name of this variable.  If the name is not blank,
     * the corresponding value is stored in m_value.  Otherwise, it
     * is bound on a field.
     */
    String m_name = null;

    /**
     * The value of the variable
     */
    PVariant m_val;

    public String GetName() {return m_name;}

    ///
    // Base class overrides:
    ///
    @Override
    public PVariant Assign(PScopeStack stk, PVariant val) {return m_val = val;}

    @Override
    public PVariant GetValue(PScopeStack stk) {return m_val;}

    @Override
    public PType GetType(PScopeStack stk) {return m_val.GetType(stk);}

    public PType GetType() {return m_val.GetType();}
}
