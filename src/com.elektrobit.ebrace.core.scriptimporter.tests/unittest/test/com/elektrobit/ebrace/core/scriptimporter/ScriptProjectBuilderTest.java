/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.scriptimporter;

import static org.mockito.Mockito.mock;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.elektrobit.ebrace.core.scriptimporter.impl.project.CopyFileToProjectHelper;
import com.elektrobit.ebrace.core.scriptimporter.impl.project.ScriptProjectBuilderImpl;

public class ScriptProjectBuilderTest
{
    // TODO rage2903#17.29 : Test makes nothing
    @Test
    public void addUserScripts() throws Exception
    {
        CopyFileToProjectHelper copyFileToProjectHelper = mock( CopyFileToProjectHelper.class );
        ScriptProjectBuilderImpl scriptProjectBuilderService = new ScriptProjectBuilderImpl( copyFileToProjectHelper );

        List<File> scripts = Arrays.asList( new File( "files/MyScript.xtend" ) );
        scriptProjectBuilderService.addUserScripts( scripts );
    }

}
