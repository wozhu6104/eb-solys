/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.chartengine.internal.handler;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;

import com.elektrobit.ebrace.core.interactor.api.analysisTimespan.AnalysisTimespanInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.analysisTimespan.AnalysisTimespanInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;

public class IncreaseAnalysisTimespanHandler implements IHandler, AnalysisTimespanInteractionCallback
{
    private final AnalysisTimespanInteractionUseCase interactionUC = UseCaseFactoryInstance.get()
            .makeAnalysisTimespanInteractionUseCase( this );

    @Override
    public void addHandlerListener(IHandlerListener handlerListener)
    {

    }

    @Override
    public void dispose()
    {
        interactionUC.unregister();
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        interactionUC.increaseAnalysisTimespan();
        return null;
    }

    @Override
    public boolean isEnabled()
    {
        return true;
    }

    @Override
    public boolean isHandled()
    {
        return true;
    }

    @Override
    public void removeHandlerListener(IHandlerListener handlerListener)
    {

    }

    @Override
    public void onAnalysisTimespanTextInputOutOfRange(int minSeconds, int maxSeconds)
    {
    }

    @Override
    public void onAnalysisTimespanTextInputInvalidFormat()
    {
    }
}
