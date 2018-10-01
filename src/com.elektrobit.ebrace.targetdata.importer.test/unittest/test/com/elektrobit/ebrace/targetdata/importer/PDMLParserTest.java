/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.targetdata.importer;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.elektrobit.ebrace.targetdata.importer.internal.NetworkPacket;
import com.elektrobit.ebrace.targetdata.importer.internal.PDMLParser;

public class PDMLParserTest
{

    private PDMLParser parser = new PDMLParser( PDMLTestStrings.PDML_DATA );
    private static List<NetworkPacket> pdmlPackets = null;

    @Test
    public void testParsePDML()
    {
        pdmlPackets = parser.parse();
        assertTrue( pdmlPackets.size() == 3 );
    }

}
