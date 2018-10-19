/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.impl.importer.json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elektrobit.ebrace.core.timesegmentmanager.api.TimeSegmentAcceptorService;
import com.elektrobit.ebrace.targetdata.impl.importer.json.util.NodeAgent;
import com.elektrobit.ebrace.targetdata.impl.importer.json.util.StructuredNodeNameToNodeTree;
import com.elektrobit.ebrace.targetdata.json.api.JsonEventTag;
import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation;
import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelationAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.STimeSegment;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;
import com.elektrobit.ebsolys.core.targetdata.api.structure.StructureAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.structure.Tree;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeLevelDef;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class JsonToEvent implements NodeAgent<TreeNode>
{
    private final RuntimeEventAcceptor runtimeEventAcceptor;
    private final StructureAcceptor structureAcceptor;
    private final ComRelationAcceptor comRelationAcceptor;
    private final TimeSegmentAcceptorService timeSegmentAcceptor;

    private final String channelDescription;
    private final StructuredNodeNameToNodeTree<TreeNode> uniqueNodes;
    private final Map<String, TreeLevelDef> treeLevels = new HashMap<>();
    private Tree tree;

    private final Map<String, RuntimeEventChannel<?>> channels = new HashMap<>();

    public JsonToEvent(RuntimeEventAcceptor runtimeEventAcceptor, StructureAcceptor structureAcceptor,
            ComRelationAcceptor comRelationAcceptor, TimeSegmentAcceptorService timeSegmentAcceptor,
            String channelDescription)
    {
        this.runtimeEventAcceptor = runtimeEventAcceptor;
        this.structureAcceptor = structureAcceptor;
        this.comRelationAcceptor = comRelationAcceptor;
        this.channelDescription = channelDescription;
        this.timeSegmentAcceptor = timeSegmentAcceptor;

        createTree();
        uniqueNodes = new StructuredNodeNameToNodeTree<TreeNode>( this, tree.getRootNode() );
    }

    private void createTree()
    {
        tree = structureAcceptor.addNewTreeInstance( channelDescription + "Tree",
                                                     "communication events found in " + channelDescription,
                                                     "system",
                                                     getTreeLevelDefinition() );
    }

    private List<TreeLevelDef> getTreeLevelDefinition()
    {
        treeLevels.put( "system", structureAcceptor.createTreeLevel( "system level", "", null ) );
        return new ArrayList<TreeLevelDef>( treeLevels.values() );
    }

    public RuntimeEvent<?> toRuntimeEvent(String event)
    {
        return null;
    }

    public void handle(JsonObject eventObject)
    {
        long timestamp = parseTimestamp( eventObject, JsonEventTag.UPTIME );
        String channelName = eventObject.get( JsonEventTag.CHANNEL ).getAsString();
        JsonElement valueElement = eventObject.get( JsonEventTag.VALUE );
        if (valueElement == null)
        {
            valueElement = eventObject;
        }
        Object valueObject = null;
        if (valueElement.isJsonPrimitive() && valueElement.getAsJsonPrimitive().isNumber())
        {
            valueObject = valueElement.getAsLong();
        }
        String valueAsJsonString = valueElement.toString().replace( "\\\"", "\"" ).replace( "}\"", "}" ).replace( "\"{",
                                                                                                                  "{" );
        String summary = eventObject.get( JsonEventTag.SUMMARY ).toString().replace( "\\\"", "\"" )
                .replace( "}\"", "}" ).replace( "\"{", "{" );

        ComRelation relation = tryToRetrieveComRelation( eventObject );

        handleTimeSegmentEventCreation( eventObject, timestamp, channelName, valueAsJsonString, summary, relation );

        handleChannelCreation( channelName, valueObject, summary );

        handleEventCreation( timestamp, channelName, valueObject, valueAsJsonString, summary, relation );

    }

    public void handle(String event) throws JsonSyntaxException
    {
        JsonObject eventObject = null;
        eventObject = new JsonParser().parse( event ).getAsJsonObject();
        handle( eventObject );
    }

    private void handleTimeSegmentEventCreation(JsonObject eventObject, long timestamp, String channelName,
            String valueAsJsonString, String summary, ComRelation relation)
    {
        if (eventObject.has( JsonEventTag.DURATION ))
        {
            createTimeSegmentEvent( timestamp,
                                    parseTimestamp( eventObject, JsonEventTag.DURATION ),
                                    channelName,
                                    valueAsJsonString,
                                    summary,
                                    relation );
        }
    }

    private ComRelation tryToRetrieveComRelation(JsonObject eventObject)
    {
        return (eventObject.has( JsonEventTag.EDGE )) ? processStructuredEvent( eventObject ) : null;
    }

    private void handleChannelCreation(String channelName, Object valueObject, String valueAsJsonString)
    {
        if (!channels.containsKey( channelName ))
        {
            if (valueObject != null && valueObject instanceof Long)
            {
                channels.put( channelName,
                              runtimeEventAcceptor
                                      .createOrGetRuntimeEventChannel( channelName, Unit.COUNT, channelDescription ) );
            }
            else if (valueAsJsonString.startsWith( "{" ) && valueAsJsonString.endsWith( "}" ))
            {
                final List<String> keys = extractJsonColumnFields( valueAsJsonString );
                channels.put( channelName,
                              runtimeEventAcceptor.createOrGetRuntimeEventChannel( channelName,
                                                                                   Unit.TEXT,
                                                                                   channelDescription,
                                                                                   keys ) );
            }
            else
            {
                channels.put( channelName,
                              runtimeEventAcceptor
                                      .createOrGetRuntimeEventChannel( channelName, Unit.TEXT, channelDescription ) );
            }
        }
    }

    private List<String> extractJsonColumnFields(String valueAsJsonString)
    {
        final List<String> keys = new ArrayList<String>();
        JsonObject value = null;
        JsonParser parser = new JsonParser();
        JsonElement jsonValue = parser.parse( valueAsJsonString );
        if (jsonValue.isJsonObject())
        {
            value = jsonValue.getAsJsonObject();
            value.entrySet().stream().forEach( entry -> keys.add( entry.getKey() ) );
        }
        return keys;
    }

    @SuppressWarnings("unchecked")
    private void handleEventCreation(long timestamp, String channelName, Object valueObject, String valueAsJsonString,
            String summary, ComRelation relation)
    {
        if (valueObject instanceof Long)
        {
            runtimeEventAcceptor.acceptEventMicros( timestamp,
                                                    (RuntimeEventChannel<Long>)channels.get( channelName ),
                                                    relation,
                                                    (Long)valueObject,
                                                    summary );
        }
        else
        {
            runtimeEventAcceptor.acceptEventMicros( timestamp,
                                                    (RuntimeEventChannel<String>)channels.get( channelName ),
                                                    relation,
                                                    valueAsJsonString,
                                                    summary );
        }
    }

    private long parseTimestamp(JsonObject eventObject, String tagName)
    {
        String timestampString = eventObject.getAsJsonObject().get( tagName ).getAsString();
        long timestamp = Long.parseLong( timestampString.replace( "u", "" ) );
        if (timestampString.endsWith( "u" ))
        {
            return timestamp;
        }
        else
        {
            return timestamp * 1000;
        }
    }

    private ComRelation processStructuredEvent(JsonObject eventObject)
    {
        String source = eventObject.get( JsonEventTag.EDGE ).getAsJsonObject().get( JsonEventTag.SOURCE ).getAsString();
        String destination = eventObject.get( JsonEventTag.EDGE ).getAsJsonObject().get( JsonEventTag.DESTINATION )
                .getAsString();

        provideSufficientLayers( source.split( "\\." ) );
        provideSufficientLayers( destination.split( "\\." ) );

        TreeNode sourceNode = uniqueNodes.getOrCreate( Arrays.asList( source.split( "\\." ) ) );
        TreeNode destinationNode = uniqueNodes.getOrCreate( Arrays.asList( destination.split( "\\." ) ) );

        return comRelationAcceptor.createOrGetComRelation( sourceNode, destinationNode, "" );
    }

    private void provideSufficientLayers(String[] layerNames)
    {
        int levels = layerNames.length;
        while (treeLevels.size() <= levels)
        {
            String newDefName = "level " + (treeLevels.size() - 1);
            TreeLevelDef newDef = structureAcceptor.createTreeLevel( newDefName, "", null );
            structureAcceptor.appendTreeLevelDef( tree, newDef );
            treeLevels.put( newDefName, newDef );
        }
    }

    private void createTimeSegmentEvent(long timestamp, long duration, String channelName, String valueAsJsonString,
            String summary, ComRelation relation)
    {
        RuntimeEventChannel<STimeSegment> channel = runtimeEventAcceptor
                .createOrGetRuntimeEventChannel( channelName, Unit.TIMESEGMENT, channelDescription );

        RuntimeEventChannel<String> segmentEventChannel = runtimeEventAcceptor.createOrGetRuntimeEventChannel( "_"
                + channelName + "_segmentBoundaries", Unit.TEXT, channelDescription );

        RuntimeEvent<String> startEvent = runtimeEventAcceptor
                .acceptEventMicros( timestamp, segmentEventChannel, relation, valueAsJsonString, summary );
        RuntimeEvent<String> endEvent = runtimeEventAcceptor
                .acceptEventMicros( timestamp + duration, segmentEventChannel, relation, valueAsJsonString, summary );

        timeSegmentAcceptor.add( channel, startEvent, endEvent );

    }

    public StructureAcceptor getStructureAcceptor()
    {
        return structureAcceptor;
    }

    public ComRelationAcceptor getComRelationAcceptor()
    {
        return comRelationAcceptor;
    }

    @Override
    public TreeNode createNodeObject(String nodeName, TreeNode parent)
    {

        String[] split = nodeName.split( "\\." );
        return structureAcceptor.addTreeNode( parent, split[split.length - 1] );
    }

}
