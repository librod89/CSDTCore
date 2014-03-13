/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *
 * @author tylau
 */
public class ValueListBoolean extends AbstractValueList<Boolean> {

    ArrayList<Boolean> list = new ArrayList<Boolean>(0);
    
    /**
     * Take a comma-separated list of {true,false} string and generate
     * a boolean list
     * @param s
     */
    public ValueListBoolean(String s) {
        name = "ValueListBoolean";
        StringTokenizer tokens = new StringTokenizer(s, ",");
        while (tokens.hasMoreTokens()) {
            list.add(Boolean.valueOf(tokens.nextToken().trim()));
        }
    }

    public ArrayList<Boolean> GetValueList() {
        return list;
    }

    public PType GetType() {
        return PType.Boolean;
    }

}
