/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Presentation.OpenGL;

import pCSDT.Presentation.*;
import pCSDT.Scripting.*;
import javax.swing.*;
import javax.media.opengl.*;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.*;
import java.util.*;
import com.sun.opengl.util.FPSAnimator;
import java.awt.Point;
import javax.swing.event.*;
import java.net.*;


/**
 *
 * @author Jason
 * This is an implementation of the GUI class that is specialized for OpenGL
 * canvases and engines
 */
public abstract class GUIOgl extends GUI implements GLEventListener, AncestorListener {
    public GLJPanel gljPanel;
    private PEngineOgl m_eng;
    protected GL gl = null; // reference to current gl pointer
    protected GLU glu = new GLU();
    boolean prev_m_advGraphics;
   

    // This is the list of objects that still need to have their textures loaded
    ArrayList<PObjectOgl> needsTextures = new ArrayList<PObjectOgl>();

    // Timing things:
    FPSAnimator animator;
    int fps = 24;
    private int curFrame = 0;

    public GUIOgl()
    {
        prev_m_advGraphics = m_eng.m_advGraphics;
    }

    /**
     * As this is an OpenGL canvas, any inheritors must obviously use
     * PEngineOgl-compatible implementations.
     * @return
     */
    public abstract PEngineOgl GetEngineOgl();

    @Override
    public final PEngine GetEngine()
    {
        if(m_eng == null)
        {
            m_eng = GetEngineOgl();
            m_eng.parent = this;
        }
        m_eng.DeferredInitialize();
        return m_eng;
    }

    @Override
    public JPanel CreateCanvas()
    {
        gljPanel = new GLJPanel(new GLCapabilities());
        gljPanel.addGLEventListener(this);
        gljPanel.addAncestorListener(this);
        gljPanel.setVisible(true);
        gljPanel.setBackground(new java.awt.Color(255,0,0));
        animator = new FPSAnimator(gljPanel, fps);
        return gljPanel;
    }
    
    @Override
    protected void OnRenderStateChanged(eRenderState oldRs)
    {
        switch(GetRenderState())
        {
        case Stopped:
            // Reset everything...
            
            m_eng.Reset();
        }
    }
    
    public void Render(GL gl, double dt)
    {
	synchronized(this)
	{
            // Draw the engine:
            m_eng.Draw(gl, glu, curFrame, dt);

            Vector<PObject> objs = m_eng.GetObjects();

            // Draw all the child objects that we can draw:
            for(PObject obj : objs)
            {
                // Can we draw the current object?
		if(!(obj instanceof PObjectOgl))
                    continue;

		PObjectOgl cur = (PObjectOgl)obj;
                // this is where we want to skip the repeated draws if not animating.
                if (renderState != eRenderState.Animating && cur.IsSelected())
                    cur.DrawSelected(gl, glu, curFrame, dt);
                else if(cur.IsMouseOver())
                    cur.DrawMouseOver(gl, glu, curFrame, dt);
                else
                    cur.Draw(gl, glu, curFrame, dt);
            }

            // Step everything forward one tick:
            // if it is in animation state
            if (this.renderState == eRenderState.Animating) {
                m_eng.Step(dt);
                for(PObject obj : objs)
                    obj.Step(dt);
            }
	}
    }

    public void LoadTextures(GL gl)
    {
        if (!pEngineTextureBeUpdated) {
            m_eng.OnLoadTextures(gl, glu);
            pEngineTextureBeUpdated = true;
        }
	Vector<PObject> objs = m_eng.GetObjects();
	for(PObject obj : objs)
        {
            if(!(obj instanceof PObjectOgl))
                continue;

            PObjectOgl cur = (PObjectOgl)obj;
            cur.OnLoadTextures(gl, glu);
        }
    }

    @Override
    public PObject ConstructObject(Class c, String name, String desc)
    {
	PObject pObj = super.ConstructObject(c, name, desc);
	if(pObj instanceof PObjectOgl)
            needsTextures.add((PObjectOgl)pObj);
	return pObj;
    }

    @Override
    public boolean LoadFromXml(URL url)
    {
	if(!super.LoadFromXml(url))
            return false;

	for(PObject obj : m_eng.GetObjects())
            if(obj instanceof PObjectOgl)
		needsTextures.add((PObjectOgl)obj);
	return true;
    }

