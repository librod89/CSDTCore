package pCSDT.Presentation.Graphics2D;

import pCSDT.Scripting.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import pCSDT.Presentation.GUI.eRenderState;

public abstract class PEngineG2D extends PEngine implements OpenG2Drawable {

    // originally, the display canva has (0,0) at top left corner
    // and positive x to the right and positive y to the bottom
    // first translate origin by ...
    public float translate_x = -1, translate_y = -1;
    
    // second scale by ...
    @AutomatableProperty(name="scale", DisplayName="scale (pixels per unit)", desc="scale", DesignTimeBehavior="A", RunTimeBehavior="H")
    public float scale = 400f/650;  // pixel per inc

    @AutomatableProperty(name="x range", DisplayName="Preferred x range", desc="x range", DesignTimeBehavior="A", RunTimeBehavior="H")
    public float range_x = 650;

    @AutomatableProperty(name="y range", DisplayName="Preferred y range", desc="y range", DesignTimeBehavior="A", RunTimeBehavior="H")
    public float range_y = 650;

    protected boolean bDrawGridBeyondRange = true;

    // interval between tick marks
    @AutomatableProperty(name="x interval", desc="x interval", DesignTimeBehavior="A", RunTimeBehavior="H")
    public float interval_x = 1;
    
    @AutomatableProperty(name="y interval", desc="y interval", DesignTimeBehavior="A", RunTimeBehavior="H")
    public float interval_y = 1;

    protected GUIG2D parent;
    BufferedImage bgTexture = null;  // bg image of this G2D Engine

    public PEngineG2D(Class[] objTypes)
    {
        super("","",objTypes);
    }

