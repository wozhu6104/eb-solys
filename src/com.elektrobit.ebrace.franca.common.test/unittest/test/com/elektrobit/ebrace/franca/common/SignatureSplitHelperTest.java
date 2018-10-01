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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.elektrobit.ebrace.franca.common.franca.mapper.impl.SignatureSplitHelper;

public class SignatureSplitHelperTest
{
    @Test
    public void isInterfaceCorrect() throws Exception
    {
        assertEquals( "org.genivi.navigationcore.MapMatchedPosition",
                      SignatureSplitHelper
                              .getInterfaceName( "org.genivi.navigationcore.MapMatchedPosition.GetPosition" ) );
    }

    @Test
    public void isMethodCorrect() throws Exception
    {
        assertEquals( "GetPosition",
                      SignatureSplitHelper
                              .getMethodName( "org.genivi.navigationcore.MapMatchedPosition.GetPosition" ) );
    }
}
