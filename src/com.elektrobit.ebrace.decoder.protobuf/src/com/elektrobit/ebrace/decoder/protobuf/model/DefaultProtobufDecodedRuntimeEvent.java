/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.decoder.protobuf.model;

import java.util.Collection;
import java.util.Set;

import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedNode;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedRuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedTree;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.RuntimeEventType;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.ProtoMessageValue;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.GeneratedMessage;

public class DefaultProtobufDecodedRuntimeEvent implements DecodedRuntimeEvent
{
    private final RuntimeEvent<?> selectedEvent;
    DecodedTree result;

    public DefaultProtobufDecodedRuntimeEvent(RuntimeEvent<?> selectedEvent)
    {
        this.selectedEvent = selectedEvent;
        GeneratedMessage message = (GeneratedMessage)((ProtoMessageValue)this.selectedEvent.getValue()).getValue();
        message.getAllFields().size();
        result = new DefaultMessageDecodedTree( selectedEvent.getRuntimeEventChannel().getName() );
        createTree( message, result.getRootNode() );
    }

    private void createTree(GeneratedMessage message, DecodedNode parentNode)
    {
        Set<FieldDescriptor> allFields = message.getAllFields().keySet();
        for (FieldDescriptor d : allFields)
        {
            String name = d.getName();
            Object o = message.getField( d );
            if (o instanceof GeneratedMessage)
            {
                GeneratedMessage childMessage = (GeneratedMessage)o;
                DecodedNode node = new DefaultMessageDecodedNode( this.result, parentNode, d.getName() );
                createTree( childMessage, node );
            }
            else if (o instanceof String)
            {
                handlePrimitiveType( parentNode, name, (String)o );
            }
            else if (o instanceof Number)
            {
                handlePrimitiveType( parentNode, name, String.valueOf( o ) );
            }
            else if (o instanceof Collection<?>)
            {
                handleCollections( (Collection<?>)o, parentNode, name );
            }
            else if (o instanceof EnumValueDescriptor)
            {
                handlePrimitiveType( parentNode, name, ((EnumValueDescriptor)o).getName() );
            }
        }
    }

    private void handlePrimitiveType(DecodedNode parentNode, String name, String value)
    {
        DecodedNode node = new DefaultMessageDecodedNode( this.result, parentNode, name );
        node.setValue( value );
    }

    private void handleCollections(Collection<?> list, DecodedNode parentNode, String name)
    {
        for (Object o : list)
        {

            if (o instanceof GeneratedMessage)
            {
                DecodedNode node = new DefaultMessageDecodedNode( this.result, parentNode, name );
                createTree( (GeneratedMessage)o, node );

            }
        }
    }

    @Override
    public DecodedTree getDecodedTree()
    {
        return result;
    }

    @Override
    public String getSummary()
    {
        return this.selectedEvent.getRuntimeEventChannel().getName();
    }

    @Override
    public RuntimeEventType getRuntimeEventType()
    {
        return RuntimeEventType.UNDEFINED;
    }

    @Override
    public RuntimeEventChannel<?> getRuntimeEventChannel()
    {
        return selectedEvent.getRuntimeEventChannel();
    }

    @Override
    public Object getRuntimeEventValue()
    {
        return selectedEvent.getValue();
    }

    @Override
    public RuntimeEvent<?> getRuntimeEvent()
    {
        return selectedEvent;
    }

}
