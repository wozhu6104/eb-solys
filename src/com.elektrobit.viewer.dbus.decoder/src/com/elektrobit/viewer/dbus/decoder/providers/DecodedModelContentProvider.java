/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.viewer.dbus.decoder.providers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.elektrobit.ebrace.viewer.common.provider.ChannelValueProvider;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedNode;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedRuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedTree;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public class DecodedModelContentProvider implements ITreeContentProvider
{

    @Override
    public Object[] getElements(Object inputElement)
    {
        if (inputElement instanceof List)
        {
            return getElementsForList( (List<?>)inputElement );
        }
        else if (inputElement instanceof DecodedRuntimeEvent)
        {
            return getElementsForDecodedRuntimeEvent( (DecodedRuntimeEvent)inputElement );
        }
        else if (inputElement instanceof Map)
        {
            return getElementsForMap( (Map<?, ?>)inputElement );
        }
        else
        {
            return Collections.emptyList().toArray();
        }
    }

    @SuppressWarnings("unchecked")
    private Object[] getElementsForList(List<?> inputElement)
    {
        List<DecodedNode> elements = new ArrayList<DecodedNode>();
        for (DecodedRuntimeEvent nextEvent : (List<DecodedRuntimeEvent>)inputElement)
        {
            if (nextEvent != null)
            {
                DecodedTree decodedTree = nextEvent.getDecodedTree();
                DecodedNode rootNode = decodedTree.getRootNode();
                List<DecodedNode> children = rootNode.getChildren();
                elements.addAll( children );
            }
        }
        return elements.toArray();
    }

    @SuppressWarnings("unchecked")
    private Object[] getElementsForMap(Map<?, ?> inputElement)
    {
        Map<RuntimeEventChannel<?>, Object> channelValues = (Map<RuntimeEventChannel<?>, Object>)inputElement;
        final List<ChannelValueProvider> input = new ArrayList<ChannelValueProvider>();

        for (final Map.Entry<RuntimeEventChannel<?>, Object> entry : channelValues.entrySet())
        {
            Object value = entry.getValue();
            if (value != null)
            {
                RuntimeEventChannel<?> channel = entry.getKey();
                List<DecodedNode> decodedNodes = ((DecodedRuntimeEvent)entry.getValue()).getDecodedTree().getRootNode()
                        .getChildren();
                ChannelValueProvider channelValueProvider = new ChannelValueProvider( channel, decodedNodes );
                input.add( channelValueProvider );
            }
            else
            {
                input.add( new ChannelValueProvider( entry.getKey(), entry.getValue() ) );
            }
        }
        return input.toArray();
    }

    private Object[] getElementsForDecodedRuntimeEvent(DecodedRuntimeEvent inputElement)
    {
        Object[] result = null;
        DecodedTree decodedTree = inputElement.getDecodedTree();
        if (decodedTree != null)
        {
            DecodedNode rootNode = decodedTree.getRootNode();
            if (rootNode != null)
            {
                List<DecodedNode> children = rootNode.getChildren();
                if (children != null)
                {
                    result = children.toArray();
                }
            }
        }
        else
        {
            result = new Object[0];
        }

        return result;
    }

    @Override
    public Object[] getChildren(Object parentElement)
    {
        Object[] result = Collections.emptyList().toArray();
        if (parentElement instanceof DecodedRuntimeEvent)
        {
            DecodedTree tree = ((DecodedRuntimeEvent)parentElement).getDecodedTree();
            result = tree.getRootNode().getChildren().toArray();
        }
        else if (parentElement instanceof DecodedNode)
        {
            result = ((DecodedNode)parentElement).getChildren().toArray();
        }
        else if (parentElement instanceof ChannelValueProvider)
        {
            result = ((ChannelValueProvider)parentElement).getNodes().toArray();
        }

        return result;
    }

    @Override
    public Object getParent(Object element)
    {
        return null;
    }

    @Override
    public boolean hasChildren(Object element)
    {
        if (element instanceof DecodedRuntimeEvent)
        {
            DecodedTree tree = (DecodedTree)element;
            return !tree.getRootNode().getChildren().isEmpty();
        }
        else if (element instanceof DecodedNode)
        {
            return !((DecodedNode)element).getChildren().isEmpty();
        }
        else if (element instanceof ChannelValueProvider)
        {
            ChannelValueProvider channelValueProvider = (ChannelValueProvider)element;
            if (!channelValueProvider.getNodes().isEmpty())
            {
                for (DecodedNode node : channelValueProvider.getNodes())
                {
                    return !node.getChildren().isEmpty();
                }
            }
        }
        return false;
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
    }

    @Override
    public void dispose()
    {
    }

}
