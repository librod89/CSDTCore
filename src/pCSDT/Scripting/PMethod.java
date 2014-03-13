/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting;
import java.lang.reflect.Method;

/**
 *
 * @author Jason
 * This represents a method that may be executed on an object.  Note that a
 * method is always bound to a corresponding object, and this binding may not
 * be changed after construction.
 * 
 * 
 */
public class PMethod extends PMethodUnbound {
    /**
     * Method constructor
     * @param name The string name of this method
     * @param retType The return type expected by this method
     * @param obj The IObject corresponding to this method
     * @param argTypes An ordered list describing the expected arguments
     * @throws IllegalArgumentException
     * 
     * Note that it is expected that Method will have the following form:
     *  PVariant MethodName(ArrayList<PStatement> scope)
     * 
     * This is exactly the way the method will be called.  Note that, for the
     * sake of implementational simplicity, the method is expected to return an
     * object of type PVariant, even though its return value is strictly listed
     * as being retType.
     */
    public PMethod(String name, PObject obj, Method method)
            throws IllegalArgumentException
    {
        super(name, method);
        m_obj = obj;
    }
    
    /**
     * Constructs a bound method from an unbound counterpart
     * @param ubm The unbound method name
     * @param obj The object to bind against
     */
    public PMethod(PMethodUnbound ubm, PObject obj)
    {
        super(ubm);
        m_obj = obj;
    }
    
    PObject m_obj;
    
    /**
     * 
     * @return The object bound on this PMethod
     */
    public PObject GetObject() {return m_obj;}
    
    /**
     * 
     * @return The result of the method invocation
     * @param stack A scope stack; methods ignore this parameter.
     */
    public PVariant Execute(IStatement[] args, PScopeStack stack)
        throws Exception
    {
        Class[] clazz = m_method.getParameterTypes();
        
        // Map each of the input members of this method:
        Object mapped[] = new Object[args.length];
        for(int i = 0; i < args.length; i++)
        {
            PVariant rs = args[i].Execute(stack);
            // original method-type-unware impl
            ////mapped[i] = rs.Wrap();
            // new method-type-aware impl
            String clazzName = clazz[i].getSimpleName();
            if (clazzName.compareToIgnoreCase("String") == 0) {
                mapped[i] = rs.Wrap(ePType.String);
            }
            else if (clazzName.compareToIgnoreCase("Float") == 0) {
                mapped[i] = rs.Wrap(ePType.Float);
            }
            else if (clazzName.compareToIgnoreCase("Integer") == 0) {
                mapped[i] = rs.Wrap(ePType.Integer);
            }
            else if (clazzName.compareToIgnoreCase("Boolean") == 0) {
                mapped[i] = rs.Wrap(ePType.Boolean);
            }
            else {
                mapped[i] = rs.Wrap();
            }
        }
        
        // Call the method itself and rewrap the return type in a Variant.
        // Note that the PVariant Object constructor will automatically do the
        // type conversion on the PVariant.
        return new PVariant(m_method.invoke(m_obj, mapped));
    }
}
