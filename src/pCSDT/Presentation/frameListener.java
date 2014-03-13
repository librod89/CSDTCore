package pCSDT.Presentation;

/**
 *
 * @author hilmi
 * 
 * This interface is a listener for the changes on frames.
 * When the animation passes to the next frame, an event is 
 * fired so that the listener classes can update their state. (e.g. sliders)
 */
public interface frameListener {
    public void frameChanged(int frame);
}
