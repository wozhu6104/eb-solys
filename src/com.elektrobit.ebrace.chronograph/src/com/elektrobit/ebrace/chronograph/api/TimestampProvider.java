/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.chronograph.api;

import java.util.Collection;

import com.elektrobit.ebsolys.core.targetdata.api.TimestampCreator;

public interface TimestampProvider
{
    public void registerTargetTimebase(String name, long absoluteTargetTimeAtRegistration);

    public TimestampCreator getHostTimestampCreator();

    public TimestampCreator getTargetTimestampCreator(String name);

    public Collection<String> getTargetTimebaseNames();
}
