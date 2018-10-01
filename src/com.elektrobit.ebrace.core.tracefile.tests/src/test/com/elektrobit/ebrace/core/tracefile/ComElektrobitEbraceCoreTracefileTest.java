/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.tracefile;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import test.com.elektrobit.ebrace.core.tracefile.internal.FileSizeLimitServiceImplTest;
import test.com.elektrobit.ebrace.core.tracefile.util.LoadFileFromTest;
import test.com.elektrobit.ebrace.core.tracefile.util.OpenFileTest;
import test.com.elektrobit.ebrace.core.tracefile.util.TraceFileSplitterTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({TraceFileSplitterTest.class, LoadFileFromTest.class, OpenFileTest.class,
        FileSizeLimitServiceImplTest.class})
public class ComElektrobitEbraceCoreTracefileTest
{
}
