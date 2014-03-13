/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting;
import java.io.Serializable;
import java.util.HashMap;
import java.util.ArrayList;
import javax.naming.NameAlreadyBoundException;

/**
 *
 * @author Jason
 * This represents a single scope of execution for use with the scripting
 * engine.  The scope allows the mapping of variable names (strings) to values,
 * and allows these values to be modified as one would expect variables to be
 * modified throughout the execution of the script.  PScope is used with the
 * PScopeStack class, which allows the creation of scoped variables.
 */
public class PScope {
    /**
     * This is a case-sensitive hash map that maps string names to the variables
     * represented by those strings.
     */
    HashMap<String, PVariable> m_vars = new HashMap<String, PVariable>();

    public PScope()
    {
    }
    
    public PScope(ArrayList<PVariable> params)
    {
        // Copy the params so we don't modify the source params by mistake:
        for(int i = 0; i < params.size(); i++)
        {
            PVariable cur = params.get(i);
            m_vars.put(cur.GetName(), cur);
        }
    }
    
    /**
     * Resolves the name to a variable in this scope
     * @param name The name to be resolved
     * @return The PVariable object corresponding to the name, or null
     */
    public PVariable Resolve(String name)
    {
        return m_vars.get(name);
    }

    /**
     * Declares a new variable in this scope, assuming that this variable does not
	 * already exist
     * @param var The variable to be declared
     */
    public void DeclareUnsafe(PVariable var)
    {
        m_vars.put(var.GetName(), var);
    }

    /**
     * Declares a new variable in this scope
     * @param var The variable to be declared
     * @throws javax.naming.NameAlreadyBoundException
     */
    public void Declare(PVariable var) throws NameAlreadyBoundException
    {
        if(Resolve(var.GetName()) != null)
            throw new NameAlreadyBoundException();
        m_vars.put(var.GetName(), var);
    }
}
