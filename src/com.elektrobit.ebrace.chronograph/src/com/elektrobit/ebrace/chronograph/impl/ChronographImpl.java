/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.chronograph.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elektrobit.ebrace.chronograph.api.TimestampMode;
import com.elektrobit.ebrace.chronograph.api.TimestampProvider;
import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.platform.commandlineparser.extern.interfaces.CommandLineParser;
import com.elektrobit.ebrace.platform.commandlineparser.extern.interfaces.ValueOfArgumentMissingException;
import com.elektrobit.ebsolys.core.targetdata.api.TimestampCreator;

import lombok.extern.log4j.Log4j;

@Log4j
@Component
public class ChronographImpl implements TimestampProvider
{
    private final long initialAbsoluteTimeInMillis;
    private final Map<String, TimestampCreator> targetTimebaseMap;
    private final TargetTimebase hostTimeBase;
    private TimestampMode timestampMode = TimestampMode.ABSOLUTE_TARGET_TIME;
    private CommandLineParser commandLineParser;
    private TimestampCreatorImpl hostTimestampCreator;

    public ChronographImpl(long initialMillis)
    {
        this.initialAbsoluteTimeInMillis = initialMillis;
        targetTimebaseMap = new HashMap<String, TimestampCreator>();
        hostTimeBase = new TargetTimebase( initialAbsoluteTimeInMillis, 0, initialMillis );
    }

    public ChronographImpl()
    {
        this( System.currentTimeMillis() );
    }

    @Reference
    public void bind(CommandLineParser commandLineParser)
    {
        this.commandLineParser = commandLineParser;
    }

    public void unbind(CommandLineParser commandLineParser)
    {
        this.commandLineParser = null;
    }

    @Activate
    public void activate(ComponentContext context)
    {
        setTimestampModeFromCMDLineIfAvailable();

        hostTimestampCreator = new TimestampCreatorImpl( timestampMode, hostTimeBase );
    }

    private void setTimestampModeFromCMDLineIfAvailable()
    {
        if (commandLineParser.hasArg( "--timestamp-mode" ))
        {
            String timestampMode = getTimestampModeString();
            if (timestampMode.equals( "absolute-race-time" ))
            {
                setTimestampMode( TimestampMode.ABSOLUTE_RACE_TIME );
            }
            else if (timestampMode.equals( "absolute-target-time" ))
            {
                setTimestampMode( TimestampMode.ABSOLUTE_TARGET_TIME );
            }
            else if (timestampMode.equals( "relative-race-time" ))
            {
                setTimestampMode( TimestampMode.RELATIVE_RACE_TIME );
            }
            else if (timestampMode.equals( "relative-target-time" ))
            {
                setTimestampMode( TimestampMode.RELATIVE_TARGET_TIME );
            }
        }
    }

    private String getTimestampModeString()
    {
        try
        {
            return commandLineParser.getValue( "--timestamp-mode" );
        }
        catch (ValueOfArgumentMissingException e)
        {
            log.warn( "Timestamp mode parameter has no value", e );
            return null;
        }
    }

    @Override
    public void registerTargetTimebase(String name, long absoluteTargetTimeAtRegistration)
    {
        registerTargetTimebase( name, absoluteTargetTimeAtRegistration, System.currentTimeMillis() );
    }

    /* test-public */
    public void registerTargetTimebase(String name, long absoluteTargetTimeAtRegistration, long currentTimeInMillis)
    {
        RangeCheckUtils.assertStringParameterNotNullOrEmpty( "Target timebase name", name );

        TargetTimebase targetTimebase = new TargetTimebase( initialAbsoluteTimeInMillis,
                                                            absoluteTargetTimeAtRegistration,
                                                            currentTimeInMillis );

        targetTimebaseMap.put( name, new TimestampCreatorImpl( getTimestampMode(), targetTimebase ) );

    }

    @Override
    public Collection<String> getTargetTimebaseNames()
    {
        return targetTimebaseMap.keySet();
    }

    private void setTimestampMode(TimestampMode timestampMode)
    {
        this.timestampMode = timestampMode;
    }

    private TimestampMode getTimestampMode()
    {
        return timestampMode;
    }

    @Override
    public TimestampCreator getHostTimestampCreator()
    {
        return hostTimestampCreator;
    }

    @Override
    public TimestampCreator getTargetTimestampCreator(String name)
    {
        return targetTimebaseMap.get( name );
    }

}
