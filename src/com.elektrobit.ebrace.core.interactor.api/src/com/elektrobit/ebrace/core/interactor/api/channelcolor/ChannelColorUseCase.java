/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.api.channelcolor;

import com.elektrobit.ebrace.core.interactor.api.common.BaseUseCase;
import com.elektrobit.ebsolys.core.targetdata.api.color.SColor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public interface ChannelColorUseCase extends BaseUseCase
{
    @Deprecated
    public void setColorForChannel(String channelName, int r, int g, int b);

    public void setColorForChannel(RuntimeEventChannel<?> channel, int r, int g, int b);

    @Deprecated
    public SColor getColorOfChannel(String channelName);

    public SColor getColorOfChannel(RuntimeEventChannel<?> channel);

    public boolean channelHasColor(RuntimeEventChannel<?> channel);
}
