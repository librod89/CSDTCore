/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting;

/**
 * A control statement, where child methods are executed conditionally based on the evaluation
 * of a control statement
 *
 * @author Jason Sanchez
 */
public abstract class PStatementControl extends PStatement
{
	public PStatementControl(PType t)
	{
		super(t);
	}

	public abstract PStatementControl clone();

	/**
	 *
	 * @return The condition on which execution of the body will be contingent
	 */
	public abstract IStatement GetCondition();

	/**
	 * Sets the statement on which this control is contingent
	 *
	 * @param s The statement to be assigned as the new control
	 */
	public abstract void SetCondition(IStatement s);

	/**
	 *
	 * @return The set of conditional bodies to this control
	 */
	public abstract IStatementList[] GetBodies();

	/**
	 * Sets the body at the specified index
	 * @param i The index of the assigned body
	 * @param s The statement to be assigned
	 */
	public abstract void SetBody(int i, IStatement s);

	public boolean HasReturnValue() {return false;}
	public boolean HasSideEffect() {return true;}
}
