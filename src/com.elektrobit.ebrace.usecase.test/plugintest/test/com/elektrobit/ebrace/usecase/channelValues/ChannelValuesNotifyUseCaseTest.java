/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.usecase.channelValues;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.core.interactor.api.channelvalues.ChannelsSnapshotNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.channelvalues.ChannelsSnapshotNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.channelvalues.SortColumn;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.resources.model.snapshot.SnapshotModel;
import com.elektrobit.ebrace.core.preferences.api.UserInteractionPreferences;
import com.elektrobit.ebrace.dev.test.util.services.CoreServiceHelper;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;
import com.elektrobit.ebsolys.core.targetdata.api.ModelElement;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedRuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarkerManager;

import test.com.elektrobit.ebrace.core.interactor.UseCaseBaseTest;

@SuppressWarnings(value = {"unchecked", "rawtypes"})
public class ChannelValuesNotifyUseCaseTest extends UseCaseBaseTest
{
    private RuntimeEventAcceptor runtimeEventAcceptorService;
    private ChannelsSnapshotNotifyUseCase usecase;
    private ResourcesModelManager resourceManager;
    private UserInteractionPreferences userInteractionPreferences;
    private TimeMarkerManager timemarkerManager;
    private ChannelsSnapshotNotifyCallback callback;
    private List<RuntimeEventChannel<?>> channels;
    private RuntimeEventChannel<Double> runtimeEventChannel2;
    private RuntimeEventChannel<Double> runtimeEventChannel3;
    private RuntimeEventChannel<Double> runtimeEventChannel;

    private final List<RuntimeEventChannel<?>> keyArguments = new ArrayList<RuntimeEventChannel<?>>();
    private final List<DecodedRuntimeEvent> valueArguments = new ArrayList<DecodedRuntimeEvent>();

    @Before
    public void setup()
    {
        runtimeEventAcceptorService = CoreServiceHelper.getRuntimeEventAcceptor();
        resourceManager = new GenericOSGIServiceTracker<ResourcesModelManager>( ResourcesModelManager.class )
                .getService();
        userInteractionPreferences = CoreServiceHelper.getUserInteractionPerferences();
        timemarkerManager = new GenericOSGIServiceTracker<TimeMarkerManager>( TimeMarkerManager.class ).getService();
        callback = Mockito.mock( ChannelsSnapshotNotifyCallback.class );
        usecase = UseCaseFactoryInstance.get().makeChannelsSnapshotNotifyUseCase( callback );
    }

    @Test
    public void testDecodeTimemarker() throws Exception
    {
        userInteractionPreferences.setIsLiveMode( false );
        SnapshotModel eventMapModel = createSnapshotModel();
        usecase.register( eventMapModel );
        usecase.setSorting( SortColumn.CHANNEL_ASC );
        createSelectedTimemarker();

        ArgumentCaptor<Map> inputCaptor = ArgumentCaptor.forClass( Map.class );
        Mockito.verify( callback, Mockito.times( 2 ) ).onNewInput( inputCaptor.capture(), Mockito.anyLong() );
        List<Map> captured = inputCaptor.getAllValues();
        HashMap<RuntimeEventChannel<?>, Object> correspondingArgument = (HashMap<RuntimeEventChannel<?>, Object>)captured
                .get( 1 );

        for (Map.Entry<RuntimeEventChannel<?>, Object> entry : correspondingArgument.entrySet())
        {
            keyArguments.add( entry.getKey() );
            valueArguments.add( (DecodedRuntimeEvent)entry.getValue() );
        }

        Assert.assertEquals( runtimeEventChannel2.getName(), keyArguments.get( 0 ).getName() );
        Assert.assertEquals( runtimeEventChannel.getName(), keyArguments.get( 1 ).getName() );
        Assert.assertEquals( runtimeEventChannel3.getName(), keyArguments.get( 2 ).getName() );

        Assert.assertEquals( null, valueArguments.get( 0 ) );
        Assert.assertEquals( 20.5, valueArguments.get( 1 ).getRuntimeEventValue() );
        Assert.assertEquals( null, valueArguments.get( 2 ) );
    }

