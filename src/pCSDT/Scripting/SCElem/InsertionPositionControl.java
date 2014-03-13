/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting.SCElem;

import pCSDT.Scripting.IStatement;
import pCSDT.Scripting.PStatementControl;

/**
 * This is a flow control insertion position.  It is used with statements that have more
 * than one body, and is distinct from the list insertion position.  It supports two major
 * types and two subtypes:  Conditional or body index, and first or last.
 *
 * Insertion positions relative to body elements are reported using InsertionPositionList.
 *
 * @author Jason Sanchez
 */
public class InsertionPositionControl extends InsertionPosition
{
	// The insertion relation
	public RelTo m_rel;

	// This is the statement to be inserted.
	public PStatementControl m_parent;

	// The body index:
	public int m_iBody;

	public enum RelTo
	{
		// Insert in the conditional position
		Conditional,

		// Insert as the first element of the specified body:
		BodyFirst,

		// Insert as the last element of the specified body:
		BodyLast,

		// Specified insertion position makes no sense
		Miss
	}

	public InsertionPositionControl(JPnlLineControl ls, IStatement s, double distance, PStatementControl parent, RelTo rel)
	{
		this(ls, s, distance, parent, rel, 0);
	}

	public InsertionPositionControl(JPnlLineControl ls, IStatement s, double distance, PStatementControl parent, RelTo rel, int iBody)
	{
		super(ls, s, distance);
		m_parent = parent;
		m_rel = rel;
		m_iBody = iBody;
	}

	@Override
	public boolean equals(Object rhs)
	{
		if(!(rhs instanceof InsertionPositionControl))
			return false;
		InsertionPositionControl s = (InsertionPositionControl)rhs;
		return
			m_rel == s.m_rel &&
			m_s == s.m_s &&
			m_parent == s.m_parent &&
			m_ls == s.m_ls;
	}
}
