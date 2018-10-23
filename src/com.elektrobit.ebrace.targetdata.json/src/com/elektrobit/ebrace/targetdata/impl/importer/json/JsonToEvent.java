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
import java.util.stream.Collectors;

import com.elektrobit.ebrace.core.targetdata.api.json.JsonEvent;
import com.elektrobit.ebrace.core.targetdata.api.json.JsonEventEdge;
import com.elektrobit.ebrace.core.timesegmentmanager.api.TimeSegmentAcceptorService;
import com.elektrobit.ebrace.targetdata.impl.importer.json.util.NodeAgent;
import com.elektrobit.ebrace.targetdata.impl.importer.json.util.StructuredNodeNameToNodeTree;
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
import com.google.gson.Gson;
import com.google.gson.JsonElement;

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

    public void handle(String eventJson)
    {
        JsonEvent event = new Gson().fromJson( eventJson, JsonEvent.class );
        handle( event );
    }

    public void handle(JsonEvent event)
    {
        ComRelation comRelation = handleComRelationCreation( event );
        handleTimeSegmentEventCreation( event, comRelation );
        handleChannelCreation( event );
        handleEventCreation( event, comRelation );
    }

    private ComRelation handleComRelationCreation(JsonEvent event)
    {
        JsonEventEdge edge = event.getEdge();
        return (edge != null) ? processStructuredEvent( edge ) : null;
    }

    private void handleTimeSegmentEventCreation(JsonEvent event, ComRelation relation)
    {
        if (event.getDuration() != null)
        {
            createTimeSegmentEvent( event, relation );
        }
    }

    private void createTimeSegmentEvent(JsonEvent event, ComRelation relation)
    {
        RuntimeEventChannel<STimeSegment> channel = runtimeEventAcceptor
                .createOrGetRuntimeEventChannel( event.getChannel(), Unit.TIMESEGMENT, channelDescription );

        RuntimeEventChannel<String> segmentEventChannel = runtimeEventAcceptor.createOrGetRuntimeEventChannel( "_"
                + event.getChannel() + "_segmentBoundaries", Unit.TEXT, channelDescription );

        String value = event.getValue().getDetails().toString();
        Object summaryObject = event.getValue().getSummary();
        String summary = (summaryObject != null && summaryObject instanceof String) ? (String)summaryObject : "";
        RuntimeEvent<String> startEvent = runtimeEventAcceptor
                .acceptEventMicros( event.getUptime(), segmentEventChannel, relation, value, summary );
        RuntimeEvent<String> endEvent = runtimeEventAcceptor.acceptEventMicros( event.getUptime()
                + event.getDuration(), segmentEventChannel, relation, value, summary );

        timeSegmentAcceptor.add( channel, startEvent, endEvent );
    }

    private void handleChannelCreation(JsonEvent event)
    {
        String channelName = event.getChannel();
        Object summary = event.getValue().getSummary();
        if (!channels.containsKey( channelName ))
        {
            if (summary != null && summary instanceof Long)
            {
                channels.put( channelName,
                              runtimeEventAcceptor
                                      .createOrGetRuntimeEventChannel( channelName, Unit.COUNT, channelDescription ) );
            }
            else
            {
                JsonElement details = event.getValue().getDetails();
                if (details != null)
                {
                    channels.put( channelName,
                                  runtimeEventAcceptor
                                          .createOrGetRuntimeEventChannel( channelName,
                                                                           Unit.TEXT,
                                                                           channelDescription,
                                                                           details.getAsJsonObject().entrySet().stream()
                                                                                   .map( entry -> entry.getKey() )
                                                                                   .collect( Collectors.toList() ) ) );
                }
                else
                {
                    channels.put( channelName,
                                  runtimeEventAcceptor.createOrGetRuntimeEventChannel( channelName,
                                                                                       Unit.TEXT,
                                                                                       channelDescription ) );
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void handleEventCreation(JsonEvent event, ComRelation relation)
    {
        if (event.getValue().getSummary() instanceof Long)
        {
            runtimeEventAcceptor.acceptEventMicros( event.getUptime(),
                                                    (RuntimeEventChannel<Long>)channels.get( event.getChannel() ),
                                                    relation,
                                                    (Long)event.getValue().getSummary(),
                                                    event.getValue().getSummary() + "" );
        }
        else
        {
            runtimeEventAcceptor.acceptEventMicros( event.getUptime(),
                                                    (RuntimeEventChannel<String>)channels.get( event.getChannel() ),
                                                    relation,
                                                    event.getValue().toString(),
                                                    (String)event.getValue().getSummary() );
        }
    }

    private ComRelation processStructuredEvent(JsonEventEdge edge)
    {
        String source = edge.getSource();
        String destination = edge.getDestination();

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
