/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.targetdata.dlt;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.dev.test.util.datamanager.MockedTimestampCreator;
import com.elektrobit.ebrace.dev.test.util.datamanager.TimestampMocker;
import com.elektrobit.ebrace.dev.test.util.services.CoreServiceHelper;
import com.elektrobit.ebrace.targetagent.protocol.commondefinitions.TargetAgentProtocolCommonDefinitions;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext.SOURCE_TYPE;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DynamicTargetAdaptorResult;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.TargetAdapter;
import com.elektrobit.ebsolys.core.targetdata.api.reset.ResetNotifier;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public class DltMonitorInvalidDataTest
{
    private RuntimeEventAcceptor runtimeEventAcceptor;
    private TargetAdapter dltMonitorController;
    private ResetNotifier resetNotifier;

    @Before
    public void setup()
    {

        DynamicTargetAdaptorResult result = CoreServiceHelper.getDltMonitorControllerFactory()
                .createNewInstance( new DataSourceContext( SOURCE_TYPE.FILE, "test." ) );
        dltMonitorController = result.getAdaptor();
        runtimeEventAcceptor = CoreServiceHelper.getRuntimeEventAcceptor();
        resetNotifier = CoreServiceHelper.getResetNotifier();

        dltMonitorController.onProtocolMessageReceived( TimestampMocker.mock( 1000 ),
                                                        TargetAgentProtocolCommonDefinitions.MessageType.MSG_TYPE_GENIVI_DLT_MONITOR_PLUGIN,
                                                        DltMessages.getInvalidDltMessage().toByteArray(),
                                                        new MockedTimestampCreator() );
    }

    @Test
    public void channelNotCreated()
    {
        List<RuntimeEventChannel<?>> channels = runtimeEventAcceptor.getRuntimeEventChannels();
        Assert.assertTrue( channels.isEmpty() );
    }

    @After
    public void cleanUp()
    {
        resetNotifier.performReset();
    }
}
