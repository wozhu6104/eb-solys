/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.util;

import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.IEvaluationService;

public class PropertyTesterUtil
{
    public static void refreshPropertyTesterEvaluation(String propertyId)
    {
        IEvaluationService evalServ = getEvaluationServiceToUpdatePropertyOfHandler();
        if (evalServ != null)
            getEvaluationServiceToUpdatePropertyOfHandler().requestEvaluation( propertyId );
    }

    private static IEvaluationService getEvaluationServiceToUpdatePropertyOfHandler()
    {
        if (PlatformUI.getWorkbench().getActiveWorkbenchWindow() != null)
        {
            return (IEvaluationService)PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                    .getService( IEvaluationService.class );
        }
        return null;
    }
}
