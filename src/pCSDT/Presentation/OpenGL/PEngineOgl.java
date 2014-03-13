/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Presentation.OpenGL;

import com.sun.opengl.util.GLUT;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;
import com.sun.opengl.util.texture.TextureIO;
import java.awt.Dimension;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.imageio.ImageIO;
import pCSDT.Scripting.*;
import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;
import pCSDT.Presentation.GUI;
import pCSDT.Presentation.GUI.eRenderState;
import pCSDT.Presentation.GUI.eGridStyle;

/**
 * This is apparently intended for displaying a 2D scene using OpenGL (jogl)
 * @author Jason
 */
public abstract class PEngineOgl extends PEngine implements OpenGLDrawable {
    // fill in the jar and ext dependencies in the following
    String[][] graphicsJars1 = {};
    String[][] graphicsExts1 = {
        {"java3d-latest", "../media/java3d/webstart/release/java3d-latest.jnlp"},
        {"jogl", "../media/jogl/builds/archive/jsr-231-1.x-webstart-current/jogl.jnlp"},
        {"gluegen-rt", "../media/gluegen/webstart/gluegen-rt.jnlp"}
    };
    String[][] graphicsOsSpecNativeLibs1 = {};

    // originally, the display canva is a [(-1,-1,-1),(1,1,1)] bounded rectangle
    // first translate origin by ...
    public float translate_x = 0, translate_y = 0; ///, translate_z = 0;

    @AutomatableProperty(name="scale", DisplayName="scale (pixels per unit)", desc="scale", DesignTimeBehavior="A", RunTimeBehavior="H")
    public float scale = 400f/15;  // pixel per inc
    
    @AutomatableProperty(name="x range", DisplayName="Preferred x range", desc="x range", DesignTimeBehavior="A", RunTimeBehavior="H")
    public float range_x = 15;

    @AutomatableProperty(name="y range", DisplayName="Preferred y range", desc="y range", DesignTimeBehavior="A", RunTimeBehavior="H")
    public float range_y = 15;

    ////@AutomatableProperty(name="z range", DisplayName="Preferred z range", desc="z range", DesignTimeBehavior="A", RunTimeBehavior="H")
    ////public float range_z = 15;

    protected boolean bDrawGridBeyondRange = true;
    
    protected boolean bDrawAnimate = true;//

    // interval between tick marks
    @AutomatableProperty(name="x interval", desc="x interval", DesignTimeBehavior="A", RunTimeBehavior="H")
    public float interval_x = 1;
    
    @AutomatableProperty(name="y interval", desc="y interval", DesignTimeBehavior="A", RunTimeBehavior="H")
    public float interval_y = 1;

    //, interval_z = 1;

    protected GUIOgl parent;

    protected Texture bgTexture = null;  // background texture
    
    protected GLUT glut = new GLUT();
    public PEngineOgl(Class[] objTypes)
    {
        this("", "", objTypes);
    }

    public PEngineOgl(String name, String desc, Class[] objTypes) {
        super(name, desc, objTypes);
        graphicsJars = graphicsJars1;
        graphicsExts = graphicsExts1;
        graphicsOsSpecNativeLibs = graphicsOsSpecNativeLibs1;
    }

    public void TranslateAndScale(GL gl, GLU glu, int frame, double dt) {
        Dimension d = m_gui.getCanvasSize();

        gl.glTranslatef(translate_x, translate_y, 0);
        // the following assumes view angle is parallel to the z axis
        gl.glScalef(scale*2/d.width, scale*2/d.height, 1);
    }

