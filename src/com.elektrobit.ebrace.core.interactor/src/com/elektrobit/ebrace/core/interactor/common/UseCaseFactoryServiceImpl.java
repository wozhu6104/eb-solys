/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.common;

import java.util.List;

import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceRegistration;
import com.elektrobit.ebrace.core.datainput.api.DataInputService;
import com.elektrobit.ebrace.core.datamanager.api.channels.RuntimeEventChannelManager;
import com.elektrobit.ebrace.core.htmldata.api.HtmlDataService;
import com.elektrobit.ebrace.core.importerregistry.api.ImporterRegistry;
import com.elektrobit.ebrace.core.interactor.actionexecution.ExecuteSTimeSegmentActionInteractionUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.allChannels.AllChannelsNotifyUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.allChannels.ChannelTreeNodeNotifyUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.analysisTimespan.AnalysisTimespanInteractionUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.api.actionexecution.ExecuteSTimeSegmentActionInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.allChannels.AllChannelsNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.allChannels.AllChannelsNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.allChannels.ChannelTreeNodeNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.allChannels.ChannelTreeNodeNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.analysisTimespan.AnalysisTimespanInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.analysisTimespan.AnalysisTimespanInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.browsercontent.HtmlViewChangedCallback;
import com.elektrobit.ebrace.core.interactor.api.browsercontent.HtmlViewNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.browsercontent.SetHtmlViewContentInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.channelcolor.ChannelColorCallback;
import com.elektrobit.ebrace.core.interactor.api.channelcolor.ChannelColorUseCase;
import com.elektrobit.ebrace.core.interactor.api.channelcolor.ColorPreferencesNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.channelcolor.ColorPreferencesNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.channelvalues.ChannelsSnapshotNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.channelvalues.ChannelsSnapshotNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.chartdata.AnalysisTimespanNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.chartdata.AnalysisTimespanNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.chartdata.ChartDataCallback;
import com.elektrobit.ebrace.core.interactor.api.chartdata.TimelineViewDataNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.chartdata.TimelineViewDataNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryService;
import com.elektrobit.ebrace.core.interactor.api.connect.ConnectionStateNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.connect.ConnectionStateNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.connect.ConnectionToTargetInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.connect.ConnectionToTargetInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.connect.ConnectionsNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.connect.ConnectionsNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.createresource.CreateConnectionInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.createresource.CreateConnectionInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.createresource.CreateResourceInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.createresource.CreateResourceInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.createresource.DefaultResourceNameNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.datainput.DataInputUseCase;
import com.elektrobit.ebrace.core.interactor.api.headlessexecutor.HeadlessExecutorInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.liveviewer.LiveViewerHandlerInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.liveviewer.LiveViewerHandlerInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.loaddatachunk.LoadDataChunkInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.loaddatachunk.LoadDataChunkInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.loaddatachunk.LoadDataChunkNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.loaddatachunk.LoadDataChunkNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.loaddatachunk.SystemCPUValuesNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.loaddatachunk.SystemCPUValuesNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.loadfile.LoadFileProgressNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.loadfile.LoadFileProgressNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.loadfile.OpenFileInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.loadfile.OpenFileInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.preferences.FileSizeLimitNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.preferences.FileSizeLimitNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.preferences.LiveModeNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.preferences.LiveModeNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.preferences.PreferencesNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.preferences.PreferencesNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.preferences.SetColorPreferencesInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.preferences.SetPreferencesInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.preferences.SetTimestampPreferencesInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.reset.ClearAllDataInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.reset.ClearAllDataInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.resources.model.table.TableModel;
import com.elektrobit.ebrace.core.interactor.api.resources.tree.ModelNameNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.resources.tree.ModelNameNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.resources.tree.ResouceTreeNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.resources.tree.ResourceTreeNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.runtimeeventdecoder.RuntimeEventDecoderNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.runtimeeventdecoder.RuntimeEventDecoderNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.script.ScriptFolderPathInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.script.ScriptFolderPathNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.script.ScriptFolderPathNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.script.changed.ScriptChangedNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.script.changed.ScriptChangedNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.script.execution.RunScriptInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.script.importing.ImportScriptInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.script.importing.ImportScriptInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.selectelement.SelectElementsInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.selectelement.SelectElementsInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.selectelement.StatusLineTextNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.selectelement.StatusLineTextNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.splitfile.SplitFileInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.splitfile.SplitFileInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.structure.CheckConnectedNodesCallback;
import com.elektrobit.ebrace.core.interactor.api.structure.CheckConnectedNodesInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.structure.RuntimeEventsOfSelectedComrelationNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.structure.SelectStructureInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.structure.SelectStructureInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.structure.SelectedStructureCallback;
import com.elektrobit.ebrace.core.interactor.api.structure.SelectedStructureNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.structure.StructureNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.structure.StructureNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.table.RowFormatter;
import com.elektrobit.ebrace.core.interactor.api.table.TableScriptFiltersNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.table.TableScriptFiltersNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.tableinput.DataCollector;
import com.elektrobit.ebrace.core.interactor.api.tableinput.FilteredTableInputNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.tableinput.FilteredTableNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.tableinput.RuntimeEventTableDataNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.tableinput.RuntimeEventTableDataNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.tableinput.TableSearchTermInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.tableinput.TableSearchTermNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.tableinput.TableSearchTermNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.timemarkers.TimeMarkersInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.timemarkers.TimeMarkersNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.timemarkers.TimeMarkersNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.usermessagelogger.UserMessageLoggerNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.usermessagelogger.UserMessageNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.browserContent.HtmlViewNotifyUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.browserContent.SetHtmlViewerContentUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.channelColor.ChannelColorUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.channelColor.ColorPreferencesNotifyUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.channelValues.ChannelsSnapshotNotifyUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.chartData.AnalysisTimespanNotifyUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.chartData.ChartDataNotifyUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.chartData.TimelineViewDataNotifyUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.connect.ConnectionStateNotifyUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.connect.ConnectionToTargetInteractionUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.connect.ConnectionsNotifyUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.createResource.CreateConnectionInteractionUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.createResource.CreateResourceInteractionUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.createResource.DefaultResourceNameNotifyUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.datainput.DataInputUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.headlessexecutor.HeadlessExecutorInteractionUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.liveviewer.LiveViewerHandlerInteractionUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.loaddatachunk.LoadDataChunkInteractionUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.loaddatachunk.LoadDataChunkNotifyUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.loaddatachunk.SystemCPUValuesNotifyUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.loadfile.LoadFileInteractionUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.loadfile.LoadFileProgressNotifyUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.preferences.FileSizeLimitNotifyUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.preferences.LiveModeNotifyUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.preferences.PreferencesNotifyUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.preferences.ScriptFolderPathInteractionUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.preferences.ScriptFolderPathNotifyUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.preferences.SetColorPreferencesInteractionUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.preferences.SetTimestampPreferencesInteractionUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.reset.ClearAllDataInteractionUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.resourcetree.ModelNameNotifyUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.resourcetree.ResourceTreeNotifyUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.runtimeeventdecoder.RuntimeEventDecoderNotifyUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.script.changed.ScriptChangedNotifyUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.script.execution.RunScriptInteractionUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.script.importing.ImportScriptInteractionUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.selectelement.SelectElementsInteractionUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.selectelement.StatusLineTextNotifyUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.splitfile.SplitFileInteractionUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.structure.CheckConnectedNodesInteractionUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.structure.RuntimeEventsOfSelectedComrelationNotifyUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.structure.SelectStructureInteractionUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.structure.SelectedStructureNotifyUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.structure.StructureNotifyUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.tableinput.FilteredTableInputNotifyUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.tableinput.RuntimeEventTableDataNotifyUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.tableinput.TableScriptFiltersNotifyUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.tableinput.TableSearchTermInteractionUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.tableinput.TableSearchTermNotifyUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.timemarkers.TimeMarkersInteractionUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.timemarkers.TimeMarkersNotifyUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.usermessagelogger.UserMessageNotifyUseCaseImpl;
import com.elektrobit.ebrace.core.preferences.api.AnalysisTimespanPreferences;
import com.elektrobit.ebrace.core.preferences.api.PreferencesService;
import com.elektrobit.ebrace.core.preferences.api.UserInteractionPreferences;
import com.elektrobit.ebrace.core.preferences.listener.UserInteractionPreferencesListener;
import com.elektrobit.ebrace.core.racescriptexecutor.api.RaceScriptLoader;
import com.elektrobit.ebrace.core.racescriptexecutor.api.ScriptExecutorService;
import com.elektrobit.ebrace.core.scriptimporter.api.ScriptImporterService;
import com.elektrobit.ebrace.core.scriptimporter.api.ScriptProjectBuilderService;
import com.elektrobit.ebrace.core.timesegmentmanager.api.TimeSegmentAcceptorService;
import com.elektrobit.ebrace.core.tracefile.api.LoadFileService;
import com.elektrobit.ebrace.core.tracefile.api.SplitFileService;
import com.elektrobit.ebrace.core.usermessagelogger.api.UserMessageLogger;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;
import com.elektrobit.ebrace.targetadapter.communicator.api.ConnectionService;
import com.elektrobit.ebsolys.core.targetdata.api.color.ChannelColorProviderService;
import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelationAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.importer.FileSizeLimitService;
import com.elektrobit.ebsolys.core.targetdata.api.reset.ClearChunkDataNotifier;
import com.elektrobit.ebsolys.core.targetdata.api.reset.ResetListener;
import com.elektrobit.ebsolys.core.targetdata.api.reset.ResetNotifier;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.SelectedElementsService;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.TargetHeaderMetaDataService;
import com.elektrobit.ebsolys.core.targetdata.api.structure.StructureAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.structure.StructureSelectionService;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNodeCheckStateService;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarkerManager;
import com.elektrobit.ebsolys.decoder.common.services.DecoderServiceManagerImpl;

