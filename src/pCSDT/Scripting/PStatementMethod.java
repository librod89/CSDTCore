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
public final class PStatementMethod extends PStatement
{
    PMethod method;
    IStatement[] defaultStmts;  // default arg values;

    public PStatementMethod()
    {
    }

    public PStatementMethod(PMethod method, IStatement... args)
            throws IllegalArgumentException
    {
        super(method.m_retType);
        this.method = method;

	Class[] c = method.m_method.getParameterTypes();
        String[] argNames = method.GetArgNames();
        PType[] argTypes = method.GetArgTypes();
        String[] argVals = method.GetArgVals();

        // If there are too many terms, throw an error
        if(c.length < args.length)
            throw new IllegalArgumentException("Too many arguments");

        this.args = new IStatement[c.length];
        
	// If there are too few terms, initialize the remanders to blank or default values:
	if(c.length > args.length)
	{
            System.arraycopy(args, 0, this.args, 0, args.length);
            for(int i = args.length; i < c.length; i++) {
                if (argNames.length == argVals.length) {
                    // check if there is a pair of {} at front and end
                    // if not, treat it normally
                    ePType type = argTypes[i].GetType();
                    String trimArgVals = argVals[i].trim();
                    this.args[i] = method.GetObject().GetPEngine().ProduceStatement(trimArgVals, type);
                }
                else {
                    // default argument is emtpy string
                    this.args[i] = new PStatementConst("");
                }
            }
	}
	else {
            System.arraycopy(args, 0, this.args, 0, args.length);
        }
        SetDefaultParameters();
    }

    public void SetDefaultParameters()
            throws IllegalArgumentException
    {
	Class[] c = method.m_method.getParameterTypes();
        String[] argNames = method.GetArgNames();
        PType[] argTypes = method.GetArgTypes();
        String[] argVals = method.GetArgVals();

        this.defaultStmts = new IStatement[c.length];
        
        for(int i = 0; i < c.length; i++) {
            if (argNames.length == argVals.length) {
                // check if there is a pair of {} at front and end
                // if not, treat it normally
                String trimArgVals = argVals[i].trim();
                ePType type = argTypes[i].GetType();
                this.defaultStmts[i] = method.GetObject().GetPEngine().ProduceStatement(trimArgVals, type);
            }
            else {
                // default argument is emtpy string
                this.defaultStmts[i] = new PStatementConst("");
            }
        }
    }
    
    /**
     * Called to actually execute a method
     * @param scope The scope stack in which the method is called
     * @return The result of the method invocation
     * @throws java.lang.Exception
     */
    @Override
    public PVariant Execute(PScopeStack scope) throws Exception
    {
        // Simply pass the call to the method with the bound arguments
        return method.Execute(args, scope);
    }

    public IStatement[] GetArgs() {
        return args;
    }

    public IStatement GetArg(int i) {
        return args[i];
    }

    public IStatement[] GetDefaultArgs() {
        return defaultStmts;
    }

    public IStatement GetDefaultArg(int i) {
        return defaultStmts[i].clone();
    }

    public void SetArg(int i, IStatement s) {
        args[i] = s;
    }

    @Override
    public JPnlLine GetGui(LayoutInfo info) {return new JPnlLineMethod(this, info);}

    public PMethod GetMethod() {return method;}

    @Override
    public Element GetXml(String name)
    {
	PObject pObj = method.GetObject();

	Element root = new Element(name);
	root.setAttribute("type", "method");
	root.setAttribute("obj", pObj.GetClassName());
	root.setAttribute("name", pObj.GetName());
	root.setAttribute("method", method.GetName());
	for(IStatement s : args)
            root.addContent(s.GetXml("Arg"));
        return root;
    }

    @Override
    public boolean SetXml(PEngine context, Element elem)
    {
	String sObj = elem.getAttributeValue("obj");
	String sName = elem.getAttributeValue("name");
	String sMethod = elem.getAttributeValue("method");
	if(sObj == null || sName == null || sMethod == null)
            return false;

	Class c = context.GetObjectType(sObj);
	if(c == null)
            return false;

	{
            PObject obj = context.GetObject(c, sName);
            if(obj == null)
                return false;

            method = obj.GetMethod(sMethod);
            if(method == null)
                return false;
	}

	int i = 0;
	args = new IStatement[elem.getChildren().size()];
	for(Object obj : elem.getChildren())
	{
            Element child;
            try {child = (Element)obj;}
            catch(Exception e) {
                e.printStackTrace();
                continue;
            }
            IStatement p = PStatement.FromXml(context, child);
            if(p == null)
                return false;
            args[i++] = p;
	}
        this.SetDefaultParameters();
	return true;
    }

    @Override
    public PStatementMethod clone()
    {
	PStatementMethod retVal = new PStatementMethod();
        PMethod pMethod = GetMethod();
	retVal.method = new PMethod(pMethod.GetName(), pMethod.GetObject(), pMethod.m_method);
	retVal.args = new IStatement[args.length];
        retVal.defaultStmts = new IStatement[args.length];
        retVal.m_retType = GetReturnType();
	for(int i = 0; i < args.length; i++) {
            retVal.args[i] = args[i].clone();
            retVal.defaultStmts = defaultStmts.clone();
        }
	return retVal;
    }

    /**
     * This method copies the arguments of the given PStatementMethod
     * @param m The PStatementMethod from which args are copied
     */
    public void cloneArgs(PStatementMethod m) {
        this.args = new IStatement[m.args.length];
        for (int i = 0; i < m.args.length; i++) {
            this.args[i] = m.args[i].clone();
        }
    }

    /**
     * Change the association of methods originally to oldObj to newObj
     * @param oldObj the old PObject
     * @param newObj the new PObject
     */
    @Override
    public void AssociateNullIdentityMethodTo(PObject newObj)
    {
        ////if(method.m_obj == null)
            m_obj = newObj;
            method.m_obj = newObj;
    }

    public boolean HasReturnValue() {return !GetReturnType().IsVoid();}
    public boolean HasSideEffect() {return true;}
}
