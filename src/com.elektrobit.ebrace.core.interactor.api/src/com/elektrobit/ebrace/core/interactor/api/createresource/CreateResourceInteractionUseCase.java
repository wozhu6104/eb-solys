/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.api.createresource;

import java.io.File;
import java.util.List;

import com.elektrobit.ebrace.core.interactor.api.common.BaseUseCase;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.chart.ChartModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.chart.ChartTypes;
import com.elektrobit.ebrace.core.interactor.api.resources.model.htmlview.HtmlViewModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.snapshot.SnapshotModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.table.TableModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.timelineview.TimelineViewModel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public interface CreateResourceInteractionUseCase extends BaseUseCase
{
    /**
     * Creates (always) a new Table with the next free name.
     * 
     * @param channels
     * @return
     */
    public TableModel createAndOpenTable(List<RuntimeEventChannel<?>> channels);

    public ChartModel createAndOpenChart(List<RuntimeEventChannel<?>> channels);

    public ChartModel createAndOpenChart(String name, ChartTypes type);

    public TimelineViewModel createAndOpenTimelineView(List<RuntimeEventChannel<?>> channels);

    public SnapshotModel createAndOpenSnapshot(List<RuntimeEventChannel<?>> channels);

    public HtmlViewModel createAndOpenHtmlView(String name, String path);

    public void createTableFromResource(ResourceModel toCopy);

    public void createAndOpenChartFromResource(ResourceModel toCopy);

    public void createAndOpenSnapshotFromResource(ResourceModel toCopy);

    /**
     * If table with the same channels exists, is it returned, otherwise new table is created and returned
     * 
     * @param channels
     * @return
     */
    public ResourceModel createOrGetAndOpenChart(List<RuntimeEventChannel<?>> channels);

    public TableModel createOrGetAndOpenTable(List<RuntimeEventChannel<?>> channels);

    public ResourceModel createOrGetAndOpenResourceAccordingToType(List<RuntimeEventChannel<?>> channels);

    public SnapshotModel createOrGetAndOpenSnapshot(List<RuntimeEventChannel<?>> channels);

    public TimelineViewModel createOrGetAndOpenTimelineView(List<RuntimeEventChannel<?>> channels);

    public void createAndOpenUserScript(File file, String name);

    public boolean isUserScriptAvailable(String scriptName);

}
