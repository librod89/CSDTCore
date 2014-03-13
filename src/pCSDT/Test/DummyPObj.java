/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pCSDT.Test;
import pCSDT.Scripting.*;
import java.util.*;

/**
 * The is a dummy PObject
 * @author tylau
 */
public class DummyPObj extends PObject {
    PProperty[] m_props;
    PMethod[] m_methods;
    
    @AutomatableProperty(name="x", desc="A variable")
    public int x;
    
    public DummyPObj(String name, String desc) {super(name, desc);}

    @AutomatableMethod(displayPos = 4, name="GetX", argNames={}, argDesc={})
    public double GetX() {return 0f;}

    @AutomatableMethod(displayPos = 5, name="SetX", argNames={"x"}, argDesc={"xValue"})
    public void SetX(int x) {this.x = x;}

    public Vector<Vector3> GetPolyBound() {
        return null;
    }

	public static void main(String[] args) throws InterruptedException
	{
		;

		for(int i = 0; i < 100; i++)
		{
			Object obj = new Object();
			System.out.print(obj.hashCode());
			System.out.println(",");
			Thread.sleep(100);
		}
	}
}
