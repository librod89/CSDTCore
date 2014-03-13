/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

package pCSDT.Presentation;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.text.*;
import javax.swing.*;
import pCSDT.Utility;

public final class GraphicsChooser extends JPanel implements ActionListener {
    ImageIcon[] images;
    GUI gui;
    String[] nameStrings;
    String[] srcStrings;
    String[] descStrings;

    JPanel usePlainColorPanel;
    JPanel useImagePanel;
    JComboBox itemList;

    class FileChooserActionListener implements ActionListener {
        JComponent parent;

        public FileChooserActionListener(JComponent parent) {
            this.parent = parent;
        }
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();
            javax.swing.filechooser.FileFilter filter = new javax.swing.filechooser.FileFilter() {
                public boolean accept(File pathname) {
                    // allow directory to be seen even with filtering
                    if (pathname.isDirectory()) {
                        return true;
                    }
                    return getExtension(pathname).compareTo("png") == 0;
                }

                public String getDescription() {
                    return ".png";
                }

                public String getExtension(File f)
                {
                    String name = f.getName();
                    int i = name.lastIndexOf(".");
                    if(i < 0)
                        return "";
                    return name.substring(i + 1).toLowerCase();
                }
            };
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(parent);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                gui.GetEngine().SetBackgroundColor("255,255,255");
                
