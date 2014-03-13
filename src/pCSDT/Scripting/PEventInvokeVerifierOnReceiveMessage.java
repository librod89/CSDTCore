/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Verify if a OnReceiveMessageEvent should be invoked
 * param[0]: message
 * param[1]: (optional): target recipient
 * @author tylau
 */
public class PEventInvokeVerifierOnReceiveMessage implements IEventInvokeVerifier {

    public boolean VerifyInvoke(PEvent e, Object... params) {
        // automatic false if insufficient parameter
        if (! ((params.length == 1 && (params[0] instanceof String) ||
               (params.length == 2 && (params[0] instanceof String && params[1] instanceof PObject))))) {
            return false;
        }
        try {
            PScopeStack scope = e.GetScopeStack();
            String msg = (String)params[0];
            PVariant v = e.GetArgs()[0].Execute(scope);
            // turn v to the value embeded in a variable if v.type is variable
            if (v.GetType() == PType.Variable) {
                v = v.lValue.GetValue(scope);
            }
            // filter those not send to this object
            if (params.length == 2) {
                ////String from = (String)params[1];
                PVariant v2 = e.GetArgs()[1].Execute(scope);
                if (v2.GetType() == PType.Variable) {
                    v2 = v2.lValue.GetValue(scope);
                }
                if (v2.GetType()==PType.Obj) {
                    if (params[1] != v2.oValue) return false;
                }
            }
            // attempt to verify with the returned string or integer value
            if (v.GetType()==PType.String) {
                if (msg.compareTo(v.sValue) == 0) return true;
            }
            else if (v.GetType()==PType.Integer) {
                if (msg.compareTo(Integer.toString(v.iValue)) == 0) return true;
            }
            else if (v.GetType()==PType.Float) {
                if (msg.compareTo(Float.toString(v.fValue)) == 0) return true;
            }
            else {
                return false;
            }

        } catch (Exception ex) {
            Logger.getLogger(PEventInvokeVerifierOnReceiveMessage.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

}
