/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.targetdata.json;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.elektrobit.ebrace.targetdata.impl.importer.json.util.NodeAgent;
import com.elektrobit.ebrace.targetdata.impl.importer.json.util.StructuredNodeNameToNodeTree;

public class StructuredNodeNameToNodeTreeTest
{
    private static final String STRUCTURE_NODE_NAME1 = "s1.m1";
    private static final String STRUCTURE_NODE_NAME2 = "s1.m2";
    private static final String STRUCTURE_NODE_NAME3 = "s1.m2.c2";
    private static final String STRUCTURE_NODE_NAME4 = "s2.m1.c1";
    private static final List<String> structuredNodeName1 = Arrays.asList( STRUCTURE_NODE_NAME1.split( "\\." ) );
    private static final List<String> structuredNodeName2 = Arrays.asList( STRUCTURE_NODE_NAME2.split( "\\." ) );
    private static final List<String> structuredNodeName3 = Arrays.asList( STRUCTURE_NODE_NAME3.split( "\\." ) );
    private static final List<String> structuredNodeName4 = Arrays.asList( STRUCTURE_NODE_NAME4.split( "\\." ) );
    private static final List<String> structuredNodeName5 = Arrays.asList( "s1".split( "\\." ) );

    static int count = 0;

    private static class IntegerNode
    {
        private final String key;
        private Integer value;
        private final IntegerNode parent;

        public IntegerNode(String key, IntegerNode parent)
        {
            this.key = key;
            this.value = getNextValue();
            this.parent = parent;
        }

        private int getNextValue()
        {
            value = count++;
            return value;
        }
    }

    private static IntegerNode rootNode = new IntegerNode( "r", null );
    private static StructuredNodeNameToNodeTree<IntegerNode> creator = new StructuredNodeNameToNodeTree<IntegerNode>( new NodeAgent<IntegerNode>()
    {
        @Override
        public IntegerNode createNodeObject(String nodeName, IntegerNode parent)
        {
            return new IntegerNode( nodeName, parent );
        }
    }, rootNode );

    @Test
    public void addNode1()
    {

        IntegerNode node = creator.getOrCreate( structuredNodeName1 );
        assertEquals( STRUCTURE_NODE_NAME1, node.key );
        assertEquals( 2, creator.getNrOfUniqueNodes() );
    }

    @Test
    public void addNode2()
    {
        IntegerNode node2 = creator.getOrCreate( structuredNodeName2 );
        assertEquals( STRUCTURE_NODE_NAME2, node2.key );
        assertEquals( 3, creator.getNrOfUniqueNodes() );
    }

    @Test
    public void addNode3()
    {
        IntegerNode node3 = creator.getOrCreate( structuredNodeName3 );
        assertEquals( STRUCTURE_NODE_NAME3, node3.key );
        assertEquals( 4, creator.getNrOfUniqueNodes() );
    }

    @Test
    public void addNode4()
    {
        IntegerNode node4 = creator.getOrCreate( structuredNodeName4 );
        assertEquals( STRUCTURE_NODE_NAME4, node4.key );
        assertEquals( 7, creator.getNrOfUniqueNodes() );
    }

    @Test
    public void checkStructure()
    {
        assertEquals( creator.getOrCreate( structuredNodeName5 ).parent, rootNode );
        assertEquals( creator.getOrCreate( structuredNodeName1 ).parent, creator.getOrCreate( structuredNodeName5 ) );
    }

}
