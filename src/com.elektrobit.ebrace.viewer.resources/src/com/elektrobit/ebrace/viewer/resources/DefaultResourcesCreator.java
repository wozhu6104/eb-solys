/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.resources;

import org.osgi.service.component.annotations.Component;

import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.createresource.CreateResourceInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.createresource.CreateResourceInteractionUseCase;
import com.elektrobit.ebsolys.core.targetdata.api.reset.StartupDoneListener;

@Component
public class DefaultResourcesCreator implements StartupDoneListener, CreateResourceInteractionCallback
{
    private final CreateResourceInteractionUseCase createResourceUseCase = UseCaseFactoryInstance.get()
            .makeCreateResourceUseCase( this );

    @Override
    public void onApplicationStarted()
    {
        String docURL = new UserDoc().getDocURL();
        createResourceUseCase.createAndOpenHtmlView( "Quick Start", docURL );
        createResourceUseCase.unregister();
    }

    @Override
    public void onChartChannelsTypeMismatch()
    {
    }

    @Override
    public void onDerivedResourceAlreadyExists()
    {
    }

    @Override
    public void onProVersionNotAvailable()
    {
    }

}