@Component(enabled = true, immediate = true)
public class UseCaseFactoryServiceImpl implements UseCaseFactoryService
{
    private SplitFileService splitFileService;
    private RuntimeEventAcceptor runtimeEventAcceptor;
    private LoadFileService loadFileService;
    private AnalysisTimespanPreferences analysisTimespanPreferences;
    private UserMessageLogger userMessageLogger;
    private ResourcesModelManager resourcesModelManager;
    private StructureAcceptor structureAcceptor;
    private TreeNodeCheckStateService treeNodeCheckStateService;
    private ComRelationAcceptor comRelationAcceptor;
    private StructureSelectionService structureSelectionService;
    private ResetNotifier resetNotifier;
    private UserInteractionPreferences userInteractionPreferences;
    private TimeMarkerManager timeMarkerManager;
    private PreferencesService preferencesService;
    private ImporterRegistry importerRegistry;
    private ChannelColorProviderService channelColorProviderService;
    private HtmlDataService browserService;
    private TargetHeaderMetaDataService targetHeaderMetaDataService;
    private ClearChunkDataNotifier clearChunkDataNotifier;
    private SelectedElementsService selectedElementService;
    private ScriptImporterService scriptImporterService;
    private FileSizeLimitService fileSizeLimitService;
    private RuntimeEventChannelManager runtimeEventChannelManager;
    private TimeSegmentAcceptorService timeSegmentAcceptorService;
    private RaceScriptLoader raceScriptLoader;
    private ScriptExecutorService scriptExecutorService;
    private ScriptProjectBuilderService scriptProjectBuilderService;
    private ConnectionService connectionService;
    private DataInputService dataStream;

