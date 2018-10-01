/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.plantumlrenderer.impl;

import org.osgi.service.component.annotations.Component;

import com.elektrobit.ebrace.core.plantumlrenderer.api.PlantUmlRendererService;
import com.elektrobit.ebrace.core.plantumlrenderer.impl.customrenderer.api.PlantUmlRenderer;
import com.elektrobit.ebrace.core.plantumlrenderer.impl.customrenderer.impl.PlantUmlPngRenderer;
import com.elektrobit.ebrace.core.plantumlrenderer.impl.customrenderer.impl.PlantUmlSVGRenderer;

import lombok.extern.log4j.Log4j;

@Log4j
@Component
public class PlantUmlRendererServiceImpl implements PlantUmlRendererService
{
    private final PlantUmlSVGRenderer svgPlantUmlRenderer = new PlantUmlSVGRenderer();
    private final PlantUmlPngRenderer pngPlantUmlRenderer = new PlantUmlPngRenderer();

    @Override
    public boolean plantumlToSVG(String plantUmlText, String pathToFile)
    {
        return plantumlToImage( plantUmlText, pathToFile, svgPlantUmlRenderer );
    }

    private boolean plantumlToImage(String plantUmlText, String pathToFile, PlantUmlRenderer plantUmlRenderer)
    {
        boolean result = true;
        final String renderResult = plantUmlRenderer.renderToFile( plantUmlText, pathToFile );
        if (renderResult == null || renderResult.equals( "(Error)" ))
        {
            log.warn( "Plant UML Image generation did not work. Input was not correct: " + plantUmlText );
            result = false;
        }
        else
        {
            log.trace( "Plant UML Image generated. File is " + pathToFile );
        }

        return result;
    }

    @Override
    public boolean plantumlToPNG(String plantUmlText, String pathToImage)
    {
        return plantumlToImage( plantUmlText, pathToImage, pngPlantUmlRenderer );
    }

}
