/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.genivi.targetadapter.systemdstartupmonitor;

import java.util.List;

import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elektrobit.ebsolys.core.targetdata.api.adapter.BaseTargetAdapterFactory;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.TargetAdaptorFactory;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;

@Component(service = TargetAdaptorFactory.class, property = BaseTargetAdapterFactory.MESSAGE_TYPE_PROPERTY
        + "MSG_TYPE_GENIVI_SYSTEMD_START_UP_MONITOR")
public class SystemdMessageAdapterFactory extends BaseTargetAdapterFactory<SystemdMessageAdapter>
{

    private RuntimeEventAcceptor runtimeEventAcceptorService;

    @Override
    protected SystemdMessageAdapter createNewAdapterInstance(DataSourceContext dataSourceContext)
    {

        return new SystemdMessageAdapter( runtimeEventAcceptorService, dataSourceContext );
    }

    @Override
    protected void registerInstanceForRequiredServices(SystemdMessageAdapter adaptor,
            List<ServiceRegistration<?>> registrations)
    {
    }

    @Reference
    public void bind(RuntimeEventAcceptor service)
    {
        runtimeEventAcceptorService = service;
    }

    public void unbind(RuntimeEventAcceptor service)
    {
        runtimeEventAcceptorService = null;
    }

}
