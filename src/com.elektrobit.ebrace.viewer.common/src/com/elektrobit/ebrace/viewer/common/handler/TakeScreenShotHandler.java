/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.PlatformUI;

import com.elektrobit.ebrace.viewer.common.dialog.ImagePreviewDialog;

public class TakeScreenShotHandler extends AbstractHandler
{

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();

        Composite editorComposite = getEditorComposite();
        if (editorComposite != null)
        {
            ImagePreviewDialog imgprevDialog = new ImagePreviewDialog( shell, editorComposite );
            imgprevDialog.create();
            imgprevDialog.open();
        }
        else
        {
            showMessage( shell );
        }

        return null;
    }

    private void showMessage(Shell sh)
    {
        MessageBox box = new MessageBox( sh, SWT.ICON_INFORMATION );
        box.setMessage( "Please open a chart, graph or table to take a screenshot" );
        box.setText( "No content for screenshot available" );
        box.open();
    }

    private Composite getEditorComposite()
    {
        EModelService service = PlatformUI.getWorkbench().getService( EModelService.class );
        EPartService partService = PlatformUI.getWorkbench().getService( EPartService.class );
        MPart editorMPart = partService.findPart( "org.eclipse.e4.ui.compatibility.editor" );

        if (editorMPart != null)
        {
            MWindow topLevelWindowFor = service.getTopLevelWindowFor( editorMPart );
            for (MUIElement sharedElement : topLevelWindowFor.getSharedElements())
            {
                if (sharedElement.getElementId().equals( IPageLayout.ID_EDITOR_AREA ))
                {
                    Composite editorAreaComposite = (Composite)sharedElement.getWidget();
                    return editorAreaComposite;
                }
            }
        }
        return null;
    }

}
