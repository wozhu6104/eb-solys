/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.interactor.loaddatachunk;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.core.interactor.api.loaddatachunk.LoadDataChunkInteractionCallback;
import com.elektrobit.ebrace.core.interactor.loaddatachunk.LoadDataChunkInteractionUseCaseImpl;
import com.elektrobit.ebrace.core.tracefile.api.LoadFileService;
import com.elektrobit.ebsolys.core.targetdata.api.reset.ClearChunkDataNotifier;

import test.com.elektrobit.ebrace.core.interactor.UseCaseBaseTest;

public class LoadDataChunkInteractionUseCaseTest extends UseCaseBaseTest
{
    private LoadDataChunkInteractionCallback callback;
    private LoadFileService loadFileService;
    private ClearChunkDataNotifier clearChunkNotifier;
    private LoadDataChunkInteractionUseCaseImpl usecase;

    @Before
    public void setup()
    {
        callback = Mockito.mock( LoadDataChunkInteractionCallback.class );
        loadFileService = Mockito.mock( LoadFileService.class );
        clearChunkNotifier = Mockito.mock( ClearChunkDataNotifier.class );
        usecase = new LoadDataChunkInteractionUseCaseImpl( callback, loadFileService, clearChunkNotifier );
    }

    @Test
    public void loadFileServiceTriggered() throws Exception
    {
        usecase.loadDataChunk( 0, 1000 );

        Mockito.verify( loadFileService ).loadFileFrom( 0, 1000L );
    }

    @Test
    public void callbackCalledOnSuccessfullFileLoading() throws Exception
    {
        Mockito.when( loadFileService.loadFileFrom( 0, 1000L ) ).thenReturn( true );

        usecase.loadDataChunk( 0, 1000L );

        Mockito.verify( callback ).onLoadDataFromSuccessful( 0, 0, 0, 0 );
    }

    @Test
    public void callbackCalledOnFailedFileLoading() throws Exception
    {
        Mockito.when( loadFileService.loadFileFrom( 0, 1000L ) ).thenReturn( false );

        usecase.loadDataChunk( 0, 1000L );

        Mockito.verify( callback ).onLoadDataFromFailed();
    }

    @Test
    public void callbackNotCalledAfterUnregistration() throws Exception
    {
        Mockito.when( loadFileService.loadFileFrom( 0, 1000L ) ).thenReturn( true );

        usecase.unregister();
        usecase.loadDataChunk( 0, 1000L );

        Mockito.verify( callback, Mockito.times( 0 ) ).onLoadDataFromSuccessful( 0, 0, 0, 0 );
    }

}
