/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.api.common;

import java.util.List;

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
import com.elektrobit.ebrace.core.interactor.api.chartdata.ChartDataNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.chartdata.TimelineViewDataNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.chartdata.TimelineViewDataNotifyUseCase;
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
import com.elektrobit.ebrace.core.interactor.api.structure.RuntimeEventsOfSelectedComrelationNotifyUseCase;
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

public interface UseCaseFactoryService
{
    public HeadlessExecutorInteractionUseCase makeHeadlessExecutorInteractionUseCase();

    public ScriptChangedNotifyUseCase makeScriptChangedNotifyUseCase(ScriptChangedNotifyCallback callback);

    public TableScriptFiltersNotifyUseCase makeTableScriptFiltersNotifyUseCase(
            TableScriptFiltersNotifyCallback callback);

    public ScriptFolderPathNotifyUseCase makeScriptFolderPathNotifyUseCase(ScriptFolderPathNotifyCallback callback);

    public ScriptFolderPathInteractionUseCase makeScriptFolderPathInteractionUseCase();

    public SplitFileInteractionUseCase makeSplitFileInteractionUseCase(SplitFileInteractionCallback callback);

    public AllChannelsNotifyUseCase makeAllChannelsNotifyUseCase(AllChannelsNotifyCallback callback);

    public ChannelTreeNodeNotifyUseCase makeChannelTreeNodeNotifyUseCase(ChannelTreeNodeNotifyCallback callback);

    public OpenFileInteractionUseCase makeLoadFileInteractionUseCase(OpenFileInteractionCallback callback);

    public LoadFileProgressNotifyUseCase makeLoadFileProgressNotifyUseCase(LoadFileProgressNotifyCallback callback,
            String pathToFile);

    public ConnectionToTargetInteractionUseCase makeConnectionToTargetInteractionUseCase(
            ConnectionToTargetInteractionCallback callback);

    public ConnectionStateNotifyUseCase makeConnectionStateNotifyUseCase(ConnectionStateNotifyCallback callback);

    public ChartDataNotifyUseCase makeChartDataNotifyUseCase(ChartDataCallback callback);

    public CreateResourceInteractionUseCase makeCreateResourceUseCase(CreateResourceInteractionCallback callback);

    public StatusLineTextNotifyUseCase createStatusLineTextNotifyUseCase(StatusLineTextNotifyCallback callback);

    public UserMessageNotifyUseCase makeUserLoggerMessageNotifyUseCase(UserMessageLoggerNotifyCallback callback);

    public ResouceTreeNotifyUseCase makeResouceTreeNotifyUseCase(ResourceTreeNotifyCallback callback);

    public StructureNotifyUseCase makeStructureNotifyUseCase(StructureNotifyCallback callback);

    public CheckConnectedNodesInteractionUseCase makeCheckConnectedNodesInteractionUseCase(
            CheckConnectedNodesCallback callback);

    // Will be replaced by other use-cases (this is not a real use case, dont inspire yourself here when creating
    // a new
    // one!)
    public FilteredTableInputNotifyUseCase makeFilteredTableInputNotifyUseCase(FilteredTableNotifyCallback callback,
            DataCollector dataCollector, List<RowFormatter> columnProviderList);

    public RuntimeEventTableDataNotifyUseCase makeRuntimeEventTableDataNotifyUseCase(
            RuntimeEventTableDataNotifyCallback callback, List<RowFormatter> columnProviderList, TableModel tableModel);

    public RuntimeEventDecoderNotifyUseCase makeRuntimeEventDecoderNotifyUseCase(
            RuntimeEventDecoderNotifyCallback callback);

    public SelectStructureInteractionUseCase makeSelectStructureInteractionUseCase(
            SelectStructureInteractionCallback callback);

    public SelectedStructureNotifyUseCase makeSelectedStructureNotifyUseCase(SelectedStructureCallback callback);

    public RuntimeEventsOfSelectedComrelationNotifyUseCase makeRuntimeEventsOfSelectedComrelationNotifyUseCaseImpl(
            RuntimeEventsOfSelectedComrelationNotifyCallback callback, List<RowFormatter> rowFormatters);

