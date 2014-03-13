/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting;

/**
 * The base InvokeVerifier class.
 * To be attached to a PEventList.
 * It is to check the parameters passed to a PEvent (hence including the
 * associated PObject).
 * All the PEventInvokeVerifiers have to return true for a PEvent instance to be
 * executed
 * @author tylau
 */
public interface IEventInvokeVerifier {
    /**
     * Check if the PEvent that attaches this verifiers is valid to be invoked
     * @param e The PEvent to be invoke verified
     * @param params The parameters input that assists testing
     * @return true if pass the verifier
     */
    public boolean VerifyInvoke(PEvent e, Object... params);
}
