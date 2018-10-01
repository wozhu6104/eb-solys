/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.validation.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.elektrobit.ebrace.validation.ValidationPlugin;

public class ValidationToolPreferenceInitializer extends AbstractPreferenceInitializer
{
    @Override
    public void initializeDefaultPreferences()
    {
        IPreferenceStore store = ValidationPlugin.getDefault().getPreferenceStore();
        store.setDefault( PreferencesConstants.VALIDATION_TOOL_PATH_TO_EXE_ID, "C:/" );
        store.setDefault( PreferencesConstants.VALIDATION_HEADLESS_RUN_ID, true );
    }
}
