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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.elektrobit.ebrace.core.interactor.headlessexecutor.validators.impl.ScriptParamIsFileValidator;

public class ScriptParamIsFileValidatorTest
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void vaidationFailingIfNotAValidFile() throws Exception
    {
        final ScriptParamIsFileValidator validator = new ScriptParamIsFileValidator( new File( "FileNotExisting" ) );
        assertTrue( "Expecting validation failing, if file not exists.", validator.validationFailed() );
    }

    @Test
    public void vaidationNotFailingIfAValidFile() throws Exception
    {
        final File script = folder.newFile( "MyScript.xtend" );
        final ScriptParamIsFileValidator validator = new ScriptParamIsFileValidator( script );
        assertFalse( "Expecting validation not failing, if file exists.", validator.validationFailed() );
    }

    @Test
    public void errorMessageNotNull() throws Exception
    {
        final ScriptParamIsFileValidator validator = new ScriptParamIsFileValidator( new File( "FileNotExisting" ) );
        assertNotNull( "Expecting that there is a error message for this validator", validator.errorMessage() );
    }
}
