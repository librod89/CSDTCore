/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 * @author Jason
 * This annotation is used to provide a little more information to the GUI about
 * the automatable classes that an engine may report to the framework--for
 * instance, a friendly name and a brief description of the class.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface AutomatableClass {
	String name() default "";
	String desc() default "";

    // Category stuff.  See eCategory for more information.
    eCategory cat() default eCategory.General;
    String CustomCategory() default "";
}
