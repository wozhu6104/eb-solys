/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.interactor.allChannels;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.core.interactor.allChannels.ChannelToNodeConverter;
import com.elektrobit.ebrace.dev.test.util.datamanager.RuntimeEventChannelMock;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.ChannelTreeNode;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public class ChannelToNodeConverterTestWithSomeChannels
{

    private static final String[] INPUT = {"A.B.C.1", "A.B.C.2", "A.B.C.3", "B.C.D.E", "C.1.1", "C.1.2", "C.1.3",
            "C.2.1", "C.2.2", "D.E.F.1", "D.E.F.2", "D.E.F.3.1"};

    private static final List<RuntimeEventChannel<?>> INPUT_LIST = createFixture();

    private ChannelToNodeConverter underTest;
    private ChannelTreeNode rootNode;

    @Before
    public void setup()
    {
        underTest = new ChannelToNodeConverter();
        rootNode = underTest.convert( INPUT_LIST );
    }

    private static List<RuntimeEventChannel<?>> createFixture()
    {
        List<RuntimeEventChannel<?>> result = new ArrayList<>();
        for (String name : INPUT)
        {
            result.add( new RuntimeEventChannelMock<String>( name ) );
        }
        return result;
    }

    @Test
    public void verifyRootNodeChildren()
    {
        assertEquals( 4, rootNode.getChildCount() );
    }

    @Test
    public void verifyChild0()
    {
        ChannelTreeNode child = rootNode.getChildAt( 0 );
        assertEquals( "A.B.C", child.getFullName() );
        assertEquals( "A.B.C", child.getNodeName() );
        assertEquals( 3, child.getChildCount() );
        assertEquals( rootNode, child.getParent() );
    }

    @Test
    public void verifyChild00()
    {
        ChannelTreeNode child = rootNode.getChildAt( 0 ).getChildAt( 0 );
        assertEquals( "A.B.C.1", child.getFullName() );
        assertEquals( "1", child.getNodeName() );
        assertEquals( 0, child.getChildCount() );
        assertEquals( rootNode.getChildAt( 0 ), child.getParent() );
    }

    @Test
    public void verifyChild01()
    {
        ChannelTreeNode child = rootNode.getChildAt( 0 ).getChildAt( 1 );
        assertEquals( "A.B.C.2", child.getFullName() );
        assertEquals( "2", child.getNodeName() );
        assertEquals( 0, child.getChildCount() );
        assertEquals( rootNode.getChildAt( 0 ), child.getParent() );
    }

    @Test
    public void verifyChild02()
    {
        ChannelTreeNode child = rootNode.getChildAt( 0 ).getChildAt( 2 );
        assertEquals( "A.B.C.3", child.getFullName() );
        assertEquals( "3", child.getNodeName() );
        assertEquals( 0, child.getChildCount() );
        assertEquals( rootNode.getChildAt( 0 ), child.getParent() );
    }

    @Test
    public void verifyChild1()
    {
        ChannelTreeNode child = rootNode.getChildAt( 1 );
        assertEquals( "B.C.D.E", child.getFullName() );
        assertEquals( "B.C.D.E", child.getNodeName() );
        assertEquals( 0, child.getChildCount() );
        assertEquals( rootNode, child.getParent() );
    }

    @Test
    public void verifyChild2()
    {
        ChannelTreeNode child = rootNode.getChildAt( 2 );
        assertEquals( "C", child.getFullName() );
        assertEquals( "C", child.getNodeName() );
        assertEquals( 2, child.getChildCount() );
        assertEquals( rootNode, child.getParent() );
    }

    @Test
    public void verifyChild20()
    {
        ChannelTreeNode child = rootNode.getChildAt( 2 ).getChildAt( 0 );
        assertEquals( "C.1", child.getFullName() );
        assertEquals( "1", child.getNodeName() );
        assertEquals( 3, child.getChildCount() );
        assertEquals( rootNode.getChildAt( 2 ), child.getParent() );
    }

    @Test
    public void verifyChild200()
    {
        ChannelTreeNode child = rootNode.getChildAt( 2 ).getChildAt( 0 ).getChildAt( 0 );
        assertEquals( "C.1.1", child.getFullName() );
        assertEquals( "1", child.getNodeName() );
        assertEquals( 0, child.getChildCount() );
        assertEquals( rootNode.getChildAt( 2 ).getChildAt( 0 ), child.getParent() );
    }

    @Test
    public void verifyChild201()
    {
        ChannelTreeNode child = rootNode.getChildAt( 2 ).getChildAt( 0 ).getChildAt( 1 );
        assertEquals( "C.1.2", child.getFullName() );
        assertEquals( "2", child.getNodeName() );
        assertEquals( 0, child.getChildCount() );
        assertEquals( rootNode.getChildAt( 2 ).getChildAt( 0 ), child.getParent() );
    }

    @Test
    public void verifyChild202()
    {
        ChannelTreeNode child = rootNode.getChildAt( 2 ).getChildAt( 0 ).getChildAt( 2 );
        assertEquals( "C.1.3", child.getFullName() );
        assertEquals( "3", child.getNodeName() );
        assertEquals( 0, child.getChildCount() );
        assertEquals( rootNode.getChildAt( 2 ).getChildAt( 0 ), child.getParent() );
    }

    @Test
    public void verifyChild21()
    {
        ChannelTreeNode child = rootNode.getChildAt( 2 ).getChildAt( 1 );
        assertEquals( "C.2", child.getFullName() );
        assertEquals( "2", child.getNodeName() );
        assertEquals( 2, child.getChildCount() );
        assertEquals( rootNode.getChildAt( 2 ), child.getParent() );
    }

    @Test
    public void verifyChild210()
    {
        ChannelTreeNode child = rootNode.getChildAt( 2 ).getChildAt( 1 ).getChildAt( 0 );
        assertEquals( "C.2.1", child.getFullName() );
        assertEquals( "1", child.getNodeName() );
        assertEquals( 0, child.getChildCount() );
        assertEquals( rootNode.getChildAt( 2 ).getChildAt( 1 ), child.getParent() );
    }

    @Test
    public void verifyChild211()
    {
        ChannelTreeNode child = rootNode.getChildAt( 2 ).getChildAt( 1 ).getChildAt( 1 );
        assertEquals( "C.2.2", child.getFullName() );
        assertEquals( "2", child.getNodeName() );
        assertEquals( 0, child.getChildCount() );
        assertEquals( rootNode.getChildAt( 2 ).getChildAt( 1 ), child.getParent() );
    }

    @Test
    public void verifyChild3()
    {
        ChannelTreeNode child = rootNode.getChildAt( 3 );
        assertEquals( "D.E.F", child.getFullName() );
        assertEquals( "D.E.F", child.getNodeName() );
        assertEquals( 3, child.getChildCount() );
        assertEquals( rootNode, child.getParent() );
    }

    @Test
    public void verifyChild30()
    {
        ChannelTreeNode child = rootNode.getChildAt( 3 ).getChildAt( 0 );
        assertEquals( "D.E.F.1", child.getFullName() );
        assertEquals( "1", child.getNodeName() );
        assertEquals( 0, child.getChildCount() );
        assertEquals( rootNode.getChildAt( 3 ), child.getParent() );
    }

    @Test
    public void verifyChild31()
    {
        ChannelTreeNode child = rootNode.getChildAt( 3 ).getChildAt( 1 );
        assertEquals( "D.E.F.2", child.getFullName() );
        assertEquals( "2", child.getNodeName() );
        assertEquals( 0, child.getChildCount() );
        assertEquals( rootNode.getChildAt( 3 ), child.getParent() );
    }

    @Test
    public void verifyChild32()
    {
        ChannelTreeNode child = rootNode.getChildAt( 3 ).getChildAt( 2 );
        assertEquals( "D.E.F.3.1", child.getFullName() );
        assertEquals( "3.1", child.getNodeName() );
        assertEquals( 0, child.getChildCount() );
        assertEquals( rootNode.getChildAt( 3 ), child.getParent() );
    }

}
