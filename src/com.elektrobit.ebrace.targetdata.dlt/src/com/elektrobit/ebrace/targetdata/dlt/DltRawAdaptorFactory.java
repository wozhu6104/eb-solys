/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.dlt;

import java.util.List;

import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elektrobit.ebrace.targetdata.dlt.internal.connection.DltChannelFromLogInfoCreator;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.BaseTargetAdapterFactory;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.TargetAdaptorFactory;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;

@Component(service = TargetAdaptorFactory.class, property = BaseTargetAdapterFactory.MESSAGE_TYPE_PROPERTY
        + "MSG_TYPE_DLT_RAW")
public class DltRawAdaptorFactory extends BaseTargetAdapterFactory<DltRawAdaptor>
{
    private RuntimeEventAcceptor runtimeEventAcceptor;
    private DltChannelFromLogInfoCreator dltChannelFromLogInfoCreator;

    @Override
    protected DltRawAdaptor createNewAdapterInstance(DataSourceContext dataSourceContext)
    {
        DltRawAdaptor dltMonitorAdaptor = new DltRawAdaptor( dltChannelFromLogInfoCreator,
                                                             runtimeEventAcceptor,
                                                             dataSourceContext );
        return dltMonitorAdaptor;
    }

    @Override
    protected void registerInstanceForRequiredServices(DltRawAdaptor adaptor,
            List<ServiceRegistration<?>> registrations)
    {
    }

    @Reference
    public void bind(RuntimeEventAcceptor runtimeEventAcceptor)
    {
        this.runtimeEventAcceptor = runtimeEventAcceptor;
    }

    public void unbind(RuntimeEventAcceptor runtimeEventAcceptor)
    {
        this.runtimeEventAcceptor = null;
    }

    @Reference
    public void bindDltChannelFromLogInfoCreator(DltChannelFromLogInfoCreator dltChannelFromLogInfoCreator)
    {
        this.dltChannelFromLogInfoCreator = dltChannelFromLogInfoCreator;
    }

    public void unbindDltChannelFromLogInfoCreator(DltChannelFromLogInfoCreator dltChannelFromLogInfoCreator)
    {
        this.dltChannelFromLogInfoCreator = null;
    }
}
