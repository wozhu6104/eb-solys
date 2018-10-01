/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.interactor.loaddatachunk;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.core.interactor.api.loaddatachunk.SystemCPUValuesNotifyCallback;
import com.elektrobit.ebrace.core.interactor.loaddatachunk.SystemCPUValuesNotifyUseCaseImpl;
import com.elektrobit.ebrace.core.tracefile.api.LoadFileService;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RTargetHeaderCPUValue;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.TargetHeaderMetaDataService;

import test.com.elektrobit.ebrace.core.interactor.UseCaseBaseTest;

public class SystemCPUValuesNotifyUseCaseTest extends UseCaseBaseTest implements SystemCPUValuesNotifyCallback
{
    private List<RTargetHeaderCPUValue> systemCPUValues;
    private TargetHeaderMetaDataService targetHeaderMetaDataService;
    private SystemCPUValuesNotifyUseCaseImpl usecase;

    @Before
    public void setup()
    {
        targetHeaderMetaDataService = Mockito.mock( TargetHeaderMetaDataService.class );

        usecase = new SystemCPUValuesNotifyUseCaseImpl( this,
                                                        targetHeaderMetaDataService,
                                                        Mockito.mock( LoadFileService.class ) );
    }

    @Test
    public void getDataOnRegistering() throws Exception
    {
        Assert.assertNotNull( systemCPUValues );
    }

    @Override
    public void onSystemCPUValuesUpdated(List<RTargetHeaderCPUValue> systemCPUValues)
    {
        this.systemCPUValues = systemCPUValues;
    }

    @Test
    public void getCorrectCPUValuesOnFileLoadingDone() throws Exception
    {
        List<RTargetHeaderCPUValue> expectedSystemCPUValues = Arrays.asList( new RTargetHeaderCPUValue( 100, 0.1 ),
                                                                             new RTargetHeaderCPUValue( 200, 0.5 ) );

        Mockito.when( targetHeaderMetaDataService.getTargetHeaderCPUValues() ).thenReturn( expectedSystemCPUValues );

        usecase.onLoadFileDone( 0, 0, 0, 0 );

        Assert.assertEquals( expectedSystemCPUValues, systemCPUValues );

    }

}
