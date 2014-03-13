/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting;

/**
 * This default statement factory checks if the key string is embraced by [].
 * If so, the key is interpreted as a list, and return the respective
 * PStatementDynamicConst. Otherwise, the key is treated as a single value,
 * and return the respective PStatementConst.
 * @author tylau
 */
public class StatementFactoryDefault extends AbstractStatementFactory {

    public StatementFactoryDefault() {}

    public IStatement GenerateStatement(String key, ePType type) {
        // characteristics of the key: starts and ends with []
        String trimmedkey = key.trim();
        if (trimmedkey.startsWith("[") && trimmedkey.endsWith("]")) {
            String trimArgVals = trimmedkey.replaceAll("\\[", "").replaceAll("\\]", "");
            if (type == ePType.Boolean)
                return new PStatementDynamicConst<Boolean>(new ValueListBoolean(trimArgVals));
            else if (type == ePType.Integer)
                return new PStatementDynamicConst<Integer>(new ValueListInteger(trimArgVals));
            else if (type == ePType.Float)
                return new PStatementDynamicConst<Float>(new ValueListFloat(trimArgVals));
            else if (type == ePType.String){
                    return new PStatementDynamicConst<String>(new ValueListString(trimArgVals));
            }
                
            else
                return new PStatementConst("");
        }
        else {
            if (type == ePType.Boolean) {
                return new PStatementConst(Boolean.valueOf(trimmedkey));
            }
            else if (type == ePType.Integer) {
                return new PStatementConst(new Integer(trimmedkey));
            }
            else if (type == ePType.Float) {
                return new PStatementConst(new Float(trimmedkey));
            }
            else if (type == ePType.String) {
                return new PStatementConst(trimmedkey);
            }
            else {
                // default to empty string
                return new PStatementConst("");
            }
        }
    }
}
