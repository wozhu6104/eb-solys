/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.interactor.headlessexecutor.validators.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

import com.elektrobit.ebrace.core.interactor.headlessexecutor.validators.impl.NumberOfParamsValidator;

public class NumberOfParamsValidatorTest
{
    @Test
    public void validationNotFailingIfTwoParams() throws Exception
    {
        final NumberOfParamsValidator validator = new NumberOfParamsValidator()
                .setParams( Arrays.asList( "ScriptParam", "DataSourceParam" ) );
        assertFalse( "Expecting validation not failing, if two params there.", validator.validationFailed() );
    }

    @Test
    public void validationNotFailingIfMoreThanTwoParams() throws Exception
    {
        final NumberOfParamsValidator validator = new NumberOfParamsValidator()
                .setParams( Arrays.asList( "ScriptParam", "DataSourceParam", "myparam=1", "myparam=2" ) );
        assertFalse( "Expecting validation not failing, if more than two params there.", validator.validationFailed() );
    }

    @Test
    public void validationFailingIfOnlyOneParam() throws Exception
    {
        final NumberOfParamsValidator validator = new NumberOfParamsValidator()
                .setParams( Arrays.asList( "ScriptParam" ) );
        assertTrue( "Expecting validation failing, if one param there.", validator.validationFailed() );
    }

    @Test
    public void validationFailingIfNoParam() throws Exception
    {
        final NumberOfParamsValidator validator = new NumberOfParamsValidator().setParams( Collections.emptyList() );
        assertTrue( "Expecting validation failing, if no params there.", validator.validationFailed() );
    }

    @Test
    public void errorMessageNotNull() throws Exception
    {
        final NumberOfParamsValidator validator = new NumberOfParamsValidator().setParams( Collections.emptyList() );
        assertNotNull( "Expecting that there is a error message for this validator", validator.errorMessage() );
    }
}
