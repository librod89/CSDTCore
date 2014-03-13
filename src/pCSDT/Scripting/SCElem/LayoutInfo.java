/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting.SCElem;

import pCSDT.*;
import org.jdom.*;
import java.awt.Image;
import java.net.URL;
import java.util.List;
import javax.imageio.*;
import org.jdom.input.*;

/**
 *
 * @author Jason
 * Layout information structure for the different scriptlet elements
 */
public class LayoutInfo {
    public Element sIf;
    public Element sWhile;
    public Element sConst;
    public Element sBinary;
    public Element sList;
    public Element sMethod;
    public Element sAssign;

    public LayoutSingleElement eIf;
    public LayoutSingleElement eWhile;
    public LayoutSingleElement eConst;
    public LayoutSingleElement eBinary;
    public LayoutSingleElement eList;
    public LayoutSingleElement eMethod;
    public LayoutSingleElement eAssign;

    public Image iIf;
    public Image iWhile;
    public Image iConst;
    public Image iBinary;
    public Image iList;
    public Image iMethod;
    public Image iAssign;

    ClassLoader m_ldr;

    public LayoutInfo(ClassLoader ldr)
    {
        m_ldr = ldr;
    }

    int[] LoadSplits(List c)
    {
        int[] temp = new int[c.size()];
        int i = 0;

        for(Object cur : c)
        {
            Element eCur = (Element)cur;
            Attribute val = eCur.getAttribute("n");
            if(val == null)
                continue;

            try
            {
                temp[i] = Integer.parseInt(val.getValue());
                i++;
            }
            catch(NumberFormatException e) {
                e.printStackTrace();
            }
        }

        int[] ret = new int[i];
        while(i-- != 0)
            ret[i] = temp[i];
        return ret;
    }

    LayoutSingleElement.SubRect[] LoadSubRects(Element parent, String... names)
    {
        LayoutSingleElement.SubRect[] r = new LayoutSingleElement.SubRect[names.length];
        for(int i = 0; i < names.length; i++)
        {
            Element child = parent.getChild(names[i]);
            r[i] = new LayoutSingleElement.SubRect(child);
        }
        return r;
    }

    void LoadIfStatement(URL context)
    {
        String path = sIf.getAttributeValue("SRC");
        if(path == null)
            return;

        try {iIf = ImageIO.read(Utility.MakeRelativeUrl(context, path));}
        catch(Exception e) {
            e.printStackTrace();
            return;
        }

        eIf = new LayoutSingleElement(iIf);
        eIf.inputRects = LoadSubRects(sIf, "Operand", "IfTrue", "IfFalse");
        eIf.hSplits = LoadSplits(sIf.getChildren("HSplit"));
        eIf.vSplits = LoadSplits(sIf.getChildren("VSplit"));
    }

    void LoadWhile(URL context)
    {
        String path = sWhile.getAttributeValue("SRC");
        if(path == null)
            return;

        try {iWhile = ImageIO.read(Utility.MakeRelativeUrl(context, path));}
        catch(Exception e) {
            e.printStackTrace();
            return;
        }

        eWhile = new LayoutSingleElement(iWhile);
        eWhile.inputRects = LoadSubRects(sWhile, "Condition", "OperationList");
        eWhile.hSplits = LoadSplits(sWhile.getChildren("HSplit"));
        eWhile.vSplits = LoadSplits(sWhile.getChildren("VSplit"));
    }

    void LoadConst(URL context)
    {
        String path = sConst.getAttributeValue("SRC");
        if(path == null)
            return;

        try {iConst = ImageIO.read(Utility.MakeRelativeUrl(context, path));}
        catch(Exception e) {
            e.printStackTrace();
            return;
        }

        eConst = new LayoutSingleElement(iConst);
        eConst.inputRects = LoadSubRects(sConst, "Variable", "Value");
        eConst.hSplits = LoadSplits(sConst.getChildren("HSplit"));
        eConst.vSplits = LoadSplits(sConst.getChildren("VSplit"));
    }

    void LoadBinary(URL context)
    {
        String path = sBinary.getAttributeValue("SRC");
        if(path == null)
            return;

        try {iBinary = ImageIO.read(Utility.MakeRelativeUrl(context, path));}
        catch(Exception e) {
            e.printStackTrace();
            return;
        }

        eBinary = new LayoutSingleElement(iBinary);
        eBinary.inputRects = LoadSubRects(sBinary, "Lhs", "Op", "Rhs");
        eBinary.hSplits = LoadSplits(sBinary.getChildren("HSplit"));
        eBinary.vSplits = LoadSplits(sBinary.getChildren("VSplit"));
    }

    void LoadList(URL context)
    {
        String path = sList.getAttributeValue("SRC");
        if(path == null)
            return;

        try {iList = ImageIO.read(Utility.MakeRelativeUrl(context, path));}
        catch(Exception e) {
            e.printStackTrace();
            return;
        }

        eList = new LayoutSingleElement(iList);
        eList.inputRects = LoadSubRects(sList, "Col");
        eList.hSplits = LoadSplits(sList.getChildren("HSplit"));
        eList.vSplits = LoadSplits(sList.getChildren("VSplit"));
    }

    void LoadMethod(URL context)
    {
        String path = sMethod.getAttributeValue("SRC");
        if(path == null)
            return;

        try {iMethod = ImageIO.read(Utility.MakeRelativeUrl(context, path));}
        catch(Exception e) {
            e.printStackTrace();
            return;
        }

        eMethod = new LayoutSingleElement(iMethod);
        eMethod.inputRects = LoadSubRects(sMethod, "ParameterList", "OperationList");
        eMethod.hSplits = LoadSplits(sMethod.getChildren("HSplit"));
        eMethod.vSplits = LoadSplits(sMethod.getChildren("VSplit"));
    }

    void LoadAssign(URL context)
    {
        String path = sAssign.getAttributeValue("SRC");
        if(path == null)
            return;

        try {iAssign = ImageIO.read(Utility.MakeRelativeUrl(context, path));}
        catch(Exception e) {
            e.printStackTrace();
            return;
        }

        eAssign = new LayoutSingleElement(iAssign);
        eAssign.inputRects = LoadSubRects(sAssign, "Variable", "Value");
        eAssign.hSplits = LoadSplits(sAssign.getChildren("HSplit"));
        eAssign.vSplits = LoadSplits(sAssign.getChildren("VSplit"));
    }

    public void LoadFromXml(URL xmlDoc) throws JDOMException, java.io.IOException
    {
        SAXBuilder b = new SAXBuilder();
        Document doc = b.build(xmlDoc);
        Element root = doc.getRootElement();

        sIf = root.getChild("If");
        //sWhile = root.getChild("While");
        //sAssign = root.getChild("Assign");
        sBinary = root.getChild("Binary");
        //sConst = root.getChild("Const");
        sList = root.getChild("List");
        //sMethod = root.getChild("Method");

        if(sIf != null)
            LoadIfStatement(xmlDoc);
        if(sBinary != null)
            LoadBinary(xmlDoc);
        //if(sWhile != null)
        //    LoadWhile(xmlDoc);
        //if(sAssign != null)
        //    LoadAssign(xmlDoc);
        //if(sConst != null)
        //    LoadConst(xmlDoc);
        if(sList != null)
            LoadList(xmlDoc);
        //if(sMethod != null)
        //    LoadMethod(xmlDoc);
    }
}