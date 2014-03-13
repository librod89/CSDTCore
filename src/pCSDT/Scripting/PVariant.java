/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting;

import java.security.InvalidParameterException;
import java.lang.reflect.Field;

/**
 *
 * @author Jason
 * This represents the variant datatype supported by the CSDT framework.
 * The variant type always describes constant data, but unlike a traditional
 * variant type, is also designed to support constraints on this data.
 * 
 * For the purpose of utility, PVariant implements the IStatement interface.
 * This allows the PVariant to be placed where PStatement is usually expected,
 * something that prevents the need to use PStatementConst in testing situations.
 */
public final class PVariant implements IAssignable {
    public PVariant(PType type) {m_type = type;}
    public PVariant(Object obj) {Assign(obj);}
    public PVariant(PVariant var) {Assign(var);}
    public PVariant(int iValue) {Assign(iValue);}
    public PVariant(Integer i) {Assign(i.intValue());}
    public PVariant(float fValue) {Assign(fValue);}
    public PVariant(Float f) {Assign(f.floatValue());}
    public PVariant(boolean bValue) {Assign(bValue);}
    public PVariant(Boolean b) {Assign(b.booleanValue());}
    public PVariant(String sValue) {Assign(sValue);}
    public PVariant(PBinaryImage biValue) {Assign(biValue);}
    public PVariant(PVariant lhs, ePBinOpType op, PVariant rhs) {Assign(lhs, op, rhs);}
    public PVariant(Object obj, Field f)
	throws ClassCastException, IllegalAccessException
    {
	Assign(obj, f);
    }
    public static final PVariant Void = new PVariant(PType.Void);
    
    /**
     * This describes the actual type of the object.  For enums, this contains
     * the string representation of each enumeration member.  This value is
     * protected rather than private because PVariable may need to change the
     * type during an assignment operation.
     */
    protected PType m_type;
    
    /**
     * This member stores data for integer values, but is also used as the index
     * when the type of the variant is an enumeration.
     */
    public int iValue;
    
    /**
     * Data store for floating-point variant types
     */
    public float fValue;
    
    /**
     * Data store for floating-point variant types
     */
    public boolean bValue;
    
    /**
     * This stores a string if the type is a string
     */
    public String sValue;
    
    /**
     * This stores a binary value encoded as base64 string
     */
    public PBinaryImage biValue;

    /**
     * This stores an object if the type is object
     */
    public PObject oValue;

    /**
     * This stores the bound field if the type is an l-value
     */
    public PVariable lValue;
    
    /**
     * 
     * @return The type of this variant
     */
    @Override
    public PType GetType(PScopeStack stk) {return GetType();}
    public PType GetType() {return m_type;}
    
    /**
     * 
     * @return The enumeration type of this variant
     */
    public ePType GetTypeVal() {return m_type.GetType();}
    
    /**
     * 
     * @return True if this type is a numeric type
     */
    public boolean IsNumeric()
    {
        switch(GetTypeVal())
        {
        case Float:
        case Integer:
            return true;
        }
        return false;
    }
    
    public boolean IsFloat() {return GetTypeVal() == ePType.Float;}
    public boolean IsInteger() {return GetTypeVal() == ePType.Integer;}
    public boolean IsString() {return GetTypeVal() == ePType.String;}
    public boolean IsBoolean() {return GetTypeVal() == ePType.Boolean;}
    public boolean IsBinaryImage() {return GetTypeVal() == ePType.BinaryImage;}

    /**
     * Attempt to resolve variable names, if necessary, to the local scope
     * @param stk The scope stack to consider
     * @return False if the variable name could not be resolved, true if resolution succeeded or was unnecessary.
     */
    public boolean Resolve(PScopeStack stk)
    {
        if(m_type == PType.Variable)
            return true;
        return false;
    }
    
    public boolean IsTrue() throws java.lang.ClassCastException {
        if(m_type.GetType() == ePType.Boolean)
            return bValue;
        throw new java.lang.ClassCastException();
    }
    
