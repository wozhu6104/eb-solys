/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.dlt.internal.connection;

import java.io.IOException;
import java.net.Socket;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionType;
import com.elektrobit.ebrace.core.usermessagelogger.api.UserMessageLogger;
import com.elektrobit.ebrace.targetadapter.communicator.api.MessageReader;
import com.elektrobit.ebrace.targetadapter.communicator.api.OutgoingMessage;
import com.elektrobit.ebrace.targetadapter.communicator.api.TargetConnection;
import com.elektrobit.ebrace.targetadapter.communicator.api.TargetConnectionDownListener;
import com.elektrobit.ebrace.targetadapter.communicator.connectionhandling.ProtoMsgCacheDataAvailableListener;
import com.elektrobit.ebrace.targetadapter.communicator.connectionhandling.ProtocolMessageSendThread;
import com.elektrobit.ebrace.targetadapter.communicator.connectionhandling.SocketClosedListener;
import com.elektrobit.ebrace.targetadapter.communicator.connectionhandling.StreamCacheImpl;
import com.elektrobit.ebrace.targetadapter.communicator.services.MessageDispatcher;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext.SOURCE_TYPE;

import lombok.extern.log4j.Log4j;

@Log4j
public class DltConnectionImpl implements TargetConnection, SocketClosedListener, ProtoMsgCacheDataAvailableListener
{
    private Socket targetConnectionSocket;
    private boolean isConnected;
    private final ProtocolMessageSendThread sender;
    private final StreamCacheImpl socketStreamCache;
    private final ConnectionModel connectionModel;
    private Thread readThread;
    private boolean shallRead = false;

    private final Object resumeReading = new Object();
    private volatile boolean liveMode = true;

    // Only for debug purpose
    private enum DBG_STATE {
        NOT_RUNNING, READ, WAITING
    }

    private volatile DBG_STATE debugState = DBG_STATE.NOT_RUNNING;
    private final MessageDispatcher messageDispatcher;
    private final ConnectionType connectionType;
    private TargetConnectionDownListener listener;

    public DltConnectionImpl(ConnectionType connectionType, ConnectionModel connectionModel,
            UserMessageLogger userMessageLogger)
    {
        this.connectionType = connectionType;
        this.connectionModel = connectionModel;
        targetConnectionSocket = null;

        String cacheFileName = "stream-cache" + connectionModel.getHost().replace( ":", "_" ) + "-"
                + connectionModel.getPort() + "." + connectionModel.getConnectionType().getExtension() + ".tmp";

        messageDispatcher = getMessageDispatcher( connectionModel );

        MessageReader<?> messageReader = messageDispatcher.getMessageReader();

        socketStreamCache = new StreamCacheImpl( connectionModel, messageReader, cacheFileName, this );
        sender = new ProtocolMessageSendThread( this );
    }

    private MessageDispatcher getMessageDispatcher(ConnectionModel connectionModel)
    {
        if (connectionType != null)
        {
            BundleContext bundleContext = FrameworkUtil.getBundle( this.getClass() ).getBundleContext();
            ServiceTracker<MessageDispatcher, MessageDispatcher> messageDispatcherTracker = new ServiceTracker<MessageDispatcher, MessageDispatcher>( bundleContext,
                                                                                                                                                      MessageDispatcher.class
                                                                                                                                                              .getName(),
                                                                                                                                                      null );
            messageDispatcherTracker.open();
            ServiceReference<MessageDispatcher>[] serviceReferences = messageDispatcherTracker.getServiceReferences();
            String desiredExtension = connectionType.getExtension();

            for (ServiceReference<MessageDispatcher> serviceReference : serviceReferences)
            {
                MessageDispatcher service = bundleContext.getService( serviceReference );
                if (service.getConnectionType().getExtension().equals( desiredExtension ))
                {
                    return service;
                }
            }
        }
        return null;
    }

    @Override
    public void setTargetConnectionDownListener(TargetConnectionDownListener listener)
    {
        this.listener = listener;
    }

    @Override
    public void unsetTargetConnectionDownListener(TargetConnectionDownListener listener)
    {
        this.listener = null;
    }

