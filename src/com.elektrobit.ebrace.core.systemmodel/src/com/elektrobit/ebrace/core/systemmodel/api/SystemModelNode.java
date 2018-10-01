/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.systemmodel.api;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.annotations.Expose;

import lombok.Data;

@Data
public class SystemModelNode
{
    private String id;
    private SystemModelNode parent;
    @Expose(deserialize = false)
    private Map<String, Object> annotations = new HashMap<>();
}
