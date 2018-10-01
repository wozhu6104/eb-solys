/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebsolys.core.targetdata.api.adapter;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.ServiceRegistration;

public abstract class BaseTargetAdapterFactory<T extends TargetAdapter> implements TargetAdaptorFactory
{
    public static final String MESSAGE_TYPE_PROPERTY = "MessageType:String=";

    @Override
    public DynamicTargetAdaptorResult createNewInstance(DataSourceContext context)
    {
        T adaptor = createNewAdapterInstance( context );

        List<ServiceRegistration<?>> registrations = new ArrayList<ServiceRegistration<?>>();
        registerInstanceForRequiredServices( adaptor, registrations );
        return new DynamicTargetAdaptorResult( adaptor, registrations );
    }

    /**
     * Create new instance of you adaptor. Adapter should use dataSourceContext when creating or getting a
     * RuntimeEventChannel
     * 
     * @param dataSourceContext
     * @return Newly create adaptor
     */
    protected abstract T createNewAdapterInstance(DataSourceContext dataSourceContext);

    /**
     * Implementor should register {@code adaptor} for OSGi interfaces that it requires and add these references to
     * {@code registrations} list.
     * 
     * @param adaptor
     * @param registrations
     * @return
     */
    protected abstract void registerInstanceForRequiredServices(T adaptor, List<ServiceRegistration<?>> registrations);
}
