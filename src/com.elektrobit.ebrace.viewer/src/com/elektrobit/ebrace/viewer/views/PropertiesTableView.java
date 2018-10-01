/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.views;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.structure.SelectedStructureCallback;
import com.elektrobit.ebrace.core.interactor.api.structure.SelectedStructureNotifyUseCase;
import com.elektrobit.ebrace.viewer.FieldDiffColoringSupport;
import com.elektrobit.ebrace.viewer.PropertyElement;
import com.elektrobit.ebsolys.core.targetdata.api.Properties;
import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;

/**
 * Shows the Properties of the selected TreeNode nodes. Allows filtering and sorting of columns.
 * 
 * @author joma8221
 * 
 */
public class PropertiesTableView extends ViewPart implements SelectedStructureCallback
{
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger( PropertiesTableView.class );

    private final HashMap<String, HashMap<String, PropertyElement>> m_properties = new HashMap<String, HashMap<String, PropertyElement>>();
    private TableViewer m_tableViewer;
    private PropertyComparator m_comparator;
    private Composite m_parent;

    private SelectedStructureNotifyUseCase selectedStructureNotifyUseCase;

    @Override
    public void createPartControl(Composite parent)
    {
        m_parent = parent;
        Composite rootComposite = new Composite( parent, SWT.NONE );

        rootComposite.setLayout( new GridLayout() );
        rootComposite.setLayoutData( new GridData( GridData.FILL_BOTH ) );

        Composite configComposite = new Composite( rootComposite, SWT.NONE );
        configComposite.setLayout( new RowLayout() );

        createTableViewer( rootComposite );
        createFilterFunctionView( configComposite );

        selectedStructureNotifyUseCase = UseCaseFactoryInstance.get().makeSelectedStructureNotifyUseCase( this );
    }

    private static Map<String, PropertyElement> convertObjectToHashMap(Object e1)
    {
        Map<String, PropertyElement> convertedMap = new HashMap<String, PropertyElement>();
        if (e1 instanceof Map)
        {
            Map<?, ?> objectAsMap = (Map<?, ?>)e1;

            for (Entry<?, ?> nextEntry : objectAsMap.entrySet())
            {
                if (nextEntry.getKey() instanceof String && nextEntry.getValue() instanceof PropertyElement)
                {
                    convertedMap.put( (String)nextEntry.getKey(), (PropertyElement)nextEntry.getValue() );
                }
            }
        }
        return convertedMap;
    }

    @Override
    public void setFocus()
    {
    }

    @Override
    public void dispose()
    {
        selectedStructureNotifyUseCase.unregister();
        super.dispose();
    }

    private void showPropertiesForTreeNodes(List<TreeNode> treeNodes)
    {
        removeColumns();

        createPropertyColumn();
        createColumns( treeNodes );

        Map<String, HashMap<String, PropertyElement>> properties = getProperties( treeNodes );
        m_tableViewer.setInput( properties );
    }

    /**
     * Generate a HashMap of HashMaps - the outer HashMap uses PropertyNames as keys, the inner HashMap uses the
     * NodeName as key and has the property value of this node as value. Used to fill the Table through a
     * ContentProvider
     * 
     * @param selectedNodes
     * @return HashMap<PropertyName, HashMap<NodeName, PropertyValue>>
     */
    private Map<String, HashMap<String, PropertyElement>> getProperties(List<TreeNode> selectedTreeNodes)
    {
        m_properties.clear();

        for (TreeNode node : selectedTreeNodes)
        {
            Properties props = node.getProperties();

            Set<Object> keys = props.getKeys();
            for (Object o : keys)
            {
                if (!m_properties.containsKey( o.toString() ))
                {
                    HashMap<String, PropertyElement> map = new HashMap<String, PropertyElement>();
                    m_properties.put( o.toString(), map );
                }

                if (props.getValue( o ) == null)
                {
                    m_properties.get( o.toString() ).put( node.getName(), new PropertyElement( node.getName(), "" ) );
                }
                else
                {
                    m_properties.get( o.toString() ).put( node.getName(),
                                                          new PropertyElement( node.getName(),
                                                                               props.getValue( o ).toString() ) );
                }

                m_properties.get( o.toString() ).put( "PropertyName",
                                                      new PropertyElement( node.getName(), o.toString() ) );
            }
        }
        return m_properties;
    }

    private void createPropertyColumn()
    {
        TableViewerColumn viewerColumn = createTableViewerColumn( "PropertyName", 150, false );
        viewerColumn.setLabelProvider( new PropertyCellLabelProvider( "PropertyName", m_parent ) );
    }

