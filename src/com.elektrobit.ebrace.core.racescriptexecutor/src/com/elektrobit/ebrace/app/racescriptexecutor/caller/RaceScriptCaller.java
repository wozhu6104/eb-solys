/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.app.racescriptexecutor.caller;

public interface RaceScriptCaller
{
    void callBeforeMethod(String scriptName, Object raceScriptInstance) throws RuntimeException;

    void callScript(String scriptName, Object raceScriptInstance, String executeMethod, Object... params)
            throws RuntimeException;

    void callAfterMethod(String scriptName, Object raceScriptInstance);
}
