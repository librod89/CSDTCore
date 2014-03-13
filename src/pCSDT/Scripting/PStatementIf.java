/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting;
import pCSDT.Scripting.SCElem.*;
import org.jdom.Element;

/**
 *
 * @author Richard
 */

// This class implements a simple IF-THEN Statement

public class PStatementIf extends PStatementControl
{
    public PStatementIf()
    {
	this(new PStatementNull(), new PStatementList(), new PStatementList());
    }

    public PStatementIf(IStatement cond, IStatementList ifTrue)
    {
	this(cond, ifTrue, new PStatementList());
    }
    
    public PStatementIf(IStatement cond, IStatementList ifTrue, IStatementList ifFalse)
    {
        super(PType.Void);
        args = new IStatement[3];
        args[0] = cond;
        args[1] = ifTrue;
        args[2] = ifFalse;  //args[2] is null if the else branch is to be skipped
    }
    
    // This method executes the StatementList if the condition is true
    // returns void
    @Override
    public PVariant Execute(PScopeStack scope) throws Exception
    {
        if (args[0] != null)
            if (args[0].Execute(scope).IsTrue()) {
                if (args[1] != null) {
                    args[1].Execute(scope);
                }
            }
            else {
                if (args[2] != null) {
                    args[2].Execute(scope);
                }
            }
        return PVariant.Void;
    }

    @Override
    public IStatement GetCondition() {return args[0];}
    
    @Override
    public IStatementList[] GetBodies() {return new IStatementList[]{(IStatementList)args[1], (IStatementList)args[2]};}

    /**
     * @return True if an else branch exists for this statement
     */
    public boolean HasElseBranch() {
        return args[2] != null;
        //return ((IStatementList)args[2]).GetChildren().length != 0;
    }

    @Override
    public JPnlLine GetGui(LayoutInfo info) {return new JPnlLineIf(this, info);}

    @Override
    public Element GetXml(String name)
    {
        Element root = new Element("name");
		root.setAttribute("type", "if");
        root.addContent(args[0].GetXml("Cond"));
        root.addContent(args[1].GetXml("True"));
		if(args[2] != null)
			root.addContent(args[2].GetXml("False"));
        return root;
    }

    @Override
    public void SetCondition(IStatement s) {args[0] = s;}

    @Override
    public void SetBody(int i, IStatement s)
    {
	switch(i)
	{
            case 0:
		args[1] = s;
            case 1:
		args[2] = s;
	}
    }

    @Override
    public boolean SetXml(PEngine context, Element elem)
    {
        Element cond = elem.getChild("Cond");
	args[0] = PStatement.FromXml(context, cond);
	if(cond == null || args[0] == null)
            return false;

	Element ifTrue = elem.getChild("True");
	if(ifTrue == null)
            return false;
        args[1] = PStatement.FromXml(context, ifTrue);
	if(args[1] == null)
            return false;
        
	Element ifFalse = elem.getChild("False");
	if(ifFalse != null) {
            args[2] = PStatement.FromXml(context, ifFalse);
        }
        else {
            args[2] = null;
        }
        return true;
    }

    @Override
    public PStatementIf clone()
    {
        if (GetBodies()[1] == null) {
            return new PStatementIf(
			args[0].clone(),
			GetBodies()[0].clone(),
			null
		);
        }
        else {
            return new PStatementIf(
			args[0].clone(),
			GetBodies()[0].clone(),
			GetBodies()[1].clone()
		);
        }
    }
}
