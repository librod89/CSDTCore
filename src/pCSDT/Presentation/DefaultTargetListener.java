/*
 * DefaultTargetListener.java
 *
 * Created on 25 de julio de 2007, 10:34 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package pCSDT.Presentation;

/**
 *
 * @author Richard
 *
 * This is an adapter class which provides default blank implementations
 * for DropTargetListener interface.
 */
import java.awt.dnd.*;

public class DefaultTargetListener implements DropTargetListener {

    /** Creates a new instance of DefaultTargetListener */
    public DefaultTargetListener() {
    }

    public void drop(DropTargetDropEvent dtde) {
    }

    public void dragEnter(DropTargetDragEvent dtde) {
    }

    public void dragOver(DropTargetDragEvent dtde) {
    }

    public void dropActionChanged(DropTargetDragEvent dtde) {
    }

    public void dragExit(DropTargetEvent dte) {
    }
}
