/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.decoder.xml;

import java.io.IOException;
import java.io.StringReader;

import org.apache.log4j.Logger;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedNode;
import com.elektrobit.ebsolys.decoder.common.api.GenericDecodedNode;
import com.elektrobit.ebsolys.decoder.common.api.GenericDecodedTree;

public class XmlEventToDecodedTreeParser
{

    private XmlPullParser xmlPullParser;
    private XmlPullParserFactory parserFactory = null;
    private static final Logger LOG = Logger.getLogger( XmlEventToDecodedTreeParser.class );

    public XmlEventToDecodedTreeParser()
    {
        try
        {
            parserFactory = XmlPullParserFactory.newInstance();
        }
        catch (XmlPullParserException e)
        {
            LOG.error( e.getMessage() );
            e.printStackTrace();
        }
    }

    public GenericDecodedTree parseXML(String xml, String treeName)
    {
        GenericDecodedTree tree = new GenericDecodedTree( treeName );
        GenericDecodedNode rootNode = tree.getRootNode();

        try
        {
            xmlPullParser = parserFactory.newPullParser();
            xmlPullParser.setInput( new StringReader( xml ) );
            int eventType = xmlPullParser.getEventType();

            DecodedNode lastChild = rootNode;
            int indentationLevel = 0;

            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                String tagName = xmlPullParser.getName();
                int numArgs = xmlPullParser.getAttributeCount();
                String value = "";

                switch (eventType)
                {
                    case XmlPullParser.START_TAG :
                        indentationLevel++;
                        if (indentationLevel > 0)
                        {
                            if (numArgs > 0)
                            {
                                lastChild = lastChild.createChildNode( tagName, value );
                                for (int i = 0; i < numArgs; i++)
                                {
                                    String attributeName = xmlPullParser.getAttributeName( i );
                                    String attributeValue = xmlPullParser.getAttributeValue( i );
                                    value += (attributeName + "=\"" + attributeValue + "\" ");

                                    lastChild.createChildNode( attributeName, attributeValue );
                                }
                                lastChild.setValue( value );
                            }
                            else
                            {
                                lastChild = lastChild.createChildNode( tagName );
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG :
                        indentationLevel--;
                        if (indentationLevel > 0)
                        {
                            lastChild = lastChild.getParentNode();
                            if (lastChild == null)
                            {
                                lastChild = rootNode;
                            }
                        }
                        break;

                    case XmlPullParser.TEXT :
                        final String text = xmlPullParser.getText();
                        if (!text.trim().isEmpty())
                        {
                            DecodedNode childNode = lastChild.createChildNode( "textContent" );
                            childNode.setValue( text );
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

        return tree;
    }

}
