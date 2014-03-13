/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting;
import org.jdom.Element;
import pCSDT.Scripting.SCElem.*;

/**
 * This is a generic interface that consumers may implement if they wish to
 * create their own executable PStatement implementations without inheriting
 * from PStatement.
 * @author Jason
 */
public interface IStatement
{
    /**
     * All statements need to provide a uniform way of representing themselves
     * in XML format.  This method constructs an XML node from the statement
     * contents and returns the newly constructed node for addition to a tree
     * of statements.  The method must be recursive in that it calls
     * getElementXml for each of its IStatement descendants and adds them to
     * the tree.
     *
	 * @param tagName The name of the tag to be returned
     * @return A serializable XML representation of this element
     */
    public Element GetXml(String tagName);
    
	/**
	 * 
	 * @param elem The element from which this statement is to be initialized
	 * @return True to indicate that the element was properly deserialized
	 */
	public abstract boolean SetXml(PEngine context, Element elem);

    /**
     * Evaluates the statement.  Because we are implementing a procedural
     * language paradigm without pointer or reference types, we require that
     * the value returned be a constant.
     * @returns The value after executing the statement
     */
    public PVariant Execute(PScopeStack scope) throws Exception;

    /**
     * @return The return type of this method
     */
    public PType GetReturnType();

    /**
     * This is used to construct a panel element for use with GUI representation of
     * a particular statement
     * @return A JPnlLine-derived class that is used to represent this statement
     */
    public JPnlLine GetGui(LayoutInfo info);

    /**
     * 
     * @return Child nodes of this statement
     */
    public IStatement[] GetChildren();

    /**
     * Set IStatement derivative having null PObject reference to the one
     * specified here
     * @param newObj the new PObject
     */
    public void AssociateNullIdentityMethodTo(PObject newObj);
	
	/**
	 * This classifier method determines whether this statement has a side effect.
	 * If it does not, then it does not make sense to add the statement to a list or
	 * to make it the body of an event.
	 *
	 * @return True if the statement has a side effect
	 */
	public boolean HasSideEffect();

	/**
	 * This classifier method is used to determine if a statement could possibly have
	 * a return value.  Control statements never have a return value, everything else could
	 * potentially have one.
	 *
	 * @return True if the expression is primitive
	 */
	public boolean HasReturnValue();

    /**
	 * Clones the statement
	 */
	public IStatement clone();
}

