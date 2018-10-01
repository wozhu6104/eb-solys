/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.plantumlrenderer.impl.customrenderer.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.elektrobit.ebrace.core.plantumlrenderer.impl.customrenderer.api.PlantUmlRenderer;

import lombok.extern.log4j.Log4j;
import net.sourceforge.plantuml.SourceStringReader;

@Log4j
public class PlantUmlPngRenderer implements PlantUmlRenderer
{

    @Override
    public String renderToFile(String plantUmlText, String pathToImage)
    {
        SourceStringReader reader = new SourceStringReader( plantUmlText );
        try (FileOutputStream fileOutputStream = new FileOutputStream( new File( pathToImage ) );)
        {
            return reader.generateImage( fileOutputStream );
        }
        catch (IOException e)
        {
            log.error( "Plant UML PNG Image generation did not work.", e );
        }

        return null;
    }

}