    @Activate
    public void start()
    {
        UseCaseFactoryInstance.register( this );
    }

    @Reference
    public void setRaceScriptLoader(RaceScriptLoader raceScriptLoader)
    {
        this.raceScriptLoader = raceScriptLoader;
    }

    public void unsetRaceScriptLoader(RaceScriptLoader raceScriptLoader)
    {
        this.raceScriptLoader = null;
    }

    @Reference
    public void setSplitFileService(SplitFileService splitFileServiceReference)
    {
        splitFileService = splitFileServiceReference;
    }

    public void unsetSplitFileService(SplitFileService splitFileServiceReference)
    {
        splitFileService = null;
    }

    @Reference
    public void setRuntimeEventAcceptor(RuntimeEventAcceptor runtimeEventAcceptorReference)
    {
        runtimeEventAcceptor = runtimeEventAcceptorReference;
    }

    public void unsetRuntimeEventAcceptor(RuntimeEventAcceptor runtimeEventAcceptorReference)
    {
        runtimeEventAcceptor = null;
    }

    @Reference
    public void setLoadFileService(LoadFileService loadFileServiceReference)
    {
        loadFileService = loadFileServiceReference;
    }

    public void unsetLoadFileService(LoadFileService loadFileServiceReference)
    {
        loadFileService = null;
    }

    @Reference
    public void setAnalysisTimespanPreferences(AnalysisTimespanPreferences analysisTimespanPreferencesReference)
    {
        analysisTimespanPreferences = analysisTimespanPreferencesReference;
    }

    public void unsetAnalysisTimespanPreferences(AnalysisTimespanPreferences AnalysisTimespanPreferencesReference)
    {
        analysisTimespanPreferences = null;
    }

    @Reference
    public void setUserMessageLogger(UserMessageLogger userMessageLoggerReference)
    {
        userMessageLogger = userMessageLoggerReference;
    }

    public void unsetUserMessageLogger(UserMessageLogger userMessageLoggerReference)
    {
        userMessageLogger = null;
    }

    @Reference
    public void setResourcesModelManager(ResourcesModelManager resourcesModelManagerReference)
    {
        resourcesModelManager = resourcesModelManagerReference;
    }

    public void unsetResourcesModelManager(ResourcesModelManager resourcesModelManagerReference)
    {
        resourcesModelManager = null;
    }

    @Reference
    public void setStructureAcceptor(StructureAcceptor structureAcceptorReference)
    {
        structureAcceptor = structureAcceptorReference;
    }

    public void unsetStructureAcceptor(StructureAcceptor structureAcceptorReference)
    {
        structureAcceptor = null;
    }

