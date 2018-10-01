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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.birt.chart.model.Chart;
import org.eclipse.birt.chart.model.ChartWithAxes;
import org.eclipse.birt.chart.model.attribute.Bounds;
import org.eclipse.birt.chart.model.attribute.ColorDefinition;
import org.eclipse.birt.chart.model.attribute.FontDefinition;
import org.eclipse.birt.chart.model.attribute.HorizontalAlignment;
import org.eclipse.birt.chart.model.attribute.Insets;
import org.eclipse.birt.chart.model.attribute.LineStyle;
import org.eclipse.birt.chart.model.attribute.StyledComponent;
import org.eclipse.birt.chart.model.attribute.TextAlignment;
import org.eclipse.birt.chart.model.attribute.VerticalAlignment;
import org.eclipse.birt.chart.model.attribute.impl.ColorDefinitionImpl;
import org.eclipse.birt.chart.model.attribute.impl.FontDefinitionImpl;
import org.eclipse.birt.chart.model.attribute.impl.InsetsImpl;
import org.eclipse.birt.chart.model.attribute.impl.LineAttributesImpl;
import org.eclipse.birt.chart.model.attribute.impl.TextAlignmentImpl;
import org.eclipse.birt.chart.model.component.Axis;
import org.eclipse.birt.chart.model.component.Series;
import org.eclipse.birt.chart.model.data.SeriesDefinition;
import org.eclipse.birt.chart.model.type.AreaSeries;
import org.eclipse.birt.chart.model.type.LineSeries;
import org.eclipse.birt.chart.style.IStyle;
import org.eclipse.birt.chart.style.IStyleProcessor;
import org.eclipse.birt.chart.style.SimpleStyle;

/**
 * This style Processor is used to implement the style change when the drawable bounds change size, and different
 * settings should be used.
 * 
 * Currently, there are 3 size profiles, each with different font size, marker size and marker visibility settings.
 * 
 * Before rendering, the current bounds should be passed so the profile can be updated.
 *
 */
public class ChartStyleProcessor implements IStyleProcessor
{
    public static final int FONT_SIZE = 9;
    public static final String CHART_FONT = "BookAntique";

    private enum StyleProfile {
        SMALL, MEDIUM, LARGE
    }

    private static class ChartStyleSettings
    {

        private final SimpleStyle style;
        private final boolean markerVisible;
        private final int markerSize;

        private ChartStyleSettings(SimpleStyle style, boolean markerVisible, int markerSize)
        {
            this.style = style;
            this.markerVisible = markerVisible;
            this.markerSize = markerSize;
        }

        public static ChartStyleSettings create(float fontSize, boolean markersVisible, int markerSize)
        {
            TextAlignment ta = TextAlignmentImpl.create();
            ta.setHorizontalAlignment( HorizontalAlignment.CENTER_LITERAL );
            ta.setVerticalAlignment( VerticalAlignment.BOTTOM_LITERAL );

            ColorDefinition color = ColorDefinitionImpl.BLACK();
            ColorDefinition bgColor = ColorDefinitionImpl.RED();

            Insets insets = InsetsImpl.create( 1.0, 1.0, 1.0, 1.0 );

            FontDefinition font = FontDefinitionImpl.create( CHART_FONT, // $NON-NLS-1$
                                                             fontSize,
                                                             false,
                                                             false,
                                                             false,
                                                             false,
                                                             true,
                                                             0.0,
                                                             ta.copyInstance() );

            SimpleStyle style = new SimpleStyle( font, color, bgColor, null, insets );

            return new ChartStyleSettings( style, markersVisible, markerSize );
        }

        public SimpleStyle getStyle()
        {
            return style;
        }

        public boolean getMarkerVisible()
        {
            return markerVisible;
        }

        public int getMarkerSize()
        {
            return markerSize;
        }
    }

    private static final int STYLE_MEDIUM = 200;
    private static final int STYLE_BIG = 500;

    private static Map<StyleProfile, ChartStyleSettings> styles = new HashMap<ChartStyleProcessor.StyleProfile, ChartStyleSettings>();

    private static Bounds lastBounds = null;

    private static ChartStyleProcessor instance = null;

    synchronized public static ChartStyleProcessor getInstance()
    {
        if (instance == null)
        {
            instance = new ChartStyleProcessor();
        }

        return instance;
    }

