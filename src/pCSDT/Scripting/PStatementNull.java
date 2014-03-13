/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting;

import org.jdom.Element;
import pCSDT.Scripting.SCElem.*;

/**
 * This is a placeholder statement.  It is used by default as the else clause of an if
 * statement where that statement only has a true branch.  A null statement does nothing.
 *
 * @author Jason Sanchez
 */
public class PStatementNull extends PStatement
{
	public PStatementNull()
	{
		;
	}

	@Override
	public PStatementNull clone() {return new PStatementNull();}

	public Element GetXml(String tagName) {
		Element root = new Element(tagName);
		root.setAttribute("type", "null");
		return root;
	}

	public boolean SetXml(PEngine context, Element elem) {return true;}

	public PVariant Execute(PScopeStack scope) {return PVariant.Void;}

	public JPnlLine GetGui(LayoutInfo info)
	{
		return new JPnlLineNull(this, info);
	}

	public boolean HasReturnValue() {return false;}
	public boolean HasSideEffect() {return false;}
}
