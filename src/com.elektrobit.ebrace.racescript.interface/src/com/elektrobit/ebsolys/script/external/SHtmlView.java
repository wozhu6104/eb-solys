/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebsolys.script.external;

public interface SHtmlView
{
    public SHtmlView setContent(String text);

    public void delete();

    public SHtmlView callJavaScriptFunction(String function, String arg);

    public SHtmlView setName(String name);
}
