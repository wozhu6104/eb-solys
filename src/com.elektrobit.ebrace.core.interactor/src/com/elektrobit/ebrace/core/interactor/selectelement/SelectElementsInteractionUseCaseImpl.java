/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.selectelement;

import java.util.List;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.core.interactor.api.selectelement.SelectElementsInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.selectelement.SelectElementsInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.common.UseCaseExecutor;
import com.elektrobit.ebrace.core.interactor.common.UseCaseRunnable;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.SelectedElementsService;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.TimebasedObject;

public class SelectElementsInteractionUseCaseImpl implements SelectElementsInteractionUseCase
{

    @SuppressWarnings("unused")
    private SelectElementsInteractionCallback callback;
    private final SelectedElementsService selectedElmntService;

    public SelectElementsInteractionUseCaseImpl(SelectElementsInteractionCallback callback,
            SelectedElementsService selectedElementService)
    {

        RangeCheckUtils.assertReferenceParameterNotNull( "callback", callback );
        RangeCheckUtils.assertReferenceParameterNotNull( "selectedElmntService ", selectedElementService );
        this.callback = callback;
        this.selectedElmntService = selectedElementService;
    }

    @Override
    public void unregister()
    {
        this.callback = null;

    }

    @Override
    public void selectedResource(final List<TimebasedObject> events)
    {
        UseCaseExecutor.schedule( new UseCaseRunnable( "SelectElementsInteractionUseCase.selectedResource",
                                                       () -> selectedElmntService.setSelectedElements( events ) ) );
    }

}
