/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.core.ebrace.datamanager.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import test.com.elektrobit.core.ebrace.datamanager.tests.tests.RuntimeEventAcceptorChannelsChangedNotificationTest;
import test.com.elektrobit.core.ebrace.datamanager.tests.tests.RuntimeEventAcceptorCreateOrGetRuntimeEventChannelCTest;
import test.com.elektrobit.core.ebrace.datamanager.tests.tests.RuntimeEventAcceptorCreateRuntimeEventChannelCTest;
import test.com.elektrobit.core.ebrace.datamanager.tests.tests.RuntimeEventAcceptorGetAllRuntimeEventChannelsCTest;
import test.com.elektrobit.core.ebrace.datamanager.tests.tests.RuntimeEventAcceptorGetAllRuntimeEventsCTest;
import test.com.elektrobit.core.ebrace.datamanager.tests.tests.RuntimeEventAcceptorGetFirstRuntimeEventCTest;
import test.com.elektrobit.core.ebrace.datamanager.tests.tests.RuntimeEventAcceptorGetFirstRuntimeEventForTimestampCTest;
import test.com.elektrobit.core.ebrace.datamanager.tests.tests.RuntimeEventAcceptorGetGanttChartDataCTest;
import test.com.elektrobit.core.ebrace.datamanager.tests.tests.RuntimeEventAcceptorGetLatestRuntimeEventCTest;
import test.com.elektrobit.core.ebrace.datamanager.tests.tests.RuntimeEventAcceptorGetLatestRuntimeEventForTimespanCTest;
import test.com.elektrobit.core.ebrace.datamanager.tests.tests.RuntimeEventAcceptorGetLatestRuntimeEventOfChannelCTest;
import test.com.elektrobit.core.ebrace.datamanager.tests.tests.RuntimeEventAcceptorGetLatestRuntimeEventsOfChannelsCTest;
import test.com.elektrobit.core.ebrace.datamanager.tests.tests.RuntimeEventAcceptorGetLineChartDataCTest;
import test.com.elektrobit.core.ebrace.datamanager.tests.tests.RuntimeEventAcceptorGetRuntimeEventChannelForNameAndTypeCTest;
import test.com.elektrobit.core.ebrace.datamanager.tests.tests.RuntimeEventAcceptorGetRuntimeEventChannelsForModelElementCTest;
import test.com.elektrobit.core.ebrace.datamanager.tests.tests.RuntimeEventAcceptorGetRuntimeEventChannelsForTypeCTest;
import test.com.elektrobit.core.ebrace.datamanager.tests.tests.RuntimeEventAcceptorGetRuntimeEventForTimespanCTest;
import test.com.elektrobit.core.ebrace.datamanager.tests.tests.RuntimeEventAcceptorGetRuntimeEventsForModelElementCTest;
import test.com.elektrobit.core.ebrace.datamanager.tests.tests.RuntimeEventAcceptorGetRuntimeEventsForModelElementsCTest;
import test.com.elektrobit.core.ebrace.datamanager.tests.tests.RuntimeEventAcceptorGetRuntimeEventsForRuntimeEventChannelCTest;
import test.com.elektrobit.core.ebrace.datamanager.tests.tests.RuntimeEventAcceptorGetRuntimeEventsForRuntimeEventChannelsCTest;
import test.com.elektrobit.core.ebrace.datamanager.tests.tests.RuntimeEventAcceptorGetRuntimeEventsForTimespanAndChannelsCTest;
import test.com.elektrobit.core.ebrace.datamanager.tests.tests.RuntimeEventAcceptorGetRuntimeEventsOfChannelsForTimestampCTest;
import test.com.elektrobit.core.ebrace.datamanager.tests.tests.RuntimeEventAcceptorStateIDTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({RuntimeEventAcceptorGetFirstRuntimeEventCTest.class,
        RuntimeEventAcceptorGetAllRuntimeEventsCTest.class, RuntimeEventAcceptorGetAllRuntimeEventChannelsCTest.class,
        RuntimeEventAcceptorGetRuntimeEventsForRuntimeEventChannelsCTest.class,
        RuntimeEventAcceptorGetRuntimeEventForTimespanCTest.class,
        RuntimeEventAcceptorGetRuntimeEventChannelsForTypeCTest.class,
        RuntimeEventAcceptorGetRuntimeEventChannelForNameAndTypeCTest.class,
        RuntimeEventAcceptorGetLatestRuntimeEventCTest.class,
        RuntimeEventAcceptorGetLatestRuntimeEventForTimespanCTest.class,
        RuntimeEventAcceptorGetRuntimeEventsForModelElementCTest.class,
        RuntimeEventAcceptorGetRuntimeEventsForRuntimeEventChannelCTest.class,
        RuntimeEventAcceptorGetRuntimeEventChannelsForModelElementCTest.class,
        RuntimeEventAcceptorGetLatestRuntimeEventOfChannelCTest.class,
        RuntimeEventAcceptorGetRuntimeEventsForTimespanAndChannelsCTest.class,
        RuntimeEventAcceptorGetFirstRuntimeEventForTimestampCTest.class,
        RuntimeEventAcceptorGetRuntimeEventsForModelElementsCTest.class,
        RuntimeEventAcceptorCreateRuntimeEventChannelCTest.class,
        RuntimeEventAcceptorCreateOrGetRuntimeEventChannelCTest.class, RuntimeEventAcceptorGetLineChartDataCTest.class,
        RuntimeEventAcceptorGetGanttChartDataCTest.class, RuntimeEventAcceptorStateIDTest.class,
        RuntimeEventAcceptorChannelsChangedNotificationTest.class,
        RuntimeEventAcceptorGetRuntimeEventsOfChannelsForTimestampCTest.class,
        RuntimeEventAcceptorGetLatestRuntimeEventsOfChannelsCTest.class})
public class RuntimeEventAcceptorCTestSuite
{

}
