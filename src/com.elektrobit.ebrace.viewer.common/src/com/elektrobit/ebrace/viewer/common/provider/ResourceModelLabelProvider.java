/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.provider;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

import com.elektrobit.ebrace.core.interactor.api.datainput.DataInputResourceModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceTreeNode;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourcesFolder;
import com.elektrobit.ebrace.core.interactor.api.resources.model.chart.ChartModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.dependencygraph.DependencyGraphModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.file.FileModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.htmlview.HtmlViewModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.script.RaceScriptResourceModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.snapshot.SnapshotModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.table.TableModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.timelineview.TimelineViewModel;
import com.elektrobit.ebrace.viewer.common.ViewerCommonPlugin;

public class ResourceModelLabelProvider extends CellLabelProvider implements ILabelProvider
{
    private final static Logger LOG = Logger.getLogger( ResourceModelLabelProvider.class );
    private Font normalFont;
    private final FontData data = Display.getDefault().getSystemFont().getFontData()[0];
    private final Font boldFont = new Font( Display.getDefault(),
                                            new FontData( data.getName(), data.getHeight(), SWT.BOLD | SWT.ITALIC ) );

    @Override
    public void update(ViewerCell cell)
    {
        if (normalFont == null)
        {
            normalFont = cell.getFont();
        }

        setCellFont( cell );
        setIcon( cell );
        setCellText( cell );

    }

    private void setCellFont(ViewerCell cell)
    {
        Object element = cell.getElement();

        if (element instanceof RaceScriptResourceModel)
        {
            RaceScriptResourceModel script = (RaceScriptResourceModel)element;
            if (script.getScriptInfo().isRunning() || script.getScriptInfo().isRunningAsCallbackScript())
            {
                cell.setFont( boldFont );
                return;
            }
        }

        if (element instanceof ConnectionModel)
        {
            ConnectionModel connectionModel = (ConnectionModel)element;
            if (connectionModel.isConnected())
            {
                cell.setFont( boldFont );
                return;
            }
        }

        cell.setFont( normalFont );
    }

    private void setIcon(ViewerCell cell)
    {
        Object element = cell.getElement();
        cell.setImage( getImage( element ) );
    }

    private void setCellText(ViewerCell cell)
    {
        String text = getText( cell.getElement() );
        cell.setText( text );
    }

    @Override
    public Image getImage(Object element)
    {
        if (element instanceof ResourcesFolder)
        {
            return ViewerCommonPlugin.getDefault().getImage( "folder", "png" );
        }
        else if (element instanceof ChartModel)
        {
            ChartModel model = (ChartModel)element;
            switch (model.getType())
            {
                case GANTT_CHART :
                    return ViewerCommonPlugin.getDefault().getImage( "chart_gantt", "png" );
                case LINE_CHART :
                    return ViewerCommonPlugin.getDefault().getImage( "chart_line", "png" );
                default :
                    break;
            }
        }
        else if (element instanceof TimelineViewModel)
        {
            return ViewerCommonPlugin.getDefault().getImage( "chart_gantt", "png" );
        }
        else if (element instanceof TableModel)
        {
            return ViewerCommonPlugin.getDefault().getImage( "table", "png" );
        }
        else if (element instanceof SnapshotModel)
        {
            return ViewerCommonPlugin.getDefault().getImage( "snapshot", "png" );
        }
        else if (element instanceof RaceScriptResourceModel)
        {
            return ViewerCommonPlugin.getDefault().getImage( "script", "png" );
        }
        else if (element instanceof FileModel)
        {
            return ViewerCommonPlugin.getDefault().getImage( "file_solys", "png" );
        }
        else if (element instanceof HtmlViewModel)
        {
            return ViewerCommonPlugin.getDefault().getImage( "race-browser", "png" );
        }
        else if (element instanceof DependencyGraphModel)
        {
            return ViewerCommonPlugin.getDefault().getImage( "dependency_graph", "png" );
        }
        else if (element instanceof ConnectionModel)
        {
            return ((ConnectionModel)element).isConnected()
                    ? ViewerCommonPlugin.getDefault().getImage( "connection_connected", "png" )
                    : ViewerCommonPlugin.getDefault().getImage( "connection_disconnected", "png" );
        }
        else if (element instanceof DataInputResourceModel)
        {
            return ((DataInputResourceModel)element).isConnected()
                    ? ViewerCommonPlugin.getDefault().getImage( "connection_connected", "png" )
                    : ViewerCommonPlugin.getDefault().getImage( "connection_disconnected", "png" );
        }
        else
        {
            LOG.warn( "Couldn't find icon for type " + element.getClass() );
        }
        return null;
    }

    @Override
    public String getText(Object element)
    {
        if (element instanceof ChartModel)
        {
            ChartModel model = (ChartModel)element;
            return model.getName() + " (" + model.getType().getName() + ")";
        }
        else if (element instanceof ResourceTreeNode)
        {
            ResourceTreeNode treeNode = (ResourceTreeNode)element;
            return treeNode.getName();
        }
        else
        {
            LOG.warn( "Unexpected type of resource " + element.getClass() );
        }
        return null;
    }

    @Override
    public String getToolTipText(Object element)
    {
        if (element instanceof FileModel)
        {
            FileModel file = (FileModel)element;
            return file.getPath();
        }
        else if (element instanceof ConnectionModel)
        {
            ConnectionModel connection = (ConnectionModel)element;
            return connection.getConnectionType().getName() + " " + connection.getHost() + ":" + connection.getPort();
        }
        else
        {
            return super.getToolTipText( element );
        }
    }

    @Override
    public void dispose()
    {
        boldFont.dispose();
        super.dispose();
    }

    @Override
    public Point getToolTipShift(Object object)
    {
        return new Point( 15, 15 );
    }

    @Override
    public int getToolTipDisplayDelayTime(Object object)
    {
        return 100;
    }

    @Override
    public int getToolTipTimeDisplayed(Object object)
    {
        return 7000;
    }

}
