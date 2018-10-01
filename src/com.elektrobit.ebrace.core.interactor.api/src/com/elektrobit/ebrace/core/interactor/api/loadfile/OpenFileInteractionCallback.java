/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.api.loadfile;

public interface OpenFileInteractionCallback
{
    public void onFileTooBig(String pathToFile);

    public void onFileLoadingStarted(String pathToFile);

    public void onFileLoadedSucessfully();

    public void onFileLoadingFailed();

    public void onFileAlreadyLoaded(String pathToFile);

    public void onFileEmpty(String pathToFile);

    public void onFileNotFound(String pathToFile);
}
