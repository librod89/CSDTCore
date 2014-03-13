package pCSDT.Presentation.Graphics2D;
import java.awt.Graphics2D;
import javax.swing.JPanel;

public interface OpenG2Drawable {
    /**
     * Prepares the canvas for drawing
     * 
     * @param frame Current frame index
     * @param dt Time step since last frame
     * Potentially, the time step could be zero.  If it is, this indicates
     * a stationary frame where no animation is taking place.
     */
    public void Draw(JPanel panel, Graphics2D canvas, int frame, double dt);

    /**
     * Draw selected override
     *
     * @param frame Current frame index
     * @param dt Time step since last frame
     * Potentially, the time step could be zero.  If it is, this indicates
     * a stationary frame where no animation is taking place.
     */
    public void DrawSelected(JPanel panel, Graphics2D canvas, int frame, double dt);

    /**
     * Draw mouse over override
     * @param canvas
     * @param frame Current frame index
     * @param dt Time step since last frame
     */
    public void DrawMouseOver(JPanel panel, Graphics2D canvas, int frame, double dt);
}
