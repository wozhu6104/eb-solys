/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.genivi.targetadapter.dbus.reset;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.dev.test.util.memory.CyclicMemoryChecker;
import com.elektrobit.ebrace.dev.test.util.services.CoreServiceHelper;
import com.elektrobit.ebsolys.core.targetdata.api.reset.ResetNotifier;

import test.com.elektrobit.ebrace.genivi.targetadapter.dbus.test.TestDBusMessageSender;
import test.com.elektrobit.ebrace.genivi.targetadapter.dbus.test.DBusRequestResponseMessages;
import test.com.elektrobit.ebrace.genivi.targetadapter.dbus.test.DBusSignalMessage;

public class ResetMemoryTest
{
    private ResetNotifier resetNotfier;
    private TestDBusMessageSender dBusMessageSender;

    @Before
    public void setup()
    {
        resetNotfier = CoreServiceHelper.getResetNotifier();
        dBusMessageSender = new TestDBusMessageSender();
    }

    @Test
    public void isMemoryConstantAfterResetOf1000Messages() throws Exception
    {
        Runnable testCode = new Runnable()
        {

            @Override
            public void run()
            {

                for (int i = 0; i < 1000; i++)
                {
                    dBusMessageSender.sendDBusMessage( DBusRequestResponseMessages.dbusRequestDummy() );
                    dBusMessageSender.sendDBusMessage( DBusRequestResponseMessages.dbusResponseDummy() );
                    dBusMessageSender.sendDBusMessage( DBusSignalMessage.dbusSignalDummy() );
                }

                resetNotfier.performReset();
            }
        };

        double heapSizeStdDevInPercent = new CyclicMemoryChecker( true ).heapSizeStdDevInPercent( testCode );
        Assert.assertTrue( heapSizeStdDevInPercent < 0.01 );
    }

}
