/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.resources.api.manager;

import java.util.List;
import java.util.Set;

import com.elektrobit.ebrace.core.interactor.api.datainput.DataInputResourceModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourcesFolder;
import com.elektrobit.ebrace.core.interactor.api.resources.model.chart.ChartModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.chart.ChartTypes;
import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionType;
import com.elektrobit.ebrace.core.interactor.api.resources.model.file.FileModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.htmlview.HtmlViewModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.script.RaceScriptResourceModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.snapshot.SnapshotModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.table.TableModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.timelineview.TimelineViewModel;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptInfo;

public interface ResourcesModelManager
{
    public void registerResourceListener(ResourceChangedListener listener);

    public void unregisterResourceListener(ResourceChangedListener listener);

    public void registerTreeListener(ResourceTreeChangedListener listener);

    public void unregisterTreeListener(ResourceTreeChangedListener listener);

    public ConnectionModel createConnection(String name, String host, int port, boolean saveToFile,
            ConnectionType connectionType);

    public List<ConnectionType> getAllConnectionTypes();

    public ChartModel createChart(String name, ChartTypes type);

    public TimelineViewModel createTimelineView(String name);

    public TableModel createTable(String name);

    public SnapshotModel createSnapshot(String name);

    public FileModel createFileModel(String name, String path);

    public void updateScripts(List<RaceScriptInfo> scripts);

    public List<ResourcesFolder> getRootFolders();

    public List<ResourceModel> getConnections();

    public List<ResourceModel> getCharts();

    public List<ResourceModel> getTimelineViews();

    public List<ResourceModel> getChartsWithCertainType(ChartTypes type);

    public List<ResourceModel> getSnapshots();

    public List<ResourceModel> getTables();

    public List<ResourceModel> getResources();

    public List<RaceScriptResourceModel> getAllScripts();

    public List<RaceScriptResourceModel> getUserScripts();

    public List<RaceScriptResourceModel> getPreinstalledScripts();

    public List<ResourceModel> getFiles();

    public List<ResourceModel> getHtmlViews();

    public RaceScriptResourceModel getRaceScriptResourceModel(RaceScriptInfo raceScriptInfo);

    public void deleteResourcesModels(List<ResourceModel> toDelete);

    public boolean isCallbackScriptRunning();

    public boolean isNameUsed(String name, List<ResourceModel> models);

    public Set<String> getUsedConnectionNames();

    public HtmlViewModel createHtmlView(String name, String path);

    public void openResourceModel(ResourceModel resModel);

    public void openDefaultHtmlView();

    public boolean scriptAlreadyExists(String scriptName);

    public DataInputResourceModel createDataInput(String name);

    public List<ResourceModel> getDataInputs();
}