    public PEngineG2D(String name, String desc, Class[] objTypes) {
        super(name, desc, objTypes);
        
     
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
    public void Draw(JPanel panel, Graphics2D canvas, int frame, double dt) {
        TranslateAndScale(panel, canvas, frame, dt);
        DrawBackground(panel, canvas, frame, dt);
        DrawGrid(panel, canvas, frame, dt);
    }

    @Override
    public Matrix4x4 GetTransformMatrix()
    {
        Matrix4x4 ret = new Matrix4x4();
        Dimension d = GetGui().getCanvasSize();
        ret.Translate(translate_x, translate_y, 0).ScaleX(scale/d.width*2).ScaleY(scale/d.height*2).ScaleZ(1);
        return ret;
    }

    public void OnLoadTextures() {
        if (!m_advGraphics) {
            bgTexture = null;
            return;
        }
        try {
            if (bgImg != null && !bgImg.binStr.equals("")) {
                bgTexture = bgImg.GetBufferedImage();
            }
            else if (bgImgPath != null && !bgImgPath.equals("")) {
                InputStream i = getClass().getResourceAsStream(bgImgPath);
                bgTexture = ImageIO.read(i);
            }
            else {
                bgTexture = null;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void TranslateAndScale(JPanel panel, Graphics2D canvas, int frame, double dt) {
        Dimension d = m_gui.getCanvasSize();
        float cmin_x = -1; float cmax_x = 1;
        cmin_x = cmin_x - translate_x;
        cmax_x = cmax_x - translate_x;
        if (bDrawGridBeyondRange) {
            cmin_x = cmin_x*d.width/2; cmax_x = cmax_x*d.width/2;
        }
        else {
            cmin_x *= range_x/2f;
            cmax_x *= range_x/2f;
        }
        
        float cmin_y = -1; float cmax_y = 1;
        cmin_y = cmin_y - translate_y;
        cmax_y = cmax_y - translate_y;
        if (bDrawGridBeyondRange) {
            cmin_y = cmin_y*d.height/2; cmax_y = cmax_y*d.height/2;
        }
        else {
            cmin_y *= range_y/2f;
            cmax_y *= range_y/2f;
        }
        canvas.translate(-cmin_x, -cmin_y);
        canvas.scale(scale, scale);
    }

    protected void DrawBackground(JPanel panel, Graphics2D canvas, int frame, double dt) {
        StringTokenizer st = new StringTokenizer(bg_color, ",");
        float bg_r = Integer.parseInt(st.nextToken().trim())/255f;
        float bg_g = Integer.parseInt(st.nextToken().trim())/255f;
        float bg_b = Integer.parseInt(st.nextToken().trim())/255f;
        canvas.setBackground(new Color(bg_r, bg_g, bg_b));
        panel.setBackground(new Color(bg_r, bg_g, bg_b));
        
        if (m_advGraphics && bgTexture != null) {
            canvas.drawImage(bgTexture, Math.round(textStartx), Math.round(textStarty), Math.round(textLength), Math.round(textHeight), null);
        }
    }

    protected void DrawGrid(JPanel panel, Graphics2D canvas, int frame, double dt) {
        if (bDrawGrid && m_gui.GetRenderState() != eRenderState.Animating) {
            StringTokenizer st = new StringTokenizer(grid_color, ",");
            float grid_r = Integer.parseInt(st.nextToken().trim())/255f;
            float grid_g = Integer.parseInt(st.nextToken().trim())/255f;
            float grid_b = Integer.parseInt(st.nextToken().trim())/255f;
            st = new StringTokenizer(grid_number_color, ",");
            float grid_num_r = Integer.parseInt(st.nextToken().trim())/255f;
            float grid_num_g = Integer.parseInt(st.nextToken().trim())/255f;
            float grid_num_b = Integer.parseInt(st.nextToken().trim())/255f;

            Dimension d = m_gui.getCanvasSize();
            float cmin_x = -1; float cmax_x = 1;
            cmin_x = cmin_x - translate_x;
            cmax_x = cmax_x - translate_x;
            if (bDrawGridBeyondRange) {
                float scaleFactor = d.width/2/scale;
                cmin_x = cmin_x*scaleFactor; cmax_x = cmax_x*scaleFactor;
            }
            else {
                float rmin = cmin_x; float rmax = cmax_x;
                float scaleFactor = range_x/(rmax-rmin);
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
            canvas.setColor(new Color(grid_r, grid_g, grid_b));
            for (float i = umin_x; i <= umax_x; i+= interval_x) {
                // Draw x mark
                canvas.drawLine(Math.round(i), Math.round(cmin_y), Math.round(i), Math.round(cmax_y));
            }
            for (float i = umin_y; i <= umax_y; i+= interval_y) {
                // Draw y mark
                canvas.drawLine(Math.round(cmin_x), Math.round(i), Math.round(cmax_x), Math.round(i));
            }
            String str = null;
            StringBuilder sb = new StringBuilder();
            sb.append("#.");
            for (int i = 0; i < grid_number_dp; i++) {
                sb.append("0");
            }
            DecimalFormat df = new DecimalFormat(sb.toString());
            canvas.setColor(new Color(grid_num_r, grid_num_g, grid_num_b));
            canvas.setFont(new Font("Helvetica", Font.PLAIN, 10));
            // Draw the numbers
            for (float i = umin_x; i <= umax_x; i+= interval_x) {
                if (grid_number_dp == 0) {
                    str = String.valueOf((long)Math.round(i));
                }
                else {
                    str = df.format(pCSDT.Utility.Round(i, grid_number_dp));
                }
                canvas.drawString(str, i-interval_x/10, interval_y/10);
            }
            for (float i = umin_y; i <= umax_y; i+= interval_y) {
                if (grid_number_dp == 0) {
                    str = String.valueOf((long)Math.round(i));
                }
                else {
                    str = df.format(pCSDT.Utility.Round(i, grid_number_dp));
                }
                canvas.drawString(str, 0, i+interval_x/10);
            }
        }
    }

    @Override
    public void DrawSelected(JPanel panel, Graphics2D canvas, int frame, double dt) {}

    @Override
    public void DrawMouseOver(JPanel panel, Graphics2D canvas, int frame, double dt) {}

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
            // randomly select an object as the selected object
            int idx = (int)(Math.random()*pickedObjs.size());
            return pickedObjs.get(idx);
        }

        Matrix4x4 A = GetTransformMatrix();
        Matrix4x4 a = A.Dup().Invert();

        // We need to determine the position of the mouse relative
        // to the camera.
        Vector3 c = A.Get3x3().ZSpace();
        Vector3 v = new Vector3(x, y);

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
        Vector3 v = new Vector3(x, y);

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



