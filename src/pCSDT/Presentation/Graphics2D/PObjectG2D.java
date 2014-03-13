package pCSDT.Presentation.Graphics2D;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import pCSDT.Scripting.*;
import java.util.*;
import javax.swing.JPanel;

public abstract class PObjectG2D extends PObject implements OpenG2Drawable {
    public PObjectG2D(String name, String desc)
    {
        super(name, desc);
    }

    /**
     * (Re)loads all textures associated with this object
     * @param canvas the Graphics2D object
     */
    public void OnLoadTextures() {}

    public Vector<Vector3> GetPolyBound()
    {
        return new Vector<Vector3>(0);
    }

    @Override
    public void DrawSelected(JPanel panel, Graphics2D canvas, int frame, double dt)
    {
	// Draw the baseline first:
	Draw(panel, canvas, frame, dt);

	// Get the bounding box:
	canvas.setColor(new java.awt.Color(0,0,0));
        Vector<Vector3> vs = GetPolyBound();
        int size = vs.size();
        int[] xs = new int[size];
        int[] ys = new int[size];
        int i = 0;
        Iterator<Vector3> iterVector3 = GetPolyBound().iterator();
        while (iterVector3.hasNext()) {
            Vector3 v = iterVector3.next();
            xs[i] = (int)Math.round(v.x());
            ys[i] = (int)Math.round(v.y());
            i++;
        }
        canvas.drawPolygon(xs, ys, size);
    }

    @Override
    public void DrawMouseOver(JPanel panel, Graphics2D canvas, int frame, double dt)
    {
	// Draw the baseline first:
	Draw(panel, canvas, frame, dt);

	// Get the bounding box:
	canvas.setColor(new java.awt.Color(0,0,0));
        Vector<Vector3> vs = GetPolyBound();
        int size = vs.size();
        int[] xs = new int[size];
        int[] ys = new int[size];
        int i = 0;
        Iterator<Vector3> iterVector3 = GetPolyBound().iterator();
        while (iterVector3.hasNext()) {
            Vector3 v = iterVector3.next();
            xs[i] = (int)Math.round(v.x());
            ys[i] = (int)Math.round(v.y());
            i++;
        }
        float[] dashPattern = { 10, 10};
        canvas.setStroke(new BasicStroke(8, BasicStroke.CAP_BUTT,
                                  BasicStroke.JOIN_MITER, 10,
                                  dashPattern, 0));
        canvas.drawPolygon(xs, ys, size);
        canvas.setStroke(new BasicStroke());
    }
}


