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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.URL;

import org.junit.Test;

public class UserDocTest
{

    @Test
    public void localUserdocInstalled() throws Exception
    {
        URL installationFolder = new File( "../com.elektrobit.ebrace.ui.ecl.browser.test" ).toURI().toURL();

        assertEquals( installationFolder + "userdoc/intro/index.html", new UserDoc( installationFolder ).getDocURL() );
    }

    @Test
    public void localUserdocNotInstalled() throws Exception
    {
        assertEquals( "http://er01545p:8989/userContent/ci/latest-stable/userdoc/intro/index.html",
                      new UserDoc( new URL( "file:///nonsense" ) ).getDocURL() );

    }

}
