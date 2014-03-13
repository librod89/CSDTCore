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
public class ValueListInteger extends AbstractValueList<Integer> {

    ArrayList<Integer> list = new ArrayList<Integer>(0);
    
    /**
     * Take a comma-separated list of integer string and generate
     * a integer list
     * @param s
     */
    public ValueListInteger(String s) {
        name = "ValueListInteger";
        StringTokenizer tokens = new StringTokenizer(s, ",");
        while (tokens.hasMoreTokens()) {
            list.add(Integer.valueOf(tokens.nextToken().trim()));
        }
    }

    public ArrayList<Integer> GetValueList() {
        return list;
    }

    public PType GetType() {
        return PType.Integer;
    }

}
