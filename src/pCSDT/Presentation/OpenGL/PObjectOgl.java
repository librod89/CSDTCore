/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Presentation.OpenGL;

import pCSDT.Scripting.*;
import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;
import java.awt.geom.*;
import com.sun.opengl.util.GLUT;
import java.util.*;

/**
 *
 * @author Jason
 */
public abstract class PObjectOgl extends PObject implements OpenGLDrawable
{

    protected static GLUT glut = new GLUT();

    public PObjectOgl(String name, String desc)
    {
        super(name, desc);
    }

    /**
     * @brief (Re)loads all textures associated with this object
     * @param gl The OpenGL object
     * @param glu The GL Utility object
     */
    public void OnLoadTextures(GL gl, GLU glu)
    {
    }

    public Vector<Vector3> GetPolyBound()
	{
       

        return new Vector<Vector3>(0);
    }

    @Override
    public void DrawSelected(GL gl, GLU glu, int frame, double dt)
    {
        Draw(gl, glu, frame, dt);
	// Draw the bounding box:
	gl.glColor3f(0.0f, 0.0f, 0.0f);
	gl.glDisable(gl.GL_TEXTURE_2D);
	gl.glBegin(gl.GL_LINE_LOOP);
	for(Vector3 v : GetPolyBound())
            gl.glVertex2d(v.x(), v.y());
        gl.glEnd();
        
       
    }

    @Override
    public void DrawMouseOver(GL gl, GLU glu, int frame, double dt)
    {
        Draw(gl, glu, frame, dt);
        // draw a bounding broken-line box around
        // Draw the bounding box:
	gl.glColor3f(0.0f, 0.0f, 0.0f);
	gl.glDisable(gl.GL_TEXTURE);
        gl.glLineStipple(1, (short)0xAAAA);
        gl.glEnable(gl.GL_LINE_STIPPLE);
	gl.glBegin(gl.GL_LINE_LOOP);
	for(Vector3 v : GetPolyBound())
            gl.glVertex2d(v.x(), v.y());
        gl.glEnd();
        gl.glDisable(gl.GL_LINE_STIPPLE);
    }

    @Override
    public PEngineOgl GetPEngine() {return (PEngineOgl)super.GetPEngine();}
}


