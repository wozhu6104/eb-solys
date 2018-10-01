/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.impl.adapter.json;

import java.util.List;

import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elektrobit.ebsolys.core.targetdata.api.adapter.BaseTargetAdapterFactory;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.TargetAdaptorFactory;
import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelationAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.importer.JsonEventHandler;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.structure.StructureAcceptor;

@Component(service = TargetAdaptorFactory.class, property = BaseTargetAdapterFactory.MESSAGE_TYPE_PROPERTY
        + "MSG_TYPE_JSONAPI_PLUGIN")
public class JsonAPIAdapterFactory extends BaseTargetAdapterFactory<JsonAPIAdapter>
{
    private RuntimeEventAcceptor runtimeEventAcceptor;
    private StructureAcceptor structureAcceptor;
    private ComRelationAcceptor comRelationAcceptor;
    private JsonEventHandler jsonEventHandler;

    @Override
    protected JsonAPIAdapter createNewAdapterInstance(DataSourceContext dataSourceContext)
    {
        return new JsonAPIAdapter( runtimeEventAcceptor,
                                   structureAcceptor,
                                   comRelationAcceptor,
                                   jsonEventHandler,
                                   dataSourceContext );
    }

    @Override
    protected void registerInstanceForRequiredServices(JsonAPIAdapter adaptor,
            List<ServiceRegistration<?>> registrations)
    {
    }

    @Reference
    public void bindRuntimeEventAcceptor(RuntimeEventAcceptor runtimeEventAcceptor)
    {
        this.runtimeEventAcceptor = runtimeEventAcceptor;
    }

    public void unbindRuntimeEventAcceptor(RuntimeEventAcceptor runtimeEventAcceptor)
    {
        this.runtimeEventAcceptor = null;
    }

    @Reference
    public void bindStructureAcceptor(StructureAcceptor structureAcceptor)
    {
        this.structureAcceptor = structureAcceptor;
    }

    public void unbindStructureAcceptor(StructureAcceptor structureAcceptor)
    {
        this.structureAcceptor = null;
    }

    @Reference
    public void bindComRelationAcceptor(ComRelationAcceptor comRelationAcceptor)
    {
        this.comRelationAcceptor = comRelationAcceptor;
    }

    public void unbindComRelationAcceptor(ComRelationAcceptor comRelationAcceptor)
    {
        this.comRelationAcceptor = null;
    }

    @Reference
    public void bindJsonEventHandler(JsonEventHandler jsonEventHandler)
    {
        this.jsonEventHandler = jsonEventHandler;
    }

    public void unbindJsonEventHandler(JsonEventHandler jsonEventHandler)
    {
        this.jsonEventHandler = jsonEventHandler;
    }
}
