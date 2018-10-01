/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.channelsview;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.nebula.visualization.xygraph.figures.Trace;
import org.eclipse.nebula.visualization.xygraph.figures.XYGraph;
import org.eclipse.nebula.visualization.xygraph.linearscale.Range;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import lombok.extern.log4j.Log4j;

@Log4j
public class SlidingWindowChart extends XYGraph
{

    private final List<Trace> traces = new ArrayList<Trace>();
    private int widthWithMargin;
    private int heightWithMargin;

    public SlidingWindowChart(int width, int height)
    {
        super();

        setShowLegend( false );
        setShowTitle( false );

        setBorder( null );

        getPrimaryXAxis().setShowMajorGrid( false );
        getPrimaryXAxis().setShowMinorGrid( false );
        getPrimaryXAxis().setBorder( null );

        getPrimaryXAxis().setDateEnabled( true );

        getPrimaryYAxis().setRange( 0, 100 );
        getPrimaryYAxis().setBorder( null );

        getPrimaryXAxis().setVisible( false );
        getPrimaryYAxis().setVisible( false );
        setTransparent( true );

        resize( width, height );
    }

    public void resize(int width, int height)
    {
        widthWithMargin = width + getPrimaryXAxis().getMargin() * 2;
        heightWithMargin = height + getPrimaryYAxis().getMargin() * 2;
        setSize( widthWithMargin, heightWithMargin );
    }

    @Override
    public void addTrace(final Trace trace)
    {
        super.addTrace( trace );
        traces.add( trace );
    }

    public Image renderChartToImage()
    {
        Image resultImage = null;
        if (!traces.isEmpty())
        {
            setXAxisRange();
            layout();

            Image image = new Image( Display.getDefault(),
                                     new Rectangle( 0, 0, widthWithMargin - 1, heightWithMargin - 1 ) );

            FigureHelper.renderFigureToImage( image, this, traces );

            createDebugImgIfLoggingActive( "initSlidingWindowChart.png", image );

            Image croppedImage = FigureHelper
                    .cropMarginFromImage( image, getPrimaryXAxis().getMargin(), getPrimaryYAxis().getMargin() );

            createDebugImgIfLoggingActive( "croppedSlidingWindowChart.png", croppedImage );

            ImageData imageData = croppedImage.getImageData();
            imageData.transparentPixel = imageData.getPixel( 0, 0 );

            resultImage = new Image( Display.getDefault(), imageData );

            createDebugImgIfLoggingActive( "resultSlidingWindowChart.png", resultImage );

            image.dispose();
            croppedImage.dispose();
        }
        return resultImage;
    }

    private void createDebugImgIfLoggingActive(String pathToImage, Image image)
    {
        if (log.isDebugEnabled())
            FigureHelper.saveImageToFile( pathToImage, image );
    }

    private void setXAxisRange()
    {
        Double lower = null;
        Double upper = null;

        for (Trace nextTrace : traces)
        {
            if (lower == null)
                lower = nextTrace.getDataProvider().getXDataMinMax().getLower();
            else if (nextTrace.getDataProvider().getXDataMinMax().getLower() < lower)
                lower = nextTrace.getDataProvider().getXDataMinMax().getLower();

            if (upper == null)
                upper = nextTrace.getDataProvider().getXDataMinMax().getUpper();
            else if (nextTrace.getDataProvider().getXDataMinMax().getUpper() > upper)
                upper = nextTrace.getDataProvider().getXDataMinMax().getUpper();
        }

        getPrimaryXAxis().setRange( new Range( lower, upper ) );
    }

    @Override
    protected void layout()
    {
        super.layout();
    }

}
