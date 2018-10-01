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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import com.elektrobit.ebrace.core.plantumlrenderer.impl.customrenderer.api.PlantUmlRenderer;

import lombok.extern.log4j.Log4j;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

@Log4j
public class PlantUmlSVGRenderer implements PlantUmlRenderer
{

    @Override
    public String renderToFile(String plantUmlText, String pathToImage)
    {
        String result = null;

        final SourceStringReader reader = new SourceStringReader( plantUmlText, "UTF-8" );

        try (final ByteArrayOutputStream os = new ByteArrayOutputStream())
        {
            result = reader.generateImage( os, new FileFormatOption( FileFormat.SVG ) );
            final String svg = new String( os.toByteArray(), Charset.forName( "UTF-8" ) );
            try (PrintWriter printWriter = new PrintWriter( pathToImage ))
            {
                printWriter.println( svg );
            }
        }
        catch (IOException e)
        {
            log.error( "Plant UML SVG Image generation did not work.", e );
        }
        return result;
    }

}
