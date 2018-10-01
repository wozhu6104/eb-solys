/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.script.wizard;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import com.elektrobit.ebrace.common.utils.FileHelper;
import com.elektrobit.ebrace.dev.usestatlogsannotationloader.api.UseStatLog;
import com.elektrobit.ebrace.dev.usestatlogsannotationloader.api.UseStatLogTypes;
import com.elektrobit.ebrace.viewer.script.ViewerScriptPlugin;

public class ScriptSourceGenerator
{
    public enum ScriptContext {
        GLOBAL_CONTEXT, TIMEMARKER_CONTEXT, CHANNEL_CONTEXT, EVENTLIST_CONTEXT, CALLBACK_CONTEXT, FILTER_CONTEXT, CHANNELS_CONTEXT, RUNTIMEEVNET_CONTEXT, TIMEMARKERS_CONTEXT
    };

    public static final String IMPORT_GLOBAL_IMPORT = "import com.elektrobit.ebsolys.script.external.Execute\nimport com.elektrobit.ebsolys.script.external.Execute.ExecutionContext\n";
    public static final String IMPORT_NONE = IMPORT_GLOBAL_IMPORT + "";
    public static final String IMPORT_RUNTIME_EVENT_CHANNEL = IMPORT_GLOBAL_IMPORT
            + "import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel\n";
    public static final String IMPORT_TIME_MARKER = IMPORT_GLOBAL_IMPORT
            + "import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker\n";
    public static final String IMPORT_RUNTIME_EVENT_LIST = IMPORT_GLOBAL_IMPORT
            + "import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent\nimport java.util.List\n";
    public static final String IMPORT_RUNTIME_EVENT_FILTER = "import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent\nimport com.elektrobit.ebsolys.script.external.Filter";
    public static final String IMPORT_RUNTIME_CHANNEL_EVENT = IMPORT_GLOBAL_IMPORT
            + " import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel\nimport java.util.List";
    public static final String IMPORT_RUNTIME_EVENT = IMPORT_GLOBAL_IMPORT
            + "import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent";
    public static final String IMPORT_TIME_MARKERS = IMPORT_GLOBAL_IMPORT
            + "import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker\nimport java.util.List\n";

    public static final String IMPORT_ANNOTATION_AFTER_RACE_SCRIPT = "import com.elektrobit.ebsolys.script.external.AfterScript\n";
    public static final String IMPORT_ANNOTATION_BEFORE_RACE_SCRIPT = "import com.elektrobit.ebsolys.script.external.BeforeScript\n";
    public static final String IMPORT_INJECTED_PARAM = "import com.elektrobit.ebrace.core.scriptannotation.impl.InjectedParam\n";

    public static final String TAG_CLASSNAME = "//CLASSNAME//";
    public static final String TAG_EXECUTE_METHOD = "//EXECUTE_METHOD//";
    public static final String TAG_EXECUTE_PARAM_IMPORT = "//EXECUTE_PARAM_IMPORT//";
    public static final String TAG_BEFORE_ANNOTATION_IMPORT = "//BEFORE_SCRIPT_ANNOTATION_IMPORT//";
    public static final String TAG_AFTER_ANNOTATION_IMPORT = "//AFTER_SCRIPT_ANNOTATION_IMPORT//";
    public static final String TAG_INJECTED_PARAM_IMPORT = "//INJECTED_PARAM_IMPORT//";
    public static final String TAG_BEFORE_METHOD = "//BEFORE_METHOD//";
    public static final String TAG_AFTER_METHOD = "//AFTER_METHOD//";
    public static final String TAG_INJECTED_PARAM = "//INJECTED_PARAM//";

