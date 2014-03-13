/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Presentation;
import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 *
 * @author tylau
 * This is the file filter for .PNG image format
 */
public class PngFileFilter extends FileFilter {
    public PngFileFilter()
    {
    }

    @Override
    public boolean accept(File f)
    {
        // allow directory to be seen even with filtering
        if (f.isDirectory()) {
            return true;
        }
	return getExtension(f).compareTo("png") == 0;
    }

    @Override
    public String getDescription()
    {
	return "*.png";
    }

    static public String getExtension(File f)
    {
	String name = f.getName();
	int i = name.lastIndexOf(".");
	if(i < 0)
            return "";
	return name.substring(i + 1).toLowerCase();
    }
}