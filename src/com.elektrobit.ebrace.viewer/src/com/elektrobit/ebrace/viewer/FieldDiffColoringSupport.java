/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer;

import java.util.HashMap;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableItem;

public class FieldDiffColoringSupport implements Listener
{

    private final TableViewer m_table;
    private final Color whiteColor = new Color( Display.getDefault(), 255, 255, 255 );
    private final Color greenColor = new Color( Display.getDefault(), 180, 255, 180 );
    private final Color redColor = new Color( Display.getDefault(), 255, 180, 180 );
    private final Color blueColor = new Color( Display.getDefault(), 180, 180, 255 );

    public FieldDiffColoringSupport(TableViewer table)
    {
        m_table = table;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void handleEvent(Event event)
    {
        Point pt = new Point( event.x, event.y );
        TableItem item = m_table.getTable().getItem( pt );

        if (item == null)
        {
            return;
        }

        resetBGColor();

        int currentIndex = m_table.getTable().indexOf( item );

        if (m_table.getTable().isSelected( currentIndex ))
        {
            for (int i = 1; i < m_table.getTable().getColumnCount(); i++)
            {
                Rectangle rect = item.getBounds( i );
                if (rect.contains( pt ))
                {
                    HashMap<String, PropertyElement> map = (HashMap<String, PropertyElement>)item.getData();
                    if (map == null)
                    {
                        return;
                    }

                    String filter = item.getText( i );
                    coloringFields( filter, map );
                    String text = m_table.getTable().getColumn( i ).getText();
                    PropertyElement propertyElement = map.get( text );
                    if (propertyElement != null)
                    {
                        propertyElement.setBackgroundColor( blueColor );
                    }
                    break;
                }
            }
        }

        m_table.refresh();
    }

    @SuppressWarnings("unchecked")
    private void resetBGColor()
    {
        TableItem[] items = m_table.getTable().getItems();

        for (int i = 0; i < items.length; i++)
        {
            if (!m_table.getTable().isSelected( i ))
            {
                TableItem item = items[i];
                HashMap<String, PropertyElement> map = (HashMap<String, PropertyElement>)item.getData();
                for (int j = 0; j < m_table.getTable().getColumnCount(); j++)
                {
                    String text = m_table.getTable().getColumn( j ).getText();
                    PropertyElement propertyElement = map.get( text );
                    if (propertyElement != null)
                    {
                        propertyElement.setBackgroundColor( whiteColor );
                    }
                }
            }
        }
    }

    private void coloringFields(String filter, HashMap<String, PropertyElement> map)
    {
        for (String key : map.keySet())
        {
            if (key.compareTo( "PropertyName" ) == 0)
            {
                continue;
            }

            if (map.get( key ).getValue().toLowerCase().compareTo( filter.toLowerCase() ) == 0)
            {
                map.get( key ).setBackgroundColor( greenColor );
            }
            else
            {
                map.get( key ).setBackgroundColor( redColor );
            }
        }
    }
}
