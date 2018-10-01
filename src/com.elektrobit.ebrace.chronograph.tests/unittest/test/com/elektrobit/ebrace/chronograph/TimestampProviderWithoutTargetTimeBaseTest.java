/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.chronograph;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.chronograph.impl.ChronographImpl;
import com.elektrobit.ebrace.platform.commandlineparser.extern.interfaces.CommandLineParser;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.Timestamp;

import junit.framework.Assert;

public class TimestampProviderWithoutTargetTimeBaseTest
{
    private ChronographImpl timestampProvider;

    @Before
    public void setup()
    {
        timestampProvider = new ChronographImpl( 5000 );
        timestampProvider.bind( Mockito.mock( CommandLineParser.class ) );
        timestampProvider.activate( null );

    }

    @Test
    public void createTimestampForHostWithoutTargetRegistration()
    {
        Timestamp timestamp = timestampProvider.getHostTimestampCreator().create( 100 );
        Assert.assertEquals( 100, timestamp.getTimeInMillis() );
    }

}
