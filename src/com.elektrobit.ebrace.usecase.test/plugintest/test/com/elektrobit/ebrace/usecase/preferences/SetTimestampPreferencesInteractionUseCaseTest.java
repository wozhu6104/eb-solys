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

import java.io.File;

import org.eclipse.core.runtime.Platform;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.preferences.SetPreferencesInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.preferences.SetTimestampPreferencesInteractionUseCase;
import com.elektrobit.ebrace.core.preferences.api.PreferencesService;
import com.elektrobit.ebrace.dev.test.util.services.CoreServiceHelper;

import junit.framework.Assert;
import test.com.elektrobit.ebrace.core.interactor.UseCaseBaseTest;

public class SetTimestampPreferencesInteractionUseCaseTest extends UseCaseBaseTest
{
    private SetTimestampPreferencesInteractionUseCase setTimestampPreferencesInteractionUseCase;
    private PreferencesService preferencesService;
    private final String TIMESTAMP_PATTERN = "H:mm:ss:SSS";

    @Before
    public void setup()
    {
        preferencesService = CoreServiceHelper.getPreferencesService();
        SetPreferencesInteractionCallback mockedSetPreferencesInteractionCallback = Mockito
                .mock( SetPreferencesInteractionCallback.class );
        setTimestampPreferencesInteractionUseCase = UseCaseFactoryInstance.get()
                .makeSetTimestampPreferencesInteractionUseCase( mockedSetPreferencesInteractionCallback );
    }

    @Test
    public void testSetTimestampFormatPreferences()
    {
        setTimestampPreferencesInteractionUseCase.setTimestampFormatPreferences( TIMESTAMP_PATTERN );
        Assert.assertEquals( TIMESTAMP_PATTERN, preferencesService.getTimestampFormatPreferences() );
    }

    @After
    public void cleanup()
    {
        String path = Platform.getLocation().toOSString() + File.separator + "properties.properties";

        if (path != null)
        {
            new File( path ).delete();
        }
    }
}
