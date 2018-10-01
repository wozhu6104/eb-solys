/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.interactor.splitfile;

import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.core.interactor.api.splitfile.SplitFileInteractionCallback;
import com.elektrobit.ebrace.core.interactor.splitfile.SplitFileInteractionUseCaseImpl;
import com.elektrobit.ebrace.core.tracefile.api.SplitFileService;

import test.com.elektrobit.ebrace.core.interactor.UseCaseBaseTest;

public class SplitFileInteractionUseCaseTest extends UseCaseBaseTest
{
    private final String PATH = "D:";

    @Test
    public void testOnSplittingStarted() throws Exception
    {
        SplitFileService splitFileService = Mockito.mock( SplitFileService.class );
        SplitFileInteractionCallback splitFileInteractionCallback = Mockito.mock( SplitFileInteractionCallback.class );

        SplitFileInteractionUseCaseImpl sut = new SplitFileInteractionUseCaseImpl( splitFileInteractionCallback,
                                                                                   splitFileService );
        sut.startSplitting( PATH );
        sut.onSplittingStarted();

        Mockito.verify( splitFileService ).registerSplitDoneListener( sut );
        Mockito.verify( splitFileInteractionCallback ).onFileSplittingStarted();
    }

    @Test
    public void testOnSplittingDone() throws Exception
    {
        SplitFileService splitFileService = Mockito.mock( SplitFileService.class );
        SplitFileInteractionCallback splitFileInteractionCallback = Mockito.mock( SplitFileInteractionCallback.class );

        SplitFileInteractionUseCaseImpl sut = new SplitFileInteractionUseCaseImpl( splitFileInteractionCallback,
                                                                                   splitFileService );
        sut.startSplitting( PATH );
        sut.onSplittingStarted();
        sut.onSplittingDone();

        Mockito.verify( splitFileService ).registerSplitDoneListener( sut );
        Mockito.verify( splitFileInteractionCallback, Mockito.times( 1 ) ).onFileSplittingStarted();
        Mockito.verify( splitFileInteractionCallback, Mockito.times( 1 ) ).onFileSplittingDone();

    }

    @Test
    public void testOnSplittingError() throws Exception
    {
        String errorMsg = "message";
        SplitFileService splitFileService = Mockito.mock( SplitFileService.class );
        SplitFileInteractionCallback splitFileInteractionCallback = Mockito.mock( SplitFileInteractionCallback.class );

        SplitFileInteractionUseCaseImpl sut = new SplitFileInteractionUseCaseImpl( splitFileInteractionCallback,
                                                                                   splitFileService );
        sut.startSplitting( PATH );
        sut.onSplittingStarted();
        sut.onSplittingError( errorMsg );;

        Mockito.verify( splitFileService ).registerSplitDoneListener( sut );
        Mockito.verify( splitFileInteractionCallback, Mockito.times( 1 ) ).onFileSplittingStarted();
        Mockito.verify( splitFileInteractionCallback, Mockito.times( 1 ) ).onFileSplittingError( errorMsg );

    }
}
