/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.usecase.preferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.preferences.SetColorPreferencesInteractionUseCase;
import com.elektrobit.ebrace.core.preferences.api.PreferencesService;
import com.elektrobit.ebrace.dev.test.util.services.CoreServiceHelper;
import com.elektrobit.ebsolys.core.targetdata.api.color.SColor;

import junit.framework.Assert;
import test.com.elektrobit.ebrace.core.interactor.UseCaseBaseTest;

public class SetColorPreferencesInteractionUseCaseTest extends UseCaseBaseTest
{
    private SetColorPreferencesInteractionUseCase setColorPreferencesInteractionUseCaseTest;
    private PreferencesService preferencesService;

    @Before
    public void setup()
    {
        preferencesService = CoreServiceHelper.getPreferencesService();
        setColorPreferencesInteractionUseCaseTest = UseCaseFactoryInstance.get().makeSetColorPreferencesInteractionUseCase();
    }

    @Test
    public void testSetColorPreferences()
    {
        List<SColor> raceColors = new ArrayList<SColor>( Arrays
                .asList( new SColor( 166, 206, 227 ),
                         new SColor( 31, 120, 180 ),
                         new SColor( 178, 223, 138 ),
                         new SColor( 51, 160, 44 ),
                         new SColor( 255, 140, 10 ),
                         new SColor( 227, 26, 28 ) ) );

        setColorPreferencesInteractionUseCaseTest.setColorPreferences( raceColors );
        Assert.assertEquals( raceColors, preferencesService.getColorPreferences() );
    }

    @Test
    public void testSetColorTranspPreferences()
    {
        double colorsTransp = 0.7;
        setColorPreferencesInteractionUseCaseTest.setColorTranspPreferences( colorsTransp );
        Assert.assertEquals( colorsTransp, preferencesService.getColorTransparencyValue() );
    }

}
