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

import java.io.File;

import org.junit.Test;

import com.elektrobit.ebrace.core.interactor.headlessexecutor.validators.impl.ScriptParamHasNoExtensionValidator;

public class ScriptParamHasNoExtensionValidatorTest
{
    @Test
    public void validationFailedIfScriptNameHasExtension() throws Exception
    {
        final ScriptParamHasNoExtensionValidator validator = new ScriptParamHasNoExtensionValidator( new File( "Test.xtend" ) );
        assertTrue( "Expecting validation failing, if script name has extension.", validator.validationFailed() );
    }

    @Test
    public void validationFailedIfScriptPathHasExtension() throws Exception
    {
        final ScriptParamHasNoExtensionValidator validator = new ScriptParamHasNoExtensionValidator( new File( "/home/user/Test.xtend" ) );
        assertTrue( "Expecting validation failing, if script path has extension.", validator.validationFailed() );
    }

    @Test
    public void validationNotFailedIfScriptPathNoHasExtension() throws Exception
    {
        final ScriptParamHasNoExtensionValidator validator = new ScriptParamHasNoExtensionValidator( new File( "e:\\home\\user\\Test" ) );
        assertFalse( "Expecting validation not failing, if script path has extension.", validator.validationFailed() );
    }

    @Test
    public void validationNotFailingIfScriptPathHasExtension() throws Exception
    {
        final ScriptParamHasNoExtensionValidator validator = new ScriptParamHasNoExtensionValidator( new File( "Test" ) );
        assertFalse( "Expecting validation not failing, if script path has no extension.",
                     validator.validationFailed() );
    }

    @Test
    public void errorMessageNotNull() throws Exception
    {
        final ScriptParamHasNoExtensionValidator validator = new ScriptParamHasNoExtensionValidator( new File( "Test" ) );
        assertNotNull( "Expecting that there is a error message for this validator", validator.errorMessage() );
    }
}
