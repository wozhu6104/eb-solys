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

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

import com.elektrobit.ebrace.application.statusline.StatusLineMessage;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.selectelement.StatusLineTextNotifyUseCase;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor
{

    private IAction savePerspectiveAction;
    private IAction resetPerspectiveAction;
    private StatusLineTextNotifyUseCase statusLineTextNotifyUseCase;

    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer)
    {
        super( configurer );
    }

    @Override
    protected void makeActions(final IWorkbenchWindow window)
    {
        savePerspectiveAction = ActionFactory.SAVE_PERSPECTIVE.create( window );
        register( savePerspectiveAction );

        resetPerspectiveAction = ActionFactory.RESET_PERSPECTIVE.create( window );
        register( resetPerspectiveAction );
    }

    @Override
    protected void fillMenuBar(IMenuManager menuBar)
    {
    }

    @Override
    protected void fillStatusLine(IStatusLineManager statusLineManager)
    {
        super.fillStatusLine( statusLineManager );
        StatusLineMessage statusLineMessage = new StatusLineMessage( statusLineManager );
        statusLineTextNotifyUseCase = UseCaseFactoryInstance.get()
                .createStatusLineTextNotifyUseCase( statusLineMessage );
    }

    @Override
    public void dispose()
    {
        statusLineTextNotifyUseCase.unregister();
        super.dispose();
    }

}
