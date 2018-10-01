/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.adapter.topresourcemonitor;

import java.util.List;

import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elektrobit.ebsolys.core.targetdata.api.adapter.BaseTargetAdapterFactory;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.TargetAdaptorFactory;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;

@Component(service = TargetAdaptorFactory.class, property = BaseTargetAdapterFactory.MESSAGE_TYPE_PROPERTY
        + "MSG_TYPE_TOP_RESOURCE_MONITOR_PLUGIN")
public class TopResourceMonitorControllerFactory extends BaseTargetAdapterFactory<TopResourceMonitorController>
{

    private RuntimeEventAcceptor runtimeEventAcceptor;

    @Override
    protected TopResourceMonitorController createNewAdapterInstance(DataSourceContext dataSourceContext)
    {
        TopResourceMonitorController TopResourceMonitorController = new TopResourceMonitorController( runtimeEventAcceptor,
                                                                                                      dataSourceContext );
        return TopResourceMonitorController;
    }

    @Override
    protected void registerInstanceForRequiredServices(TopResourceMonitorController adaptor,
            List<ServiceRegistration<?>> registrations)
    {
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
