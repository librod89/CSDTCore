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
 * This is a convenience interface for the specification of get/set properties.
 * The types of these properties must correspond to one of the PType members.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface AutomatableProperty {
    String name() default "";
    String DisplayName() default "";
    String desc() default "";
    //String group() default "basic";

    // determine if readable/editable in design time and run tim
    String DesignTimeBehavior() default "B";  // "B" - editable in basic group, "A" - editable in advanced group, "H" - hidden
    String RunTimeBehavior() default "E";  // "E" - editable (R+W), "R" - read only, "H" - hidden
    // Category stuff.  See eCategory for more information.
    eCategory cat() default eCategory.General;
    String CustomCategory() default "";
}
