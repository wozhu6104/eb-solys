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

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceTreeNode;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourcesFolder;
import com.elektrobit.ebrace.core.interactor.api.resources.tree.ResouceTreeNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.resources.tree.ResourceTreeNotifyCallback;
import com.elektrobit.ebrace.viewer.resources.util.OpenEditorUtil;

public class QuickStartViewHandler extends AbstractHandler implements ResourceTreeNotifyCallback
{
    private ResourceModel quickStartResourceModel;
    private final ResouceTreeNotifyUseCase resouceTreeNotifyUseCase = UseCaseFactoryInstance.get()
            .makeResouceTreeNotifyUseCase( this );

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        if (quickStartResourceModel != null)
        {
            IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
            OpenEditorUtil.openResourcesEditor( activePage, quickStartResourceModel );
        }
        return null;
    }

    @Override
    public void onNewResourceTreeData(List<ResourcesFolder> folders)
    {
        for (ResourcesFolder nextFolder : folders)
        {
            if (nextFolder.getName().toLowerCase().equals( "html views" ))
            {
                for (ResourceTreeNode model : nextFolder.getChildren())
                {
                    if (model.getName().toLowerCase().equals( "quick start" ) && model instanceof ResourceModel)
                    {
                        this.quickStartResourceModel = (ResourceModel)model;
                    }
                }
            }
        }
    }

    @Override
    public void openResource(ResourceModel resourceModel)
    {

    }

    @Override
    public void revealResource(ResourceModel resourceModel)
    {
        if (quickStartResourceModel.equals( resourceModel ))
        {
            quickStartResourceModel = null;
        }
    }

    @Override
    protected void finalize() throws Throwable
    {
        resouceTreeNotifyUseCase.unregister();
        super.finalize();
    }
}
