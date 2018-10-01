/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.preferences.api;

import java.util.List;

import com.elektrobit.ebrace.core.interactor.api.chart.LineChartModelSettings;
import com.elektrobit.ebrace.core.interactor.api.connect.ConnectionSettings;
import com.elektrobit.ebrace.core.preferences.listener.PreferencesListener;
import com.elektrobit.ebsolys.core.targetdata.api.color.SColor;

public interface PreferencesService
{
    /**
     * Returns the actual set timestamp preference
     * 
     * @return the actual pattern of timestamp
     */
    public String getTimestampFormatPreferences();

    /**
     * Sets the pattern format preferences
     */
    public void setTimestampFormatPreferences(String pattern);

    /**
     * Sets the color preferences
     */
    public void setColorPreferences(List<SColor> colors);

    /**
     * Returns the actual color preferences
     * 
     * @return the actual color preferences
     */
    public List<SColor> getColorPreferences();

    /**
     * Sets the colors' transparency preferences
     */
    public void setColorTransparencyPreferences(double value);

    /**
     * Returns the actual colors' transparency
     * 
     * @return the actual colors' transparency
     */
    public double getColorTransparencyValue();

    /**
     * Sets all connections
     */
    public void setConnections(List<ConnectionSettings> connecions);

    /**
     * Returns the actual stored connections
     * 
     * @return the actual stored connections
     */
    public List<ConnectionSettings> getConnections();

    /**
     * Registers a preferences listener
     * 
     * @param preferencesListener
     *            listener to be registered
     */
    public void registerPreferencesListener(PreferencesListener preferencesListener);

    /**
     * Unregister a preferences listener
     * 
     * @param preferencesListener
     *            listener to be unregistered
     */
    public void unregisterPreferencesListener(PreferencesListener preferencesListener);

    /**
     * Get search terms history for specific table view
     * 
     * @param viewID
     * 
     * @return List of strings previously saved for this viewID
     */
    public List<String> getTableSearchTermsHistory(String viewID);

    /**
     * Set terms history for a view with specific viewID
     */
    public void setTableSearchTermsHistory(List<String> terms, String viewID);

    /**
     * Set length of chart analysis timespan
     * 
     * @param analysisTimespanLength
     */
    public void setAnalysisTimespanLength(long analysisTimespanLength);

    /**
     * Get length of chart analysis timespan
     * 
     * @return timespan length
     */
    public long getAnalysisTimespanLength();

    public LineChartModelSettings getGlobalLineChartSettings();

    public void setLineChartModelSettings(LineChartModelSettings lineChartModelSettings);

    public String getScriptFolderPath();

    public void setScriptFolderPath(String path);

    public void setScriptFolderPathToDefault();

    public String getDefaultScriptFolderPath();
}
