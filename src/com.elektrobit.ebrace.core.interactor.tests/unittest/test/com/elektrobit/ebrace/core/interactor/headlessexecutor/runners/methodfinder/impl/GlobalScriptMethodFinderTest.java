/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.methodfinder.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptInfo;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptMethod;
import com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.script.impl.global.GlobalScriptRunPart;
import com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.script.impl.helper.ScriptMethodFinder;
import com.elektrobit.ebrace.core.racescriptexecutor.api.ScriptExecutorService;

public class GlobalScriptMethodFinderTest
{
    @Test
    public void globalMethodsDelegatedToGenericMethodFinder() throws Exception
    {
        List<RaceScriptMethod> globalMethods = Arrays.asList( mock( RaceScriptMethod.class ) );

        RaceScriptInfo script = mock( RaceScriptInfo.class );
        when( script.getGlobalMethods() ).thenReturn( globalMethods );

        ScriptMethodFinder genericScriptMethodFinder = mock( ScriptMethodFinder.class );

        new GlobalScriptRunPart( mock( ScriptExecutorService.class ), genericScriptMethodFinder )
                .extractScriptMethod( script, "execute" );

        verify( genericScriptMethodFinder ).extractScriptMethod( globalMethods, "execute" );

    }
}
