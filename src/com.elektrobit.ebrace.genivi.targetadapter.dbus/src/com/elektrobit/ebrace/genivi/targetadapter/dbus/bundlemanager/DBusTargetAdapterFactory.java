/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.genivi.targetadapter.dbus.bundlemanager;

import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.api.ProcessInfoChangedListenerIF;
import com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.api.ReadProcessRegistryIF;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.BaseTargetAdapterFactory;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.TargetAdaptorFactory;
import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelationAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.structure.StructureAcceptor;

@Component(service = TargetAdaptorFactory.class, property = BaseTargetAdapterFactory.MESSAGE_TYPE_PROPERTY
        + "MSG_TYPE_DBUS")
public class DBusTargetAdapterFactory extends BaseTargetAdapterFactory<DBusTargetAdapterController>
{
    private StructureAcceptor structureAcceptor;
    private ComRelationAcceptor comRelationAcceptor;
    private RuntimeEventAcceptor runtimeEventAcceptor;
    private ReadProcessRegistryIF processRegistry;

    @Override
    protected DBusTargetAdapterController createNewAdapterInstance(DataSourceContext dataSourceContext)
    {
        DBusTargetAdapterController adaptor = new DBusTargetAdapterController( structureAcceptor,
                                                                               comRelationAcceptor,
                                                                               runtimeEventAcceptor,
                                                                               processRegistry,
                                                                               dataSourceContext );
        return adaptor;
    }

    @Override
    protected void registerInstanceForRequiredServices(DBusTargetAdapterController adaptor,
            List<ServiceRegistration<?>> registrations)
    {
        BundleContext bundleContext = FrameworkUtil.getBundle( this.getClass() ).getBundleContext();

        ServiceRegistration<ProcessInfoChangedListenerIF> serviceRegistration = bundleContext
                .registerService( ProcessInfoChangedListenerIF.class, adaptor, null );
        registrations.add( serviceRegistration );
    }

    @Reference
    protected void setStructureAcceptor(StructureAcceptor structureAcceptor)
    {
        this.structureAcceptor = structureAcceptor;
    }

    protected void unsetStructureAcceptor(StructureAcceptor structureAcceptor)
    {
        this.structureAcceptor = null;
    }

    @Reference
    protected void setComRelationAcceptor(ComRelationAcceptor comRelationAcceptor)
    {
        this.comRelationAcceptor = comRelationAcceptor;
    }

    protected void unsetComRelationAcceptor(ComRelationAcceptor structureAcceptor)
    {
        this.comRelationAcceptor = null;
    }

    @Reference
    protected void setRuntimeEventAcceptor(RuntimeEventAcceptor runtimeEventAcceptor)
    {
        this.runtimeEventAcceptor = runtimeEventAcceptor;
    }

    protected void unsetRuntimeEventAcceptor(RuntimeEventAcceptor runtimeEventAcceptor)
    {
        this.runtimeEventAcceptor = null;
    }

    @Reference
    protected void setReadProcessRegistryIF(ReadProcessRegistryIF readProcessRegistryIF)
    {
        this.processRegistry = readProcessRegistryIF;
    }

    protected void unsetReadProcessRegistryIF(ReadProcessRegistryIF readProcessRegistryIF)
    {
        this.processRegistry = null;
    }
}
