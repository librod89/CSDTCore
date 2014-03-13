/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Presentation.OpenGL;

import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;

/**
 *
 * @author Jason
 */
public interface OpenGLDrawable {
    /**
     * Prepares the canvas for drawing
     * @param gl GL context
     * @param glu GLU toolkit
     * @param frame Current frame index
     * @param dt Time step since last frame
     * Potentially, the time step could be zero.  If it is, this indicates
     * a stationary frame where no animation is taking place.
     */
    public void Draw(GL gl, GLU glu, int frame, double dt);
	
    /**
     * Draw selected override
     * @param gl GL context
     * @param glu GLU toolkit
     * @param frame Current frame index
     * @param dt Time step since last frame
     */
    public void DrawSelected(GL gl, GLU glu, int frame, double dt);

    /**
     * Draw mouse over override
     * @param gl GL context
     * @param glu GLU toolkit
     * @param frame Current frame index
     * @param dt Time step since last frame
     */
    public void DrawMouseOver(GL gl, GLU glu, int frame, double dt);
}