    @Test
    public void testDecodeLiveMode() throws Exception
    {
        userInteractionPreferences.setIsLiveMode( true );
        SnapshotModel eventMapModel = createSnapshotModel();
        usecase.register( eventMapModel );
        usecase.setSorting( SortColumn.CHANNEL_ASC );

        ArgumentCaptor<Map> inputCaptor = ArgumentCaptor.forClass( Map.class );
        Mockito.verify( callback ).onNewInput( inputCaptor.capture(), Mockito.anyLong() );
        List<Map> captured = inputCaptor.getAllValues();
        HashMap<RuntimeEventChannel<?>, Object> correspondingArgument = (HashMap<RuntimeEventChannel<?>, Object>)captured
                .get( 0 );

        for (Map.Entry<RuntimeEventChannel<?>, Object> entry : correspondingArgument.entrySet())
        {
            keyArguments.add( entry.getKey() );
            valueArguments.add( (DecodedRuntimeEvent)entry.getValue() );
        }

        Assert.assertEquals( runtimeEventChannel.getName(), keyArguments.get( 1 ).getName() );
        Assert.assertEquals( runtimeEventChannel2.getName(), keyArguments.get( 0 ).getName() );
        Assert.assertEquals( runtimeEventChannel3.getName(), keyArguments.get( 2 ).getName() );

        Assert.assertEquals( 100.0, valueArguments.get( 0 ).getRuntimeEventValue() );
        Assert.assertEquals( 20.5, valueArguments.get( 1 ).getRuntimeEventValue() );
        Assert.assertEquals( 2000.0, valueArguments.get( 2 ).getRuntimeEventValue() );
    }

    @Test
    public void testDecodeAnalysisMode() throws Exception
    {
        userInteractionPreferences.setIsLiveMode( false );
        SnapshotModel eventMapModel = createSnapshotModel();
        usecase.register( eventMapModel );
        usecase.setSorting( SortColumn.VALUE_ASC );

        ArgumentCaptor<Map> inputCaptor = ArgumentCaptor.forClass( Map.class );
        Mockito.verify( callback ).onNewInput( inputCaptor.capture(), Mockito.anyLong() );
        List<Map> captured = inputCaptor.getAllValues();
        HashMap<RuntimeEventChannel<?>, Object> correspondingArgument = (HashMap<RuntimeEventChannel<?>, Object>)captured
                .get( 0 );

        for (Map.Entry<RuntimeEventChannel<?>, Object> entry : correspondingArgument.entrySet())
        {
            keyArguments.add( entry.getKey() );
            valueArguments.add( (DecodedRuntimeEvent)entry.getValue() );
        }

        Assert.assertEquals( runtimeEventChannel.getName(), keyArguments.get( 0 ).getName() );
        Assert.assertEquals( runtimeEventChannel2.getName(), keyArguments.get( 1 ).getName() );
        Assert.assertEquals( runtimeEventChannel3.getName(), keyArguments.get( 2 ).getName() );

        Assert.assertEquals( null, valueArguments.get( 0 ) );
        Assert.assertEquals( null, valueArguments.get( 1 ) );
        Assert.assertEquals( null, valueArguments.get( 2 ) );
    }

