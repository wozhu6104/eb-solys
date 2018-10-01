/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.tracefile.internal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elektrobit.ebrace.platform.commandlineparser.extern.interfaces.CommandLineParser;
import com.elektrobit.ebrace.platform.commandlineparser.extern.interfaces.ValueOfArgumentMissingException;
import com.elektrobit.ebsolys.core.targetdata.api.importer.FileSizeLimitService;

import lombok.extern.log4j.Log4j;

@Component
@Log4j
public class FileSizeLimitServiceImpl implements FileSizeLimitService
{
    public final static String SOLYS_LIMIT_FILE_SIZE_PARAMETER = "-solys-file-size-limit";
    public final static long DEFAULT_FILE_LIMIT_MB = 500;

    private CommandLineParser commandLineParser;

    @Override
    public long getMaxSolysFileSizeMB()
    {
        String valueString;

        try
        {
            valueString = commandLineParser.getValue( SOLYS_LIMIT_FILE_SIZE_PARAMETER );
        }
        catch (ValueOfArgumentMissingException e)
        {
            return DEFAULT_FILE_LIMIT_MB;
        }

        long maxSize = tryToParseSize( valueString );
        if (maxSize == 0)
        {
            return Long.MAX_VALUE;
        }
        return maxSize;
    }

    private long tryToParseSize(String valueString)
    {
        try
        {
            Long result = Long.valueOf( valueString );
            return result;
        }
        catch (NumberFormatException e)
        {
            log.warn( "Cannot parse max file size, value was " + valueString + ". Using default value "
                    + DEFAULT_FILE_LIMIT_MB );
            return DEFAULT_FILE_LIMIT_MB;
        }
    }

    @Reference(unbind = "unbind")
    public void bind(CommandLineParser commandLineParser)
    {
        this.commandLineParser = commandLineParser;
    }

    public void unbind(CommandLineParser commandLineParser)
    {
        this.commandLineParser = null;
    }
}
