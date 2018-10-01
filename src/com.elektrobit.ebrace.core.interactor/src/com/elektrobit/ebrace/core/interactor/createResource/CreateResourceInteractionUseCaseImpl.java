/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.createResource;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.core.interactor.api.common.ProVersion;
import com.elektrobit.ebrace.core.interactor.api.createresource.CreateResourceInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.createresource.CreateResourceInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.chart.ChartModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.chart.ChartTypes;
import com.elektrobit.ebrace.core.interactor.api.resources.model.htmlview.HtmlViewModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.script.RaceScriptResourceModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.snapshot.SnapshotModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.table.TableModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.timelineview.TimelineViewModel;
import com.elektrobit.ebrace.core.interactor.api.usermessagelogger.UserMessageLoggerTypes;
import com.elektrobit.ebrace.core.usermessagelogger.api.UserMessageLogger;
import com.elektrobit.ebrace.resources.api.manager.ResourceTreeChangedListener;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.STimeSegment;

public class CreateResourceInteractionUseCaseImpl
        implements
            CreateResourceInteractionUseCase,
            ResourceTreeChangedListener
{
    private enum ResourceType {
        LINE_CHART, GANTT_CHART, TIMELINE_VIEW, TABLE
    };

    private static final String TABLE_DEFAULT_PREFIX_NAME = "Table";
    private static final String SNAPSHOT_DEFAULT_PREFIX_NAME = "Snapshot";
    private static final String TIMELINE_VIEW_DEFAULT_PREFIX_NAME = "Timeline View";

    private final ResourcesModelManager resourcesModelManager;
    private CreateResourceInteractionCallback createResourceCallback;
    private ChartTypes chartDataType;
    private final UserMessageLogger userMessageLogger;

    private final List<String> scriptsToOpen = new CopyOnWriteArrayList<>();

    public CreateResourceInteractionUseCaseImpl(CreateResourceInteractionCallback createResourceCallback,
            ResourcesModelManager resourcesModelManager, UserMessageLogger userMessageLogger)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "createResourceCallback", createResourceCallback );

        this.createResourceCallback = createResourceCallback;
        this.resourcesModelManager = resourcesModelManager;
        this.userMessageLogger = userMessageLogger;

        resourcesModelManager.registerTreeListener( this );
    }

    @Override
    public ChartModel createAndOpenChart(List<RuntimeEventChannel<?>> channels)
    {
        Class<?> channelsDataType = getChannelsCommonType( channels );
        if (channelsDataType == null)
        {
            createResourceCallback.onChartChannelsTypeMismatch();
            return null;
        }
        else
        {
            chartDataType = getChartType( channelsDataType );
            ChartModel model = resourcesModelManager
                    .createChart( createNextDefaultResourceName( chartDataType.getName() ), chartDataType );
            model.setChannels( channels );
            resourcesModelManager.openResourceModel( model );
            return model;
        }
    }

    @Override
    public ChartModel createAndOpenChart(String name, ChartTypes type)
    {
        ChartModel model = resourcesModelManager.createChart( name, type );
        resourcesModelManager.openResourceModel( model );
        return model;
    }

    private Class<?> getChannelsCommonType(List<RuntimeEventChannel<?>> channels)
    {
        if (channels.isEmpty())
        {
            return null;
        }
        Class<?> channelType = getChannelsType( channels.get( 0 ) );
        if (channelType == null || !areChannelsAssignableFromDataType( channels, channelType ))
        {
            return null;
        }
        return channelType;
    }

    private Class<?> getChannelsType(RuntimeEventChannel<?> channel)
    {
        if (Number.class.isAssignableFrom( channel.getUnit().getDataType() ))
        {
            return Number.class;
        }
        else if (Boolean.class.isAssignableFrom( channel.getUnit().getDataType() ))
        {
            return Boolean.class;
        }
        else if (STimeSegment.class.isAssignableFrom( channel.getUnit().getDataType() ))
        {
            return STimeSegment.class;
        }
        return null;
    }

    private boolean areChannelsAssignableFromDataType(List<RuntimeEventChannel<?>> channels, Class<?> channelType)
    {
        for (RuntimeEventChannel<?> channel : channels)
        {
            if (!channelType.isAssignableFrom( channel.getUnit().getDataType() ))
            {
                return false;
            }
        }
        return true;
    }

    private ChartTypes getChartType(Class<?> type)
    {
        ChartTypes chartType = ChartTypes.LINE_CHART;
        if (Boolean.class.isAssignableFrom( type ))
        {
            chartType = ChartTypes.GANTT_CHART;
        }
        return chartType;
    }

    @Override
    public TableModel createAndOpenTable(List<RuntimeEventChannel<?>> channels)
    {
        TableModel model = resourcesModelManager
                .createTable( createNextDefaultResourceName( TABLE_DEFAULT_PREFIX_NAME ) );
        model.setChannels( channels );
        resourcesModelManager.openResourceModel( model );
        return model;
    }

    @Override
    public TimelineViewModel createAndOpenTimelineView(List<RuntimeEventChannel<?>> channels)
    {
        TimelineViewModel model = resourcesModelManager
                .createTimelineView( createNextDefaultResourceName( TIMELINE_VIEW_DEFAULT_PREFIX_NAME ) );
        model.setChannels( channels );
        resourcesModelManager.openResourceModel( model );
        return model;
    }

    @Override
    public SnapshotModel createAndOpenSnapshot(List<RuntimeEventChannel<?>> channels)
    {
        if (proFeaturesNotActiveAndShowDialog())
        {
            return null;
        }

        SnapshotModel model = resourcesModelManager
                .createSnapshot( createNextDefaultResourceName( SNAPSHOT_DEFAULT_PREFIX_NAME ) );
        model.setChannels( channels );
        resourcesModelManager.openResourceModel( model );
        return model;
    }

    private boolean proFeaturesNotActiveAndShowDialog()
    {
        if (!proFeaturesActive())
        {
            showProVersionNeededDialog();
            createResourceCallback.onProVersionNotAvailable();
            return true;
        }
        return false;
    }

    private boolean proFeaturesActive()
    {
        return ProVersion.getInstance().isActive();
    }

    private void showProVersionNeededDialog()
    {
        userMessageLogger.logUserMessage( UserMessageLoggerTypes.INFO,
                                          "Using this feature requires EB solys pro version." );
    }

    @Override
    public void createAndOpenSnapshotFromResource(ResourceModel orgResourceModel)
    {
        if (proFeaturesNotActiveAndShowDialog())
        {
            return;
        }

        if (!isResourceAlreadyExisting( orgResourceModel, resourcesModelManager.getSnapshots() ))
        {
            SnapshotModel eventMapModel = resourcesModelManager.createSnapshot( orgResourceModel.getName() );
            eventMapModel.setChannels( orgResourceModel.getChannels() );
            resourcesModelManager.openResourceModel( eventMapModel );
        }
        else
        {
            createResourceCallback.onDerivedResourceAlreadyExists();
        }
    }

    @Override
    public SnapshotModel createOrGetAndOpenSnapshot(List<RuntimeEventChannel<?>> channels)
    {
        if (proFeaturesNotActiveAndShowDialog())
        {
            return null;
        }

        List<ResourceModel> resourceModels = resourcesModelManager.getSnapshots();
        ResourceModel model = findResourceModelWithSameChannels( resourceModels, channels );
        if (model == null)
        {
            model = resourcesModelManager
                    .createSnapshot( createNextDefaultResourceName( SNAPSHOT_DEFAULT_PREFIX_NAME ) );
            model.setChannels( channels );
        }

        if (model != null)
        {
            resourcesModelManager.openResourceModel( model );
        }
        return (SnapshotModel)model;
    }

    @Override
    public HtmlViewModel createAndOpenHtmlView(final String name, final String url)
    {
        HtmlViewModel htmlViewModel = resourcesModelManager.createHtmlView( name, url );
        resourcesModelManager.openResourceModel( htmlViewModel );
        return htmlViewModel;
    }

    private String createNextDefaultResourceName(String resourceModelSimpleName)
    {
        int defaultNameCounter = 0;
        Set<String> existingResourceModelNames = getExistingResourcesNames( resourceModelSimpleName );
        while (existingResourceModelNames.contains( resourceModelSimpleName + "_" + defaultNameCounter ))
        {
            defaultNameCounter++;
        }
        return resourceModelSimpleName + "_" + defaultNameCounter;
    }

    private Set<String> getExistingResourcesNames(String resourceModelSimpleName)
    {
        if (resourceModelSimpleName.equals( TABLE_DEFAULT_PREFIX_NAME ))
        {
            return getExistingResourcesNames( resourcesModelManager.getTables() );
        }
        else if (resourceModelSimpleName.equals( SNAPSHOT_DEFAULT_PREFIX_NAME ))
        {
            return getExistingResourcesNames( resourcesModelManager.getSnapshots() );
        }
        else if (resourceModelSimpleName.equals( TIMELINE_VIEW_DEFAULT_PREFIX_NAME ))
        {
            return getExistingResourcesNames( resourcesModelManager.getTimelineViews() );
        }
        else
        {
            return getExistingResourcesNames( resourcesModelManager.getCharts() );
        }
    }

    private Set<String> getExistingResourcesNames(List<ResourceModel> resources)
    {
        Set<String> names = new HashSet<String>();
        for (ResourceModel resource : resources)
        {
            names.add( resource.getName() );
        }
        return names;
    }

    @Override
    public void createTableFromResource(ResourceModel toCopy)
    {
        createOrGetAndOpenTable( toCopy.getChannels() );
    }

    @Override
    public void createAndOpenChartFromResource(ResourceModel toCopy)
    {
        Class<?> channelsDataType = getChannelsCommonType( toCopy.getChannels() );
        if (channelsDataType == null)
        {
            createResourceCallback.onChartChannelsTypeMismatch();
        }
        else
        {
            createOrGetAndOpenChart( toCopy.getChannels() );
        }
    }

    private boolean isResourceAlreadyExisting(ResourceModel resourceModelToCopy, List<ResourceModel> models)
    {
        if (resourcesModelManager.isNameUsed( resourceModelToCopy.getName(), models ))
        {
            return true;
        }
        return false;
    }

    @Override
    public void unregister()
    {
        resourcesModelManager.registerTreeListener( this );
        createResourceCallback = null;
    }

    @Override
    public ResourceModel createOrGetAndOpenChart(List<RuntimeEventChannel<?>> channels)
    {
        List<ResourceModel> resourceModels = resourcesModelManager.getCharts();
        resourceModels.addAll( resourcesModelManager.getTimelineViews() );

        ResourceModel model = findResourceModelWithSameChannels( resourceModels, channels );
        if (model == null)
        {
            Class<?> channelsDataType = getChannelsCommonType( channels );
            if (channelsDataType == null)
            {
                createResourceCallback.onChartChannelsTypeMismatch();
            }
            else if (channelsDataType.equals( STimeSegment.class ))
            {
                String name = createNextDefaultResourceName( TIMELINE_VIEW_DEFAULT_PREFIX_NAME );
                model = resourcesModelManager.createTimelineView( name );
                model.setChannels( channels );
            }
            else
            {
                chartDataType = getChartType( channelsDataType );
                model = resourcesModelManager.createChart( createNextDefaultResourceName( chartDataType.getName() ),
                                                           chartDataType );
                model.setChannels( channels );
            }
        }

        if (model != null)
        {
            resourcesModelManager.openResourceModel( model );
        }

        return model;

    }

    @Override
    public TableModel createOrGetAndOpenTable(List<RuntimeEventChannel<?>> channels)
    {
        List<ResourceModel> resourceModels = resourcesModelManager.getTables();

        ResourceModel model = findResourceModelWithSameChannels( resourceModels, channels );
        if (model == null)
        {
            model = resourcesModelManager.createTable( createNextDefaultResourceName( TABLE_DEFAULT_PREFIX_NAME ) );
            model.setChannels( channels );
        }

        if (model != null)
        {
            resourcesModelManager.openResourceModel( model );
        }
        return (TableModel)model;
    }

    private ResourceModel findResourceModelWithSameChannels(List<ResourceModel> resourceModels,
            List<RuntimeEventChannel<?>> channels)
    {
        Set<List<RuntimeEventChannel<?>>> channelsNew = new HashSet<List<RuntimeEventChannel<?>>>();
        channelsNew.add( channels );

        if (!resourceModels.isEmpty())
        {
            for (ResourceModel resModel : resourceModels)
            {
                Set<List<RuntimeEventChannel<?>>> channeslModel = new HashSet<List<RuntimeEventChannel<?>>>();
                channeslModel.add( resModel.getChannels() );

                if (channeslModel.equals( channelsNew ))
                {
                    return resModel;
                }
            }
        }

        return null;
    }

    @Override
    public ResourceModel createOrGetAndOpenResourceAccordingToType(List<RuntimeEventChannel<?>> channels)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "channels", channels );

        if (channels.isEmpty())
        {
            return null;
        }

        Set<ResourceType> possibleResourcesTypes = findRightEventType( channels );

        if (possibleResourcesTypes.size() > 1)
        {
            return createOrGetAndOpenTable( channels );
        }

        ResourceType chType = possibleResourcesTypes.iterator().next();

        if (chType.equals( ResourceType.LINE_CHART ) || chType.equals( ResourceType.GANTT_CHART ))
        {
            return createOrGetAndOpenChart( channels );
        }
        else if (chType.equals( ResourceType.TIMELINE_VIEW ))
        {
            return createOrGetAndOpenTimelineView( channels );
        }
        else
        {
            return createOrGetAndOpenTable( channels );
        }
    }

    private Set<ResourceType> findRightEventType(List<RuntimeEventChannel<?>> channels)
    {
        Set<ResourceType> channelsType = new HashSet<>();

        for (RuntimeEventChannel<?> channel : channels)
        {
            Class<?> unitDataType = channel.getUnit().getDataType();
            if (Number.class.isAssignableFrom( unitDataType ))
            {
                channelsType.add( ResourceType.LINE_CHART );
            }
            else if (Boolean.class.isAssignableFrom( unitDataType ))
            {
                channelsType.add( ResourceType.GANTT_CHART );
            }
            else if (STimeSegment.class.isAssignableFrom( unitDataType ))
            {
                channelsType.add( ResourceType.TIMELINE_VIEW );
            }
            else
            {
                channelsType.add( ResourceType.TABLE );
            }
        }

        return channelsType;
    }

    @Override
    public TimelineViewModel createOrGetAndOpenTimelineView(List<RuntimeEventChannel<?>> channels)
    {
        List<ResourceModel> resourceModels = resourcesModelManager.getTimelineViews();
        ResourceModel model = findResourceModelWithSameChannels( resourceModels, channels );
        if (model == null)
        {
            Class<?> channelsDataType = getChannelsCommonType( channels );
            if (channelsDataType == null || !channelsDataType.equals( STimeSegment.class ))
            {
                createResourceCallback.onChartChannelsTypeMismatch();
            }
            else
            {
                String newName = createNextDefaultResourceName( TIMELINE_VIEW_DEFAULT_PREFIX_NAME );
                model = resourcesModelManager.createTimelineView( newName );
                model.setChannels( channels );
            }
        }
        resourcesModelManager.openResourceModel( model );

        return (TimelineViewModel)model;
    }

    @Override
    public void createAndOpenUserScript(File file, String name)
    {
        final RaceScriptResourceModel model = getUserScriptWithName( name );

        if (model == null)
        {
            scriptsToOpen.add( name );
        }
        else
        {
            resourcesModelManager.openResourceModel( model );
        }
    }

    private RaceScriptResourceModel getUserScriptWithName(String name)
    {
        for (ResourceModel nextUserScriptModel : resourcesModelManager.getUserScripts())
        {
            if (nextUserScriptModel.getName().equals( name ))
            {
                return (RaceScriptResourceModel)nextUserScriptModel;
            }
        }
        return null;
    }

    @Override
    public boolean isUserScriptAvailable(String name)
    {
        return getUserScriptWithName( name ) != null;
    }

    @Override
    public void onResourceTreeChanged()
    {
    }

    @Override
    public void onResourceDeleted(ResourceModel resourceModel)
    {
        if (resourceModel instanceof RaceScriptResourceModel && scriptsToOpen.contains( resourceModel.getName() ))
        {
            scriptsToOpen.remove( resourceModel.getName() );
        }
    }

    @Override
    public void onResourceRenamed(ResourceModel resourceModel)
    {
    }

    @Override
    public void onResourceAdded(ResourceModel resourceModel)
    {
        if (resourceModel instanceof RaceScriptResourceModel && scriptsToOpen.contains( resourceModel.getName() )
                && resourceModel != null)
        {
            scriptsToOpen.remove( resourceModel.getName() );
            resourcesModelManager.openResourceModel( resourceModel );
        }
    }

    @Override
    public void onOpenResourceModel(ResourceModel resourceModel)
    {
    }

}
