/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.headlessexecutor.validators.impl;

import java.io.File;

import com.elektrobit.ebrace.core.interactor.headlessexecutor.validators.api.HeadlessParamValidator;

public class DataSourceSyntaxValidator implements HeadlessParamValidator
{

    private final String dataSourceParam;

    public DataSourceSyntaxValidator(String dataSourceParam)
    {
        this.dataSourceParam = dataSourceParam;
    }

    @Override
    public boolean validationFailed()
    {
        boolean result = true;
        if (dataSourceParam != null)
        {
            result &= !dataSourceParamCorrect();
        }
        return result;
    }

    private boolean dataSourceParamCorrect()
    {
        if (validConnectionSyntax( dataSourceParam ))
        {
            return true;
        }
        return new File( dataSourceParam ).isFile();
    }

    private boolean validConnectionSyntax(String fileOrConnection)
    {
        final String[] splittedConnection = fileOrConnection.split( ":" );
        if (splittedConnection.length != 2)
        {
            return false;
        }
        else if (!validIP( splittedConnection[0] ) || !validPort( splittedConnection[1] ))
        {
            return false;
        }
        return true;
    }

    private boolean validIP(String ipAddress)
    {
        return !ipAddress.isEmpty();
    }

    private boolean validPort(String port)
    {
        try
        {
            int portAsInt = Integer.parseInt( port );
            if (portAsInt >= 1 && portAsInt <= 65535)
            {
                return true;
            }
        }
        catch (NumberFormatException e)
        {
        }

        return false;
    }

    @Override
    public String errorMessage()
    {
        return "Data source param must be a path to a valid EB solys file or a <IP>:<PORT> to a running EB solys targetagent. "
                + "E.g '/path/to/myfiles/trace.bin' or '192.168.2.2:1234'";
    }

}
