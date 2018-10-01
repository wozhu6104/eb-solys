/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.chartengine.internal;

import java.util.Collection;
import java.util.HashMap;

import org.eclipse.birt.chart.model.attribute.ColorDefinition;
import org.eclipse.birt.chart.model.attribute.impl.ColorDefinitionImpl;

import com.elektrobit.ebrace.core.interactor.api.channelcolor.ChannelColorCallback;
import com.elektrobit.ebrace.core.interactor.api.channelcolor.ChannelColorUseCase;
import com.elektrobit.ebsolys.core.targetdata.api.color.SColor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public class ChannelColorCreator implements ChannelColorCallback
{
    private final static HashMap<String, ColorDefinition> seriesColorDefinitions = new HashMap<String, ColorDefinition>();
    private final ChannelColorUseCase channelColorUseCase;

    public ChannelColorCreator(ChannelColorUseCase channelColorUseCase)
    {
        this.channelColorUseCase = channelColorUseCase;
    }

    private void setColorDefinitionForSeries(String seriesName, ColorDefinition colorDefinition)
    {
        channelColorUseCase.setColorForChannel( seriesName,
                                                colorDefinition.getRed(),
                                                colorDefinition.getGreen(),
                                                colorDefinition.getBlue() );
        seriesColorDefinitions.put( seriesName, colorDefinition );
    }

    public ColorDefinition getColorDefinitionForSeries(String seriesName)
    {
        ColorDefinition newColorDefinition = null;
        if (channelColorUseCase.getColorOfChannel( seriesName ) != null)
        {
            SColor colorAsRaceColor = channelColorUseCase.getColorOfChannel( seriesName );
            newColorDefinition = ColorDefinitionImpl.create( colorAsRaceColor.getRed(),
                                                             colorAsRaceColor.getGreen(),
                                                             colorAsRaceColor.getBlue() );
        }
        else
        {
            newColorDefinition = createDefaultColorForSeries( seriesName );
            setColorDefinitionForSeries( seriesName, newColorDefinition );
        }
        return newColorDefinition;
    }

    private ColorDefinition createDefaultColorForSeries(String seriesName)
    {
        int hashcode = seriesName.hashCode();
        int red = hashcode & 255;
        int green = (hashcode >> 8) & 255;
        int blue = (hashcode >> 16) & 255;
        int alpha = (hashcode >> 32) & 255;
        return ColorDefinitionImpl.create( red, green, blue, alpha );
    }

    @Override
    public void onColorAssigned(Collection<RuntimeEventChannel<?>> channels)
    {
    }

}
