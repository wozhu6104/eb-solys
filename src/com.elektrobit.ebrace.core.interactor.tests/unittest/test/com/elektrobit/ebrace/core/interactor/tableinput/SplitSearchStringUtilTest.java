/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.interactor.tableinput;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.elektrobit.ebrace.core.interactor.tableinput.filter.SplitSearchStringUtil;

import junit.framework.Assert;

public class SplitSearchStringUtilTest
{

    private final String FILTER_STRING = "upstart       -      \"-dbus\"               -  3062           \"-mem\"    ";

    @Test
    public void testGetIgnoredWordsList()
    {
        List<String> expected = Arrays.asList( "3062", "-dbus" );
        List<String> ignoredWords = SplitSearchStringUtil.getIgnoredWordsList( FILTER_STRING );

        Assert.assertEquals( 2, ignoredWords.size() );
        Assert.assertEquals( expected, ignoredWords );
    }

    @Test
    public void testGetNotIgnoredWordsList()
    {
        List<String> expected = Arrays.asList( "upstart", "-mem" );
        List<String> ignoredWords = SplitSearchStringUtil.getNotIgnoredWordsList( FILTER_STRING );

        Assert.assertEquals( 2, ignoredWords.size() );
        Assert.assertEquals( expected, ignoredWords );
    }
}
