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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.core.interactor.api.preferences.PreferencesNotifyCallback;
import com.elektrobit.ebrace.core.interactor.preferences.PreferencesNotifyUseCaseImpl;
import com.elektrobit.ebrace.core.preferences.api.PreferencesService;

import test.com.elektrobit.ebrace.core.interactor.UseCaseBaseTest;

public class PreferencesNotifyUseCaseTest extends UseCaseBaseTest
{
    private PreferencesService preferencesService;
    private PreferencesNotifyCallback preferencesCallback;
    private final String DEFAULT_VALUE_TIMESTAMP_FORMAT = "HH:mm:ss.SSS";
    private PreferencesNotifyUseCaseImpl preferencesUsecase;

    @Before
    public void setup()
    {
        preferencesService = mock( PreferencesService.class );
        preferencesCallback = mock( PreferencesNotifyCallback.class );
        when( preferencesService.getTimestampFormatPreferences() ).thenReturn( DEFAULT_VALUE_TIMESTAMP_FORMAT );
        preferencesUsecase = new PreferencesNotifyUseCaseImpl( preferencesCallback, preferencesService );
    }

    @Test
    public void defaultValuePosted()
    {
        verify( preferencesCallback ).onTimestampFormatChanged( DEFAULT_VALUE_TIMESTAMP_FORMAT );
    }

    @Test
    public void updateAfterPreferencesChanged()
    {
        preferencesUsecase.onTimestampFormatPreferencesChanged();
        verify( preferencesCallback, times( 2 ) ).onTimestampFormatChanged( DEFAULT_VALUE_TIMESTAMP_FORMAT );
    }

    @Test
    public void unregistrationDone()
    {
        preferencesUsecase = new PreferencesNotifyUseCaseImpl( preferencesCallback, preferencesService );
        preferencesUsecase.unregister();
        verify( preferencesService ).unregisterPreferencesListener( preferencesUsecase );
    }
}
