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
public class PPropertyUnbound extends PFieldUnbound {
    protected PPropertyUnbound(PType type, Field field, AutomatableProperty anno)
            throws IllegalArgumentException
    {
        super(field);
        m_type = type;
        m_name = anno.name().length() == 0?field.getName():anno.name();
        m_displayName = anno.DisplayName();
        m_desc = anno.desc();
        //m_group = anno.group();
        m_designTimeBehavior = anno.DesignTimeBehavior();
        m_runTimeBehavior = anno.RunTimeBehavior();
        m_anno = anno;
    }
    
    PType m_type;
    String m_name;
    String m_displayName;
    String m_desc;
    //String m_group;
    String m_designTimeBehavior;
    String m_runTimeBehavior;
    AutomatableProperty m_anno;
    
    // Accessor methods:
    public PType GetType() {return m_type;}
    @Override public String GetName() {return m_name;}
    public String GetDisplayName() {
        if (m_displayName.equals("")) return m_name;
        else return m_displayName;
    }
    @Override public String GetDesc() {return m_desc;}
    //@Override public String GetGroup() {return m_group;}
    public String GetDesignTimeProperties() {return m_designTimeBehavior;}
    public String GetRunTimeProperties() {return m_runTimeBehavior;}
    
    ///
    // Base class overrides
    ///
    @Override
    public String toString() {return m_name;}
}
