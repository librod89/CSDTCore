/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Presentation;
import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 *
 * @author sanchj3
 * This is the file filter used in the serialization interfaces
 */
public class CsdtFileFilter extends FileFilter {
	public CsdtFileFilter()
	{
	}

	@Override
	public boolean accept(File f)
	{
        // allow directory to be seen even with filtering
        if (f.isDirectory()) {
            return true;
        }
		return getExtension(f).compareTo("xml") == 0;
	}

	@Override
	public String getDescription()
	{
		return "CSDT Save Files";
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