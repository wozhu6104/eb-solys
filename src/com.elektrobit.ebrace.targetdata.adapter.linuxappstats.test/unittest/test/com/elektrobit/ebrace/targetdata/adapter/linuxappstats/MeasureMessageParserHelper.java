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

import com.elektrobit.ebrace.targetdata.adapter.linuxappstats.protobuf.LinuxAppStatsContentTAProto;
import com.elektrobit.ebrace.targetdata.adapter.linuxappstats.protobuf.LinuxAppStatsContentTAProto.MeasureMessage;
import com.elektrobit.ebrace.targetdata.adapter.linuxappstats.protobuf.LinuxAppStatsContentTAProto.MeasureMessage.Builder;
import com.google.protobuf.TextFormat;
import com.google.protobuf.TextFormat.ParseException;

public class MeasureMessageParserHelper
{
    public static MeasureMessage createMessageFromString(String messageAsString)
    {
        Builder builder = LinuxAppStatsContentTAProto.MeasureMessage.newBuilder();
        try
        {
            TextFormat.merge( messageAsString, builder );
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        return builder.build();
    }
}
