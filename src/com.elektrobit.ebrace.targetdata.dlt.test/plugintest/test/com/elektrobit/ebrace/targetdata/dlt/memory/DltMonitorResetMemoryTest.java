/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.targetdata.dlt.memory;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.dev.test.util.datamanager.MockedTimestampCreator;
import com.elektrobit.ebrace.dev.test.util.datamanager.TimestampMocker;
import com.elektrobit.ebrace.dev.test.util.memory.CyclicMemoryChecker;
import com.elektrobit.ebrace.dev.test.util.services.CoreServiceHelper;
import com.elektrobit.ebrace.targetagent.protocol.commondefinitions.TargetAgentProtocolCommonDefinitions;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext.SOURCE_TYPE;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DynamicTargetAdaptorResult;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.TargetAdapter;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.TargetAdaptorFactory;
import com.elektrobit.ebsolys.core.targetdata.api.reset.ResetNotifier;

import test.com.elektrobit.ebrace.targetdata.dlt.DltMessages;

public class DltMonitorResetMemoryTest
{
    private TargetAdapter dltMonitorController;
    private ResetNotifier resetNotifier;

    @Before
    public void setup()
    {
        TargetAdaptorFactory factory = CoreServiceHelper.getDltMonitorControllerFactory();
        DynamicTargetAdaptorResult result = factory
                .createNewInstance( new DataSourceContext( SOURCE_TYPE.FILE, "test." ) );
        dltMonitorController = result.getAdaptor();
        resetNotifier = CoreServiceHelper.getResetNotifier();
    }

    @Test
    public void isHeapSizeConstantAfterReset() throws Exception
    {
        Runnable testCode = new Runnable()
        {

            @Override
            public void run()
            {

                for (int i = 0; i < 1000; i++)
                {
                    dltMonitorController.onProtocolMessageReceived( TimestampMocker.mock( 1000 + i ),
                                                                    TargetAgentProtocolCommonDefinitions.MessageType.MSG_TYPE_GENIVI_DLT_MONITOR_PLUGIN,
                                                                    DltMessages.getDltDummyMessage().toByteArray(),
                                                                    new MockedTimestampCreator() );
                }

                resetNotifier.performReset();

            }
        };

        MatcherAssert.assertThat( new CyclicMemoryChecker( true ).heapSizeStdDevInPercent( testCode ),
                                  Matchers.lessThan( 0.01 ) );
    }

    @After
    public void cleanUp()
    {
        resetNotifier.performReset();
    }
}
