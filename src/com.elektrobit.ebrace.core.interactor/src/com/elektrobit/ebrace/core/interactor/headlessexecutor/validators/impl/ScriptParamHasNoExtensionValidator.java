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

import org.apache.commons.io.FilenameUtils;

import com.elektrobit.ebrace.core.interactor.headlessexecutor.validators.api.HeadlessParamValidator;

public class ScriptParamHasNoExtensionValidator implements HeadlessParamValidator
{
    private final File scriptFile;

    public ScriptParamHasNoExtensionValidator(File scriptFile)
    {
        this.scriptFile = scriptFile;
    }

    @Override
    public boolean validationFailed()
    {
        final boolean result = filePathHasExtension();
        return result;
    }

    private boolean filePathHasExtension()
    {
        final String extension = FilenameUtils.getExtension( scriptFile.getAbsolutePath() );
        return !extension.isEmpty();
    }

    @Override
    public String errorMessage()
    {
        return "Script name/path must not contain extension. E.g. path '/path/to/my/scripts/MyScript.xtend' shall be '/path/to/my/scripts/MyScript'.";
    }

}
