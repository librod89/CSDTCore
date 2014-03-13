/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting.SCElem;

import pCSDT.Scripting.IStatement;

/**
 * This indicates the insertion position occurs within a list
 * @author Jason Sanchez
 */
public class InsertionPositionList extends InsertionPosition
{
	public int m_index;

	public InsertionPositionList(JPnlLineList ls, IStatement s, double distance, int index)
	{
		super(ls, s, distance);
		m_index = index;
		m_s = s;
	}
}
