/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting;

import Jama.*;

/**
 * A 3x3 matrix that can describe a scale, affine, and rotation.
 * @author Jason
 */
public class Matrix3x3 {
    Matrix m = Matrix.identity(3, 3);

    private Matrix3x3(Matrix _m) {m = _m;}

    public Matrix3x3() {}

    public Matrix3x3(Vector3 r, double theta)
    {
        double s = Math.sin(theta);
        double c = Math.cos(theta);

        double[][] tmp =
        {
            {
                (1 - r.x() * r.x()) * c + r.x() * r.x(),
                r.x() * r.y() * (1 - c) + r.z() * s,
                r.x() * r.z() * (1 - c) - r.y() * s
            },
            {
                r.x() * r.y() * (1 - c) - r.z() * s,
                (1 - r.y() * r.y()) * c + r.y() * r.y(),
                r.y() * r.z() * (1 - c) + r.x() * s
            },
            {
                r.x() * r.z() * (1 - c) + r.y() * s,
                r.y() * r.z() * (1 - c) - r.x() * s,
                (1 - r.z() * r.z()) * c + r.z() * r.z()
            }
        };
        m = new Matrix(tmp);
    }

    public Matrix3x3(Matrix3x3 src)
    {
        m = src.m.copy();
    }

    public Matrix3x3(double theta)
    {
        double[][] tmp =
        {
            {Math.cos(theta), Math.sin(theta), -Math.sin(theta)},
            {Math.cos(theta), 0.0, 0.0},
            {1.0, 0.0, 0.0}
        };
        m = new Matrix(tmp);
    }

    public Matrix3x3 Dup()
    {
        return new Matrix3x3(this);
    }

    /**
     * Computes this * M and stores it in this.
     * @param M
     * @return
     */
    public Matrix3x3 Mul(Matrix3x3 M)
    {
        m = m.times(M.m);
        return this;
    }

    /**
     * Performs an entrywise negation of every element in the matrix
     * @return this
     */
    public Matrix3x3 Neg()
    {
        m.timesEquals(-1.0);
        return this;
    }

    public Matrix3x3 Add(Matrix3x3 A)
    {
        m = m.plus(A.m);
        return this;
    }

    public Matrix3x3 Sub(Matrix3x3 A)
    {
        m = m.minus(A.m);
        return this;
    }

    /**
     * Scales the matrix in the x-dimension
     * @param sx The scaling coefficient; values greater than 1 magnify, values less than 1 minify
     * @return this
     */
    public Matrix3x3 Scale(double sx, double sy, double sz)
    {
        double[][] tmp = {{sx, 0, 0},{0, sy, 0},{0, 0, sz}};
        Matrix v = new Matrix(tmp);
        m = m.times(v);
        return this;
    }

    /**
     * Scales the matrix in the x-dimension
     * @param sx The scaling coefficient; values greater than 1 magnify, values less than 1 minify
     * @return this
     */
    public Matrix3x3 ScaleX(double sx)
    {
        return Scale(sx, 1, 1);
    }

    /**
     * Scales the matrix in the y-dimension
     * @param sy The scaling coefficient; values greater than 1 magnify, values less than 1 minify
     * @return this
     */
    public Matrix3x3 ScaleY(double sy)
    {
        return Scale(1, sy, 1);
    }

    /**
     * Scales the matrix in the z-dimension
     * @param sz The scaling coefficient; values greater than 1 magnify, values less than 1 minify
     * @return this
     */
    public Matrix3x3 ScaleZ(double sz)
    {
        return Scale(1, 1, sz);
    }

    /**
     * Scales the matrix uniformly in all spacial dimensions
     * @param sx The scaling coefficient; values greater than 1 magnify, values less than 1 minify
     * @return this
     */
    public Matrix3x3 ScaleUniform(double s)
    {
        m.timesEquals(s);
        return this;
    }

    /**
     * Inverts the matrix
     * @return The inverted matrix
     */
    public Matrix3x3 Invert()
    {
        m = m.inverse();
        return this;
    }

    public double Det()
    {
        return m.det();
    }

    public Vector3 Mul(Vector3 V)
    {
        return new Vector3(m.times(V.v));
    }

    public Vector3 Mul(double x, double y)
    {
        double[][] b = m.getArray();
        return new Vector3(
            b[0][0] * x + b[0][1] * y,
            b[1][0] * x + b[1][1] * y,
            b[2][0] * x + b[2][1] * y
        );
    }

    public Vector3 Mul(double x, double y, double z)
    {
        double[][] b = m.getArray();
        return new Vector3(
            b[0][0] * x + b[0][1] * y + b[0][2] * z,
            b[1][0] * x + b[1][1] * y + b[1][2] * z,
            b[2][0] * x + b[2][1] * y + b[2][2] * z
        );
    }

    /**
     * This algorithm uses QR decomposition to compute the view vector of this matrix, assuming that the default view vector is in the -z direction.
     * @return The basis of the null space of this matrix
     */
    public Vector3 ZSpace()
    {
        Matrix t = m.copy();
        double[][] ary = t.getArray();
        ary[2][0] = ary[2][1] = ary[2][2] = 0;

        QRDecomposition qr = t.qr();
        Matrix Q = qr.getQ();

        ary = Q.getArray();
        return new Vector3(ary[0][2], ary[1][2], ary[2][2]);
    }

    /**
     * 
     * @return The wrapped Jama matrix object
     */
    public Matrix GetM() {return m;}

    @Override
    public Object clone() {return Dup();}
}
