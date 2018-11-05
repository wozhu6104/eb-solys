/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.app.racescriptexecutor.impl.service;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.reset.ClearAllDataInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.reset.ClearAllDataInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.usermessagelogger.UserMessageLoggerTypes;
import com.elektrobit.ebrace.core.plantumlrenderer.api.PlantUmlRendererService;
import com.elektrobit.ebrace.core.systemmodel.api.SystemModel;
import com.elektrobit.ebrace.core.systemmodel.api.SystemModelAccess;
import com.elektrobit.ebrace.core.systemmodel.api.SystemModelChangedListener;
import com.elektrobit.ebrace.core.systemmodel.api.ViewModelGenerator;
import com.elektrobit.ebrace.core.targetdata.api.json.JsonEventHandler;
import com.elektrobit.ebrace.core.timesegmentmanager.api.TimeSegmentAcceptorService;
import com.elektrobit.ebrace.core.usermessagelogger.api.UserMessageLogger;
import com.elektrobit.ebrace.franca.common.validator.api.FrancaTraceValidator;
import com.elektrobit.ebrace.platform.commandlineparser.extern.interfaces.CommandLineParser;
import com.elektrobit.ebrace.platform.commandlineparser.extern.interfaces.ValueOfArgumentMissingException;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;
import com.elektrobit.ebsolys.core.targetdata.api.ModelElement;
import com.elektrobit.ebsolys.core.targetdata.api.color.ChannelColorProviderService;
import com.elektrobit.ebsolys.core.targetdata.api.color.SColor;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedNode;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedRuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedTree;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventTag;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.STimeSegment;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarkerManager;
import com.elektrobit.ebsolys.decoder.common.api.DecodedRuntimeEventStringConverter;
import com.elektrobit.ebsolys.decoder.common.api.DecoderServiceManager;
import com.elektrobit.ebsolys.decoder.common.services.DecoderService;
import com.elektrobit.ebsolys.script.external.Console;
import com.elektrobit.ebsolys.script.external.SChart;
import com.elektrobit.ebsolys.script.external.SConnection;
import com.elektrobit.ebsolys.script.external.SHtmlView;
import com.elektrobit.ebsolys.script.external.SSnapshot;
import com.elektrobit.ebsolys.script.external.STable;
import com.elektrobit.ebsolys.script.external.STimelineView;
import com.elektrobit.ebsolys.script.external.ScriptContext;

public class ScriptContextImpl implements ScriptContext, ClearAllDataInteractionCallback
{
    private final RuntimeEventAcceptor runtimeEventAcceptor;
    private final Console scriptConsole;
    private final TimeMarkerManager timeMarkerManager;
    private final FrancaTraceValidator francaTraceValidator;
    private final DecodedRuntimeEventStringConverter decodedRuntimeEventStringConverter;
    private final DecoderServiceManager decoderServiceManager;
    private final ChannelColorProviderService channelColorProviderService;
    private final UIResourcesFromScriptCreator uiResourcesFromScriptCreator;
    private final UserMessageLogger userMessageLogger;
    private final TimeSegmentAcceptorService timeSegmentAcceptor;
    private final PlantUmlRendererService plantUmlRendererService;
    private final CommandLineParser commandlineParser;
    private final JsonEventHandler jsonEventHandler;
    private final SystemModelAccess systemModel;

    public ScriptContextImpl(final RuntimeEventAcceptor runtimeEventAcceptor, final Console scriptConsole,
            final TimeMarkerManager timeMarkerManager, FrancaTraceValidator francaTraceValidator,
            DecodedRuntimeEventStringConverter decodedRuntimeEventStringConverter,
            DecoderServiceManager decoderServiceManager, ChannelColorProviderService channelColorProviderService,
            ResourcesModelManager resourcesModelManager, UserMessageLogger userMessageLogger,
            TimeSegmentAcceptorService timeSegmentAcceptor, PlantUmlRendererService plantUmlRendererService,
            CommandLineParser commandlineParser, JsonEventHandler jsonEventHandler, SystemModelAccess systemModel)
    {
        this.runtimeEventAcceptor = runtimeEventAcceptor;
        this.scriptConsole = scriptConsole;
        this.timeMarkerManager = timeMarkerManager;
        this.francaTraceValidator = francaTraceValidator;
        this.decodedRuntimeEventStringConverter = decodedRuntimeEventStringConverter;
        this.decoderServiceManager = decoderServiceManager;
        this.channelColorProviderService = channelColorProviderService;
        this.userMessageLogger = userMessageLogger;
        this.timeSegmentAcceptor = timeSegmentAcceptor;
        this.plantUmlRendererService = plantUmlRendererService;
        this.commandlineParser = commandlineParser;
        this.jsonEventHandler = jsonEventHandler;
        this.systemModel = systemModel;
        uiResourcesFromScriptCreator = new UIResourcesFromScriptCreator( scriptConsole, resourcesModelManager );
    }

