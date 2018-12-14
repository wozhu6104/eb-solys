/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.ui.ecl.file;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.connect.ConnectionStateNotifyCallback;
import com.elektrobit.ebrace.viewer.common.constants.ViewIDs;

public class EverConnectedTester extends PropertyTester implements ConnectionStateNotifyCallback
{
    private final String PROPERTY_NAME_PREVIOUSLY_CONNECTED_MODE = "isEverConnected";

    public EverConnectedTester()
    {
        UseCaseFactoryInstance.get().makeConnectionStateNotifyUseCase( this );
    }

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue)
    {
        if (PROPERTY_NAME_PREVIOUSLY_CONNECTED_MODE.equals( property ))
        {
            return true;
        }
        return false;
    }

    /**
     * Calling IEvaluationService.requestEvaluation() has no effect on the properties controlling items in main toolbar
     * in Eclipse 3/4 mixed mode, but switching focus between the views triggers wanted evaluation.
     */
    private void switchFocusToForcePropertyReevaluation()
    {
        Display.getDefault().asyncExec( new Runnable()
        {

            @Override
            public void run()
            {
                IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                IViewPart channelsView = page.findView( ViewIDs.CHANNELS_VIEW_ID );
                IViewPart resourceExplorerView = page.findView( ViewIDs.RESOURCE_EXPLORER_VIEW_ID );
                IViewPart timemarkersView = page.findView( ViewIDs.TIMEMARKERS_VIEW_ID );

                if (channelsView != null)
                {
                    channelsView.setFocus();
                }
                if (resourceExplorerView != null)
                {
                    resourceExplorerView.setFocus();
                }
                if (timemarkersView != null)
                {
                    timemarkersView.setFocus();
                }
            }
        } );
    }

    @Override
    public void onTargetConnecting()
    {
    }

    @Override
    public void onTargetConnected()
    {
        switchFocusToForcePropertyReevaluation();
    }

    @Override
    public void onTargetDisconnected()
    {
        switchFocusToForcePropertyReevaluation();
    }

}