    @Reference
    public void setTreeNodeCheckStateService(TreeNodeCheckStateService treeNodeCheckStateServiceReference)
    {
        treeNodeCheckStateService = treeNodeCheckStateServiceReference;
    }

    public void unsetTreeNodeCheckStateService(TreeNodeCheckStateService treeNodeCheckStateServiceReference)
    {
        treeNodeCheckStateService = null;
    }

    @Reference
    public void setComRelationAcceptor(ComRelationAcceptor comRelationAcceptorReference)
    {
        comRelationAcceptor = comRelationAcceptorReference;
    }

    public void unsetComRelationAcceptor(ComRelationAcceptor comRelationAcceptorReference)
    {
        comRelationAcceptor = null;
    }

    @Reference
    public void setStructureSelectionService(StructureSelectionService structureSelectionServiceReference)
    {
        structureSelectionService = structureSelectionServiceReference;
    }

    public void unsetStructureSelectionService(StructureSelectionService structureSelectionServiceReference)
    {
        structureSelectionService = null;
    }

    @Reference
    public void setResetNotifier(ResetNotifier resetNotifierReference)
    {
        resetNotifier = resetNotifierReference;
    }

    public void unsetResetNotifier(ResetNotifier structureSelectionServiceReference)
    {
        resetNotifier = null;
    }

    @Reference
    public void setUserInteractionPreferences(UserInteractionPreferences userInteractionPreferencesReference)
    {
        userInteractionPreferences = userInteractionPreferencesReference;
    }

    public void unsetUserInteractionPreferences(UserInteractionPreferences userInteractionPreferencesReference)
    {
        userInteractionPreferences = null;
    }

    @Reference
    public void setTimemarkerManager(TimeMarkerManager timeMarkerManagerReference)
    {
        timeMarkerManager = timeMarkerManagerReference;
    }

    public void unsetTimemarkerManager(TimeMarkerManager timeMarkerManagerReference)
    {
        timeMarkerManager = null;
    }

    @Reference
    public void setPreferencesService(PreferencesService preferencesServiceReference)
    {
        preferencesService = preferencesServiceReference;
    }

    public void unsetPreferencesService(PreferencesService preferencesServiceReference)
    {
        preferencesService = null;
    }

    @Reference
    public void setImporterRegistry(ImporterRegistry importerRegistryReference)
    {
        importerRegistry = importerRegistryReference;
    }

    public void unsetImporterRegistry(ImporterRegistry importerRegistryReference)
    {
        importerRegistry = null;
    }

    @Reference
    public void setChannelColorProviderService(ChannelColorProviderService channelColorProviderServiceReference)
    {
        channelColorProviderService = channelColorProviderServiceReference;
    }

    public void unsetChannelColorProviderService(ChannelColorProviderService channelColorProviderServiceReference)
    {
        channelColorProviderService = null;
    }

    @Reference
    public void setSelectedElementsService(SelectedElementsService selectedElmntService)
    {
        selectedElementService = selectedElmntService;
    }

    public void unsetSelectedElementsService(SelectedElementsService selectedElmntService)
    {
        selectedElementService = null;
    }

    @Reference
    public void setBrowserService(HtmlDataService browserServiceReference)
    {
        browserService = browserServiceReference;
    }

    public void unsetBrowserService(HtmlDataService browserServiceReference)
    {
        browserService = null;
    }

    @Reference
    public void setTargetHeaderMetaDataService(TargetHeaderMetaDataService targetHeaderMetaDataServiceReference)
    {
        targetHeaderMetaDataService = targetHeaderMetaDataServiceReference;
    }

    public void unsetTargetHeaderMetaDataService(TargetHeaderMetaDataService targetHeaderMetaDataServiceReference)
    {
        targetHeaderMetaDataService = null;
    }

    @Reference
    public void setClearChunkDataNotifier(ClearChunkDataNotifier clearChunkDataNotifierReference)
    {
        clearChunkDataNotifier = clearChunkDataNotifierReference;
    }

    public void unsetClearChunkDataNotifier(ClearChunkDataNotifier clearChunkDataNotifierReference)
    {
        clearChunkDataNotifier = null;
    }

    @Reference
    public void setScriptImporterService(ScriptImporterService scriptImporterServiceReference)
    {
        scriptImporterService = scriptImporterServiceReference;
    }

    public void unsetScriptImporterService(ScriptImporterService scriptImporterServiceReference)
    {
        scriptImporterService = null;
    }

    @Reference
    public void setFileSizeLimitService(FileSizeLimitService fileSizeLimitServiceReference)
    {
        fileSizeLimitService = fileSizeLimitServiceReference;
    }

    public void unsetFileSizeLimitService(FileSizeLimitService fileSizeLimitServiceReference)
    {
        fileSizeLimitService = null;
    }

