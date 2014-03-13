/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting;

/**
 * This stream of statement factory takes a key, a type and an IStatement as
 * input. When there is a match of key and type, a clone of the IStatement is
 * returned.
 * @author tylau
 */
public class StatementFactoryCloning extends AbstractStatementFactory {

    String key;
    ePType type;
    IStatement stmt;  // the stmt to be cloned

    public StatementFactoryCloning(String key, ePType type, IStatement stmt) {
        this.key = key;
        this.type = type;
        this.stmt = stmt;
    }

    public IStatement GenerateStatement(String key, ePType type) {
        if (key.compareToIgnoreCase(this.key) == 0 && type == this.type) {
            return stmt.clone();
        }
        return null;
    }
}
