/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.usecase.chartData.tests;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.core.interactor.api.chartdata.ChartDataCallback;
import com.elektrobit.ebrace.core.interactor.api.chartdata.ChartDataNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.chartdata.RunMode;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.resources.model.chart.ChartModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.chart.ChartTypes;
import com.elektrobit.ebrace.core.preferences.api.AnalysisTimespanPreferences;
import com.elektrobit.ebrace.core.preferences.api.AnalysisTimespanPreferences.ANALYSIS_TIMESPAN_CHANGE_REASON;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.LineChartData;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

import test.com.elektrobit.ebrace.core.interactor.UseCaseBaseTest;

public class ChartDataNotifyUseCaseTest extends UseCaseBaseTest
{
    AnalysisTimespanPreferences analysisTimespanPreferences = new GenericOSGIServiceTracker<AnalysisTimespanPreferences>( AnalysisTimespanPreferences.class )
            .getService();
    RuntimeEventAcceptor runtimeEventAcceptor = new GenericOSGIServiceTracker<RuntimeEventAcceptor>( RuntimeEventAcceptor.class )
            .getService();
    private final ResourcesModelManager resourceManager = new GenericOSGIServiceTracker<ResourcesModelManager>( ResourcesModelManager.class )
            .getService();

    List<RuntimeEventChannel<?>> runtimeEventChannels = new ArrayList<RuntimeEventChannel<?>>();

    @Test
    public void testRegisterLineChart()
    {
        ChartDataCallback chartDataCallback = Mockito.mock( ChartDataCallback.class );
        ChartDataNotifyUseCase sut = UseCaseFactoryInstance.get().makeChartDataNotifyUseCase( chartDataCallback );
        ChartModel chartModel = resourceManager.createChart( "TestLine", ChartTypes.LINE_CHART );
        sut.register( chartModel, RunMode.LIVE );

        Mockito.verify( chartDataCallback ).onNewLineChartData( (LineChartData)Mockito.anyObject() );

        analysisTimespanPreferences.setAnalysisTimespanEnd( System.currentTimeMillis(),
                                                            ANALYSIS_TIMESPAN_CHANGE_REASON.USER_SHIFT );
        Mockito.verify( chartDataCallback ).onNewLineChartData( (LineChartData)Mockito.anyObject() );
    }
}
