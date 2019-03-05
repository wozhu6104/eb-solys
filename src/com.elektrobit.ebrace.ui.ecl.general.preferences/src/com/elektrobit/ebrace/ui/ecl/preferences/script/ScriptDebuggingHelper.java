/*******************************************************************************
 * Copyright (C) 2019 systemticks GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.ui.ecl.preferences.script;

import java.io.File;
import java.net.URL;

import org.eclipse.core.runtime.Platform;

import com.elektrobit.ebrace.common.utils.FileHelper;

public class ScriptDebuggingHelper
{
    static final String START_TAG = "#GENERATED-REMOTE-DEBUG-OPTION-START";
    static final String DEBUG_OPTION_PARAM = "-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005";
    static final String END_TAG = "#GENERATED-REMOTE-DEBUG-OPTION-START";

    public static boolean isDebugOptionInIni()
    {
        try
        {
            URL url = new URL( Platform.getInstallLocation().getURL() + "ebsolys.ini" );
            File ebSolysIni = new File( url.toURI() );
            if (ebSolysIni.exists())
            {
                String ebSolysIniContent = FileHelper.readFileToString( ebSolysIni );
                if (ebSolysIniContent.contains( START_TAG ))
                {
                    return true;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return false;
    }
}
