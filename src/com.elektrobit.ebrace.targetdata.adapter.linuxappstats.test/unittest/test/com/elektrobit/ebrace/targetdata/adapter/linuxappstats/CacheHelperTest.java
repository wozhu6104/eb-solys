/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.targetdata.adapter.linuxappstats;

import org.junit.Assert;
import org.junit.Test;

import com.elektrobit.ebrace.targetdata.adapter.linuxappstats.service.CacheHelper;

public class CacheHelperTest
{
    @Test
    public void cacheHelperWorking() throws Exception
    {
        CacheHelper<String> cacheHelper = new CacheHelper<String>();
        Assert.assertTrue( cacheHelper.needsUpdate( "key", "value" ) );
        Assert.assertFalse( cacheHelper.needsUpdate( "key", "value" ) );
    }
}
