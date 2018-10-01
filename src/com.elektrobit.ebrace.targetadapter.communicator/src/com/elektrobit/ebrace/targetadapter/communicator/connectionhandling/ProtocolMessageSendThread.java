/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetadapter.communicator.connectionhandling;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.elektrobit.ebrace.targetadapter.communicator.api.OutgoingMessage;

public class ProtocolMessageSendThread implements Runnable
{
    private Socket sendSocket;
    private final List<OutgoingMessage> msgQueue;
    private volatile Thread sendThread;
    private volatile boolean shallRun;
    private final Object startStopMonitor;
    private final SocketClosedListener listener;

    public ProtocolMessageSendThread(SocketClosedListener listener)
    {
        this.listener = listener;
        startStopMonitor = new Object();
        shallRun = false;
        sendSocket = null;
        msgQueue = new CopyOnWriteArrayList<OutgoingMessage>();
        sendThread = null;
    }

    public boolean sendProtocolMessage(OutgoingMessage msg)
    {
        return addMessageToQueue( msg );
    }

    private boolean addMessageToQueue(OutgoingMessage msg)
    {
        boolean queueWasInitiallyEmpty = false;
        boolean messageAddedSuccessfully = false;
        synchronized (msgQueue)
        {
            if (msgQueue.size() == 0)
            {
                queueWasInitiallyEmpty = true;
            }

            messageAddedSuccessfully = msgQueue.add( msg );
            if (messageAddedSuccessfully && queueWasInitiallyEmpty)
            {
                msgQueue.notifyAll();
            }
        }
        return messageAddedSuccessfully;
    }

    public void start(Socket sendSocket)
    {
        this.sendSocket = sendSocket;
        sendThread = new Thread( this );
        allowSendCycles();
        sendThread.start();
    }

    public void stop() throws InterruptedException
    {
        permitSendCycles();
        clearAllQueuedMessages();
        shutDownSendSocket();
    }

    private void clearAllQueuedMessages()
    {
        synchronized (msgQueue)
        {
            msgQueue.clear();
            msgQueue.notifyAll();
            Thread.yield();
        }
    }

    private void shutDownSendSocket()
    {
        try
        {
            sendSocket.getOutputStream().close();
        }
        catch (IOException e)
        {
        }
    }

    private void allowSendCycles()
    {
        synchronized (startStopMonitor)
        {
            shallRun = true;
        }
    }

    private void permitSendCycles()
    {
        synchronized (startStopMonitor)
        {
            shallRun = false;
        }
    }

    private boolean nextSendCycleAllowed()
    {
        boolean allowed = false;
        synchronized (startStopMonitor)
        {
            allowed = shallRun;
        }
        return allowed;
    }

    private OutgoingMessage waitForNextMessageToBeAvailable()
    {
        if (waitForMessageQueueNotEmpty())
        {
            return getNextMessageFromQueue();
        }
        else
        {
            return null;
        }
    }

    private OutgoingMessage getNextMessageFromQueue()
    {
        synchronized (msgQueue)
        {
            if (msgQueue.size() > 0)
            {
                return msgQueue.remove( 0 );
            }
            else
            {
                return null;
            }
        }
    }

    private boolean waitForMessageQueueNotEmpty()
    {
        boolean queueNotEmpty = false;
        synchronized (msgQueue)
        {
            while (msgQueue.size() == 0 && nextSendCycleAllowed())
            {
                try
                {
                    msgQueue.wait();
                }
                catch (InterruptedException e)
                {
                }
            }

            if (msgQueue.size() > 0)
            {
                queueNotEmpty = true;
            }
        }

        return queueNotEmpty;
    }

    @Override
    public void run()
    {
        while (nextSendCycleAllowed())
        {
            OutgoingMessage msg = waitForNextMessageToBeAvailable();
            if (msg != null)
            {
                try
                {
                    sendMessageToOutputStream( new DataOutputStream( sendSocket.getOutputStream() ), msg );
                }
                catch (IOException e)
                {
                    permitSendCycles();
                    postSocketClosed();
                }
                finally
                {
                    Thread.yield();
                }
            }
        }
    }

    private void sendMessageToOutputStream(DataOutputStream stream, OutgoingMessage msg) throws IOException
    {
        stream.write( msg.toByteArray() );
    }

    private void postSocketClosed()
    {
        listener.onSocketClosed();
    }

}
