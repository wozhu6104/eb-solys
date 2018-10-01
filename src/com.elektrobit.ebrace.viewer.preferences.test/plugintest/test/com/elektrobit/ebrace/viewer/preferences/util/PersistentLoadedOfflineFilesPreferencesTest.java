/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.viewer.preferences.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.viewer.preferences.util.PersistentLoadedOfflineFilesPreferences;

public class PersistentLoadedOfflineFilesPreferencesTest
{
    private static String PATH_TO_FILE_1 = "C:\\Users\\anmi8844\\EB_RACE\\file1.bin";
    private static String PATH_TO_FILE_2 = "C:\\Users\\anmi8844\\EB_RACE\\file2.bin";

    @Before
    public void clearPreferences()
    {
        PersistentLoadedOfflineFilesPreferences.clearTheList();
    }

    @Test
    public void testPreferencesAreEmpty()
    {
        String[] paths = PersistentLoadedOfflineFilesPreferences.getValueForLoadedFilesString();
        Assert.assertEquals( 0, paths.length );
    }

    @Test
    public void testPreferencesWithOneFile()
    {
        PersistentLoadedOfflineFilesPreferences.appendFileToRecentFilesList( PATH_TO_FILE_1 );
        String[] paths = PersistentLoadedOfflineFilesPreferences.getValueForLoadedFilesString();
        Assert.assertEquals( 1, paths.length );
        Assert.assertEquals( PATH_TO_FILE_1, paths[0] );
    }

    @Test
    public void testPreferencesWithTwoFiles()
    {
        PersistentLoadedOfflineFilesPreferences.appendFileToRecentFilesList( PATH_TO_FILE_1 );
        PersistentLoadedOfflineFilesPreferences.appendFileToRecentFilesList( PATH_TO_FILE_2 );
        String[] paths = PersistentLoadedOfflineFilesPreferences.getValueForLoadedFilesString();
        Assert.assertEquals( 2, paths.length );
        Assert.assertEquals( PATH_TO_FILE_1, paths[1] );
        Assert.assertEquals( PATH_TO_FILE_2, paths[0] );
    }
}
