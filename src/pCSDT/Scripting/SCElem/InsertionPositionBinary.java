/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting.SCElem;

import pCSDT.Scripting.*;

/**
 * Represents an insertion position for a binary expression
 * @author Jason Sanchez
 */
public class InsertionPositionBinary extends InsertionPosition
{
	boolean m_bLhs;

	public InsertionPositionBinary(JPnlLineBinary ls, IStatement s, double distance, boolean bLhs)
	{
		super(ls, s, distance);

		m_bLhs = bLhs;
	}
}
