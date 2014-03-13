/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting;

import java.awt.Point;

/**
 * The definition of a 3D point in integers
 * @author tylau
 */
public class Point3D implements Cloneable {
    public int x;
    public int y;
    public int z = -1;

    public Point3D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point3D(Point p) {
        x = p.x;
        y = p.y;
    }

    public Point3D(Point p, int z) {
        x = p.x;
        y = p.y;
        this.z = z;
    }

    public Point3D(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void SetXY(Point p) {
        x = p.x;
        y = p.y;
    }

    public Point Get2DPoint() {
        return new Point(x, y);
    }

    @Override
    public Object clone() {
        return new Point3D(x, y, z);
    }
}
