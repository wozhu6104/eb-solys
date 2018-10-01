/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.adapter.log4jtargetadapter.internal;

import java.util.List;

import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elektrobit.ebrace.chronograph.api.TimestampProvider;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.BaseTargetAdapterFactory;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.TargetAdaptorFactory;
import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelationAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;

@Component(service = TargetAdaptorFactory.class, property = BaseTargetAdapterFactory.MESSAGE_TYPE_PROPERTY
        + "MSG_TYPE_LOG4J_PLUGIN")
public class Log4jTargetAdapterFactory extends BaseTargetAdapterFactory<Log4jTargetAdapter>
{
    private RuntimeEventAcceptor runtimeEventAcceptor;
    private ComRelationAcceptor comRelationAcceptor;
    private TimestampProvider tsProvider;

    @Override
    protected Log4jTargetAdapter createNewAdapterInstance(DataSourceContext dataSourceContext)
    {
        return new Log4jTargetAdapter( runtimeEventAcceptor,
                                              tsProvider,
                                              comRelationAcceptor,
                                              dataSourceContext );
    }

    @Override
    protected void registerInstanceForRequiredServices(Log4jTargetAdapter adaptor,
            List<ServiceRegistration<?>> registrations)
    {
    }

    @Reference
    public void bindTimestampProvider(TimestampProvider tsProvider)
    {
        this.tsProvider = tsProvider;
    }

    public void unbindTimestampProvider(TimestampProvider tsProvider)
    {
        this.tsProvider = null;
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
    public void bindComRelationAcceptor(ComRelationAcceptor comRelationAcceptor)
    {
        this.comRelationAcceptor = comRelationAcceptor;
    }

    public void unbindComRelationAcceptor(ComRelationAcceptor comRelationAcceptor)
    {
        this.comRelationAcceptor = null;
    }

}
