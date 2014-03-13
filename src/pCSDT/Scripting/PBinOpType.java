/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting;

/**
 *
 * @author Jason
 */
public class PBinOpType {
    public static String GetOpString(ePBinOpType t)
    {
        switch(t)
        {
        case Add:
            return "+";
        case Subtract:
            return "-";
        case Multiply:
            return "*";
        case Divide:
            return "/";
        case Less:
            return "<";
        case LessEqual:
            return "<=";
        case Equal:
            return "==";
        case NotEqual:
            return "!=";
        case GreaterEqual:
            return ">=";
        case Greater:
            return ">";

        case AND:
            return "AND";
        case OR:
            return "OR";
        }
        return "";
    }

	public static ePBinOpType FromString(String op)
	{
        if(op.compareTo("+") == 0)
			return ePBinOpType.Add;
        if(op.compareTo("-") == 0)
			return ePBinOpType.Subtract;
        if(op.compareTo("*") == 0)
			return ePBinOpType.Multiply;
        if(op.compareTo("/") == 0)
			return ePBinOpType.Divide;
        if(op.compareTo("<") == 0)
        	return ePBinOpType.Less;
        if(op.compareTo("<=") == 0)
			return ePBinOpType.LessEqual;
        if(op.compareTo("==") == 0)
			return ePBinOpType.Equal;
        if(op.compareTo("!=") == 0)
			return ePBinOpType.NotEqual;
        if(op.compareTo(">=") == 0)
			return ePBinOpType.GreaterEqual;
        if(op.compareTo(">") == 0)
			return ePBinOpType.Greater;
        if(op.compareTo("AND") == 0)
			return ePBinOpType.AND;
        if(op.compareTo("OR") == 0)
			return ePBinOpType.OR;
        return ePBinOpType.Unknown;
	}

	public static boolean IsComparison(ePBinOpType op)
	{
		return
			ePBinOpType.Less.ordinal() <= op.ordinal() &&
			op.ordinal() <= ePBinOpType.Greater.ordinal();
	}

	public static boolean IsArithmetic(ePBinOpType op)
	{
		return
			ePBinOpType.Add.ordinal() <= op.ordinal() &&
			op.ordinal() <= ePBinOpType.Divide.ordinal();
	}

	public static boolean IsLogical(ePBinOpType op)
	{
		return
			ePBinOpType.AND.ordinal() <= op.ordinal() &&
			op.ordinal() <= ePBinOpType.OR.ordinal();
	}

	/**
	 * Attempts to infer the type result from the given operation involving the type
	 * of two operands
	 * @param lhs The left operand
	 * @param op The relevant operator
	 * @param rhs The right operand
	 * @return The inferred type, or Unknown if the given operation isn't valid
	 */
	public static PType InferType(ePType lhs, ePBinOpType op, ePType rhs)
	{
		// Allow comparison and concatenation for strings:
		if(
			lhs == ePType.String &&
			rhs == ePType.String
		)
			return (
				IsComparison(op) ||
				op == ePBinOpType.Add
			)?
			PType.String:
			PType.Unknown;

		// Numeric type promotion needed?
		if(PType.IsNumeric(lhs) && PType.IsNumeric(rhs))
		{
            if(!IsArithmetic(op)) {
                // may be comparison
                if (IsComparison(op)) {
                    return PType.Boolean;
                }
                return PType.Unknown;
            }
			if(lhs == ePType.Integer && rhs == ePType.Integer) {
                return PType.Integer;
            }
            return PType.Float;
		}

		// Binary operation?
		if(lhs == ePType.Boolean && rhs == ePType.Boolean && IsLogical(op))
			return PType.Boolean;

		// No idea, give up.
		return PType.Unknown;
	}
}
