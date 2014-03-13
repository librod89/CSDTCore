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
 * This annotation indicates that an external CSDT method is being specified.
 * The types and return type of this method must each be members of the PType
 * set.  Do not use variant or PStatement in your specification.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface AutomatableMethod {
    /**
     * This is the friendly function name for the function on which this
     * annotation is bound.  You might use this instead of the default function
     * name if you would like to include spaces or special characters.
     * 
     * @return The friendly function name
     */
    int displayPos() default 0;
    //For changing position programmatically
    String displayPosString() default "";
    String name() default "";

    /**
    * This is an ordered list of the friendly names of the arguments to the bound
    * function.  The length of this list must match the length of the arguments
    * because parameter names are typically stripped out in the release build
    * and so cannot be discovered via reflection.
    * 
    * @return An ordered list of friendly argument name
    */
    
    String[] argNames() default {};
    String[] argDesc() default {};
    String[] argVals() default {};

    // Category stuff.  See eCategory for more information.
    eCategory cat() default eCategory.General;
    String CustomCategory() default "";
}
