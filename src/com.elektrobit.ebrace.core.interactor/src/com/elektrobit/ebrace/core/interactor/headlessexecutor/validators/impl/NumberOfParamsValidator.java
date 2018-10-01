/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.headlessexecutor.validators.impl;

import java.util.List;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.core.interactor.headlessexecutor.validators.api.HeadlessParamValidator;

public class NumberOfParamsValidator implements HeadlessParamValidator
{
    private List<String> params;

    public NumberOfParamsValidator setParams(final List<String> params)
    {
        this.params = params;
        return this;
    }

    @Override
    public boolean validationFailed()
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "params", params );

        final boolean result = (params.isEmpty() || params.size() == 1);
        return result;
    }

    @Override
    public String errorMessage()
    {
        return "Number of EB solys automation parameter must be at least 2: Script path and data source (file or connection).";
    }

}
