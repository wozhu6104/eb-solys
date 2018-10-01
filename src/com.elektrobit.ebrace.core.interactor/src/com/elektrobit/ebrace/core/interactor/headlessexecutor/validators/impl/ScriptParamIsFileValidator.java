/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.headlessexecutor.validators.impl;

import java.io.File;

import com.elektrobit.ebrace.core.interactor.headlessexecutor.validators.api.HeadlessParamValidator;

public class ScriptParamIsFileValidator implements HeadlessParamValidator
{

    private final File scriptFile;

    public ScriptParamIsFileValidator(final File scriptFile)
    {
        this.scriptFile = scriptFile;
    }

    @Override
    public boolean validationFailed()
    {
        boolean result = !scriptFile.isFile();
        return result;
    }

    @Override
    public String errorMessage()
    {
        return "Script param must point to a valid file.";
    }

}
