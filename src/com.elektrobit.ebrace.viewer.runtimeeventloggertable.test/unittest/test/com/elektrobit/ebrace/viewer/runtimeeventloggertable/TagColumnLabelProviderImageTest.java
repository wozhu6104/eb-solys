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

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.core.interactor.api.resources.model.table.TableModel;
import com.elektrobit.ebrace.viewer.common.ImageCreator;
import com.elektrobit.ebrace.viewer.runtimeeventloggertable.RuntimeEventLoggerTableUIPlugin;
import com.elektrobit.ebrace.viewer.runtimeeventloggertable.labelprovider.TagColumnLabelProvider;
import com.elektrobit.ebrace.viewer.runtimeeventloggertable.labelprovider.TagDrawData;
import com.elektrobit.ebrace.viewer.runtimeeventloggertable.util.TableCellBackgroundColorCreator;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventTag;

public class TagColumnLabelProviderImageTest
{
    private TagColumnLabelProvider tagColumnLabelProvider;
    private Color color;
    private Image image;
    private TableCellBackgroundColorCreator colorCreator;
    private TableModel tableModel;

    @Before
    public void setup()
    {
        color = new Color( null, 255, 255, 255 );
        image = new Image( null, new Rectangle( 0, 0, 5, 5 ) );

        tableModel = Mockito.mock( TableModel.class );
        Mockito.when( tableModel.isBackgroundEnabled() ).thenReturn( true );

        colorCreator = Mockito.mock( TableCellBackgroundColorCreator.class );
        Mockito.when( colorCreator.getBackground( Mockito.eq( tableModel ), Mockito.anyObject() ) ).thenReturn( color );

    }

    @Test
    public void warningTagDrawDataCorrect() throws Exception
    {
        RuntimeEvent<?> runtimeEvent = Mockito.mock( RuntimeEvent.class );
        Mockito.when( runtimeEvent.getTag() ).thenReturn( RuntimeEventTag.WARNING );

        ImageCreator imageCreator = Mockito.mock( ImageCreator.class );
        Mockito.when( imageCreator.createImage( RuntimeEventLoggerTableUIPlugin.PLUGIN_ID,
                                                TagColumnLabelProvider.WARNING_ICON_PATH ) )
                .thenReturn( image );

        tagColumnLabelProvider = new TagColumnLabelProvider( tableModel, imageCreator, colorCreator );

        Assert.assertEquals( new TagDrawData( color, image ), tagColumnLabelProvider.getTagDrawData( runtimeEvent ) );
    }

    @Test
    public void errorTagDrawDataCorrect() throws Exception
    {
        RuntimeEvent<?> runtimeEvent = Mockito.mock( RuntimeEvent.class );
        Mockito.when( runtimeEvent.getTag() ).thenReturn( RuntimeEventTag.ERROR );

        ImageCreator imageCreator = Mockito.mock( ImageCreator.class );
        Mockito.when( imageCreator.createImage( RuntimeEventLoggerTableUIPlugin.PLUGIN_ID,
                                                TagColumnLabelProvider.ERROR_ICON_PATH ) )
                .thenReturn( image );

        tagColumnLabelProvider = new TagColumnLabelProvider( tableModel, imageCreator, colorCreator );

        Assert.assertEquals( new TagDrawData( color, image ), tagColumnLabelProvider.getTagDrawData( runtimeEvent ) );
    }

    @Test
    public void customTagDrawDataCorrect() throws Exception
    {
        RuntimeEvent<?> runtimeEvent = Mockito.mock( RuntimeEvent.class );
        Mockito.when( runtimeEvent.getTag() ).thenReturn( new RuntimeEventTag( "MyTag" ) );

        ImageCreator imageCreator = Mockito.mock( ImageCreator.class );
        Mockito.when( imageCreator.createImage( RuntimeEventLoggerTableUIPlugin.PLUGIN_ID, "icons/default_tag.gif" ) )
                .thenReturn( image );

        tagColumnLabelProvider = new TagColumnLabelProvider( tableModel, imageCreator, colorCreator );

        Assert.assertEquals( new TagDrawData( color, image ), tagColumnLabelProvider.getTagDrawData( runtimeEvent ) );
    }

    @After
    public void cleanup()
    {
        color.dispose();
        image.dispose();
    }

}
