/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.runtimeeventloggertable.labelprovider;

import org.eclipse.jface.viewers.OwnerDrawLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;

import com.elektrobit.ebrace.core.interactor.api.resources.model.table.TableModel;
import com.elektrobit.ebrace.viewer.common.ImageCreator;
import com.elektrobit.ebrace.viewer.runtimeeventloggertable.RuntimeEventLoggerTableUIPlugin;
import com.elektrobit.ebrace.viewer.runtimeeventloggertable.util.TableCellBackgroundColorCreator;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventTag;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;

public class TagColumnLabelProvider extends OwnerDrawLabelProvider
{
    public static final String ERROR_ICON_PATH = "icons/error.png";
    public static final String WARNING_ICON_PATH = "icons/warning.png";
    private static final String DEFAULT_ICON_PATH = "icons/default_tag.gif";
    private static final String IMAGE_PLACEHOLDER_NAME = "com.elektrobit.ebrace.viewer.runtimeeventloggertable.editor.TagColumnLabelProvider.IMAGEPLACEHOLDER";

    private static final int CELL_WIDTH_IN_PX = 16;
    private static final int CELL_HEIGHT_IN_PX = 18;
    private static final int ICON_POSITION_VERTICAL_OFFSET = 1; // (CELL_HEIGHT-ICON_HEIGHT)/2 to center the icon
                                                                // vertically

    public final Image warningImage;
    public final Image errorImage;
    public final Image defaultImage;
    private final TableModel model;
    private final TableCellBackgroundColorCreator tableCellBackgroundHelper;
    private Image imagePlaceholder;

    public TagColumnLabelProvider(TableModel model, ImageCreator imageCreator,
            TableCellBackgroundColorCreator tableCellBackgroundHelper)
    {
        this.model = model;
        this.tableCellBackgroundHelper = tableCellBackgroundHelper;
        this.warningImage = imageCreator.createImage( RuntimeEventLoggerTableUIPlugin.PLUGIN_ID, WARNING_ICON_PATH );
        this.errorImage = imageCreator.createImage( RuntimeEventLoggerTableUIPlugin.PLUGIN_ID, ERROR_ICON_PATH );
        this.defaultImage = imageCreator.createImage( RuntimeEventLoggerTableUIPlugin.PLUGIN_ID, DEFAULT_ICON_PATH );
    }

    @Override
    public String getToolTipText(Object element)
    {
        String toolTip = null;
        if (element instanceof RuntimeEvent<?>)
        {
            RuntimeEvent<?> event = (RuntimeEvent<?>)element;
            if (event.getTag() != null)
            {
                toolTip = event.getTag().getTagName();
            }
            if (event.getTagDescription() != null && !event.getTagDescription().isEmpty())
            {
                toolTip += ": " + event.getTagDescription();
            }
        }
        return toolTip;
    }

    @Override
    public boolean useNativeToolTip(Object object)
    {
        return true;
    }

    private Image getImage(RuntimeEventTag tag)
    {
        Image image = null;
        if (tag != null)
        {
            if (tag.equals( RuntimeEventTag.WARNING ))
            {
                image = warningImage;
            }
            else if (tag.equals( RuntimeEventTag.ERROR ))
            {
                image = errorImage;
            }
            else
            {
                image = defaultImage;
            }
        }
        return image;
    }

    @Override
    public void paint(Event event, Object element)
    {
        TagDrawData data = getTagDrawData( element );
        if (data != null)
        {
            Rectangle cellBounds = event.getBounds();
            cellBounds.width = CELL_WIDTH_IN_PX;
            cellBounds.height = CELL_HEIGHT_IN_PX;

            // This paint method is called after Selection or MouseHover paint.
            // That's why we must store the current cell as image,
            // paint the tag image, and paint the stored image afterwards.
            if (isMouseHover( event ) || isSelected( event ))
            {
                if (data.getImage() != null)
                {
                    storeCellAsImage( event.gc, cellBounds );
                    drawTagImageIfNeeded( event.gc, cellBounds, data );
                    drawStoredImage( event.gc, cellBounds, imagePlaceholder );
                }
            }
            else
            {
                drawBackgroundColorIfNeeded( event.gc, cellBounds, data );
                drawTagImageIfNeeded( event.gc, cellBounds, data );
            }
        }
    }

    public TagDrawData getTagDrawData(Object element)
    {
        TagDrawData result = null;
        if (element instanceof RuntimeEvent<?>)
        {
            final RuntimeEventTag tag = ((RuntimeEvent<?>)element).getTag();
            final Color backgroundColor = tableCellBackgroundHelper.getBackground( model, element );
            final Image image = getImage( tag );

            result = new TagDrawData( backgroundColor, image );
        }
        else if (element instanceof TimeMarker)
        {
            final Color backgroundColor = tableCellBackgroundHelper.getBackground( model, element );
            result = new TagDrawData( backgroundColor, null );
        }
        return result;
    }

    private boolean isSelected(Event event)
    {
        return (event.detail & SWT.SELECTED) != 0;
    }

    private boolean isMouseHover(Event event)
    {
        return (event.detail & SWT.MouseHover) != 0;
    }

    private void storeCellAsImage(GC gc, Rectangle rec)
    {
        createImagePlaceholderIfNeeded( gc, IMAGE_PLACEHOLDER_NAME );
        gc.copyArea( imagePlaceholder, rec.x, rec.y );
    }

    private void createImagePlaceholderIfNeeded(GC gc, final String imageName)
    {
        if (imagePlaceholder == null)
        {
            imagePlaceholder = new Image( gc.getDevice(), CELL_WIDTH_IN_PX, CELL_HEIGHT_IN_PX );
        }
    }

    private void drawTagImageIfNeeded(GC gc, Rectangle rec, TagDrawData data)
    {
        if (data.getImage() != null)
        {
            gc.drawImage( data.getImage(), rec.x, rec.y + ICON_POSITION_VERTICAL_OFFSET );
        }
    }

    private void drawStoredImage(GC gc, Rectangle rec, Image image)
    {
        gc.setAlpha( 0 );
        gc.drawImage( image, rec.x, rec.y );
    }

    private void drawBackgroundColorIfNeeded(GC gc, Rectangle bounds, TagDrawData data)
    {
        if (data.getBackgroundColor() != null)
        {
            gc.setBackground( data.getBackgroundColor() );
            gc.fillRectangle( bounds );
        }
    }

    @Override
    protected void measure(Event event, Object element)
    {
    }

    @Override
    protected void erase(Event event, Object element)
    {
    }

    @Override
    public void dispose()
    {
        if (imagePlaceholder != null)
        {
            imagePlaceholder.dispose();
        }
    }
}