    @Reference
    public void setRuntimeEventChannelManager(RuntimeEventChannelManager runtimeEventChannelManagerReference)
    {
        runtimeEventChannelManager = runtimeEventChannelManagerReference;
    }

    public void unsetRuntimeEventChannelManager(RuntimeEventChannelManager runtimeEventChannelManagerReference)
    {
        runtimeEventChannelManager = null;
    }

    @Reference
    public void setTimeSegmentAcceptorService(TimeSegmentAcceptorService timeSegmentAcceptorServiceReference)
    {
        timeSegmentAcceptorService = timeSegmentAcceptorServiceReference;
    }

    public void unsetTimeSegmentAcceptorService(TimeSegmentAcceptorService timeSegmentAcceptorServiceReference)
    {
        timeSegmentAcceptorService = null;
    }

    @Reference
    public void setScriptExecutorService(ScriptExecutorService scriptExecutorService)
    {
        this.scriptExecutorService = scriptExecutorService;
    }

    public void unsetScriptExecutorService(ScriptExecutorService scriptExecutorService)
    {
        this.scriptExecutorService = null;
    }

    @Reference
    public void setScriptProjectBuilderService(ScriptProjectBuilderService scriptProjectBuilderService)
    {
        this.scriptProjectBuilderService = scriptProjectBuilderService;
    }

    public void unsetScriptProjectBuilderService(ScriptProjectBuilderService scriptProjectBuilderService)
    {
        this.scriptProjectBuilderService = null;
    }

    @Reference
    public void setConnectionService(ConnectionService connectionService)
    {
        this.connectionService = connectionService;
    }

    public void unsetConnectionService(ConnectionService connectionService)
    {
        this.connectionService = null;
    }

    @Reference
    public void setDataStreamService(DataInputService dataStream)
    {
        this.dataStream = dataStream;
    }

    public void unsetDataStreamService(DataInputService dataStream)
    {
        this.dataStream = null;
    }

    @Override
    public SplitFileInteractionUseCase makeSplitFileInteractionUseCase(SplitFileInteractionCallback callback)
    {
        return new SplitFileInteractionUseCaseImpl( callback, splitFileService );
    }

    @Override
    public AllChannelsNotifyUseCase makeAllChannelsNotifyUseCase(AllChannelsNotifyCallback callback)
    {
        return new AllChannelsNotifyUseCaseImpl( callback, runtimeEventChannelManager, runtimeEventAcceptor );
    }

    @Override
    public ChannelTreeNodeNotifyUseCase makeChannelTreeNodeNotifyUseCase(ChannelTreeNodeNotifyCallback callback)
    {
        return new ChannelTreeNodeNotifyUseCaseImpl( callback, runtimeEventChannelManager );
    }

    @Override
    public OpenFileInteractionUseCase makeLoadFileInteractionUseCase(OpenFileInteractionCallback callback)
    {
        return new LoadFileInteractionUseCaseImpl( callback, loadFileService, importerRegistry, userMessageLogger );
    }

    @Override
    public LoadFileProgressNotifyUseCase makeLoadFileProgressNotifyUseCase(LoadFileProgressNotifyCallback callback,
            String pathToFile)
    {
        return new LoadFileProgressNotifyUseCaseImpl( callback, pathToFile, loadFileService );
    }

    @Override
    public ConnectionToTargetInteractionUseCase makeConnectionToTargetInteractionUseCase(
            ConnectionToTargetInteractionCallback callback)
    {
        return new ConnectionToTargetInteractionUseCaseImpl( callback, connectionService );
    }

    @Override
    public ConnectionStateNotifyUseCase makeConnectionStateNotifyUseCase(ConnectionStateNotifyCallback callback)
    {
        ConnectionStateNotifyUseCaseImpl useCase = new ConnectionStateNotifyUseCaseImpl( callback, connectionService );
        return useCase;
    }

    @Override
    public ChartDataNotifyUseCaseImpl makeChartDataNotifyUseCase(ChartDataCallback callback)
    {
        return new ChartDataNotifyUseCaseImpl( callback,
                                               runtimeEventAcceptor,
                                               analysisTimespanPreferences,
                                               resourcesModelManager,
                                               timeMarkerManager );
    }

    @Override
    public CreateResourceInteractionUseCase makeCreateResourceUseCase(CreateResourceInteractionCallback callback)
    {
        CreateResourceInteractionUseCase useCase = new CreateResourceInteractionUseCaseImpl( callback,
                                                                                             resourcesModelManager,
                                                                                             userMessageLogger );
        return useCase;
    }

    @Override
    public StatusLineTextNotifyUseCase createStatusLineTextNotifyUseCase(StatusLineTextNotifyCallback callback)
    {

        StatusLineTextNotifyUseCase statsuLine = new StatusLineTextNotifyUseCaseImpl( callback,
                                                                                      selectedElementService,
                                                                                      preferencesService,
                                                                                      connectionService );
        return statsuLine;
    }

