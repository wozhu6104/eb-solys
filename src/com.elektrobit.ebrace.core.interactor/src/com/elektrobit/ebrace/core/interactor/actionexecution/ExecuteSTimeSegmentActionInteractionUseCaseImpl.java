/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.actionexecution;

import com.elektrobit.ebrace.core.interactor.api.actionexecution.ExecuteSTimeSegmentActionInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.common.UseCaseExecutor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.STimeSegment;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.STimeSegmentClickAction;

public class ExecuteSTimeSegmentActionInteractionUseCaseImpl implements ExecuteSTimeSegmentActionInteractionUseCase
{
    @Override
    public void executeClickAction(STimeSegment segment)
    {
        STimeSegmentClickAction clickAction = segment.getClickAction();

        if (clickAction != null)
        {
            UseCaseExecutor.schedule( () -> clickAction.onSTimeSegmentClicked( segment ) );
        }
    }
}
