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

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import test.com.elektrobit.ebrace.app.racescriptexecutor.helper.AnnotationHelperTest;
import test.com.elektrobit.ebrace.app.racescriptexecutor.impl.service.ScriptFileLoaderTest;
import test.com.elektrobit.ebrace.app.racescriptexecutor.impl.service.ScriptTargetConnectorTest;
import test.com.elektrobit.ebrace.app.racescriptexecutor.impl.service.ScriptTargetDisconnectorTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({AnnotationHelperTest.class, DecodedNodeScriptAPITest.class, RaceScriptHelperTest.class,
        ScriptFileLoaderTest.class, ScriptTargetDisconnectorTest.class, ScriptTargetConnectorTest.class})
public class ComElektrobitEbraceAppRacescriptexecutorTest
{
}
