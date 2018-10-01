/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.util;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

import com.elektrobit.ebrace.viewer.common.editor.EventMapEditorInput;

public class OpenWorkbenchPartUtil
{
    private static final String EVENT_MAP_EDITOR_ID = "com.elektrobit.ebrace.viewer.eventmap.eventMapEditor";

    public static void openEventMapEditor(IWorkbenchPage page)
    {
        try
        {
            if (page != null)
                page.openEditor( new EventMapEditorInput(), EVENT_MAP_EDITOR_ID );
        }
        catch (PartInitException e)
        {
            System.out.println( "Problem" );
            e.printStackTrace();
        }
    }

}
