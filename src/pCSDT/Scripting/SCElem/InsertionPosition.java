/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting.SCElem;

import pCSDT.Scripting.*;

/**
 * This class describes the insertion position of a statement in a statement tree.
 * @author Jason Sanchez
 */
public abstract class InsertionPosition
{
	// The line targeted for this insertion:
	JPnlLine m_ls;

	// The statement being inserted:
	IStatement m_s;

	// The recorded distance from line to insertion point:
	public double m_distance;

	public InsertionPosition(JPnlLine ls, IStatement s, double distance)
	{
		m_ls = ls;
		m_s = s;
		m_distance = distance;
	}

	/**
	 * This performs the insertion operation described by this structure
	 * @return True to indicate the insertion operation completed as requested
	 */
	public boolean Insert() {return m_ls.Insert(this);}

	/**
	 * Called to highlight the insertion position
	 */
	public void Highlight() {m_ls.Highlight(this);}

	/**
	 * Called to clear a highlight
	 */
	public void ClearHighlight() {m_ls.ClearHighlight();}

	/**
	 * Called to get the statement to be inserted
	 * @return The exact statement to be inserted
	 */
	public IStatement GetStatement() {return m_s;}

	/**
	 *
	 * @return The parent GUI to the insertion position
	 */
	public JPnlLine GetGui() {return m_ls;}

	/**
	 * Called to get the absolute distance from the insertion point to the control
	 * @return The distance, in pixels
	 */
	public double GetDistance() {return m_distance;}

	/**
	 * Returns the best scored of n passed insertion positions.
	 */
	public static InsertionPosition GetBestPosition(InsertionPosition... positions)
	{
		double best = 1e9;
		InsertionPosition retVal = null;
		for(InsertionPosition pos : positions)
			if(pos != null && pos.GetDistance() < best)
			{
				retVal = pos;
				best = pos.GetDistance();
			}
		return retVal;
	}
}
