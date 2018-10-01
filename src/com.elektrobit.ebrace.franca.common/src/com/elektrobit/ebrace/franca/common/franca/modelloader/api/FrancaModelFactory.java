/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.franca.common.franca.modelloader.api;

import java.util.List;

import org.franca.core.franca.FModel;

public interface FrancaModelFactory
{
    public List<FModel> getFrancaModels();
}
