/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.resources;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class UserDocTest
{
    @Test
    public void pathToUserdocNotNOTFOUND() throws Exception
    {
        String url = new UserDoc().getDocURL();

        assertFalse( url.equals( "NOT-FOUND" ) );
    }
}