    public ClearAllDataInteractionUseCase makeClearAllDataInteractionUseCase(ClearAllDataInteractionCallback callback);

    public SetTimestampPreferencesInteractionUseCase makeSetTimestampPreferencesInteractionUseCase(
            SetPreferencesInteractionCallback callback);

    public SetColorPreferencesInteractionUseCase makeSetColorPreferencesInteractionUseCase();

    public ConnectionsNotifyUseCase makeConnectionsNotifyUseCase(ConnectionsNotifyCallback callback);

    public PreferencesNotifyUseCase makePreferencesNotifyUseCase(PreferencesNotifyCallback callback);

    public ChannelColorUseCase makeChannelColorUseCase(ChannelColorCallback callback);

    public ChannelsSnapshotNotifyUseCase makeChannelsSnapshotNotifyUseCase(
            ChannelsSnapshotNotifyCallback channelsSnapshotNotifyCallback);

    public SelectElementsInteractionUseCase makeSelectElementsInteractionUseCase(
            SelectElementsInteractionCallback selectElementsInteractionCallback);

    public ModelNameNotifyUseCase makeModelNameNotifyUseCase(ModelNameNotifyCallback callback);

    public SetHtmlViewContentInteractionUseCase makeSetHtmlViewContentUseCase();

    public HtmlViewNotifyUseCase makeHtmlViewNotifyUseCase(HtmlViewChangedCallback cb);

    public ColorPreferencesNotifyUseCase makeColorPreferencesNotifyUseCase(ColorPreferencesNotifyCallback callback);

    public TimeMarkersNotifyUseCase makeTimeMarkersNotifyUseCase(TimeMarkersNotifyCallback callback);

    public TimeMarkersInteractionUseCase makeTimeMarkersInteractionUseCase();

    public LoadDataChunkInteractionUseCase makeLoadDataChunkInteractionUseCase(
            LoadDataChunkInteractionCallback callback);

    public LoadDataChunkNotifyUseCase makeLoadDataChunkNotifyUseCase(LoadDataChunkNotifyCallback callback);

    public TableSearchTermNotifyUseCase makeTableSearchTermNotifyUseCase(TableSearchTermNotifyCallback callback,
            String viewID);

    public TableSearchTermInteractionUseCase makeTableSearchTermInteractionUseCase(String viewID);

    public SystemCPUValuesNotifyUseCase makeSystemCPUValuesNotifyUseCase(SystemCPUValuesNotifyCallback callback);

    public DefaultResourceNameNotifyUseCase makeDefaultResourceNameNotifyUseCase();

    public ImportScriptInteractionUseCase makeImportScriptInteractionUseCase(ImportScriptInteractionCallback callback);

    public AnalysisTimespanNotifyUseCase makeAnalysisTimespanNotifyUseCase(AnalysisTimespanNotifyCallback callback);

    public AnalysisTimespanInteractionUseCase makeAnalysisTimespanInteractionUseCase(
            AnalysisTimespanInteractionCallback callback);

    public FileSizeLimitNotifyUseCase makeFileSizeLimitNotifyUseCase(FileSizeLimitNotifyCallback callback);

    public TimelineViewDataNotifyUseCase makeTimelineViewDataNotifyUseCase(TimelineViewDataNotifyCallback callback);

    public ExecuteSTimeSegmentActionInteractionUseCase makeExecuteSTimeSegmentActionInteractionUseCase();

    public LiveModeNotifyUseCase makeLiveModeNotifyUseCase(LiveModeNotifyCallback callback);

    public CreateConnectionInteractionUseCase makeCreateConnectionInteractionUseCase(
            CreateConnectionInteractionCallback callback);

    public RunScriptInteractionUseCase makeRunScriptInteractionUseCase();

    public DataInputUseCase makeDataInputUseCase();

    public LiveViewerHandlerInteractionUseCase makeActivateLiveViewerUseCase(
            LiveViewerHandlerInteractionCallback callback);
}