    ///
    // Implementations for GLEventListener
    ///
    @Override
    public void init(GLAutoDrawable drawable)
    {
        gl = drawable.getGL();
        
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

        gl.glHint(GL.GL_POLYGON_SMOOTH_HINT, GL.GL_DONT_CARE);
        gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_DONT_CARE);

        // Start if not already started
        if(!animator.isAnimating())
	{
            animator.start();
            LoadTextures(drawable.getGL());
	}
    }
    
    @Override
    public void display(GLAutoDrawable drawable)
    {
        if (prev_m_advGraphics != GetEngine().m_advGraphics) {
            if (GetEngine().m_advGraphics) {
                gl.glEnable(GL.GL_LINE_SMOOTH);
                gl.glEnable(GL.GL_POLYGON_SMOOTH);
            }
            else {
                gl.glDisable(GL.GL_LINE_SMOOTH);
                gl.glDisable(GL.GL_POLYGON_SMOOTH);
            }
            ////GetEngineOgl().OnLoadTextures(gl, glu);
            prev_m_advGraphics = GetEngine().m_advGraphics;
        }
        
        
        for(PObjectOgl obj : needsTextures)
            obj.OnLoadTextures(drawable.getGL(), glu);
	needsTextures.clear();

        if (pEngineTextureBeUpdated) {
            m_eng.OnLoadTextures(gl, glu);
            ////System.out.println("pEngineTextureBeUpdate set to false by GUIOgl.display()");
            pEngineTextureBeUpdated = false;
        }

        GLContext glc = drawable.getContext();
        glc.makeCurrent();
        switch(GetRenderState())
        {
        case Stopped:
            if(!animation){
                if(needsToDraw){
                   Render(drawable.getGL(), 0);
                    
                   needsToDraw = false;
                }
            }else{
                   Render(drawable.getGL(), 0);
                }
            break;
        case Animating:
            if(!animation){
                if(needsToDraw){
                   Render(drawable.getGL(), 0);
                    
                   needsToDraw = false;
                }
            }else{
                Render(drawable.getGL(), 1.0f / fps);
                }
          
            
            break;
        }
    }

    @Override
    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged)
    {
        LoadTextures(drawable.getGL());
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
    {
        GL gl = drawable.getGL();
        gl.glViewport(0, 0, width, height);
	LoadTextures(drawable.getGL());
        needsToDraw = true;
    }
    
    ///
    // Implementations for ComponentListener:
    ///
    @Override
    public void ancestorAdded(AncestorEvent e) {}

    @Override
    public void ancestorMoved(AncestorEvent e) {}

    @Override
    public void ancestorRemoved(AncestorEvent e) {}

    @Override
    protected Vector3 getActualLocVec(Point p)
    {
        float x = 2f*p.x/getCanvas().getWidth() - 1;//(p.x - width/2.0f)/(width/2.0f);
        float y = 1 - 2f*p.y/getCanvas().getHeight();//-(p.y - height/2.0f)/(height/2.0f);
        Matrix4x4 A = GetEngine().GetTransformMatrix();
        Matrix4x4 a = A.Dup().Invert();
        return a.Mul(x, y);
    }

    /**
     * Attempts to pick a particular object out of the rendering set, and
     * return that object.
     * @param x The x-coordinate, relative to the top-left corner of the panel
     * @param y The y-coordinate, relative to the top-left corner of the panel
     * @return A picked object, or null if no such object exists.  Optionally,
     * it may return the engine instead, if the engine has properties.
     */
    public PObject Pick(double x, double y)
    {
        x = (x * 2 / getCanvas().getWidth()) - 1;
        y = (-y * 2 / getCanvas().getHeight()) + 1;
        return GetEngine().Pick(x, y);
    }

    /**
     * Attempts to pick all objects lying on (x, y) relative to the top-left
     * corner of the panel
     * @param x The x-coordinate, relative to the top-left corner of the panel
     * @param y The y-coorindate, relative to the top-left corner of the panel
     * @return A Vector of Pobject
     */
    public Vector<PObject> PickAll(double x, double y)
    {
        x = (x * 2 / getCanvas().getWidth()) - 1;
        y = (-y * 2 / getCanvas().getHeight()) + 1;
        return GetEngine().PickAll(x, y);
    }

    public float GetCanvasPreferredRelativeWidth() {
        return (m_eng.range_x)/400f*m_eng.scale;
    }

    public float GetCanvasPreferredRelativeHeight() {
        return (m_eng.range_y)/400f*m_eng.scale;
    }
    
}