    static
    {
        ChartStyleSettings largeSettings = ChartStyleSettings.create( FONT_SIZE, false, 3 );
        styles.put( StyleProfile.LARGE, largeSettings );

        ChartStyleSettings mediumSettings = ChartStyleSettings.create( FONT_SIZE, false, 3 );
        styles.put( StyleProfile.MEDIUM, mediumSettings );

        ChartStyleSettings smallSettings = ChartStyleSettings.create( FONT_SIZE, false, 3 );
        styles.put( StyleProfile.SMALL, smallSettings );
    }

    private ChartStyleProcessor()
    {
        super();
    }

    public void setRenderingSizeHint(Bounds bounds)
    {
        lastBounds = bounds;
    }

    @Override
    public ColorDefinition getDefaultBackgroundColor()
    {
        return styles.get( StyleProfile.LARGE ).getStyle().getBackgroundColor().copyInstance();
    }

    @Override
    public void setDefaultBackgroundColor(ColorDefinition arg0)
    {
    }

    @Override
    public IStyle getStyle(Chart arg0, StyledComponent arg1)
    {

        return styles.get( StyleProfile.LARGE ).getStyle().copy();
    }

    private StyleProfile getCurrentStyle()
    {
        if (lastBounds == null)
        {
            return StyleProfile.LARGE;
        }
        if (lastBounds.getHeight() >= STYLE_BIG && lastBounds.getWidth() >= STYLE_BIG)
        {
            return StyleProfile.LARGE;
        }
        else if (lastBounds.getHeight() >= STYLE_MEDIUM && lastBounds.getWidth() >= STYLE_MEDIUM)
        {
            return StyleProfile.MEDIUM;
        }
        else
        {
            return StyleProfile.SMALL;
        }
    }

    @Override
    public void processStyle(Chart baseChart)
    {

        int red = 255;
        int blue = 255;
        int green = 255;

        ColorDefinition color = ColorDefinitionImpl.create( red, green, blue );
        baseChart.getBlock().setBackground( color );
        baseChart.getPlot().getClientArea().setBackground( color );

        ChartStyleSettings settings = styles.get( getCurrentStyle() );
        IStyle style = settings.getStyle();
        baseChart.getTitle().getLabel().getCaption().setFont( style.getFont().copyInstance() );
        baseChart.getTitle().getLabel().getCaption().getFont().setBold( true );
        baseChart.getTitle().getLabel().getCaption().setColor( ColorDefinitionImpl.GREY() );
        baseChart.getLegend().getText().setFont( style.getFont().copyInstance() );
        if (baseChart instanceof ChartWithAxes)
        {
            ChartWithAxes chart = (ChartWithAxes)baseChart;
            Axis xAxisPrimary = chart.getPrimaryBaseAxes()[0];
            Axis[] yAxis = chart.getOrthogonalAxes( xAxisPrimary, true );

            xAxisPrimary.getTitle().getCaption().setFont( style.getFont().copyInstance() );
            xAxisPrimary.getLabel().getCaption().setFont( style.getFont().copyInstance() );

            xAxisPrimary.getMajorGrid().setLineAttributes( LineAttributesImpl.create( ColorDefinitionImpl.GREY(),
                                                                                      LineStyle.DOTTED_LITERAL,
                                                                                      1 ) );
            xAxisPrimary.getMajorGrid().getLineAttributes().setVisible( true );

            for (Axis axis : yAxis)
            {
                axis.getMajorGrid().setLineAttributes( LineAttributesImpl.create( ColorDefinitionImpl.GREY(),
                                                                                  LineStyle.DOTTED_LITERAL,
                                                                                  1 ) );
                axis.getMajorGrid().getLineAttributes().setVisible( true );

                axis.getLabel().getCaption().setFont( style.getFont().copyInstance() );

                for (SeriesDefinition seriesDefinition : axis.getSeriesDefinitions())
                {
                    for (Series series : seriesDefinition.getRunTimeSeries())
                    {
                        if (series instanceof LineSeries)
                        {
                            LineSeries line = (LineSeries)series;
                            line.getMarkers().get( 0 ).setVisible( settings.getMarkerVisible() );
                            line.getMarkers().get( 0 ).setSize( settings.getMarkerSize() );
                        }
                        if (series instanceof AreaSeries)
                        {
                            AreaSeries line = (AreaSeries)series;
                            line.getMarkers().get( 0 ).setVisible( settings.getMarkerVisible() );
                            line.getMarkers().get( 0 ).setSize( settings.getMarkerSize() );

                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean updateChart(Chart model, Object obj)
    {
        return false;
    }

    @Override
    public boolean needInheritingStyles()
    {
        return true;
    }
}
