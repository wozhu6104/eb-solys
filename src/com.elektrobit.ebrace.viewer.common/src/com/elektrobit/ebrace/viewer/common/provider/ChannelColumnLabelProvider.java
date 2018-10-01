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

import java.util.Collection;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import com.elektrobit.ebrace.core.interactor.api.channelcolor.ChannelColorCallback;
import com.elektrobit.ebrace.core.interactor.api.channelcolor.ChannelColorUseCase;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.table.RowFormatter;
import com.elektrobit.ebrace.viewer.common.util.ColorPreferences;
import com.elektrobit.ebsolys.core.targetdata.api.color.SColor;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedNode;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;

public class ChannelColumnLabelProvider extends ColumnLabelProvider implements RowFormatter, ChannelColorCallback
{
    private final ChannelColorUseCase channelColorUseCase;
    private ResourceManager resourceManager = null;

    public ChannelColumnLabelProvider()
    {
        channelColorUseCase = UseCaseFactoryInstance.get().makeChannelColorUseCase( this );
    }

    @Override
    public String getText(Object element)
    {
        String channelName = null;
        if (element instanceof RuntimeEvent<?>)
        {
            channelName = ((RuntimeEvent<?>)element).getRuntimeEventChannel().getName();
        }
        else if (element instanceof DecodedNode)
        {
            DecodedNode decodedNode = (DecodedNode)element;
            channelName = decodedNode.getName();
        }
        else
        {
            if (element instanceof ChannelValueProvider)
            {
                ChannelValueProvider channelValueProvider = (ChannelValueProvider)element;
                String unitName = channelValueProvider.getRuntimeEventChannel().getUnit().getName();
                if (channelValueProvider.getRuntimeEventChannel() != null)
                {
                    channelName = channelValueProvider.getRuntimeEventChannel().getName() + " [" + unitName + "]";
                }
                else
                {
                    channelName = channelValueProvider.getNodes().get( 0 ).getParentTree().getRootNode().getName()
                            + " [" + unitName + "]";
                }
            }
        }
        return channelName;
    }

    @Override
    public Color getForeground(Object element)
    {
        String channelName = null;
        if (element instanceof RuntimeEvent<?>)
        {
            channelName = ((RuntimeEvent<?>)element).getRuntimeEventChannel().getName();
        }
        else if (element instanceof DecodedNode)
        {
            channelName = ((DecodedNode)element).getParentTree().getRootNode().getName();
            return getResourceManager().createColor( new RGB( 0, 0, 0 ) );
        }

        if (channelName != null)
        {
            SColor colorAsRaceColor = channelColorUseCase.getColorOfChannel( channelName );
            return getResourceManager()
                    .createColor( new RGB( colorAsRaceColor.getRed(),
                                           colorAsRaceColor.getGreen(),
                                           colorAsRaceColor.getBlue() ) );

        }
        return super.getForeground( element );
    }

    private ResourceManager getResourceManager()
    {
        if (resourceManager == null)
        {
            resourceManager = new LocalResourceManager( JFaceResources.getResources() );
        }
        return resourceManager;
    }

    @Override
    public void dispose()
    {
        super.dispose();
        if (resourceManager != null)
        {
            resourceManager.dispose();
        }
    }

    @Override
    public Color getBackground(Object element)
    {
        if (element instanceof TimeMarker)
        {
            return ColorPreferences.TIMEMARKER_CELL_HIGHLIGHTED_BG_COLOR;
        }
        return super.getBackground( element );
    }

    @Override
    public void onColorAssigned(Collection<RuntimeEventChannel<?>> channels)
    {
    }

}
