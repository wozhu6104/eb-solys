/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.resources.tester;

import org.eclipse.core.expressions.PropertyTester;

import com.elektrobit.ebrace.platform.commandlineparser.extern.interfaces.BetaFeatureConfigurator;

public class BetaActiveStateProvider extends PropertyTester
{
    public final static String BETA_ACTIVE_STATE_ID = "isBetaMode";

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue)
    {
        if (BETA_ACTIVE_STATE_ID.equals( property ))
        {
            return BetaFeatureConfigurator.Features.DATA_CHUNK.isActive();
        }
        return false;
    }

}