    /**
     *
     * @return A string representation of this variant
     */
    @Override
    public String toString()
    {
        switch(m_type.GetType())
        {
            case Void:
                return "";
            case String:
                return sValue;
            case Integer:
                return (new Integer(iValue)).toString();
            case Float:
                return (new Float(fValue)).toString();
            case BinaryImage:
                return biValue.toString();
            case Enumeration:
                return m_type.GetEnumMembers().get(iValue);
            case Obj:
                return oValue.toString();
                    case Boolean:
                            if(bValue)
                                    return "true";
                            else
                                    return "false";
            default:
                return "";
        }
    }

    public double toNumber() throws ClassCastException
    {
	switch(m_type.GetType())
	{
            case Integer:
		return iValue;
            case Float:
		return fValue;
            default:
		throw new ClassCastException("The type of this variant is not an integer");
	}
    }

    /**
     * Attempts to coerce a value from the input string to the most restrictive
     * available variant
     * @param val The input value to be coerced
     * @return The converted type, which may be a string if the system gives up
     */
    public static PVariant FromString(String val)
    {
        if(val.equals("true"))
            return new PVariant(true);
        if(val.equals("false"))
            return new PVariant(false);
        try {return new PVariant(Integer.parseInt(val));}
        catch(Exception e) {
            try {return new PVariant(Float.parseFloat(val));}
            catch(Exception ex) {
                return new PVariant(val);
            }
        }
    }

    /**
     * Attempts to coerce a value from the input string by matching it to the
     * passed type
     * @param val The input value to be coerced
     * @param type The target type to coerce val into
     * @return The converted type, on success, or else null
     */
    public static PVariant FromString(String val, PType type)
            throws NumberFormatException
    {
        switch(type.GetType())
        {
            case Boolean:
                if(val.equals("true"))
                    return new PVariant(true);
                if(val.equals("false"))
                    return new PVariant(false);
                return null;
            case String:
                return new PVariant(val);
            case Integer:
                try {return new PVariant(Integer.parseInt(val));}
                catch(Exception e) {
                    return null;
                }
            case Float:
                try {return new PVariant(Float.parseFloat(val));}
                catch(Exception e) {
                    return null;
                }
            case BinaryImage:
                try {return new PVariant(new PBinaryImage(val));}
                catch (Exception e) {
                    return null;
                }
        }
        return PVariant.Void;
    }
    
    /**
     * Assigns the value of the variant
     * @param rhs The value to which to change this variable
     * @return this
     * Note that type compatibility checking is not performed.  Variants are
     * designed to contain values of varying data, thus it is quite legal to
     * reassign a value that was of type String to type Integer.
     */
    @Override
    public PVariant Assign(PScopeStack stk, PVariant rhs)
    {
        m_type = rhs.GetType(stk);
        iValue = rhs.iValue;
        fValue = rhs.fValue;
        bValue = rhs.bValue;
        sValue = rhs.sValue;
        biValue = rhs.biValue;
        oValue = rhs.oValue;
        return this;
    }
    
    /**
     * Enables seamless assignment of an Object to the related PVariant
     * @param obj The object to cast to a PVariant type
     * @return this
     * @throws ClassCastException
     */
    public PVariant Assign(Object obj)
            throws ClassCastException
    {
        if(obj instanceof String)
            return Assign((String)obj);
        if(obj instanceof Integer)
            return Assign(((Integer)obj).intValue());
        if(obj instanceof Boolean)
            return Assign(((Boolean)obj).booleanValue());
        if(obj instanceof Float)
            return Assign(((Float)obj).floatValue());
        if(obj instanceof Double)
            return Assign((float)((Double)obj).doubleValue());
        if (obj instanceof PBinaryImage) {
            return Assign((PBinaryImage)obj);
        }
        if(obj instanceof PObject)
            return Assign((PObject)obj);
		if(obj instanceof PVariant)
			return Assign((PVariant)obj);
        if(obj == null)
            return Void;
        throw new ClassCastException();
    }