    @Override
    public UserMessageNotifyUseCase makeUserLoggerMessageNotifyUseCase(UserMessageLoggerNotifyCallback callback)
    {
        return new UserMessageNotifyUseCaseImpl( callback, userMessageLogger );
    }

    @Override
    public ResouceTreeNotifyUseCase makeResouceTreeNotifyUseCase(ResourceTreeNotifyCallback callback)
    {
        return new ResourceTreeNotifyUseCaseImpl( callback, resourcesModelManager );
    }

    @Override
    public StructureNotifyUseCase makeStructureNotifyUseCase(StructureNotifyCallback callback)
    {
        return new StructureNotifyUseCaseImpl( callback, structureAcceptor, treeNodeCheckStateService );
    }

    @Override
    public CheckConnectedNodesInteractionUseCase makeCheckConnectedNodesInteractionUseCase(
            CheckConnectedNodesCallback callback)
    {
        return new CheckConnectedNodesInteractionUseCaseImpl( callback,
                                                              comRelationAcceptor,
                                                              treeNodeCheckStateService );
    }

    @Override
    @Deprecated
    // Will be replaced by other use-cases (this is not a real use case, dont inspire yourself here when creating a new
    // one!)
    public FilteredTableInputNotifyUseCase makeFilteredTableInputNotifyUseCase(FilteredTableNotifyCallback callback,
            DataCollector dataCollector, List<RowFormatter> columnProviderList)
    {
        return new FilteredTableInputNotifyUseCaseImpl( callback, dataCollector, columnProviderList );
    }

    @Override
    public RuntimeEventTableDataNotifyUseCase makeRuntimeEventTableDataNotifyUseCase(
            RuntimeEventTableDataNotifyCallback callback, List<RowFormatter> columnProviderList, TableModel tableModel)
    {
        RuntimeEventTableDataNotifyUseCaseImpl useCaseImpl = new RuntimeEventTableDataNotifyUseCaseImpl( callback,
                                                                                                         columnProviderList,
                                                                                                         runtimeEventAcceptor,
                                                                                                         analysisTimespanPreferences,
                                                                                                         resourcesModelManager,
                                                                                                         tableModel,
                                                                                                         userInteractionPreferences,
                                                                                                         timeMarkerManager,
                                                                                                         scriptExecutorService );
        ServiceRegistration<?> serviceRegistration = GenericOSGIServiceRegistration
                .registerService( UserInteractionPreferencesListener.class, useCaseImpl );
        useCaseImpl.setServiceRegistration( serviceRegistration );
        return useCaseImpl;
    }

    @Override
    public RuntimeEventDecoderNotifyUseCase makeRuntimeEventDecoderNotifyUseCase(
            RuntimeEventDecoderNotifyCallback callback)
    {
        return new RuntimeEventDecoderNotifyUseCaseImpl( callback );
    }

    @Override
    public SelectStructureInteractionUseCase makeSelectStructureInteractionUseCase(
            SelectStructureInteractionCallback callback)
    {
        return new SelectStructureInteractionUseCaseImpl( callback, structureSelectionService );
    }

    @Override
    public SelectedStructureNotifyUseCase makeSelectedStructureNotifyUseCase(SelectedStructureCallback callback)
    {
        return new SelectedStructureNotifyUseCaseImpl( callback, structureSelectionService );
    }

    @Override
    public RuntimeEventsOfSelectedComrelationNotifyUseCaseImpl makeRuntimeEventsOfSelectedComrelationNotifyUseCaseImpl(
            RuntimeEventsOfSelectedComrelationNotifyCallback callback, List<RowFormatter> rowFormatters)
    {
        return new RuntimeEventsOfSelectedComrelationNotifyUseCaseImpl( callback,
                                                                        rowFormatters,
                                                                        structureSelectionService,
                                                                        comRelationAcceptor,
                                                                        runtimeEventAcceptor,
                                                                        timeMarkerManager );
    }

    @Override
    public ClearAllDataInteractionUseCase makeClearAllDataInteractionUseCase(ClearAllDataInteractionCallback callback)
    {
        return new ClearAllDataInteractionUseCaseImpl( callback, resetNotifier );
    }

    @Override
    public SetTimestampPreferencesInteractionUseCase makeSetTimestampPreferencesInteractionUseCase(
            SetPreferencesInteractionCallback callback)
    {
        return new SetTimestampPreferencesInteractionUseCaseImpl( callback, preferencesService );
    }

    @Override
    public SetColorPreferencesInteractionUseCase makeSetColorPreferencesInteractionUseCase()
    {
        return new SetColorPreferencesInteractionUseCaseImpl( preferencesService );
    }

    @Override
    public ConnectionsNotifyUseCase makeConnectionsNotifyUseCase(ConnectionsNotifyCallback callback)
    {
        return new ConnectionsNotifyUseCaseImpl( callback, resourcesModelManager );
    }

