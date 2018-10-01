/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.datamanager.internal.runtime.event;

import org.junit.Assert;
import org.junit.Test;

import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

public class UnitTest
{
    @Test
    public void testCustomUnitEquals() throws Exception
    {
        Unit<Long> customUnit1 = Unit.createCustomUnit( "name", Long.class );
        Unit<Long> customUnit2 = Unit.createCustomUnit( "name", Long.class );
        Assert.assertEquals( customUnit1, customUnit2 );
    }
}
