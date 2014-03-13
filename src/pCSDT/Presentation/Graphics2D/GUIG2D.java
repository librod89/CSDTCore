package pCSDT.Presentation.Graphics2D;

import pCSDT.Presentation.*;
import pCSDT.Scripting.*;
import javax.swing.*;
import java.util.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.awt.*;
import java.net.URL;

public abstract class GUIG2D extends GUI implements PropertyChangeListener {

    public JPanel g2jPanel;
    private PEngineG2D m_eng;
    
    // Timing things:
    int fps = 40;
    private int curFrame = 0;

    boolean prev_m_advGraphics;
    ArrayList<PObjectG2D> needsTextures = new ArrayList<PObjectG2D>();

    public GUIG2D()
    {
        prev_m_advGraphics = m_eng.m_advGraphics;
    }

    class G2JPanel extends JPanel implements Runnable {

        double dt = 1.0/fps*1000;

        public G2JPanel() {
            super();
            Thread t = new Thread(this);
            t.start();
        }

        public void TimeStep() {
            m_eng.Step(dt);
            for (PObject obj: m_eng.GetObjects()) {
                obj.Step(dt);
            }
        }

        @Override
        public void paint(Graphics graphics) {
            super.paint(graphics);
            java.awt.Graphics2D g = (java.awt.Graphics2D)graphics;
            // update texture if needed
            for (PObjectG2D obj: needsTextures)
                obj.OnLoadTextures();

            if (pEngineTextureBeUpdated) {
                m_eng.OnLoadTextures();
                pEngineTextureBeUpdated = false;
            }
            // draw the objects in the engine's store
            Vector<PObject> objs = m_eng.GetObjects();
            Collections.sort(objs);

            // Draw the engine:
            m_eng.Draw(this, g, curFrame, dt);
            // Draw all the child objects that we can draw:
            for(int i = 0; i < objs.size(); i++) {
                // Can we draw the current object?
                if(!(objs.get(i) instanceof OpenG2Drawable))
                    continue;

                PObjectG2D cur = (PObjectG2D)objs.get(i);

                if (renderState != eRenderState.Animating && ((PObjectG2D)objs.get(i)).IsSelected()) {
                    cur.DrawSelected(this, g, curFrame, dt);
                }
                else if(cur.IsMouseOver()) {
                    cur.DrawMouseOver(this, g, curFrame, dt);
                }
                else {
                    cur.Draw(this, g, curFrame, dt);
                }
            }
        }

        public void run() {
            try {
                while(true) {
                    TimeStep();
                    repaint();
                    Thread.sleep(1000/fps);
                }
            }
            catch (InterruptedException e) {}
        }
    }

    /**
     * As this is an Graphics2D canvas, any inheritors must obviously use
     * PEngineG2D-compatible implementations.
     * @return
     */
    public abstract PEngineG2D GetEngineG2D();
    
    @Override
    public final PEngine GetEngine()
    {
        if(m_eng == null)
        {
            m_eng = GetEngineG2D();
            m_eng.parent = this;
        }
        m_eng.DeferredInitialize();
        return m_eng;
    }

    @Override
    public JPanel CreateCanvas()
    {
        g2jPanel = new G2JPanel();
        g2jPanel.addPropertyChangeListener(this);
        g2jPanel.setVisible(true);

        return g2jPanel;
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
    
    public void propertyChange(PropertyChangeEvent e) {
    }

    public void LoadTextures()
    {
        if (!pEngineTextureBeUpdated) {
            m_eng.OnLoadTextures();
            pEngineTextureBeUpdated = true;
        }
        Vector<PObject> objs = m_eng.GetObjects();
        for (PObject obj: objs) {
            if (!(obj instanceof PObjectG2D))
                continue;
            PObjectG2D cur = (PObjectG2D)obj;
            cur.OnLoadTextures();
        }
    }

    @Override
    public PObject ConstructObject(Class c, String name, String desc)
    {
        PObject pObj = super.ConstructObject(c, name, desc);
        if (pObj instanceof PObjectG2D) {
            needsTextures.add((PObjectG2D)pObj);
        }
        return pObj;
    }

    @Override
    public boolean LoadFromXml(URL url) {
        if (!super.LoadFromXml(url)) {
            return false;
        }

        for (PObject obj: m_eng.GetObjects()) {
            if (obj instanceof PObjectG2D) {
                needsTextures.add((PObjectG2D)obj);
            }
        }
        return true;
    }

    protected Vector3 getActualLocVec(Point p)
    {
        float x = 2f*p.x/getCanvas().getWidth() - 1;
        float y = 2f*p.y/getCanvas().getHeight() - 1;
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
        /*
        x = (x * 2 / canvas.getWidth()) - 1;
        y = (-y * 2 / canvas.getHeight()) + 1;
         * 
         */
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
        /*
        x = (x * 2 / canvas.getWidth()) - 1;
        y = (-y * 2 / canvas.getHeight()) + 1;
         */
        return GetEngine().PickAll(x, y);
    }

    public float GetCanvasPreferredRelativeWidth() {
        return m_eng.range_x/400f*m_eng.scale;
    }

    public float GetCanvasPreferredRelativeHeight() {
        return m_eng.range_y/400f*m_eng.scale;
    }
}
