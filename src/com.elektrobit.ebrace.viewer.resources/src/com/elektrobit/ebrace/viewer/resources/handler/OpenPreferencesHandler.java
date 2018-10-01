/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.resources.handler;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.handlers.HandlerUtil;

public class OpenPreferencesHandler extends BaseResourcesModelHandler
{
    private static final String PARAMETER_ID_PREFERENCE_PAGE_ID = "com.elektrobit.ebrace.viewer.chartengine.chartProperties";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        Object o = getSelection( event );
        final PreferenceDialog dialog = PreferencesUtil
                .createPropertyDialogOn( HandlerUtil.getActiveShell( event ),
                                         (IAdaptable)o,
                                         PARAMETER_ID_PREFERENCE_PAGE_ID,
                                         null,
                                         null );
        dialog.open();
        return null;
    }
}
