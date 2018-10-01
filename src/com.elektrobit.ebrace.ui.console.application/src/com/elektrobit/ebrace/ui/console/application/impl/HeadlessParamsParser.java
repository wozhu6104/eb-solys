/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.ui.console.application.impl;

import java.util.regex.Pattern;

import com.elektrobit.ebrace.core.interactor.api.headlessexecutor.HeadlessParamConstants;
import com.elektrobit.ebrace.core.interactor.api.headlessexecutor.HeadlessParams;
import com.elektrobit.ebrace.platform.commandlineparser.extern.interfaces.CommandLineParser;
import com.elektrobit.ebrace.platform.commandlineparser.extern.interfaces.ValueOfArgumentMissingException;

public class HeadlessParamsParser
{
    private final HeadlessParams params = new HeadlessParams();
    private final CommandLineParser commandLineParser;

    public HeadlessParamsParser(CommandLineParser commandLineParser)
    {
        this.commandLineParser = commandLineParser;
        readCommandParams();
    }

    public HeadlessParams getParams()
    {
        return params;
    }

    private void readCommandParams()
    {
        readScriptParam();
        readFileToLoadNameParam();
        readHelpParam();
        readConnectParam();
        readSaveToFilePathParam();
        readSaveToFilePrefixParam();
        readScriptModeParams();
    }

    private void readScriptParam()
    {
        String scriptParam = null;

        try
        {
            scriptParam = commandLineParser.getValue( HeadlessParamConstants.SCRIPT_PARAMETER_SWITCH );
        }
        catch (ValueOfArgumentMissingException e)
        {
        }

        if (scriptParam != null)
        {
            params.scriptName = scriptParam;
        }
    }

    private void readFileToLoadNameParam()
    {
        try
        {
            params.fileToLoadName = commandLineParser.getValue( HeadlessParamConstants.FILE_TO_LOAD_PARAMETER_SWITCH );
        }
        catch (ValueOfArgumentMissingException e)
        {
        }
    }

    private void readHelpParam()
    {
        params.isHelpSwitchSet = commandLineParser.hasArg( HeadlessParamConstants.HELP_PARAMETER_SWITCH );
    }

    private void readConnectParam()
    {
        String ipAndPort = null;
        try
        {
            ipAndPort = commandLineParser.getValue( HeadlessParamConstants.CONNECT_PARAMETER_SWITCH );
        }
        catch (ValueOfArgumentMissingException e)
        {
        }

        if (ipAndPort != null)
        {
            String regexSeparator = Pattern.quote( HeadlessParamConstants.IP_AND_PORT_SEPARATOR );
            String[] ipPortArray = ipAndPort.split( regexSeparator );
            if (ipPortArray.length >= 1)
            {
                params.targetIpAdress = ipPortArray[0];
            }
            if (ipPortArray.length == 2)
            {
                try
                {
                    params.targetPort = ipPortArray[1];
                }
                catch (NumberFormatException e)
                {
                }
            }
        }
    }

    private void readSaveToFilePathParam()
    {
        try
        {
            params.saveToFilePath = commandLineParser
                    .getValue( HeadlessParamConstants.DESTINATION_PATH_PARAMETER_SWITCH );
        }
        catch (ValueOfArgumentMissingException e)
        {
        }
    }

    private void readSaveToFilePrefixParam()
    {
        try
        {
            params.saveToFilePrefix = commandLineParser
                    .getValue( HeadlessParamConstants.DESTINATION_FILE_PREFIX_PARAMETER_SWITCH );
        }
        catch (ValueOfArgumentMissingException e)
        {
        }
    }

    private void readScriptModeParams() throws IllegalArgumentException
    {
        try
        {
            String scriptMode = commandLineParser.getValue( HeadlessParamConstants.SCRIPT_METHOD_NAME );
            if (scriptMode != null)
            {
                params.scriptMethodName = scriptMode;
            }
        }
        catch (ValueOfArgumentMissingException e)
        {
        }
    }
}
