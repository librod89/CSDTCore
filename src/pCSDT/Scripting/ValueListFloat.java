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
public class ValueListFloat extends AbstractValueList<Float> {

    ArrayList<Float> list = new ArrayList<Float>(0);
    
    /**
     * Take a comma-separated list of float string and generate
     * a float list
     * @param s
     */
    public ValueListFloat(String s) {
        name = "ValueListFloat";
        StringTokenizer tokens = new StringTokenizer(s, ",");
        while (tokens.hasMoreTokens()) {
            list.add(Float.valueOf(tokens.nextToken().trim()));
        }
    }

    public ArrayList<Float> GetValueList() {
        return list;
    }

    public PType GetType() {
        return PType.Float;
    }

}
