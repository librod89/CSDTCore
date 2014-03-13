/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting.SCElem;

import pCSDT.Scripting.IStatement;

/**
 * This class is for relative insertions on events.  The only time this is typically
 * returned is when the first statement is being inserted into an event class.  This
 * insertion position therefore only makes sense for events that have an empty listener.
 *
 * @author Jason Sanchez
 */
public class InsertionPositionEvent extends InsertionPosition
{
    JPnlLineEvent m_lEvt;
    int argIdx = -1;  // if > -1, it indicates the arg pos to be inserted
    // if argIdx == -1, check if it is the first position in execution statement
    // list to be inserted
    boolean m_bFirst = false;

    // For insertion to the execution statement list
    public InsertionPositionEvent(JPnlLineEvent ls, IStatement s, double distance, boolean bFirst)
    {
	super(ls, s, distance);
	m_s = s;
	m_bFirst = bFirst;
    }

    // for insertion to the arg list
    public InsertionPositionEvent(JPnlLineEvent ls, IStatement s, double distance, int argIdx) {
        super(ls, s, distance);
        m_s = s;
        this.argIdx = argIdx;
    }

    public int GetArgIdx() {
        return argIdx;
    }
}
