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

import com.elektrobit.ebrace.core.interactor.api.loaddatachunk.LoadDataChunkNotifyCallback;
import com.elektrobit.ebrace.core.interactor.loaddatachunk.LoadDataChunkNotifyUseCaseImpl;
import com.elektrobit.ebrace.core.tracefile.api.LoadFileService;

import test.com.elektrobit.ebrace.core.interactor.UseCaseBaseTest;

public class LoadDataChunkNotifyUseCaseTest extends UseCaseBaseTest
{
    private LoadDataChunkNotifyUseCaseImpl usecase;
    private LoadDataChunkNotifyCallback callback;
    private LoadFileService loadFileService;

    @Before
    public void setup()
    {
        callback = Mockito.mock( LoadDataChunkNotifyCallback.class );
        loadFileService = Mockito.mock( LoadFileService.class );
        usecase = new LoadDataChunkNotifyUseCaseImpl( callback, loadFileService );
    }

    @Test
    public void loadFileProgressListenerRegisteredOnRegistration() throws Exception
    {
        Mockito.verify( loadFileService ).registerFileProgressListener( usecase );
    }

    @Test
    public void callbackCalledOnRegistration() throws Exception
    {
        Mockito.verify( loadFileService ).getChunkStartTimestamp();
        Mockito.verify( loadFileService ).getChunkEndTimestamp();
        Mockito.verify( loadFileService ).getStartTimestamp();
        Mockito.verify( loadFileService ).getEndTimestamp();
        Mockito.verify( callback ).onDataChunkChanged( 0, 0, 0, 0, 0L );
    }

    @Test
    public void callbackCalledOnLoadFileDone() throws Exception
    {
        usecase.onLoadFileDone( 1, 100, 1, 50 );

        Mockito.verify( callback ).onDataChunkChanged( 1, 100, 1, 50, 49L );
    }

    @Test
    public void loadFileProgressListenerUnregisteredOnUnregistration() throws Exception
    {
        usecase.unregister();

        Mockito.verify( loadFileService ).unregisterFileProgressListener( usecase );
    }

    @Test
    public void callbackNotCalledAfterUnregistration() throws Exception
    {
        usecase.unregister();

        usecase.onLoadFileDone( 1, 100, 1, 50 );

        Mockito.verify( callback, Mockito.times( 0 ) ).onDataChunkChanged( 1, 100, 1, 50, 49L );
    }
}
