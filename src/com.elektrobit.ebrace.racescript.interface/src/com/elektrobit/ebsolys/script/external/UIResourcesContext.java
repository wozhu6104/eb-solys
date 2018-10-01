/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebsolys.script.external;

public interface UIResourcesContext
{
    enum CHART_TYPE {
        LINE_CHART, GANTT_CHART
    };

    /**
     * Create new table (or get if table already exists) with predefined name
     * 
     * @return RTable
     */
    public STable createOrGetTable(String name);

    /**
     * Returns a table named {@link name} or null
     * 
     * @param name
     *            of desired table
     * @return RTable or null if table with such a name does not exist
     */
    public STable getTable(String name);

    /**
     * Create new chart (or get if chart already exists) with predefined name
     * 
     * @param name
     * @param chartType
     *            LINE_CHART or GANTT_CHART
     * 
     * @return RChart
     */
    public SChart createOrGetChart(String name, CHART_TYPE chartType);

    /**
     * Returns a chart named {@link name} or null
     * 
     * @param name
     *            of desired chart
     * @return RChart or null if chart with such a name does not exist
     */
    public SChart getChart(String name);

    /**
     * Create new snapshot view (or get if snapshot already exists) with predefined name
     * 
     * @return RSnapshot
     */

    /**
     * Create new TimelineView (or get if TimelineView already exists) with predefined name
     * 
     * @return STimelineView
     */
    public STimelineView createOrGetTimelineView(String name);

    /**
     * Returns a TimelineView named {@link name} or null
     * 
     * @param name
     *            of desired TimelineView
     * @return STimelineView or null if table with such a name does not exist
     */
    public STimelineView getTimelineView(String name);

    public SSnapshot createOrGetSnapshot(String name);

    /**
     * Returns a snapshot view named {@link name} or null
     * 
     * @param name
     *            of desired snapshot
     * @return RSnapshot or null if table with such a name does not exist
     */
    public SSnapshot getSnapshot(String name);

    /**
     * Create new html view (or get if it already exists) with predefined name
     * 
     * @return the default html view
     */
    public SHtmlView createOrGetHtmlView(String name);

    /**
     * Set the content of the HTML view which will be treated as HTML text.
     * 
     * @param view
     *            the view to render the HTML text
     * @param text
     *            the HTML text to be rendered
     */
    public void setContent(SHtmlView view, String text);

    /**
     * Retreive a HTML view by name
     * 
     * @param name
     *            the name of the view
     * 
     * @return the default html view
     */
    public SHtmlView getHtmlView(String name);

    /**
     * Trigger the html view to update it's content without refreshing the page
     * 
     * @param view
     * 
     * @param function
     */
    public void callJavaScriptFunction(SHtmlView view, String function, String arg);
}
