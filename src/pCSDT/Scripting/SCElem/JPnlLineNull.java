/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting.SCElem;

import java.awt.Component;
import pCSDT.Scripting.*;

/**
 * An empty line, having no children
 * @author Jason Sanchez
 */
public class JPnlLineNull extends JPnlLine
{
	public JPnlLineNull(PStatementNull statement, LayoutInfo l)
	{
		super(statement, l);
	}

	@Override
	public void InitControls()
	{
	}

	@Override
	public eSaveResult Save()
	{
		return eSaveResult.Success;
	}

	@Override
	public void setEditable(boolean b)
	{
	}

	@Override
	public PStatementNull GetStatement() {return (PStatementNull)m_statement;}

	@Override
	public JPnlLine clone()
	{
		return new JPnlLineNull(GetStatement(), m_l);
	}

}
