/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.franca.common;

import org.junit.Assert;
import org.junit.Test;

public class DefaultFrancaModelLoaderTest
{
    @Test
    public void loadModelTest()
    {
        Assert.assertTrue( "Franca model loading can't be tested with pure JUnit because it has dependecies to com.google.inject.",
                           true );
    }
}
