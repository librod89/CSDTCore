/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting;

import java.util.ArrayList;

/**
 * An abstract class that expects its subclass to return an ArrayList of values
 * of a particular type. We can also ask for its PType.
 * @author tylau
 */
public abstract class AbstractValueList<T> {
    String name;  // name of this value list

    public String GetName() {return name;}
    public void SetName(String name) {this.name = name;}
    
    /**
     * Give the list of values stored in this value list
     * @return an ArrayList of value of type <T>
     */
    public abstract ArrayList<T> GetValueList();
    
    /**
     * Give the PType of value stored in this value list
     * @return PType
     */
    public abstract PType GetType();
}
