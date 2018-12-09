/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.datainput;

import java.io.IOException;
import java.util.Set;

import com.elektrobit.ebrace.core.datainput.api.DataInputService;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.createresource.CreateConnectionInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.createresource.CreateConnectionInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.datainput.DataInputUseCase;
import com.elektrobit.ebrace.core.interactor.common.UseCaseExecutor;
import com.elektrobit.ebrace.core.interactor.common.UseCaseRunnable;

public class DataInputUseCaseImpl implements DataInputUseCase, CreateConnectionInteractionCallback
{
    private final CreateConnectionInteractionUseCase createConnectionInteractionUseCase = UseCaseFactoryInstance.get()
            .makeCreateConnectionInteractionUseCase( this );

    private final DataInputService service;

    public DataInputUseCaseImpl(DataInputService service)
    {
        this.service = service;
    }

    @Override
    public void loadDataInputsFromFile(String path) throws IOException
    {
        service.loadDataInputDescriptionsFromFile( path );
        service.getDataInputs().forEach( dataInput -> createConnectionInteractionUseCase.createDataInput( dataInput ) );
    }

    @Override
    public void startReading(String dataInputId)
    {
        UseCaseExecutor.schedule( new UseCaseRunnable( "DataInputUseCase.startReading",
                                                       () -> service.startReading( dataInputId ) ) );
    }

    @Override
    public void stopReading(String dataInputId)
    {
        service.stopReading( dataInputId );
    }

    @Override
    public void startReadingAllInputs()
    {
        UseCaseExecutor.schedule( new UseCaseRunnable( "DataInputUseCase.startReadingAllInputs",
                                                       () -> service.startReadingAllInputs() ) );

    }

    @Override
    public void stopReadingAllInputs()
    {
        service.stopReadingAllInputs();
    }

    @Override
    public Set<String> getAllAvailableDataInputs()
    {
        return service.getDataInputs();
    }

    @Override
    public void onPortInvalid(int port)
    {
    }
}
