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

import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.ChannelTreeNode;

public class ChannelTreeNodeNullTest
{
    private ChannelTreeNode underTest;

    @Before
    public void setUp()
    {
        underTest = new ChannelTreeNode( null );
    }

    @Test
    public void callAddChildWithNull()
    {
        underTest.addChild( null );
    }

}
