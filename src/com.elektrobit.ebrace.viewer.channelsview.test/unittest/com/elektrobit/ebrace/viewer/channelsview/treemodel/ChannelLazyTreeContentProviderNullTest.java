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

import static org.junit.Assert.assertNull;

import org.eclipse.jface.viewers.TreeViewer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ChannelLazyTreeContentProviderNullTest
{
    private ChannelLazyTreeContentProvider underTest;
    private TreeViewer viewer;

    @Before
    public void setUp()
    {
        viewer = Mockito.mock( TreeViewer.class );
        underTest = new ChannelLazyTreeContentProvider( viewer );
    }

    @Test
    public void callInputChangedWithNull()
    {
        underTest.inputChanged( null, null, null );
    }

    @Test
    public void callGetParentWithNull()
    {
        Object parent = underTest.getParent( null );
        assertNull( parent );
    }

    @Test(expected = NullPointerException.class)
    public void callUpdateChildCountWithNull()
    {
        underTest.updateChildCount( null, 0 );
    }

    @Test(expected = NullPointerException.class)
    public void callUpdateElementWithNull()
    {
        underTest.updateElement( null, 0 );
    }

}
