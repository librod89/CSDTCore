/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Richard
 */
public class XMLFilter extends FileFilter {
        //Accepts ONLY xml files.
    @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true; // to make visible directories
            }

            String extension = getExtension(f);
            if (extension != null) {
                if (extension.equals("xml")) {
                    return true;
                } else {
                    return false;
                }
            }

            return false;
        }

        //The description of this filter
    @Override
        public String getDescription() {
            return "XML Files";
        }
    
    
        public String getExtension(File f) {
                String ext = null;
                String s = f.getName();
                int i = s.lastIndexOf('.');

                if (i > 0 && i < s.length() - 1) {
                    ext = s.substring(i + 1).toLowerCase();
                }
                return ext;
       }
}
