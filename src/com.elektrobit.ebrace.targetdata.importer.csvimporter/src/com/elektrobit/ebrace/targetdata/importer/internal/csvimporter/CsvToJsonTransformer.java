/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.importer.internal.csvimporter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.elektrobit.ebrace.targetdata.importer.internal.csvimporter.api.Transformer;
import com.elektrobit.ebrace.targetdata.json.api.JsonEventTag;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class CsvToJsonTransformer implements Transformer
{
    private interface Command
    {
        String map(String parameter);
    }

    private final Logger log = Logger.getLogger( getClass() );

    private CsvSpecification spec;

    @Override
    public void acquireMetaData(String hint, String path)
    {
        // later we could load this from a file, fire up a dialog, etc.
        if (hint == null)
        {
            Map<String, List<Object>> fieldMapping = new HashMap<>();
            String separator = ",";
            fieldMapping.put( JsonEventTag.UPTIME, Arrays.asList( 0 ) );
            fieldMapping.put( JsonEventTag.CHANNEL, Arrays.asList( 2 ) );
            fieldMapping.put( JsonEventTag.SUMMARY, Arrays.asList( 1 ) );
            fieldMapping.put( JsonEventTag.VALUE, Arrays.asList( 1 ) );
            spec = new CsvSpecification( hint, separator, fieldMapping );
        }
        else
        {
            List<String> validLines = readValidSpecLines( path );
            if (validSpec( validLines ))
            {
                spec = CsvSpecification.from( hint, validLines );
            }
        }
    }

    private List<String> readValidSpecLines(String path)
    {
        List<String> validLines = new ArrayList<String>();
        FileReader fileReader = null;
        BufferedReader br = null;
        try
        {
            String line = null;
            fileReader = new FileReader( new File( path + "/csv-spec.txt" ) );
            br = new BufferedReader( fileReader );
            while ((line = br.readLine()) != null)
            {
                if (!line.startsWith( "#" ) && !line.isEmpty())
                {
                    validLines.add( line );
                }
            }
            br.close();
            fileReader.close();
        }
        catch (FileNotFoundException e1)
        {
            log.error( "CSV specification could not be opened" );
        }
        catch (IOException e)
        {
            log.error( "CSV specification could not be read" );
        }
        return validLines;
    }

    private boolean validSpec(List<String> lines)
    {
        return true;
    }

    @Override
    public String transformEvent(String input)
    {
        String[] eventFields = input.split( spec.getSeparator() );
        int index = 0;
        for (String field : eventFields)
        {
            field = field.replaceAll( "\"", "" );
            if (field.length() == 0)
            {
                field = " ";
            }
            eventFields[index++] = field;
        }

        Gson gson = new Gson();

        JsonObject object = new JsonObject();

        for (String fieldTag : spec.getFieldMapping().keySet())
        {
            object.addProperty( fieldTag, resolvePropertyValueFromFields( fieldTag, eventFields ) );
        }

        return gson.toJson( object );
    }

    private String resolvePropertyValueFromFields(String property, String[] eventFields)
    {
        String propertyValueBuilder = "";
        Object lastValue = null;
        for (Object o : spec.getFieldMapping().get( property ))
        {
            if (o instanceof Integer)
            {
                propertyValueBuilder += eventFields[(int)o];
            }
            else if (o instanceof Command)
            {
                propertyValueBuilder = ((Command)o).map( eventFields[(int)lastValue] );
            }
            else if (o instanceof String)
            {
                String stringParam = (String)o;
                if (stringParam.startsWith( CsvSpecification.TIME_FORMAT_PREFIX ))
                {
                    propertyValueBuilder = handleTimeField( eventFields, lastValue, stringParam );
                }
                else
                {
                    propertyValueBuilder += (String)o;
                }
            }
            lastValue = o;
        }
        return propertyValueBuilder;
    }

    private String handleTimeField(String[] eventFields, Object lastValue, String stringParam)
    {
        String propertyValue = "0";
        String format = stringParam.replace( CsvSpecification.TIME_FORMAT_PREFIX, "" );
        if (format.equals( "" ))
        {
            propertyValue = eventFields[(int)lastValue];
        }
        else
        {
            SimpleDateFormat sdf = new SimpleDateFormat( format );
            try
            {
                propertyValue = sdf.parse( eventFields[(int)lastValue] ).getTime() + "";
            }
            catch (ParseException e)
            {
            }
        }
        return propertyValue;
    }
}
