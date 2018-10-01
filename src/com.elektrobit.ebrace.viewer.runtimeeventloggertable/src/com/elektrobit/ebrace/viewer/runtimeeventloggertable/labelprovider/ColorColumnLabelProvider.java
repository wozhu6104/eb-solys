/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.runtimeeventloggertable.labelprovider;

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
import com.elektrobit.ebrace.core.interactor.api.resources.model.table.TableModel;
import com.elektrobit.ebrace.viewer.common.provider.ChannelValueProvider;
import com.elektrobit.ebrace.viewer.common.util.ColorPreferences;
import com.elektrobit.ebsolys.core.targetdata.api.color.SColor;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedNode;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;

public class ColorColumnLabelProvider extends ColumnLabelProvider implements ChannelColorCallback
{
    private final ChannelColorUseCase channelColorUseCase;
    private final ResourceManager resourceManager = new LocalResourceManager( JFaceResources.getResources() );

    public ColorColumnLabelProvider()
    {
        channelColorUseCase = UseCaseFactoryInstance.get().makeChannelColorUseCase( this );
    }

    public void assignColors(TableModel model)
    {
        for (RuntimeEventChannel<?> channel : model.getChannels())
        {
            channelColorUseCase.getColorOfChannel( channel );
        }
    }

    @Override
    public Color getBackground(Object element)
    {
        Color result = null;
        if (element instanceof TimeMarker)
        {
            result = ColorPreferences.TIMEMARKER_CELL_HIGHLIGHTED_BG_COLOR;
        }
        else
        {
            result = getColorOfChannel( element );
        }
        return result;
    }

    private Color getColorOfChannel(Object element)
    {
        Color result = null;
        String channelName = null;
        if (element instanceof RuntimeEvent<?>)
        {
            channelName = ((RuntimeEvent<?>)element).getRuntimeEventChannel().getName();
        }
        else if (element instanceof DecodedNode)
        {
            channelName = ((DecodedNode)element).getParentTree().getRootNode().getName();
        }
        else if (element instanceof ChannelValueProvider)
        {
            channelName = ((ChannelValueProvider)element).getRuntimeEventChannel().getName();
        }

        if (channelName != null)
        {
            SColor colorAsRaceColor = channelColorUseCase.getColorOfChannel( channelName );
            result = resourceManager.createColor( new RGB( colorAsRaceColor.getRed(),
                                                           colorAsRaceColor.getGreen(),
                                                           colorAsRaceColor.getBlue() ) );
        }

        return result;
    }

    @Override
    public String getText(Object element)
    {
        return null;
    }

    @Override
    public void dispose()
    {
        super.dispose();
        resourceManager.dispose();
    }

    @Override
    public void onColorAssigned(Collection<RuntimeEventChannel<?>> channels)
    {
    }

}