    @Override
    public boolean loadFile(String path)
    {
        ScriptFileLoader scriptFileLoader = new ScriptFileLoader( path, scriptConsole );
        boolean result = scriptFileLoader.loadFile();
        return result;
    }

    @Override
    public void clearAllData()
    {
        ClearAllDataInteractionUseCase useCase = UseCaseFactoryInstance.get()
                .makeClearAllDataInteractionUseCase( this );
        useCase.reset();
        useCase.unregister();
    }

    @Override
    public SConnection createConnection(String name, String host, int port, boolean saveDataToFile)
    {
        ScriptConnectionCreator connectionCreator = new ScriptConnectionCreator( scriptConsole );
        SConnection connection = connectionCreator.createConnection( name, host, port, saveDataToFile );
        return connection;
    }

    @Override
    public boolean connectToTarget(SConnection connection)
    {
        ScriptTargetConnector scriptTargetConnector = new ScriptTargetConnector( connection );
        boolean connected = scriptTargetConnector.connect();
        return connected;
    }

    @Override
    public void disconnectFromTarget(SConnection connection)
    {
        ScriptTargetDisconnector disconnector = new ScriptTargetDisconnector();
        disconnector.disconnectFromTarget( connection );
        return;
    }

    @Override
    public void disconnectAllTargets()
    {
        ScriptTargetDisconnector disconnector = new ScriptTargetDisconnector();
        disconnector.disconnectFromAllTargets();
    }

    @Override
    public void onResetDone()
    {
    }

    @Override
    public void consolePrint(String text)
    {
        scriptConsole.print( text );
    }

    @Override
    public void consolePrintln(String text)
    {
        scriptConsole.println( text );
    }

    @Override
    public SortedSet<TimeMarker> getAllTimemarkers()
    {
        return timeMarkerManager.getAllTimeMarkers();
    }

    @Override
    public SortedSet<TimeMarker> getTimemarkersFrom(long timestamp)
    {
        return timeMarkerManager.getAllTimeMarkersGreaterThanTimestamp( timestamp );
    }

    @Override
    public SortedSet<TimeMarker> getTimemarkersBetween(long fromTimestamp, long toTimestamp)
    {
        return timeMarkerManager.getAllTimeMarkersBetweenTimestamp( fromTimestamp, toTimestamp );
    }

    @Override
    public TimeMarker createTimemarker(long timestamp)
    {
        return timeMarkerManager.createNewTimeMarker( timestamp );
    }

    @Override
    public TimeMarker createTimemarker(long timestamp, String name)
    {
        TimeMarker timeline = timeMarkerManager.createNewTimeMarker( timestamp );
        timeline.setName( name );

        return timeline;
    }

    @Override
    public void removeAllTimemarkers()
    {
        timeMarkerManager.removeAllTimeMarkers();
    }

    @Override
    public void removeTimemarker(TimeMarker marker)
    {
        timeMarkerManager.removeTimeMarker( marker );
    }

    @Override
    public void removeAllTimemarkersWithPrefix(String prefix)
    {
        timeMarkerManager.removeAllTimeMarkersWithPrefix( prefix );
    }

    @Override
    public void jumpTo(TimeMarker timeMarker)
    {
        timeMarkerManager.setCurrentSelectedTimeMarker( timeMarker );
    }

    @Override
    public List<RuntimeEventChannel<?>> getAllChannels()
    {
        return runtimeEventAcceptor.getRuntimeEventChannels();
    }

    @Override
    public <T> RuntimeEventChannel<T> createOrGetChannel(String name, Unit<T> unit, String description)
    {
        return runtimeEventAcceptor.createOrGetRuntimeEventChannel( name, unit, description );
    }

    @Override
    public <T> RuntimeEventChannel<T> createOrGetChannel(String name, Unit<T> unit, String description, int r, int g,
            int b)
    {
        RuntimeEventChannel<T> newChannel = runtimeEventAcceptor.createRuntimeEventChannel( name, unit, description );
        channelColorProviderService.setColorForChannel( newChannel, r, g, b );
        return newChannel;
    }

