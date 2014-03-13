/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;
import pCSDT.Presentation.*;

/**
 *
 * @author tylau
 */
public class ValueListString extends AbstractValueList<String> {

    ArrayList<String> list = new ArrayList<String>(0);
    
    PObject obj;
    /**
     * Take a comma-separated list of string and generate
     * a string list
     * Used for object-image list in JPnlObjMgr.java
     * @param s
     */
    public ValueListString(String s) {
        name = "ValueListString";
        obj = JPnlObjMgr.currentObj; 
        if(s.equals("PlaitCostumeChange") && !"Stage".equals(obj.m_name)){
            ArrayList<String> temp = JPnlObjMgr.ObjectImageMap.get(obj);
            s = "";
            int i = 1;
            if(temp != null) for(String p : temp){
                //Show only filename instead of whole path
                //File f = new File(p);
                s += "," + "Costume " + i;
                i++;
            }
        }
        StringTokenizer tokens = new StringTokenizer(s, ",");
        while (tokens.hasMoreTokens()) {
            list.add(tokens.nextToken().trim());
        }
    }

    public ArrayList<String> GetValueList() {
        return list;
    }

    public PType GetType() {
        return PType.String;
    }

}
