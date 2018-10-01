/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.preferences.impl;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.osgi.service.component.annotations.Component;

import com.elektrobit.ebrace.common.utils.GenericListenerCaller;
import com.elektrobit.ebrace.core.interactor.api.chart.LineChartModelSettings;
import com.elektrobit.ebrace.core.interactor.api.connect.ConnectionSettings;
import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionType;
import com.elektrobit.ebrace.core.preferences.api.PreferencesService;
import com.elektrobit.ebrace.core.preferences.listener.PreferencesListener;
import com.elektrobit.ebsolys.core.targetdata.api.color.SColor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

@Component
public class PreferencesServiceImpl implements PreferencesService
{
    private final static String ANALYSIS_TIMESPAN_PREFERENCES_KEY = "analysisTimespan";
    private final static String LINECHART_MODEL_SETTINGS_KEY = "linechartModelSettings";

    private final static String TIMESTAMP_PREFERENCES_KEY = "timestampPreferences";
    private final static String TIMESTAMP_PREFERENCES_DESCRIPTION = "TimestampPreferences";

    private final static String COLOR_PREFERENCES_KEY = "colorPreferences";
    private final static String COLOR_PREFERENCES_DESCRIPTION = "ColorPreferences";
    private final static String COLOR_TRANSPARENCY_PREFERENCES_KEY = "colorTransparencyPreferences";

    private final static double DEFAULT_TRANSPARENCY_VALUE = 0.8;
    private final static String COLOR_TRANSPARENCY_PREFERENCES_DESCRIPTION = "ColorTransparencyPreferences";

    private final static String CONNECTION_PREFERENCES_KEY = "connections";
    private final static String CONNECTION_PREFERENCES_DESCRIPTION = "ConnectionSettings";

    private final static String TABLE_SEARCH_HISTORY_KEY = "tableSearchKeys:";
    private final static String DEFAULT_VALUE_TIMESTAMP_FORMAT = "HH:mm:ss.SSS";

    private final static String SCRIPT_FOLDER_PATH_KEY = "scriptFolderPath";
    private final static String DEFAULT_SCRIPT_FOLDER = File.separator + "RaceScripts";

    private static final long DEFAULT_ANALYSIS_TIMESPAN_VALUE = 30000000;

    private final GenericListenerCaller<PreferencesListener> preferencesListeners = new GenericListenerCaller<PreferencesListener>();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter( ConnectionType.class, new PreferenceConnectionTypeInstanceCreator() ).create();
    private final PropertiesStore propertiesStore;
    private final ScriptFolderDefaultPathProvider defaultPathProvider;

    public PreferencesServiceImpl()
    {
        this( new PropertiesStore(), () -> Platform.getLocation().toFile().getAbsolutePath() + DEFAULT_SCRIPT_FOLDER );
    }

    public PreferencesServiceImpl(PropertiesStore propertiesStore, ScriptFolderDefaultPathProvider defaultPathProvider)
    {
        this.defaultPathProvider = defaultPathProvider;
        this.propertiesStore = propertiesStore;
    }

    @Override
    public void registerPreferencesListener(PreferencesListener preferencesListener)
    {
        preferencesListeners.add( preferencesListener );
    }

    @Override
    public void unregisterPreferencesListener(PreferencesListener preferencesListener)
    {
        preferencesListeners.remove( preferencesListener );
    }

    private void storePreferenceForKey(String key, Object value, String description)
    {
        propertiesStore.storePreferenceForKey( key, value, description );
    }

    @Override
    public void setTimestampFormatPreferences(String pattern)
    {
        storePreferenceForKey( TIMESTAMP_PREFERENCES_KEY, pattern, TIMESTAMP_PREFERENCES_DESCRIPTION );
        preferencesListeners.notifyListeners( (listener) -> listener.onTimestampFormatPreferencesChanged() );
    }

    @Override
    public String getTimestampFormatPreferences()
    {
        String timestampFormat = getPreferenceValue( TIMESTAMP_PREFERENCES_KEY );
        if (timestampFormat == null || timestampFormat.isEmpty())
        {
            timestampFormat = DEFAULT_VALUE_TIMESTAMP_FORMAT;
        }
        return timestampFormat;
    }

    @Override
    public List<String> getTableSearchTermsHistory(String viewID)
    {
        String historyAsString = getPreferenceValue( getTableHistoryKey( viewID ) );
        Type collectionType = new TypeToken<List<String>>()
        {
        }.getType();
        List<String> terms = gson.fromJson( historyAsString, collectionType );
        if (terms == null)
        {
            return Collections.emptyList();
        }
        return terms;
    }

    private java.lang.String getTableHistoryKey(String viewID)
    {
        return TABLE_SEARCH_HISTORY_KEY + viewID;
    }

    @Override
    public void setTableSearchTermsHistory(final List<String> terms, final String viewID)
    {
        String json = gson.toJson( terms );
        storePreferenceForKey( getTableHistoryKey( viewID ), json, "" );

        preferencesListeners
                .notifyListeners( (listener) -> listener.onTableSearchTermsHistoryChanged( terms, viewID ) );
    }

