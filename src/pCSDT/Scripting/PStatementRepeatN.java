/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting;

import org.jdom.Element;
import pCSDT.Scripting.SCElem.*;

/**
 * This is a specialization of PStatementWhile in that it repeats the contained expressions
 * a fixed integer number of times.
 *
 * @author Jason Sanchez
 */
public class PStatementRepeatN extends PStatementControl
{
	public PStatementRepeatN()
	{
		this(new PStatementConst(1), new PStatementList());
	}

	public PStatementRepeatN(IStatement n, IStatementList body)
	{
		super(PType.Void);
		args = new PStatement[2];
		args[0] = n;
		args[1] = body;
	}

	@Override
	public PStatementRepeatN clone()
	{
		return new PStatementRepeatN(
			GetCondition().clone(),
			GetBodies()[0].clone()
		);
	}

    @Override
    public Element GetXml(String tagName)
    {
        Element root = new Element(tagName);
		root.setAttribute("type", "repeat-n");
        root.addContent(args[0].GetXml("Value"));
        root.addContent(args[1].GetXml("Do"));
        return root;
    }

	@Override
	public void SetCondition(IStatement s) {args[0] = s;}

	@Override
	public boolean SetXml(PEngine context, Element e)
	{
		Element cond = e.getChild("Value");
		Element list = e.getChild("Do");
		if(cond == null || list == null)
			return false;
		args[0] = PStatement.FromXml(context, cond);
		args[1] = PStatement.FromXml(context, list);
		return args[0] != null && args[1] != null;
	}

	public PVariant Execute(PScopeStack scope) throws Exception
	{
		PVariant retVal = args[0].Execute(scope);
		if(!retVal.IsNumeric())
			return PVariant.Void;
		for(int i = (int)retVal.toNumber(); i > 0; i--)
                    if (args[1] != null)
                        args[1].Execute(scope);
		return PVariant.Void;
	}

	public JPnlLineRepeatN GetGui(LayoutInfo info)
	{
		return new JPnlLineRepeatN(this, info);
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
}
