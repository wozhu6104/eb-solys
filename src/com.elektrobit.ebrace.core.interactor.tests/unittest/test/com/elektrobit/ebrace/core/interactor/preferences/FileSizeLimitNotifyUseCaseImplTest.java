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

import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.core.interactor.api.preferences.FileSizeLimitNotifyCallback;
import com.elektrobit.ebrace.core.interactor.preferences.FileSizeLimitNotifyUseCaseImpl;
import com.elektrobit.ebsolys.core.targetdata.api.importer.FileSizeLimitService;

public class FileSizeLimitNotifyUseCaseImplTest
{
    @SuppressWarnings("unused")
    @Test
    public void postValueImmediately() throws Exception
    {
        FileSizeLimitService mockedService = Mockito.mock( FileSizeLimitService.class );
        Mockito.when( mockedService.getMaxSolysFileSizeMB() ).thenReturn( 400L );

        FileSizeLimitNotifyCallback mockedCallback = Mockito.mock( FileSizeLimitNotifyCallback.class );

        FileSizeLimitNotifyUseCaseImpl sut = new FileSizeLimitNotifyUseCaseImpl( mockedService, mockedCallback );

        Mockito.verify( mockedCallback ).onFileSizeLimitChanged( 400L );
    }
}
