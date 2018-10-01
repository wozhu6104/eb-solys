/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.datamanager;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import test.com.elektrobit.ebrace.core.datamanager.internal.color.ChannelColorProviderServiceImplTest;
import test.com.elektrobit.ebrace.core.datamanager.internal.listener.ChannelListenerNotifierTest;
import test.com.elektrobit.ebrace.core.datamanager.internal.model.structure.StructureSelectionServiceImplTest;
import test.com.elektrobit.ebrace.core.datamanager.internal.model.structure.TreeNodeCheckStateServiceImplTest;
import test.com.elektrobit.ebrace.core.datamanager.internal.runtime.event.MinMaxTest;
import test.com.elektrobit.ebrace.core.datamanager.internal.runtime.event.RuntimeEventAcceptorImplStateIdTest;
import test.com.elektrobit.ebrace.core.datamanager.internal.runtime.event.RuntimeEventChannelManagerNotificationTest;
import test.com.elektrobit.ebrace.core.datamanager.internal.runtime.event.RuntimeEventChannelManagerTest;
import test.com.elektrobit.ebrace.core.datamanager.internal.runtime.event.RuntimeEventSortTest;
import test.com.elektrobit.ebrace.core.datamanager.internal.runtime.event.RuntimeEventTagTest;
import test.com.elektrobit.ebrace.core.datamanager.internal.runtime.event.TargetHeaderMetaDataServiceTest;
import test.com.elektrobit.ebrace.core.datamanager.internal.runtime.event.UnitTest;
import test.com.elektrobit.ebrace.core.datamanager.reset.ResetTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({StructureSelectionServiceImplTest.class, TreeNodeCheckStateServiceImplTest.class,
        RuntimeEventAcceptorImplStateIdTest.class, RuntimeEventChannelManagerTest.class,
        RuntimeEventChannelManagerNotificationTest.class, ResetTest.class, ChannelColorProviderServiceImplTest.class,
        RuntimeEventSortTest.class, RuntimeEventTagTest.class, TargetHeaderMetaDataServiceTest.class,
        /* LineChartDataBuilderTest.class, */ ChannelListenerNotifierTest.class, UnitTest.class,
        MinMaxTest.class /* Addhereyourtestclasses */})
public class ComElektrobitEbraceDatamanagerTest
{
}
