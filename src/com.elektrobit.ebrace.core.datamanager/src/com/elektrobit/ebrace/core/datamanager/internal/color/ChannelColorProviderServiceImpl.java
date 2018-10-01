/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.datamanager.internal.color;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.core.interactor.api.chart.LineChartModelSettings;
import com.elektrobit.ebrace.core.interactor.api.connect.ConnectionSettings;
import com.elektrobit.ebrace.core.preferences.api.PreferencesService;
import com.elektrobit.ebrace.core.preferences.listener.PreferencesListener;
import com.elektrobit.ebsolys.core.targetdata.api.color.ChannelColorProviderService;
import com.elektrobit.ebsolys.core.targetdata.api.color.ColorChannelListener;
import com.elektrobit.ebsolys.core.targetdata.api.color.ColorSettingsPreferenceConstants;
import com.elektrobit.ebsolys.core.targetdata.api.color.SColor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

@Component(service = ChannelColorProviderService.class, immediate = true)
public class ChannelColorProviderServiceImpl implements ChannelColorProviderService, PreferencesListener
{
    private volatile Map<String, SColor> channelColorDefinitions = new LinkedHashMap<String, SColor>();
    private final Set<ColorChannelListener> colorChannelListeners = new HashSet<ColorChannelListener>();

    private PreferencesService preferencesService;
    private List<SColor> nextRaceColors = new ArrayList<SColor>();

    @Reference
    public void bind(PreferencesService preferencesService)
    {
        this.preferencesService = preferencesService;
    }

    public void unbind(PreferencesService preferencesService)
    {
        this.preferencesService = null;
    }

    @Activate
    protected void activate(ComponentContext componentContext)
    {
        this.preferencesService.registerPreferencesListener( this );
    }

    @Override
    public void registerColorChannelListener(ColorChannelListener listener)
    {
        colorChannelListeners.add( listener );
    }

    @Override
    public void unregisterColorChannelListener(ColorChannelListener listener)
    {
        colorChannelListeners.remove( listener );
    }

    @Override
    public void setColorForChannel(RuntimeEventChannel<?> channel, int r, int g, int b)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "channel", channel );
        SColor raceColor = new SColor( r, g, b );
        channelColorDefinitions.put( channel.getName(), raceColor );
        notifyChannelColorAssigned( channel );
    }

    @Override
    public SColor getColorForChannel(RuntimeEventChannel<?> channel)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "channel", channel );

        SColor c = null;
        String channelName = channel.getName();
        if (channelColorDefinitions.containsKey( channelName ) && channelColorDefinitions.get( channelName ) != null)
        {
            c = channelColorDefinitions.get( channelName );
        }

        return c;
    }

    @Override
    public SColor createAndGetColorForChannel(RuntimeEventChannel<?> channel)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "channel", channel );
        SColor c = getColorForChannel( channel );
        if (c == null)
        {
            String channelName = null;
            if (channel != null)
            {
                channelName = channel.getName();
            }
            c = createDefaultColorForChannel( channelName );
            channelColorDefinitions.put( channelName, c );
            notifyChannelColorAssigned( channel );
        }
        return c;
    }

    private SColor createDefaultColorForChannel(String channelName)
    {

        if (nextRaceColors.isEmpty() || nextRaceColors == null)
        {
            List<SColor> colors = preferencesService.getColorPreferences();
            if (colors.isEmpty() || colors == null)
            {
                List<SColor> defaultRaceColorsCopy = new ArrayList<SColor>( ColorSettingsPreferenceConstants.defaultChannelColors );
                nextRaceColors = defaultRaceColorsCopy;
            }
            else
            {
                nextRaceColors = colors;
            }
        }

        return getNextRaceColor();
    }

    private SColor getNextRaceColor()
    {
        SColor nextRaceColor = nextRaceColors.get( 0 );
        nextRaceColors.remove( 0 );
        return nextRaceColor;
    }

    private void notifyChannelColorAssigned(RuntimeEventChannel<?> channel)
    {
        for (ColorChannelListener listener : colorChannelListeners)
        {
            listener.onColorAssignedToChannel( channel );
        }
    }

    @Override
    public boolean hasColor(RuntimeEventChannel<?> channel)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "channel", channel );

        return channelColorDefinitions.containsKey( channel.getName() );
    }

    @Override
    public void onTimestampFormatPreferencesChanged()
    {
    }

    @Override
    public void onColorTransparencyChanged(double value)
    {
    }

    @Override
    public void onTableSearchTermsHistoryChanged(List<String> keystosearch, String viewID)
    {
    };

    @Override
    public void onColorPaletteChanged(List<SColor> colors)
    {
        nextRaceColors = colors;
    }

    @Override
    public void onConnectionsChanged(List<ConnectionSettings> connections)
    {
    }

    @Override
    public void onLineChartModelSettingsChanged(LineChartModelSettings lineChartModelSettings)
    {
    }

    @Override
    public void onScriptFolderPathChanged(String newScriptFolderPath)
    {
    }
}
