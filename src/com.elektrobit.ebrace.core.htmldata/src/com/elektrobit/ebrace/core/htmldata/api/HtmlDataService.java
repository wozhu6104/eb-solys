/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.htmldata.api;

import com.elektrobit.ebrace.core.interactor.api.resources.model.htmlview.HtmlContentChangedListener;
import com.elektrobit.ebrace.core.interactor.api.resources.model.htmlview.HtmlViewModel;

public interface HtmlDataService
{
    void setHtmlViewText(HtmlViewModel model, String text);

    void addCallback(HtmlContentChangedListener cb);

    void removeCallback(HtmlContentChangedListener cb);

    void callJavaScriptFunction(HtmlViewModel model, String function, String arg);
}
