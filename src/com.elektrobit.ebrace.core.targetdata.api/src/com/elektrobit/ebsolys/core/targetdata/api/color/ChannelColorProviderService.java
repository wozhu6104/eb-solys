/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebsolys.core.targetdata.api.color;

import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public interface ChannelColorProviderService
{
    public void registerColorChannelListener(ColorChannelListener listener);

    public void unregisterColorChannelListener(ColorChannelListener listener);

    public void setColorForChannel(RuntimeEventChannel<?> channel, int r, int g, int b);

    public SColor getColorForChannel(RuntimeEventChannel<?> channel);

    public SColor createAndGetColorForChannel(RuntimeEventChannel<?> channel);

    public boolean hasColor(RuntimeEventChannel<?> channel);
}
