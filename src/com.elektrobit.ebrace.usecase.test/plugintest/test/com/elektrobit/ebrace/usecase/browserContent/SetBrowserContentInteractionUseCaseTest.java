/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.usecase.browserContent;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.elektrobit.ebrace.core.interactor.api.browsercontent.SetHtmlViewContentInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.createresource.CreateResourceInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.createresource.CreateResourceInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourcesFolder;
import com.elektrobit.ebrace.core.interactor.api.resources.model.htmlview.HtmlContentChangedListener;
import com.elektrobit.ebrace.core.interactor.api.resources.model.htmlview.HtmlViewModel;
import com.elektrobit.ebrace.core.interactor.api.resources.tree.ResourceTreeNotifyCallback;

import test.com.elektrobit.ebrace.core.interactor.UseCaseBaseTest;

public class SetBrowserContentInteractionUseCaseTest extends UseCaseBaseTest
        implements
            CreateResourceInteractionCallback,
            ResourceTreeNotifyCallback,
            HtmlContentChangedListener
{
    private List<ResourcesFolder> folders;

    private final String inputHTML = "<html><body>Hello2</body></html>";

    @Test
    public void browserFolderAndDefaultBrowserWasCreated() throws Exception
    {
        UseCaseFactoryInstance.get().makeResouceTreeNotifyUseCase( this );

        CreateResourceInteractionUseCase resourceUseCase = UseCaseFactoryInstance.get().makeCreateResourceUseCase( this );
        SetHtmlViewContentInteractionUseCase contentUseCase = UseCaseFactoryInstance.get().makeSetHtmlViewContentUseCase();

        HtmlViewModel model = resourceUseCase.createAndOpenHtmlView( "Default Browser", "test.html" );
        contentUseCase.setContent( model, inputHTML );

        String resultHTML = getResultHTMLFromFile( model.getURL() );

        assertHasResourceFolderWithName( "HTML Views" );
        assertEquals( resultHTML, inputHTML );
    }

    private String getResultHTMLFromFile(String filePath)
    {
        BufferedReader br;
        String resultHTML = "";
        try
        {
            br = new BufferedReader( new FileReader( filePath ) );
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null)
            {
                sb.append( line );
                line = br.readLine();
            }
            resultHTML = sb.toString();
            br.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return resultHTML;
    }

    private void assertHasResourceFolderWithName(String name)
    {
        for (ResourcesFolder nextFolder : folders)
        {
            if (nextFolder.getName().equals( name ))
            {
                return;
            }
        }

        Assert.fail( "Expecting folder " + name + " in ResourceFolders." );
    }

    @Override
    public void onNewResourceTreeData(List<ResourcesFolder> folders)
    {
        this.folders = folders;
    }

    @Override
    public void onChartChannelsTypeMismatch()
    {
    }

    @Override
    public void onDerivedResourceAlreadyExists()
    {
    }

    @Override
    public void revealResource(ResourceModel resourceModel)
    {
    }

    @Override
    public void onContentChanged(HtmlViewModel affectedModel)
    {
    }

    @Override
    public void onJavaScriptFunctionRequested(HtmlViewModel model, String function, String arg)
    {
    }

    @Override
    public void openResource(ResourceModel resourceModel)
    {
    }

    @Override
    public void onProVersionNotAvailable()
    {
    }
}
