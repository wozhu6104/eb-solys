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

import java.io.IOException;
import java.io.StringReader;

import org.apache.log4j.Logger;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.elektrobit.ebsolys.core.targetdata.api.ModelElement;

public class NetworkPacket
{
    private static final Logger LOG = Logger.getLogger( NetworkPacket.class );

    private long timestamp = 0;

    private String payload = "";

    private XmlPullParserFactory parserFactory;

    private XmlPullParser xmlPullParser;

    private String sourceIP = "";
    private String destinationIP = "";
    private String sourceService = "unknown";
    private String destinationService = "unknown";
    private String comRelationName = "";

    public NetworkPacket()
    {
        try
        {
            parserFactory = XmlPullParserFactory.newInstance();
            xmlPullParser = parserFactory.newPullParser();
        }
        catch (XmlPullParserException e)
        {
            LOG.error( e.getMessage() );
            e.printStackTrace();
        }
    }

    public NetworkPacket fromXML(String packetTagContent)
    {
        payload = packetTagContent;

        timestamp = parseTimestamp( packetTagContent );
        parseComRelation( packetTagContent );

        return this;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public String getPayload()
    {
        return payload;
    }

    private long parseTimestamp(String packetTagContent)
    {
        long timestamp = 0;

        try
        {
            xmlPullParser.setInput( new StringReader( packetTagContent ) );
            int eventType = xmlPullParser.getEventType();

            boolean timestampFound = false;

            while (eventType != XmlPullParser.END_DOCUMENT && !timestampFound)
            {
                String tagName = xmlPullParser.getName();

                switch (eventType)
                {
                    case XmlPullParser.START_TAG :
                        if (tagName.equals( "field" ))
                        {

                            if (xmlPullParser.getAttributeValue( 0 ).equals( "timestamp" ))
                            {
                                timestamp = pdmlTimeToMilliseconds( xmlPullParser.getAttributeValue( 4 ) );
                                timestampFound = true;
                            }
                        }
                        break;

                    default :
                        break;
                }

                eventType = xmlPullParser.next();
            }
        }
        catch (XmlPullParserException e)
        {
            LOG.error( e.getMessage() );
            e.printStackTrace();
        }
        catch (IOException e)
        {
            LOG.error( e.getMessage() );
            e.printStackTrace();
        }

        return timestamp;
    }

    private long pdmlTimeToMilliseconds(String pdmlTimestamp)
    {
        return Math.round( Double.parseDouble( pdmlTimestamp ) * 10000 ) / 10;
    }

    private void parseComRelation(String packetTagContent)
    {
        // boolean isGiopPacket = false;
        try
        {
            xmlPullParser.setInput( new StringReader( packetTagContent ) );
            int eventType = xmlPullParser.getEventType();

            boolean isReply = false;
            boolean isRequest = false;
            boolean serviceSet = false;

            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                String tagName = xmlPullParser.getName();

                switch (eventType)
                {
                    case XmlPullParser.START_TAG :

                        if (tagName.equals( "field" ))
                        {
                            String nameAttribute = xmlPullParser.getAttributeValue( 0 );

                            if (nameAttribute.equals( "ip.src" ))
                            {
                                sourceIP = xmlPullParser.getAttributeValue( 4 );
                            }
                            if (nameAttribute.equals( "ip.dst" ))
                            {
                                destinationIP = xmlPullParser.getAttributeValue( 4 );
                            }
                            if (nameAttribute.equals( "tcp.srcport" ))
                            {
                                sourceService = xmlPullParser.getAttributeValue( 4 );
                            }
                            if (nameAttribute.equals( "tcp.dstport" ))
                            {
                                destinationService = xmlPullParser.getAttributeValue( 4 );
                            }
                            if (nameAttribute.equals( "giop" ))
                            {
                                // isGiopPacket = true;
                            }
                            if (nameAttribute.equals( "giop.type" ))
                            {
                                if (xmlPullParser.getAttributeValue( 1 ).contains( "Reply" ))
                                {
                                    isReply = true;
                                }
                                else if (xmlPullParser.getAttributeValue( 1 ).contains( "Request" ))
                                {
                                    isRequest = true;
                                }
                            }
                            if (nameAttribute.startsWith( "giop-" ))
                            {
                                String[] serviceTokens = nameAttribute.split( "\\." );

                                if ((isRequest || isReply) && !serviceSet)
                                {
                                    comRelationName += serviceTokens[0];

                                    if (serviceTokens.length == 2)
                                    {
                                        comRelationName = serviceTokens[0] + "." + xmlPullParser.getAttributeValue( 4 );
                                    }
                                    if (serviceTokens.length == 4)
                                    {
                                        comRelationName = serviceTokens[0] + "." + serviceTokens[1] + "."
                                                + serviceTokens[2];
                                    }

                                    if (isReply)
                                    {
                                        sourceService += ":" + comRelationName;
                                    }
                                    else
                                    {
                                        destinationService += ":" + comRelationName;
                                    }
                                    serviceSet = true;
                                }
                            }
                        }
                        break;

                    default :
                        break;
                }

                eventType = xmlPullParser.next();
            }
        }
        catch (XmlPullParserException e)
        {
            LOG.error( e.getMessage() );
            e.printStackTrace();
        }
        catch (IOException e)
        {
            LOG.error( e.getMessage() );
            e.printStackTrace();
        }
    }

    public ModelElement getComRelation(PDMLStructureManager structureManager)
    {
        return structureManager.createComRelation( sourceIP, destinationIP, sourceService, destinationService );

    }

}
