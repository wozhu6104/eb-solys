/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.channelsview.treemodel;

import java.util.stream.Collectors;

import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.elektrobit.ebrace.core.interactor.api.channelcolor.ChannelColorUseCase;
import com.elektrobit.ebrace.viewer.channelsview.ChannelsViewPlugin;
import com.elektrobit.ebrace.viewer.channelsview.SColorImagePainter;
import com.elektrobit.ebsolys.core.targetdata.api.color.SColor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.ChannelTreeNode;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel.CommonParameterNames;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

public class ChannelLazyTreeLabelProvider extends StyledCellLabelProvider implements ILabelProvider
{
    private final CommonParameterNames columnName;
    private ChannelColorUseCase useCase;
    private ResourceManager resouceManager;

    public ChannelLazyTreeLabelProvider(CommonParameterNames name)
    {
        this.columnName = name;
    }

    public ChannelLazyTreeLabelProvider(CommonParameterNames color, ChannelColorUseCase channelColorUseCase,
            ResourceManager manager)
    {
        this.columnName = color;
        this.useCase = channelColorUseCase;
        this.resouceManager = manager;
    }

    @Override
    public void update(ViewerCell cell)
    {
        ChannelTreeNode node = (ChannelTreeNode)cell.getElement();

        String cellText = getLineLabelText( node );
        cell.setText( cellText );

        Image icon = getIconForNode( node );
        cell.setImage( icon );

        super.update( cell );
    }

    public String getLineLabelText(ChannelTreeNode node)
    {
        if (node.isLeaf())
        {
            return getSearchName( node );
        }
        else if (columnName.equals( CommonParameterNames.NAME ))
        {
            return node.getNodeName();
        }
        else
        {
            return "";
        }
    }

    private String getSearchName(ChannelTreeNode node)
    {
        String returnValue = "";
        RuntimeEventChannel<?> channel = node.getRuntimeEventChannel();
        Unit<?> unit = channel.getUnit();
        String unitName = unit.getName();

        Object logLevel = channel.getParameter( RuntimeEventChannel.CommonParameterNames.LOG_LEVEL.getName() );
        String logLevelString = logLevel != null ? (String)logLevel : "";

        switch (columnName)
        {
            case NAME :
                returnValue = node.getNodeName();
                break;
            case LOG_LEVEL :
                returnValue = logLevelString;
                break;
            case TYPE :
                returnValue = unitName;
                break;
            case COLOR :
                break;
            default :
                break;
        }

        return returnValue;
    }

    private Image getIconForNode(ChannelTreeNode node)
    {
        switch (columnName)
        {
            case NAME : {
                if (node.isLeaf())
                {
                    return ChannelsViewPlugin.getDefault().getImage( "channel", "png" );
                }
                else
                {
                    return ChannelsViewPlugin.getDefault().getImage( "channel_group", "png" );
                }
            }
            case COLOR : {
                if (node.getRuntimeEventChannel() != null && useCase.channelHasColor( node.getRuntimeEventChannel() ))
                {
                    SColor sColor = useCase.getColorOfChannel( node.getRuntimeEventChannel() );
                    return new SColorImagePainter( resouceManager ).getImageForColor( sColor );
                }
            }
            default :
                return null;
        }
    }

    @Override
    public String getToolTipText(Object element)
    {
        ChannelTreeNode node = (ChannelTreeNode)element;
        RuntimeEventChannel<?> channel = node.getRuntimeEventChannel();
        String toolTipContent = "";
        if (channel != null)
        {
            toolTipContent = getTooltipForChannel( channel );
        }
        else
        {
            toolTipContent = getTooltipForChannelGroup( node );
        }
        return toolTipContent;
    }

    private String getTooltipForChannel(RuntimeEventChannel<?> channel)
    {
        String toolTipContent = "Channel Name: " + channel.getName() + "\n" + "Description: " + channel.getDescription()
                + "\n" + "Parameters:\n   "
                + channel.getParameterNames().stream()
                        .map( parameterName -> parameterName + ":" + channel.getParameter( parameterName ).toString() )
                        .collect( Collectors.joining( "\n   " ) );
        return toolTipContent;
    }

    private String getTooltipForChannelGroup(ChannelTreeNode node)
    {
        return "Group Name: " + node.getFullName();
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
        return 5000;
    }

    @Override
    public Image getImage(Object element)
    {
        ChannelTreeNode node = (ChannelTreeNode)element;
        return getIconForNode( node );
    }

    @Override
    public String getText(Object element)
    {
        String text = null;
        if (element != null)
        {
            ChannelTreeNode node = (ChannelTreeNode)element;
            text = getLineLabelText( node );
        }
        return text;
    }
}
