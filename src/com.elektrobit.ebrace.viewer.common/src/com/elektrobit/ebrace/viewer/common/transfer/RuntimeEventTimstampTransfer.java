/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.transfer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;

public class RuntimeEventTimstampTransfer extends ByteArrayTransfer
{
    private static RuntimeEventTimstampTransfer instance = new RuntimeEventTimstampTransfer();
    private static final String RUNTIME_EVENT_TIMESTAMP_TRANSFER_TYPE = "runtime_event_timestamp_transfer_type";
    private static final int RUNTIME_EVENT_TIMESTAMP_TRANSFER_TYPE_ID = registerType( RUNTIME_EVENT_TIMESTAMP_TRANSFER_TYPE );

    RuntimeEventAcceptor runtimeEventAcceptor = new GenericOSGIServiceTracker<RuntimeEventAcceptor>( RuntimeEventAcceptor.class )
            .getService();

    public static RuntimeEventTimstampTransfer getInstance()
    {
        return instance;
    }

    @Override
    protected int[] getTypeIds()
    {
        return new int[]{RUNTIME_EVENT_TIMESTAMP_TRANSFER_TYPE_ID};
    }

    @Override
    protected String[] getTypeNames()
    {
        return new String[]{RUNTIME_EVENT_TIMESTAMP_TRANSFER_TYPE};
    }

    @Override
    public void javaToNative(Object object, TransferData transferData)
    {
        if (!checkMyType( object ) || !isSupportedType( transferData ))
        {
            return;
        }
        @SuppressWarnings("unchecked")
        List<RuntimeEvent<?>> myTypes = (List<RuntimeEvent<?>>)object;
        try
        {
            // write data to a byte array and then ask super to convert to pMedium
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            DataOutputStream writeOut = new DataOutputStream( out );
            for (RuntimeEvent<?> event : myTypes)
            {
                // byte[] buffer = channel.getName().getBytes();
                writeOut.writeLong( event.getTimestamp() );
            }
            byte[] buffer = out.toByteArray();
            writeOut.close();
            super.javaToNative( buffer, transferData );
        }
        catch (IOException e)
        {
        }
    }

    @Override
    public Object nativeToJava(TransferData transferData)
    {
        if (isSupportedType( transferData ))
        {
            byte[] buffer = (byte[])super.nativeToJava( transferData );
            if (buffer == null)
                return null;
            List<Long> myData = new ArrayList<Long>();
            try
            {
                ByteArrayInputStream in = new ByteArrayInputStream( buffer );
                DataInputStream readIn = new DataInputStream( in );
                while (readIn.available() > 0)
                {
                    long timestamp = readIn.readLong();
                    myData.add( timestamp );
                }
                readIn.close();
            }
            catch (IOException ex)
            {
                return null;
            }
            return myData;
        }
        return null;
    }

    boolean checkMyType(Object object)
    {
        if (object == null || !(object instanceof List) || ((List<?>)object).size() == 0)
        {
            return false;
        }
        return true;
    }

    @Override
    protected boolean validate(Object object)
    {
        return checkMyType( object );
    }
}
