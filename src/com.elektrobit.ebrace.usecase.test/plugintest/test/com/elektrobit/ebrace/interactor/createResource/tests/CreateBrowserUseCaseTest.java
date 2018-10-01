/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.interactor.createResource.tests;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.createresource.CreateResourceInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.createresource.CreateResourceInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceTreeNode;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourcesFolder;
import com.elektrobit.ebrace.core.interactor.api.resources.tree.ResourceTreeNotifyCallback;

import test.com.elektrobit.ebrace.core.interactor.UseCaseBaseTest;

public class CreateBrowserUseCaseTest extends UseCaseBaseTest
        implements
            CreateResourceInteractionCallback,
            ResourceTreeNotifyCallback
{
    private List<ResourcesFolder> folders;

    private final String TEST_BROWSER_NAME = "Test Browser";

    @Test
    public void browserFolderAndDefaultBrowserWasCreated() throws Exception
    {
        UseCaseFactoryInstance.get().makeResouceTreeNotifyUseCase( this );

        CreateResourceInteractionUseCase resourceUseCase = UseCaseFactoryInstance.get().makeCreateResourceUseCase( this );

        resourceUseCase.createAndOpenHtmlView( TEST_BROWSER_NAME, "" );

        assertHasResourceFolderWithName( "HTML Views" );
        assertHasDefaultBrowser();
    }

    private void assertHasDefaultBrowser()
    {
        boolean browserFound = false;
        for (ResourcesFolder nextFolder : folders)
        {
            if (nextFolder.getName().equals( "HTML Views" ))
            {
                for (ResourceTreeNode view : nextFolder.getChildren())
                {
                    browserFound |= view.getName().equals( TEST_BROWSER_NAME );
                }
            }
        }

        Assert.assertTrue( browserFound );
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
    public void openResource(ResourceModel resourceModel)
    {
    }

    @Override
    public void onProVersionNotAvailable()
    {
    }
}
