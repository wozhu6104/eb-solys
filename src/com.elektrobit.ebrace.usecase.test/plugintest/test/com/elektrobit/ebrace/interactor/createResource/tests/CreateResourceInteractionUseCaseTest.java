/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.interactor.createResource.tests;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.core.interactor.api.common.ProVersion;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.createresource.CreateResourceInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.createresource.CreateResourceInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.chart.ChartModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.chart.ChartTypes;
import com.elektrobit.ebrace.core.interactor.api.resources.model.table.TableModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.timelineview.TimelineViewModel;
import com.elektrobit.ebrace.dev.test.util.services.CoreServiceHelper;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

import test.com.elektrobit.ebrace.core.interactor.UseCaseBaseTest;

public class CreateResourceInteractionUseCaseTest extends UseCaseBaseTest implements CreateResourceInteractionCallback
{
    private ResourcesModelManager resourceManager;
    private RuntimeEventAcceptor runtimeEventAcceptorService;
    private CreateResourceInteractionUseCase createResourceUseCase;

    @Before
    public void setup()
    {
        ProVersion.getInstance().setActive( true );
        resourceManager = new GenericOSGIServiceTracker<ResourcesModelManager>( ResourcesModelManager.class )
                .getService();
        runtimeEventAcceptorService = CoreServiceHelper.getRuntimeEventAcceptor();
        createResourceUseCase = UseCaseFactoryInstance.get().makeCreateResourceUseCase( this );
    }

    private List<RuntimeEventChannel<?>> createDoubleTypeChannels()
    {
        List<RuntimeEventChannel<?>> channels = new ArrayList<RuntimeEventChannel<?>>();
        channels.add( mockChannelWithUnit( Unit.PERCENT ) );
        channels.add( mockChannelWithUnit( Unit.PERCENT ) );
        channels.add( mockChannelWithUnit( Unit.PERCENT ) );
        return channels;
    }

    private List<RuntimeEventChannel<?>> createBooleanTypeChannels()
    {
        List<RuntimeEventChannel<?>> channels = new ArrayList<RuntimeEventChannel<?>>();
        channels.add( mockChannelWithUnit( Unit.BOOLEAN ) );
        channels.add( mockChannelWithUnit( Unit.BOOLEAN ) );
        return channels;
    }

    private List<RuntimeEventChannel<?>> createStringTypeChannels()
    {
        List<RuntimeEventChannel<?>> channels = new ArrayList<RuntimeEventChannel<?>>();
        channels.add( mockChannelWithUnit( Unit.TEXT ) );
        channels.add( mockChannelWithUnit( Unit.TEXT ) );
        return channels;
    }

    @Test
    public void testCreateLineChart()
    {
        List<RuntimeEventChannel<?>> channels = createDoubleTypeChannels();
        createResourceUseCase.createAndOpenChart( channels );

        Assert.assertEquals( 1, resourceManager.getChartsWithCertainType( ChartTypes.LINE_CHART ).size() );
        ResourceModel createdChart = resourceManager.getChartsWithCertainType( ChartTypes.LINE_CHART ).get( 0 );
        Assert.assertEquals( "LineChart_0", createdChart.getName() );
    }

    @Test
    public void testCreateGanttChart()
    {
        List<RuntimeEventChannel<?>> channels = createBooleanTypeChannels();
        createResourceUseCase.createAndOpenChart( channels );
        Assert.assertEquals( 1, resourceManager.getChartsWithCertainType( ChartTypes.GANTT_CHART ).size() );
        ResourceModel createdChart = resourceManager.getChartsWithCertainType( ChartTypes.GANTT_CHART ).get( 0 );
        Assert.assertEquals( "GanttChart_0", createdChart.getName() );
    }

    @Test
    public void testCreateChartWithWrongChannels()
    {
        CreateResourceInteractionCallback mockedCreateResourceCallback = Mockito
                .mock( CreateResourceInteractionCallback.class );
        CreateResourceInteractionUseCase sut = UseCaseFactoryInstance.get().makeCreateResourceUseCase( mockedCreateResourceCallback );
        sut.createAndOpenChart( createStringTypeChannels() );
        Mockito.verify( mockedCreateResourceCallback ).onChartChannelsTypeMismatch();
    }

