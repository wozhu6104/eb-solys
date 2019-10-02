/*******************************************************************************
 * Copyright (C) 2019 systemticks GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.script.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;

import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptMethod;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.script.external.Matches;

public class ScriptMethodContextCheck
{

    public static boolean isChannelMatching(RaceScriptMethod method, RuntimeEventChannel<?> selectedChannel)
    {
        String restriction = "";

        if (method.getMethod().getParameterCount() == 1)
        {
            Parameter arg = method.getMethod().getParameters()[0];
            for (Annotation argAnnotation : arg.getAnnotations())
            {
                if (argAnnotation instanceof Matches)
                {
                    restriction = ((Matches)argAnnotation).name();
                    if (restriction.contains( "*" ))
                    {
                        restriction = restriction.replace( ".", "\\." ).replace( "*", ".*" );
                    }
                }
            }
        }

        return (restriction.equals( "" ) || selectedChannel.getName().matches( restriction ));
    }

}
