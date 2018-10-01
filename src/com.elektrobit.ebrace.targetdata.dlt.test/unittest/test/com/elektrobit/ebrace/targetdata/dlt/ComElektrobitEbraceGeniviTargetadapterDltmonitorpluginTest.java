/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.targetdata.dlt;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import test.com.elektrobit.ebrace.targetdata.dlt.internal.DltLogInfoTypeTest;
import test.com.elektrobit.ebrace.targetdata.dlt.internal.connection.GetAllDltChannelsMessageSenderServiceTest;
import test.com.elektrobit.ebrace.targetdata.dlt.logimporter.DltExtendedHeaderTest;
import test.com.elektrobit.ebrace.targetdata.dlt.logimporter.DltMessageParserTest;
import test.com.elektrobit.ebrace.targetdata.dlt.logimporter.DltProcPayloadParserTest;
import test.com.elektrobit.ebrace.targetdata.dlt.logimporter.DltStandardHeaderTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({DltMessageParserTest.class, DltExtendedHeaderTest.class, DltStandardHeaderTest.class,
        DltProcPayloadParserTest.class, GetAllDltChannelsMessageSenderServiceTest.class, DltLogInfoTypeTest.class,
        DltStandardHeaderTest.class/* Add here your test classes */})
public class ComElektrobitEbraceGeniviTargetadapterDltmonitorpluginTest
{
}
