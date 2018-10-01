/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.channelsview.treemodel;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.eclipse.jface.viewers.TreeViewer;
import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.ChannelTreeNode;

public class ChannelLazyTreeContentProviderOnlyRootNodeTest
{
    private ChannelLazyTreeContentProvider underTest;
    private TreeViewer viewer;
    private ChannelTreeNode rootNode;

    @Before
    public void setUp()
    {
        viewer = mock( TreeViewer.class );
        underTest = new ChannelLazyTreeContentProvider( viewer );

        rootNode = new ChannelTreeNode( "root" );
    }

    @Test
    public void verifyUpdateChildCount()
    {
        underTest.updateChildCount( rootNode, 0 );
        verify( viewer ).setChildCount( rootNode, 0 );
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void callUpdateElementWithRootNode()
    {
        underTest.updateElement( rootNode, 0 );
    }

}
