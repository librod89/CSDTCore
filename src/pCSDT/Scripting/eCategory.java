/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting;

/**
 *
 * @author Jason
 * This is a categorization enumeration for the sorting of methods/events/props
 * into various categories.  The category is set as part of the annotation, and
 * the categories provided here in this enumeration are only suggestions--you
 * can provide an extended category, if you prefer, by specifying the Custom
 * category.  In that case, you should set the CustomCategory string, which is
 * ignored unless the Custom category enumeration member is set.
 * 
 * From the GUI side, the category controls which tab the various elements are
 * sorted into when the GUI is set up (categories without any elements are
 * omitted, obviously, to reduce confusion).  If you have a category with a
 * custom name, then the tab will take on the name of the custom category and
 * any other elements with a case-sensitive equivalent category name will be
 * placed in the same tab.
 */
public enum eCategory {
    // This is the default category, and members in Default wind up in the
    // General tab.
    General,
    
    // This category is reserved for elements dealing with position, velocity,
    // acceleration, and so forth.
    Motion,
    
    // This category is for elements dealing with color, texture, shape, size,
    // visibility, and so forth.
    Appearance,
    
    // This category is (obviously) used for any elements that don't really fit
    // into any other category.
    Misc
}
