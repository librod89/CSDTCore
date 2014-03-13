/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting.SCElem;

import pCSDT.Scripting.IStatement;

/**
 *
 * @author tylau
 */
public class InsertionPositionAssign extends InsertionPosition {
    boolean m_bLhs;

    public InsertionPositionAssign(JPnlLineAssign ls, IStatement s, double distance, boolean bLhs)
    {
	super(ls, s, distance);
	m_bLhs = bLhs;
    }
}
