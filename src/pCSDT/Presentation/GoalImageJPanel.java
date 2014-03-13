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
/**
 *
 * @author Bill
 */

public class GoalImageJPanel extends JPanel implements ActionListener {







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
    JEditorPane editpane;
     ArrayList<goalProperties> gp = new ArrayList<goalProperties>();
      static JFrame frame = new JFrame("Goal Image");


   class goalProperties {

       private String name_source;
       private String image_source;
       private String html_source;
       private String toolt_source;

       goalProperties( String a, String b, String c, String d){

           name_source = a;
           image_source = b;
           html_source = c;
           toolt_source = d;
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
       public void gp_add( String a, String b, String c, String d ){
           name_source = a;
           image_source = b;
           html_source = c;
           toolt_source = d;
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
    public GoalImageJPanel(GUI gui, String conf_file) throws IOException {
        super(new BorderLayout());

        this.gui = gui;



        
       
        load_gp(conf_file);
        int imgNum = gp.size();
      
       

           // images = new ImageIcon[imgNum];
           // editpane = new JEditorPane();
            JTabbedPane tabPane;
            Double panelNum = Math.ceil(imgNum/4D);
            tabPane = new JTabbedPane();
            tabPane.setPreferredSize(new Dimension(600,600));
            //for (int j = 0; j < panelNum; j++){

            JPanel panel1 = new JPanel( new GridLayout( 4 , 2 ));

            JScrollPane scroller = new JScrollPane(panel1);
            tabPane.add(scroller);

            int j = 0;
            for (final goalProperties gps : gp){
                JButton button;
                
           
            j++;
                
               // if(i < imgNum){

                img = createImageIcon(gps.getImage_Source());
             
                editpane = createEditorPane(gps.getHtml_Source());
                
                button = new JButton();
                button.setSize(img.getIconWidth(), img.getIconHeight());
                 button.setIcon(img);
               

             
                button.setBorder(BorderFactory.createRaisedBevelBorder());
                button.setToolTipText(gps.getToolT_Source());
                button.addActionListener( new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent ae) {
                                try {
                                    createAndShowGoalImage(gps.getImage_Source(), gps.getHtml_Source());
                                  
                                } catch (IOException ex) {
                                    Logger.getLogger(GoalImageJPanel.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                
                            }
                });
                panel1.add( button );
               
                panel1.add( editpane );
             
                if( j % 4 == 0){
            tabPane.add("Page "+(j/4), scroller);
           add( tabPane );
           panel1 = new JPanel(new GridLayout(4, 2));
           scroller = new JScrollPane(panel1);
            tabPane.add(scroller);
        }
        }
      
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
        JEditorPane editorPane = new JEditorPane();
         editorPane.setEditable(false);
         editorPane.setAutoscrolls(true);
                editorPane.setAlignmentX(TOP_ALIGNMENT);
                editorPane.setAlignmentY(LEFT_ALIGNMENT);
                editorPane.setSize(5, 20);


        //System.out.println("Path: " + path);
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

            NumberFormat digit3 = new DecimalFormat("000");
            int i = 0;
            while(i < imgNum){
                String imgCodeNum = "Img" + digit3.format(i);

                nameStrings[i] = prop.getProperty(imgCodeNum + ".name");
               
                srcStrings[i] = prop.getProperty(imgCodeNum + ".loc");
                htmlStrings[i] = prop.getProperty(imgCodeNum + ".html");
                tooltStrings[i] = prop.getProperty(imgCodeNum + ".toolt");

                goalProperties gps = new goalProperties(nameStrings[i], srcStrings[i], htmlStrings[i], tooltStrings[i]);
                gp.add (gps);
            i++;
            }
            for (goalProperties gps : gp){
            
        }

    }

    public void createAndShowGoalImage(String image, String html) throws IOException {

        ImageIcon icon;
        JLabel label;
        icon = createImageIcon( image );

        label = new JLabel( icon );
        label.setSize(350, 350);
        JPanel panel1 = null, panel2 = null;
        panel1 = new JPanel();
        panel2 = new JPanel();
        
        JEditorPane editpane;
        editpane = new JEditorPane();
       
        editpane = createEditorPane( html );
        
        JTabbedPane tabPane;
        
        tabPane = new JTabbedPane();
        
        panel2.add(label, BorderLayout.CENTER );
        tabPane.add("Goal Image", panel2);
        
        
        
        tabPane.setPreferredSize(new Dimension(300,300));
        editpane.setSize(5,20);
        editpane.setEditable(false);
        editpane.setAutoscrolls(true);
        editpane.setAlignmentX(TOP_ALIGNMENT);
        editpane.setAlignmentY(LEFT_ALIGNMENT);
        editpane.setSize(5, 20);
        panel1.add( editpane);
        JScrollPane scroller = new JScrollPane(panel1);
        
        tabPane.add("Information", scroller);
        //Create and set up the window.
        JFrame frame1 = new JFrame("Goal Image");
        //frame.setDefaultCloseOperation(this.EXIT_ON_CLOSE);
        frame1.setLayout( new BorderLayout() );
        frame1.add( tabPane );
        frame1.setSize(350, 350);
        


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
        JComponent newContentPane = new GoalImageJPanel(gui, conf_file);
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


