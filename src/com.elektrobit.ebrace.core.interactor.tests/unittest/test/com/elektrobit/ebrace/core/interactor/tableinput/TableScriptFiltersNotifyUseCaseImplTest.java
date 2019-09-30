/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.interactor.tableinput;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.core.interactor.api.resources.model.script.RaceScript;
import com.elektrobit.ebrace.core.interactor.api.resources.model.script.RaceScriptResourceModel;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptMethod;
import com.elektrobit.ebrace.core.interactor.api.table.TableScriptFiltersNotifyCallback;
import com.elektrobit.ebrace.core.interactor.tableinput.TableScriptFiltersNotifyUseCaseImpl;
import com.elektrobit.ebrace.core.racescriptexecutor.api.RaceScriptLoader;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;

import test.com.elektrobit.ebrace.core.interactor.UseCaseBaseTest;

public class TableScriptFiltersNotifyUseCaseImplTest extends UseCaseBaseTest implements TableScriptFiltersNotifyCallback
{
    private static final String SCRIPT_ONE_NAME = "scriptOne";
    private ResourcesModelManager mockedResourcesModelManager;
    private TableScriptFiltersNotifyUseCaseImpl sutScriptFilterNotifyUseCase;
    private List<RaceScriptMethod> lastFilterMethods = null;
    private RaceScript mockedScript;

    @Before
    public void setup()
    {
        lastFilterMethods = null;

        mockedResourcesModelManager = mock( ResourcesModelManager.class );

        RaceScriptMethod[] methodsArray = new RaceScriptMethod[]{
                new RaceScriptMethod( "methodOne", "", SCRIPT_ONE_NAME, null, "" ),
                new RaceScriptMethod( "methodTwo", "", SCRIPT_ONE_NAME, null, "" )};

        List<RaceScriptResourceModel> scriptModelList = new ArrayList<>();
        scriptModelList.add( createScriptModelWithMethods( Arrays.asList( methodsArray ) ) );
        when( mockedResourcesModelManager.getAllScripts() ).thenReturn( scriptModelList );

        sutScriptFilterNotifyUseCase = new TableScriptFiltersNotifyUseCaseImpl( this,
                                                                                mockedResourcesModelManager,
                                                                                mock( RaceScriptLoader.class ) );
    }

    public RaceScriptResourceModel createScriptModelWithMethods(List<RaceScriptMethod> methods)
    {
        RaceScriptResourceModel mockedScriptResourceModel = mock( RaceScriptResourceModel.class );
        mockedScript = mock( RaceScript.class );
        when( mockedScriptResourceModel.getScriptInfo() ).thenReturn( mockedScript );
        when( mockedScriptResourceModel.getName() ).thenReturn( SCRIPT_ONE_NAME );

        when( mockedScript.getFilterMethods() ).thenReturn( methods );
        when( mockedScript.getName() ).thenReturn( SCRIPT_ONE_NAME );
        return mockedScriptResourceModel;
    }

    @Test
    public void testInitialData() throws Exception
    {
        Assert.assertEquals( 2, lastFilterMethods.size() );
        Assert.assertEquals( "methodOne", lastFilterMethods.get( 0 ).getMethodName() );
        Assert.assertEquals( "methodTwo", lastFilterMethods.get( 1 ).getMethodName() );
    }

    @Test
    public void testUpdateWithNewScript() throws Exception
    {
        RaceScriptMethod[] script2Methods = new RaceScriptMethod[]{
                new RaceScriptMethod( "methodThree", "", "scriptThree", null, "" )};
        RaceScript mockedScript2 = mock( RaceScript.class );
        when( mockedScript2.getFilterMethods() ).thenReturn( Arrays.asList( script2Methods ) );
        when( mockedScript2.getName() ).thenReturn( "scriptThree" );

        sutScriptFilterNotifyUseCase.filterMethodsChanged( mockedScript2, Arrays.asList( script2Methods ) );

        Assert.assertEquals( 3, lastFilterMethods.size() );
        Assert.assertTrue( listOfMethodContains( lastFilterMethods, "methodThree" ) );
    }

    @Test
    public void testUpdateWithCurrentScript() throws Exception
    {
        postUpdateWithOneChangedMethod();

        Assert.assertEquals( 2, lastFilterMethods.size() );
        Assert.assertTrue( listOfMethodContains( lastFilterMethods, "methodOne" ) );
        Assert.assertTrue( listOfMethodContains( lastFilterMethods, "methodTwoNew" ) );
    }

    private void postUpdateWithOneChangedMethod()
    {
        RaceScriptMethod[] methodsArray = new RaceScriptMethod[]{
                new RaceScriptMethod( "methodOne", "", SCRIPT_ONE_NAME, null, "" ),
                new RaceScriptMethod( "methodTwoNew", "", SCRIPT_ONE_NAME, null, "" )};
        when( mockedScript.getFilterMethods() ).thenReturn( Arrays.asList( methodsArray ) );

        sutScriptFilterNotifyUseCase.filterMethodsChanged( mockedScript, Arrays.asList( methodsArray ) );
    }

    private boolean listOfMethodContains(List<RaceScriptMethod> methods, String methodName)
    {
        for (RaceScriptMethod raceScriptMethod : methods)
        {
            if (raceScriptMethod.getMethodName().equals( methodName ))
            {
                return true;
            }
        }
        return false;
    }

    @Test
    public void testScriptDeleted() throws Exception
    {
        RaceScriptResourceModel mockedScriptResourceModel = mock( RaceScriptResourceModel.class );
        when( mockedScriptResourceModel.getName() ).thenReturn( SCRIPT_ONE_NAME );

        sutScriptFilterNotifyUseCase.onResourceDeleted( mockedScriptResourceModel );
        Assert.assertTrue( lastFilterMethods.isEmpty() );
    }

    @Override
    public void onScriptFilterMethodsChanged(List<RaceScriptMethod> filterMethods)
    {
        this.lastFilterMethods = filterMethods;
    }
}
