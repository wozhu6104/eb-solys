/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.api.datainput;

import java.io.IOException;
import java.util.Set;

public interface DataInputUseCase
{
    public Set<String> getAllAvailableDataInputs();

    public void loadDataInputsFromFile(String path) throws IOException;

    public void startReadingAllInputs();

    public void stopReadingAllInputs();

    public void startReading(String dataInputId);

    public void stopReading(String dataInputId);
}
