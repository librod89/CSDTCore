/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting;

import pCSDT.Scripting.SCElem.*;
import org.jdom.Element;
import org.jdom.Text;

/**
 *
 * @author Jason
 * This is a binary statement.
 */
public class PStatementBinary extends PStatement {
    public PStatementBinary()
    {
        args = new IStatement[2];
    }

    public PStatementBinary(ePBinOpType binType)
    {
        this(PType.Void, binType);
    }
    
    public PStatementBinary(PType m_retType, ePBinOpType binType)
    {
        super(m_retType);
        m_binType = binType;
        args = new IStatement[2];
    }
    
    public PStatementBinary(IStatement lhs, IStatement rhs, ePBinOpType binType)
    {
        super(PType.Void);
        m_binType = binType;
        args = new IStatement[2];
        args[0] = lhs;
        args[1] = rhs;
    }

    /**
     * This is the binary operation type of this statement.
     */
    ePBinOpType m_binType;

    public ePBinOpType GetOperator(){return m_binType;}

    /**
     *
     * @return The left-hand side of the operator
     */
    public IStatement GetLhs() {return args[0];}
    
    /**
     * 
     * @return The right-hand side of the operator
     */
    public IStatement GetRhs() {return args[1];}

    /**
     *
     * @return The binary operator type
     */
    public ePBinOpType GetOpType() {return m_binType;}

    /**
     * @brief Sets the new operation type
     * @param t The new binary operation type
     */
    public void SetOpType(ePBinOpType t) {m_binType = t;}

    /**
     * Sets the left hand side of the expression
     * @param s The new right-hand side
     */
    public void SetLhs(IStatement s) {args[0] = s;}

    /**
     * Sets the right hand side of the expression
     * @param s The new right-hand side
     */
    public void SetRhs(IStatement s) {args[1] = s;}
	
    @Override
    public PType GetReturnType()
    {
	switch(m_binType)
	{
            case Equal:
            case Less:
            case LessEqual:
            case Greater:
            case GreaterEqual:
            case NotEqual:
            case AND:
            case OR:
		return PType.Boolean;
            case Add:
            case Subtract:
            case Multiply:
            case Divide:
		return PType.Float;
	}
	return super.GetReturnType();
    }

    @Override public PVariant Execute(PScopeStack scope) throws Exception
    {
        // Evaluate our two statements and return the combined result.
        return PVariant.BinaryOp(
			args[0].Execute(scope),
			m_binType,
			args[1].Execute(scope)
		);
    }

    @Override
    public JPnlLine GetGui(LayoutInfo info) {return new JPnlLineBinary(this, info);}

    @Override
    public Element GetXml(String tagName)
    {
        Element root = new Element(tagName);
	root.setAttribute("type", "binary");
	root.addContent(args[0].GetXml("Lhs"));

	Text op = new Text("Op");
	op.setText(PBinOpType.GetOpString(m_binType));
	root.addContent(op);

	root.addContent(args[1].GetXml("Rhs"));
        return root;
    }

    @Override
    public boolean SetXml(PEngine context, Element elem)
    {
	Element lhs = elem.getChild("Lhs");
	//Element op = elem.getChild("Op");
	Element rhs = elem.getChild("Rhs");
	if(lhs == null || rhs == null)
            return false;

        args[0] = PStatement.FromXml(context, lhs);
        m_binType = PBinOpType.FromString(elem.getTextTrim());
        args[1] = PStatement.FromXml(context ,rhs);
        if(
            args[0] == null &&
            m_binType == ePBinOpType.Unknown &&
            args[1] == null
	) {
            return false;
        }
        m_retType = PBinOpType.InferType(
			args[0].GetReturnType().GetType(),
			m_binType,
			args[1].GetReturnType().GetType()
		);
        //// on SetXML(), we have no way to check the value assigned to variable
        //// because we don't assign value to them until run time
        ////return m_retType != PType.Unknown;
        return true;
    }

    @Override
    public PStatementBinary clone()
    {
	return new PStatementBinary(
		args[0] == null ? null : args[0].clone(),
		args[1] == null ? null : args[1].clone(),
		m_binType
	);
    }

    public boolean HasReturnValue() {return true;}
    public boolean HasSideEffect() {return false;}
}
