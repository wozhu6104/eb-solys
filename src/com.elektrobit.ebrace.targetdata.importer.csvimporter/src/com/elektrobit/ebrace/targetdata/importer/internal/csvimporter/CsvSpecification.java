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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CsvSpecification
{
    public static final String CHANNEL_TAG = "channel";
    public static final String SUMMARY_TAG = "summary";
    public static final String VALUE_TAG = "value";
    public static final String UPTIME_TAG = "uptime";
    public static final String TIME_FORMAT_PREFIX = "tf_";

    private static final String DEFAULT_VALUE_STRING = "no value detected";
    private static final String CSV_CHANNEL_PREFIX = "trace.csv";

    private final String schema;
    private final Map<String, List<Object>> fieldMapping;
    private String separator = ",";

    public CsvSpecification(String schema, String separator, Map<String, List<Object>> fieldMapping)
    {
        this.schema = schema;
        this.separator = separator;
        this.fieldMapping = fieldMapping;
    }

    public String getSchema()
    {
        return schema;
    }

    public Map<String, List<Object>> getFieldMapping()
    {
        return fieldMapping;
    }

    public String getSeparator()
    {
        return separator;
    }

    public static CsvSpecification from(String schema, List<String> validLines)
    {
        CsvSpecification returnSpec = null;

        try
        {
            int schemaIndex = validLines.indexOf( schema );
            String separator = validLines.get( schemaIndex + 1 );
            String mapping = validLines.get( schemaIndex + 2 );

            String[] mappingFields = mapping.split( "," );

            if (mappingFields.length == schema.split( separator ).length)
            {
                Map<String, List<Object>> fieldMapping = createFieldMappingFromSchema( mappingFields );

                fillUpRequiredFields( fieldMapping );

                returnSpec = new CsvSpecification( schema, separator, fieldMapping );
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return returnSpec;
    }

    private static Map<String, List<Object>> createFieldMappingFromSchema(String[] mappingFields)
    {
        Map<String, List<Object>> fieldMapping = new HashMap<String, List<Object>>();

        int index = 0;
        for (String field : Arrays.asList( mappingFields ))
        {
            if (field != null && !field.equals( "" ))
            {
                handleField( fieldMapping, index, field );
            }
            index++;
        }
        return fieldMapping;
    }

    private static void handleField(Map<String, List<Object>> fieldMapping, int index, String field)
    {
        String[] fieldParts = field.split( " " );
        List<Object> objects = new ArrayList<>();
        String fieldId = "";

        for (String fieldPart : fieldParts)
        {
            if (fieldPart.startsWith( "$" ))
            {
                fieldId = extractFieldId( fieldPart );
                objects.add( index );
                if (fieldId.startsWith( UPTIME_TAG ))
                {
                    objects.add( handleFieldPart( fieldPart.substring( 1 ) ) );
                }
            }
            else
            {
                objects.add( handleFieldPart( fieldPart ) );
            }
        }

        if (!fieldMapping.containsKey( fieldId ))
        {
            fieldMapping.put( fieldId, objects );
        }
        else
        {
            fieldMapping.get( fieldId ).addAll( objects );
        }
    }

    private static String extractFieldId(String fieldPart)
    {
        String fieldId;
        if (fieldPart.indexOf( "(" ) > 0)
        {
            fieldId = fieldPart.substring( 1, fieldPart.indexOf( "(" ) );
        }
        else
        {
            fieldId = fieldPart.substring( 1 );
        }
        return fieldId;
    }

    private static Object handleFieldPart(String part)
    {
        Object returnObject = null;

        if (part.startsWith( UPTIME_TAG ))
        {
            String timeFormat = timeFormatBetweenBrackets( part );
            returnObject = TIME_FORMAT_PREFIX + timeFormat;
        }
        else
        {
            returnObject = part;
        }
        return returnObject;
    }

    private static String timeFormatBetweenBrackets(String fieldName)
    {
        int openBrackedIndex = fieldName.indexOf( '(' ) + 1;
        int closingBracketIndex = fieldName.indexOf( ')' );
        return fieldName.substring( openBrackedIndex, closingBracketIndex );
    }

    private static void fillUpRequiredFields(Map<String, List<Object>> fieldMapping)
    {
        if (!fieldMapping.containsKey( UPTIME_TAG ))
        {
            fieldMapping.put( UPTIME_TAG, Arrays.asList( "0" ) );
        }
        if (!fieldMapping.containsKey( VALUE_TAG ))
        {
            fieldMapping.put( VALUE_TAG, Arrays.asList( DEFAULT_VALUE_STRING ) );
        }
        if (!fieldMapping.containsKey( SUMMARY_TAG ))
        {
            fieldMapping.put( SUMMARY_TAG, fieldMapping.get( VALUE_TAG ) );
        }
        if (!fieldMapping.containsKey( CHANNEL_TAG ))
        {
            fieldMapping.put( CHANNEL_TAG, Arrays.asList( CSV_CHANNEL_PREFIX ) );
        }
    }
}
