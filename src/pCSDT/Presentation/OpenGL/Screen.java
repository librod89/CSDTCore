/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Presentation.OpenGL;

import java.awt.Graphics;
import pCSDT.Presentation.*;
import javax.swing.JFrame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 *
 * @author Jason
 */
public class Screen extends JFrame implements frameListener, WindowListener {
    private PEngineOgl eng;
    private GLScene scene;

    public Screen(PEngineOgl eng) {
        super();
        this.eng = eng;
        scene = new GLScene(eng);
        
        add(scene.glCanvas);
        setSize(600, 600);
        setVisible(true);
        scene.addFrameListener(this);
        
        // Make sure we clean up when we're done.
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(this);
    }

    public void startPlaying(boolean loop)
    {
        scene.startPlaying(0, -1, loop);
    }
    
    public void stopPlaying()
    {
        scene.endPlaying();
    }
    
    @Override
    public void frameChanged(int frame)
    {
    }
    
    // Window listener overrides, ignored for the most part:
    @Override
    public void windowActivated(WindowEvent e) {} 
    @Override
    public void windowClosed(WindowEvent e) {}
    @Override
    public void windowClosing(WindowEvent e)
    {
        removeWindowListener(this);
        scene.doCleanup();
    }
    @Override
    public void windowDeactivated(WindowEvent e) {}
    @Override
    public void windowDeiconified(WindowEvent e) {} 
    @Override
    public void windowIconified(WindowEvent e) {} 
    @Override
    public void windowOpened(WindowEvent e) {}
}
