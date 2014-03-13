/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting.SCElem;

import org.jdom.*;
import java.awt.*;
import java.awt.image.ImageObserver;
import javax.swing.*;
import java.util.TreeMap;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author Jason
 */
public class LayoutSingleElement implements ImageObserver {
    public Element srcElement;
    public Image sliceImg;
    public int m_imgCx;
    public int m_imgCy;

    public LayoutSingleElement(Image sliceImg)
    {
        this.sliceImg = sliceImg;
        m_imgCx = sliceImg.getWidth(this);
        m_imgCy = sliceImg.getHeight(this);
    }

    public static class SubRect
    {
        public Rectangle bounds;
        boolean hResize = false;
        boolean vResize = false;

        public SubRect(int x, int y, int cx, int cy)
        {
            bounds = new Rectangle(x, y, cx, cy);
        }

        public SubRect(Element e)
        {
            int x, y, cx, cy;
            x = Integer.parseInt(e.getAttribute("x").getValue());
            y = Integer.parseInt(e.getAttribute("y").getValue());
            cx = Integer.parseInt(e.getAttribute("cx").getValue());
            cy = Integer.parseInt(e.getAttribute("cy").getValue());
            bounds = new Rectangle(x, y, cx, cy);
        }
    }

    public static class SplitElement
    {
        public SplitElement(int aPos)
        {
            pos = aPos;
        }

        int index;
        int pos;
    }

    public static class GridEntry
    {
        public JComponent c;
        public boolean hResize;
        public boolean vResize;
        public boolean added = false;
        public int gridWidth = 1;
        public int gridHeight = 1;
    }

    public SubRect[] inputRects;
    public int[] hSplits;
    public int[] vSplits;

    @Override
    public boolean imageUpdate(Image img, int infoFlags, int x, int y, int width, int height)
    {
        return true;
    }

    public void MakeSubElements(JComponent parent, GridBagLayout layout, JPnlSingleChild[] inputs)
    {
        TreeMap<Integer, SplitElement> x = new TreeMap<Integer, SplitElement>();
        TreeMap<Integer, SplitElement> y = new TreeMap<Integer, SplitElement>();

        // Identify the mandatory divisions:
        for(SubRect r : inputRects)
        {
            x.put(r.bounds.x, new SplitElement(r.bounds.x));
            x.put(r.bounds.x + r.bounds.width, new SplitElement(r.bounds.x + r.bounds.width));
            y.put(r.bounds.y, new SplitElement(r.bounds.y));
            y.put(r.bounds.y + r.bounds.height, new SplitElement(r.bounds.y + r.bounds.height));
        }

        // Need to set up the absolute boundaries of the entire field:
        if(!x.containsKey(0))
            x.put(0, new SplitElement(0));
        if(!y.containsKey(0))
            y.put(0, new SplitElement(0));
        if(!x.containsKey(m_imgCx))
            x.put(m_imgCx, new SplitElement(m_imgCx));
        if(!y.containsKey(m_imgCy))
            y.put(m_imgCy, new SplitElement(m_imgCy));

        // Determine which rectangles need to be split, and in what directions, and add the
        // splitters into the x and y split sets:
        for(SubRect sr : inputRects)
        {
            for(int vSplit : vSplits)
            {
                /*Integer i = new Integer(vSplit);
                if(x.containsKey(new Integer(i)))
                    x.put(i, new SplitElement(vSplit));*/
                if(sr.bounds.x < vSplit && vSplit < sr.bounds.x + sr.bounds.width)
                    sr.hResize = true;
            }
            for(int hSplit : hSplits)
            {
                /*Integer i = new Integer(hSplit);
                if(y.containsKey(new Integer(i)))
                    y.put(i, new SplitElement(hSplit));*/
                if(sr.bounds.y < hSplit && hSplit < sr.bounds.y + sr.bounds.height)
                    sr.vResize = true;
            }
        }

        // Iterate through, setting up indices:
        SplitElement[] xElements = new SplitElement[x.size()];
        SplitElement[] yElements = new SplitElement[y.size()];
        int cx = x.size() - 1;
        int cy = y.size() - 1;
        GridEntry[][] ctrls = new GridEntry[cx][cy];
        for(int i = 0; i < cx; i++)
            for(int j = 0; j < cy; j++)
                ctrls[i][j] = new GridEntry();

        {
            int n;
            Iterator<SplitElement> i;

            n = 0;
            i = x.values().iterator();
            while(i.hasNext())
            {
                SplitElement cur = i.next();
                xElements[n] = cur;
                cur.index = n++;
            }

            n = 0;
            i = y.values().iterator();
            while(i.hasNext())
            {
                SplitElement cur = i.next();
                yElements[n] = cur;
                cur.index = n++;
            }
        }

        // Set up the controls:
        for(int i = 0; i < inputRects.length; i++)
        {
            SubRect sr = inputRects[i];
            SplitElement xe = x.get(sr.bounds.x);
            SplitElement ye = y.get(sr.bounds.y);

            if(sr.hResize && !ctrls[xe.index][ye.index].hResize)
                for(int j = 0; j < cy; j++)
                    ctrls[xe.index][j].hResize = true;
            if(sr.vResize && !ctrls[xe.index][ye.index].vResize)
                for(int j = 0; j < cx; j++)
                    ctrls[j][ye.index].vResize = true;

            inputs[i] = new JPnlSingleChild();
            ctrls[xe.index][ye.index].c = inputs[i];
        }

        for(int i = 0; i < cx; i++)
            for(int j = 0; j < cy; j++)
            {
                if(ctrls[i][j].c != null)
                    continue;

                JPictureBox box  = new JPictureBox(
                    sliceImg,
                    xElements[i].pos,
                    yElements[j].pos,
                    xElements[i + 1].pos - xElements[i].pos,
                    yElements[j + 1].pos - yElements[j].pos
                );
                ctrls[i][j].c = box;
            }

        GridBagConstraints c = new GridBagConstraints(
            0, 0, 1, 1,
            1, 1,
            GridBagConstraints.CENTER,
            GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0
        );

        c.fill = GridBagConstraints.NONE;
        for(int i = 0; i < cx; i++)
        {
            c.gridx = i;
            for(int j = 0; j < cy; j++)
            {
                c.gridy = j;
                GridEntry cur = ctrls[i][j];
                if(cur.added)
                    continue;
                cur.added = true;

                if(false) if(cur.hResize)
                    c.fill = cur.vResize?GridBagConstraints.BOTH:GridBagConstraints.HORIZONTAL;
                else
                    c.fill = cur.vResize?GridBagConstraints.VERTICAL:GridBagConstraints.NONE;

                if(cur.hResize && cur.vResize)
                    c.fill = GridBagConstraints.BOTH;
                else
                    c.fill = GridBagConstraints.NONE;

                c.gridwidth = cur.gridWidth;
                c.gridheight = cur.gridHeight;
                c.weightx = cur.hResize?1:0;
                c.weighty = cur.vResize?1:0;

                parent.add(cur.c, c);
            }
        }
    }
}











