/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation is used to indicate that a PEventList may be automated. It
 * should be specified on just about every PEventList member of a class. The
 * only exception is if you have a PEventList that's used internally by the
 * class.
 *
 * @author tylau
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface AutomatableEventList {
    String name() default "";
    String desc() default "";

    String[] argNames() default {};
    String[] argDesc() default {};
    String[] argTypes() default {};
    String[] argVals() default {};

    // Category stuff. See eCategory for more information
    eCategory cat() default eCategory.General;
    String CustomCategory() default "";
}