    @Override
    public void setColorPreferences(final List<SColor> colors)
    {
        String colorsJson = gson.toJson( colors );
        storePreferenceForKey( COLOR_PREFERENCES_KEY, colorsJson, COLOR_PREFERENCES_DESCRIPTION );

        preferencesListeners.notifyListeners( (listener) -> listener.onColorPaletteChanged( colors ) );
    }

    @Override
    public List<SColor> getColorPreferences()
    {
        String colorPreferenceAsString = getPreferenceValue( COLOR_PREFERENCES_KEY );
        Type collectionType = new TypeToken<List<SColor>>()
        {
        }.getType();
        List<SColor> colors = gson.fromJson( colorPreferenceAsString, collectionType );
        if (colors == null)
        {
            return Collections.emptyList();
        }

        return colors;
    }

    @Override
    public void setColorTransparencyPreferences(final double value)
    {
        storePreferenceForKey( COLOR_TRANSPARENCY_PREFERENCES_KEY, value, COLOR_TRANSPARENCY_PREFERENCES_DESCRIPTION );

        preferencesListeners.notifyListeners( (listener) -> listener.onColorTransparencyChanged( value ) );
    }

    @Override
    public double getColorTransparencyValue()
    {
        String transparencyPrefAsString = getPreferenceValue( COLOR_TRANSPARENCY_PREFERENCES_KEY );
        if (transparencyPrefAsString == null)
        {
            return DEFAULT_TRANSPARENCY_VALUE;
        }
        else
        {
            return Double.parseDouble( transparencyPrefAsString );
        }
    }

    @Override
    public void setConnections(List<ConnectionSettings> connections)
    {
        String jsonString = gson.toJson( connections );
        storePreferenceForKey( CONNECTION_PREFERENCES_KEY, jsonString, CONNECTION_PREFERENCES_DESCRIPTION );

        preferencesListeners.notifyListeners( (listener) -> listener.onConnectionsChanged( connections ) );
    }

    @Override
    public List<ConnectionSettings> getConnections()
    {
        String connectionPreferenceAsString = getPreferenceValue( CONNECTION_PREFERENCES_KEY );
        Type collectionType = new TypeToken<List<ConnectionSettings>>()
        {
        }.getType();

        List<ConnectionSettings> connections = gson.fromJson( connectionPreferenceAsString, collectionType );
        if (connections == null)
        {
            return Collections.emptyList();
        }
        return connections;
    }

    private String getPreferenceValue(String key)
    {
        return propertiesStore.getPreferenceValue( key );
    }

    @Override
    public void setAnalysisTimespanLength(long analysisTimespanLength)
    {
        storePreferenceForKey( ANALYSIS_TIMESPAN_PREFERENCES_KEY, analysisTimespanLength, "" );
    }

    @Override
    public long getAnalysisTimespanLength()
    {
        String analysisTimespanLengthString = getPreferenceValue( ANALYSIS_TIMESPAN_PREFERENCES_KEY );
        if (analysisTimespanLengthString == null)
        {
            return DEFAULT_ANALYSIS_TIMESPAN_VALUE;
        }
        else
        {
            return Long.parseLong( analysisTimespanLengthString );
        }
    }

    @Override
    public LineChartModelSettings getGlobalLineChartSettings()
    {
        String lineChartSettingsAsString = getPreferenceValue( LINECHART_MODEL_SETTINGS_KEY );
        if (lineChartSettingsAsString == null)
        {
            return new LineChartModelSettings();
        }
        else
        {
            LineChartModelSettings loadedSettings = gson.fromJson( lineChartSettingsAsString,
                                                                   LineChartModelSettings.class );
            return loadedSettings;
        }
    }

    @Override
    public void setLineChartModelSettings(LineChartModelSettings lineChartModelSettings)
    {
        String settingsAsJson = gson.toJson( lineChartModelSettings );
        storePreferenceForKey( LINECHART_MODEL_SETTINGS_KEY, settingsAsJson, "" );
        preferencesListeners
                .notifyListeners( (listener) -> listener.onLineChartModelSettingsChanged( lineChartModelSettings ) );
    }

    @Override
    public String getScriptFolderPath()
    {
        String scriptFolderPath = getPreferenceValue( SCRIPT_FOLDER_PATH_KEY );
        if (scriptFolderPath == null)
        {
            return defaultPathProvider.getDefaultPath();
        }
        else
        {
            return scriptFolderPath;
        }
    }

    @Override
    public void setScriptFolderPath(String path)
    {
        storePreferenceForKey( SCRIPT_FOLDER_PATH_KEY, path, "" );
        preferencesListeners.notifyListeners( (listener) -> listener.onScriptFolderPathChanged( path ) );
    }

    @Override
    public void setScriptFolderPathToDefault()
    {
        setScriptFolderPath( defaultPathProvider.getDefaultPath() );
    }

    @Override
    public String getDefaultScriptFolderPath()
    {
        return defaultPathProvider.getDefaultPath();
    }
}
