/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting.SCElem;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Graphics;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 *
 * @author Jason
 */
public class JPictureBox extends JPanel {
    Image m_src;
    int m_x;
    int m_y;
    int m_cx;
    int m_cy;

    public JPictureBox(Image src, int x, int y, int cx, int cy)
    {
        m_src = src;

        m_x = x;
        m_y = y;
        m_cx = cx;
        m_cy = cy;
    }

    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(m_cx, m_cy);
    }

    @Override
    public void paint(Graphics g)
    {
        g.drawImage(
            m_src,
            0, 0, getWidth(), getHeight(),
            m_x, m_y, m_x + m_cx, m_y + m_cy,
            this
        );
        g.setColor(Color.WHITE);
        g.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2);
        g.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight());
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        g.setColor(Color.RED);
        g.fillRect(getWidth() / 2 - 5, getHeight() / 2 - 5, 10, 10);
    }
}
