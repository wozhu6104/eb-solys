/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.interactor.chartData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;

import com.elektrobit.ebrace.core.interactor.api.chartdata.TimelineViewDataNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.resources.model.timelineview.TimelineViewModel;
import com.elektrobit.ebrace.core.interactor.chartData.TimelineViewDataNotifyUseCaseImpl;
import com.elektrobit.ebrace.core.timesegmentmanager.api.TimeSegmentAcceptorService;
import com.elektrobit.ebrace.dev.test.util.datamanager.RuntimeEventChannelMock;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.STimeSegment;

import test.com.elektrobit.ebrace.core.interactor.UseCaseBaseTest;

public class TimelineViewDataNotifyUseCaseImplTest extends UseCaseBaseTest
{
    private TimelineViewDataNotifyUseCaseImpl sutTimelineViewNotifyUseCase;
    private TimeSegmentAcceptorService mockedSegmentAcceptor;
    private TimelineViewDataNotifyCallback mockedCallback;
    private Map<RuntimeEventChannel<STimeSegment>, List<STimeSegment>> result1;
    private TimelineViewModel mockTimelineViewModel;
    private ResourcesModelManager mockedResourcesModelManager;

    @Before
    public void setup()
    {
        mockedSegmentAcceptor = Mockito.mock( TimeSegmentAcceptorService.class );

        result1 = new HashMap<RuntimeEventChannel<STimeSegment>, List<STimeSegment>>();
        setSegmentAcceptorResult( result1 );

        mockedResourcesModelManager = Mockito.mock( ResourcesModelManager.class );
        mockedCallback = Mockito.mock( TimelineViewDataNotifyCallback.class );
        sutTimelineViewNotifyUseCase = new TimelineViewDataNotifyUseCaseImpl( mockedSegmentAcceptor,
                                                                              mockedResourcesModelManager,
                                                                              mockedCallback );

        mockTimelineViewModel = mockTimelineViewModel();
        sutTimelineViewNotifyUseCase.register( mockTimelineViewModel );
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private TimelineViewModel mockTimelineViewModel()
    {
        TimelineViewModel mockedModel = Mockito.mock( TimelineViewModel.class );
        RuntimeEventChannel<?> channel1 = new RuntimeEventChannelMock<>( "channel1" );
        RuntimeEventChannel<?> channel2 = new RuntimeEventChannelMock<>( "channel1" );
        List channels = new ArrayList<>();
        channels.add( channel1 );
        channels.add( channel2 );
        Mockito.when( mockedModel.getChannels() ).thenReturn( channels );
        return mockedModel;
    }

    private OngoingStubbing<Map<RuntimeEventChannel<STimeSegment>, List<STimeSegment>>> setSegmentAcceptorResult(
            Map<RuntimeEventChannel<STimeSegment>, List<STimeSegment>> result1)
    {
        return Mockito.when( mockedSegmentAcceptor.getTimeSegmentsForChannel( Mockito.any() ) ).thenReturn( result1 );
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void dataInitiallyPosted() throws Exception
    {
        ArgumentCaptor<Map> resultCaptor = ArgumentCaptor.forClass( Map.class );
        Mockito.verify( mockedCallback ).onNewTimelineData( resultCaptor.capture() );
        Map capturedResult = resultCaptor.getValue();
        Assert.assertTrue( result1 == capturedResult );
    }

    @Test
    public void dataPostedWhenModelChanged() throws Exception
    {
        sutTimelineViewNotifyUseCase.onResourceModelChannelsChanged( mockTimelineViewModel );
        Mockito.verify( mockedCallback, Mockito.times( 2 ) ).onNewTimelineData( Mockito.anyMap() );
    }

    @Test
    public void registrationUpdatedWhenModelChanged() throws Exception
    {
        Mockito.reset( mockedSegmentAcceptor );
        sutTimelineViewNotifyUseCase.onResourceModelChannelsChanged( mockTimelineViewModel );
        List<RuntimeEventChannel<?>> expectedChannels = mockTimelineViewModel.getChannels();
        Mockito.verify( mockedSegmentAcceptor, Mockito.times( 1 ) ).registerListener( sutTimelineViewNotifyUseCase,
                                                                                      expectedChannels );
    }

    @Test
    public void dataNotPostedWhenAnotherModelChanged() throws Exception
    {
        sutTimelineViewNotifyUseCase.onResourceModelChannelsChanged( Mockito.mock( TimelineViewModel.class ) );
        Mockito.verify( mockedCallback, Mockito.times( 1 ) ).onNewTimelineData( Mockito.anyMap() );
    }

    @Test
    public void noDataPostenAfterUnregister() throws Exception
    {
        sutTimelineViewNotifyUseCase.unregister();
        sutTimelineViewNotifyUseCase.onResourceModelChannelsChanged( mockTimelineViewModel );
        sutTimelineViewNotifyUseCase.onChannelsContentChanged();

        Mockito.verify( mockedCallback, Mockito.times( 1 ) ).onNewTimelineData( Mockito.anyMap() );
    }

    @Test
    public void unregisterCalled() throws Exception
    {
        sutTimelineViewNotifyUseCase.unregister();
        Mockito.verify( mockedSegmentAcceptor ).unregisterListener( sutTimelineViewNotifyUseCase );
        Mockito.verify( mockedResourcesModelManager ).unregisterResourceListener( sutTimelineViewNotifyUseCase );
    }
}
