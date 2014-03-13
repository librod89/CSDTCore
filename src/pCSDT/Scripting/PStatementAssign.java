/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting;
import org.jdom.Element;
import pCSDT.Scripting.SCElem.*;

/**
 *
 * @author Jason
 */
public class PStatementAssign extends PStatement
{
    public PStatementAssign()
    {
        args = new IStatement[2];
    }

    public PStatementAssign(IStatement rh)
    {
        args = new IStatement[2];
        args[1] = rh;
    }

    public PStatementAssign(IStatement lh, IStatement rh)
    {
        super(PType.Unknown);
        args = new IStatement[2];
        args[0] = lh;
        args[1] = rh;
    }

    public PStatementAssign(PProperty prop, IStatement rh)
    {
	this(new PStatementProperty(prop), rh);
    }

    public IStatement GetLhs() {return args[0];}

    public void SetLhs(IStatement s) { args[0] = s;}

    public IStatement GetRhs() {return args[1];}

    public void SetRhs(IStatement s) { args[1] = s;}

    @Override
    public PVariant Execute(PScopeStack stk) throws Exception
    {
        PStatementVariable pVariableName = null;
        if (args[0] instanceof PStatementProperty) {
            pVariableName = new PStatementVariable(((PStatementProperty)args[0]).GetProperty().GetName());
            PVariant retVal = pVariableName.Assign(stk, args[1].Execute(stk));
            ((PStatementProperty)args[0]).GetProperty().SetValue(retVal);
            m_retType = retVal.GetType();
            return retVal;
        }
        else if (args[0] instanceof PStatementVariable) {
            pVariableName = new PStatementVariable(((PStatementVariable)args[0]).GetName());
            PVariant retVal = pVariableName.Assign(stk, args[1].Execute(stk));
            ((PStatementVariable)args[0]).Assign(stk, retVal);
            m_retType = retVal.GetType();
            return retVal;
        }
        return null;
    }

    @Override
    public JPnlLine GetGui(LayoutInfo info) {
        return new JPnlLineAssign(this, info);
    }

    @Override
    public Element GetXml(String tagName)
    {
        if (args[0] instanceof PStatementProperty) {
            Element root = new Element(tagName);
            root.setAttribute("type", "prop-assign");
            root.addContent(args[0].GetXml("LValue"));
            root.addContent(args[1].GetXml("RValue"));
            return root;
        }
        else if (args[0] instanceof PStatementVariable) {
            Element root = new Element(tagName);
            root.setAttribute("type", "variable-assign");
            root.addContent(args[0].GetXml("LValue"));
            root.addContent(args[1].GetXml("RValue"));
            return root;
        }
        else {
            return null;
        }
    }

    @Override
    public boolean SetXml(PEngine context, Element elem)
    {
        if (elem.getAttributeValue("type").equals("prop-assign")) {
            Element lhs = elem.getChild("LValue");
            Element rhs = elem.getChild("RValue");
            if(lhs == null || rhs == null)
                return false;

            args[0] = PStatement.FromXml(context, lhs);
            args[1] = PStatement.FromXml(context, rhs);
            return args[0] != null && args[1] != null;
        }
        else if (elem.getAttributeValue("type").equals("variable-assign")) {
            Element lhs = elem.getChild("LValue");
            Element rhs = elem.getChild("RValue");
            if(lhs == null || rhs == null)
		return false;

            args[0] = PStatement.FromXml(context, lhs);
            args[1] = PStatement.FromXml(context, rhs);
            return args[0] != null && args[1] != null;
        }
        else {
            return false;
        }
    }

    @Override
    public PStatementAssign clone()
    {
	return new PStatementAssign(
		args[0] == null? null:args[0].clone(),
                args[1] == null? null:args[1].clone()
		);
    }

    public boolean HasReturnValue() {return false;}
    public boolean HasSideEffect() {return true;}
}
