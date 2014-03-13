/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting;

import Jama.*;

/**
 *
 * @author Jason
 */
public class Vector3 {
    public static final Vector3 X = new Vector3(1.0, 0.0, 0.0);
    public static final Vector3 Y = new Vector3(0.0, 1.0, 0.0);
    public static final Vector3 Z = new Vector3(0.0, 0.0, 1.0);
    public static final Vector3 O = new Vector3();

    public Matrix v = new Matrix(3, 1);

    public Vector3() {}

    /**
     * Constructor used to take a copy of an existing Jama matrix
     * @param _v The Jama matrix to wrap
     */
    public Vector3(Matrix _v) {v = _v;}

    public Vector3(double x, double y)
    {
        this(x, y, 0.0);
    }

    public Vector3(double x, double y, double z)
    {
        double[][] tmp = {{x}, {y}, {z}};
        v = new Matrix(tmp);
    }

    public Vector3(Vector3 s)
    {
        v = s.v.copy();
    }

    public Vector3 Scale(Vector3 d)
    {
        double[][] a = v.getArray();
        a[0][0] *= d.x();
        a[1][0] *= d.y();
        a[2][0] *= d.z();
        return this;
    }

    public Vector3 Translate(Vector3 d)
    {
        v.plusEquals(d.v);
        return this;
    }

    public Vector3 Translate(double x, double y, double z)
    {
        double[][] a = v.getArray();
        a[0][0] += x;
        a[1][0] += y;
        a[2][0] += z;
        return this;
    }

    /**
     *
     * @return The length of this vector
     */
    public double Length()
    {
        return v.norm2();
    }

    /**
     * Adjusts the length while preserving the direction
     */
    public void SetLength(double r)
    {
        Normalize();
        v.timesEquals(r);
    }

    /**
     * Sets the length of this vector to 1 while preserving the ratio m_x/m_ys
     */
    public void Normalize()
    {
        v.timesEquals(1 / v.norm2());
    }

    public void SetX(double x) { v.set(0, 0, x); }
    public void SetY(double y) { v.set(1, 0, y); }
    public void SetZ(double z) { v.set(2, 0, z); }

    public void SetValue(double x, double y, double z)
    {
        SetX(x); SetY(y); SetZ(z);
    }

    public double x() {return v.get(0, 0);}
    public double y() {return v.get(1, 0);}
    public double z() {return v.get(2, 0);}

    /**
     *
     * @param v The vector with which to dot product this vector
     * @return The dot product of this vector with another vector
     */
    public double Dot(Vector3 V)
    {
        double[][] a = v.getArray();
        double[][] b = V.v.getArray();
        return a[0][0] * b[0][0] + a[1][0] * b[1][0] + a[2][0] * b[2][0];
    }

    /**
     * Computes the cross product [this cross v]
     * @param v The other vector with which to cross
     * @return The cross product of this vector with v
     */
    public Vector3 Cross(Vector3 v)
    {
        return new Vector3(
            -z() * v.y() + y() * v.z(),
             z() * v.x() - x() * v.z(),
            -y() * v.x() + x() * v.y()
        );
    }

    public Vector3 Add(Vector3 V)
    {
        v.plusEquals(V.v);
        return this;
    }

    public Vector3 Sub(Vector3 V)
    {
        v.minusEquals(V.v);
        return this;
    }

    public Vector3 Dup() {return new Vector3(this);}

    @Override
    public Object clone() {return new Vector3(this);}
}
