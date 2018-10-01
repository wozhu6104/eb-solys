/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import test.com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.test.ResMonFordGen2Test;
import test.com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.test.ResMonLinuxWithThreads;
import test.com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.test.ResMonLinuxWithoutThreadsTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ResMonLinuxWithoutThreadsTest.class, ResMonFordGen2Test.class, ResMonLinuxWithThreads.class})
public class ComElektrobitEbraceGeniviTargetadapterResourcemonitorTest
{
}