    @Override
    public PreferencesNotifyUseCase makePreferencesNotifyUseCase(PreferencesNotifyCallback callback)
    {
        return new PreferencesNotifyUseCaseImpl( callback, preferencesService );
    }

    @Override
    public ChannelColorUseCase makeChannelColorUseCase(ChannelColorCallback callback)
    {
        return new ChannelColorUseCaseImpl( callback, channelColorProviderService, runtimeEventAcceptor );
    }

    @Override
    public ChannelsSnapshotNotifyUseCase makeChannelsSnapshotNotifyUseCase(
            ChannelsSnapshotNotifyCallback channelsSnapshotNotifyCallback)
    {
        return new ChannelsSnapshotNotifyUseCaseImpl( channelsSnapshotNotifyCallback,
                                                      runtimeEventAcceptor,
                                                      timeMarkerManager,
                                                      analysisTimespanPreferences,
                                                      userInteractionPreferences,
                                                      DecoderServiceManagerImpl.getInstance(),
                                                      resourcesModelManager );
    }

    @Override
    public SelectElementsInteractionUseCase makeSelectElementsInteractionUseCase(
            SelectElementsInteractionCallback selectElementsInteractionCallback)
    {
        return new SelectElementsInteractionUseCaseImpl( selectElementsInteractionCallback, selectedElementService );
    }

    @Override
    public ModelNameNotifyUseCase makeModelNameNotifyUseCase(ModelNameNotifyCallback callback)
    {
        return new ModelNameNotifyUseCaseImpl( callback, resourcesModelManager );
    }

    @Override
    public SetHtmlViewContentInteractionUseCase makeSetHtmlViewContentUseCase()
    {
        return new SetHtmlViewerContentUseCaseImpl( browserService );
    }

    @Override
    public HtmlViewNotifyUseCase makeHtmlViewNotifyUseCase(HtmlViewChangedCallback cb)
    {
        return new HtmlViewNotifyUseCaseImpl( browserService, cb );
    }

    @Override
    public ColorPreferencesNotifyUseCase makeColorPreferencesNotifyUseCase(ColorPreferencesNotifyCallback callback)
    {
        return new ColorPreferencesNotifyUseCaseImpl( callback, preferencesService );
    }

    @Override
    public TimeMarkersNotifyUseCase makeTimeMarkersNotifyUseCase(TimeMarkersNotifyCallback callback)
    {
        return new TimeMarkersNotifyUseCaseImpl( callback, timeMarkerManager );
    }

    @Override
    public TimeMarkersInteractionUseCase makeTimeMarkersInteractionUseCase()
    {
        return new TimeMarkersInteractionUseCaseImpl( timeMarkerManager );
    }

    @Override
    public LoadDataChunkInteractionUseCase makeLoadDataChunkInteractionUseCase(
            LoadDataChunkInteractionCallback callback)
    {
        return new LoadDataChunkInteractionUseCaseImpl( callback, loadFileService, clearChunkDataNotifier );
    }

    @Override
    public LoadDataChunkNotifyUseCase makeLoadDataChunkNotifyUseCase(LoadDataChunkNotifyCallback callback)
    {
        return new LoadDataChunkNotifyUseCaseImpl( callback, loadFileService );
    }

    @Override
    public TableSearchTermNotifyUseCase makeTableSearchTermNotifyUseCase(TableSearchTermNotifyCallback callback,
            String viewID)
    {
        TableSearchTermNotifyUseCaseImpl useCaseImpl = new TableSearchTermNotifyUseCaseImpl( callback,
                                                                                             viewID,
                                                                                             preferencesService );
        return useCaseImpl;
    }

    @Override
    public TableSearchTermInteractionUseCase makeTableSearchTermInteractionUseCase(String viewID)
    {
        return new TableSearchTermInteractionUseCaseImpl( preferencesService, viewID );
    }

    @Override
    public SystemCPUValuesNotifyUseCase makeSystemCPUValuesNotifyUseCase(SystemCPUValuesNotifyCallback callback)
    {

        SystemCPUValuesNotifyUseCaseImpl useCaseImpl = new SystemCPUValuesNotifyUseCaseImpl( callback,
                                                                                             targetHeaderMetaDataService,
                                                                                             loadFileService );

        ServiceRegistration<?> serviceRegistration = GenericOSGIServiceRegistration
                .registerService( ResetListener.class, useCaseImpl );
        useCaseImpl.setServiceRegistration( serviceRegistration );

        return useCaseImpl;

    }

    @Override
    public DefaultResourceNameNotifyUseCase makeDefaultResourceNameNotifyUseCase()
    {
        return new DefaultResourceNameNotifyUseCaseImpl( resourcesModelManager );
    }

