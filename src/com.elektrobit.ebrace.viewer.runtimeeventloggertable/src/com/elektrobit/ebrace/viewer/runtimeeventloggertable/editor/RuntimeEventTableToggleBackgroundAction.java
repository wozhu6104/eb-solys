/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.runtimeeventloggertable.editor;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.elektrobit.ebrace.core.interactor.api.resources.model.table.TableModel;
import com.elektrobit.ebrace.viewer.common.ViewerCommonPlugin;

public class RuntimeEventTableToggleBackgroundAction extends Action
{
    private static final String TOOLTIP_TEXT = "Toggle table background";
    private static final String PATH_TO_TOGGLE_IMAGE = "icons/table_background_toggle_on.png";

    private final RuntimeEventLoggerTableEditor parent;

    public RuntimeEventTableToggleBackgroundAction(RuntimeEventLoggerTableEditor parent)
    {
        super( "", IAction.AS_CHECK_BOX );

        this.parent = parent;

        initializeAction();
    }

    @Override
    public void run()
    {
        if (isCalledFromTable())
        {
            parent.toggleTableColor();
        }
    }

    private void initializeAction()
    {
        setToolTipText( TOOLTIP_TEXT );

        ImageDescriptor originalImageDescriptor = ViewerCommonPlugin.getDefault()
                .getImageDescriptor( PATH_TO_TOGGLE_IMAGE );
        setImageDescriptor( originalImageDescriptor );
        setChecked( ((TableModel)parent.getModel()).isBackgroundEnabled() );
    }

    private boolean isCalledFromTable()
    {
        IWorkbench workbench = PlatformUI.getWorkbench();
        IWorkbenchWindow activeWindow = workbench.getActiveWorkbenchWindow();
        IWorkbenchPage activePage = activeWindow.getActivePage();
        IWorkbenchPart activePart = activePage.getActivePart();

        if (activePart instanceof RuntimeEventLoggerTableEditor)
        {
            return true;
        }

        return false;
    }
}
