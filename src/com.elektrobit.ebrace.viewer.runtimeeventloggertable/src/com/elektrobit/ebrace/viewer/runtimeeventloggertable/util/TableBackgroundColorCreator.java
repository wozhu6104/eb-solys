/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.runtimeeventloggertable.util;

import java.util.Collection;
import java.util.List;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import com.elektrobit.ebrace.core.interactor.api.channelcolor.ChannelColorCallback;
import com.elektrobit.ebrace.core.interactor.api.channelcolor.ChannelColorUseCase;
import com.elektrobit.ebrace.core.interactor.api.channelcolor.ColorPreferencesNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.channelcolor.ColorPreferencesNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.viewer.common.RColorRegistry;
import com.elektrobit.ebsolys.core.targetdata.api.color.SColor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public class TableBackgroundColorCreator implements ChannelColorCallback, ColorPreferencesNotifyCallback
{
    private final ChannelColorUseCase channelColorUseCase;
    private final ColorPreferencesNotifyUseCase colorPreferencesNotifyUseCase;

    private double colorTransparency;

    public TableBackgroundColorCreator()
    {
        channelColorUseCase = UseCaseFactoryInstance.get().makeChannelColorUseCase( this );
        colorPreferencesNotifyUseCase = UseCaseFactoryInstance.get().makeColorPreferencesNotifyUseCase( this );
    }

    public Color createBackgroundColorForChannel(RuntimeEvent<?> runtimeEvent)
    {
        RuntimeEventChannel<?> channel = ((RuntimeEvent<?>)runtimeEvent).getRuntimeEventChannel();
        String channelName = channel.getName();
        if (channelName != null)
        {
            SColor colorAsRaceColor = channelColorUseCase.getColorOfChannel( channel );
            RGB colorBrighter = makeColorBrighter( colorAsRaceColor, colorTransparency );
            return RColorRegistry.createOrGetColor( colorBrighter );
        }
        return null;
    }

    private RGB makeColorBrighter(SColor color, double tintFactor)
    {
        int newR = (int)(color.getRed() + (255 - color.getRed()) * tintFactor);
        int newG = (int)(color.getGreen() + (255 - color.getGreen()) * tintFactor);
        int newB = (int)(color.getBlue() + (255 - color.getBlue()) * tintFactor);
        return new RGB( newR, newG, newB );
    }

    @Override
    public void onColorAssigned(Collection<RuntimeEventChannel<?>> channels)
    {
    }

    @Override
    public void onColorTransparencyChanged(double value)
    {
        colorTransparency = value;
    }

    @Override
    public void onColorPaletteChanged(List<SColor> newColorPalette)
    {
    }

    public void dispose()
    {
        channelColorUseCase.unregister();
        colorPreferencesNotifyUseCase.unregister();
    }
}
