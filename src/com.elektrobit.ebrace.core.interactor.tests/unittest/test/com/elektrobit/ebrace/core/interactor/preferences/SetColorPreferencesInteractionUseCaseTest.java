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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.core.interactor.api.preferences.SetColorPreferencesInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.preferences.SetColorPreferencesInteractionUseCaseImpl;
import com.elektrobit.ebrace.core.preferences.api.PreferencesService;
import com.elektrobit.ebsolys.core.targetdata.api.color.SColor;

import junit.framework.Assert;

public class SetColorPreferencesInteractionUseCaseTest
{
    private SetColorPreferencesInteractionUseCase setColorPreferencesInteractionUseCaseTest;
    private PreferencesService mockedPreferencesService;

    private final List<SColor> raceColors = new ArrayList<SColor>( Arrays
            .asList( new SColor( 166, 206, 227 ),
                     new SColor( 31, 120, 180 ),
                     new SColor( 178, 223, 138 ),
                     new SColor( 51, 160, 44 ),
                     new SColor( 255, 140, 10 ),
                     new SColor( 227, 26, 28 ) ) );
    double colorsTransp = 0.7;

    @Before
    public void setup()
    {
        mockedPreferencesService = Mockito.mock( PreferencesService.class );

        setColorPreferencesInteractionUseCaseTest = new SetColorPreferencesInteractionUseCaseImpl( mockedPreferencesService );
    }

    @Test
    public void testStorePreferenceForKeyForColors()
    {
        setColorPreferencesInteractionUseCaseTest.setColorPreferences( raceColors );
        Mockito.verify( mockedPreferencesService, Mockito.times( 1 ) ).setColorPreferences( raceColors );
    }

    @Test
    public void testStorePreferenceForKeyForColorTransparency()
    {

        setColorPreferencesInteractionUseCaseTest.setColorTranspPreferences( colorsTransp );
        Mockito.verify( mockedPreferencesService, Mockito.times( 1 ) ).setColorTransparencyPreferences( colorsTransp );
    }

    @Test
    public void testGetColorTransparencyPreferences()
    {
        Mockito.when( mockedPreferencesService.getColorTransparencyValue() ).thenReturn( colorsTransp );
        Assert.assertEquals( colorsTransp, mockedPreferencesService.getColorTransparencyValue() );
    }

    @Test
    public void testGetColorPreferences()
    {
        Mockito.when( mockedPreferencesService.getColorPreferences() ).thenReturn( raceColors );
        Assert.assertEquals( raceColors, mockedPreferencesService.getColorPreferences() );
    }
}
