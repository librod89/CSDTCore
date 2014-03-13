/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting;

import java.lang.reflect.*;

/**
 * Similar to PMethodUnbound, PEventListUnbound is used to describe an event and
 * is not bound to an active object instance.
 * @author tylau
 */
public class PEventListUnbound extends PFieldUnbound {

    protected AutomatableEventList m_anno;
    protected String m_name;
    protected String m_desc;
    protected boolean m_bIsDefault = false;

    public PEventListUnbound(Field f, AutomatableEventList anno) {
        super(f);
        m_anno = anno;
        m_name = anno.name();
        m_desc = anno.desc();
    }

    public boolean IsDefault() {return m_bIsDefault;}

    public void SetDefault(boolean bIsDefault) {m_bIsDefault = bIsDefault;}

    @Override public String GetName() {return m_name;}

    @Override public String GetDesc() {return m_desc;}

    public PType[] GetArgTypes() {
        String[] typeStrs = m_anno.argTypes();
        PType[] types = new PType[typeStrs.length];
        for (int i = 0; i < types.length; i++) {
            if (typeStrs[i].compareToIgnoreCase("int") == 0)
                types[i] = PType.Integer;
            else if (typeStrs[i].compareToIgnoreCase("float") == 0)
                types[i] = PType.Float;
            else if (typeStrs[i].compareToIgnoreCase("boolean") == 0)
                types[i] = PType.Boolean;
            else if (typeStrs[i].compareToIgnoreCase("String") == 0)
                types[i] = PType.String;
            else if (typeStrs[i].compareToIgnoreCase("PObject") == 0 || typeStrs[i].compareToIgnoreCase("Object") == 0)
                types[i] = PType.Obj;
            else
                types[i] = PType.Unknown;
        }
        return types;
    }

    public String[] GetArgNames() {return m_anno==null?null:m_anno.argNames();}

    public String[] GetArgDesc() {return m_anno==null?null:m_anno.argDesc();}

    public String[] GetArgVals() {return m_anno==null?null:m_anno.argVals();}
                    
    public int GetArgCount() {return m_anno==null?null:m_anno.argTypes().length;}

    public int GetArity() {return m_anno==null?null:m_anno.argTypes().length;}

    @Override public String toString() {return m_name;}

}