    @Test
    public void testToggleSortValues() throws Exception
    {
        SnapshotModel eventMapModel = createSnapshotModel();
        usecase.register( eventMapModel );
        usecase.setSorting( SortColumn.VALUE_ASC );

        ArgumentCaptor<Map> inputCaptor = ArgumentCaptor.forClass( Map.class );
        Mockito.verify( callback ).onNewInput( inputCaptor.capture(), Mockito.anyLong() );
        List<Map> captured = inputCaptor.getAllValues();
        HashMap<RuntimeEventChannel<?>, Object> correspondingArgument = (HashMap<RuntimeEventChannel<?>, Object>)captured
                .get( 0 );

        for (Map.Entry<RuntimeEventChannel<?>, Object> entry : correspondingArgument.entrySet())
        {
            keyArguments.add( entry.getKey() );
            valueArguments.add( (DecodedRuntimeEvent)entry.getValue() );
        }

        Assert.assertEquals( runtimeEventChannel.getName(), keyArguments.get( 0 ).getName() );
        Assert.assertEquals( runtimeEventChannel2.getName(), keyArguments.get( 1 ).getName() );
        Assert.assertEquals( runtimeEventChannel3.getName(), keyArguments.get( 2 ).getName() );

        Assert.assertEquals( 20.5, valueArguments.get( 0 ).getRuntimeEventValue() );
        Assert.assertEquals( 100.0, valueArguments.get( 1 ).getRuntimeEventValue() );
        Assert.assertEquals( 2000.0, valueArguments.get( 2 ).getRuntimeEventValue() );

        usecase.setSorting( SortColumn.VALUE_DESC );
        ArgumentCaptor<Map> inputCaptorForReverse = ArgumentCaptor.forClass( Map.class );
        Mockito.verify( callback, Mockito.times( 2 ) ).onNewInput( inputCaptorForReverse.capture(), Mockito.anyLong() );
        List<Map> capturedReverse = inputCaptorForReverse.getAllValues();
        correspondingArgument = (HashMap<RuntimeEventChannel<?>, Object>)capturedReverse.get( 1 );

        keyArguments.clear();
        valueArguments.clear();

        for (Map.Entry<RuntimeEventChannel<?>, Object> entry : correspondingArgument.entrySet())
        {
            keyArguments.add( entry.getKey() );
            valueArguments.add( (DecodedRuntimeEvent)entry.getValue() );
        }

        Assert.assertEquals( runtimeEventChannel3.getName(), keyArguments.get( 0 ).getName() );
        Assert.assertEquals( runtimeEventChannel2.getName(), keyArguments.get( 1 ).getName() );
        Assert.assertEquals( runtimeEventChannel.getName(), keyArguments.get( 2 ).getName() );

        Assert.assertEquals( 2000.0, valueArguments.get( 0 ).getRuntimeEventValue() );
        Assert.assertEquals( 100.0, valueArguments.get( 1 ).getRuntimeEventValue() );
        Assert.assertEquals( 20.5, valueArguments.get( 2 ).getRuntimeEventValue() );
    }

