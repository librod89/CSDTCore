/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Presentation;

import pCSDT.Scripting.*;
import java.util.HashMap;

/**
 *
 * @author Jason
 * This is a wrapper class for the GUI's automatable type.  It's used in the
 * tree control to get a friendly name from a class name.  If you want to change
 * the friendly name for your class, then have a look at the AutomatableClass
 * annotation.
 */
public class GuiAutomatableType {
    Class m_clazz;
    String m_friendlyName;
    static HashMap m_hm = new HashMap();
    
    /**
     * Singleton method for creating an automatable type
     * @param clazz
     * @return An automatable type based on a particular class name
     */
    public static GuiAutomatableType Get(Class clazz)
    {
        GuiAutomatableType t = (GuiAutomatableType)m_hm.get(clazz);
        if(t == null)
            m_hm.put(clazz, t = new GuiAutomatableType(clazz));
        return t;
    }
    
    GuiAutomatableType(Class clazz)
    {
        m_clazz = clazz;
        try
        {
            AutomatableClass p = (AutomatableClass)clazz.getAnnotation(AutomatableClass.class);
            m_friendlyName = p.name();
        }
        catch(Exception e)
        {
            // If we can't get the annotation then default to the simple name.
            m_friendlyName = m_clazz.getSimpleName();
        }
    }
    
    public Class GetClass()
    {
        return m_clazz;
    }
    
    @Override
    public String toString()
    {
        return m_friendlyName;
    }
}
