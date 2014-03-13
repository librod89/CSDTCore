/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pCSDT.Scripting;

import pCSDT.Scripting.SCElem.*;
import org.jdom.Element;

/**
 *
 * @author Jason
 * This is the implementation of a while statement.
 */
public class PStatementWhile extends PStatementControl
{
    public PStatementWhile()
    {
        this(null, null);
    }

    public PStatementWhile(IStatement condition, IStatementList operand)
    {
        super(PType.Void);
        args = new IStatement[2];
        args[0] = condition;
        args[1] = operand;
    }

    @Override
    public IStatement GetCondition() {return args[0];}

    @Override
    public IStatementList[] GetBodies() {return new IStatementList[]{(IStatementList)args[1]};}

    @Override
    public void SetBody(int i, IStatement s)
    {
	switch(i)
	{
            case 0:
		args[1] = s;
	}
    }

    @Override
    public void SetCondition(IStatement s) {args[0] = s;}
	
    @Override
    public PVariant Execute(PScopeStack scope) throws Exception
    {
        if (args[0] != null)
            while (args[0].Execute(scope).IsTrue() && args[1] != null)
                args[1].Execute(scope);
        return PVariant.Void;
    }

    @Override
    public JPnlLine GetGui(LayoutInfo info) {return new JPnlLineWhile(this, info);}

    @Override
    public Element GetXml(String tagName)
    {
        Element root = new Element(tagName);
		root.setAttribute("type", "while");
        root.addContent(args[0].GetXml("Condition"));
        root.addContent(args[1].GetXml("Do"));
        return root;
    }

    @Override
    public boolean SetXml(PEngine context, Element e)
    {
	Element cond = e.getChild("Condition");
	Element list = e.getChild("Do");
	if(cond == null || list == null)
            return false;
	args[0] = PStatement.FromXml(context, cond);
	args[1] = PStatement.FromXml(context, list);
	return args[0] != null && args[1] != null;
    }

    @Override
    public PStatementWhile clone()
    {
	return new PStatementWhile(
		GetCondition().clone(),
		GetBodies()[0].clone()
	);
    }
}

