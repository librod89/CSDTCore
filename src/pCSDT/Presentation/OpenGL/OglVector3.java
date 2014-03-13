/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Presentation.OpenGL;

import pCSDT.Scripting.*;

/**
 *
 * @author Jason
 */
public class OglVector3 extends Vector3 {
    public OglVector3(double x, double y)
    {
        super(x, y);
    }

    public OglVector3(double x, double y, double z)
    {
        super(x, y, z);
    }

    public OglVector3(Vector3 s)
    {
        super(s);
    }
}
