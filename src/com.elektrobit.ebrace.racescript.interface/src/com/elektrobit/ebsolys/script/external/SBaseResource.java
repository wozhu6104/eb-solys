/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebsolys.script.external;

import java.util.List;

import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public interface SBaseResource<T>
{
    public T setName(String newName);

    public T add(RuntimeEventChannel<?> channel);

    public T add(List<RuntimeEventChannel<?>> channels);

    public T remove(RuntimeEventChannel<?> channel);

    public T remove(List<RuntimeEventChannel<?>> channels);

    public T clear();

    public void delete();
}
