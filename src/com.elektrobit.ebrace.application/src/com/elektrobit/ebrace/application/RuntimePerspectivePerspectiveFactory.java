/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.application;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;

import com.elektrobit.ebrace.viewer.common.constants.ViewIDs;

public class RuntimePerspectivePerspectiveFactory implements IPerspectiveFactory
{
    @Override
    public void createInitialLayout(IPageLayout layout)
    {
        layout.setEditorAreaVisible( true );

        // channel view
        layout.addView( ViewIDs.CHANNELS_VIEW_ID, IPageLayout.LEFT, 0.22f, IPageLayout.ID_EDITOR_AREA );
        layout.getViewLayout( ViewIDs.CHANNELS_VIEW_ID ).setCloseable( false );

        // resource explorer
        layout.addView( ViewIDs.RESOURCE_EXPLORER_VIEW_ID, IPageLayout.BOTTOM, 0.45f, ViewIDs.CHANNELS_VIEW_ID );
        layout.getViewLayout( ViewIDs.RESOURCE_EXPLORER_VIEW_ID ).setCloseable( false );

        IFolderLayout bottomLeftFolder = layout.createFolder( ViewIDs.RUNTIME_PERSP_BOTTOM_LEFT_FOLDER_ID,
                                                              IPageLayout.BOTTOM,
                                                              0.6f,
                                                              ViewIDs.RESOURCE_EXPLORER_VIEW_ID );

        // timemarker view
        bottomLeftFolder.addView( ViewIDs.TIMEMARKERS_VIEW_ID );
        layout.getViewLayout( ViewIDs.TIMEMARKERS_VIEW_ID ).setCloseable( false );

        // properties view
        bottomLeftFolder.addView( ViewIDs.PROPERTIES_VIEW_ID );
        layout.getViewLayout( ViewIDs.PROPERTIES_VIEW_ID ).setCloseable( false );

        layout.addStandaloneViewPlaceholder( ViewIDs.SLIDING_WINDOW_VIEW_ID,
                                             IPageLayout.TOP,
                                             0.04f,
                                             IPageLayout.ID_EDITOR_AREA,
                                             false );

        layout.addStandaloneViewPlaceholder( IConsoleConstants.ID_CONSOLE_VIEW,
                                             IPageLayout.BOTTOM,
                                             0.74f,
                                             IPageLayout.ID_EDITOR_AREA,
                                             true );
    }
}
