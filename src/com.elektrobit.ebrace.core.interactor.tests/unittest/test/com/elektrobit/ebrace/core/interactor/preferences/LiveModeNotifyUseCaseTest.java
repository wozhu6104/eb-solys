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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.ServiceRegistration;

import com.elektrobit.ebrace.core.interactor.api.preferences.LiveModeNotifyCallback;
import com.elektrobit.ebrace.core.interactor.preferences.LiveModeNotifyUseCaseImpl;
import com.elektrobit.ebrace.core.preferences.api.UserInteractionPreferences;

import test.com.elektrobit.ebrace.core.interactor.UseCaseBaseTest;

public class LiveModeNotifyUseCaseTest extends UseCaseBaseTest
{
    private LiveModeNotifyUseCaseImpl sutLiveModeNotifyUseCase;
    private UserInteractionPreferences mockedUserInteractionPreferences;
    private LiveModeNotifyCallback mockedCallback;

    @Before
    public void setup()
    {
        mockedCallback = Mockito.mock( LiveModeNotifyCallback.class );
        mockedUserInteractionPreferences = Mockito.mock( UserInteractionPreferences.class );
        mockPreferencesWithValue( true );
        sutLiveModeNotifyUseCase = new LiveModeNotifyUseCaseImpl( mockedCallback, mockedUserInteractionPreferences );
    }

    private void mockPreferencesWithValue(boolean value)
    {
        Mockito.when( mockedUserInteractionPreferences.isLiveMode() ).thenReturn( value );

    }

    @Test
    public void initialValuePosted() throws Exception
    {
        Mockito.verify( mockedCallback ).onIsLiveModeChanged( true );
    }

    @Test
    public void updatedValuePosted() throws Exception
    {
        Mockito.reset( mockedCallback );
        sutLiveModeNotifyUseCase.onIsLiveModeChanged( true );
        Mockito.verify( mockedCallback ).onIsLiveModeChanged( true );
    }

    @Test
    public void testUnregister() throws Exception
    {
        Mockito.reset( mockedCallback );
        ServiceRegistration<?> mockedServiceRegistration = mock( ServiceRegistration.class );
        sutLiveModeNotifyUseCase.setServiceRegistration( mockedServiceRegistration );
        sutLiveModeNotifyUseCase.unregister();
        sutLiveModeNotifyUseCase.onIsLiveModeChanged( true );
        Mockito.verifyNoMoreInteractions( mockedCallback );
    }
}
