/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.application.statusline;

import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.elektrobit.ebrace.core.interactor.api.selectelement.StatusLineTextNotifyCallback;

public class StatusLineMessage implements StatusLineTextNotifyCallback
{

    public IActionBars getStatusLine()
    {

        IWorkbenchWindow win = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        IWorkbenchPage page = win.getActivePage();
        IWorkbenchPart part = page.getActivePart();
        IWorkbenchPartSite site = part.getSite();
        IActionBars actionBars = null;

        if (site instanceof IEditorSite)
        {
            IEditorSite esite = (IEditorSite)site;
            actionBars = esite.getActionBars();
            return actionBars;
        }

        if (site instanceof IViewSite)
        {
            IViewSite esite = (IViewSite)site;
            actionBars = esite.getActionBars();
            return actionBars;
        }

        return actionBars;
    }

    @Override
    public void onNewStatus(String status)
    {
        getStatusLine().getStatusLineManager().setMessage( status );
    }

}
