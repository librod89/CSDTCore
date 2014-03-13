/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting;

/**
 * @author Jason
 * Enumeration of the supported types by the PVariant class and elsewhere.
 */
public enum ePType
{
    /**
     * This indicates that the type doesn't store any data.  It's used for
     * functions that have no return type, otherwise there'd be no other way to
     * indicate that the function doesn't return anything.
     */
    Void,
    
    /**
     * The following few members indicate string, integer, and floating-point
     * data.
     */
    String,
    Integer,
    Float,
    Boolean,

    /**
     * Binary data
     */
    BinaryImage,

    /**
     * Object is also a supported first-class type--the wrapped type always
     * inherits from PObject
     */
    Obj,
    
    /**
     * Enumerations are a special category, and are reserved for cases where a
     * function may perform one of a few different operations.  The existence of
     * an explicit enumeration like this allows the GUI to query the type for
     * the string representation of the supported operations, rather than making
     * the user remember integer codes for specific operations.
     */
    Enumeration,
    
    /**
     * A variable refers to a value in the scope of execution.  Note that this
     * value does not have any compile time type, as all variables are assumed
     * to be of Variant type.
     */
    Variable,

    /**
     * Unknown types are reserved for variants and statements that were constructed
     * without arguments.  Typeness cannot be inferred in such situations.
     */
    Unknown
}

