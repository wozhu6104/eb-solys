/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.targetdata.adapter.linuxappstats;

import com.elektrobit.ebrace.dev.test.util.datamanager.MockedTimestampCreator;
import com.elektrobit.ebrace.targetdata.adapter.linuxappstats.protobuf.LinuxAppStatsContentTAProto.MeasureMessage;
import com.elektrobit.ebrace.targetdata.adapter.linuxappstats.service.AppStatsMessageAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;

public class AcceptMessageHelper
{
    private final AppStatsMessageAcceptor appStatsMessageAcceptor;

    public AcceptMessageHelper(RuntimeEventAcceptor runtimeEventAcceptor, DataSourceContext context)
    {
        appStatsMessageAcceptor = new AppStatsMessageAcceptor( runtimeEventAcceptor, context );
    }

    public void acceptMeasuredMessage(String processExample) throws IllegalArgumentException
    {
        MeasureMessage processMeasureMessage = MeasureMessageParserHelper.createMessageFromString( processExample );
        appStatsMessageAcceptor.acceptMessage( processMeasureMessage, new MockedTimestampCreator() );
    }
}
