/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.app.racescriptexecutor;

import static org.mockito.Mockito.mock;

import org.junit.Test;

import com.elektrobit.ebrace.app.racescriptexecutor.impl.service.ScriptExecutorServiceImpl;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptInfo;
import com.elektrobit.ebrace.core.racescriptexecutor.api.ScriptExecutorService;

public class ScriptExecutorServiceTest
{
    @Test
    public void testName() throws Exception
    {
        ScriptExecutorService service = new ScriptExecutorServiceImpl();
        service.runScriptWithGlobalMethod( mock( RaceScriptInfo.class ), "myMethod" );
    }
}
