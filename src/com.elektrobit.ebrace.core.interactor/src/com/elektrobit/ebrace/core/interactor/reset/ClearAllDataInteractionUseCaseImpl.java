/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.reset;

import com.elektrobit.ebrace.core.interactor.api.reset.ClearAllDataInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.reset.ClearAllDataInteractionUseCase;
import com.elektrobit.ebsolys.core.targetdata.api.reset.ResetNotifier;

public class ClearAllDataInteractionUseCaseImpl implements ClearAllDataInteractionUseCase
{

    private final ClearAllDataInteractionCallback callback;
    private final ResetNotifier resetNotifier;

    public ClearAllDataInteractionUseCaseImpl(ClearAllDataInteractionCallback callback, ResetNotifier resetNotifier)
    {
        this.callback = callback;
        this.resetNotifier = resetNotifier;
    }

    @Override
    public void reset()
    {
        resetNotifier.performReset();
        callback.onResetDone();
    }

    @Override
    public void unregister()
    {
    }

}
