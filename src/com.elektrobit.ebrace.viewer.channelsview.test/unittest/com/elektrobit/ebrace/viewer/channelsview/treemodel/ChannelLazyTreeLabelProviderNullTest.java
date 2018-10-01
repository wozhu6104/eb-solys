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

import org.junit.Before;
import org.junit.Test;

public class ChannelLazyTreeLabelProviderNullTest
{
    private ChannelLazyTreeLabelProvider underTest;

    @Before
    public void setUp()
    {
        underTest = new ChannelLazyTreeLabelProvider( null );
    }

    @Test(expected = NullPointerException.class)
    public void callUpdateWithNull()
    {
        underTest.update( null );
    }

    @Test(expected = NullPointerException.class)
    public void callGetLineLabelTextWithNull()
    {
        underTest.getLineLabelText( null );
    }

    @Test(expected = NullPointerException.class)
    public void callGetToolTipTextWithNull()
    {
        underTest.getToolTipText( null );
    }

    @Test(expected = NullPointerException.class)
    public void callGetImageWithNull()
    {
        underTest.getImage( null );
    }

    @Test
    public void callGetTextWithNull()
    {
        String text = underTest.getText( null );
        assertNull( text );
    }

}
