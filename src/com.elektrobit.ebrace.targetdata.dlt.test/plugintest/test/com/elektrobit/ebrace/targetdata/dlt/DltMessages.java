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

import com.elektrobit.ebrace.targetagent.protocol.dltloginspector.TargetAgentProtocolDLTLogInspector.DLTLogInspectorMessage;
import com.elektrobit.ebrace.targetagent.protocol.dltloginspector.TargetAgentProtocolDLTLogInspector.DLTLogInspectorMessageId;
import com.elektrobit.ebrace.targetagent.protocol.dltloginspector.TargetAgentProtocolDLTLogInspector.DLTLogInspectorTraceMessage;
import com.elektrobit.ebrace.targetagent.protocol.dltloginspector.TargetAgentProtocolDLTLogInspector.DLTMessageType;
import com.elektrobit.ebrace.targetagent.protocol.dltloginspector.TargetAgentProtocolDLTLogInspector.MsgBusInfo;
import com.elektrobit.ebrace.targetagent.protocol.dltloginspector.TargetAgentProtocolDLTLogInspector.MsgControlInfo;
import com.elektrobit.ebrace.targetagent.protocol.dltloginspector.TargetAgentProtocolDLTLogInspector.MsgLogInfo;
import com.elektrobit.ebrace.targetagent.protocol.dltloginspector.TargetAgentProtocolDLTLogInspector.MsgTraceInfo;

public class DltMessages
{
    public static DLTLogInspectorMessage getDltDummyMessage()
    {
        return getDltLogDummyMessage( DLTMessageType.DLT_TYPE_LOG, MsgLogInfo.DLT_LOG_INFO, null, null, null );
    }

    public static DLTLogInspectorMessage getDltLogDummyMessage(DLTMessageType messageType, MsgLogInfo msgLogInfo,
            MsgTraceInfo msgTraceInfo, MsgBusInfo msgBusInfo, MsgControlInfo msgControlInfo)
    {
        DLTLogInspectorMessage.Builder builder = DLTLogInspectorMessage.newBuilder();

        builder.setId( DLTLogInspectorMessageId.LOG_ANALYZER_MESSAGE_TRACE );
        builder.setTraceMessage( createDLTLogInspectorTraceMessage( messageType,
                                                                    msgLogInfo,
                                                                    msgTraceInfo,
                                                                    msgBusInfo,
                                                                    msgControlInfo ) );

        return builder.build();
    }

    private static DLTLogInspectorTraceMessage createDLTLogInspectorTraceMessage(DLTMessageType messageType,
            MsgLogInfo msgLogInfo, MsgTraceInfo msgTraceInfo, MsgBusInfo msgBusInfo, MsgControlInfo msgControlInfo)
    {
        DLTLogInspectorTraceMessage.Builder builderForValue = DLTLogInspectorTraceMessage.newBuilder();
        builderForValue.setTimestamp( "123456789" );
        builderForValue.setChannel( "Channel" );
        builderForValue.setContext( "Context" );
        builderForValue.setData( "Dlt message data" );
        builderForValue.setMessageType( messageType );
        if (msgLogInfo != null)
            builderForValue.setLogInfo( msgLogInfo );
        if (msgTraceInfo != null)
            builderForValue.setTraceInfo( msgTraceInfo );
        if (msgBusInfo != null)
            builderForValue.setBusInfo( msgBusInfo );
        if (msgControlInfo != null)
            builderForValue.setControlInfo( msgControlInfo );

        return builderForValue.build();
    }

    public static DLTLogInspectorMessage getInvalidDltMessage()
    {
        DLTLogInspectorMessage.Builder builder = DLTLogInspectorMessage.newBuilder();

        builder.setId( DLTLogInspectorMessageId.LOG_ANALYZER_MESSAGE_INVALID );
        builder.setTraceMessage( createDLTLogInspectorTraceMessage( DLTMessageType.DLT_TYPE_CONTROL,
                                                                    null,
                                                                    null,
                                                                    null,
                                                                    null ) );

        return builder.build();
    }
}
