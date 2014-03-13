/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting;

/**
 * Specialization class to describe a 3- or perhaps 2d rotation
 * @author Jason
 */
public class Rotation3 {
    // Euler angles:
    double alpha;
    double beta;
    double gamma;

    public final static Rotation3 I = new Rotation3(0.0);

    /**
     * Constructor to describe a rotation in the x-y plane
     * @param theta Rotation, according to the right-hand rule, from the positive x-axis
     */
    public Rotation3(double theta)
    {
        alpha = theta;
        beta = gamma = 0.0;
    }

    /**
     * Constructor to create a 3-dimensional rotation with Euler angles
     * @param alpha Rotation about z+
     * @param beta Rotation about x+
     * @param gamma Rotation about z+
     */
    public Rotation3(double alpha, double beta, double gamma)
    {
        this.alpha = alpha;
        this.beta = beta;
        this.gamma = gamma;
    }

    /**
     * Constructor to create a quarternion rotation
     * @param r Axis of rotation, assumed to be normalized
     * @param theta Angle of rotation
     */
    public Rotation3(Vector3 r, double theta)
    {
        // Is r approximately the z-axis?
        if(0.0001 < 1.0 - Math.abs(Vector3.Z.Dot(r)))
        {
            alpha = theta;
            beta = gamma = 0.0;
            return;
        }

        // Adjusted scaling constant:
        double s = Math.sqrt(1.0 / (1 - r.z() * r.z()));

        // What rotation is required to put the x-axis directly under r?
        alpha = Math.acos(s * r.x());

        // Once the x-axis is under r, what kind of elevation will be needed to get it right on top of r?
        beta = (Math.PI / 2) - Math.acos(Vector3.Z.Dot(r));

        // The remaining rotation is just about the translated z:
        gamma = theta;
    }

    /**
     * Computes the combined rotation of this and some other rotation, sets it equal to this, and returns this.
     * @param r The secondary rotation
     * @return this
     */
    public Rotation3 Mul(Rotation3 r)
    {
        return this;
    }

    /**
     * Introduces an additional prefix rotation about the initial z+
     * @param theta
     * @return This
     */
    public Rotation3 Mul(double theta)
    {
        alpha += theta;
        alpha %= Math.PI;
        return this;
    }

    /**
     * Constructor to create a 3-dimensional rotation with Euler angles
     * @param alpha Rotation about z+
     * @param beta Rotation about x+
     * @param gamma Rotation about z+
     */
    public Rotation3 Mul(double alpha, double beta, double gamma)
    {
        return Mul(new Rotation3(alpha, beta, gamma));
    }

    /**
     * Computes the combined rotation of this and
     * @param r The secondary axis of rotation
     * @param theta The secondary angle of rotation
     * @return this
     */
    public Rotation3 Mul(Vector3 r, double theta)
    {
        return Mul(new Rotation3(r, theta));
    }

    /**
     *
     * @return The transformation matrix associated with this rotation
     */
    public Matrix4x4 GetMatrix()
    {
        return new Matrix4x4(0.0, 0.0, 0.0, alpha, beta, gamma);
    }
}