    public PVariant Assign(PVariant rhs)
    {
	m_type = rhs.m_type;
	iValue = rhs.iValue;
	fValue = rhs.fValue;
	bValue = rhs.bValue;
	sValue = rhs.sValue;
        if (rhs.biValue != null) {
            biValue = rhs.biValue.clone();
        }
        else {
            biValue = null;
        }
        oValue = rhs.oValue;
	return this;
    }

    public PVariant Assign(int iValue)
    {
        m_type = PType.Integer;
        this.iValue = iValue;
        return this;
    }
    
    public PVariant Assign(float fValue)
    {
        m_type = PType.Float;
        this.fValue = fValue;
        return this;
    }
    
    public PVariant Assign(boolean bValue)
    {
        m_type = PType.Boolean;
        this.bValue = bValue;
        return this;
    }
    
    public PVariant Assign(String sValue)
    {
        m_type = PType.String;
        this.sValue = sValue;
        return this;
    }

    public PVariant Assign(PBinaryImage biValue)
    {
        m_type = PType.BinaryImage;
        this.biValue = biValue.clone();
        return this;
    }
    
    public PVariant Assign(PObject oValue)
    {
        m_type = PType.Obj;
        this.oValue = oValue;
        return this;
    }
    
    /**
     * Constructs a variant from a binary operation of two other variants
     * @param lhs The left-side operation of the binary op
     * @param op The operation to be performed
     * @param rhs The right-side operation of the binary op
     * @throws ClassCastException If the operation cannot be performed based on the types of the parameters
     */
    public PVariant Assign(PVariant lhs, ePBinOpType op, PVariant rhs)
         throws ClassCastException, InvalidParameterException
    {
        switch(op)
        {
        case Add:
            // Add is a special case, being supported between a pair of string
            // types as a synonym for concatenation.
            if(lhs.IsString() && rhs.IsString())
                return Assign(lhs.sValue + rhs.sValue);
        case Subtract:
        case Multiply:
        case Divide:
            // We can now perform the operation.
            if(lhs.IsInteger())
                if(rhs.IsInteger())
                    switch(op)
                    {
                    case Add:
                        return Assign(lhs.iValue + rhs.iValue);
                    case Subtract:
                        return Assign(lhs.iValue - rhs.iValue);
                    case Multiply:
                        return Assign(lhs.iValue * rhs.iValue);
                    case Divide:
                        return Assign(lhs.iValue / rhs.iValue);
                    }
                else if(rhs.IsFloat())
                    switch(op)
                    {
                    case Add:
                        return Assign(lhs.iValue + rhs.fValue);
                    case Subtract:
                        return Assign(lhs.iValue - rhs.fValue);
                    case Multiply:
                        return Assign(lhs.iValue * rhs.fValue);
                    case Divide:
                        return Assign(lhs.iValue / rhs.fValue);
                    }
                else throw new ClassCastException();
            else if(lhs.IsFloat())
                if(rhs.IsInteger())
                    switch(op)
                    {
                    case Add:
                        return Assign(lhs.fValue + rhs.iValue);
                    case Subtract:
                        return Assign(lhs.fValue - rhs.iValue);
                    case Multiply:
                        return Assign(lhs.fValue * rhs.iValue);
                    case Divide:
                        return Assign(lhs.fValue / rhs.iValue);
                    }
                else if(rhs.IsFloat())
                    switch(op)
                    {
                    case Add:
                        return Assign(lhs.fValue + rhs.fValue);
                    case Subtract:
                        return Assign(lhs.fValue - rhs.fValue);
                    case Multiply:
                        return Assign(lhs.fValue * rhs.fValue);
                    case Divide:
                        return Assign(lhs.fValue / rhs.fValue);
                    }
            else throw new ClassCastException();
            break;
        // Boolean:
            
        case AND:    
            if(lhs.IsBoolean()&&rhs.IsBoolean()){
                return Assign(lhs.bValue&&rhs.bValue);
            }
            else throw new ClassCastException();

            
        case OR:
            if(lhs.IsBoolean()&&rhs.IsBoolean()){
                return Assign(lhs.bValue||rhs.bValue);
            }
            else throw new ClassCastException();

            
        // Comparison:
        case Less:
        case LessEqual:
        case Equal:
        case NotEqual:
        case GreaterEqual:
        case Greater:
            // We can now perform the operation.
            if(lhs.IsInteger())
                if(rhs.IsInteger())
                    switch(op)
                    {
                    case Less:
                        return Assign(lhs.iValue < rhs.iValue);
                    case LessEqual:
                        return Assign(lhs.iValue <= rhs.iValue);
                    case Equal:
                        return Assign(lhs.iValue == rhs.iValue);
                    case NotEqual:
                        return Assign(lhs.iValue != rhs.iValue);
                    case GreaterEqual:
                        return Assign(lhs.iValue >= rhs.iValue);
                    case Greater:
                        return Assign(lhs.iValue > rhs.iValue);
                    }
                else if(rhs.IsFloat())
                    switch(op)
                    {
                    case Less:
                        return Assign(lhs.iValue < rhs.fValue);
                    case LessEqual:
                        return Assign(lhs.iValue <= rhs.fValue);
                    case Equal:
                        return Assign(lhs.iValue == rhs.fValue);
                    case NotEqual:
                        return Assign(lhs.iValue != rhs.fValue);
                    case GreaterEqual:
                        return Assign(lhs.iValue >= rhs.fValue);
                    case Greater:
                        return Assign(lhs.iValue > rhs.fValue);
                    }
                else throw new ClassCastException();
            else if(lhs.IsFloat())
                if(rhs.IsInteger())
                    switch(op)
                    {
                    case Less:
                        return Assign(lhs.fValue < rhs.iValue);
                    case LessEqual:
                        return Assign(lhs.fValue <= rhs.iValue);
                    case Equal:
                        return Assign(lhs.fValue == rhs.iValue);
                    case NotEqual:
                        return Assign(lhs.fValue != rhs.iValue);
                    case GreaterEqual:
                        return Assign(lhs.fValue >= rhs.iValue);
                    case Greater:
                        return Assign(lhs.fValue > rhs.iValue);
                    }
                else if(rhs.IsFloat())
                    switch(op)
                    {
                    case Less:
                        return Assign(lhs.fValue < rhs.fValue);
                    case LessEqual:
                        return Assign(lhs.fValue <= rhs.fValue);
                    case Equal:
                        return Assign(lhs.fValue == rhs.fValue);
                    case NotEqual:
                        return Assign(lhs.fValue != rhs.fValue);
                    case GreaterEqual:
                        return Assign(lhs.fValue >= rhs.fValue);
                    case Greater:
                        return Assign(lhs.fValue > rhs.fValue);
                    }
                else throw new ClassCastException();
            else if(lhs.IsString() && rhs.IsString())
                switch(op)
                {
                case Less:
                    return Assign(lhs.sValue.compareTo(rhs.sValue) < 0);
                case LessEqual:
                    return Assign(lhs.sValue.compareTo(rhs.sValue) <= 0);
                case Equal:
                    return Assign(lhs.sValue.compareTo(rhs.sValue) == 0);
                case NotEqual:
                    return Assign(lhs.sValue.compareTo(rhs.sValue) != 0);
                case GreaterEqual:
                    return Assign(lhs.sValue.compareTo(rhs.sValue) >= 0);
                case Greater:
                    return Assign(lhs.sValue.compareTo(rhs.sValue) > 0);
                }
            else throw new ClassCastException();
        default:
            // Assignment not supported in this variant type, because it requires
            // that the identity of this clsas and of lhs are equivalent--a clearly
            // impossible task, as lhs must be a variable for this to work.
            throw new InvalidParameterException();
        }
        return this;
    }
    
