/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.franca.common.validator.api;

import org.franca.tools.contracts.tracevalidator.parser.DefaultTraceParser;

import com.elektrobit.ebrace.franca.common.franca.modelloader.api.DefaultFrancaModelFactory;
import com.elektrobit.ebrace.franca.common.validator.impl.DefaultFrancaTraceValidator;

public class FrancaTraceValidatorFactory
{
    public static DefaultFrancaTraceValidator createFrancaTraceValidator()
    {
        return new DefaultFrancaTraceValidator( new DefaultTraceParser(), new DefaultFrancaModelFactory() );
    }
}
