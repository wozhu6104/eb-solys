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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.core.interactor.allChannels.ChannelToNodeConverter;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.ChannelTreeNode;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public class ChannelToNodeConverterTestWithEmptyList
{

    private static final List<RuntimeEventChannel<?>> INPUT = new ArrayList<>();

    private ChannelToNodeConverter underTest;
    private ChannelTreeNode rootNode;

    @Before
    public void setup()
    {
        underTest = new ChannelToNodeConverter();
        rootNode = underTest.convert( INPUT );
    }

    @Test
    public void checkThatResultIsNotNull()
    {
        assertNotNull( rootNode );
    }

    @Test
    public void verifyRootNodeHasNoParent()
    {
        assertNull( rootNode.getParent() );
    }

    @Test
    public void verifyRootNodeHasChildren()
    {
        assertEquals( 0, rootNode.getChildCount() );
    }

}
