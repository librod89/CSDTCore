/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting;

import java.lang.reflect.*;

/**
 *
 * @author Jason
 */
abstract public class PFieldUnbound {
    /**
     *
     * @param f The field wrapped by this unbound
     */
    public PFieldUnbound(Field field)
    {
        m_field = field;
    }

    protected Field m_field;

    abstract public String GetName();
    abstract public String GetDesc();
    //abstract public String GetGroup();

    /**
     *
     * @return The field corresponding to this unbound events
     */
    public Field GetField() {return m_field;}
    
    /**
     * Accesses the field in the passed object
     * @param obj The object on which to read
     * @return The located object
     * @throws java.lang.IllegalAccessException Thrown by the access operation
     */
    public Object Get(Object obj) throws IllegalAccessException
    {
        return m_field.get(obj);
    }

    /**
     * Sets the value on the corresponding field for the passed member
     * @param obj The object whose field to set
     * @param val The value to assign
     * @throws java.lang.IllegalAccessException Thrown by the access operation
     */
    public void Set(Object obj, Object val) throws IllegalAccessException
    {
        // if the recipient is of type string, cast what is to be assigned
        // to a string
        if (m_field.getType() == java.lang.String.class) {
            m_field.set(obj, val.toString());
        }
        else {
            m_field.set(obj, val);
        }
    }
}
