/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting;
import java.io.Serializable;
import java.util.ArrayList;
import javax.naming.NameAlreadyBoundException;

/**
 *
 * @author Jason
 * This class represents a scope stack of variables.  When a variable is sought,
 * the scope stack starts first at the top of the stack and percolates its way
 * downwards until it reaches the root local stack.  When a new scope stack is
 * created, it is initially charged with the names and values of the event
 * parameters and values.  The framework automatically pushes a new scope stack
 * prior to the execution of any PStatementList statement, and pops the scope
 * stack when the execution of a PStatementList is complete.
 */
public class PScopeStack
{
    public PScopeStack()
    {
        m_stk = new ArrayList<PScope>();
    }
    
    ArrayList<PScope> m_stk;
    
    /**
     * Attempts to resolve a variable within scope
     * @param name
     * @return The variant corresponding to that variable, or null if not found.
     */
    public IAssignable Resolve(String name)
    {
        for(int i = 0; i < m_stk.size(); i++)
        {
            PVariable var = m_stk.get(i).Resolve(name);
            if(var != null)
                return var;
        }

	// Unable to find any variable by that name all the way up.  We must
	// create a new variable and add it in _this_ scope.
        PVariable pRetVal = new PVariable(name, PVariant.Void);
	DeclareUnsafe(pRetVal);
        return pRetVal;
    }
    
    /**
     * Charges the scope stack with an initial set of parameters
     * @param params The parameters to charge into the scope
     */
    public void Charge(ArrayList<PVariable> params)
    {
        Reset();
        m_stk.add(new PScope(params));
    }
    
    /**
     * Adds a new scope to the scope stack
     * @return The newly added scope stack
     */
    public PScope Push()
    {
        PScope retVal = new PScope();
        m_stk.add(retVal);
        return retVal;
    }

    /**
     * Declares a new variable in the current scope
     * @param var The variable to be declared
     * @throws javax.naming.NameAlreadyBoundException
     */
    public void Declare(PVariable var) throws NameAlreadyBoundException
    {
        m_stk.get(m_stk.size() - 1).Declare(var);
    }

    /**
     * Declares a new variable in the current scope, assuming that the variable does not
	 * already exist therein
     * @param var The variable to be declared
     */
    public void DeclareUnsafe(PVariable var)
    {
        m_stk.get(m_stk.size() - 1).DeclareUnsafe(var);
    }
    
    /**
     * Removes a scope unit from the scope stack
     */
    public void Pop()
    {
        m_stk.remove(m_stk.size() - 1);
    }
    
    /**
     * Resets the scope stack to a depth of zero
     */
    public void Reset()
    {
        m_stk.clear();
    }
}
