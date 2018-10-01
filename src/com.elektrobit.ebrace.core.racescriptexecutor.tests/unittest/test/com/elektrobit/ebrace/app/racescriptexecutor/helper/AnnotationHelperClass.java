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

import com.elektrobit.ebsolys.script.external.AfterScript;
import com.elektrobit.ebsolys.script.external.BeforeScript;
import com.elektrobit.ebsolys.script.external.Execute;
import com.elektrobit.ebsolys.script.external.Execute.ExecutionContext;

public class AnnotationHelperClass
{

    @Execute(context = ExecutionContext.GLOBAL)
    public void testExecuteScriptMethod()
    {

    }

    @Execute(context = ExecutionContext.CALLBACK)
    public void testCallbackMethod()
    {

    }

    @BeforeScript
    public void testBeforeMethod()
    {

    }

    @AfterScript
    public void testAfterMethod()
    {

    }

}
