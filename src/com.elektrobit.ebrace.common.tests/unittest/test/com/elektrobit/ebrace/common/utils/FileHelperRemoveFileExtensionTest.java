/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.common.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.elektrobit.ebrace.common.utils.FileHelper;

public class FileHelperRemoveFileExtensionTest
{
    @Test
    public void withExtension() throws Exception
    {
        assertEquals( "MyScript", FileHelper.removeExtension( "MyScript.xtend" ) );
    }

    @Test
    public void withoutExtension() throws Exception
    {
        assertEquals( "MyScript", FileHelper.removeExtension( "MyScript" ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyFileName() throws Exception
    {
        FileHelper.removeExtension( "" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullFileName() throws Exception
    {
        FileHelper.removeExtension( null );
    }

}
