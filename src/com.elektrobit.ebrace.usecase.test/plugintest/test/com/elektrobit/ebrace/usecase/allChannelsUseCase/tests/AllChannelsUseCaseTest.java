/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.usecase.allChannelsUseCase.tests;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.core.interactor.api.allChannels.AllChannelsNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.allChannels.AllChannelsNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.dev.test.util.services.CoreServiceHelper;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

import test.com.elektrobit.ebrace.core.interactor.UseCaseBaseTest;

public class AllChannelsUseCaseTest extends UseCaseBaseTest implements AllChannelsNotifyCallback
{

    private final RuntimeEventAcceptor runtimeEventAcceptorService = CoreServiceHelper.getRuntimeEventAcceptor();
    private final List<RuntimeEventChannel<?>> expectedRuntimeEventChannels = new ArrayList<RuntimeEventChannel<?>>();
    private List<RuntimeEventChannel<?>> resultRuntimeEventChannels;

    @Before
    public void resetRuntimeEventAcceptor()
    {
        runtimeEventAcceptorService.dispose();
    }

    private void createChannels()
    {
        RuntimeEventChannel<Double> runtimeEventChannel = runtimeEventAcceptorService
                .createRuntimeEventChannel( "1", Unit.PERCENT, "Description" );
        expectedRuntimeEventChannels.add( runtimeEventChannel );
        runtimeEventChannel = runtimeEventAcceptorService.createRuntimeEventChannel( "2", Unit.PERCENT, "Description" );
        expectedRuntimeEventChannels.add( runtimeEventChannel );
        runtimeEventChannel = runtimeEventAcceptorService.createRuntimeEventChannel( "3", Unit.PERCENT, "Description" );
        expectedRuntimeEventChannels.add( runtimeEventChannel );
    }

    @Test
    public void testRuntimeChannelAddedAfterUseCaseCreated() throws Exception
    {
        UseCaseFactoryInstance.get().makeAllChannelsNotifyUseCase( this );
        createChannels();

        Assert.assertEquals( expectedRuntimeEventChannels, resultRuntimeEventChannels );
    }

    @Test
    public void testRuntimeChannelAddedBeforeUseCaseCreated() throws Exception
    {
        createChannels();
        UseCaseFactoryInstance.get().makeAllChannelsNotifyUseCase( this );

        Assert.assertEquals( expectedRuntimeEventChannels, resultRuntimeEventChannels );
    }

    @Test
    public void testUnregister() throws Exception
    {
        AllChannelsNotifyUseCase allChannelsNotifyUseCase = UseCaseFactoryInstance.get()
                .makeAllChannelsNotifyUseCase( this );
        allChannelsNotifyUseCase.unregister();

        this.resultRuntimeEventChannels = null;
        createChannels();

        Assert.assertNull( "We shouldn't get a runtime event channels after we called unregister.",
                           resultRuntimeEventChannels );
    }

    @Override
    public void onAllChannelsChanged(List<RuntimeEventChannel<?>> allChannels)
    {
        this.resultRuntimeEventChannels = allChannels;
    }

    @Override
    public void onChannelRemoved(RuntimeEventChannel<?> deletedChannel)
    {
        // TODO Auto-generated method stub

    }

}
