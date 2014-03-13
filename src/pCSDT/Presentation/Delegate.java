/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Presentation;

import java.lang.reflect.*;

/**
 *
 * @author Jason
 * This is a delegate class used to wrap bound event invocations.  This
 * class can also bind objects on the events.
 */
public class Delegate implements Runnable {
    Object m_obj;
    Method m_func;
    Object[] m_args;
    
    /**
     * Delegate binding with bound function name
     * @param obj The object on which the Delegate is bound
     * @param func The already looked up name of the function to be called
     * @param args The arguments to be passed when the delegate is called
     */
    public Delegate(Object obj, Method func, Object... args)
    {
        m_obj = obj;
        m_func = func;
        m_args = args;
    }
    
    /**
     * Delegate binding with function name lookup--if the function name is not
     * found or if the argument types do not match one of its overloads, an
     * exception is thrown
     * @param obj The object on which the Delegate is bound
     * @param funcName The name of the function to be called
     * @param args The arguments to be passed when the delegate is called
     * @throws java.lang.NoSuchMethodException
     */
    public Delegate(Object obj, String funcName, Object... args)
            throws NoSuchMethodException
    {
        Class c = obj.getClass();
        Class[] argTypes = new Class[args.length];
        for(int i = 0; i < args.length; i++)
            argTypes[i] = args[i].getClass();
        
        m_obj = obj;
        m_func = c.getMethod(funcName, argTypes);
        m_args = args;
    }
    
    @Override
    public void run()
    {
        try
        {
            m_func.invoke(m_obj, m_args);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
