/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.app.racescriptexecutor.helper;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.elektrobit.ebrace.app.racescriptexecutor.helper.AnnotationHelper;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptMethod;

public class AnnotationHelperTest
{
    @Test
    public void testGetAfterMethods()
    {
        List<RaceScriptMethod> afterMethods = AnnotationHelper.getAfterMethods( new AnnotationHelperClass(),
                                                                                "ScriptName" );
        Assert.assertEquals( afterMethods.get( 0 ).getMethodName(), "testAfterMethod" );
    }

    @Test
    public void testGetBeforeMethods()
    {
        List<RaceScriptMethod> beforeMethods = AnnotationHelper.getBeforeMethods( new AnnotationHelperClass(),
                                                                                  "ScriptName" );
        Assert.assertEquals( beforeMethods.get( 0 ).getMethodName(), "testBeforeMethod" );
    }

    @Test
    public void testGetAllMethods()
    {
        List<RaceScriptMethod> getAllMethods = AnnotationHelper.getAllMethods( new AnnotationHelperClass(),
                                                                               "ScriptName" );

        // Number of methods should be 4 of course,
        // but if a test coverage run is with EmmaECL done ,
        // then a method is added to the AnnotationHelperClass
        // and the test fails.
        // That's why we check also if there more than 4 methods
        Assert.assertTrue( getAllMethods.size() >= 4 );
    }
}
