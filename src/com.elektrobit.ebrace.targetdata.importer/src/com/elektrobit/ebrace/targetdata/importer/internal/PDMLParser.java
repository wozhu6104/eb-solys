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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class PDMLParser
{
    private static final Logger LOG = Logger.getLogger( PDMLParser.class );

    private FileReader fileReader = null;
    private BufferedReader bufferedReader = null;
    private StringReader stringReader = null;

    private static final String OPEN_TAG_PACKET = "<packet>";
    private static final String CLOSE_TAG_PACKET = "</packet>";

    public PDMLParser(File file)
    {
        try
        {
            fileReader = new FileReader( file );
            bufferedReader = new BufferedReader( fileReader );
        }
        catch (FileNotFoundException e)
        {
            LOG.error( e.getMessage() );
            e.printStackTrace();
        }

    }

    public PDMLParser(String pdml)
    {
        stringReader = new StringReader( pdml );
        bufferedReader = new BufferedReader( stringReader );
    }

    public List<NetworkPacket> parse()
    {
        List<NetworkPacket> pdmlPackets = new ArrayList<NetworkPacket>();
        try
        {
            String line;
            String packetTagContent = "";
            while ((line = bufferedReader.readLine()) != null)
            {

                if (line.startsWith( OPEN_TAG_PACKET ))
                {
                    packetTagContent = line + "\n";
                }
                else if (line.startsWith( CLOSE_TAG_PACKET ))
                {
                    packetTagContent += line;
                    pdmlPackets.add( new NetworkPacket().fromXML( packetTagContent ) );
                    packetTagContent = "";
                }
                else
                {
                    packetTagContent += (line + "\n");
                }
            }

            bufferedReader.close();
            if (fileReader != null)
                fileReader.close();

        }
        catch (FileNotFoundException e)
        {
            LOG.error( e.getMessage() );
            e.printStackTrace();
        }
        catch (IOException e)
        {
            LOG.error( e.getMessage() );
            e.printStackTrace();
        }
        return pdmlPackets;
    }

}
