/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Presentation;

import java.awt.*;
import pCSDT.Scripting.*;
import pCSDT.Scripting.SCElem.*;

/**
 *
 * @author Jason Sanchez
 */
public class JPnlOperatorMgr extends JPnlTemplateMgr
{
	PObject m_pObj;

	public JPnlOperatorMgr(LayoutInfo l, JPnlScriptlet jpnlScriptlet)
	{
            super(l, jpnlScriptlet);
            setBackground(new Color(125,125,125));
            InitControls();
	}

	private void InitControls()
	{
            AddExpression(new JPnlLineBinary(new PStatementBinary(
                                             new PStatementConst(1),
					     new PStatementConst(1),
					     ePBinOpType.Equal), m_l), 0);
            AddExpression(new JPnlLineBinary(new PStatementBinary(
					     new PStatementConst(1),
					     new PStatementConst(1),
					     ePBinOpType.NotEqual), m_l), 1);
            AddExpression(new JPnlLineBinary(new PStatementBinary(
					     new PStatementConst(1),
					     new PStatementConst(1),
					     ePBinOpType.Less), m_l), 2);
            AddExpression(new JPnlLineBinary(new PStatementBinary(
					     new PStatementConst(1),
					     new PStatementConst(1),
					     ePBinOpType.LessEqual), m_l), 3);
            AddExpression(new JPnlLineBinary(new PStatementBinary(
					     new PStatementConst(1),
					     new PStatementConst(1),
					     ePBinOpType.Greater), m_l), 4);
            AddExpression(new JPnlLineBinary(new PStatementBinary(
					     new PStatementConst(1),
					     new PStatementConst(1),
					     ePBinOpType.GreaterEqual), m_l), 5);
            AddExpression(new JPnlLineBinary(new PStatementBinary(
					     new PStatementConst(1),
					     new PStatementConst(1),
					     ePBinOpType.Add), m_l), 6);
            AddExpression(new JPnlLineBinary(new PStatementBinary(
					     new PStatementConst(1),
					     new PStatementConst(1),
					     ePBinOpType.Subtract), m_l), 7);
            AddExpression(new JPnlLineBinary(new PStatementBinary(
					     new PStatementConst(1),
					     new PStatementConst(1),
					     ePBinOpType.Multiply), m_l), 8);
            AddExpression(new JPnlLineBinary(new PStatementBinary(
                                             new PStatementConst(1),
                                             new PStatementConst(1),
                                             ePBinOpType.Divide), m_l),9);
            AddExpression(new JPnlLineBinary(new PStatementBinary(
                                             new PStatementConst(1),
                                             new PStatementConst(1),
                                             ePBinOpType.AND), m_l),10);
            AddExpression(new JPnlLineBinary(new PStatementBinary(
                                             new PStatementConst(1),
                                             new PStatementConst(1),
                                             ePBinOpType.OR), m_l),11);
            // Update the panel
            updateUI();
	}

	@Override
	public void BindObject(PObject obj)
	{
		m_pObj = obj;
		removeAll();
		InitControls();
	}
}
