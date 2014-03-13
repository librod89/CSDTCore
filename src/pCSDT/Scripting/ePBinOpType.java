/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting;

/**
 *
 * @author Jason
 * This is an enumeration containing all of the supported binary operations
 */
public enum ePBinOpType {
    // Simple arithmetic operations:
    Add,
    Subtract,
    Multiply,
    Divide,
    
    // Comparison:
    Less,
    LessEqual,
    Equal,
    NotEqual,
    GreaterEqual,
    Greater,
    
    // Boolean
    AND,
    OR,

	// Unrecognized optype
	Unknown
}
