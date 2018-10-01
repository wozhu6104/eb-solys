/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.interactor.channelColor.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.core.interactor.api.channelcolor.ChannelColorCallback;
import com.elektrobit.ebrace.core.interactor.api.channelcolor.ChannelColorUseCase;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.resources.model.chart.ChartModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.chart.ChartTypes;
import com.elektrobit.ebrace.dev.test.util.services.CoreServiceHelper;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;
import com.elektrobit.ebsolys.core.targetdata.api.color.ColorSettingsPreferenceConstants;
import com.elektrobit.ebsolys.core.targetdata.api.color.SColor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

import test.com.elektrobit.ebrace.core.interactor.UseCaseBaseTest;

public class ChannelColorUseCaseTest extends UseCaseBaseTest
{
    private final RuntimeEventAcceptor runtimeEventAcceptorService = CoreServiceHelper.getRuntimeEventAcceptor();
    private ResourcesModelManager resourceManager;
    private ChannelColorUseCase usecase;
    private ChannelColorCallback callback;

    @Captor
    private ArgumentCaptor<Collection<RuntimeEventChannel<?>>> channelsCaptor;

    @Before
    public void resetRuntimeEventAcceptor()
    {
        MockitoAnnotations.initMocks( this );

        resourceManager = new GenericOSGIServiceTracker<ResourcesModelManager>( ResourcesModelManager.class )
                .getService();
        callback = mock( ChannelColorCallback.class );
        usecase = UseCaseFactoryInstance.get().makeChannelColorUseCase( callback );
    }

    private RuntimeEventChannel<?> makeChannel(String name)
    {
        return runtimeEventAcceptorService.createOrGetRuntimeEventChannel( name, Unit.COUNT, "description" );
    }

    @Test
    public void verifySetColorForChannelNotifiesListener()
    {
        RuntimeEventChannel<?> channel = makeChannel( "Channel1" );

        boolean hasColor = usecase.channelHasColor( channel );
        assertFalse( hasColor );
        verify( callback, times( 0 ) ).onColorAssigned( null );

        usecase.setColorForChannel( channel, 255, 0, 255 );
        assertTrue( usecase.channelHasColor( channel ) );

        verify( callback, times( 1 ) ).onColorAssigned( channelsCaptor.capture() );
        // assertTrue( argument.getValue().contains( channel ) );
    }

    @Test
    public void createChartWithOneChannelSetsDefaultColor()
    {
        SColor firstDefaultColor = ColorSettingsPreferenceConstants.defaultChannelColors.get( 0 );
        List<RuntimeEventChannel<?>> channels = new ArrayList<RuntimeEventChannel<?>>();
        RuntimeEventChannel<?> channel = makeChannel( "Channel2" );
        channels.add( channel );

        ChartModel chart = resourceManager.createChart( "Chart", ChartTypes.LINE_CHART );
        chart.setChannels( channels );
        assertEquals( firstDefaultColor, usecase.getColorOfChannel( channel ) );

        verify( callback, times( 1 ) ).onColorAssigned( channelsCaptor.capture() );
        // assertTrue( argument.getValue().contains( channel ) );
    }

    @Test
    public void verifyChannelHasCorrectRaceColorAfterSetColor()
    {
        int r = 255, g = 0, b = 255;
        SColor raceColor = new SColor( r, g, b );
        RuntimeEventChannel<?> channel = makeChannel( "Channel4" );

        usecase.setColorForChannel( channel, r, g, b );
        assertEquals( raceColor, usecase.getColorOfChannel( channel ) );
    }

    @After
    public void cleanUp()
    {
        runtimeEventAcceptorService.dispose();
        usecase.unregister();
    }

}
