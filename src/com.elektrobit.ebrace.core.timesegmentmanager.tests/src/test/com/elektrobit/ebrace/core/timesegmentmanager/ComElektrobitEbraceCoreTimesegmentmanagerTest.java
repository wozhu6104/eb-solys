/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.timesegmentmanager;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import test.com.elektrobit.ebrace.core.timesegmentmanager.impl.AddTimeSegmentToChannelTest;
import test.com.elektrobit.ebrace.core.timesegmentmanager.impl.ClearTimeSegmentChannelTest;
import test.com.elektrobit.ebrace.core.timesegmentmanager.impl.CreateTimeSegmentChannelTest;
import test.com.elektrobit.ebrace.core.timesegmentmanager.impl.GetTimeSegmentsTest;
import test.com.elektrobit.ebrace.core.timesegmentmanager.impl.SetTimeSegmentColorTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({CreateTimeSegmentChannelTest.class, AddTimeSegmentToChannelTest.class,
        ClearTimeSegmentChannelTest.class, GetTimeSegmentsTest.class,
        SetTimeSegmentColorTest.class/* Add here your test classes */})
public class ComElektrobitEbraceCoreTimesegmentmanagerTest
{
}
