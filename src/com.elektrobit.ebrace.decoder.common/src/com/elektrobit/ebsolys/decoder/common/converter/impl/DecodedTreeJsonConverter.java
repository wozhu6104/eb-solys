/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebsolys.decoder.common.converter.impl;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedNode;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedNodeVisitor;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedTree;
import com.elektrobit.ebsolys.decoder.common.api.GenericDecodedNode;
import com.elektrobit.ebsolys.decoder.common.api.GenericDecodedTree;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class DecodedTreeJsonConverter implements DecodedNodeVisitor
{
    private DecodedTree tree = null;
    private String stringRepresentation = "";
    private JsonObject root = null;
    private HashMap<Integer, JsonObject> jsonNodeCache = null;
    private final UniqueKeyTransformer keyTransformer;

    public DecodedTreeJsonConverter()
    {
        this( new NoJsonKeyTransformer() );
    }

    public DecodedTreeJsonConverter(final UniqueKeyTransformer keyTransformer)
    {
        root = new JsonObject();
        this.keyTransformer = keyTransformer;
    }

    @Override
    public void nodeVisited(DecodedNode currentNode)
    {
        final String transformedKeyName = keyTransformer.transform( currentNode.getName() );

        if (currentNode.getValue() == null)
        { // parent
            JsonObject newJsonNode = new JsonObject();

            jsonNodeCache.put( currentNode.hashCode(), newJsonNode );

            if (currentNode.getParentNode() == null)
            {
                root.add( transformedKeyName, newJsonNode );
            }
            else
            {
                JsonObject parent = jsonNodeCache.get( currentNode.getParentNode().hashCode() );
                parent.add( transformedKeyName, newJsonNode );
            }
        }
        else
        { // child
            JsonObject parent = jsonNodeCache.get( currentNode.getParentNode().hashCode() );
            parent.addProperty( transformedKeyName, currentNode.getValue() );
        }
    }

    public String getStringRepresentation(DecodedTree decodedTree)
    {
        root.entrySet().clear();
        tree = decodedTree;
        jsonNodeCache = new HashMap<Integer, JsonObject>();

        jsonNodeCache.clear();

        tree.traverse( this );
        stringRepresentation = root.toString();
        return stringRepresentation;
    }

    public DecodedTree getDecodedTreeFromString(String decodedTreeAsString)
    {
        tree = null;
        JsonElement decodedTreeJsonElement = getJsonElementFromString( decodedTreeAsString );

        return copyJsonToDecodedTree( decodedTreeJsonElement );
    }

    private DecodedTree copyJsonToDecodedTree(JsonElement decodedTreeJsonObject)
    {
        Entry<String, JsonElement> rootNode = getFirstNodeOf( decodedTreeJsonObject );

        String rootNodeKey = rootNode.getKey();
        JsonElement traverseRootNode = rootNode.getValue();

        if (!rootNode.getValue().isJsonObject())
        {
            rootNodeKey = "FAKE_ROOT";
            traverseRootNode = decodedTreeJsonObject;
        }

        tree = new GenericDecodedTree( keyTransformer.retransform( rootNodeKey ) );

        DecodedNode currentRootNode = tree.getRootNode();

        traverseJson( traverseRootNode, currentRootNode );

        return tree;
    }

    private Entry<String, JsonElement> getFirstNodeOf(JsonElement root)
    {
        return root.getAsJsonObject().entrySet().iterator().next();
    }

    private JsonElement getJsonElementFromString(String decodedTreeAsString)
    {
        JsonParser parser = new JsonParser();
        JsonElement decodedTreeJson = parser.parse( decodedTreeAsString );
        return decodedTreeJson;
    }

    private void traverseJson(JsonElement jsonElement, DecodedNode currentParent)
    {
        if (jsonElement.isJsonObject())
        {
            final Set<Entry<String, JsonElement>> ens = ((JsonObject)jsonElement).entrySet();
            if (ens != null)
            {
                // Iterate JSON Elements with Key values
                for (Entry<String, JsonElement> en : ens)
                {
                    // add new parent to current parent
                    final DecodedNode newParent = new GenericDecodedNode( tree,
                                                                          currentParent,
                                                                          keyTransformer.retransform( en.getKey() ) );
                    traverseJson( en.getValue(), newParent );
                }
            }
        }
        else if (jsonElement.isJsonArray())
        {
            final JsonArray jsonArray = jsonElement.getAsJsonArray();
            int numberOfArrayElements = jsonArray.size();
            currentParent.setValue( "(" + numberOfArrayElements + ")" );

            for (int i = 0; i < numberOfArrayElements; i++)
            {
                final JsonElement jsonArrayElement = jsonArray.get( i );
                if (jsonArrayElement.isJsonPrimitive())
                {
                    currentParent.createChildNode( "<" + String.valueOf( i ) + ">", jsonArrayElement.getAsString() );
                }
                else
                {
                    final DecodedNode newParent = new GenericDecodedNode( tree,
                                                                          currentParent,
                                                                          keyTransformer.retransform( "<"
                                                                                  + String.valueOf( i ) + ">" ) );
                    traverseJson( jsonArrayElement, newParent );
                }
            }
        }
        else if (jsonElement.isJsonNull())
        {
            currentParent.setValue( "Null" );
        }
        else
        {
            if (!jsonElement.isJsonPrimitive())
            {
                throw new UnsupportedOperationException( "Unsupported JSON data type." );
            }

            // add new child to current parent
            currentParent.setValue( jsonElement.getAsString() );
        }
    }
}
