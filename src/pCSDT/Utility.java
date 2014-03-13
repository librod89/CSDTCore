/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT;

import java.awt.Component;
import java.awt.Point;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Random;

/**
 *
 * @author Jason
 * Generic utility class with a variety of utility routines
 */
public class Utility {
    static Random rnd = new Random();

    public static<T> T[] resizeArray(T[] oldArray, int newSize)
    {
        int oldSize = oldArray.length;
        T[] newArray = (T[])new Object[newSize];
        int preserveLength = Math.min(oldSize, newSize);
        if(0 < preserveLength)
            System.arraycopy(oldArray, 0, newArray, 0, preserveLength);
        return newArray;
    }

    public static String GetPathPart(URL url)
    {
        String ret = url.toString();
        for(int i = ret.length() - 1; i != 0; i--)
            switch(ret.charAt(i))
            {
            case '/':
            case '\\':
                return ret.substring(0, i + 1);
            }
        return ret;
    }

    public static URL MakeRelativeUrl(URL relTo, String subUrl) throws MalformedURLException
    {
        return new URL(GetPathPart(relTo) + subUrl);
    }

    /**
     * Makes a name guaranteed to be unique to the current run
     * @return A unique name
     */
    public static String MakeName()
    {
	final String srcChars = "abcdefghijklmnopqrstuvwxyz";

	StringBuilder retVal = new StringBuilder();
	for(int i = 0; i < 15; i++)
            retVal.append(srcChars.charAt(rnd.nextInt(srcChars.length())));
	return retVal.toString();
    }

    /**
     * Attempts to find a parent with the given component type
     * @param parentType The parent type to be matched
     * @param comp The component from which to start
     * @return The first ancestor of comp with the type given by parentType
     */
    public static <T extends Component> T GetTypedAncestor(Class<T> parentType, Component comp)
    {
	while(comp != null)
	{
            if(parentType.isInstance(comp) && comp.isEnabled())
                return (T)comp;
            comp = comp.getParent();
	}
	return null;
    }

    /**
     * Makes a point defined in the same coordinate system as comp relative to comp's coordinate system
     * @param pt The point to be made relative
     * @param comp The child control whose coordinate system is to be used
     * @return A relative point
     */
    public static Point MakeRelative(Point pt, Component comp)
    {
	return new Point(pt.x - comp.getX(), pt.y - comp.getY());
    }

    /**
     * Transforms a point defined in the coordinate space of src to the coordinate space of dest
     *
     * @param src The coordinate space in which pt is defined
     * @param pt The point to be transformed
     * @param dst The destination coordinate space to use
     * @return The transformed coordinate
     */
    public static Point Transform(Component src, Point pt, Component dst)
    {
	Point srcScreen = src.getLocationOnScreen();
	Point dstScreen = dst.getLocationOnScreen();
	return new Point(
                    srcScreen.x - dstScreen.x + pt.x,
                    srcScreen.y - dstScreen.y + pt.y
	);
    }

    /**
     * Round a floating point number f to decimal point dp
     * @param f
     * @param dp
     * @return
     */
    public static double Round(float f, int dp) {
        return Math.round(f*Math.pow(10, dp))/Math.pow(10, dp);
    }

    /**
     * Format a given URL string so it is good for use by java routine
     * @param s
     * @return
     */
    public static URL FormatURL(String s) {
        String[] comps = s.split(":", 2);
        try {
            URI uri = new URI("http", comps[1], null);
            return uri.toURL();
        }
        catch (Exception e) {
            return null;
        }
    }
}
