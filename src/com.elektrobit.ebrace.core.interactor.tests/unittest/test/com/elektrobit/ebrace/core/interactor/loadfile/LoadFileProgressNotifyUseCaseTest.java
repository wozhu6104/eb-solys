/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.interactor.loadfile;

import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.core.interactor.api.loadfile.LoadFileProgressNotifyCallback;
import com.elektrobit.ebrace.core.interactor.loadfile.LoadFileProgressNotifyUseCaseImpl;
import com.elektrobit.ebrace.core.tracefile.api.LoadFileService;

import test.com.elektrobit.ebrace.core.interactor.UseCaseBaseTest;

public class LoadFileProgressNotifyUseCaseTest extends UseCaseBaseTest
{
    public static final String FILE_NAME = "test.bin";

    @Test
    public void testLoadFileProgress() throws Exception
    {
        LoadFileService mockedFileService = Mockito.mock( LoadFileService.class );
        Mockito.when( mockedFileService.isFileLoading() ).thenReturn( true );
        Mockito.when( mockedFileService.isFileNotLoaded( FILE_NAME ) ).thenReturn( true );

        LoadFileProgressNotifyCallback mockedCallback = Mockito.mock( LoadFileProgressNotifyCallback.class );

        LoadFileProgressNotifyUseCaseImpl sut = new LoadFileProgressNotifyUseCaseImpl( mockedCallback,
                                                                                       FILE_NAME,
                                                                                       mockedFileService );

        Mockito.verify( mockedFileService ).registerFileProgressListener( sut );

        int percentDone = 5;
        sut.onLoadFileProgressChanged( percentDone );
        Mockito.verify( mockedCallback ).onLoadFileProgressChanged( percentDone );

        sut.onLoadFileDone( 0, 0, 0, 0 );
        Mockito.verify( mockedCallback ).onLoadFileDone( 0, 0, 0, 0 );
    }

    @Test
    public void testFileAlreadyLoaded() throws Exception
    {
        LoadFileService mockedFileService = Mockito.mock( LoadFileService.class );
        Mockito.when( mockedFileService.isFileNotLoaded( FILE_NAME ) ).thenReturn( false );
        Mockito.when( mockedFileService.isFileLoading() ).thenReturn( false );
        LoadFileProgressNotifyCallback mockedCallback = Mockito.mock( LoadFileProgressNotifyCallback.class );

        @SuppressWarnings("unused")
        LoadFileProgressNotifyUseCaseImpl sut = new LoadFileProgressNotifyUseCaseImpl( mockedCallback,
                                                                                       FILE_NAME,
                                                                                       mockedFileService );

        Mockito.verify( mockedCallback ).onLoadFileDone( Mockito.anyLong(),
                                                         Mockito.anyLong(),
                                                         Mockito.anyLong(),
                                                         Mockito.anyLong() );
    }

    @Test
    public void testFileLoadingAlreadyFailed() throws Exception
    {
        LoadFileService mockedFileService = Mockito.mock( LoadFileService.class );
        Mockito.when( mockedFileService.isFileNotLoaded( FILE_NAME ) ).thenReturn( true );
        Mockito.when( mockedFileService.isFileLoadingFailed( FILE_NAME ) ).thenReturn( true );
        Mockito.when( mockedFileService.isFileLoading() ).thenReturn( false );
        LoadFileProgressNotifyCallback mockedCallback = Mockito.mock( LoadFileProgressNotifyCallback.class );

        @SuppressWarnings("unused")
        LoadFileProgressNotifyUseCaseImpl sut = new LoadFileProgressNotifyUseCaseImpl( mockedCallback,
                                                                                       FILE_NAME,
                                                                                       mockedFileService );

        Mockito.verify( mockedCallback ).onLoadFileCanceled();
    }

    @Test
    public void testUnregister() throws Exception
    {
        LoadFileService mockedFileService = Mockito.mock( LoadFileService.class );
        Mockito.when( mockedFileService.isFileNotLoaded( FILE_NAME ) ).thenReturn( true );
        Mockito.when( mockedFileService.isFileLoading() ).thenReturn( true );

        LoadFileProgressNotifyCallback mockedCallback = Mockito.mock( LoadFileProgressNotifyCallback.class );

        LoadFileProgressNotifyUseCaseImpl sut = new LoadFileProgressNotifyUseCaseImpl( mockedCallback,
                                                                                       FILE_NAME,
                                                                                       mockedFileService );

        sut.unregister();
        sut.onLoadFileProgressChanged( 0 );
        sut.onLoadFileDone( 0, 0, 0, 0 );

        Mockito.verifyNoMoreInteractions( mockedCallback );
    }
}
