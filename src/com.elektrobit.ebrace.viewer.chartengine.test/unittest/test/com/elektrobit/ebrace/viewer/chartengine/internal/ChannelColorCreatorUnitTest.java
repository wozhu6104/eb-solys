/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.viewer.chartengine.internal;

import org.eclipse.birt.chart.model.attribute.ColorDefinition;
import org.eclipse.birt.chart.model.attribute.impl.ColorDefinitionImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.core.interactor.api.channelcolor.ChannelColorUseCase;
import com.elektrobit.ebrace.viewer.chartengine.internal.ChannelColorCreator;
import com.elektrobit.ebsolys.core.targetdata.api.color.SColor;

public class ChannelColorCreatorUnitTest
{
    private ChannelColorUseCase mockedChannelColorUseCase;
    private ChannelColorCreator sutSeriesColorDefintionsCreator;

    @Before
    public void setUp()
    {
        mockedChannelColorUseCase = Mockito.mock( ChannelColorUseCase.class );
        sutSeriesColorDefintionsCreator = new ChannelColorCreator( mockedChannelColorUseCase );
    }

    @Test
    public void testGetColorDefinitionForSeries()
    {
        mockedChannelColorUseCase.setColorForChannel( "Channel", 255, 0, 0 );
        Mockito.when( mockedChannelColorUseCase.getColorOfChannel( "Channel" ) ).thenReturn( new SColor( 255, 0, 0 ) );
        ColorDefinition actualColor = sutSeriesColorDefintionsCreator.getColorDefinitionForSeries( "Channel" );

        Assert.assertEquals( actualColor.getRed(), ColorDefinitionImpl.create( 255, 0, 0 ).getRed() );
        Assert.assertEquals( actualColor.getGreen(), ColorDefinitionImpl.create( 255, 0, 0 ).getGreen() );
        Assert.assertEquals( actualColor.getBlue(), ColorDefinitionImpl.create( 255, 0, 0 ).getBlue() );
    }
}
