/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.importer.internal;

import com.elektrobit.ebrace.targetagent.protocol.frame.TargetAgentProtocolFrame.Header;
import com.elektrobit.ebrace.targetagent.protocol.frame.TargetAgentProtocolFrameOld.OldHeader;

public class TraceFileConverter
{

    private final int versionToken;

    public TraceFileConverter(int versionToken)
    {
        this.versionToken = versionToken;
    }

    public Header convert(OldHeader inputHeader)
    {
        Header.Builder builder = Header.newBuilder();

        builder.setTimestamp( inputHeader.getTimestamp() );
        builder.setType( inputHeader.getType() );
        builder.setLength( inputHeader.getLength() );
        builder.setVersionToken( versionToken );

        return builder.build();
    }

}
