/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.datamanager.internal.runtime.event;

import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.elektrobit.ebrace.core.datamanager.internal.runtime.event.TargetHeaderMetaDataServiceImpl;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RTargetHeaderCPUValue;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RTargetHeaderMetaData;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.TargetHeaderMetaDataService.MetaDataKeys;

public class TargetHeaderMetaDataServiceTest
{
    @Ignore
    @Test
    public void computedCPUValuesCorrect() throws Exception
    {
        TargetHeaderMetaDataServiceImpl metaDataService = new TargetHeaderMetaDataServiceImpl();

        metaDataService
                .addMetaData( new RTargetHeaderMetaData( 1000000, MetaDataKeys.SYSTEM_CPU_VALUES.toString(), "100" ) );
        metaDataService
                .addMetaData( new RTargetHeaderMetaData( 2000000, MetaDataKeys.SYSTEM_CPU_VALUES.toString(), "200" ) );
        metaDataService
                .addMetaData( new RTargetHeaderMetaData( 2500000, MetaDataKeys.SYSTEM_CPU_VALUES.toString(), "219" ) );

        List<RTargetHeaderCPUValue> cpuValues = metaDataService.getTargetHeaderCPUValues();

        Assert.assertEquals( new RTargetHeaderCPUValue( 2000000, 10.0 ), cpuValues.get( 0 ) );
        Assert.assertEquals( new RTargetHeaderCPUValue( 2500000, 3.8 ), cpuValues.get( 1 ) );
    }

    @Ignore
    @Test
    public void ignoreNonCPUValues() throws Exception
    {
        TargetHeaderMetaDataServiceImpl metaDataService = new TargetHeaderMetaDataServiceImpl();

        metaDataService
                .addMetaData( new RTargetHeaderMetaData( 1000000, MetaDataKeys.SYSTEM_CPU_VALUES.toString(), "100" ) );
        metaDataService.addMetaData( new RTargetHeaderMetaData( 1111, "mem.system", "111" ) );
        metaDataService
                .addMetaData( new RTargetHeaderMetaData( 2000000, MetaDataKeys.SYSTEM_CPU_VALUES.toString(), "200" ) );
        metaDataService
                .addMetaData( new RTargetHeaderMetaData( 2500000, MetaDataKeys.SYSTEM_CPU_VALUES.toString(), "219" ) );

        List<RTargetHeaderCPUValue> cpuValues = metaDataService.getTargetHeaderCPUValues();

        Assert.assertEquals( new RTargetHeaderCPUValue( 2000000, 10.0 ), cpuValues.get( 0 ) );
        Assert.assertEquals( new RTargetHeaderCPUValue( 2500000, 3.8 ), cpuValues.get( 1 ) );
    }

    @Ignore
    @Test
    public void ignoreNonParsableValue() throws Exception
    {
        TargetHeaderMetaDataServiceImpl metaDataService = new TargetHeaderMetaDataServiceImpl();

        metaDataService
                .addMetaData( new RTargetHeaderMetaData( 1000000, MetaDataKeys.SYSTEM_CPU_VALUES.toString(), "100" ) );
        metaDataService.addMetaData( new RTargetHeaderMetaData( 1111,
                                                                MetaDataKeys.SYSTEM_CPU_VALUES.toString(),
                                                                "no-valid-value" ) );
        metaDataService
                .addMetaData( new RTargetHeaderMetaData( 2000000, MetaDataKeys.SYSTEM_CPU_VALUES.toString(), "200" ) );
        metaDataService
                .addMetaData( new RTargetHeaderMetaData( 2500000, MetaDataKeys.SYSTEM_CPU_VALUES.toString(), "219" ) );

        List<RTargetHeaderCPUValue> cpuValues = metaDataService.getTargetHeaderCPUValues();

        Assert.assertEquals( new RTargetHeaderCPUValue( 2000000, 10.0 ), cpuValues.get( 0 ) );
        Assert.assertEquals( new RTargetHeaderCPUValue( 2500000, 3.8 ), cpuValues.get( 1 ) );
    }

}
