/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.core.ebrace.datamanager.tests.tests;

import org.junit.Assert;
import org.junit.Test;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceRegistration;
import com.elektrobit.ebrace.dev.test.util.services.CoreServiceHelper;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannelsChangedListener;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

public class RuntimeEventAcceptorChannelsChangedNotificationTest implements RuntimeEventChannelsChangedListener
{
    private int numbersOfNotifcations;

    @Test
    public void testName() throws Exception
    {
        numbersOfNotifcations = 0;
        CoreServiceHelper.getRuntimeEventAcceptor().dispose();

        GenericOSGIServiceRegistration.registerService( RuntimeEventChannelsChangedListener.class, this );

        CoreServiceHelper.getRuntimeEventAcceptor().createOrGetRuntimeEventChannel( "TestChannel", Unit.TEXT, "" );

        Assert.assertEquals( 1, numbersOfNotifcations );
    }

    @Override
    public void onRuntimeEventChannelsChanged()
    {
        numbersOfNotifcations++;
    }

    // @Override
    // public void onRuntimeEventChannelDeleted(RuntimeEventChannel<?> channel)
    // {
    // numbersOfNotifcations++;
    // }

}
