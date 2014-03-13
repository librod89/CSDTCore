/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import static pCSDT.Scripting.PEngine.codebaseURL;

/**
 *
 * @author Jason
 * This represents a method not yet bound on an object.  Useful when attempting
 * to get a list of the methods supported by an object.
 */
public class PMethodUnbound {
    
    //This count is for methods not specified in identify.txt
    static int leftoverCount = 0;
    /**
     * Constructs a new unbound method
     * @param name The friendly name of the method
     * @param method The method itself
     * @throws java.lang.IllegalArgumentException
     */
    public PMethodUnbound(String name, Method method)
            throws IllegalArgumentException
    {
        m_retType = PType.FromClass(method.getReturnType());
        
        Class[] argSet = method.getParameterTypes();
        m_anno = (AutomatableMethod)method.getAnnotation(AutomatableMethod.class);
        if(m_anno == null)
            throw new IllegalArgumentException("The input method must be annotated with PropertyMethod");
        
        // Ensure all friendly names exist:
        if(m_anno.argNames().length != argSet.length)
            throw new IllegalArgumentException("Not all arguments on input method " + name + " were given friendly names");
        m_displayPos = m_anno.displayPos();
        File f = null;
        String scan = "";
        boolean found = false;
        if (m_anno.displayPosString().equals("[]")){
            //Find codelets in Identify.txt that have highest priority
            try {
                Scanner sc = null;
                if(codebaseURL != null){
                    InputStream u = new URL(codebaseURL + "demos/identify.txt").openStream();
                    sc = new Scanner(u);
                }
                else{
                    f = new File("C:/Users/Public/demos/identify.txt");
                    sc = new Scanner(f, "UTF-8");
                }
                while(sc.hasNext()) scan += sc.next() + " ";
                scan = scan.substring(7);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(PMethodUnbound.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MalformedURLException ex) {
                Logger.getLogger(PMethodUnbound.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(PMethodUnbound.class.getName()).log(Level.SEVERE, null, ex);
            }
            scan = scan.trim();
            int i = scan.indexOf('/');
            int count = 0;
            while(!scan.isEmpty()){
                if(scan.substring(0, i).trim().equals(name)){
                    m_displayPos = count; 
                    found = true;
                    scan = ""; //end while loop
                }
                else{
                    scan = scan.substring(i+1);
                    i = scan.indexOf('/');
                    count++;
                }
            }
            
            if(!found) {
                m_displayPos = count + leftoverCount;
                leftoverCount++;
            }
        }
        m_name = name;
        m_argTypes = new PType[argSet.length];
        m_method = method;
        
        // Fill in our argument types:
        for(int i = argSet.length; i-- > 0;)
            m_argTypes[i] = PType.FromClass(argSet[i]);
    }
    
    /**
     * Copy constructor
     * @param cpy The copy from which to take values
     */
    public PMethodUnbound(PMethodUnbound cpy)
    {
        m_displayPos = cpy.m_displayPos;
        m_name = cpy.m_name;
        m_retType = cpy.m_retType;
        m_argTypes = cpy.m_argTypes;
        m_anno = cpy.m_anno;
        m_method = cpy.m_method;
    }
    protected int m_displayPos;
    protected String m_name;
    protected PType m_retType;
    protected PType m_argTypes[];
    protected AutomatableMethod m_anno;
    protected transient Method m_method;
    
    public int GetDisplayPos() { return m_displayPos;}
    
    /**
     * 
     * @return The name of this method
     */
    public String GetName() {return m_name;}
    
    /**
     * 
     * @return An ordered list of the types of the arguments this method expects
     */
    public PType[] GetArgTypes() {return m_argTypes;}
    
    /**
     * 
     * @return An ordered list of the classes of the arguments this method expects
     */
    public Class[] GetArgClasses() {return m_method.getParameterTypes();}
    
    /**
     * 
     * @return An ordered list of the names of the arguments
     */
    public String[] GetArgNames() {return m_anno.argNames();}
    
    /**
     * 
     * @return An ordered list of the description of the arguments
     */
    public String[] GetArgDescs() {return m_anno.argDesc();}

    /**
     *
     * @return An ordered list of the default argument values
     */
    public String[] GetArgVals() {return m_anno.argVals();}

    /**
     * 
     * @return The number of arguments
     */
    public int GetArgCount() {return m_argTypes.length;}

    /**
     *
     * @return The arity of this function
     */
    public int GetArity() {return m_argTypes.length;}

    @Override
    public String toString() {return m_name;}
}
