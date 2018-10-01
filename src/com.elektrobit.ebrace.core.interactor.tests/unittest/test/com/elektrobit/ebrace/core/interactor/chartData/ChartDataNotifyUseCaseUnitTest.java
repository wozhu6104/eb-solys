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

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.core.interactor.api.chartdata.ChartDataCallback;
import com.elektrobit.ebrace.core.interactor.api.chartdata.RunMode;
import com.elektrobit.ebrace.core.interactor.api.resources.model.chart.ChartModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.chart.ChartTypes;
import com.elektrobit.ebrace.core.interactor.chartData.ChartDataNotifyUseCaseImpl;
import com.elektrobit.ebrace.core.preferences.api.AnalysisTimespanPreferences;
import com.elektrobit.ebrace.core.preferences.api.AnalysisTimespanPreferences.ANALYSIS_TIMESPAN_CHANGE_REASON;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.GanttChartData;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.LineChartData;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventProvider;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarkerManager;

import test.com.elektrobit.ebrace.core.interactor.UseCaseBaseTest;

public class ChartDataNotifyUseCaseUnitTest extends UseCaseBaseTest
{
    ChartDataNotifyUseCaseImpl sut;

    AnalysisTimespanPreferences mockedAnalysisTimespanPreferences;
    RuntimeEventProvider mockedRuntimeEventProvider;
    ChartDataCallback mockedChartDataCallback;
    RuntimeEventAcceptor mockedRuntimeEventAcceptor;
    ResourcesModelManager mockedResourcesModelManager;
    TimeMarkerManager mockedTimeMarkerManager;
    ChartModel mockedChartModel;
    ChartModel mockedChartModel2;

    @Before
    public void setUp()
    {
        mockedAnalysisTimespanPreferences = mock( AnalysisTimespanPreferences.class );
        mockedRuntimeEventProvider = mock( RuntimeEventProvider.class );
        mockedChartDataCallback = mock( ChartDataCallback.class );
        mockedRuntimeEventAcceptor = mock( RuntimeEventAcceptor.class );
        mockedResourcesModelManager = mock( ResourcesModelManager.class );
        mockedTimeMarkerManager = mock( TimeMarkerManager.class );
        mockedChartModel = mock( ChartModel.class );
        mockedChartModel2 = mock( ChartModel.class );
        sut = new ChartDataNotifyUseCaseImpl( mockedChartDataCallback,
                                              mockedRuntimeEventProvider,
                                              mockedAnalysisTimespanPreferences,
                                              mockedResourcesModelManager,
                                              mockedTimeMarkerManager );
    }

    @Test
    public void testOnNewLineChartData()
    {
        LineChartData lineChartData = mock( LineChartData.class );

        when( mockedChartModel.getType() ).thenReturn( ChartTypes.LINE_CHART );
        when( mockedChartModel.isLineChartPresAsBar() ).thenReturn( false );
        when( mockedRuntimeEventProvider.getLineChartData( new ArrayList<RuntimeEventChannel<?>>(),
                                                           0,
                                                           0,
                                                           false,
                                                           null,
                                                           false ) ).thenReturn( lineChartData );

        sut.register( mockedChartModel, RunMode.ANALYSIS );
        verify( mockedResourcesModelManager ).registerResourceListener( sut );
        verify( mockedChartDataCallback, times( 1 ) ).onNewLineChartData( lineChartData );

        sut.fullTimespanEndTimeChanged( 20000 );
        verify( mockedChartDataCallback, times( 2 ) ).onNewLineChartData( lineChartData );

        sut.analysisTimespanLengthChanged( 30000 );
        verify( mockedChartDataCallback, times( 3 ) ).onNewLineChartData( lineChartData );

        sut.onAnalysisTimespanChanged( ANALYSIS_TIMESPAN_CHANGE_REASON.USER_SHIFT );
        verify( mockedChartDataCallback, times( 4 ) ).onNewLineChartData( lineChartData );

        sut.onResourceModelChannelsChanged( mockedChartModel );
        verify( mockedChartDataCallback, times( 5 ) ).onNewLineChartData( lineChartData );

        sut.unregister();
        verify( mockedResourcesModelManager ).unregisterResourceListener( sut );
        verifyNoMoreInteractions( mockedResourcesModelManager );
    }

    @Test
    public void verifyRegisterRegistersAllListeners()
    {
        when( mockedChartModel.getType() ).thenReturn( ChartTypes.LINE_CHART );
        when( mockedChartModel.isLineChartPresAsBar() ).thenReturn( false );
        sut.register( mockedChartModel, RunMode.ANALYSIS );

        verify( mockedResourcesModelManager ).registerResourceListener( sut );
        verify( mockedAnalysisTimespanPreferences ).addTimespanPreferencesChangedListener( sut );
        verify( mockedTimeMarkerManager ).registerListener( sut );

        // TODO: switch to 1 when we switch back to callback. See EBRACE-2810
        verify( mockedRuntimeEventProvider, times( 0 ) ).registerListener( sut, null );
    }

