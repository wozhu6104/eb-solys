/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.script.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.elektrobit.ebrace.core.interactor.api.common.ProVersion;
import com.elektrobit.ebrace.viewer.common.UserMessageDialog;
import com.elektrobit.ebrace.viewer.script.wizard.NewScriptWizard;
import com.elektrobit.ebrace.viewer.script.wizard.NewScriptWizardNamePage;

public class CreateScriptHandler extends AbstractHandler
{

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        if (ProVersion.getInstance().isActive())
        {
            Shell activeShell = HandlerUtil.getActiveShell( event );
            StructuredSelection selection = (StructuredSelection)HandlerUtil.getCurrentSelection( event );
            IWorkbenchPart part = HandlerUtil.getActivePart( event );
            if (shouldOpenWizard( activeShell, part, selection ))
            {
                openNewXTendScriptWizard( activeShell, part );
            }
        }
        else
        {
            UserMessageDialog.UserProMessageDialog();
        }
        return null;
    }

    private void openNewXTendScriptWizard(Shell activeShell, IWorkbenchPart part)
    {
        NewScriptWizardNamePage page = new NewScriptWizardNamePage();
        NewScriptWizard wizard = new NewScriptWizard( page );
        WizardDialog dialog = new WizardDialog( activeShell, wizard )
        {
            @Override
            protected void configureShell(Shell newShell)
            {
                super.configureShell( newShell );
                newShell.setSize( 600, 620 );
                newShell.setMinimumSize( 600, 620 );
            }
        };
        dialog.create();
        dialog.open();
    }

    private boolean shouldOpenWizard(Shell activeShell, IWorkbenchPart part, StructuredSelection selection)
    {
        return selection instanceof StructuredSelection && activeShell != null && part != null;
    }
}
