/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebsolys.decoder.common.api;

import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.decoder.common.services.DecoderService;

public interface DecoderServiceManager
{
    public DecoderService getDecoderServiceForEvent(RuntimeEvent<?> event);

    public DecoderService getDecoderForClassName(String className);
}