    @Override
    public <T> RuntimeEventChannel<T> createOrGetChannel(String name, Unit<T> unit, String description, int[] rgb)
    {
        if (rgb.length != 3)
        {
            throw new IllegalArgumentException( "Length of RGB array has to be 3" );
        }
        RuntimeEventChannel<T> newChannel = runtimeEventAcceptor.createRuntimeEventChannel( name, unit, description );
        channelColorProviderService.setColorForChannel( newChannel, rgb[0], rgb[1], rgb[2] );
        return newChannel;
    }

    @Override
    public <T> void removeChannel(RuntimeEventChannel<T> channel)
    {
        runtimeEventAcceptor.removeRuntimeEventChannel( channel );
    }

    @Override
    public <T> void addEventToChannel(RuntimeEventChannel<T> channel, long timestamp, T value,
            ModelElement modelElement)
    {
        runtimeEventAcceptor.acceptEventMicros( timestamp, channel, modelElement, value );
    }

    @Override
    public void addJsonEvent(String jsonEvent)
    {
        jsonEventHandler.handle( jsonEvent );
    }

    @Override
    public <T> void addEvent(RuntimeEventChannel<T> channel, long timestamp, T value)
    {
        addEventToChannel( channel, timestamp, value, null );
    }

    @Override
    public List<RuntimeEvent<?>> getEvents(RuntimeEventChannel<?> channel)
    {
        return runtimeEventAcceptor.getRuntimeEventsOfRuntimeEventChannel( channel );
    }

    @Override
    public List<RuntimeEvent<?>> getAllEventsFromChannels(List<RuntimeEventChannel<?>> channels)
    {
        List<RuntimeEvent<?>> allRuntimeEvents = new ArrayList<RuntimeEvent<?>>();
        for (RuntimeEventChannel<?> nextChannel : channels)
        {
            allRuntimeEvents.addAll( getEvents( nextChannel ) );
        }
        return allRuntimeEvents;
    }

    @Override
    public RuntimeEvent<?> getEventAt(RuntimeEventChannel<?> channel, TimeMarker timeMarker)
    {
        List<RuntimeEventChannel<?>> channelInList = new ArrayList<>( Arrays.asList( channel ) );
        long timestamp = timeMarker.getTimestamp();
        Map<RuntimeEventChannel<?>, RuntimeEvent<?>> result = runtimeEventAcceptor
                .getRuntimeEventsOfChannelsForTimestamp( channelInList, timestamp );
        RuntimeEvent<?> onlyValue = result.values().iterator().next();
        return onlyValue;
    }

    @Override
    public void setColorOfChannel(RuntimeEventChannel<?> channel, int r, int g, int b)
    {
        channelColorProviderService.setColorForChannel( channel, r, g, b );
    }

    @Override
    public void setColorOfChannel(RuntimeEventChannel<?> channel, int[] rgb)
    {
        if (rgb.length != 3)
        {
            throw new IllegalArgumentException( "Length of RGB array has to be 3" );
        }
        channelColorProviderService.setColorForChannel( channel, rgb[0], rgb[1], rgb[2] );
    }

    @Override
    public int[] getColorOfChannel(RuntimeEventChannel<?> channel)
    {
        SColor colorAsRaceColor = channelColorProviderService.getColorForChannel( channel );
        if (colorAsRaceColor == null)
        {
            throw new IllegalArgumentException( "Channel " + channel.getName() + " has no color assigned." );
        }
        else
        {
            return new int[]{colorAsRaceColor.getRed(), colorAsRaceColor.getGreen(), colorAsRaceColor.getBlue()};
        }
    }

    @Override
    public DecodedRuntimeEvent decode(final RuntimeEvent<?> runtimeEventToDecode)
    {
        DecoderService decoderServiceForEvent = decoderServiceManager.getDecoderServiceForEvent( runtimeEventToDecode );

        DecodedRuntimeEvent decodedRuntimeEvent = null;

        if (decoderServiceForEvent != null)
        {
            decodedRuntimeEvent = decoderServiceForEvent.decode( runtimeEventToDecode );
        }

        return decodedRuntimeEvent;
    }

    @Override
    public boolean isValidFrancaTrace(String fullQualifiedInterface, List<String> trace)
    {
        return francaTraceValidator.validate( fullQualifiedInterface, trace );
    }

    @Override
    public DecodedTree convertFromString(String decodedTreeAsString)
    {
        return decodedRuntimeEventStringConverter.convertFromString( decodedTreeAsString );
    }

    @Override
    public String convertToString(DecodedTree decodedTree)
    {
        return decodedRuntimeEventStringConverter.convertToString( decodedTree );
    }

