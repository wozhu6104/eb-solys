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

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.core.htmldata.api.HtmlDataService;
import com.elektrobit.ebrace.core.interactor.api.browsercontent.HtmlViewChangedCallback;
import com.elektrobit.ebrace.core.interactor.api.browsercontent.HtmlViewNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.common.UIExecutor;
import com.elektrobit.ebrace.core.interactor.api.resources.model.htmlview.HtmlContentChangedListener;
import com.elektrobit.ebrace.core.interactor.api.resources.model.htmlview.HtmlViewModel;

public class HtmlViewNotifyUseCaseImpl implements HtmlViewNotifyUseCase, HtmlContentChangedListener
{
    private HtmlViewChangedCallback cb;
    private HtmlDataService service;
    private HtmlViewModel model;

    public HtmlViewNotifyUseCaseImpl(HtmlDataService htmlDataService, HtmlViewChangedCallback callback)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "browserService", htmlDataService );
        RangeCheckUtils.assertReferenceParameterNotNull( "callback", callback );
        service = htmlDataService;
        service.addCallback( this );
        cb = callback;
    }

    @Override
    public void register(HtmlViewModel model)
    {
        this.model = model;
    }

    @Override
    public void unregister()
    {
        cb = null;
        service.removeCallback( this );
        service = null;
    }

    @Override
    public void onContentChanged(final HtmlViewModel model)
    {
        if (this.model != null && (this.model.getName()).equals( model.getName() ))
        {
            UIExecutor.post( new Runnable()
            {
                @Override
                public void run()
                {
                    if (cb != null)
                        cb.onContentChanged();
                };
            } );
        }
    }

    @Override
    public void onJavaScriptFunctionRequested(HtmlViewModel model, final String function, final String arg)
    {
        UIExecutor.post( new Runnable()
        {
            @Override
            public void run()
            {
                if (cb != null)
                    cb.onJavaScriptFunctionRequested( function, arg );
            };
        } );

    }

}
