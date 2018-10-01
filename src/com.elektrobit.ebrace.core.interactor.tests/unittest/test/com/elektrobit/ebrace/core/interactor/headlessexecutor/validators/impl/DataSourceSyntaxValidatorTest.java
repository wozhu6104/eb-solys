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

import org.junit.Test;

import com.elektrobit.ebrace.core.interactor.headlessexecutor.validators.impl.DataSourceSyntaxValidator;

public class DataSourceSyntaxValidatorTest
{

    @Test
    public void validationFailedIfFileNotExists() throws Exception
    {
        final DataSourceSyntaxValidator validator = new DataSourceSyntaxValidator( "fileNotThere" );
        assertTrue( "Expecting validation failed if file not exists.", validator.validationFailed() );
    }

    @Test
    public void validationNotFailedIfConnectionParamOk() throws Exception
    {
        final DataSourceSyntaxValidator validator = new DataSourceSyntaxValidator( "192.168.2.2:1234" );
        assertFalse( "Expecting validation not failed if connection params right.", validator.validationFailed() );
    }

    @Test
    public void validationFailedIfPortToBig() throws Exception
    {
        final DataSourceSyntaxValidator validator = new DataSourceSyntaxValidator( "192.168.2.2:123456" );
        assertTrue( "Expecting validation failed if port to big.", validator.validationFailed() );
    }

    @Test
    public void validationFailedIfPortToSmall() throws Exception
    {
        final DataSourceSyntaxValidator validator = new DataSourceSyntaxValidator( "192.168.2.2:0" );
        assertTrue( "Expecting validation failed if port to small.", validator.validationFailed() );
    }

    @Test
    public void validationFailedIfPortNoANumber() throws Exception
    {
        final DataSourceSyntaxValidator validator = new DataSourceSyntaxValidator( "192.168.2.2:thisShouldBeANumber" );
        assertTrue( "Expecting validation failed if port not a number.", validator.validationFailed() );
    }

    @Test
    public void validationFailedIfPortNotThere() throws Exception
    {
        final DataSourceSyntaxValidator validator = new DataSourceSyntaxValidator( "192.168.2.2:" );
        assertTrue( "Expecting validation failed if port not there.", validator.validationFailed() );
    }

    @Test
    public void validationFailedIfIPNotThere() throws Exception
    {
        final DataSourceSyntaxValidator validator = new DataSourceSyntaxValidator( ":1234" );
        assertTrue( "Expecting validation failed if IP not there.", validator.validationFailed() );
    }

    @Test
    public void validationFailedIfIPAndPortNotThere() throws Exception
    {
        final DataSourceSyntaxValidator validator = new DataSourceSyntaxValidator( ":" );
        assertTrue( "Expecting validation failed if IP not there.", validator.validationFailed() );
    }

    @Test
    public void errorMessageNotNull() throws Exception
    {
        final DataSourceSyntaxValidator validator = new DataSourceSyntaxValidator( ":" );
        assertNotNull( "Expecting that there is a error message for this validator", validator.errorMessage() );
    }
}
