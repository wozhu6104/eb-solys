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

import java.util.Map;

public interface ViewModelGenerator
{
    public String handleNode(String name, String parent, Map<String, Object> annotations);

    public String handleEdge(String from, String to, Map<String, Object> annotations);

    public String start();

    public String end();

    public String nodesStart();

    public String nodesEnd();

    public String edgesStart();

    public String edgesEnd();

    public String separator();
}
