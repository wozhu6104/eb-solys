/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.browserContent;

import com.elektrobit.ebrace.core.htmldata.api.HtmlDataService;
import com.elektrobit.ebrace.core.interactor.api.browsercontent.SetHtmlViewContentInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.resources.model.htmlview.HtmlViewModel;
import com.elektrobit.ebrace.core.interactor.common.UseCaseExecutor;

public class SetHtmlViewerContentUseCaseImpl implements SetHtmlViewContentInteractionUseCase
{

    private final HtmlDataService htmlDataService;

    public SetHtmlViewerContentUseCaseImpl(HtmlDataService service)
    {
        htmlDataService = service;
    }

    @Override
    public void unregister()
    {
    }

    @Override
    public void setContent(final HtmlViewModel model, final String text)
    {
        UseCaseExecutor.schedule( new Runnable()
        {
            @Override
            public void run()
            {
                if (htmlDataService != null)
                    htmlDataService.setHtmlViewText( model, text );
            }
        } );
    }

    @Override
    public void callJavaScriptFunction(final HtmlViewModel model, final String function, final String arg)
    {
        UseCaseExecutor.schedule( new Runnable()
        {
            @Override
            public void run()
            {
                if (htmlDataService != null)
                    htmlDataService.callJavaScriptFunction( model, function, arg );
            }
        } );

    }

}
