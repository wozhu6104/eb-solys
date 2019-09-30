/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebsolys.script.external;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Execute {
    public enum ExecutionContext {
        GLOBAL, PRESELECTION, CALLBACK
    }

    public enum CallbackTime {
        MILLIS_100, MILLIS_1000, MILLIS_10000;

        public int getTime()
        {
            switch (this)
            {
                case MILLIS_100 :
                    return 100;
                case MILLIS_1000 :
                    return 1000;
                case MILLIS_10000 :
                    return 10000;
                default :
                    return 100;
            }
        }
    }

    ExecutionContext context() default ExecutionContext.GLOBAL;

    CallbackTime time() default CallbackTime.MILLIS_100;

    String description() default "";

    String restrictedTo() default "";

}
