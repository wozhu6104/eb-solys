/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.htmldata.internal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;

import com.elektrobit.ebrace.core.htmldata.api.HtmlDataService;
import com.elektrobit.ebrace.core.interactor.api.resources.model.htmlview.HtmlContentChangedListener;
import com.elektrobit.ebrace.core.interactor.api.resources.model.htmlview.HtmlViewModel;

@Component
public class HtmlDataServiceImpl implements HtmlDataService
{
    private final List<HtmlContentChangedListener> listeners = new ArrayList<HtmlContentChangedListener>();

    public HtmlDataServiceImpl()
    {
    }

    @Override
    public void setHtmlViewText(HtmlViewModel model, String text)
    {
        FileWriter writer;
        try
        {
            File file = new File( new URL( model.getURL() ).toURI() );
            writer = new FileWriter( file );
            writer.write( text );
            writer.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();
        }
        finally
        {
            notifyListeners( model );
        }
    }

    private void notifyListeners(HtmlViewModel model)
    {
        for (HtmlContentChangedListener listener : listeners)
        {
            if (listener != null)
            {
                listener.onContentChanged( model );
            }
        }
    }

    @Override
    public void addCallback(HtmlContentChangedListener cb)
    {
        listeners.add( cb );
    }

    @Override
    public void removeCallback(HtmlContentChangedListener cb)
    {
        listeners.remove( listeners.indexOf( cb ) );
    }

    @Override
    public void callJavaScriptFunction(HtmlViewModel model, String function, String arg)
    {
        for (HtmlContentChangedListener listener : listeners)
        {
            if (listener != null)
            {
                listener.onJavaScriptFunctionRequested( model, function, arg );
            }
        }

    }

}