    public void DrawBackground(GL gl, GLU glu, int frame, double dt) {
        // Clear depth and color:
        StringTokenizer st = new StringTokenizer(bg_color, ",");
        float bg_r = Integer.parseInt(st.nextToken().trim())/255.0f;
        float bg_g = Integer.parseInt(st.nextToken().trim())/255.0f;
        float bg_b = Integer.parseInt(st.nextToken().trim())/255.0f;
        gl.glClearColor(bg_r, bg_g, bg_b, 1.0f);
        gl.glClearDepth(1.0);
        gl.glClear(gl.GL_COLOR_BUFFER_BIT | gl.GL_DEPTH_BUFFER_BIT);

        if (bgTexture != null) {
            TextureCoords tc = bgTexture.getImageTexCoords();
            gl.glPushMatrix();
            gl.glColor4f(1f, 1f, 1f, this.textAlpha);
            {
                gl.glEnable(gl.GL_TEXTURE_2D);
                gl.glTexEnvf(gl.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);

                bgTexture.enable();
                bgTexture.bind();

                gl.glBegin(gl.GL_QUADS);
                    gl.glTexCoord3d(tc.left(), tc.top(), 0);
                    gl.glVertex3f(textStartx, textStarty+textHeight, 0f);
                    gl.glTexCoord3d(tc.right(), tc.top(), 0);
                    gl.glVertex3f(textStartx+textLength, textStarty+textHeight, 0f);
                    gl.glTexCoord3d(tc.right(), tc.bottom(), 0);
                    gl.glVertex3f(textStartx+textLength, textStarty, 0f);
                    gl.glTexCoord3d(tc.left(), tc.bottom(), 0);
                    gl.glVertex3f(textStartx, textStarty, 0f);
                gl.glEnd();

                bgTexture.disable();
                gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);
                gl.glDisable(gl.GL_TEXTURE_2D);
            }
            gl.glPopMatrix();
        }
    }

    public void DrawGrid(GL gl, GLU glu, int frame, double dt) {
        if (bDrawGrid && this.m_gui.GetRenderState()!=eRenderState.Animating) {

            /*
            gl.glBegin(gl.GL_LINES);
            // Draw x- and y-axes:
            gl.glColor3f(0.0f, 0.0f, 0.0f);
            gl.glVertex3d(min_x, 0, 0);
            gl.glVertex3d(max_x, 0, 0);
            gl.glVertex3d(0, min_y, 0);
            gl.glVertex3d(0, max_y, 0);
            gl.glVertex3d(0, 0, min_z);
            gl.glVertex3d(0, 0, max_z);
            gl.glEnd();
             *
             */

            StringTokenizer st = new StringTokenizer(grid_color, ",");
            float grid_r = Integer.parseInt(st.nextToken().trim())/255.0f;
            float grid_g = Integer.parseInt(st.nextToken().trim())/255.0f;
            float grid_b = Integer.parseInt(st.nextToken().trim())/255.0f;

            st = new StringTokenizer(grid_number_color, ",");
            float grid_num_r = Integer.parseInt(st.nextToken().trim())/255.0f;
            float grid_num_g = Integer.parseInt(st.nextToken().trim())/255.0f;
            float grid_num_b = Integer.parseInt(st.nextToken().trim())/255.0f;

            gl.glPushMatrix();
            gl.glLineWidth(1);
            // Scale before x and y marks:
            gl.glBegin(gl.GL_LINES);
            gl.glColor3f(grid_r, grid_g, grid_b);

            Dimension d = m_gui.getCanvasSize();
            float cmin_x = -1; float cmax_x = 1;
            cmin_x = cmin_x - translate_x;
            cmax_x = cmax_x - translate_x;
            if (bDrawGridBeyondRange) {
                float scaleFactor = d.width/2/scale;
                cmin_x = cmin_x*scaleFactor; cmax_x = cmax_x*scaleFactor;
            }
            else {
                // compute the min and max from the range and split ratio
                // rmin, rmax are relative distance from 0
                float rmin = cmin_x; float rmax = cmax_x;
                float scaleFactor = range_x/(rmax - rmin);
                cmax_x = rmax*scaleFactor;
                cmin_x = rmin*scaleFactor;
            }
            
            float cmin_y = -1; float cmax_y = 1;
            cmin_y = cmin_y - translate_y;
            cmax_y = cmax_y - translate_y;
            if (bDrawGridBeyondRange) {
                float scaleFactor = d.height/2/scale;
                cmin_y = cmin_y*scaleFactor; cmax_y = cmax_y*scaleFactor;
            }
            else {
                float rmin = cmin_y; float rmax = cmax_y;
                float scaleFactor = range_y/(rmax-rmin);
                cmax_y = rmax*scaleFactor;
                cmin_y = rmin*scaleFactor;
            }
            
            // TODO: min_x, min_y, max_x, max_y should be multiple of interval_x
            float umin_x, umin_y, umax_x, umax_y;

            // expand a bit if draw beyond grid, else reduce a bit
            if (bDrawGridBeyondRange) {
                float rx1 = cmin_x % interval_x;
                if (rx1 == 0) {
                    umin_x = cmin_x;
                }
                else {
                    if (cmin_x < 0) umin_x = cmin_x - (interval_x + cmin_x % interval_x);
                    else umin_x = cmin_x - cmin_x % interval_x;
                }

                float rx2 = cmax_x % interval_x;
                if (rx2 == 0) {
                    umax_x = cmax_x;
                }
                else {
                    if (cmax_x < 0) umax_x = cmax_x + (interval_x + cmax_x % interval_x);
                    else umax_x = cmax_x + (interval_x - cmax_x % interval_x);
                }

                float ry1 = cmin_y % interval_y;
                if (ry1 == 0) {
                    umin_y = cmin_y;
                }
                else {
                    if (cmin_y < 0) umin_y = cmin_y - (interval_y + cmin_y % interval_y);
                    else umin_y = cmin_y - cmin_y % interval_y;
                }

                float ry2 = cmax_y % interval_y;
                if (ry2 == 0) {
                    umax_y = cmax_y;
                }
                else {
                    if (cmax_y < 0) umax_y = cmax_y + (interval_y + cmax_y % interval_y);
                    else umax_y = cmax_y + (interval_y - cmax_y % interval_y);
                }
            }
            else {
                float rx1 = cmin_x % interval_x;
                if (rx1 == 0) {
                    umin_x = cmin_x;
                }
                else {
                    if (cmin_x < 0) umin_x = cmin_x - (cmin_x % interval_x);
                    else umin_x = cmin_x + (interval_x - cmin_x % interval_x);
                }

                float rx2 = cmax_x % interval_x;
                if (rx2 == 0) {
                    umax_x = cmax_x;
                }
                else {
                    if (cmax_x < 0) umax_x = cmax_x - (interval_x + cmax_x % interval_x);
                    else umax_x = cmax_x - cmax_x % interval_x;
                }
                
                float ry1 = cmin_y % interval_y;
                if (ry1 == 0) {
                    umin_y = cmin_y;
                }
                else {
                    if (cmin_y < 0) umin_y = cmin_y - (cmin_y % interval_y);
                    else umin_y = cmin_y + (interval_y - cmin_y % interval_y);
                }

                float ry2 = cmax_y % interval_y;
                if (ry2 == 0) {
                    umax_y = cmax_y;
                }
                else {
                    if (cmax_y < 0) umax_y = cmax_y - (interval_y + cmax_y % interval_y);
                    else umax_y = cmax_y - cmax_y % interval_y;
                }
            }
            
            // Draw x- y- marks on z = 0 plane
            
            if(this.m_gui.GetGridStyle() == eGridStyle.Cartesian){
            for(float i = umin_x; i <= umax_x; i+=interval_x)
            {
                // Draw x marks:
                gl.glVertex3d(i, cmin_y, 0);
                gl.glVertex3d(i, cmax_y, 0);
            }
            for (float i = umin_y; i <= umax_y; i+=interval_y)
            {
                // Draw y marks:
                gl.glVertex3d(cmin_x, i, 0);
                gl.glVertex3d(cmax_x, i, 0);
            }
            }else{
                
                //float centerx = cmin_x + (cmin_x+cmax_x)/2;
                //float centery = cmin_y + (cmin_y+cmax_y)/2;
                float centerx = 0;
                float centery = 0;
                double radius = centerx;
                gl.glTranslatef(0,0,0);
                gl.glBegin(gl.GL_TRIANGLE_FAN);
                for(float i = umin_x; i <= umax_x; i += interval_x){
                
                for (int angle = 0; angle < 360; angle++) {
                double a_radian = angle*Math.PI/180.0f;
                 gl.glVertex2d(Math.sin(a_radian)*radius, Math.cos(a_radian)*radius);
             }
                radius += interval_x;
            }
                for (int angle = 0; angle < 360; angle += 30) {
                double a_radian = angle*Math.PI/180.0f;
                 gl.glVertex2d(0, 0);
                 gl.glVertex2d(Math.sin(a_radian)*radius, Math.cos(a_radian)*radius);
                 
                 String str = String.valueOf(angle);
                 //String str = null;
                 //str = "30";
                 gl.glRasterPos3d(Math.sin(a_radian)*radius,Math.cos(a_radian)*radius, 0f);
                 //glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10, "30"  );
                 //glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10, str);
                 
             }
            }
                
            /*
            // Draw x- z- marks on y = 0 plane
            for (float i = cmin_x; i <= cmax_x; i+=interval_x) {
                // Draw x marks:
                gl.glVertex3d(i, 0, min_z);
                gl.glVertex3d(i, 0, max_z);
            }
            for (float i=min_z; i <= max_z; i+=interval_z) {
                // Draw z marks:
                gl.glVertex3d(min_x, 0, i);
                gl.glVertex3d(max_x, 0, i);
            }
             *
             */
            gl.glEnd();

            String str = null;
            StringBuilder sb = new StringBuilder();
            sb.append("#.");
            for (int i = 0; i < grid_number_dp; i++) {
                sb.append("0");
            }
            DecimalFormat df = new DecimalFormat(sb.toString());
            gl.glColor3f(grid_num_r,grid_num_g,grid_num_b);
            // Draw the numbers
            for(float i = umin_x; i <= umax_x; i+=interval_x) {
                if (grid_number_dp == 0) {
                    str = String.valueOf((long)Math.round(i));
                }
                else {
                    str = df.format(pCSDT.Utility.Round(i, grid_number_dp));
                }
                gl.glRasterPos3f(i-interval_x/20,0,0);
                glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10, str );
            }
            for(float i = umin_y; i <= umax_y; i+=interval_y) {
                if (i != 0) {
                    if (grid_number_dp == 0) {
                        str = String.valueOf((long)Math.round(i));
                    }
                    else {
                        str = df.format(pCSDT.Utility.Round(i, grid_number_dp));
                    }
                    gl.glRasterPos3f(0,i-interval_y/20, 0);
                    glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10, str );
                }
            }
           
            
            
            
            /*
            for(float i = min_z; i <= max_z; i+=interval_z) {
                if (i != 0) {
                    if (grid_number_dp == 0) {
                        str = String.valueOf((long)Math.round(i));
                    }
                    else {
                        str = df.format(pCSDT.Utility.Round(i, grid_number_dp));
                    }
                    gl.glRasterPos3f(0, 0, i-interval_y/20);
                    glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10, str );
                }
            }
            */
            gl.glPopMatrix();
        }else{
            
        }
    }

    @Override
    public void Draw(GL gl, GLU glu, int frame, double dt) {
        gl.glLoadIdentity();
        TranslateAndScale(gl, glu, frame, dt);
        DrawBackground(gl, glu, frame, dt);
        DrawGrid(gl, glu, frame, dt);
    }
    
    @Override
    public void DrawSelected(GL gl, GLU glu, int frame, double dt) {}
    
    @Override
    public void DrawMouseOver(GL gl, GLU glu, int frame, double dt) {}

    @Override
    public final Matrix4x4 GetTransformMatrix()
    {
        return GetTransformMatrixOgl();
    }

    /**
     * Loads a pixel-real matrix, where the top-left corner corresponds
     * to (0, 0) and the point (cx, cy) corresponds to one past the lower-right
     */
    public void LoadPixelReal()
    {
        GL gl = parent.gljPanel.getGL();
        gl.glLoadIdentity();
        gl.glTranslatef(-1.0f, 1.0f, 0.0f);
        int cx = parent.gljPanel.getWidth();
        int cy = parent.gljPanel.getHeight();
        gl.glScalef(
            2.0f / cx,
            -2.0f / cy,
            1.0f
        );
    }

    /**
     * Specialized OpenGL transformation matrix function
     * @return The corresponding transformation matrix
     */
    public OglMatrix4x4 GetTransformMatrixOgl()
    {
	OglMatrix4x4 ret = new OglMatrix4x4();
        Dimension d = GetGui().getCanvasSize();
        ret.Translate(translate_x, translate_y, 0).ScaleX(scale/d.width*2).ScaleY(scale/d.height*2).ScaleZ(1);
        return ret;
    }

    /**
     * Set Background image of this engine
     * @param bRelative whether the given fileName is absolute or relative
     * @param fileName the name of background image file
     */
    /*
    @Override
    public void SetBackgroundImage(boolean bRelative, String fileName) {
        super.SetBackgroundImage(bRelative, fileName);
        parent.SetPEngineTextureBeUpdated(true);
    }
     * 
     */

    public void OnLoadTextures(GL gl, GLU glu)
    {
        if (!m_advGraphics) {
            bgTexture = null;
            return;
        }
        try
	{
            if (bgImg != null && !bgImg.binStr.equals("")) {
                // load from bgImg
                bgTexture = TextureIO.newTexture(bgImg.GetBufferedImage(), true);
                bgTexture.setTexParameterf(GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
                bgTexture.setTexParameterf(GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
            }
            else if (bgImgPath != null && !bgImgPath.equals("")) {
                // load from bgImgPath
                InputStream i = getClass().getResourceAsStream(bgImgPath);
                bgTexture = TextureIO.newTexture(ImageIO.read(i), true);
                bgTexture.setTexParameterf(GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
                bgTexture.setTexParameterf(GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
            }
            else {
                // clear texture
                bgTexture = null;
            }
        }
        catch(Exception e)
	{
            e.printStackTrace();
        }
    }

    /**
     * Attempts to recover an object based on its specified screen coordinates
     * @param x The x-coordinate, in logical screen coordinates
     * @param y The y-coordinate, in logical screen coordinates
     * @return The object at the specified screen coordinates
     * Note that, by default, the GUI class passes an x and y pixel offset from the top-left
     * corner of the canvas.
     */
    public PObject Pick(double x, double y)
    {
        Vector<PObject> pickedObjs = PickAll(x, y);

        if (pickedObjs.size() > 0) {
            ////Collections.sort(pickedObjs);
            ////return pickedObjs.get(pickedObjs.size()-1);
            ////return pickedObjs.get(0);
            // randomly select an object as the selected object
            int idx = (int)(Math.random()*pickedObjs.size());
            return pickedObjs.get(idx);
        }

        Matrix4x4 A = GetTransformMatrix();
        Matrix4x4 a = A.Dup().Invert();

        // We need to determine the position of the mouse relative
        // to the camera.
        Vector3 c = A.Get3x3().ZSpace();
        Vector3 v = a.Mul(x, y);

        switch(HitTest(v, c))
        {
        case Hit:
            return this;
        }
        return null;
    }


    /**
     * Attempts to recover an object based on its specified screen coordinates
     * A side effect is to set the isMosueOver status of the PObjects
     * @param x The x-coordinate, in logical screen coordinates
     * @param y The y-coordinate, in logical screen coordinates
     * @return A Vector containing object at the specified screen coordinates
     * Note that, by default, the GUI class passes an x and y pixel offset from the top-left
     * corner of the canvas.
     */
    public Vector<PObject> PickAll(double x, double y)
    {
        Matrix4x4 A = GetTransformMatrix();
        Matrix4x4 a = A.Dup().Invert();

        // We need to determine the position of the mouse relative
        // to the camera.
        Vector3 c = A.Get3x3().ZSpace();
        Vector3 v = a.Mul(x, y);

        Vector<PObject> pickedObjs = new Vector<PObject>(0);

        int s = objs.size();
        for(int i = s; i-- != 0;)
        {
            PObject obj = objs.elementAt(i);
            Vector3 pos = new Vector3(v);
            pos.Sub(obj.GetPosition3());

            HTResult rs = obj.HitTest(pos, c);
            switch(rs)
            {
                case Miss:
                    obj.SetIsMouseOver(false);
                    break;
                case Hit:
                    //return obj;
                    obj.SetIsMouseOver(true);
                    pickedObjs.add(obj);
                    break;
            }
        }

        return pickedObjs;
    }

    /**
     * This method is for subclass to override, defining how the graphic effect
     * should be removed.
     */
    @Override
    public void ClearDrawing()
    {
    }

    @Override
    public String IsInputValid(String str, String propName) {
        String superReturn = super.IsInputValid(str, propName);
        if (superReturn != null) {
            return superReturn;
        }
        else if (propName.equals("scale")
                || propName.equals("x range") || propName.equals("y range")
                || propName.equals("x interval") || propName.equals("y interval")) {
            float f = Float.parseFloat(str);
            if (f<=0) {
                return "The value should be an integer bigger than 0.";
            }
        }
        return null;
    }
}



