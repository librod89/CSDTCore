package pCSDT.Presentation.Graphics2D;

import pCSDT.Scripting.*;
import java.awt.Canvas;

public class G2DMatrix4x4 extends Matrix4x4 {
    public G2DMatrix4x4() {}
    public G2DMatrix4x4(Matrix4x4 src) {super(src);}
    public G2DMatrix4x4(double dx, double dy, double theta) {super(dx, dy, theta);}
    public G2DMatrix4x4(Vector3 r, double theta) {super(r, theta);}

    /**
     * Applies the transformation described by this matrix to the indicated GL context.
     * @param gl The OpenGL context to transform
     */
    public void Apply(Canvas canvas)
    {
        double[] _m = new double[16];
        double[][] tm = m.GetM().getArray();
        _m[0]  = tm[0][0];
        _m[1]  = tm[0][1];
        _m[2]  = tm[0][2];
        _m[3] = 0;
        _m[4]  = tm[1][0];
        _m[5]  = tm[1][1];
        _m[6]  = tm[1][2];
        _m[7] = 0;
        _m[8]  = tm[2][0];
        _m[9]  = tm[2][1];
        _m[10] = tm[2][2];
        _m[11] = 0;
        _m[12] = dx;
        _m[13] = dy;
        _m[14] = dz;
        _m[15] = 1.0;
    }
}


