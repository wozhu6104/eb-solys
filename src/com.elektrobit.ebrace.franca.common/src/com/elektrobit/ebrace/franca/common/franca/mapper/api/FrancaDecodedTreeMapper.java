/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.franca.common.franca.mapper.api;

import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedNode;

public interface FrancaDecodedTreeMapper
{
    void mapInParameters(DecodedNode interfaceNode, String interfecaName, String memberName);

    void mapOutParameters(DecodedNode interfaceNode, String responseInterface, String responseMethod);

    void mapBroadcastParameters(DecodedNode interfaceNode, String interfaceName, String memberName);
}