    private void createColumns(List<TreeNode> treeNodes)
    {

        for (TreeNode node : treeNodes)
        {
            TableViewerColumn column = createTableViewerColumn( node.getName(), 100, true );
            column.setLabelProvider( new PropertyCellLabelProvider( node.getName(), m_parent ) );
        }
    }

    private void removeColumns()
    {
        TableColumn columns[] = m_tableViewer.getTable().getColumns();

        for (int i = 0; i < columns.length; i++)
        {
            columns[i].dispose();
        }
    }

    private TableViewerColumn createTableViewerColumn(final String title, int bound, boolean moveable)
    {
        TableViewerColumn viewerColumn = new TableViewerColumn( m_tableViewer, SWT.CENTER );

        final TableColumn column = viewerColumn.getColumn();
        column.setText( title );
        column.setWidth( bound );
        column.setResizable( true );
        column.setMoveable( moveable );

        column.addSelectionListener( new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {

                m_comparator.setColName( title );

                TableColumn column = (TableColumn)e.getSource();

                int dir = column.getParent().getSortDirection();

                if (m_tableViewer.getTable().getSortColumn() == column)
                {
                    dir = ((dir == SWT.UP) ? SWT.DOWN : SWT.UP);
                }
                else
                {
                    dir = SWT.DOWN;
                }

                column.getParent().setSortDirection( dir );
                column.getParent().setSortColumn( column );
                m_tableViewer.refresh();
            }
        } );
        return viewerColumn;
    }

    private void createFilterFunctionView(Composite composite)
    {
        Label lbl = new Label( composite, SWT.NORMAL );
        lbl.setText( "Filter: " );

        Text filterRowText = new Text( composite, SWT.SEARCH );

        filterRowText.addKeyListener( new SearchFilterKeyListener( this, filterRowText ) );

    }

    private void createTableViewer(Composite composite)
    {
        m_tableViewer = new TableViewer( composite, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION );
        m_tableViewer.getTable().setLayoutData( new GridData( GridData.FILL_BOTH ) );
        m_tableViewer.getTable().setLinesVisible( true );
        m_tableViewer.getTable().setHeaderVisible( true );
        m_tableViewer.getTable().addListener( SWT.MouseDown, new FieldDiffColoringSupport( m_tableViewer ) );

        m_comparator = new PropertyComparator();
        m_tableViewer.setComparator( m_comparator );

        m_tableViewer.setContentProvider( new IStructuredContentProvider()
        {
            @Override
            public void dispose()
            {
            }

            @Override
            public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
            {
            }

            @Override
            public Object[] getElements(Object inputElement)
            {
                HashMap<?, ?> map = ((HashMap<?, ?>)inputElement);
                Object[] objects = new Object[map.size()];
                Set<?> keys = map.keySet();
                Iterator<?> iter = keys.iterator();
                int i = 0;
                while (iter.hasNext())
                {
                    objects[i] = map.get( iter.next() );
                    i++;
                }
                return objects;
            }
        } );
    }

    /**
     * Used to compare the Properties in PropertyTableView. Compares elements from HashMaps where the column name is the
     * key to the elements.
     * 
     * @author joma8221
     * 
     */
    public static class PropertyComparator extends ViewerComparator
    {
        private String m_colname;
        private static final int DESCENDING = 1;
        private int direction = DESCENDING;

        PropertyComparator()
        {
            m_colname = "PropertyName";
        }

        public void setColName(String colname)
        {
            if (m_colname.compareTo( colname ) == 0)
            {
                direction = 1 - direction;
            }
            else
            {
                direction = DESCENDING;
            }
            m_colname = colname;
        }

        @Override
        public int compare(Viewer viewer, Object e1, Object e2)
        {
            int rc = 0;
            Map<String, PropertyElement> map1 = convertObjectToHashMap( e1 );
            Map<String, PropertyElement> map2 = convertObjectToHashMap( e2 );

            if (map1.containsKey( m_colname ) && map2.containsKey( m_colname ))
            {
                rc = map1.get( m_colname ).getValue().compareTo( map2.get( m_colname ).getValue() );
            }
            else if (map1.containsKey( m_colname ))
            {
                rc = 1;
            }
            else if (map2.containsKey( m_colname ))
            {
                rc = -1;
            }

            if (direction == DESCENDING)
            {
                rc = -rc;
            }

            return rc;
        }

    }

    /**
     * Provides cells with label/text from a HashMap. Used if the TableItem is a HashMap with column name as key
     * HashMap<ColumnName, Value/Text/Label>, Updates Label, ForegroundColor, BackgroundColor and Font
     * 
     * @author joma8221
     * 
     */
    public static class PropertyCellLabelProvider extends CellLabelProvider
    {
        private final String m_columnName;

        public PropertyCellLabelProvider(String columnName, Composite parent)
        {
            m_columnName = columnName;
        }

        @Override
        public void update(ViewerCell cell)
        {
            Map<String, PropertyElement> map = convertObjectToHashMap( cell.getElement() );
            PropertyElement element = map.get( m_columnName );
            if (element == null)
            {
                element = new PropertyElement( m_columnName,
                                               "",
                                               new Color( Display.getDefault(), 255, 255, 255 ),
                                               new Color( Display.getDefault(), 0, 0, 0 ),
                                               Display.getDefault().getSystemFont() );
                map.put( m_columnName, element );
            }
            cell.setText( element.getValue() );
            cell.setBackground( element.getBackgroundColor() );
            cell.setForeground( element.getForegroundColor() );
            cell.setFont( element.getFont() );
        }
    }

    public static class ColumnFilter
    {
        private final String m_filter;

        public ColumnFilter(String filterPattern)
        {
            m_filter = filterPattern;
        }

        public boolean isFiltered(TreeNode node, String property)
        {
            Properties props = node.getProperties();
            Object value = props.getValue( property );
            if (value == null)
            {
                return true;
            }
            return !value.toString().toLowerCase().contains( m_filter );
        }
    }

    public static class SearchFilterKeyListener implements KeyListener
    {
        private final PropertiesTableView m_propertiesView;
        private final Text m_filterTextField;
        private final Font m_normalFont;
        private final Font m_markedFont;
        private final Color m_normalColor;
        private final Color m_markedColor;

        public SearchFilterKeyListener(PropertiesTableView propertiesView, Text filterTextField)
        {
            m_propertiesView = propertiesView;
            m_filterTextField = filterTextField;
            m_normalFont = m_filterTextField.getFont();

            FontData[] fontData = m_normalFont.getFontData();
            fontData[0].setStyle( SWT.BOLD );
            m_markedFont = new Font( m_normalFont.getDevice(), fontData );

            m_normalColor = new Color( Display.getDefault(), 0, 0, 0 );
            m_markedColor = new Color( Display.getDefault(), 0, 160, 0 );
        }

        @Override
        public void keyPressed(KeyEvent e)
        {
        }

        @Override
        public void keyReleased(KeyEvent e)
        {
            for (ViewerFilter filter : m_propertiesView.m_tableViewer.getFilters())
            {
                m_propertiesView.m_tableViewer.removeFilter( filter );
            }

            ViewerFilter searchFilter = new ViewerFilter()
            {
                @Override
                public boolean select(Viewer viewer, Object parentElement, Object element)
                {
                    Map<String, PropertyElement> map = convertObjectToHashMap( element );
                    if (map == null || map.size() <= 0)
                    {
                        return false;
                    }

                    boolean isSelected = false;

                    for (String key : map.keySet())
                    {
                        PropertyElement propertyElement = map.get( key );
                        if (m_filterTextField.getText().isEmpty())
                        {
                            propertyElement.setForegroundColor( m_normalColor );
                            propertyElement.setFont( m_normalFont );
                            isSelected = true;
                            continue;
                        }

                        if (propertyElement.getValue().toLowerCase()
                                .contains( m_filterTextField.getText().toLowerCase() ))
                        {
                            propertyElement.setForegroundColor( m_markedColor );
                            propertyElement.setFont( m_markedFont );
                            isSelected = true;
                        }
                        else
                        {
                            propertyElement.setForegroundColor( m_normalColor );
                            propertyElement.setFont( m_normalFont );
                        }

                    }

                    return isSelected;
                }
            };

            m_propertiesView.m_tableViewer.addFilter( searchFilter );
        }
    }

    @Override
    public void onNodesSelected(List<TreeNode> nodes)
    {
        showPropertiesForTreeNodes( nodes );
    }

    @Override
    public void onComRelationsSelected(List<ComRelation> comRelations)
    {
        showPropertiesForTreeNodes( Collections.<TreeNode> emptyList() );
    }

    @Override
    public void onSelectionCleared()
    {
        showPropertiesForTreeNodes( Collections.<TreeNode> emptyList() );
    }
}
