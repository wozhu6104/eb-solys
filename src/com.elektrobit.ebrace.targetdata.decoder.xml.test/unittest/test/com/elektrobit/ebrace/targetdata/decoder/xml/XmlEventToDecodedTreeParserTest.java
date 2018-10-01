/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.targetdata.decoder.xml;

import org.junit.Test;

import com.elektrobit.ebrace.targetdata.decoder.xml.XmlEventToDecodedTreeParser;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedNode;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedNodeVisitor;
import com.elektrobit.ebsolys.decoder.common.api.GenericDecodedTree;

import junit.framework.Assert;

public class XmlEventToDecodedTreeParserTest
{
    //@formatter:off
    private static final String inputXML_simple = 
    		  "<rootTag>"
    		+ "  <tag1/>"
    		+ "  <tag2 name=\"value\">Hello</tag2>"
    		+ "  <tag3>"
    		+ "    <tag3a/>"
    		+ "    <tag3b content=\"content3b\"/>"
    		+ "  </tag3>"
    		+ "</rootTag>";
    //@formatter:on

    private static GenericDecodedTree inputTree = null;
    private final XmlEventToDecodedTreeParser parser = new XmlEventToDecodedTreeParser();

    @Test
    public void test_parseXML_simpleCase() throws Exception
    {
        final String treeName = "SimpleTree";
        inputTree = new GenericDecodedTree( treeName );
        DecodedNode xmlRoot = inputTree.getRootNode().createChildNode( "rootTag" );
        xmlRoot.createChildNode( "tag1" );

        DecodedNode child2 = xmlRoot.createChildNode( "tag2" );
        String value = "name=\"value\" ";
        child2.createChildNode( "name", "value" );
        child2.setValue( value );

        DecodedNode child2child1 = child2.createChildNode( "textContent" );
        child2child1.setValue( "Hello" );

        DecodedNode child3 = xmlRoot.createChildNode( "tag3" );
        child3.createChildNode( "tag3a" );
        DecodedNode child3b = child3.createChildNode( "tag3b" );
        String value3b = "content=\"content3b\" ";
        child3b.createChildNode( "content", "content3b" );
        child3b.setValue( value3b );

        GenericDecodedTree result = parser.parseXML( inputXML_simple, treeName );

        result.traverse( new DecodedNodeVisitor()
        {

            @Override
            public void nodeVisited(DecodedNode node)
            {
                System.out.println( node.getName() + ": " + node.getValue() );

            }
        } );

        Assert.assertTrue( inputTree.equals( result ) );
    }
}
