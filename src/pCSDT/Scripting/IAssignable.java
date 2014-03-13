/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting;

import org.jdom.Element;

/**
 *
 * @author Jason
 */
public interface IAssignable {
    public PVariant Assign(PScopeStack stk, PVariant rhs);
    public PVariant GetValue(PScopeStack stk);
    public PType GetType(PScopeStack stk);
}