    @Test
    public void verifyUnregisterUnregistersAllListeners()
    {
        sut.unregister();
        verify( mockedResourcesModelManager ).unregisterResourceListener( sut );
        verify( mockedAnalysisTimespanPreferences ).removeTimespanPreferencesChangedListener( sut );
        verify( mockedTimeMarkerManager ).unregisterListener( sut );

        // TODO: switch to 1 when we switch back to callback. See EBRACE-2810
        verify( mockedRuntimeEventProvider, times( 0 ) ).unregisterListener( sut );
    }

    @Test
    public void verifyOnChannelChangeRuntimeEventProviderCallsRegister()
    {
        when( mockedChartModel.getType() ).thenReturn( ChartTypes.LINE_CHART );
        sut.register( mockedChartModel, RunMode.ANALYSIS );
        sut.onResourceModelChannelsChanged( mockedChartModel );

        // TODO: switch to 1 when we switch back to callback. See EBRACE-2810
        verify( mockedRuntimeEventProvider, times( 0 ) ).registerListener( sut, null );
    }

    @Test
    public void testOnNewGanttChartData()
    {
        GanttChartData ganttChartData = mock( GanttChartData.class );
        when( mockedChartModel.getType() ).thenReturn( ChartTypes.GANTT_CHART );
        when( mockedChartModel.isLineChartPresAsBar() ).thenReturn( false );
        when( mockedRuntimeEventProvider.getGanttChartData( new ArrayList<RuntimeEventChannel<?>>(), 0, 0, null ) )
                .thenReturn( ganttChartData );

        sut.register( mockedChartModel, RunMode.ANALYSIS );
        verify( mockedChartDataCallback, times( 1 ) ).onNewGanttChartData( ganttChartData );

        sut.fullTimespanEndTimeChanged( 20000 );
        verify( mockedChartDataCallback, times( 2 ) ).onNewGanttChartData( ganttChartData );

        sut.analysisTimespanLengthChanged( 30000 );
        verify( mockedChartDataCallback, times( 3 ) ).onNewGanttChartData( ganttChartData );

        sut.onAnalysisTimespanChanged( ANALYSIS_TIMESPAN_CHANGE_REASON.USER_SHIFT );
        verify( mockedChartDataCallback, times( 4 ) ).onNewGanttChartData( ganttChartData );

        sut.onResourceModelChannelsChanged( mockedChartModel );
        verify( mockedChartDataCallback, times( 5 ) ).onNewGanttChartData( ganttChartData );
    }

    @Test
    public void testGanntAgregationComputation() throws Exception
    {
        when( mockedChartModel.getType() ).thenReturn( ChartTypes.GANTT_CHART );
        when( mockedChartModel.isLineChartPresAsBar() ).thenReturn( false );
        when( mockedChartDataCallback.getWidth() ).thenReturn( 1000 );
        when( mockedAnalysisTimespanPreferences.getFullTimespanStart() ).thenReturn( 0L );
        when( mockedAnalysisTimespanPreferences.getFullTimespanEnd() ).thenReturn( 1000000L );

        sut.register( mockedChartModel, RunMode.FULL );
        sut.notifyChartWidthChanged();
        verify( mockedRuntimeEventProvider ).getGanttChartData( anyList(), eq( 0L ), eq( 1000000L ), eq( 2000L ) );
    }

    @Test
    public void testLineAgregationComputation() throws Exception
    {
        when( mockedChartModel.getType() ).thenReturn( ChartTypes.LINE_CHART );
        when( mockedChartModel.isLineChartPresAsBar() ).thenReturn( false );
        when( mockedChartDataCallback.getWidth() ).thenReturn( 1000 );
        when( mockedAnalysisTimespanPreferences.getFullTimespanStart() ).thenReturn( 0L );
        when( mockedAnalysisTimespanPreferences.getFullTimespanEnd() ).thenReturn( 1000000L );

        sut.register( mockedChartModel, RunMode.FULL );
        sut.notifyChartWidthChanged();
        verify( mockedRuntimeEventProvider )
                .getLineChartData( anyList(), eq( 0L ), eq( 1000000L ), anyBoolean(), eq( 5000L ), eq( false ) );
    }

    @Test
    public void testSelectedChannedChanged() throws Exception
    {
        when( mockedChartModel.getType() ).thenReturn( ChartTypes.LINE_CHART );
        sut.register( mockedChartModel, RunMode.FULL );
        sut.onResourceModelSelectedChannelsChanged( mockedChartModel );
        verify( mockedChartDataCallback ).onSelectedChannelsChanged();
        verifyNoMoreInteractions( mockedChartDataCallback );
    }

    @Test
    public void testSelectedChannedChangedNegative() throws Exception
    {
        when( mockedChartModel.getType() ).thenReturn( ChartTypes.LINE_CHART );
        sut.register( mockedChartModel, RunMode.FULL );
        sut.onResourceModelSelectedChannelsChanged( mockedChartModel2 );
        verifyNoMoreInteractions( mockedChartDataCallback );
    }
}
