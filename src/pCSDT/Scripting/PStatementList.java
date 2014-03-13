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
 * This class represents a list of statements.  It's used by the scripting
 * system to provide a way of describing an ordered set of statements.  A
 * statement list is similar to a function in that it takes a set of arguments,
 * but dissimilar in that it allows arguments that have Void return types and
 * does permit modification to the stack.
 */
public class PStatementList extends PStatement implements IStatementList {
    /**
     * This constructor composes a new PStatementList, optionally with a
     * list of children bound on it.
     * @param children The child PStatement instances
     */
    public PStatementList(IStatement... children)
    {
        super(PType.Void);
        args = children;
    }
    
    /**
     * Performs in-order execution of the statements described by this statement
     * list.
     * @param stack The current scope stack
     * @return PStatementList always returns a Void variant
     * @throws java.lang.Exception
     */
    @Override public PVariant Execute(PScopeStack stack) throws Exception
    {
        for(int i = 0; i < args.length; i++)
            args[i].Execute(stack);
        return PVariant.Void;
    }

    // Base class overrides:
    @Override
    public void AppendChild(IStatement c)
    {
        AppendArg(c);
    }

    @Override
    public void InsertChild(IStatement c, int index)
    {
        InsertArg(c, index);
    }
    
    //@Override
    public void RemoveChild(int index)
    {
        RemoveArg(index);
    }

    public boolean RemoveChild(IStatement s)
    {
        boolean bFinished = false;
        for (int i = 0; !bFinished && i < args.length; i++) {
            if (s == args[i]) {
                RemoveArg(i);
                return true;
            }
            // check if it is a list
            // if so, need to do recursive check
            if (args[i] instanceof IStatementList) {
                if (((IStatementList)args[i]).RemoveChild(s)) {
                    return true;
                }
            }
            IStatement[] iStmtList = args[i].GetChildren();
            for (int j = 0; j < iStmtList.length; j++) {
                if (iStmtList[j] instanceof IStatementList) {
                    if (((IStatementList)iStmtList[j]).RemoveChild(s)) {
                        return true;
                    }
                }
            }
            
            // looking into the associated list of the current IStatement

        }
        return false;
    }

    /**
     * Remove all children
     */
    public void ClearAll() {
        args = new IStatement[0];
    }

    public boolean IsEmpty() {
        return args.length == 0;
    }
    //@Override
    public void MoveChild(int index, int newIndex)
    {
        MoveArg(index, newIndex);
    }

    @Override
    public JPnlLine GetGui(LayoutInfo info) {return new JPnlLineList(this, info);}

    @Override
    public Element GetXml(String tagName) {
        Element pStList = new Element(tagName);
		pStList.setAttribute("type", "list");
        for(IStatement pS : args)
            pStList.addContent(pS.GetXml("Statement"));
        return pStList;
    }

    @Override
    public boolean SetXml(PEngine context, Element elem)
    {
	args = new IStatement[elem.getChildren().size()];
	int i = 0;
	for(Object obj : elem.getChildren())
	{
            Element child;
            try {child = (Element)obj;}
            catch(Exception e) {
                e.printStackTrace();
                continue;
            }

            IStatement s = PStatement.FromXml(context, child);
            if(s == null)
                return false;
            args[i++] = s;
	}
	return true;
    }

    @Override
    public PStatementList clone()
    {
	PStatementList retVal = new PStatementList();
	retVal.args = new IStatement[args.length];
	for(int i = 0; i < args.length; i++)
            retVal.args[i] = args[i].clone();
	return retVal;
    }

    public boolean HasReturnValue() {return false;}
    public boolean HasSideEffect() {return true;}
}
