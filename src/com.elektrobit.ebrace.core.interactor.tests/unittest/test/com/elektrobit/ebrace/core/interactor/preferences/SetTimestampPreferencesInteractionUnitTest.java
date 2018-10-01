/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.interactor.preferences;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.common.time.format.TimeFormatter;
import com.elektrobit.ebrace.core.interactor.api.preferences.SetPreferencesInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.preferences.SetTimestampPreferencesInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.preferences.SetTimestampPreferencesInteractionUseCaseImpl;
import com.elektrobit.ebrace.core.preferences.api.PreferencesService;

import junit.framework.Assert;

public class SetTimestampPreferencesInteractionUnitTest
{
    private SetTimestampPreferencesInteractionUseCase setTimestampPreferencesInteractionUseCase;
    private PreferencesService mockedPreferencesService;
    private SetPreferencesInteractionCallback mockedCallback;

    private final String VALID_TIMESTAMP_FORMAT = "H:mm:ss:SSS";
    private final String INVALID_TIMESTAMP_FORMAT = "asadsfasf";
    private final String EMPTY_TIMESTAMP_FORMAT = "";

    @Before
    public void setup()
    {
        mockedCallback = Mockito.mock( SetPreferencesInteractionCallback.class );
        mockedPreferencesService = Mockito.mock( PreferencesService.class );

        setTimestampPreferencesInteractionUseCase = new SetTimestampPreferencesInteractionUseCaseImpl( mockedCallback,
                                                                                                       mockedPreferencesService );
    }

    @Test
    public void testSetTimestampFormatPreferences()
    {
        setTimestampPreferencesInteractionUseCase.setTimestampFormatPreferences( VALID_TIMESTAMP_FORMAT );
        Mockito.verify( mockedPreferencesService ).setTimestampFormatPreferences( "H:mm:ss:SSS" );
    }

    @Test
    public void testSetTimestampFormatPreferencesNotValid()
    {
        setTimestampPreferencesInteractionUseCase.setTimestampFormatPreferences( INVALID_TIMESTAMP_FORMAT );
        Mockito.verify( mockedCallback ).onCustomFormatNotValid();
    }

    @Test
    public void testGetTimestampFormatPreferences()
    {
        Mockito.when( mockedPreferencesService.getTimestampFormatPreferences() ).thenReturn( VALID_TIMESTAMP_FORMAT );
        Assert.assertEquals( VALID_TIMESTAMP_FORMAT,
                             setTimestampPreferencesInteractionUseCase.getTimestampFormatPreferences() );
    }

    @Test
    public void testIsTimestampFormatValid()
    {
        Assert.assertEquals( false,
                             setTimestampPreferencesInteractionUseCase
                                     .isTimestampFormatValid( EMPTY_TIMESTAMP_FORMAT ) );
        Assert.assertEquals( true,
                             setTimestampPreferencesInteractionUseCase
                                     .isTimestampFormatValid( VALID_TIMESTAMP_FORMAT ) );
        Assert.assertEquals( true,
                             setTimestampPreferencesInteractionUseCase
                                     .isTimestampFormatValid( TimeFormatter.TIMESTAMP_MICROSECONDS ) );
        Assert.assertEquals( true,
                             setTimestampPreferencesInteractionUseCase
                                     .isTimestampFormatValid( TimeFormatter.TIMESTAMP_MILLISECONDS ) );
    }

    @Test
    public void testUnregister()
    {
        setTimestampPreferencesInteractionUseCase.unregister();
        Mockito.verifyNoMoreInteractions( mockedCallback );
    }
}
