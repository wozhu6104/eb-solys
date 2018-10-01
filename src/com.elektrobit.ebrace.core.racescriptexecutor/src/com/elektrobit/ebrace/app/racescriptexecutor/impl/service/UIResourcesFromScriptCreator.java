/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.app.racescriptexecutor.impl.service;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;

import com.elektrobit.ebrace.app.racescriptexecutor.impl.uimodels.SChartImpl;
import com.elektrobit.ebrace.app.racescriptexecutor.impl.uimodels.SHtmlViewImpl;
import com.elektrobit.ebrace.app.racescriptexecutor.impl.uimodels.SSnapshotImpl;
import com.elektrobit.ebrace.app.racescriptexecutor.impl.uimodels.STableImpl;
import com.elektrobit.ebrace.app.racescriptexecutor.impl.uimodels.STimelineViewImpl;
import com.elektrobit.ebrace.core.interactor.api.createresource.CreateResourceInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.chart.ChartModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.chart.ChartTypes;
import com.elektrobit.ebrace.core.interactor.api.resources.model.htmlview.HtmlViewModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.snapshot.SnapshotModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.table.TableModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.timelineview.TimelineViewModel;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;
import com.elektrobit.ebsolys.script.external.Console;
import com.elektrobit.ebsolys.script.external.EbSolysProVersionNeededException;
import com.elektrobit.ebsolys.script.external.SChart;
import com.elektrobit.ebsolys.script.external.SHtmlView;
import com.elektrobit.ebsolys.script.external.SSnapshot;
import com.elektrobit.ebsolys.script.external.STable;
import com.elektrobit.ebsolys.script.external.STimelineView;
import com.elektrobit.ebsolys.script.external.UIResourcesContext;

public class UIResourcesFromScriptCreator implements UIResourcesContext, CreateResourceInteractionCallback
{
    private final Console scriptConsole;
    private final ResourcesModelManager resourcesModelManager;

    public UIResourcesFromScriptCreator(Console scriptConsole, ResourcesModelManager resourcesModelManager)
    {
        this.scriptConsole = scriptConsole;
        this.resourcesModelManager = resourcesModelManager;
    }

    @Override
    public STable createOrGetTable(String name)
    {
        STable foundTable = getTable( name );
        if (foundTable != null)
        {
            return foundTable;
        }
        else
        {
            TableModel table = resourcesModelManager.createTable( name );
            STable rTable = new STableImpl( table, resourcesModelManager );
            if (name != null)
            {
                rTable.setName( name );
            }
            resourcesModelManager.openResourceModel( table );
            return rTable;
        }
    }

    @Override
    public STable getTable(String name)
    {
        List<ResourceModel> tables = resourcesModelManager.getTables();
        for (ResourceModel resourceModel : tables)
        {
            if (resourceModel.getName().equals( name ) && resourceModel instanceof TableModel)
            {
                STable table = new STableImpl( (TableModel)resourceModel, resourcesModelManager );
                return table;
            }
        }
        return null;
    }

    @Override
    public void onDerivedResourceAlreadyExists()
    {
        scriptConsole.println( "ERROR: Derived resource already exists" );
    }

    @Override
    public void onChartChannelsTypeMismatch()
    {
        scriptConsole
                .println( "ERROR: When creating a chart, all channels need to have numerical type only (line chart) or boolean type only (gantt chart)" );
    }

    @Override
    public SChart createOrGetChart(String name, CHART_TYPE chartType)
    {
        SChart foundChart = getChart( name );
        if (foundChart != null && foundChart.getType() != chartType)
        {
            scriptConsole.println( "ERROR: Chart " + name + " already exists with type " + foundChart.getType() );
            return null;
        }
        else if (foundChart != null)
        {
            return foundChart;
        }

        ChartTypes internalType = chartType == CHART_TYPE.LINE_CHART ? ChartTypes.LINE_CHART : ChartTypes.GANTT_CHART;
        ChartModel chartModel = resourcesModelManager.createChart( name, internalType );
        SChart chart = new SChartImpl( chartModel, scriptConsole, resourcesModelManager );
        resourcesModelManager.openResourceModel( chartModel );
        return chart;
    }

