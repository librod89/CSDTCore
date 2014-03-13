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
public class InsertionPositionMethod extends InsertionPosition
{
    int argIdx = -1;

	public InsertionPositionMethod(JPnlLineMethod ls, IStatement s, double distance, int argIdx)
	{
		super(ls, s, distance);
        this.argIdx = argIdx;
	}

    public int GetArgIdx() {
        return argIdx;
    }
}
