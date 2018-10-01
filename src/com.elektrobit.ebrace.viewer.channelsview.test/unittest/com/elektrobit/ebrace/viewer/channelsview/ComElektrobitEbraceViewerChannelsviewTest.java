/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.channelsview;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.elektrobit.ebrace.viewer.channelsview.treemodel.ChannelLazyTreeContentProviderTestSuite;
import com.elektrobit.ebrace.viewer.channelsview.treemodel.ChannelLazyTreeLabelProviderTestSuite;
import com.elektrobit.ebrace.viewer.channelsview.treemodel.ChannelTreeNodeTestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ChannelLazyTreeLabelProviderTestSuite.class, ChannelLazyTreeContentProviderTestSuite.class,
        ChannelTreeNodeTestSuite.class, MapComboTest.class})
public class ComElektrobitEbraceViewerChannelsviewTest
{
}
