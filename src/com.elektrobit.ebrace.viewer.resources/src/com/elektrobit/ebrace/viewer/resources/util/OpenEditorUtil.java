/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.resources.util;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.chart.ChartModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.dependencygraph.DependencyGraphModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.htmlview.HtmlViewModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.snapshot.SnapshotModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.table.TableModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.timelineview.TimelineViewModel;
import com.elektrobit.ebrace.dev.usestatlogsannotationloader.api.UseStatLog;
import com.elektrobit.ebrace.dev.usestatlogsannotationloader.api.UseStatLogTypes;
import com.elektrobit.ebrace.viewer.common.constants.ViewIDs;
import com.elektrobit.ebrace.viewer.resources.editor.ResourcesModelEditorInput;

public class OpenEditorUtil
{
    @UseStatLog(value = UseStatLogTypes.RESOURCE_NUMBER_OF_CHANNELS, parser = UseStatNumberOfChannelsParser.class)
    public static IEditorPart openResourcesEditor(IWorkbenchPage page, ResourceModel model)
    {
        try
        {
            if (page != null)
            {
                IViewPart resourceExplorerView = page.findView( ViewIDs.RESOURCE_EXPLORER_VIEW_ID );
                if (resourceExplorerView != null)
                {
                    resourceExplorerView.setFocus();
                }

                if (model instanceof ChartModel)
                {
                    return page.openEditor( new ResourcesModelEditorInput( model ), Constants.CHART_EDITOR_ID );
                }
                if (model instanceof TableModel)
                {
                    return page.openEditor( new ResourcesModelEditorInput( model ), Constants.TABLE_EDITOR_ID );
                }
                if (model instanceof SnapshotModel)
                {
                    return page.openEditor( new ResourcesModelEditorInput( model ), Constants.EVENT_MAP_EDITOR_ID );
                }
                if (model instanceof HtmlViewModel)
                {
                    return page.openEditor( new ResourcesModelEditorInput( model ), Constants.BROWSER_EDITOR_ID );
                }
                if (model instanceof DependencyGraphModel)
                {
                    return page.openEditor( new ResourcesModelEditorInput( model ),
                                            Constants.DEPENDECY_GRAPH_EDITOR_ID );
                }
                if (model instanceof TimelineViewModel)
                {
                    return page.openEditor( new ResourcesModelEditorInput( model ), Constants.TIMELINE_VIEW_EDITOR_ID );
                }
            }
        }
        catch (PartInitException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
