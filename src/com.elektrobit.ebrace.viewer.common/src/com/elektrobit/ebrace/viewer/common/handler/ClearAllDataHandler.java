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

import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.reset.ClearAllDataInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.reset.ClearAllDataInteractionUseCase;

public class ClearAllDataHandler extends AbstractHandler implements ClearAllDataInteractionCallback
{
    private final ClearAllDataInteractionUseCase uc = UseCaseFactoryInstance.get().makeClearAllDataInteractionUseCase( this );

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        uc.reset();
        return null;
    }

    @Override
    public void onResetDone()
    {
    }
}
