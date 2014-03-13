/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting;

/**
 * @brief This is an assignable variable that also exports some properties to set names and
 * get GUI information.  This allows it to be embedded in a statement tree.
 *
 * The reason that unnamed variables are forbidden from being present in a statement tree
 * is that there is no possible representation for such a variable (imagine how one would
 * refer to a variable in a codelet if that variable had no name).
 *
 * As named variables may appear in a statement tree, they must implement the IStatement
 * interface.
 * @author sanchj3
 */
public interface INamedAssignable extends IAssignable, IStatement {
	public String GetName();
	public void SetName(String name);
	public INamedAssignable clone();
}