    @Test
    public void testCreateTable()
    {
        List<RuntimeEventChannel<?>> channels = createDoubleTypeChannels();
        createResourceUseCase.createAndOpenTable( channels );

        Assert.assertEquals( 1, resourceManager.getTables().size() );
        Assert.assertEquals( "Table_0", resourceManager.getTables().get( 0 ).getName() );
    }

    @Test
    public void testCreateSnapshot()
    {
        List<RuntimeEventChannel<?>> channels = createDoubleTypeChannels();
        createResourceUseCase.createAndOpenSnapshot( channels );

        Assert.assertEquals( 1, resourceManager.getSnapshots().size() );
        Assert.assertEquals( "Snapshot_0", resourceManager.getSnapshots().get( 0 ).getName() );
    }

    @Test
    public void testCreateTableFromResource()
    {
        createResourceUseCase.createTableFromResource( createChartModel() );

        Assert.assertEquals( 1, resourceManager.getTables().size() );
        Assert.assertEquals( "Table_0", resourceManager.getTables().get( 0 ).getName() );
        Assert.assertEquals( 3, resourceManager.getTables().get( 0 ).getChannels().size() );
    }

    @Test
    public void testCreateSnapshotFromResource()
    {
        createResourceUseCase.createAndOpenSnapshotFromResource( createChartModel() );

        Assert.assertEquals( 1, resourceManager.getSnapshots().size() );
        Assert.assertEquals( "TestLineChart", resourceManager.getSnapshots().get( 0 ).getName() );
        Assert.assertEquals( 3, resourceManager.getSnapshots().get( 0 ).getChannels().size() );
    }

    @Test
    public void testCreateOrGetAndOpenResourceAccordingToTypeNumeric()
    {
        List<RuntimeEventChannel<?>> channels = new ArrayList<>();
        channels.add( mockChannelWithUnit( Unit.PERCENT ) );

        ResourceModel newResource = createResourceUseCase.createOrGetAndOpenResourceAccordingToType( channels );

        Assert.assertTrue( newResource instanceof ChartModel );
        Assert.assertEquals( ChartTypes.LINE_CHART, ((ChartModel)newResource).getType() );
        Assert.assertEquals( channels, newResource.getChannels() );
    }

    @Test
    public void testCreateOrGetAndOpenResourceAccordingToTypeTimeSegments()
    {
        List<RuntimeEventChannel<?>> channels = new ArrayList<>();
        channels.add( mockChannelWithUnit( Unit.TIMESEGMENT ) );

        ResourceModel newResource = createResourceUseCase.createOrGetAndOpenResourceAccordingToType( channels );

        Assert.assertTrue( newResource instanceof TimelineViewModel );
        Assert.assertEquals( channels, newResource.getChannels() );
    }

    @Test
    public void testCreateOrGetAndOpenResourceAccordingToTypeTEXT()
    {
        List<RuntimeEventChannel<?>> channels = new ArrayList<>();
        channels.add( mockChannelWithUnit( Unit.TEXT ) );

        ResourceModel newResource = createResourceUseCase.createOrGetAndOpenResourceAccordingToType( channels );

        Assert.assertTrue( newResource instanceof TableModel );
        Assert.assertEquals( channels, newResource.getChannels() );
    }

    @Test
    public void testCreateOrGetAndOpenResourceAccordingToTypeBoolean()
    {
        List<RuntimeEventChannel<?>> channels = new ArrayList<>();
        channels.add( mockChannelWithUnit( Unit.BOOLEAN ) );

        ResourceModel newResource = createResourceUseCase.createOrGetAndOpenResourceAccordingToType( channels );

        Assert.assertTrue( newResource instanceof ChartModel );
        Assert.assertEquals( ChartTypes.GANTT_CHART, ((ChartModel)newResource).getType() );
        Assert.assertEquals( channels, newResource.getChannels() );
    }

