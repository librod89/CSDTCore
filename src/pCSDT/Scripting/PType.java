/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting;
import java.util.ArrayList;

/**
 *
 * @author Jason
 * This class describes the various types supported by the CSDT framework.
 * PType is used by the IMethodFactory to describe the return type and parameter
 * type information for the IMethod it constructs.
 */
public class PType
{
    private PType(ePType type)
    {
        m_type = type;
        m_enumMembers = null;
    }
    
    /**
     * Constructs a new enumeration PType
     * @param enumMembers An ordered list of the members of the enumeration
     */
    public PType(ArrayList<String> enumMembers)
    {
        m_type = ePType.Enumeration;
        m_enumMembers = enumMembers;
    }
    
    /**
     * Standard values for each of the fundamental types:
     */
    public static final PType Void = new PType(ePType.Void);
    public static final PType String = new PType(ePType.String);
    public static final PType Integer = new PType(ePType.Integer);
    public static final PType Float = new PType(ePType.Float);
    public static final PType Boolean = new PType(ePType.Boolean);
    public static final PType BinaryImage = new PType(ePType.BinaryImage);
    public static final PType Variable = new PType(ePType.Variable);
    public static final PType Obj = new PType(ePType.Obj);
    public static final PType Unknown = new PType(ePType.Unknown);
    
    /**
     * This represents the actual type described by this class.
     */
    private ePType m_type;
    
    /**
     * If m_type is an enumeration, then this will contain an ordered list of
     * the different enumeration members supported by the class.  Note that
     * variant types will use their integer field to index this arraylist.
     * This element is the reason we don't simply use a ePType by itself when
     * we need to distinguish type.
     */
    private ArrayList<String> m_enumMembers;
    
    /**
     * @return An arraylist of the enumeration members, or null
     * This method is used by the GUI, and by the Variant type to provide string
     * representations of an enumeration.  It only returns a meaningful response
     * if m_type happens to be ePType.Enumeration.
     */
    public ArrayList<String> GetEnumMembers()
    {
        return m_enumMembers;
    }
    
    /**
     * @return The type of that this PType object describes
     */
    public ePType GetType() {return m_type;}
    
    /**
     * Attempts to derive a PType from the passed class type
     * @param clazz The class from which to find the corresponding PType
     * @return The PType type equivalent of the passed class type
     * @throws java.lang.ClassCastException Thrown when no matching PType is found
     */
    public static PType FromClass(Class clazz) throws ClassCastException
    {
        if(clazz == String.class)
            return PType.String;
        if(clazz == int.class || clazz == Integer.class)
            return PType.Integer;
        if(clazz == float.class || clazz == Float.class)
            return PType.Float;
        if(clazz == double.class || clazz == Double.class)
            return PType.Float;
        if(clazz == boolean.class || clazz == Boolean.class)
            return PType.Boolean;
        if (clazz == PBinaryImage.class)
            return PType.BinaryImage;
        if (clazz == void.class)
            return PType.Void;
        if (clazz == PObject.class)
            return PType.Obj;
        if(PObject.class.isAssignableFrom(clazz))
            return PType.Obj;
        if(clazz.isEnum())
        {
            // TODO:  Special handling for enum types
        }
        throw new ClassCastException();
    }

    /**
     *
     * @return True if the specified type is numeric
     */
    public static boolean IsNumeric(ePType type)
    {
	switch(type)
	{
            case Integer:
            case Float:
		return true;
	}
	return false;
    }

    public boolean IsNumeric() {return IsNumeric(m_type);}

    public boolean IsString() {return m_type == ePType.String;}

    public boolean IsBinaryImage() {return m_type == ePType.BinaryImage;}

    public boolean IsBoolean() {return m_type == ePType.Boolean;}

    public boolean IsVoid() {return m_type == ePType.Void;}

    ///
    // Base class overrides
    ///
    @Override
    public String toString()
    {
	switch(m_type)
	{
            case String:
		return "string";
            case Integer:
		return "integer";
            case Float:
		return "float";
            case Boolean:
		return "boolean";
            case BinaryImage:
                return "binaryimage";
            case Void:
		return "void";
            case Obj:
		return "obj";
            case Variable:
                return "variable";
	}
	return "";
    }

    /**
     * From a string representation to its respective pType
     * @param s
     * @return
     */
    public static ePType valueOf(String s) {
        if (s.compareToIgnoreCase("string")==0) return ePType.String;
        else if (s.compareToIgnoreCase("integer")==0) return ePType.Integer;
        else if (s.compareToIgnoreCase("float")==0) return ePType.Float;
        else if (s.compareToIgnoreCase("boolean")==0) return ePType.Boolean;
        else if (s.compareToIgnoreCase("binaryImage")==0) return ePType.BinaryImage;
        else if (s.compareToIgnoreCase("void")==0) return ePType.Void;
        else if (s.compareToIgnoreCase("PObject")==0) return ePType.Obj;
        else if (s.compareToIgnoreCase("obj")==0) return ePType.Obj;
        else if (s.compareToIgnoreCase("variable")==0) return ePType.Variable;
        else return ePType.Unknown;
    }
}
