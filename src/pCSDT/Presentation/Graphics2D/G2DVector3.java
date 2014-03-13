package pCSDT.Presentation.Graphics2D;

import pCSDT.Scripting.*;

/**
 * Not sure how this applies to Java2D CSDTs.
 *
 * @author Jason
 */
public class G2DVector3 extends Vector3 {
    public G2DVector3(double x, double y)
    {
        super(x, y);
    }

    public G2DVector3(double x, double y, double z)
    {
        super(x, y, z);
    }

    public G2DVector3(Vector3 s)
    {
        super(s);
    }
}
