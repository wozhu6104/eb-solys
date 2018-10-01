/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor;

import java.util.List;

import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.api.WriteProcessRegistryIF;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.BaseTargetAdapterFactory;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.TargetAdaptorFactory;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.structure.StructureAcceptor;

@Component(service = TargetAdaptorFactory.class, property = BaseTargetAdapterFactory.MESSAGE_TYPE_PROPERTY
        + "MSG_TYPE_RESOURCE_MONITOR")
public class ResourceMonitorControllerFactory extends BaseTargetAdapterFactory<ResourceMonitorController>
{

    private WriteProcessRegistryIF processRegistry;
    private StructureAcceptor structureAcceptor;
    private RuntimeEventAcceptor runtimeEventAcceptor;

    @Override
    protected ResourceMonitorController createNewAdapterInstance(DataSourceContext dataSourceContext)
    {
        ResourceMonitorController resourceMonitorController = new ResourceMonitorController( processRegistry,
                                                                                             structureAcceptor,
                                                                                             runtimeEventAcceptor,
                                                                                             dataSourceContext );
        return resourceMonitorController;
    }

    @Override
    protected void registerInstanceForRequiredServices(ResourceMonitorController adaptor,
            List<ServiceRegistration<?>> registrations)
    {
    }

    @Reference
    public void bindWriteProcessRegistryIF(final WriteProcessRegistryIF writeProcessRegistryIF)
    {
        this.processRegistry = writeProcessRegistryIF;
    }

    public void unbindWriteProcessRegistryIF(final WriteProcessRegistryIF writeProcessRegistryIF)
    {
        this.processRegistry = null;
    }

    @Reference
    public void bindStructureAcceptor(final StructureAcceptor structureAcceptor)
    {
        this.structureAcceptor = structureAcceptor;
    }

    public void unbindStructureAcceptor(final StructureAcceptor structureAcceptor)
    {
        this.structureAcceptor = null;
    }

    @Reference
    public void bindRuntimeEventAcceptor(final RuntimeEventAcceptor runtimeEventAcceptor)
    {
        this.runtimeEventAcceptor = runtimeEventAcceptor;
    }

    public void unbindRuntimeEventAcceptor(final RuntimeEventAcceptor runtimeEventAcceptor)
    {
        this.runtimeEventAcceptor = null;
    }
}