    @Test
    public void testToggleSortChannels() throws Exception
    {
        SnapshotModel eventMapModel = createSnapshotModel();
        usecase.register( eventMapModel );
        usecase.setSorting( SortColumn.CHANNEL_ASC );

        ArgumentCaptor<Map> inputCaptor = ArgumentCaptor.forClass( Map.class );
        Mockito.verify( callback ).onNewInput( inputCaptor.capture(), Mockito.anyLong() );
        List<Map> captured = inputCaptor.getAllValues();
        HashMap<RuntimeEventChannel<?>, Object> correspondingArgument = (HashMap<RuntimeEventChannel<?>, Object>)captured
                .get( 0 );

        for (Map.Entry<RuntimeEventChannel<?>, Object> entry : correspondingArgument.entrySet())
        {
            keyArguments.add( entry.getKey() );
            valueArguments.add( (DecodedRuntimeEvent)entry.getValue() );
        }

        Assert.assertEquals( runtimeEventChannel.getName(), keyArguments.get( 1 ).getName() );
        Assert.assertEquals( runtimeEventChannel2.getName(), keyArguments.get( 0 ).getName() );
        Assert.assertEquals( runtimeEventChannel3.getName(), keyArguments.get( 2 ).getName() );

        Assert.assertEquals( 100.0, valueArguments.get( 0 ).getRuntimeEventValue() );
        Assert.assertEquals( 20.5, valueArguments.get( 1 ).getRuntimeEventValue() );
        Assert.assertEquals( 2000.0, valueArguments.get( 2 ).getRuntimeEventValue() );

        usecase.setSorting( SortColumn.CHANNEL_DESC );
        ArgumentCaptor<Map> inputCaptorReverse = ArgumentCaptor.forClass( Map.class );
        Mockito.verify( callback, Mockito.times( 2 ) ).onNewInput( inputCaptorReverse.capture(), Mockito.anyLong() );
        List<Map> capturedReverse = inputCaptorReverse.getAllValues();
        correspondingArgument = (HashMap<RuntimeEventChannel<?>, Object>)capturedReverse.get( 1 );

        keyArguments.clear();
        valueArguments.clear();

        for (Map.Entry<RuntimeEventChannel<?>, Object> entry : correspondingArgument.entrySet())
        {
            keyArguments.add( entry.getKey() );
            valueArguments.add( (DecodedRuntimeEvent)entry.getValue() );
        }

        Assert.assertEquals( runtimeEventChannel3.getName(), keyArguments.get( 0 ).getName() );
        Assert.assertEquals( runtimeEventChannel.getName(), keyArguments.get( 1 ).getName() );
        Assert.assertEquals( runtimeEventChannel2.getName(), keyArguments.get( 2 ).getName() );

        Assert.assertEquals( 100.0, valueArguments.get( 2 ).getRuntimeEventValue() );
        Assert.assertEquals( 20.5, valueArguments.get( 1 ).getRuntimeEventValue() );
        Assert.assertEquals( 2000.0, valueArguments.get( 0 ).getRuntimeEventValue() );
    }

    private SnapshotModel createSnapshotModel()
    {
        SnapshotModel snapshotModel = resourceManager.createSnapshot( "Snapshot" );
        snapshotModel.setChannels( createDoubleTypeChannels() );
        return snapshotModel;
    }

    private List<RuntimeEventChannel<?>> createDoubleTypeChannels()
    {
        channels = new ArrayList<RuntimeEventChannel<?>>();

        runtimeEventChannel = runtimeEventAcceptorService.createRuntimeEventChannel( "D", Unit.PERCENT, "Description" );
        runtimeEventAcceptorService.acceptEventMicros( 900, runtimeEventChannel, ModelElement.NULL_MODEL_ELEMENT, 0.0 );
        runtimeEventAcceptorService.acceptEventMicros( 1000,
                                                       runtimeEventChannel,
                                                       ModelElement.NULL_MODEL_ELEMENT,
                                                       20.5 );
        channels.add( runtimeEventChannel );

        runtimeEventChannel2 = runtimeEventAcceptorService.createRuntimeEventChannel( "A",
                                                                                      Unit.PERCENT,
                                                                                      "Description" );
        runtimeEventAcceptorService.acceptEventMicros( 10, runtimeEventChannel2, null, 100.0 );
        channels.add( runtimeEventChannel2 );

        runtimeEventChannel3 = runtimeEventAcceptorService.createRuntimeEventChannel( "G",
                                                                                      Unit.PERCENT,
                                                                                      "Description" );
        runtimeEventAcceptorService.acceptEventMicros( 50, runtimeEventChannel3, null, 2000.0 );
        channels.add( runtimeEventChannel3 );
        return channels;
    }

    private void createSelectedTimemarker()
    {
        TimeMarker tm = timemarkerManager.createNewTimeMarker( 1000 );
        timemarkerManager.setCurrentSelectedTimeMarker( tm );
    }

    @After
    public void cleanUp()
    {
        resourceManager.deleteResourcesModels( resourceManager.getResources() );
        runtimeEventAcceptorService.dispose();
        timemarkerManager.setCurrentSelectedTimeMarker( null );
        usecase.unregister();
        userInteractionPreferences.setIsLiveMode( true );
        callback = null;
        keyArguments.clear();
        valueArguments.clear();
    }
}
