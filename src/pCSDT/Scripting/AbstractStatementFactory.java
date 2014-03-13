/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting;

/**
 * This interface defines the framework of a statement factory
 * which is supposed to generate an IStatement which is based on given key
 * string and type
 * @author tylau
 */
public abstract class AbstractStatementFactory {

    /**
     * Given the key and type, determine if a particular kind of IStatement
     * should be generated
     * @param key
     * @param type
     * @return the respective IStatement if appropriate, null otherwise
     */
    public abstract IStatement GenerateStatement(String key, ePType type);

}
