/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting;

import org.jdom.Element;

/**
 *
 * @author Jason
 * This is the interface used to describe a scripting statement.  Scripting
 * statements are always evaluable and therefore always have a return type.
 * 
 * Note that, before the statement may be executed, all variables in args must
 * be properly resolved.  Note also that the return type is not always strictly
 * known, as is the case for statements that wrap PVariable instances as the
 * type of a PVariable may evolve as the program is executed and the variable
 * is reused for other purposes.  In the cases where the return type is not
 * strictly known, it is left null.
 */
public abstract class PStatement implements IStatement
{
    protected PObject m_obj;  // the PObject associated with this PStatement

    protected PStatement()
    {
	this(PType.Unknown);
    }

    protected PStatement(PType retType)
    {
        m_retType = retType;
    }
    
    /**
     * This represents the arguments bound on this particular statement.  The
     * arguments are bound here so a tree of statements may be statically
     * composed and traversed.  These are always statements; if constants must
     * be used as arguments, they are first wrapped by PStatementConst.
     */
    public IStatement[] args;

    /**
     * The return type of this statement.
     */
    protected PType m_retType;
    
    /**
     * Appends an argument to the argument list.  Not at all efficient, but
     * useful if you don't have any other choice.s
     * @param arg The argument to be appended
     */
    public void AppendArg(IStatement arg)
    {
        // old impl - generic array creation issue
        //args = Utility.resizeArray(args, args.length + 1);
        //args[args.length - 1] = arg;

        // working temporary solution
        IStatement[] newargs = new IStatement[args.length+1];
        System.arraycopy(args, 0, newargs, 0, args.length);
        newargs[args.length] = arg;
        args = newargs;
    }

    /**
     * Inserts an argument at the position specified in the list
     * @param arg The argument to be inserted
     * @param index The index of the argument to be inserted
     */
    public void InsertArg(IStatement arg, int index)
    {
        if(index > args.length) index = args.length;

        IStatement[] newArgs = new IStatement[args.length+1];
        for(int i = args.length; i > index; i--)
            newArgs[i] = args[i-1];
        newArgs[index] = arg;
        for(int i = index - 1; i >= 0; i--)
            newArgs[i] = args[i];
        args = newArgs;
    }

    /**
     * Removes an argument from the list of arguments, shifting the remaining
     * arguments down
     * @param index The index of the argument to be removed
     */
    public void RemoveArg(int index)
    {
        if (args.length == 1)
        {
            args = new IStatement[0];
            return;
        }
            
        IStatement[] newArgs = new IStatement[args.length-1];
        int j = 0;
        for (int i = 0; i < newArgs.length; i++)
        {
            if(j == index) j++;
            newArgs[i] = args[j];
            j++;
        }
        args = newArgs;
    }

    /**
     * Moves an argument from one index in the list and makes its index match
     * the index specified by newIndex
     * @param index The argument to be moved
     * @param newIndex The new index for the argument
     */
    public void MoveArg(int index, int newIndex)
    {
        IStatement argument = args[index];
        RemoveArg(index);
        if (index > newIndex)
            newIndex--;
        InsertArg(argument, newIndex);
    }
    
    /**
     * @return The return type of this method
     */
    @Override
    public PType GetReturnType() {return m_retType;}
    
    /**
     * @return Children of this node
     */
    @Override
    public IStatement[] GetChildren() {return args;}

    /**
     * @return A statement constructed from an XML node, or null if no such
     * statement exists
     */
    public static IStatement FromXml(PEngine context, Element e)
    {
	if(e == null)
            return null;

	IStatement retVal = null;
	String type = e.getAttributeValue("type");
        String object = e.getAttributeValue("object");
        if(type == null && object == null) {
            return null;
        }
        if (type != null) {
            if(type.compareTo("list") == 0)
                retVal = new PStatementList();
            else if (type.compareTo("prop-assign") == 0)
                retVal = new PStatementAssign();
            else if (type.compareTo("variable-assign") == 0)
                retVal = new PStatementAssign();
            else if (type.compareTo("variable") == 0)
                retVal = new PStatementVariable();
            else if(type.compareTo("if") == 0)
                retVal = new PStatementIf();
            else if(type.compareTo("while") == 0)
                retVal = new PStatementWhile();
            else if (type.compareTo("do-forever") == 0)
                retVal = new PStatementDoForever();
            else if(type.compareTo("repeat-n") == 0)
                retVal = new PStatementRepeatN();
            else if(type.compareTo("method") == 0)
                retVal = new PStatementMethod();
            else if(type.compareTo("binary") == 0)
                retVal = new PStatementBinary();
            else if(type.compareTo("const") == 0)
                retVal = new PStatementConst();
            else if(type.compareTo("dynamicConst") == 0) {
                // build the list
                String optionType = e.getAttributeValue("option-type");
                String optionValues = e.getAttributeValue("option-values");

                retVal = context.ProduceStatement(optionValues, PType.valueOf(optionType));
            }
            else if(type.compareTo("null") == 0)
                retVal = new PStatementNull();
        }
        else if (object != null) {
            String prop = e.getAttributeValue("prop");
            String r = context.GetObject(object).GetProperty(prop).GetUnboundProperty().GetRunTimeProperties();
            if (r.compareToIgnoreCase("R") == 0)
                retVal = new PStatementReadOnlyProperty();
            else
                retVal = new PStatementProperty();
        }
        else {
            return null;
        }
        if(retVal.SetXml(context, e)) {
            return retVal;
        }
        return null;
    }

    public static Element XmlFromAssignable(String tagName, IAssignable assign)
    {
	if(assign instanceof PStatementVariable)
	{
            PStatementVariable lVal;
            try {lVal = (PStatementVariable)assign;}
            catch(Exception e) {
                e.printStackTrace();
                return null;
            }

            Element root = new Element(tagName);
            root.setAttribute("type", "variable");
            root.setAttribute("name", lVal.GetName());
            return root;
	}
	if(assign instanceof PProperty)
	{
            PProperty prop;
            try {prop = (PProperty)assign;}
            catch(Exception e) {
                e.printStackTrace();
                return null;
            }

            Element root = new Element(tagName);
            root.setAttribute("type", "property");
            root.setAttribute("obj", prop.GetObject().GetName());
            root.setAttribute("prop", prop.GetUnboundProperty().GetName());
            return root;
	}
	return null;
    }

    public static INamedAssignable AssignableFromXml(PEngine context, Element e)
    {
	////INamedAssignable retVal;
	String name = e.getAttributeValue("name");
	if(name == null)
            return null;
	return new PStatementVariable(name);
    }

    /**
    * Change the association of methods originally to oldObj to newObj
    * @param oldObj the old PObject
    * @param newObj the new PObject
    */
    public void AssociateNullIdentityMethodTo(PObject newObj)
    {
        m_obj = newObj;
        if (args != null) {
            for(IStatement i : args)
                if (i != null)
                    i.AssociateNullIdentityMethodTo(newObj);
        }
    }

    @Override
    public abstract IStatement clone();
}
