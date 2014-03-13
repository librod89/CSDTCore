/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting;

import org.apache.commons.codec.binary.Base64;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.imageio.ImageIO;
import org.jdom.Element;
import pCSDT.Utility;

/**
 * Internal representation of a binary image in pCSDT
 * contains a Base64 encoded string that stores a buffered image
 * Other custom data type can also be done this way.
 * @author tylau
 */
public final class PBinaryImage implements Cloneable {
    // the Base64 string representation of the binary image
    public String binStr;

    /**
     * Constructor with no argument
     */
    public PBinaryImage() {
        binStr = "";
    }

    /**
     * Constructor that takes an already-encoded binary image
     * @param encodedStr the already-encoded binary image string
     */
    public PBinaryImage(String encodedStr) {
        binStr = encodedStr;
    }

    /**
     * Create the object with given BufferedImage and specific format
     * @param img the buffered image to be stored in this object
     * @param format the original file format of the buffered image
     */
    public PBinaryImage(BufferedImage img, String format)
            throws IOException {
        SetImage(img, format);
    }

    /**
     * Create the object from a file
     * @param bRelative whether the link is absolute or relative
     * @param fileName the name of the image file (png)
     * @throws IOException
     */
    public PBinaryImage(boolean bRelative, String fileName) throws IOException
    {
        SetImage(bRelative, fileName);
    }

    /**
     * Set image with given BufferedImage with format info
     * @param img the buffered image to be stored in this object
     * @param format the original file format of the buffered image
     */
    public void SetImage(BufferedImage img, String format) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, format, baos);
        baos.flush();
        byte[] encodedBytes = Base64.encodeBase64(baos.toByteArray());
        binStr = new String(encodedBytes);
    }

    /**
     * Set image with given FileName
     * @param bRelative whether the FileName is absolute or relative
     * @param FileName file name of the image (png)
     * @throws IOException
     */
    public void SetImage(boolean bRelative, String fileName)
            throws IOException {
        // update internal BufferedImage
        if (fileName != null && !fileName.equals("")) {
            try {
                // update bgImg
                if (bRelative) {
                    InputStream i = getClass().getResourceAsStream(fileName);
                    if (i != null) {
                        SetImage(ImageIO.read(i), "png");
                    }
                    else {
                        SetImage("");
                    }
                }
                else {
                    if (fileName.startsWith("http://")) {
                        SetImage(ImageIO.read(Utility.FormatURL(fileName)), "png");
                    }
                    else {
                        SetImage(ImageIO.read(new File(fileName)), "png");
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            SetImage("");
        }
    }
    /**
     * Set image with given encodedStr
     * @param encodedStr the already-encoded binary image to be stored here
     */
    public void SetImage(String encodedStr) {
        binStr = encodedStr;
    }

    /**
     * get the encoded image string represented by this object
     * @return the encoded string
     */
    @Override
    public String toString() {
        return binStr;
    }

    /**
     * get the BufferedImage representation of binStr
     * @return the BufferedImage represented by this object
     */
    public BufferedImage GetBufferedImage() throws IOException {
        byte[] decodedBytes = Base64.decodeBase64(binStr);
        return ImageIO.read(new ByteArrayInputStream(decodedBytes));
    }

    /**
     * Clone this object
     * @return the cloned object, with a copy of same image
     */
    @Override
    public PBinaryImage clone() {
        PBinaryImage bistr = new PBinaryImage();
        bistr.binStr = this.binStr;
        return bistr;
    }

    /**
     * Return the XML representation of this object
     * @return the XML representation of this object
     */
    public Element GetXml()
    {
	Element root = new Element("content");
        root.addContent(binStr);
	return root;
    }

    /**
     * Use XML to set up this object
     * @param elem the XML node that records info about this object
     * @return whether we successfully set up the object
     */
    public boolean SetXml(Element elem)
    {
        binStr = elem.getText();
	return true;
    }
}
