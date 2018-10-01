/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.viewer.runtimeeventloggertable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.core.interactor.api.resources.model.table.TableModel;
import com.elektrobit.ebrace.viewer.common.ImageCreator;
import com.elektrobit.ebrace.viewer.runtimeeventloggertable.labelprovider.TagColumnLabelProvider;
import com.elektrobit.ebrace.viewer.runtimeeventloggertable.util.TableCellBackgroundColorCreator;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventTag;

public class TagColumnLabelProviderToolTipTest
{
    private TagColumnLabelProvider tagColumnLabelProvider;

    @Before
    public void setup()
    {
        TableModel tableModel = Mockito.mock( TableModel.class );
        Mockito.when( tableModel.isBackgroundEnabled() ).thenReturn( true );
        tagColumnLabelProvider = new TagColumnLabelProvider( tableModel,
                                                             Mockito.mock( ImageCreator.class ),
                                                             Mockito.mock( TableCellBackgroundColorCreator.class ) );
    }

    @Test
    public void tagDescriptionIsShownInToolTip() throws Exception
    {
        RuntimeEvent<?> runtimeEvent = Mockito.mock( RuntimeEvent.class );
        Mockito.when( runtimeEvent.getTag() ).thenReturn( RuntimeEventTag.WARNING );
        Mockito.when( runtimeEvent.getTagDescription() ).thenReturn( "Wrong API used." );

        Assert.assertEquals( "WARNING: Wrong API used.", tagColumnLabelProvider.getToolTipText( runtimeEvent ) );
    }

    @Test
    public void warningToolTipIsShown() throws Exception
    {
        RuntimeEvent<?> runtimeEvent = Mockito.mock( RuntimeEvent.class );
        Mockito.when( runtimeEvent.getTag() ).thenReturn( RuntimeEventTag.WARNING );
        Mockito.when( runtimeEvent.getTagDescription() ).thenReturn( "" );

        Assert.assertEquals( "WARNING", tagColumnLabelProvider.getToolTipText( runtimeEvent ) );
    }

    @Test
    public void errorToolTipIsShown() throws Exception
    {
        RuntimeEvent<?> runtimeEvent = Mockito.mock( RuntimeEvent.class );
        Mockito.when( runtimeEvent.getTag() ).thenReturn( RuntimeEventTag.ERROR );
        Mockito.when( runtimeEvent.getTagDescription() ).thenReturn( "" );

        Assert.assertEquals( "ERROR", tagColumnLabelProvider.getToolTipText( runtimeEvent ) );
    }

    @Test
    public void customToolTipIsShown() throws Exception
    {
        RuntimeEvent<?> runtimeEvent = Mockito.mock( RuntimeEvent.class );
        Mockito.when( runtimeEvent.getTag() ).thenReturn( new RuntimeEventTag( "CustomTag" ) );
        Mockito.when( runtimeEvent.getTagDescription() ).thenReturn( "" );

        Assert.assertEquals( "CustomTag", tagColumnLabelProvider.getToolTipText( runtimeEvent ) );
    }

}
