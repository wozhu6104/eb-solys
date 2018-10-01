/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.datamanager.timemarker;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import test.com.elektrobit.ebrace.core.datamanager.timemarker.util.EventTimestampPositionInListConverterTest;
import test.com.elektrobit.ebrace.core.datamanager.timemarker.util.TimeMarkerImplTest;
import test.com.elektrobit.ebrace.core.datamanager.timemarker.util.TimeMarkerManagerImplTest;
import test.com.elektrobit.ebrace.core.datamanager.timemarker.util.TimeMarkerManagerResetTest;
import test.com.elektrobit.ebrace.core.datamanager.timemarker.util.TimestampPositionInChartConverterTest;
import test.com.elektrobit.ebrace.core.datamanager.timemarker.util.TimestampPositionInListConverterTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({TimestampPositionInChartConverterTest.class, TimeMarkerManagerImplTest.class,
        TimeMarkerImplTest.class, EventTimestampPositionInListConverterTest.class, TimeMarkerManagerResetTest.class,
        TimestampPositionInListConverterTest.class})
public class ComElektrobitEbraceDatamanagerTimemarkerTest
{
}
