package pCSDT.Presentation.OpenGL;

import pCSDT.Presentation.*;
import com.sun.opengl.util.FPSAnimator;
import java.util.Vector;
import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.GLEventListener;
import java.awt.*;
import java.nio.FloatBuffer;
import pCSDT.Scripting.*;

public class GLScene implements GLEventListener
{
    PEngineOgl m_eng;

    float[] LightAmbient =
    {
        0.0f, 0.0f, 0.0f, 1.0f
    };
    float[] LightDiffuse =
    {
        1.0f, 1.0f, 1.0f, 1.0f
    };
    float[] LightPosition =
    {
        0.0f, 10.0f, 10.0f, 1.0f
    };
    
    // Class variables to be changed during runtime
    public int currentFrame = 0;
    public boolean isPlaying = false;
    public boolean repeat = false;
    public int startingFrame = 0,  endingFrame = 0;
    public float speed = 0.06f;
    public int framerate = 24;
    public Vector<frameListener> listeners;
    public FPSAnimator animator;
    public GLCanvas glCanvas;
    protected GLU glu;
    protected GL gl;
    public static int[] textures = new int[2];
    public static Color backGroundColor = new Color(0.0f, 0.0f, 0.0f);

    // These are the dimensions of the actual canvas
    public int cx;
    public int cy;
    
    // This is the current time in the animation, the starting time, and the
    // overlap time:
    public double t = 0;
    public double s = 0;
    public double o = 0;

    public GLScene(PEngineOgl eng)
    {
        this(eng, 400, 300);
    }

    public GLScene(PEngineOgl eng, int cx, int cy)
    {
        m_eng = eng;
        init(cx, cy);
    }

    public void init(int cx, int cy)
    {
        GLCapabilities caps = new GLCapabilities();
        caps.setDoubleBuffered(true);
        caps.setStereo(false);
        glCanvas = new GLCanvas(caps);
        glCanvas.setSize(cx, cy);
        glCanvas.addGLEventListener(this);
        animator = new FPSAnimator(glCanvas, framerate);
        animator.start();
        glu = new GLU();
        listeners = new Vector<frameListener>();
    }

    public void doCleanup()
    {
        animator.stop();
        glCanvas.removeGLEventListener(this);
        currentFrame = 0;
        isPlaying = false;
        t = 0;
        speed = 0.02f;
    }

    public void DrawFrame(int curFrame, double dt)
    {
        Vector<PObject> objs = m_eng.GetObjects();
        for(int i = 0; i < objs.size(); i++)
            objs.get(i).Step(dt);
        
        // Draw the engine:
        m_eng.Draw(gl, glu, curFrame, dt);
        
        // Draw all the child objects that we can draw:
        for(int i = 0; i < objs.size(); i++)
        {
            // Can we draw the current object?
            if(!(objs.get(i) instanceof OpenGLDrawable))
                continue;
            
            OpenGLDrawable cur = (OpenGLDrawable)objs.get(i);
            cur.Draw(gl, glu, curFrame, dt);
        }
        
        // Step everything forward one tick:
        m_eng.Step(dt);
    }

    /**
     * void init() Called just AFTER the GL-Context is created.
     */
    @Override
    public void init(GLAutoDrawable glAutoDrawable)
    {
        gl = glAutoDrawable.getGL();

        gl.glShadeModel(GL.GL_SMOOTH);                         //Enables Smooth Color Shading
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);               //This Will Clear The Background Color To Black
        gl.glClearDepth(1.0);                                  //Enables Clearing Of The Depth Buffer
        gl.glEnable(GL.GL_DEPTH_TEST);                            //Enables Depth Testing
        gl.glDepthFunc(GL.GL_LEQUAL);                             //The Type Of Depth Test To Do
        gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);  // Really Nice Perspective Calculations
        gl.glLightfv(GL.GL_LIGHT1, GL.GL_AMBIENT, FloatBuffer.wrap(LightAmbient));        // Setup The Ambient Light
        gl.glLightfv(GL.GL_LIGHT1, GL.GL_DIFFUSE, FloatBuffer.wrap(LightDiffuse));        // Setup The Diffuse Light
        gl.glLightfv(GL.GL_LIGHT1, GL.GL_POSITION, FloatBuffer.wrap(LightPosition));    // Position The Light
        gl.glEnable(GL.GL_LIGHT1);                                // Enable Light One
        gl.glEnable(GL.GL_NORMALIZE);                             // Enable normalizing
        gl.glEnable(GL.GL_LIGHTING);                                // Enable lighting
        gl.glColorMaterial(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT_AND_DIFFUSE);
        gl.glEnable(GL.GL_COLOR_MATERIAL);
    }
    
    //   Called by the drawable to initiate OpenGL rendering by the client.
    @Override
    public void display(GLAutoDrawable glAutoDrawable)
    {
        GLContext glj = glAutoDrawable.getContext();
        glj.makeCurrent();      //Ensure GL is initialised correctly

        double dt;
        
        // We could compute the time offset, but for real-world performance
        // reasons we just increment by the framerate every time.  Otherwise,
        // we can introduce some strange error conditions when our own
        // computation of the interval period interacts with the FPSAnimator
        if(false)
        {
            t = System.currentTimeMillis() / 1000.0;
            dt = t - s;
            
            // Bound dt in the range [1.0 / framerate, 3s]
            if(dt < 1.0 / framerate)
                dt = 1.0 / framerate;
            else if(3 < dt)
                dt = 3;
        }
        else
            dt = 1.0 / framerate;

        // Execute the timestep:
        DrawFrame(currentFrame, dt);
    }
    //Called by the drawable during the first repaint after the component has been resized.
    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int i, int i1, int width, int height)
    {
        gl = glAutoDrawable.getGL();
        gl.glViewport(0, 0, width, height);                       // Reset The Current Viewport And Perspective Transformation

        // Set the projection matrix:
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();

        //glu.gluPerspective(45.0f, width / height, 0.1f, 150.0f);
        cx = width;
        cy = height;
        
        // Switch back:
        gl.glMatrixMode(GL.GL_MODELVIEW);                            // Select The Modelview Matrix
        gl.glLoadIdentity();                                      // Reset The ModalView Matrix
    }

    @Override
    public void displayChanged(GLAutoDrawable glAutoDrawable, boolean b, boolean b1)
    {
    }

    private void fireFrameChanged(int currentFrame)
    {
        int i;
        for (i = 0; i < listeners.size(); i++)
        {
            listeners.elementAt(i).frameChanged(currentFrame);
        }
    }

    public void addFrameListener(frameListener listener)
    {
        listeners.add(listener);
    }

    public void removeFrameListener(frameListener listener)
    {
        listeners.remove(listener);
    }

    public void startPlaying()
    {
        isPlaying = true;
        s = System.currentTimeMillis() / 1000.0;
    }

    public void startPlaying(int startingFrame, int endingFrame, boolean repetitions)
    {
        this.startingFrame = startingFrame;
        this.endingFrame = endingFrame;
        this.repeat = repetitions;
        this.currentFrame = startingFrame;
        startPlaying();
    }

    public void pausePlaying()
    {
        this.isPlaying = false;
    }

    public void resumePlaying()
    {
        this.isPlaying = true;
    }

    public void endPlaying()
    {
        if(isPlaying)
        {
            isPlaying = false;
            animator.stop();
        }
    }
}