    @Test
    public void testCreateOrGetAndOpenResourceAccordingToTypenEmptyList()
    {
        List<RuntimeEventChannel<?>> channels = new ArrayList<>();
        ResourceModel newResource = createResourceUseCase.createOrGetAndOpenResourceAccordingToType( channels );
        Assert.assertNull( newResource );
    }

    private <T> RuntimeEventChannel<T> mockChannelWithUnit(Unit<T> unit)
    {
        @SuppressWarnings("unchecked")
        RuntimeEventChannel<T> channel = Mockito.mock( RuntimeEventChannel.class );
        Mockito.when( channel.getUnit() ).thenReturn( unit );
        return channel;
    }

    private ChartModel createChartModel()
    {
        ChartModel chartModel = resourceManager.createChart( "TestLineChart", ChartTypes.LINE_CHART );
        chartModel.setChannels( createDoubleTypeChannels() );
        return chartModel;
    }

    @Test
    public void testCreateChartFromResource()
    {
        createResourceUseCase.createAndOpenChartFromResource( createTableModelWithBooleanTypeChannels() );
        Assert.assertEquals( 1, resourceManager.getChartsWithCertainType( ChartTypes.GANTT_CHART ).size() );
        Assert.assertEquals( 2,
                             resourceManager.getChartsWithCertainType( ChartTypes.GANTT_CHART ).get( 0 ).getChannels()
                                     .size() );
        Assert.assertEquals( "GanttChart_0",
                             resourceManager.getChartsWithCertainType( ChartTypes.GANTT_CHART ).get( 0 ).getName() );

        createResourceUseCase.createAndOpenChartFromResource( createTableModelWithDoubleTypeChannels() );
        Assert.assertEquals( 1, resourceManager.getChartsWithCertainType( ChartTypes.LINE_CHART ).size() );
        Assert.assertEquals( 2,
                             resourceManager.getChartsWithCertainType( ChartTypes.GANTT_CHART ).get( 0 ).getChannels()
                                     .size() );

        Assert.assertEquals( "LineChart_0",
                             resourceManager.getChartsWithCertainType( ChartTypes.LINE_CHART ).get( 0 ).getName() );

        Assert.assertEquals( 2, resourceManager.getCharts().size() );

        CreateResourceInteractionCallback mockedCreateResourceCallback = Mockito
                .mock( CreateResourceInteractionCallback.class );
        CreateResourceInteractionUseCase sut = UseCaseFactoryInstance.get().makeCreateResourceUseCase( mockedCreateResourceCallback );
        sut.createAndOpenChartFromResource( createTableModelWithStringTypeChannels() );
        Mockito.verify( mockedCreateResourceCallback ).onChartChannelsTypeMismatch();
    }

    private TableModel createTableModelWithStringTypeChannels()
    {
        TableModel tableModel = resourceManager.createTable( "TableStringTypeChannel" );
        tableModel.setChannels( createStringTypeChannels() );
        return tableModel;
    }

    private TableModel createTableModelWithDoubleTypeChannels()
    {
        TableModel tableModel = resourceManager.createTable( "TableDoubleTypeChannel" );
        tableModel.setChannels( createDoubleTypeChannels() );
        return tableModel;
    }

    private TableModel createTableModelWithBooleanTypeChannels()
    {
        TableModel tableModel = resourceManager.createTable( "TableBooleanTypeChannel" );
        tableModel.setChannels( createBooleanTypeChannels() );
        return tableModel;
    }

    @Override
    public void onChartChannelsTypeMismatch()
    {

    }

    @After
    public void cleanUp()
    {
        resourceManager.deleteResourcesModels( resourceManager.getResources() );
        runtimeEventAcceptorService.dispose();
    }

    @Override
    public void onDerivedResourceAlreadyExists()
    {
    }

    @Override
    public void onProVersionNotAvailable()
    {
    }
}
