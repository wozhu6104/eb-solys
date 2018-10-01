/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.interactor.table;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.tableinput.TableSearchTermInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.tableinput.TableSearchTermNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.tableinput.TableSearchTermNotifyUseCase;

import test.com.elektrobit.ebrace.core.interactor.UseCaseBaseTest;

public class TableSearchTermTest extends UseCaseBaseTest implements TableSearchTermNotifyCallback
{
    private static final String TEST_VIEW_ID = "testViewID";
    private TableSearchTermInteractionUseCase termInteractionUseCase;
    private List<String> latestSearchTerms = null;
    private TableSearchTermNotifyUseCase termNotifyUseCase;

    @Before
    public void setup()
    {
        termInteractionUseCase = UseCaseFactoryInstance.get().makeTableSearchTermInteractionUseCase( TEST_VIEW_ID );
        termInteractionUseCase.deleteAllTerms();

        termNotifyUseCase = UseCaseFactoryInstance.get().makeTableSearchTermNotifyUseCase( this, TEST_VIEW_ID );
    }

    @Test
    public void testUpdateWithNewTerms() throws Exception
    {
        termInteractionUseCase.addSearchTerm( "aaa" );
        Assert.assertArrayEquals( new String[]{"aaa"}, latestSearchTerms.toArray() );

        termInteractionUseCase.addSearchTerm( "bbb" );
        Assert.assertArrayEquals( new String[]{"bbb", "aaa"}, latestSearchTerms.toArray() );

        termInteractionUseCase.addSearchTerm( "aaa" );
        Assert.assertArrayEquals( new String[]{"aaa", "bbb"}, latestSearchTerms.toArray() );
    }

    @Test
    public void testUpdateWithEmptyString() throws Exception
    {
        termInteractionUseCase.addSearchTerm( "aaa" );
        termInteractionUseCase.addSearchTerm( "" );
        Assert.assertArrayEquals( new String[]{"aaa"}, latestSearchTerms.toArray() );
    }

    @Test
    public void testUpdateWithNullString() throws Exception
    {
        termInteractionUseCase.addSearchTerm( "aaa" );
        termInteractionUseCase.addSearchTerm( null );
        Assert.assertArrayEquals( new String[]{"aaa"}, latestSearchTerms.toArray() );
    }

    @Test
    public void testUnregister() throws Exception
    {
        termInteractionUseCase.addSearchTerm( "aaa" );
        termNotifyUseCase.unregister();
        termInteractionUseCase.addSearchTerm( "bbb" );

        Assert.assertArrayEquals( new String[]{"aaa"}, latestSearchTerms.toArray() );
    }

    @After
    public void unregister()
    {
        termNotifyUseCase.unregister();
    }

    @Override
    public void onSearchTermsChanged(List<String> searchTerms)
    {
        this.latestSearchTerms = searchTerms;
    }
}
