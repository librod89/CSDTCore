package pCSDT.Scripting;

/**
 *
 * @author Jason
 */
public interface IStatementList extends IStatement {
    /**
     * Appends a child to this statement list
     * @param c The child to be appended
     */
    public void AppendChild(IStatement c);
    
    /**
     * Inserts a child in the statement list at a particular index
     * @param c The child to be appended
     * @param index The location where the child is to be inserted
     */
    public void InsertChild(IStatement c, int index);
    
    /**
     * Removes a child at a particular index
     * @param index The index at which the child is to be removed
     */
    public void RemoveChild(int index);

    /**
     * Removes a child specified by the reference passed
     * @param c reference to the child to be removed from the list
     * @return true if something has been removed, false otherwise
     */
    public boolean RemoveChild(IStatement c);

    /**
     * Remove all children
     */
    public void ClearAll();
    
    /**
     * Moves a child at a particular index to a new index
     * @param index The current index of the child
     * @param newIndex The index to which the child is to be moved
     */
    public void MoveChild(int index, int newIndex);

    public IStatementList clone();
}