    @UseStatLog(value = UseStatLogTypes.USER_SCRIPT_CREATED, parser = UseStatLogScriptTypeParser.class)
    public static String generateClassContent(final String className, ScriptContext scriptContext, boolean beforeMethod,
            boolean afterMethod, boolean injectedParameter)
    {
        String scriptTemplate = getStringFromFilePath( "resources/ScriptTemplate.txt" );

        String executeMethod = getStringFromFilePath( getExecuteFunctionFilePath( scriptContext ) );
        String contextImportLine = getContextImportLine( scriptContext );

        scriptTemplate = scriptTemplate.replaceFirst( TAG_CLASSNAME, className );
        scriptTemplate = scriptTemplate.replaceFirst( TAG_EXECUTE_METHOD, executeMethod );
        scriptTemplate = scriptTemplate.replaceFirst( TAG_EXECUTE_PARAM_IMPORT, contextImportLine );
        scriptTemplate = scriptTemplate.replaceFirst( TAG_BEFORE_ANNOTATION_IMPORT,
                                                      beforeMethod ? IMPORT_ANNOTATION_BEFORE_RACE_SCRIPT : "" );
        scriptTemplate = scriptTemplate.replaceFirst( TAG_AFTER_ANNOTATION_IMPORT,
                                                      afterMethod ? IMPORT_ANNOTATION_AFTER_RACE_SCRIPT : "" );
        scriptTemplate = scriptTemplate.replaceFirst( TAG_INJECTED_PARAM_IMPORT,
                                                      injectedParameter ? IMPORT_INJECTED_PARAM : "" );

        scriptTemplate = scriptTemplate.replaceFirst( TAG_BEFORE_METHOD, beforeMethod ? getBeforeMethod() : "" );
        scriptTemplate = scriptTemplate.replaceFirst( TAG_AFTER_METHOD, afterMethod ? getAfterMethod() : "" );
        scriptTemplate = scriptTemplate.replaceFirst( TAG_INJECTED_PARAM, injectedParameter ? getInjectedParam() : "" );

        return scriptTemplate;
    }

    private static String getExecuteFunctionFilePath(ScriptContext context)
    {
        String path;
        switch (context)
        {
            case CHANNEL_CONTEXT :
                path = "resources/ScriptChannelMethodTemplate.txt";
                break;
            case EVENTLIST_CONTEXT :
                path = "resources/ScriptEventListMethodTemplate.txt";
                break;
            case TIMEMARKER_CONTEXT :
                path = "resources/ScriptTimeMarkerMethodTemplate.txt";
                break;
            case GLOBAL_CONTEXT :
                path = "resources/ScriptGlobalMethodTemplate.txt";
                break;
            case CALLBACK_CONTEXT :
                path = "resources/ScriptCallbackMethodTemplate.txt";
                break;
            case FILTER_CONTEXT :
                path = "resources/ScriptFilterkMethodTemplate.txt";
                break;
            case CHANNELS_CONTEXT :
                path = "resources/ScriptEventChannelsMethodsTemplate.txt";
                break;
            case TIMEMARKERS_CONTEXT :
                path = "resources/ScriptTimeMarkersMethodsTemplate.txt";
                break;
            case RUNTIMEEVNET_CONTEXT :
                path = "resources/ScriptRuntimeEventMethodsTemplate.txt";
                break;
            default :
                throw new RuntimeException( context + " not considered" );
        }

        return path;
    }

    private static String getContextImportLine(ScriptContext scriptContext)
    {
        switch (scriptContext)
        {
            case CHANNEL_CONTEXT :
                return IMPORT_RUNTIME_EVENT_CHANNEL;
            case EVENTLIST_CONTEXT :
                return IMPORT_RUNTIME_EVENT_LIST;
            case TIMEMARKER_CONTEXT :
                return IMPORT_TIME_MARKER;
            case GLOBAL_CONTEXT :
                return IMPORT_NONE;
            case CALLBACK_CONTEXT :
                return IMPORT_RUNTIME_EVENT_LIST;
            case FILTER_CONTEXT :
                return IMPORT_RUNTIME_EVENT_FILTER;
            case CHANNELS_CONTEXT :
                return IMPORT_RUNTIME_CHANNEL_EVENT;
            case RUNTIMEEVNET_CONTEXT :
                return IMPORT_RUNTIME_EVENT;
            case TIMEMARKERS_CONTEXT :
                return IMPORT_TIME_MARKERS;

            default :
                return null;
        }
    }

    private static String getBeforeMethod()
    {
        return getStringFromFilePath( "resources/ScriptBeforeMethodTemplate.txt" );
    }

    private static String getAfterMethod()
    {
        return getStringFromFilePath( "resources/ScriptAfterMethodTemplate.txt" );
    }

    private static String getInjectedParam()
    {
        return getStringFromFilePath( "resources/InjectedParamTemplate.txt" );
    }

    private static String getStringFromFilePath(String path)
    {
        URI uri = FileHelper.locateFileInBundle( ViewerScriptPlugin.PLUGIN_ID, path );
        File file = new File( uri );
        return getStringFromFile( file );
    }

    private static String getStringFromFile(File file)
    {
        String result = null;
        try
        {
            result = StringFromFileReaderHelper.stringFromFile( file );
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return result;
    }
}
