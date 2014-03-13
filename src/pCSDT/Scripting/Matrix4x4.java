/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting;

/**
 *
 * @author Jason
 */
public class Matrix4x4 {
    public static final Matrix4x4 I = new Matrix4x4();

    // 3x3 submatrix:
    protected Matrix3x3 m = new Matrix3x3();

    // Translational elements, pre-applied to the input coordinates
    protected double dx = 0.0;
    protected double dy = 0.0;
    protected double dz = 0.0;

    /**
     * Default constructor.  Makes an identity matrix
     */
    public Matrix4x4() {}

    /**
     * Constructs a matrix that rotates theta degrees around r
     * @param r The axis of rotation
     * @param theta The amount by which to rotate, in radians
     * @return
     */
    public Matrix4x4(Vector3 r, double theta)
    {
        m = new Matrix3x3(r, theta);
    }

    public Matrix4x4(double dx, double dy, double theta)
    {
        m = new Matrix3x3(theta);
        this.dx = dx;
        this.dy = dy;
    }

    /**
     * Copy constructor
     * @param src The matrix to clone
     */
    public Matrix4x4(Matrix3x3 src)
    {
        m = src.Dup();
    }

    /**
     * Copy constructor
     * @param src The matrix to clone
     */
    public Matrix4x4(Matrix4x4 src)
    {
        m = src.m.Dup();
        dx = src.dx;
        dy = src.dy;
        dz = src.dz;
    }

    /**
     * Constructor to create a 3-dimensional combined translation/rotation with Euler angles
     * @param adx x-shift
     * @param ady y-shift
     * @param adz z-shift
     * @param alpha Rotation about z+
     * @param beta Rotation about x+
     * @param gamma Rotation about z+
     */
    public Matrix4x4(double adx, double ady, double adz, double alpha, double beta, double gamma)
    {
        dx = adx;
        dy = ady;
        dz = adz;
    }

    public Matrix4x4 Identity() {return new Matrix4x4();}

    /**
     * Computes this * M and stores it in this.
     * @param M
     * @return
     */
    public Matrix4x4 Mul(Matrix4x4 M)
    {
        m.Mul(M.m);

        // Transform the current offsets according to the new matrix.
        Vector3 d = M.Mul(dx, dy, dz);
        dx = d.x() + M.dx;
        dy = d.y() + M.dy;
        dz = d.z() + M.dz;
        return this;
    }

    /**
     * Performs an entrywise negation of every element in the matrix
     * @return this
     */
    public Matrix4x4 Neg()
    {
        m.Neg();
        dx = -dx;
        dy = -dy;
        dz = -dz;
        return this;
    }

    public Matrix4x4 Add(Matrix4x4 A)
    {
        m.Add(A.m);
        dx += A.dx;
        dy += A.dy;
        dz += A.dz;
        return this;
    }

    public Matrix4x4 Sub(Matrix4x4 A)
    {
        m.Sub(A.m);
        return this;
    }

    /**
     * Rotates the matrix about the specified vector, by the specified amount.
     * @param v The rotation vector, assumed to be normalized
     * @param theta The amount by which to rotate
     * @return this
     */
    public Matrix4x4 Rotate(Vector3 v, double theta)
    {
        return Mul(new Matrix4x4(v, theta));
    }

    /**
     * Rotates this matrix around the positive Z-axis
     * @param theta The angle by which to rotate
     * @return this
     */
    public Matrix4x4 RotateX(double theta) {return Rotate(Vector3.X, theta);}

    /**
     * Rotates this matrix around the positive Z-axis
     * @param theta The angle by which to rotate
     * @return this
     */
    public Matrix4x4 RotateY(double theta) {return Rotate(Vector3.Y, theta);}

    /**
     * Rotates this matrix around the positive Z-axis
     * @param theta The angle by which to rotate
     * @return this
     */
    public Matrix4x4 RotateZ(double theta) {return Rotate(Vector3.Z, theta);}

