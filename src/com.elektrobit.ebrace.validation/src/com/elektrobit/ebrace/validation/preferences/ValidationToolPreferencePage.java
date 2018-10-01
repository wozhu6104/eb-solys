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

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.elektrobit.ebrace.validation.ValidationPlugin;

public class ValidationToolPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{
    @Override
    public void init(IWorkbench workbench)
    {
        setPreferenceStore( ValidationPlugin.getDefault().getPreferenceStore() );
    }

    @Override
    protected void createFieldEditors()
    {
        addField( new FileFieldEditor( PreferencesConstants.VALIDATION_TOOL_PATH_TO_EXE_ID,
                                       "Path to executable: ",
                                       getFieldEditorParent() ) );
        addField( new BooleanFieldEditor( PreferencesConstants.VALIDATION_HEADLESS_RUN_ID,
                                          "Headless run ",
                                          getFieldEditorParent() ) );
    }
}
