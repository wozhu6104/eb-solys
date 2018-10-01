/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.common;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import test.com.elektrobit.ebrace.common.time.format.TimeFormatterTest;
import test.com.elektrobit.ebrace.common.utils.ByteArrayHelperTest;
import test.com.elektrobit.ebrace.common.utils.FileHelperRemoveFileExtensionTest;
import test.com.elektrobit.ebrace.common.utils.GenericListenerCallerTest;
import test.com.elektrobit.ebrace.common.utils.HexStringHelperTest;
import test.com.elektrobit.ebrace.common.utils.PageLoadingListCompositeTest;
import test.com.elektrobit.ebrace.common.utils.RangeCheckUtilsTest;
import test.com.elektrobit.ebrace.common.utils.UnitConverterTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({PageLoadingListCompositeTest.class, TimeFormatterTest.class, HexStringHelperTest.class,
        GenericListenerCallerTest.class, FileHelperRemoveFileExtensionTest.class, RangeCheckUtilsTest.class,
        UnitConverterTest.class, ByteArrayHelperTest.class})
public class ComElektrobitEbraceCommonTest
{
}
