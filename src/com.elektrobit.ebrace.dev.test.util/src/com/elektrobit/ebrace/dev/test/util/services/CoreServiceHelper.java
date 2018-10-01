/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.dev.test.util.services;

import com.elektrobit.ebrace.chronograph.api.TimestampProvider;
import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.core.importerregistry.api.ImporterRegistry;
import com.elektrobit.ebrace.core.preferences.api.PreferencesService;
import com.elektrobit.ebrace.core.preferences.api.UserInteractionPreferences;
import com.elektrobit.ebrace.core.racescriptexecutor.api.RaceScriptLoader;
import com.elektrobit.ebrace.core.racescriptexecutor.api.ScriptExecutorService;
import com.elektrobit.ebrace.core.usermessagelogger.api.UserMessageLogger;
import com.elektrobit.ebrace.targetagent.protocol.commondefinitions.TargetAgentProtocolCommonDefinitions;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.TargetAdaptorFactory;
import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelationAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelationProvider;
import com.elektrobit.ebsolys.core.targetdata.api.reset.ResetNotifier;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventProvider;
import com.elektrobit.ebsolys.core.targetdata.api.structure.StructureAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.structure.StructureProvider;

public class CoreServiceHelper
{
    private static final AdaptorFactoryServiceFetcher TARGET_ADAPTOR_FACTORY_SERVICE_FETCHER = new AdaptorFactoryServiceFetcher();

    public static RuntimeEventProvider getRuntimeEventProvider()
    {
        return new GenericOSGIServiceTracker<RuntimeEventProvider>( RuntimeEventProvider.class ).getService();
    }

    public static RuntimeEventAcceptor getRuntimeEventAcceptor()
    {
        return new GenericOSGIServiceTracker<RuntimeEventAcceptor>( RuntimeEventAcceptor.class ).getService();
    }

    public static StructureProvider getStructureProvider()
    {
        return new GenericOSGIServiceTracker<StructureProvider>( StructureProvider.class ).getService();
    }

    public static StructureAcceptor getStructureAcceptor()
    {
        return new GenericOSGIServiceTracker<StructureAcceptor>( StructureAcceptor.class ).getService();
    }

    public static ComRelationProvider getComRelationProvider()
    {
        return new GenericOSGIServiceTracker<ComRelationProvider>( ComRelationProvider.class ).getService();
    }

    public static ComRelationAcceptor getComRelationAcceptor()
    {
        return new GenericOSGIServiceTracker<ComRelationAcceptor>( ComRelationAcceptor.class ).getService();
    }

    public static ResetNotifier getResetNotifier()
    {
        return new GenericOSGIServiceTracker<ResetNotifier>( ResetNotifier.class ).getService();
    }

    public static UserMessageLogger getUserMessageLogger()
    {
        return new GenericOSGIServiceTracker<UserMessageLogger>( UserMessageLogger.class ).getService();
    }

    public static TargetAdaptorFactory getDltMonitorControllerFactory()
    {
        return TARGET_ADAPTOR_FACTORY_SERVICE_FETCHER
                .getServiceForMessageType( TargetAgentProtocolCommonDefinitions.MessageType.MSG_TYPE_GENIVI_DLT_MONITOR_PLUGIN
                        .name() );
    }

    public static TargetAdaptorFactory getDBusMonitorControllerFactory()
    {
        return TARGET_ADAPTOR_FACTORY_SERVICE_FETCHER
                .getServiceForMessageType( TargetAgentProtocolCommonDefinitions.MessageType.MSG_TYPE_DBUS.name() );
    }

    public static TargetAdaptorFactory getResourceMonitorControllerFactory()
    {
        return TARGET_ADAPTOR_FACTORY_SERVICE_FETCHER
                .getServiceForMessageType( TargetAgentProtocolCommonDefinitions.MessageType.MSG_TYPE_RESOURCE_MONITOR
                        .name() );
    }

    public static TargetAdaptorFactory getVMostControllerFactory()
    {
        return TARGET_ADAPTOR_FACTORY_SERVICE_FETCHER
                .getServiceForMessageType( TargetAgentProtocolCommonDefinitions.MessageType.MSG_TYPE_MOST_SPY_MONITOR_PLUGIN
                        .name() );
    }

    public static TargetAdaptorFactory getEvmControllerFactory()
    {
        return TARGET_ADAPTOR_FACTORY_SERVICE_FETCHER
                .getServiceForMessageType( TargetAgentProtocolCommonDefinitions.MessageType.MSG_TYPE_EVM_PLUGIN
                        .name() );
    }

    public static TargetAdaptorFactory getSimpleStringControllerFactory()
    {
        return TARGET_ADAPTOR_FACTORY_SERVICE_FETCHER
                .getServiceForMessageType( TargetAgentProtocolCommonDefinitions.MessageType.MSG_TYPE_SIMPLE_STRING_PLUGIN
                        .name() );
    }

    public static TargetAdaptorFactory getSingletonGatewayControllerFactory()
    {
        return TARGET_ADAPTOR_FACTORY_SERVICE_FETCHER
                .getServiceForMessageType( TargetAgentProtocolCommonDefinitions.MessageType.MSG_TYPE_SINGLETON_GATEWAY_PLUGIN
                        .name() );
    }

    public static TargetAdaptorFactory getWmControllerFactory()
    {
        return TARGET_ADAPTOR_FACTORY_SERVICE_FETCHER
                .getServiceForMessageType( TargetAgentProtocolCommonDefinitions.MessageType.MSG_TYPE_WM_PLUGIN.name() );
    }

    public static ImporterRegistry getImporterRegistry()
    {
        return new GenericOSGIServiceTracker<ImporterRegistry>( ImporterRegistry.class ).getService();
    }

    public static UserInteractionPreferences getUserInteractionPerferences()
    {
        return new GenericOSGIServiceTracker<UserInteractionPreferences>( UserInteractionPreferences.class )
                .getService();
    }

    public static TimestampProvider getTimestampProvider()
    {
        return new GenericOSGIServiceTracker<TimestampProvider>( TimestampProvider.class ).getService();
    }

    public static PreferencesService getPreferencesService()
    {
        return new GenericOSGIServiceTracker<PreferencesService>( PreferencesService.class ).getService();
    }

    public static RaceScriptLoader getRaceScriptLoader()
    {
        return new GenericOSGIServiceTracker<RaceScriptLoader>( RaceScriptLoader.class ).getService();
    }

    public static ScriptExecutorService getScriptExecutorService()
    {
        return new GenericOSGIServiceTracker<ScriptExecutorService>( ScriptExecutorService.class ).getService();
    }
}
