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

import com.elektrobit.ebrace.chronograph.impl.ChronographImpl;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.Timestamp;

import junit.framework.Assert;

public class TimestampProviderWithTargetTimeBaseTest
{
    private ChronographImpl timestampProvider;

    @Before
    public void setup()
    {
        timestampProvider = new ChronographImpl( 5000 );
        timestampProvider.registerTargetTimebase( "TARGET_NAME", 6000, 6100 );
    }

    @Test
    public void createTimestampForTargetWithTargetRegistration()
    {
        Timestamp timestamp = timestampProvider.getTargetTimestampCreator( "TARGET_NAME" ).create( 100 );

        Assert.assertEquals( 100, timestamp.getTimeInMillis() );
    }

}
