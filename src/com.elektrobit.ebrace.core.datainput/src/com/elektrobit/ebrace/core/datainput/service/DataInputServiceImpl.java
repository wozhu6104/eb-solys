/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.datainput.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elektrobit.ebrace.common.utils.FileHelper;
import com.elektrobit.ebrace.core.datainput.api.DataInput;
import com.elektrobit.ebrace.core.datainput.api.DataInputService;
import com.elektrobit.ebrace.core.datainput.api.DataStream;
import com.elektrobit.ebrace.core.datainput.api.DataStreamParser;
import com.elektrobit.ebrace.core.datainput.api.DataStreamTokenizer;
import com.elektrobit.ebrace.core.datainput.factory.DataStreamFactory;
import com.elektrobit.ebrace.core.datainput.factory.DataStreamParserFactory;
import com.elektrobit.ebrace.core.datainput.factory.DataStreamTokenizerFactory;
import com.elektrobit.ebrace.core.targetdata.api.json.JsonEventHandler;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import lombok.extern.log4j.Log4j;

@Log4j
@Component(immediate = true)
public class DataInputServiceImpl implements DataInputService
{
    private final Map<String, DataInput> inputs = new HashMap<>();
    private JsonEventHandler jsonEventHandler;

    @Override
    public void loadDataInputDescriptionsFromFile(String path) throws IOException
    {
        String jsonDescription = FileHelper.readFileToString( path );

        JsonParser parser = new JsonParser();
        JsonElement targets = parser.parse( jsonDescription );
        JsonArray jsonTargets = targets.getAsJsonObject().get( "targets" ).getAsJsonArray();
        jsonTargets.forEach( target -> {
            DataInput input = createDataInputForTarget( target );
            inputs.put( input.getId(), input );
            log.info( "added new data input: " + input.getId() + ", " + input.getStream().getType() + ", "
                    + input.getTokenizer().getId() + ", " + input.getParser().getId() );
        } );
    }

    private DataInput createDataInputForTarget(JsonElement target)
    {
        DataStreamDescriptor descriptor = new DataStreamDescriptor( target.getAsJsonObject() );
        DataInput input = null;
        try
        {
            input = new DataInput( descriptor.stringValueOf( "id" ),
                                   DataStreamFactory.getDataStream( descriptor ),
                                   DataStreamTokenizerFactory.getTokenizer( descriptor ),
                                   DataStreamParserFactory.getParser( descriptor ),
                                   false );

        }
        catch (IOException e)
        {
            log.warn( "creation of data input failed" );
        }
        return input;
    }

    @Override
    public DataInput createDataInputOnTheFly(String id, DataStream stream, DataStreamTokenizer tokenizer,
            DataStreamParser parser)
    {
        DataInput input = new DataInput( id, stream, tokenizer, parser, false );
        inputs.put( id, input );
        return input;
    }

    @Override
    public void startReadingAllInputs()
    {
        inputs.keySet().forEach( input -> startReading( input ) );

    }

    @Override
    public void stopReadingAllInputs()
    {
        inputs.keySet().forEach( input -> stopReading( input ) );

    }

    @Override
    public void startReading(String dataInputId)
    {
        inputs.get( dataInputId ).startReading( jsonEventHandler );
    }

    @Override
    public void stopReading(String dataInputId)
    {
        inputs.get( dataInputId ).stopReading();
    }

    @Reference
    public void bindJsonEventHandler(JsonEventHandler jsonEventHandler)
    {
        this.jsonEventHandler = jsonEventHandler;
    }

    public void unbindJsonEventHandler(JsonEventHandler jsonEventHandler)
    {
        this.jsonEventHandler = null;
    }

    @Override
    public Set<String> getDataInputs()
    {
        return inputs.keySet();
    }
}