    @Override
    public boolean sendMessage(OutgoingMessage msg)
    {
        if (isConnected)
        {
            return sender.sendProtocolMessage( msg );
        }
        return false;
    }

    @Override
    @Deprecated
    public boolean connect()
    {
        System.out.println( "connect" );
        startDataReceiving();
        try
        {
            isConnected = true;
            targetConnectionSocket = new Socket( connectionModel.getHost(), connectionModel.getPort() );

            socketStreamCache.start( targetConnectionSocket.getInputStream() );
            sender.start( targetConnectionSocket );
            System.out.println( "set ip" );
        }
        catch (IOException e)
        {
            targetConnectionSocket = null;
            System.out.println( "Not able to establish a connection to: " + connectionModel.getHost() + ":"
                    + connectionModel.getPort() + ". Reason was: <<" + e.getMessage() + ">>" );
            isConnected = false;
            setConnected( false );
        }

        return isConnected;
    }

    @Override
    public boolean disconnect()
    {
        if (isConnected)
        {
            try
            {
                stopDataReceiving();
                socketStreamCache.stop();
                sender.stop();
                targetConnectionSocket.close();
                targetConnectionSocket = null;
                isConnected = false;

                if (listener != null)
                {
                    listener.onConnectionDown( this );
                }
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
                isConnected = false;
                setConnected( false );
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

        }

        return true;
    }

    public boolean isConnected()
    {
        return isConnected;
    }

    private DataSourceContext getDataSourceContext()
    {
        return new DataSourceContext( SOURCE_TYPE.CONNECTION, connectionModel.getId() );
    }

    @Override
    public void onSocketClosed()
    {
        disconnect();
    }

    private void setConnected(boolean connected)
    {
        if (connectionModel != null)
        {
            connectionModel.setConnected( connected );
        }
    }

    private void startDataReceiving()
    {
        if (!shallRead)
        {
            shallRead = true;
            readThread = new Thread( new Runnable()
            {
                @Override
                public void run()
                {
                    DataSourceContext dataSourceContext = getDataSourceContext();

                    while (shallRead)
                    {
                        if (liveMode)
                        {
                            Object msg = socketStreamCache.getNextMsg();
                            if (msg != null)
                            {
                                messageDispatcher.forwardMessage( msg, connectionModel, dataSourceContext );
                                changeStateIfNeeded( DBG_STATE.READ );
                            }
                            else
                            {
                                notifyCacheAndWait();
                            }
                        }
                        else
                        {
                            waitForLiveMode();
                        }
                    }
                }

                private void notifyCacheAndWait()
                {
                    synchronized (resumeReading)
                    {
                        try
                        {
                            changeStateIfNeeded( DBG_STATE.WAITING );
                            socketStreamCache.waitForData();
                            resumeReading.wait();
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }

                private void waitForLiveMode()
                {
                    synchronized (resumeReading)
                    {
                        try
                        {
                            changeStateIfNeeded( DBG_STATE.WAITING );
                            resumeReading.wait();
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }

            } );
            readThread.setName( "Cache: file reader:" + connectionModel.getId() );
            readThread.start();
        }
    }

    private void stopDataReceiving()
    {
        shallRead = false;
        resumeReadingThread();
        if (readThread != null)
        {
            try
            {
                readThread.join( 100 );
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        changeStateIfNeeded( DBG_STATE.NOT_RUNNING );
    }

    private void changeStateIfNeeded(DBG_STATE newState)
    {
        if (debugState != newState)
        {
            DBG_STATE stateBefore = debugState;
            debugState = newState;
            log.debug( stateBefore + " --> " + debugState );
        }
    }

    @Override
    public void resumeReading()
    {
        this.liveMode = true;
        resumeReadingThread();
    }

    @Override
    public void pauseReading()
    {
        this.liveMode = false;
        resumeReadingThread();
    }

    @Override
    public void onNewDataInCacheAvailable()
    {
        resumeReadingThread();
    }

    private void resumeReadingThread()
    {
        synchronized (resumeReading)
        {
            resumeReading.notify();
        }
    }
}