    @Override
    public ImportScriptInteractionUseCase makeImportScriptInteractionUseCase(ImportScriptInteractionCallback callback)
    {
        return new ImportScriptInteractionUseCaseImpl( callback, scriptImporterService );
    }

    @Override
    public AnalysisTimespanNotifyUseCase makeAnalysisTimespanNotifyUseCase(AnalysisTimespanNotifyCallback callback)
    {
        return new AnalysisTimespanNotifyUseCaseImpl( analysisTimespanPreferences, callback );
    }

    @Override
    public AnalysisTimespanInteractionUseCase makeAnalysisTimespanInteractionUseCase(
            AnalysisTimespanInteractionCallback callback)
    {
        return new AnalysisTimespanInteractionUseCaseImpl( callback, analysisTimespanPreferences );
    }

    @Override
    public FileSizeLimitNotifyUseCase makeFileSizeLimitNotifyUseCase(FileSizeLimitNotifyCallback callback)
    {
        return new FileSizeLimitNotifyUseCaseImpl( fileSizeLimitService, callback );
    }

    @Override
    public TimelineViewDataNotifyUseCase makeTimelineViewDataNotifyUseCase(TimelineViewDataNotifyCallback callback)
    {
        return new TimelineViewDataNotifyUseCaseImpl( timeSegmentAcceptorService, resourcesModelManager, callback );
    }

    @Override
    public ExecuteSTimeSegmentActionInteractionUseCase makeExecuteSTimeSegmentActionInteractionUseCase()
    {
        return new ExecuteSTimeSegmentActionInteractionUseCaseImpl();
    }

    @Override
    public HeadlessExecutorInteractionUseCase makeHeadlessExecutorInteractionUseCase()
    {
        return new HeadlessExecutorInteractionUseCaseImpl( raceScriptLoader,
                                                           preferencesService,
                                                           scriptProjectBuilderService,
                                                           loadFileService,
                                                           scriptExecutorService,
                                                           connectionService,
                                                           resourcesModelManager );
    }

    @Override
    @Deprecated
    public LiveModeNotifyUseCase makeLiveModeNotifyUseCase(LiveModeNotifyCallback callback)
    {
        LiveModeNotifyUseCaseImpl useCaseImpl = new LiveModeNotifyUseCaseImpl( callback, userInteractionPreferences );

        ServiceRegistration<?> serviceRegistration = GenericOSGIServiceRegistration
                .registerService( UserInteractionPreferencesListener.class, useCaseImpl );
        useCaseImpl.setServiceRegistration( serviceRegistration );
        return useCaseImpl;
    }

    @Override
    public ScriptChangedNotifyUseCase makeScriptChangedNotifyUseCase(ScriptChangedNotifyCallback callback)
    {
        return new ScriptChangedNotifyUseCaseImpl( raceScriptLoader, callback );
    }

    @Override
    public TableScriptFiltersNotifyUseCase makeTableScriptFiltersNotifyUseCase(
            TableScriptFiltersNotifyCallback callback)
    {
        TableScriptFiltersNotifyUseCaseImpl useCaseImpl = new TableScriptFiltersNotifyUseCaseImpl( callback,
                                                                                                   resourcesModelManager,
                                                                                                   raceScriptLoader );
        return useCaseImpl;
    }

    @Override
    public ScriptFolderPathNotifyUseCase makeScriptFolderPathNotifyUseCase(ScriptFolderPathNotifyCallback callback)
    {
        return new ScriptFolderPathNotifyUseCaseImpl( preferencesService, callback );
    }

    @Override
    public ScriptFolderPathInteractionUseCase makeScriptFolderPathInteractionUseCase()
    {
        return new ScriptFolderPathInteractionUseCaseImpl( preferencesService );
    }

    @Deactivate
    public void stop()
    {
        UseCaseFactoryInstance.unregister();
    }

    @Override
    public CreateConnectionInteractionUseCase makeCreateConnectionInteractionUseCase(
            CreateConnectionInteractionCallback callback)
    {
        return new CreateConnectionInteractionUseCaseImpl( resourcesModelManager, connectionService, callback );
    }

    @Override
    public RunScriptInteractionUseCase makeRunScriptInteractionUseCase()
    {
        return new RunScriptInteractionUseCaseImpl( scriptExecutorService );
    }

    @Override
    public DataInputUseCase makeDataInputUseCase()
    {
        return new DataInputUseCaseImpl( dataStream );
    }

    @Override
    public LiveViewerHandlerInteractionUseCase makeActivateLiveViewerUseCase(
            LiveViewerHandlerInteractionCallback callback)
    {
        return new LiveViewerHandlerInteractionUseCaseImpl( callback,
                                                            analysisTimespanPreferences,
                                                            runtimeEventAcceptor,
                                                            userInteractionPreferences,
                                                            timeMarkerManager );
    }
}
