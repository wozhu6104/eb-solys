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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.dev.test.util.datamanager.MockedTimestampCreator;
import com.elektrobit.ebrace.dev.test.util.datamanager.TimestampMocker;
import com.elektrobit.ebrace.dev.test.util.services.CoreServiceHelper;
import com.elektrobit.ebrace.targetagent.protocol.commondefinitions.TargetAgentProtocolCommonDefinitions;
import com.elektrobit.ebrace.targetagent.protocol.dltloginspector.TargetAgentProtocolDLTLogInspector.DLTMessageType;
import com.elektrobit.ebrace.targetagent.protocol.dltloginspector.TargetAgentProtocolDLTLogInspector.MsgBusInfo;
import com.elektrobit.ebrace.targetagent.protocol.dltloginspector.TargetAgentProtocolDLTLogInspector.MsgControlInfo;
import com.elektrobit.ebrace.targetagent.protocol.dltloginspector.TargetAgentProtocolDLTLogInspector.MsgLogInfo;
import com.elektrobit.ebrace.targetagent.protocol.dltloginspector.TargetAgentProtocolDLTLogInspector.MsgTraceInfo;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext.SOURCE_TYPE;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DynamicTargetAdaptorResult;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.TargetAdapter;
import com.elektrobit.ebsolys.core.targetdata.api.reset.ResetNotifier;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public class DltMonitorIntegrationTest
{
    private static String TRACE_PREFIX = "trace.";

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

        sendMessage( 1000, DLTMessageType.DLT_TYPE_LOG, MsgLogInfo.DLT_LOG_INFO, null, null, null );
        sendMessage( 1005, DLTMessageType.DLT_TYPE_LOG, MsgLogInfo.DLT_LOG_INFO, null, null, null );

        sendMessage( 1001, DLTMessageType.DLT_TYPE_APP_TRACE, null, MsgTraceInfo.DLT_TRACE_VARIABLE, null, null );
        sendMessage( 1001, DLTMessageType.DLT_TYPE_NW_TRACE, null, null, MsgBusInfo.DLT_NW_TRACE_IPC, null );
        sendMessage( 1001, DLTMessageType.DLT_TYPE_CONTROL, null, null, null, MsgControlInfo.DLT_CONTROL_REQUEST );
    }

    private void sendMessage(long timestamp, DLTMessageType messageType, MsgLogInfo msgLogInfo,
            MsgTraceInfo msgTraceInfo, MsgBusInfo msgBusInfo, MsgControlInfo msgControlInfo)
    {
        dltMonitorController.onProtocolMessageReceived( TimestampMocker.mock( timestamp ),
                                                        TargetAgentProtocolCommonDefinitions.MessageType.MSG_TYPE_GENIVI_DLT_MONITOR_PLUGIN,
                                                        DltMessages
                                                                .getDltLogDummyMessage( messageType,
                                                                                        msgLogInfo,
                                                                                        msgTraceInfo,
                                                                                        msgBusInfo,
                                                                                        msgControlInfo )
                                                                .toByteArray(),
                                                        new MockedTimestampCreator() );
    }

    @Test
    public void channelCorrectCreated()
    {
        Assert.assertNotNull( getDltLogInfoChannel() );
        Assert.assertNotNull( getDltAppTraceVariableChannel() );
        Assert.assertNotNull( getDltNwTraceIpcChannel() );
        Assert.assertNotNull( getDltControlRequestChannel() );
    }

    private RuntimeEventChannel<String> getDltLogInfoChannel()
    {
        return runtimeEventAcceptor.getRuntimeEventChannel( "test." + TRACE_PREFIX + "dlt.log.info", String.class );
    }

    private RuntimeEventChannel<String> getDltAppTraceVariableChannel()
    {
        return runtimeEventAcceptor.getRuntimeEventChannel( "test." + TRACE_PREFIX + "dlt.app_trace.variable",
                                                            String.class );
    }

    private RuntimeEventChannel<String> getDltNwTraceIpcChannel()
    {
        return runtimeEventAcceptor.getRuntimeEventChannel( "test." + TRACE_PREFIX + "dlt.nw_trace.ipc", String.class );
    }

    private RuntimeEventChannel<String> getDltControlRequestChannel()
    {
        return runtimeEventAcceptor.getRuntimeEventChannel( "test." + TRACE_PREFIX + "dlt.control.request",
                                                            String.class );
    }

    @Test
    public void numberOfMessageCorrect()
    {
        Assert.assertEquals( 2,
                             runtimeEventAcceptor.getRuntimeEventsOfRuntimeEventChannel( getDltLogInfoChannel() )
                                     .size() );
    }

    @Test
    public void timestampOfMessageCorrect()
    {
        Assert.assertEquals( 1000000,
                             runtimeEventAcceptor.getRuntimeEventsOfRuntimeEventChannel( getDltLogInfoChannel() )
                                     .get( 0 ).getTimestamp() );
    }

    @Test
    public void contentOfMessageCorrect()
    {
        String expectedMessageTimestamp = DltMessages.getDltDummyMessage().getTraceMessage().getTimestamp();
        String expectedMessageChannel = DltMessages.getDltDummyMessage().getTraceMessage().getChannel();
        String expectedMessageContext = DltMessages.getDltDummyMessage().getTraceMessage().getContext();
        String expectedMessageData = DltMessages.getDltDummyMessage().getTraceMessage().getData();

        String expectedMessageString = "[" + expectedMessageTimestamp + "]" + expectedMessageChannel + "."
                + expectedMessageContext + ": " + expectedMessageData;

        String resultMessageData = (String)runtimeEventAcceptor
                .getRuntimeEventsOfRuntimeEventChannel( getDltLogInfoChannel() ).get( 0 ).getValue();

        Assert.assertEquals( expectedMessageString, resultMessageData );
    }

    @After
    public void cleanUp()
    {
        resetNotifier.performReset();
    }
}
