/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.resources.connection;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.datainput.DataInputResourceModel;
import com.elektrobit.ebrace.core.interactor.api.datainput.DataInputUseCase;
import com.elektrobit.ebrace.viewer.resources.handler.BaseResourcesModelHandler;

public class StartReadingHandler extends BaseResourcesModelHandler
{

    DataInputUseCase uc = UseCaseFactoryInstance.get().makeDataInputUseCase();

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        Object selectedObject = getSelection( event );
        DataInputResourceModel target = (DataInputResourceModel)selectedObject;
        uc.startReading( target.getName() );
        target.setConnected( true );
        return null;
    }

}