    @Override
    public SChart getChart(String name)
    {
        List<ResourceModel> charts = resourcesModelManager.getCharts();
        for (ResourceModel resourceModel : charts)
        {
            if (resourceModel.getName().equals( name ) && resourceModel instanceof ChartModel)
            {
                SChart chart = new SChartImpl( (ChartModel)resourceModel, scriptConsole, resourcesModelManager );
                return chart;
            }
        }
        return null;
    }

    @Override
    public STimelineView createOrGetTimelineView(String name)
    {
        STimelineView foundTimelineView = getTimelineView( name );
        if (foundTimelineView != null)
        {
            return foundTimelineView;
        }
        else
        {
            TimelineViewModel model = resourcesModelManager.createTimelineView( name );
            STimelineView timelineView = new STimelineViewImpl( model, scriptConsole, resourcesModelManager );
            model.setName( name );
            resourcesModelManager.openResourceModel( model );
            return timelineView;
        }
    }

    @Override
    public STimelineView getTimelineView(String name)
    {
        List<ResourceModel> timelineViews = resourcesModelManager.getTimelineViews();
        for (ResourceModel resourceModel : timelineViews)
        {
            if (resourceModel.getName().equals( name ))
            {
                STimelineView timelineView = new STimelineViewImpl( (TimelineViewModel)resourceModel,
                                                                    scriptConsole,
                                                                    resourcesModelManager );
                return timelineView;
            }
        }
        return null;
    }

    @Override
    public SSnapshot createOrGetSnapshot(String name)
    {
        SSnapshot foundSnapshot = getSnapshot( name );
        if (foundSnapshot != null)
        {
            return foundSnapshot;
        }
        else
        {
            SnapshotModel snapshotModel = resourcesModelManager.createSnapshot( name );
            SSnapshot rSnapshot = new SSnapshotImpl( snapshotModel, resourcesModelManager );
            if (name != null)
            {
                rSnapshot.setName( name );
            }
            resourcesModelManager.openResourceModel( snapshotModel );
            return rSnapshot;
        }
    }

    @Override
    public SSnapshot getSnapshot(String name)
    {
        List<ResourceModel> snapshots = resourcesModelManager.getSnapshots();
        for (ResourceModel resourceModel : snapshots)
        {
            if (resourceModel.getName().equals( name ) && resourceModel instanceof SnapshotModel)
            {
                SSnapshot snapshot = new SSnapshotImpl( (SnapshotModel)resourceModel, resourcesModelManager );
                return snapshot;
            }
        }
        return null;
    }

    @Override
    public SHtmlView getHtmlView(String name)
    {
        List<ResourceModel> htmlViews = resourcesModelManager.getHtmlViews();
        for (ResourceModel resourceModel : htmlViews)
        {
            if (resourceModel.getName().equals( name ) && resourceModel instanceof HtmlViewModel)
            {
                SHtmlView htmlView = new SHtmlViewImpl( (HtmlViewModel)resourceModel, resourcesModelManager );
                return htmlView;
            }
        }
        return null;
    }

    @Override
    public SHtmlView createOrGetHtmlView(String name)
    {
        if (name.equals( "Example" ))
        {
            scriptConsole.println( "WARNING! Example view cannot be modified or deleted." );
            return null;
        }

        SHtmlView foundView = getHtmlView( name );
        if (foundView != null)
        {
            return foundView;
        }
        else
        {
            String url = createDefaultHtmlFilesUrl( name );
            if (url != null)
            {
                HtmlViewModel htmlView = resourcesModelManager.createHtmlView( name, url.toString() );
                SHtmlView rHtmlView = new SHtmlViewImpl( htmlView, resourcesModelManager );
                resourcesModelManager.openResourceModel( htmlView );
                return rHtmlView;
            }
        }
        scriptConsole.println( "WARNING! Couldn't create html file." );
        return null;
    }

    private String createDefaultHtmlFilesUrl(String name)
    {

        try
        {
            return new File( name + ".html" ).toURI().toURL().toString();
        }
        catch (MalformedURLException e)
        {
            scriptConsole.println( "WARNING! Cannot create html file: " + e.toString() );
        }

        return null;
    }

    @Override
    public void setContent(SHtmlView view, String text)
    {
        view.setContent( text );
    }

    @Override
    public void callJavaScriptFunction(SHtmlView view, String function, String arg)
    {
        view.callJavaScriptFunction( function, arg );
    }

    @Override
    public void onProVersionNotAvailable()
    {
        throw new EbSolysProVersionNeededException( "Please upgrade your copy to EB solys pro in order to use this feature." );
    }
}
