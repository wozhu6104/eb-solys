/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.systemmodel.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Component;

import com.elektrobit.ebrace.core.systemmodel.api.SystemModel;
import com.elektrobit.ebrace.core.systemmodel.api.SystemModelAccess;
import com.elektrobit.ebrace.core.systemmodel.api.SystemModelChangedListener;
import com.elektrobit.ebrace.core.systemmodel.api.SystemModelEdge;
import com.elektrobit.ebrace.core.systemmodel.api.SystemModelNode;
import com.elektrobit.ebrace.core.systemmodel.api.ViewModelGenerator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;

@Component
public class SystemModelAccessService implements SystemModelAccess
{
    private final List<SystemModelChangedListener> listeners = new ArrayList<>();

    @Override
    public SystemModel initFromFile(String absolutePathToInputModel) throws FileNotFoundException
    {
        JsonReader reader = new JsonReader( new FileReader( new File( absolutePathToInputModel ) ) );
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter( SystemModelNode.class, new SystemModelNodeDeserializer() );
        gsonBuilder.registerTypeAdapter( SystemModelEdge.class, new SystemModelEdgeDeserializer() );
        Gson gson = gsonBuilder.create();
        SystemModel returnModel = (SystemModelImpl)gson.fromJson( reader, SystemModelImpl.class );
        try
        {
            reader.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return returnModel;
    }

    @Override
    public String generate(SystemModel model, ViewModelGenerator generator)
    {
        StringBuilder builder = new StringBuilder();

        builder.append( generator.start() );

        builder.append( generator.nodesStart() );
        builder.append( joinNodes( model, generator ) );
        builder.append( generator.nodesEnd() );

        builder.append( generator.separator() );

        builder.append( generator.edgesStart() );
        builder.append( joinEdges( model, generator ) );
        builder.append( generator.edgesEnd() );

        builder.append( generator.end() );

        return builder.toString();
    }

    private String joinNodes(SystemModel model, ViewModelGenerator generator)
    {
        return String.join( generator.separator(), generateNodes( model, generator ) );
    }

    private String[] generateNodes(SystemModel model, ViewModelGenerator generator)
    {
        List<SystemModelNode> nodes = model.getNodes();
        String[] generatedNodes = new String[nodes.size()];

        nodes.stream().map( node -> node.getId() ).collect( Collectors.toList() ).toArray( generatedNodes );

        nodes.stream().map( node -> generator.handleNode( node.getId(),
                                                          node.getParent() != null ? node.getParent().getId() : "",
                                                          node.getAnnotations() ) )
                .collect( Collectors.toList() ).toArray( generatedNodes );
        return generatedNodes;
    }

    private String joinEdges(SystemModel model, ViewModelGenerator generator)
    {
        return String.join( generator.separator(), generateEdges( model, generator ) );
    }

    private String[] generateEdges(SystemModel model, ViewModelGenerator generator)
    {
        String[] generatedEdges = new String[model.getEdges().size()];
        model.getEdges().stream()
                .map( edge -> generator.handleEdge( edge.getFrom().getId(),
                                                    edge.getTo().getId(),
                                                    edge.getAnnotations() ) )
                .collect( Collectors.toList() ).toArray( generatedEdges );
        return generatedEdges;
    }

    @Override
    public SystemModelNode addNode(SystemModel model, SystemModelNode node)
    {
        model.addNode( node );
        callListeners( model );
        return node;
    }

    @Override
    public SystemModelEdge addEdge(SystemModel model, SystemModelEdge edge)
    {
        model.addEdge( edge );
        callListeners( model );
        return edge;
    }

    @Override
    public void addSystemModelChangedListener(SystemModelChangedListener listener)
    {
        listeners.add( listener );
    }

    @Override
    public void removeSystemModelChangedListener(SystemModelChangedListener listener)
    {
        listeners.remove( listener );
    }

    private void callListeners(SystemModel model)
    {
        listeners.forEach( listener -> listener.onSystemModelChanged( model ) );
    }

    private final class SystemModelNodeDeserializer implements JsonDeserializer<SystemModelNode>
    {
        private static final String ID = "id";
        private static final String PARENT = "parent";

        @Override
        public SystemModelNode deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException
        {
            SystemModelNode node = new SystemModelNode();
            JsonObject nodeJsonObject = json.getAsJsonObject();

            appendId( node, nodeJsonObject );
            appendParent( node, nodeJsonObject );
            addRemainingValuesToAnnotations( node, nodeJsonObject );

            return node;
        }

        private void appendId(SystemModelNode node, JsonObject nodeJsonObject)
        {
            String nodeID = nodeJsonObject.get( ID ).getAsString();
            node.setId( nodeID );
        }

        private void appendParent(SystemModelNode node, JsonObject nodeJsonObject)
        {
            JsonElement parent = nodeJsonObject.get( PARENT );
            if (parent != null)
            {
                SystemModelNode parentNode = new SystemModelNode();
                parentNode.setId( parent.getAsString() );
                node.setParent( parentNode );
            }
        }

        private void addRemainingValuesToAnnotations(SystemModelNode node, JsonObject nodeJsonObject)
        {
            Set<Entry<String, JsonElement>> allValues = nodeJsonObject.entrySet();
            for (Entry<String, JsonElement> entry : allValues)
            {
                String parameterName = entry.getKey();
                if (!parameterName.equals( PARENT ) && !parameterName.equals( ID ))
                {
                    addEntryToAnnotations( node, entry );
                }
            }
        }

        private void addEntryToAnnotations(SystemModelNode node, Entry<String, JsonElement> entry)
        {
            String parameterName = entry.getKey();
            JsonElement value = entry.getValue();
            String parameterValue;
            if (value.isJsonArray())
            {
                parameterValue = value.getAsJsonArray().toString();
            }
            else
            {
                parameterValue = value.getAsString();
            }
            node.getAnnotations().put( parameterName, parameterValue );
        }

    }

    private final class SystemModelEdgeDeserializer implements JsonDeserializer<SystemModelEdge>
    {
        @Override
        public SystemModelEdge deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException
        {
            SystemModelEdge edge = new SystemModelEdge();
            SystemModelNode from = new SystemModelNode();
            SystemModelNode to = new SystemModelNode();
            from.setId( json.getAsJsonObject().get( "from" ).getAsString() );
            to.setId( json.getAsJsonObject().get( "to" ).getAsString() );
            edge.setFrom( from );
            edge.setTo( to );
            return edge;
        }
    }

}
