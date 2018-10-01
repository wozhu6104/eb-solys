/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.preferences.page;

import org.eclipse.jface.dialogs.IDialogSettings;

import com.elektrobit.ebrace.viewer.preferences.ViewerPreferencesPlugin;

public class DataFilesPreferences
{
    private static final String SAVE_DATA_FILES_PREFERENCES_KEY = "SaveDataFile";
    private static final int TRUE = 1;
    private static final int FALSE = 0;

    private static IDialogSettings settings = ViewerPreferencesPlugin.getDefault().getDialogSettings();

    public static boolean isSaveDataToFileActive()
    {
        try
        {
            return settings.getInt( SAVE_DATA_FILES_PREFERENCES_KEY ) == TRUE;
        }
        catch (NumberFormatException e)
        {
        }
        return true;
    }

    public static void setSaveDataToFileActive(boolean active)
    {
        settings.put( SAVE_DATA_FILES_PREFERENCES_KEY, active ? TRUE : FALSE );
    }
}