    @Override
    public void setColorOfChanel(RuntimeEventChannel<?> channel, int r, int g, int b)
    {
        setColorOfChannel( channel, r, g, b );
    }

    @Override
    public void setColorOfChanel(RuntimeEventChannel<?> channel, int[] rgb)
    {
        setColorOfChannel( channel, rgb );
    }

    @Override
    public DecodedNode getFirstNode(DecodedNode startNode, String key)
    {
        DecodedNode result = null;
        if (startNode.getName().equals( key ))
        {
            result = startNode;
        }
        else
        {
            for (DecodedNode child : startNode.getChildren())
            {
                result = getFirstNode( child, key );
                if (result != null)
                {
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public List<DecodedNode> getNodes(DecodedNode startNode, String key)
    {
        List<DecodedNode> result = new ArrayList<DecodedNode>();
        if (startNode.getName().equals( key ))
        {
            result.add( startNode );
        }
        for (DecodedNode child : startNode.getChildren())
        {
            result.addAll( getNodes( child, key ) );
        }
        return result;
    }

    @Override
    public String getFirstValue(DecodedNode startNode, String key)
    {
        DecodedNode node = getFirstNode( startNode, key );
        return node != null ? node.getValue() : null;
    }

    @Override
    public List<String> getValues(DecodedNode startNode, String key)
    {
        List<String> result = new ArrayList<String>();
        for (DecodedNode node : getNodes( startNode, key ))
        {
            result.add( node.getValue() );
        }
        return result;
    }

    @Override
    public boolean keyExists(DecodedNode startNode, String key)
    {
        return getFirstValue( startNode, key ) != null;
    }

    @Override
    public int numberOfKeys(DecodedNode startNode, String key)
    {
        return getNodes( startNode, key ).size();
    }

    @Override
    public DecodedNode getFirstNode(DecodedRuntimeEvent decodedRuntimeEvent, String key)
    {
        return getFirstNode( decodedRuntimeEvent.getDecodedTree().getRootNode(), key );
    }

    @Override
    public List<DecodedNode> getNodes(DecodedRuntimeEvent decodedRuntimeEvent, String key)
    {
        return getNodes( decodedRuntimeEvent.getDecodedTree().getRootNode(), key );
    }

    @Override
    public String getFirstValue(DecodedRuntimeEvent decodedRuntimeEvent, String key)
    {
        return getFirstValue( decodedRuntimeEvent.getDecodedTree().getRootNode(), key );
    }

    @Override
    public List<String> getValues(DecodedRuntimeEvent decodedRuntimeEvent, String key)
    {
        return getValues( decodedRuntimeEvent.getDecodedTree().getRootNode(), key );
    }

    @Override
    public boolean keyExists(DecodedRuntimeEvent decodedRuntimeEvent, String key)
    {
        return keyExists( decodedRuntimeEvent.getDecodedTree().getRootNode(), key );
    }

    @Override
    public int numberOfKeys(DecodedRuntimeEvent decodedRuntimeEvent, String key)
    {
        return numberOfKeys( decodedRuntimeEvent.getDecodedTree().getRootNode(), key );
    }

    @Override
    public STable createOrGetTable(String name)
    {
        return uiResourcesFromScriptCreator.createOrGetTable( name );
    }

    @Override
    public STable getTable(String name)
    {
        return uiResourcesFromScriptCreator.getTable( name );
    }

    @Override
    public SChart createOrGetChart(String name, CHART_TYPE chartType)
    {
        return uiResourcesFromScriptCreator.createOrGetChart( name, chartType );
    }

    @Override
    public SChart getChart(String name)
    {
        return uiResourcesFromScriptCreator.getChart( name );
    }

    @Override
    public STimelineView createOrGetTimelineView(String name)
    {
        return uiResourcesFromScriptCreator.createOrGetTimelineView( name );
    }

    @Override
    public STimelineView getTimelineView(String name)
    {
        return uiResourcesFromScriptCreator.getTimelineView( name );
    }

    @Override
    public SHtmlView getHtmlView(String name)
    {
        return uiResourcesFromScriptCreator.getHtmlView( name );
    }

    @Override
    public SHtmlView createOrGetHtmlView(String name)
    {
        return uiResourcesFromScriptCreator.createOrGetHtmlView( name );
    }

    @Override
    public void setContent(SHtmlView view, String text)
    {
        uiResourcesFromScriptCreator.setContent( view, text );
    }

    @Override
    public void callJavaScriptFunction(SHtmlView view, String function, String arg)
    {
        uiResourcesFromScriptCreator.callJavaScriptFunction( view, function, arg );
    }

    @Override
    public SSnapshot createOrGetSnapshot(String name)
    {
        return uiResourcesFromScriptCreator.createOrGetSnapshot( name );
    }

    @Override
    public SSnapshot getSnapshot(String name)
    {
        return uiResourcesFromScriptCreator.getSnapshot( name );
    }

    @Override
    public RuntimeEvent<?> setTag(RuntimeEvent<?> event, RuntimeEventTag tag, String tagDescription)
    {
        RuntimeEvent<?> resultEvent = null;
        if (event == null)
        {
            consolePrintln( "Error when calling setTag: RuntimeEvent must not be null!" );
        }
        else if (tag == null)
        {
            consolePrintln( "Error when calling setTag: RuntimeEventTag must not be null!" );
        }
        else
        {
            resultEvent = runtimeEventAcceptor.setTag( event, tag, tagDescription );
        }

        return resultEvent;
    }

    @Override
    public RuntimeEvent<?> clearTag(RuntimeEvent<?> event)
    {
        return runtimeEventAcceptor.clearTag( event );
    }

    @Override
    public void messageBoxInfo(String message)
    {
        userMessageLogger.logUserMessage( UserMessageLoggerTypes.INFO, message );
    }

    @Override
    public void messageBoxWarning(String message)
    {
        userMessageLogger.logUserMessage( UserMessageLoggerTypes.WARNING, message );
    }

    @Override
    public void messageBoxError(String message)
    {
        userMessageLogger.logUserMessage( UserMessageLoggerTypes.ERROR, message );
    }

    @Override
    public RuntimeEventChannel<STimeSegment> createOrGetTimeSegmentChannel(String name, String description)
    {
        return timeSegmentAcceptor.createOrGetTimeSegmentChannel( name, description );
    }

    @Override
    public STimeSegment add(RuntimeEventChannel<STimeSegment> timeSegmentChannel, RuntimeEvent<?> startEvent,
            RuntimeEvent<?> endEvent)
    {
        return timeSegmentAcceptor.add( timeSegmentChannel, startEvent, endEvent );
    }

    @Override
    public STimeSegment add(RuntimeEventChannel<STimeSegment> timeSegmentChannel, RuntimeEvent<?> startEvent,
            RuntimeEvent<?> endEvent, String label)
    {
        return timeSegmentAcceptor.add( timeSegmentChannel, startEvent, endEvent, label );
    }

    @Override
    public List<STimeSegment> getTimeSegments(RuntimeEventChannel<STimeSegment> timeSegmentChannel)
    {
        return timeSegmentAcceptor.getTimeSegments( timeSegmentChannel );
    }

    @Override
    public void setColor(STimeSegment timeSegment, int r, int g, int b)
    {
        timeSegmentAcceptor.setColor( timeSegment, new SColor( r, g, b ) );
    }

    @Override
    public void setColor(STimeSegment timeSegment, int[] rgb)
    {
        if (rgb.length != 3)
        {
            throw new IllegalArgumentException( "Length of RGB array has to be 3" );
        }
        setColor( timeSegment, rgb[0], rgb[1], rgb[2] );
    }

    @Override
    public boolean plantUmlToSVG(String plantUmlText, String pathToImage)
    {
        return plantUmlRendererService.plantumlToSVG( plantUmlText, pathToImage );
    }

    @Override
    public boolean plantUmlToPNG(String plantUmlText, String pathToImage)
    {
        return plantUmlRendererService.plantumlToPNG( plantUmlText, pathToImage );
    }

    @Override
    public String getParameter(String key)
    {
        try
        {
            return commandlineParser.getValue( key );
        }
        catch (ValueOfArgumentMissingException e)
        {
            return null;
        }
    }

    @Override
    public boolean hasParameter(String key)
    {
        return commandlineParser.hasArg( key );
    }

    @Override
    public <T> RuntimeEventChannel<T> createOrGetChannel(String name, Unit<T> unit, String description,
            List<String> valueColumns)
    {
        return runtimeEventAcceptor.createOrGetRuntimeEventChannel( name, unit, description, valueColumns );
    }

    @Override
    public SystemModel initSystemModelFromFile(String path) throws FileNotFoundException
    {
        return systemModel.initFromFile( path );
    }

    @Override
    public String getInputModelRepresentation(SystemModel model, ViewModelGenerator generator)
    {
        return systemModel.generate( model, generator );
    }

    @Override
    public SystemModelAccess addSystemModelChangedListener(SystemModelChangedListener listener)
    {
        systemModel.addSystemModelChangedListener( listener );
        return systemModel;
    }
}
