/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.ui.console.application;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import com.elektrobit.ebrace.ui.console.application.impl.ProjectLocationExtractHelper;

public class ProjectLocationExtractHelperTest
{
    @Test
    public void simplePathExtractedCorrectly() throws Exception
    {
        assertEquals( new File( "e:/tmp/RaceScripts" ).getAbsolutePath(),
                      ProjectLocationExtractHelper
                              .getProjectLocationFromScriptPath( "e:/tmp/RaceScripts/src/HelloWorld.xtend" ) );
    }
}
