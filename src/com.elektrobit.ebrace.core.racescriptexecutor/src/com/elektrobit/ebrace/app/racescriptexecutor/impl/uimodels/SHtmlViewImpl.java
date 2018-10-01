/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.app.racescriptexecutor.impl.uimodels;

import java.util.ArrayList;
import java.util.List;

import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.htmlview.HtmlViewModel;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;
import com.elektrobit.ebsolys.script.external.SHtmlView;

public class SHtmlViewImpl implements SHtmlView
{
    private final HtmlViewModel model;

    private final ResourcesModelManager resourcesModelManager;

    public SHtmlViewImpl(HtmlViewModel model, ResourcesModelManager resourcesModelManager)
    {
        this.model = model;
        this.resourcesModelManager = resourcesModelManager;
    }

    @Override
    public SHtmlView setContent(String text)
    {
        UseCaseFactoryInstance.get().makeSetHtmlViewContentUseCase().setContent( model, text );
        return this;
    }

    @Override
    public void delete()
    {
        List<ResourceModel> viewInList = new ArrayList<ResourceModel>();
        viewInList.add( model );
        resourcesModelManager.deleteResourcesModels( viewInList );
    }

    @Override
    public SHtmlView callJavaScriptFunction(String function, String arg)
    {
        UseCaseFactoryInstance.get().makeSetHtmlViewContentUseCase().callJavaScriptFunction( model, function, arg );
        return this;
    }

    @Override
    public SHtmlView setName(String newName)
    {
        model.setName( newName );
        return this;
    }
}