    /**
     * Performs a 2-dimensional rotation, equivalent to RotateZ.
     * @param theta The angle by which to rotate
     * @return this
     */
    public Matrix4x4 Rotate2D(double theta) {return Rotate(Vector3.Z, theta);}

    /**
     * Translates this matrix in the xy dimensions
     * @param dx The x-translate amount
     * @param dy The y-translate amount
     * @return this
     */
    public Matrix4x4 Translate(double dx, double dy)
    {
        this.dx += dx;
        this.dy += dy;
        return this;
    }

    // Position offset accessor methods:
    public double GetDx() {return dx;}
    public double GetDy() {return dy;}
    public double GetDz() {return dz;}

    /**
     * Translates this matrix in the xyz dimensions
     * @param dx The x-translate amount
     * @param dy The y-translate amount
     * @param dz The z-translate amount
     * @return this
     */
    public Matrix4x4 Translate(double dx, double dy, double dz)
    {
        this.dx += dx;
        this.dy += dy;
        this.dz += dz;
        return this;
    }

    /**
     * Translates this matrix according to a translation vector
     * @param d The translation vector
     * @return this
     */
    public Matrix4x4 Translate(Vector3 d)
    {
        dx += d.x();
        dy += d.y();
        dz += d.z();
        return this;
    }

    /**
     * Scales the matrix nonuniformly in all three dimensions
     * @param sx The scaling coefficient; values greater than 1 magnify, values less than 1 minify
     * @return this
     */
    public Matrix4x4 Scale(double sx, double sy, double sz)
    {
        m.Scale(sx, sy, sz);
        dx *= sx;
        dy *= sy;
        dz *= sz;
        return this;
    }

    /**
     * Scales the matrix in the x-dimension
     * @param sx The scaling coefficient; values greater than 1 magnify, values less than 1 minify
     * @return this
     */
    public Matrix4x4 ScaleX(double sx)
    {
        m.ScaleX(sx);
        dx *= sx;
        return this;
    }

    /**
     * Scales the matrix in the y-dimension
     * @param sy The scaling coefficient; values greater than 1 magnify, values less than 1 minify
     * @return this
     */
    public Matrix4x4 ScaleY(double sy)
    {
        m.ScaleY(sy);
        dy *= sy;
        return this;
    }

    /**
     * Scales the matrix in the z-dimension
     * @param sz The scaling coefficient; values greater than 1 magnify, values less than 1 minify
     * @return this
     */
    public Matrix4x4 ScaleZ(double sz)
    {
        m.ScaleZ(sz);
        dz *= sz;
        return this;
    }

    /**
     * Scales the matrix uniformly in all spacial dimensions
     * @param sx The scaling coefficient; values greater than 1 magnify, values less than 1 minify
     * @return this
     */
    public Matrix4x4 ScaleUniform(double s)
    {
        m.ScaleUniform(s);
        dx *= s; dy *= s; dz *= s;
        return this;
    }

    /**
     * Inverts the matrix
     * @return The inverted matrix
     */
    public Matrix4x4 Invert()
    {
        m.Invert();

        // Transform, invert, and compensate the offset parameters
        Vector3 d = m.Mul(dx, dy, dz);
        d = m.Mul(d);
        dx = -d.x();
        dy = -d.y();
        dz = -d.z();
        return this;
    }

    public Matrix4x4 Dup()
    {
        return new Matrix4x4(this);
    }

    public Vector3 Mul(Vector3 v)
    {
        v = m.Mul(v).Translate(dx, dy, dz);
        return v;
    }

    public Vector3 Mul(double x, double y)
    {
        Vector3 v = m.Mul(x, y).Translate(dx, dy, dz);
        return v;
    }

    public Vector3 Mul(double x, double y, double z)
    {
        Vector3 v = m.Mul(x, y, z).Translate(dx, dy, dz);
        return v;
    }

    /**
     * 
     * @return The embedded 3x3 describing a rotoscale
     */
    public Matrix3x3 Get3x3() {return m;}

    @Override
    public Object clone() {return Dup();}
}
