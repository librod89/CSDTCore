/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ScriptletTester.java
 *
 * Created on Jul 10, 2009, 12:09:20 PM
 */

package pCSDT.Test;

import java.awt.Point;
import pCSDT.Presentation.*;
import pCSDT.Scripting.*;
import pCSDT.Scripting.SCElem.LayoutInfo;

/**
 *
 * @author Jason
 */
public class ScriptletTester extends javax.swing.JFrame {
    public JPnlScriptlet m_pnl;
	public LayoutInfo m_l = new LayoutInfo(ScriptletTester.class.getClassLoader());

    /** Creates new form ScriptletTester */
    public ScriptletTester()
    {
        initComponents();

        /*
        PObject pObject = new DummyPObj("Dummy", "DummyPObject");
        PEvent evt = new PEvent();
		evt.SetListener(
			new PStatementList(
				new PStatementAssign(
					new PVariableName("a"),
					new PStatementConst(3)
				),
				new PStatementWhile(
					new PStatementBinary(
						new PVariableName("a"),
						new PStatementConst(4),
						ePBinOpType.Less
					),
					new PStatementList(
						new PStatementMethod(
							pObject.GetMethod("GetX")
						),
						new PStatementMethod(
							pObject.GetMethod("SetX"),
							new PStatementConst(10)
						),
						new PStatementMethod(
							pObject.GetMethod("GetX")
						),
						new PStatementMethod(
							pObject.GetMethod("GetX")
						),
						new PStatementMethod(
							pObject.GetMethod("SetX"),
							new PStatementBinary(
								new PStatementConst(20),
								new PStatementConst(new PVariant(PType.Void)),
								ePBinOpType.Add
							)
						)
					)
				),
				new PStatementMethod(
					pObject.GetMethod("GetX")
				),
				new PStatementMethod(
					pObject.GetMethod("GetX")
				)
			)
		);

        m_scriptlet.BindEvent(evt, new Point(0, 0));
         */
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        m_scriptlet = new pCSDT.Presentation.JPnlScriptlet(m_l);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setName("TestFrame"); // NOI18N

        m_scriptlet.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        m_scriptlet.setLayout(new java.awt.FlowLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 783, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(m_scriptlet, javax.swing.GroupLayout.DEFAULT_SIZE, 783, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 535, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(m_scriptlet, javax.swing.GroupLayout.DEFAULT_SIZE, 535, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ScriptletTester().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private pCSDT.Presentation.JPnlScriptlet m_scriptlet;
    // End of variables declaration//GEN-END:variables

}