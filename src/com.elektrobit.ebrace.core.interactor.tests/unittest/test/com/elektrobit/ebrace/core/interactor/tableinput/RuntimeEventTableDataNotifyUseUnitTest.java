/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.interactor.tableinput;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.ServiceRegistration;

import com.elektrobit.ebrace.core.interactor.api.resources.model.table.TableModel;
import com.elektrobit.ebrace.core.interactor.api.table.RowFormatter;
import com.elektrobit.ebrace.core.interactor.api.tableinput.RuntimeEventTableDataNotifyCallback;
import com.elektrobit.ebrace.core.interactor.tableinput.RuntimeEventTableDataNotifyUseCaseImpl;
import com.elektrobit.ebrace.core.preferences.api.AnalysisTimespanPreferences;
import com.elektrobit.ebrace.core.preferences.api.UserInteractionPreferences;
import com.elektrobit.ebrace.core.racescriptexecutor.api.ScriptExecutorService;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventProvider;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarkerManager;

import test.com.elektrobit.ebrace.core.interactor.UseCaseBaseTest;

public class RuntimeEventTableDataNotifyUseUnitTest extends UseCaseBaseTest
{
    RuntimeEventTableDataNotifyUseCaseImpl sut;

    RuntimeEventTableDataNotifyCallback mockedTableDataNotifyCallback;
    List<RowFormatter> mockedRowFormatters = Collections.emptyList();
    RuntimeEventProvider mockedRuntimeEventProvider;
    AnalysisTimespanPreferences mockedAnalysisTimespanPreferences;
    TimeMarkerManager mockedTimeMarkerManager;
    ResourcesModelManager mockedResourcesModelManager;
    UserInteractionPreferences mockedUserInteractionPreferences;
    ServiceRegistration<?> mockedServiceRegistration;
    TableModel mockedTableModel;

    List<RuntimeEventChannel<?>> channels;

    private ScriptExecutorService mockedScriptExecutorService;

    @Before
    public void setUp()
    {
        mockedTableDataNotifyCallback = mock( RuntimeEventTableDataNotifyCallback.class );
        mockedRuntimeEventProvider = mock( RuntimeEventProvider.class );
        mockedAnalysisTimespanPreferences = mock( AnalysisTimespanPreferences.class );
        mockedTimeMarkerManager = mock( TimeMarkerManager.class );
        mockedResourcesModelManager = mock( ResourcesModelManager.class );
        mockedUserInteractionPreferences = mock( UserInteractionPreferences.class );
        mockedScriptExecutorService = mock( ScriptExecutorService.class );
        mockedServiceRegistration = mock( ServiceRegistration.class );
        mockedTableModel = mock( TableModel.class );
        sut = new RuntimeEventTableDataNotifyUseCaseImpl( mockedTableDataNotifyCallback,
                                                          mockedRowFormatters,
                                                          mockedRuntimeEventProvider,
                                                          mockedAnalysisTimespanPreferences,
                                                          mockedResourcesModelManager,
                                                          mockedTableModel,
                                                          mockedUserInteractionPreferences,
                                                          mockedTimeMarkerManager,
                                                          mockedScriptExecutorService );

        channels = new ArrayList<RuntimeEventChannel<?>>();
        when( mockedTableModel.getChannels() ).thenReturn( channels );
    }

    @Test
    public void verifyConstructorRegistersAllListeners()
    {
        verify( mockedResourcesModelManager ).registerResourceListener( sut );
        verify( mockedTimeMarkerManager ).registerListener( sut );
        // TODO: switch to 1 when we switch back to callback. See EBRACE-2810
        verify( mockedRuntimeEventProvider, times( 0 ) ).registerListener( sut, null );
    }

    @Test
    public void verifyUnregisterUnregistersAllListeners()
    {
        sut.setServiceRegistration( mockedServiceRegistration );
        sut.unregister();
        verify( mockedResourcesModelManager ).unregisterResourceListener( sut );
        verify( mockedTimeMarkerManager ).unregisterListener( sut );
        // TODO: switch to 1 when we switch back to callback. See EBRACE-2810
        verify( mockedRuntimeEventProvider, times( 0 ) ).unregisterListener( sut );
    }

    @Test
    public void verifyOnChannelChangeRuntimeEventProviderCallsRegister()
    {
        sut.onResourceModelChannelsChanged( mockedTableModel );
        // TODO: switch to 1 when we switch back to callback. See EBRACE-2810
        verify( mockedRuntimeEventProvider, times( 0 ) ).registerListener( sut, channels );
    }

}
