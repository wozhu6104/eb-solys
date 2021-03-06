/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.api.browsercontent;

import com.elektrobit.ebrace.core.interactor.api.common.BaseUseCase;
import com.elektrobit.ebrace.core.interactor.api.resources.model.htmlview.HtmlViewModel;

public interface SetHtmlViewContentInteractionUseCase extends BaseUseCase
{
    public void setContent(HtmlViewModel model, String text);

    public void callJavaScriptFunction(HtmlViewModel model, String function, String arg);
}
