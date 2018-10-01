/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.preferences.util;

import org.eclipse.jface.dialogs.IDialogSettings;

import com.elektrobit.ebrace.viewer.preferences.ViewerPreferencesPlugin;

public class PersistentLoadedOfflineFilesPreferences
{
    private static final String LOADED_FILES_LIST_SETTINGS_ID = "Loaded files";
    private static final String FILES_SEPARATOR = ";";

    private static IDialogSettings settings = ViewerPreferencesPlugin.getDefault().getDialogSettings();

    public static void appendFileToRecentFilesList(String path)
    {

        String loadedFiles = settings.get( LOADED_FILES_LIST_SETTINGS_ID );
        if (loadedFiles == null || loadedFiles.equals( "" ))
            loadedFiles = path;
        else
        {
            if (!loadedFiles.contains( path ))
                loadedFiles = path + ";" + loadedFiles;
        }
        settings.put( LOADED_FILES_LIST_SETTINGS_ID, loadedFiles );
    }

    public static String[] getValueForLoadedFilesString()
    {
        String value = settings.get( LOADED_FILES_LIST_SETTINGS_ID );
        if (value == null)
            return new String[0];
        else
            return value.split( FILES_SEPARATOR );
    }

    public static void clearTheList()
    {
        settings.put( LOADED_FILES_LIST_SETTINGS_ID, (String)null );
    }
}