    /**
     * Assigns the value of this variant based on the type value of the
     * specified field value.
     * @param obj The parent object on which to invoke f
     * @param f The field to take from obj
     * @return this
     * @throws ClassCastException
     * @throws IllegalAccessException
     */
    public PVariant Assign(Object obj, Field f)
            throws ClassCastException, IllegalAccessException
    {
        PType t = PType.FromClass(f.getType());
        switch(t.GetType())
        {
            case String:
                sValue = (String)f.get(obj);
                break;
            case Integer:
                iValue = f.getInt(obj);
                break;
            case Float:
                fValue = f.getFloat(obj);
                break;
            case Boolean:
                bValue = f.getBoolean(obj);
                break;
            case BinaryImage:
                biValue = (PBinaryImage)f.get(obj);
            case Obj:
                oValue = (PObject)f.get(obj);
        }
        return this;
    }
    
    /**
     * Attempts to extract the value of this variant to the passed field
     * @param obj
     * @param f
     * @throws java.lang.ClassCastException
     * @throws java.lang.IllegalAccessException
     */
    public void Extract(Object obj, Field f)
            throws ClassCastException, IllegalAccessException
    {
        PType t = PType.FromClass(f.getType());
        switch(t.GetType())
        {
            case String:
                f.set(obj, sValue);
                break;
            case Integer:
                f.setInt(obj, iValue);
                break;
            case Float:
                f.setFloat(obj, fValue);
                break;
            case Boolean:
                f.setBoolean(obj, bValue);
                break;
            case BinaryImage:
                f.set(obj, biValue);
                break;
            case Obj:
                f.set(obj, oValue);
                break;
        }
    }
    
