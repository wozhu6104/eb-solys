/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.chartengine.internal.swt.rendering;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.apache.log4j.Logger;
import org.eclipse.birt.chart.device.ICallBackNotifier;
import org.eclipse.birt.chart.device.IDeviceRenderer;
import org.eclipse.birt.chart.exception.ChartException;
import org.eclipse.birt.chart.factory.GeneratedChartState;
import org.eclipse.birt.chart.factory.Generator;
import org.eclipse.birt.chart.factory.RunTimeContext;
import org.eclipse.birt.chart.model.Chart;
import org.eclipse.birt.chart.model.attribute.Bounds;
import org.eclipse.birt.chart.model.attribute.CallBackValue;
import org.eclipse.birt.chart.model.attribute.impl.BoundsImpl;
import org.eclipse.birt.chart.util.PluginSettings;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.viewer.chartengine.internal.ChartStyleProcessor;
import com.elektrobit.ebrace.viewer.chartengine.internal.swt.ChartBuilder;
import com.elektrobit.ebrace.viewer.chartengine.internal.swt.ChartScaleRatio;
import com.elektrobit.ebrace.viewer.image.SwtAwtImageConvertor;

import lombok.Getter;

public class ChartToImageRenderer implements ICallBackNotifier
{
    private final ChartStyleProcessor styleProcessor = ChartStyleProcessor.getInstance();
    private final RunTimeContext runtimeContext = new RunTimeContext();
    @Getter
    private GeneratedChartState chartState;
    private ChartBuilder<?> chartBuilder;
    private final int NUMBER_OF_REPETITION_TO_GET_IMAGE = 4;
    private final static Logger LOG = Logger.getLogger( ChartToImageRenderer.class );

    private IDeviceRenderer getRenderer()
    {
        PluginSettings settings = PluginSettings.instance();
        try
        {
            IDeviceRenderer rendererInstance = settings.getDevice( "dv.PNG" );
            return rendererInstance;
        }
        catch (ChartException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param size
     * @return Image or null if rendering failed (image has to be disposed when it is not needed anymore).
     */
    public synchronized ImageData createChartImage(Rectangle size, ChartBuilder<?> chartBuilder)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "chartBuilder", chartBuilder );
        this.chartBuilder = chartBuilder;
        IDeviceRenderer renderer = getRenderer();
        RangeCheckUtils.assertReferenceParameterNotNull( "renderer", renderer );
        renderer.setProperty( IDeviceRenderer.UPDATE_NOTIFIER, this );

        if (!chartBuilder.isDataSet())
        {
            return null;
        }

        BufferedImage bufferedImage = new BufferedImage( size.width, size.height, BufferedImage.TYPE_INT_ARGB );

        Graphics2D g2d = (Graphics2D)bufferedImage.getGraphics();
        renderer.setProperty( IDeviceRenderer.GRAPHICS_CONTEXT, g2d );
        renderer.setProperty( IDeviceRenderer.CACHED_IMAGE, bufferedImage );

        Bounds bounds = BoundsImpl.create( size.x, size.y, size.width, size.height );
        double scaleRatio = 72d / renderer.getDisplayServer().getDpiResolution();
        ChartScaleRatio.set( scaleRatio );
        bounds.scale( scaleRatio ); // BOUNDS MUST BE SPECIFIED IN POINTS

        styleProcessor.setRenderingSizeHint( bounds );

        Chart chart = chartBuilder.build();

        try
        {
            Generator gr = Generator.instance();
            GeneratedChartState newChartState = gr.build( renderer.getDisplayServer(),
                                                          chart,
                                                          bounds,
                                                          null,
                                                          runtimeContext,
                                                          styleProcessor );
            chartState = newChartState;
            renderer.getDisplayServer().getDpiResolution();
            gr.render( renderer, newChartState );

        }
        catch (ChartException e)
        {
            e.printStackTrace();
            renderer.dispose();
            return null;
        }
        catch (NullPointerException e)
        {
            return null;
        }

        ImageData swtImageData = SwtAwtImageConvertor.convertToSWT( bufferedImage );
        renderer.dispose();
        return swtImageData;
    }

    public synchronized ImageData createImageChartWithExceptionHandling(Rectangle size, ChartBuilder<?> chartBuilder)
    {
        ImageData imgData = null;
        for (int i = 0; i < NUMBER_OF_REPETITION_TO_GET_IMAGE; i++)
        {
            imgData = createChartImage( size, chartBuilder );
            if (imgData != null)
            {
                return imgData;
            }
        }
        LOG.warn( "Unable to create image from chart after trying for many times. Error in Birt-Chart library" );
        return null;
    }

    @Override
    public void regenerateChart()
    {

    }

    @Override
    public void repaintChart()
    {

    }

    @Override
    public Object peerInstance()
    {
        return this;
    }

    @Override
    public Chart getDesignTimeModel()
    {
        return chartBuilder.build();
    }

    @Override
    public Chart getRunTimeModel()
    {
        return chartState.getChartModel();
    }

    @Override
    public void callback(Object arg0, Object arg1, CallBackValue arg2)
    {
    }
}
