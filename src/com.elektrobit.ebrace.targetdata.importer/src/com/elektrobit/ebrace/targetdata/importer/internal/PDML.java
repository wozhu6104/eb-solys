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

import java.util.ArrayList;

public class PDML
{
    private final ArrayList<NetworkPacket> packets = new ArrayList<NetworkPacket>();

    public int getNumberOfPackets()
    {
        return packets.size();
    }

    public void add(NetworkPacket packet)
    {
        packets.add( packet );
    }

    public ArrayList<NetworkPacket> getPackets()
    {
        return packets;
    }

}