    /**
     * Attempts to box the wrapped type in a general object according to the
     * given ePType
     * @param the desired ePType
     * @return The wrapped type
     * Note that this will return null if the described type is of type Void
     */
    public Object Wrap(ePType type)
    {
        switch(type)
        {
            case String:
                switch (m_type.GetType()) {
                    case String:
                        return sValue;
                    case Integer:
                        return new Integer(iValue).toString();
                    case Float:
                        return new Float(fValue).toString();
                    case Boolean:
                        return bValue?"True":"False";
                }
                break;
            case Integer:
                switch (m_type.GetType()) {
                    case String:
                        return Integer.parseInt(sValue);
                    case Integer:
                        return new Integer(iValue);
                    case Float:
                        return Integer.parseInt(new Float(Math.round(fValue)).toString());
                    case Boolean:
                        return bValue?1:0;
                }
                break;
            case Float:
                switch (m_type.GetType()) {
                    case String:
                        return Float.parseFloat(sValue);
                    case Integer:
                        return new Float(iValue);
                    case Float:
                        return new Float(fValue);
                    case Boolean:
                        return bValue?1f:0f;
                }
                break;
            case Boolean:
                switch (m_type.GetType()) {
                    case String:
                        return Boolean.parseBoolean(sValue);
                    case Integer:
                        return iValue==0?false:true;
                    case Float:
                        return fValue==0?false:true;
                    case Boolean:
                        return bValue;
                }
                break;
            case BinaryImage:
                return biValue;
            case Obj:
                return oValue;
        }
        return null;
    }

    /**
     * Attempts to box the wrapped type in a general object
     * @return The wrapped type
     * Note that this will return null if the described type is of type Void
     */
    public Object Wrap()
    {
        switch(m_type.GetType())
        {
            case String:
                return sValue;
            case Integer:
                return new Integer(iValue);
            case Float:
                return new Float(fValue);
            case Boolean:
                return bValue;
            case BinaryImage:
                return biValue;
            case Obj:
                return oValue;
        }
        return null;
    }
    
    static PVariant BinaryOp(PVariant lhs, ePBinOpType op, PVariant rhs)
    {
        // Create a new variant to hold the result:
        return new PVariant(lhs, op, rhs);
    }

    // Base class overrides:
    @Override
    public PVariant GetValue(PScopeStack stk) {return this;}
}
