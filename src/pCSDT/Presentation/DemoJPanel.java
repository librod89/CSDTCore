/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Presentation;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import java.math.*;
import javax.swing.Icon;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import org.jdom.JDOMException;
import pCSDT.Presentation.GUI.*;
import pCSDT.Scripting.PEngine;
import pCSDT.Utility;

/**
 *
 * @author Bill
 */

public class DemoJPanel extends JPanel implements HyperlinkListener, ActionListener {

    //JFrame frame;
    ImageIcon icon;
    ImageIcon img;
    JButton two;
    ImageIcon[] images;
    GUI gui;
    String[] nameStrings;
    String[] srcStrings;
    String[] htmlStrings;
    String[] tooltStrings;
    String[] xmlStrings;
    JEditorPane editpane;
     ArrayList<demoProperties> gp = new ArrayList<demoProperties>();
      static JFrame frame = new JFrame("Demo");

    public void hyperlinkUpdate(HyperlinkEvent he) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


   class demoProperties {

       private String name_source;
       private String image_source;
       private String html_source;
       private String toolt_source;
       private String xml_source;

       demoProperties( String a, String b, String c, String d, String e){

           name_source = a;
           image_source = b;
           html_source = c;
           toolt_source = d;
           xml_source = e;
       }
       public String getName_Source(){
           return name_source;
       }
       public String getToolT_Source(){
           return toolt_source;
       }

