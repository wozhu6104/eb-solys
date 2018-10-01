/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.interactor.analysisTimespan;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.core.interactor.analysisTimespan.AnalysisTimespanInteractionUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.api.analysisTimespan.AnalysisTimespanInteractionCallback;
import com.elektrobit.ebrace.core.preferences.api.AnalysisTimespanPreferences;

public class AnalysisTimespanInteractionUseCaseImplTest
{
    private AnalysisTimespanInteractionCallback mockedCallback;
    private AnalysisTimespanPreferences mockedPreferences;
    private AnalysisTimespanInteractionUseCaseImpl sutAnalysisTimespanInteractionUseCase;

    @Before
    public void setup()
    {
        mockedCallback = Mockito.mock( AnalysisTimespanInteractionCallback.class );
        mockedPreferences = Mockito.mock( AnalysisTimespanPreferences.class );
        sutAnalysisTimespanInteractionUseCase = new AnalysisTimespanInteractionUseCaseImpl( mockedCallback,
                                                                                            mockedPreferences );
    }

    private void setupPreferences(long analysisTimespanLengthSeconds)
    {
        Mockito.when( mockedPreferences.getAnalysisTimespanLength() )
                .thenReturn( analysisTimespanLengthSeconds * 1000000 );
    }

    private void verifyAnalysisTimespanLengthSet(long analysisTimespanLengthSeconds)
    {
        Mockito.verify( mockedPreferences ).setAnalysisTimespanLength( analysisTimespanLengthSeconds * 1000000L );
    }

    @Test
    public void testIncrease() throws Exception
    {
        setupPreferences( 5 );
        sutAnalysisTimespanInteractionUseCase.increaseAnalysisTimespan();
        verifyAnalysisTimespanLengthSet( 6 );
    }

    @Test
    public void testIncreaseOverLimit() throws Exception
    {
        setupPreferences( AnalysisTimespanInteractionUseCaseImpl.ANALYSIS_TIMESPAN_SECONDS_MAX );
        sutAnalysisTimespanInteractionUseCase.increaseAnalysisTimespan();
        Mockito.verify( mockedPreferences, Mockito.atLeastOnce() ).getAnalysisTimespanLength();
        Mockito.verifyNoMoreInteractions( mockedPreferences );
    }

    @Test
    public void testDecrease() throws Exception
    {
        setupPreferences( 5 );
        sutAnalysisTimespanInteractionUseCase.decreaseAnalysisTimespan();
        verifyAnalysisTimespanLengthSet( 4 );
    }

    @Test
    public void testDecreaseUnderLimit() throws Exception
    {
        setupPreferences( AnalysisTimespanInteractionUseCaseImpl.ANALYSIS_TIMESPAN_SECONDS_MIN );
        sutAnalysisTimespanInteractionUseCase.decreaseAnalysisTimespan();
        Mockito.verify( mockedPreferences, Mockito.atLeastOnce() ).getAnalysisTimespanLength();
        Mockito.verifyNoMoreInteractions( mockedPreferences );
    }

    @Test
    public void testTextInputWithoutS() throws Exception
    {
        sutAnalysisTimespanInteractionUseCase.setAnalysisTimespan( "20" );
        verifyAnalysisTimespanLengthSet( 20 );
    }

    @Test
    public void testTextInputWithS() throws Exception
    {
        sutAnalysisTimespanInteractionUseCase.setAnalysisTimespan( "20s" );
        verifyAnalysisTimespanLengthSet( 20 );
    }

    @Test
    public void testTextInputWithSandSpace() throws Exception
    {
        sutAnalysisTimespanInteractionUseCase.setAnalysisTimespan( "20 s" );
        verifyAnalysisTimespanLengthSet( 20 );
    }

    @Test
    public void testTextInputOutOfRangeMax() throws Exception
    {
        sutAnalysisTimespanInteractionUseCase.setAnalysisTimespan( "2000" );
        verifyAnalysisTimespanLengthSet( AnalysisTimespanInteractionUseCaseImpl.ANALYSIS_TIMESPAN_SECONDS_MAX );
        Mockito.verify( mockedCallback )
                .onAnalysisTimespanTextInputOutOfRange( AnalysisTimespanInteractionUseCaseImpl.ANALYSIS_TIMESPAN_SECONDS_MIN,
                                                        AnalysisTimespanInteractionUseCaseImpl.ANALYSIS_TIMESPAN_SECONDS_MAX );
    }

    @Test
    public void testTextInputOutOfRangeMin() throws Exception
    {
        sutAnalysisTimespanInteractionUseCase.setAnalysisTimespan( "0" );
        verifyAnalysisTimespanLengthSet( AnalysisTimespanInteractionUseCaseImpl.ANALYSIS_TIMESPAN_SECONDS_MIN );
        Mockito.verify( mockedCallback )
                .onAnalysisTimespanTextInputOutOfRange( AnalysisTimespanInteractionUseCaseImpl.ANALYSIS_TIMESPAN_SECONDS_MIN,
                                                        AnalysisTimespanInteractionUseCaseImpl.ANALYSIS_TIMESPAN_SECONDS_MAX );
    }

    @Test
    public void testTextInpuInvalidValue() throws Exception
    {
        sutAnalysisTimespanInteractionUseCase.setAnalysisTimespan( "abc" );
        Mockito.verifyNoMoreInteractions( mockedPreferences );
        Mockito.verify( mockedCallback ).onAnalysisTimespanTextInputInvalidFormat();
    }

    @Test
    public void testTextInpuInvalidEmptyString() throws Exception
    {
        sutAnalysisTimespanInteractionUseCase.setAnalysisTimespan( "" );
        Mockito.verifyNoMoreInteractions( mockedPreferences );
        Mockito.verify( mockedCallback ).onAnalysisTimespanTextInputInvalidFormat();
    }
}