                gui.GetEngine().SetBackgroundImage(false,
                        chooser.getSelectedFile().getAbsolutePath());
            }
        }
    }
    /*
     * Despite its use of EmptyBorder, this panel makes a fine content
     * pane because the empty border just increases the panel's size
     * and is "painted" on top of the panel's normal background.  In
     * other words, the JPanel fills its entire background if it's
     * opaque (which it is by default); adding a border doesn't change
     * that.
     */
    public GraphicsChooser(final GUI gui, String conf_file) {

        super(new BorderLayout());

        this.gui = gui;

        // set up Panels
        //usePlainColorPanel = new JPanel(new GridBagLayout());
        useImagePanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5,5,5,5);
        c.fill = GridBagConstraints.HORIZONTAL;
        /*
        JButton clearImgButton = new JButton("Default");
        clearImgButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gui.GetEngine().SetBackgroundColor(gui.GetEngine().defaultBgColor);
                gui.GetEngine().SetBackgroundImage(true, null);
            }
        });
         * 
         */
        /*
        JButton selectColorButton = new JButton("Select");
        selectColorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color br = JColorChooser.showDialog(
                            ((Component)e.getSource()).getParent(),
                            "Pick background color", Color.blue);
                if(br != null)
                {
                    gui.GetEngine().SetBackgroundImage(true, null);
                    gui.GetEngine().SetBackgroundColor(
                            br.getRed()+","+br.getGreen()+","+br.getBlue());
                }
            }
        });
         * 
         */
        /*
        usePlainColorPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Plain Color"));
        usePlainColorPanel.add(clearImgButton, c);
        c.gridy++;
        usePlainColorPanel.add(selectColorButton, c);

        JButton clearButton = new JButton("Default");
        /*
        clearButton.setEnabled(gui.GetEngine().m_advGraphics);
        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gui.GetEngine().SetBackgroundImage(true, 
                        gui.GetEngine().defaultBgFile);
                gui.GetEngine().SetBackgroundColor("255,255,255");
        
            }
        });
        */
        JButton fileSelectButton = new JButton("Select from local files");
        
        //fileSelectButton.setEnabled(gui.GetEngine().m_advGraphics);
        fileSelectButton.addActionListener(new FileChooserActionListener(this));
        c.gridy = 0;
        useImagePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Image"));
        //useImagePanel.add(clearButton, c);
        c.gridy++;
        useImagePanel.add(fileSelectButton, c);

        // set up the itemList
        
        int imgNum = 0;
        Integer[] intArray = new Integer[0];

        // if conf_file is null, do nothing
        if (conf_file != null) {
            Properties prop = new Properties();
            InputStream obj = null;

            try {
                if (conf_file.startsWith("http://")) {
                    obj = Utility.FormatURL(conf_file).openStream();
                }
                else {
                    obj = gui.getClass().getResourceAsStream(conf_file);
                }
                if (obj != null) {
                    obj = new BufferedInputStream(obj);
                    prop.load((InputStream)obj);
                }
                else {
                    System.err.println(conf_file + ": file not found.");
                }
                imgNum = Integer.parseInt(prop.getProperty("number"));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
           
            nameStrings = new String[imgNum];
            srcStrings = new String[imgNum];
            descStrings = new String[imgNum];
            NumberFormat digit3 = new DecimalFormat("000");
            for (int i = 0; i < imgNum; i++) {
                String imgCodeNum = "Icon" + digit3.format(i);
                nameStrings[i] = prop.getProperty(imgCodeNum + ".name");
                srcStrings[i] = prop.getProperty(imgCodeNum + ".loc");
                descStrings[i] = prop.getProperty(imgCodeNum + ".desc");
            }
        
            //Load the pet images and create an array of indexes.
            images = new ImageIcon[nameStrings.length];
            intArray = new Integer[nameStrings.length];
            for (int i = 0; i < nameStrings.length; i++) {
                intArray[i] = new Integer(i);
                images[i] = createImageIcon(srcStrings[i]);
            }
        }

        //Create the combo box.
        itemList = new JComboBox(intArray);
        //itemList.setEnabled(gui.GetEngine().m_advGraphics && intArray.length>0);
        ComboBoxRenderer renderer= new ComboBoxRenderer();
        renderer.setPreferredSize(new Dimension(160, 120));
        renderer.setLocation(50, 50);
        itemList.setSelectedIndex(-1);
        itemList.setRenderer(renderer);
        itemList.setMaximumRowCount(3);

        // add listener
        itemList.addActionListener(this);
        c.gridy++;
        useImagePanel.add(itemList, c);

        //Lay out the demo.
        //add(usePlainColorPanel, BorderLayout.NORTH);
        add(useImagePanel, BorderLayout.CENTER);
        
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
    }

    /** Returns an ImageIcon, or null if the path was invalid. */
    protected ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = null;
        if (!path.startsWith("http://")) {
            imgURL = this.gui.getClass().getResource(path);
        }
        else {
            try {
                imgURL = Utility.FormatURL(path);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (imgURL != null) {
            ////return new ImageIcon(imgURL);
            ImageIcon tmpIcon = new ImageIcon(imgURL);
            return new ImageIcon(tmpIcon.getImage().getScaledInstance(160, 120,
                    java.awt.Image.SCALE_SMOOTH));
        } else {
            System.err.println("Couldn't find file: " + path);
                return null;
        }
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    public static void createAndShowGUI(GUI gui, String conf_file) {
        //Create and set up the window.
        JFrame frame = new JFrame("Set Object Icon");
        //frame.setDefaultCloseOperation(this.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        GraphicsChooser newContentPane = new GraphicsChooser(gui, conf_file);
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        JComboBox cb = (JComboBox)e.getSource();
        int selectedIdx = cb.getSelectedIndex();
        gui.GetEngine().SetBackgroundColor("255,255,255");
        gui.GetEngine().SetBackgroundImage(
                !srcStrings[selectedIdx].startsWith("http://"),
                srcStrings[selectedIdx]);
    }

    class ComboBoxRenderer extends JPanel
                           implements ListCellRenderer {
        private Font uhOhFont;
        private JLabel label;

        public ComboBoxRenderer() {
            setOpaque(true);
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
            label = new JLabel();
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setVerticalAlignment(JLabel.CENTER);
            add(label, BorderLayout.CENTER);
        }

        /*
         * This method finds the image and text corresponding
         * to the selected value and returns the label, set up
         * to display the text and image.
         */
        public Component getListCellRendererComponent(
                                           JList list,
                                           Object value,
                                           int index,
                                           boolean isSelected,
                                           boolean cellHasFocus) {
            if (value == null) {
                return new JLabel("=== Select from templates ===");
            }

            //Get the selected index. (The index param isn't
            //always valid, so just use the value.)
            int selectedIndex = ((Integer)value).intValue();

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            //Set the icon and text.  If icon was null, say so.
            ImageIcon icon = images[selectedIndex];
            String name = nameStrings[selectedIndex];
            String desc = descStrings[selectedIndex];
            label.setIcon(icon);
            if (icon != null) {
                ////setText(pet);
                setToolTipText("<html><p><b>" + name + "</b></p><p>"
                        + desc + "</p></html>");
                label.setFont(list.getFont());
            } else {
                setUhOhText(name + " (no image available)",
                            list.getFont());
            }

            return this;
        }

        //Set the font and text when no image was found.
        protected void setUhOhText(String uhOhText, Font normalFont) {
            if (uhOhFont == null) { //lazily create this font
                uhOhFont = normalFont.deriveFont(Font.ITALIC);
            }
            label.setFont(uhOhFont);
            setToolTipText(uhOhText);
        }
    }
}