       public String getImage_Source(){
           return image_source;
       }
       public String getHtml_Source(){
           return html_source;

       }
       public String getXml_Source(){
           return xml_source;
       }
       public void gp_add( String a, String b, String c, String d, String e ){
           name_source = a;
           image_source = b;
           html_source = c;
           toolt_source = d;
           xml_source = e;
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
    public DemoJPanel(GUI gui, String conf_file) throws IOException {
            super(new BorderLayout());

            this.gui = gui;
            load_gp(conf_file);
            JTabbedPane tabPane;
            tabPane = new JTabbedPane();
            tabPane.setPreferredSize(new Dimension(600,400));
            
            

            int j = 0;
            for (final demoProperties gps : gp){
                j++;
                editpane = createEditorPane(gps.getHtml_Source());
             
                JButton loadxmlbt = new JButton();
                JLabel newLabel = new JLabel();
                newLabel.setText("Load Demo File");
                loadxmlbt.add(newLabel, BorderLayout.EAST);
                loadxmlbt.setSize(new Dimension(3,50));
                loadxmlbt.setAlignmentX(LEFT_ALIGNMENT);
                loadxmlbt.addActionListener( new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent ae){
                    try {
                        loadAndShowXML(gps.getXml_Source());
                    } catch (IOException ex) {
                        Logger.getLogger(DemoJPanel.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (JDOMException ex) {
                        Logger.getLogger(DemoJPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                            
                            
                      
                    }
                });
               
                
              
                
                JPanel panel1 = new JPanel();

                panel1.setLayout(new BoxLayout(panel1, BoxLayout.PAGE_AXIS));
                panel1.add(loadxmlbt);
                editpane.setAlignmentX(LEFT_ALIGNMENT);
                panel1.add( editpane);
                
                JScrollPane scroller = new JScrollPane(panel1);

            
                tabPane.add("Tutorial "+(j), scroller);
                add( tabPane );
          
        }
            
    }
    
    protected void loadAndShowXML(String path) throws IOException, JDOMException {
       
        //java.net.URL imgURL = this.gui.getClass().getResource(path);
       
           //     this.gui.GetEngine().LoadXml(imgURL);
               // this.gui.LoadFromXml(imgURL);
               this.gui.ClearEngine();
              
               //if(path != null){
                 try {
                if (path.startsWith("http://")) {
                    this.gui.LoadFromXml(Utility.FormatURL(path));
                }
                else {
                    this.gui.LoadFromXml(gui.getClass().getResource(path));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
                  this.gui.GetEngine().DeferredInitialize();
              // }

              
    }
    
        
        
    
     

   
        
         
        
    


    /** Returns an ImageIcon, or null if the path was invalid. */
    protected ImageIcon createImageIcon(String path) {
        //System.out.println("Path: " + path);
        java.net.URL imgURL = this.gui.getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
                return null;
        }
    }

    protected final JEditorPane createEditorPane(String path) throws IOException {
        JTextPane editorPane = new JTextPane();
       
         editorPane.setEditable(false);
         editorPane.addHyperlinkListener(this);
         editorPane.setAutoscrolls(true);
                editorPane.setAlignmentX(TOP_ALIGNMENT);
                editorPane.setAlignmentY(LEFT_ALIGNMENT);
              //  editorPane.setSize(300, 300);


        java.net.URL textURL = this.gui.getClass().getResource(path);
        if (textURL != null) {
                editorPane.setPage(textURL);
                return new JEditorPane(textURL);

        } else {
            System.err.println("Couldn't find file: " + path);
                return null;
        }
        
        
    }
    

     private void load_gp(String conf_file){
            System.out.println("conf_file " + conf_file);
            int imgNum = 0;
            Properties prop = new Properties();
            if (conf_file != null) {
            
            InputStream obj = null;

            try {
                obj = gui.getClass().getResourceAsStream(conf_file);
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
        }
            nameStrings = new String[imgNum];
            srcStrings = new String[imgNum];
            htmlStrings = new String[imgNum];
            tooltStrings = new String[imgNum];
            xmlStrings = new String[imgNum];

            NumberFormat digit3 = new DecimalFormat("000");
            int i = 0;
            while(i < imgNum){
                String imgCodeNum = "Img" + digit3.format(i);

                nameStrings[i] = prop.getProperty(imgCodeNum + ".name");
                srcStrings[i] = prop.getProperty(imgCodeNum + ".loc");
                htmlStrings[i] = prop.getProperty(imgCodeNum + ".html");
                tooltStrings[i] = prop.getProperty(imgCodeNum + ".toolt");
                xmlStrings[i] = prop.getProperty(imgCodeNum + ".xml");

                demoProperties gps = new demoProperties(nameStrings[i], srcStrings[i], htmlStrings[i], tooltStrings[i], xmlStrings[i]);
                gp.add (gps);
                
                System.out.println(xmlStrings[i]);        
            i++;
            }
            /*
            for (demoProperties gps : gp){
                
        
            
        }
             * 
             */

    }

    public void createAndShowDemoImages(String image, String html) throws IOException {

        ImageIcon icon;
        JLabel label;
        icon = createImageIcon( image );

        label = new JLabel( icon );
        //label.setSize(350, 350);
        JPanel panel1 = null, panel2 = null;
        panel1 = new JPanel();
        panel2 = new JPanel();
        
        JEditorPane editpane;
        editpane = new JEditorPane();
       
        editpane = createEditorPane( html );
        
        JTabbedPane tabPane;
        
        tabPane = new JTabbedPane();
        
        panel2.add(label, BorderLayout.CENTER );
        tabPane.add("Demo Image", panel2);
        
        
        
       // tabPane.setPreferredSize(new Dimension(300,300));
        //editpane.setSize(5,20);
        editpane.setEditable(false);
        editpane.setAutoscrolls(true);
        editpane.setAlignmentX(TOP_ALIGNMENT);
        editpane.setAlignmentY(LEFT_ALIGNMENT);
        //editpane.setSize(5, 20);
        panel1.add( editpane);
        JScrollPane scroller = new JScrollPane(panel1);
        
        tabPane.add("Information", scroller);
        //Create and set up the window.
        JFrame frame1 = new JFrame("Demo");
        //frame.setDefaultCloseOperation(this.EXIT_ON_CLOSE);
        frame1.setLayout( new BorderLayout() );
        frame1.add( tabPane );
        //frame1.setSize(350, 350);
        


        //Create and set up the content pane.
        

        //Display the window.
        frame1.pack();
        frame1.setVisible(true);
        frame.setVisible(false);
       

    }
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    public static void createAndShowGUI(GUI gui, String conf_file) throws IOException {
        //Create and set up the window.
        
        //frame.setDefaultCloseOperation(this.EXIT_ON_CLOSE);

        
        //Create and set up the content pane.
        JComponent newContentPane = new DemoJPanel(gui, conf_file);
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

   

   

      
   
}


