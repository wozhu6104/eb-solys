/*******************************************************************************
 * Copyright (C) 2019 systemticks GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.api.liveviewer;

import com.elektrobit.ebrace.core.interactor.api.common.BaseUseCase;

public interface LiveViewerHandlerInteractionUseCase extends BaseUseCase
{
    void activateLiveViewer();
}