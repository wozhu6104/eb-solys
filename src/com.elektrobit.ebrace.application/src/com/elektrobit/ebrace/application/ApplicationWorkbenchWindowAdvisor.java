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

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor
{

    public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer)
    {
        super( configurer );
    }

    @Override
    public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer)
    {
        return new ApplicationActionBarAdvisor( configurer );
    }

    @Override
    public void preWindowOpen()
    {
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        configurer.setShowCoolBar( true );
        configurer.setShowStatusLine( true );
        configurer.setShowProgressIndicator( false );
        configurer.setShowPerspectiveBar( true );
    }

    @Override
    public void openIntro()
    {
        IWorkbenchWindowConfigurer workbenchWindowConfigurer = getWindowConfigurer();
        IActionBarConfigurer actionBarConfigurer = workbenchWindowConfigurer.getActionBarConfigurer();
        IContributionItem[] items = actionBarConfigurer.getCoolBarManager().getItems();
        for (int i = 0; i < items.length; i++)
        {
            if (items[i].getId().equals( "org.eclipse.debug.ui.launch.toolbar" )
                    || items[i].getId().equals( "org.eclipse.debug.ui.main.toolbar" ))
                items[i].setVisible( false );
        }

        actionBarConfigurer.getCoolBarManager().update( true );

        super.openIntro();
    }

    @Override
    public void postWindowOpen()
    {
        super.postWindowOpen();

        IWorkbenchWindowConfigurer workbenchWindowConfigurer = getWindowConfigurer();
        IActionBarConfigurer actionBarConfigurer = workbenchWindowConfigurer.getActionBarConfigurer();
        IMenuManager menuManager = actionBarConfigurer.getMenuManager();

        IContributionItem[] menuItems = menuManager.getItems();
        for (int i = 0; i < menuItems.length; i++)
        {
            IContributionItem menuItem = menuItems[i];

            // Hack to remove the Run menu - it seems you cannot do this using the
            // "org.eclipse.ui.activities" extension
            if ("org.eclipse.ui.run".equals( menuItem.getId() ))
            {
                menuManager.remove( menuItem );
            }
        }

        menuManager.update( true );

        IPerspectiveDescriptor[] perspectives = PlatformUI.getWorkbench().getPerspectiveRegistry().getPerspectives();

        for (int i = 0; i < perspectives.length; i++)
        {
            if (perspectives[i].getId().contains( "Java" ))
            {
                PlatformUI.getWorkbench().getPerspectiveRegistry().deletePerspective( perspectives[i] );
            }
        }

    }

    @Override
    public void postWindowCreate()
    {
        super.postWindowCreate();
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        Shell shell = configurer.getWindow().getShell();
        shell.setMaximized( true );
    }
}
