/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.snapshot.editor;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import com.elektrobit.ebrace.common.time.format.TimeFormatter;
import com.elektrobit.ebrace.core.interactor.api.allChannels.AllChannelsNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.allChannels.AllChannelsNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.channelcolor.ChannelColorCallback;
import com.elektrobit.ebrace.core.interactor.api.channelcolor.ChannelColorUseCase;
import com.elektrobit.ebrace.core.interactor.api.channelvalues.ChannelsSnapshotNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.channelvalues.ChannelsSnapshotNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.channelvalues.SortColumn;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.preferences.PreferencesNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.preferences.PreferencesNotifyUseCase;
import com.elektrobit.ebrace.resources.api.model.BaseResourceModel;
import com.elektrobit.ebrace.viewer.common.provider.ChannelColumnLabelProvider;
import com.elektrobit.ebrace.viewer.common.provider.ChannelValueProvider;
import com.elektrobit.ebsolys.core.targetdata.api.color.SColor;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedRuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.viewer.dbus.decoder.providers.DecodedValueLabelProvider;
import com.elektrobit.viewer.dbus.decoder.swt.MessageDecoderComposite;

public class ChannelsSnapshotDecoderComposite extends MessageDecoderComposite
        implements
            PreferencesNotifyCallback,
            ChannelsSnapshotNotifyCallback,
            ChannelColorCallback,
            AllChannelsNotifyCallback
{
    private final Color GRAY_RACE_COLOR;
    private final double value = 0.5;

    private TimeFormatter formatter;
    private TreeColumn channelNameColumn;
    private TreeColumn valueColumn;
    private PreferencesNotifyUseCase makePreferencesNotifyUseCase;
    private ChannelsSnapshotNotifyUseCase channelValuesNotifyUseCase;
    private ChannelColorUseCase channelColorUseCase;
    private AllChannelsNotifyUseCase channelsNotifyUseCase;

    private long lastTimestamp;
    private final boolean showSearchAndDecoder;
    BaseResourceModel model;
    private final ResourceManager resourceManager;
    private final SortColumn sortColumn = SortColumn.CHANNEL_ASC;

    public ChannelsSnapshotDecoderComposite(Composite parent, int style)
    {
        this( parent, style, true );
    }

    public ChannelsSnapshotDecoderComposite(Composite parent, int style, boolean showSearchAndDecoder)
    {
        super( parent, style, showSearchAndDecoder );
        makeUseCases();
        this.showSearchAndDecoder = showSearchAndDecoder;
        resourceManager = new LocalResourceManager( JFaceResources.getResources(), parent );
        GRAY_RACE_COLOR = resourceManager.createColor( new RGB( 155, 155, 155 ) );
        registerListeners();
    }

    private void makeUseCases()
    {
        makePreferencesNotifyUseCase = UseCaseFactoryInstance.get().makePreferencesNotifyUseCase( this );
        channelValuesNotifyUseCase = UseCaseFactoryInstance.get().makeChannelsSnapshotNotifyUseCase( this );
        channelColorUseCase = UseCaseFactoryInstance.get().makeChannelColorUseCase( this );
        channelsNotifyUseCase = UseCaseFactoryInstance.get().makeAllChannelsNotifyUseCase( this );
    }

    private void registerListeners()
    {
        registerColorPickerListener();
        registerColumnSizeListener();
        registerCustomSelectionAndMouseHoverListener();
        paintColorsOfChannelsListener();
    }

    @Override
    protected void createColumns()
    {
        createChannelNameColumn();
        createValueColumn();
    }

    @Override
    public void setSize(int width, int height)
    {
        super.setSize( width, height );
    }

    private void createValueColumn()
    {
        valueColumn = this.filteredTree.createColumn( "Value", new DecodedValueLabelProvider()
        {
            @Override
            public Color getBackground(Object element)
            {
                return null;
            }
        } ).getColumn();
        registerValueColumnSelectionListener();
    }

    private void registerValueColumnSelectionListener()
    {
        valueColumn.addListener( SWT.Selection, new Listener()
        {
            @Override
            public void handleEvent(Event event)
            {
                if (!checkIfChannelsHaveSameType())
                {
                    showCannotSortDialog();
                    return;
                }
                setSortingColumnAndDirection( valueColumn );

                if (valueColumn.getParent().getSortDirection() == SWT.UP)
                {
                    channelValuesNotifyUseCase.setSorting( SortColumn.VALUE_ASC );
                }
                else
                {
                    channelValuesNotifyUseCase.setSorting( SortColumn.VALUE_DESC );
                }
            }
        } );
    }

    private void createChannelNameColumn()
    {
        channelNameColumn = this.filteredTree.createColumn( "Channel", new ChannelColumnLabelProvider() ).getColumn();
        setSortingColumnAndDirection( channelNameColumn );
        registerChannelColumnSelectionListener();
    }

    private void registerChannelColumnSelectionListener()
    {
        channelNameColumn.addListener( SWT.Selection, new Listener()
        {
            @Override
            public void handleEvent(Event event)
            {
                setSortingColumnAndDirection( channelNameColumn );

                if (channelNameColumn.getParent().getSortDirection() == SWT.UP)
                {
                    channelValuesNotifyUseCase.setSorting( SortColumn.CHANNEL_ASC );
                }
                else
                {
                    channelValuesNotifyUseCase.setSorting( SortColumn.CHANNEL_DESC );
                }

            }
        } );
    }

    private void registerCustomSelectionAndMouseHoverListener()
    {
        filteredTree.getViewer().getTree().addListener( SWT.EraseItem, new Listener()
        {
            @Override
            public void handleEvent(Event event)
            {
                GC gc = event.gc;
                int clientWidth = filteredTree.getViewer().getTree().getClientArea().width;
                Rectangle areaToDrawSelection = new Rectangle( 0, event.y, clientWidth, event.height );

                if ((event.detail & SWT.SELECTED) == 0)
                {
                    return; /* item not selected, just mouse hover */
                }

                gc.setBackground( Display.getDefault().getSystemColor( SWT.COLOR_BLACK ) );
                gc.drawRectangle( areaToDrawSelection );

                gc.setBackground( Display.getDefault().getSystemColor( SWT.COLOR_GRAY ) );
                gc.setAlpha( 150 );
                gc.fillRectangle( areaToDrawSelection );

                event.detail &= ~SWT.SELECTED;
            }
        } );
    }

    private void paintColorsOfChannelsListener()
    {
        filteredTree.getViewer().getTree().addListener( SWT.PaintItem, new Listener()
        {
            @Override
            public void handleEvent(Event event)
            {
                GC gc = event.gc;
                TreeItem item = (TreeItem)event.item;
                if (item.getData() instanceof ChannelValueProvider)
                {
                    ChannelValueProvider channelValueProvider = (ChannelValueProvider)item.getData();
                    RuntimeEventChannel<?> channel = channelValueProvider.getRuntimeEventChannel();
                    SColor channelColor = channelColorUseCase.getColorOfChannel( channel );
                    Color color = resourceManager.createColor( new RGB( channelColor.getRed(),
                                                                        channelColor.getGreen(),
                                                                        channelColor.getBlue() ) );
                    gc.setBackground( color );
                    setBackgroundForDisabledChannels( gc, item, channel, color );
                    gc.fillRectangle( 0, event.y, 10, event.height );
                }
            }
        } );
    }

    private void setBackgroundForDisabledChannels(GC gc, TreeItem item, RuntimeEventChannel<?> channel,
            Color channelColor)
    {
        if (model.getDisabledChannels().contains( channel ))
        {
            item.setForeground( getDisplay().getSystemColor( SWT.COLOR_GRAY ) );

            int grayedRed = createGrayedColorCoefficient( GRAY_RACE_COLOR.getRed(), channelColor.getRed() );
            int grayedGreen = createGrayedColorCoefficient( GRAY_RACE_COLOR.getGreen(), channelColor.getGreen() );
            int grayedBlue = createGrayedColorCoefficient( GRAY_RACE_COLOR.getBlue(), channelColor.getBlue() );
            Color color = resourceManager.createColor( new RGB( grayedRed, grayedGreen, grayedBlue ) );
            gc.setBackground( color );
        }
    }

    private int createGrayedColorCoefficient(int grayColor, int channelColor)
    {
        int color = grayColor - channelColor;
        return (int)(channelColor + value * color);
    }

    private void registerColumnSizeListener()
    {
        filteredTree.addControlListener( new ControlListener()
        {

            @Override
            public void controlResized(ControlEvent e)
            {
                resizeColumns();
            }

            @Override
            public void controlMoved(ControlEvent e)
            {
            }
        } );
    }

    private void resizeColumns()
    {
        Rectangle clientArea = filteredTree.getClientArea();
        int channelColumnWidth = (int)(clientArea.width * 0.75);
        filteredTree.getViewer().getTree().getColumn( 0 ).setWidth( (channelColumnWidth) );
        filteredTree.getViewer().getTree().getColumn( 1 ).setWidth( (int)(channelColumnWidth * 0.25) );
    }

    private void registerColorPickerListener()
    {
        filteredTree.getViewer().getTree()
                .addMouseListener( new ColorPickerMouseListener( filteredTree.getViewer().getTree(),
                                                                 resourceManager,
                                                                 channelColorUseCase ) );
    }

    private void setSortingColumnAndDirection(final TreeColumn column)
    {
        Tree table = column.getParent();
        if (column.equals( table.getSortColumn() ))
        {
            table.setSortDirection( table.getSortDirection() == SWT.UP ? SWT.DOWN : SWT.UP );
        }
        else
        {
            table.setSortColumn( column );
            table.setSortDirection( SWT.UP );
        }
    }

    private boolean checkIfChannelsHaveSameType()
    {
        List<RuntimeEventChannel<?>> channels = model.getChannels();
        if (!channels.isEmpty())
        {
            Class<?> type = channels.get( 0 ).getUnit().getDataType();
            if (Number.class.isAssignableFrom( type ))
            {
                type = Number.class;
            }
            for (RuntimeEventChannel<?> channel : channels)
            {
                if (!type.isAssignableFrom( channel.getUnit().getDataType() ))
                {
                    return false;
                }
            }
        }
        return true;
    }

    private void showCannotSortDialog()
    {
        MessageBox box = new MessageBox( new Shell(), SWT.ICON_WARNING | SWT.OK );
        box.setMessage( "Cannot sort values with mixed data types." );
        box.setText( "Sorting not possible" );
        box.open();
    }

    public void setResourceModel(BaseResourceModel model)
    {
        this.model = model;
        if (filteredTree.getViewer().getTree().getSortColumn().equals( valueColumn ))
        {
            if (!checkIfChannelsHaveSameType())
            {
                showCannotSortDialog();
                setSortingColumnAndDirection( channelNameColumn );
                channelValuesNotifyUseCase.setSorting( SortColumn.CHANNEL_ASC );
            }
        }
        channelValuesNotifyUseCase.register( model );
        channelValuesNotifyUseCase.setSorting( sortColumn );
    }

    private void setTimestampInSummaryTextField(long timestamp)
    {
        if (formatter != null)
        {
            headerTextField.setText( formatter.formatMicros( timestamp ) );
        }
        this.lastTimestamp = timestamp;
    }

    @Override
    public void dispose()
    {
        makePreferencesNotifyUseCase.unregister();
        channelValuesNotifyUseCase.unregister();
        channelColorUseCase.unregister();
        channelsNotifyUseCase.unregister();
        resourceManager.dispose();
        super.dispose();
    }

    @Override
    public void onTimestampFormatChanged(String timestampFormatPreferences)
    {
        if (showSearchAndDecoder)
        {
            formatter = new TimeFormatter( timestampFormatPreferences );
            headerTextField.setText( formatter.formatMicros( lastTimestamp ) );
        }
    }

    @Override
    public void onNewInput(Map<RuntimeEventChannel<?>, DecodedRuntimeEvent> events, long timestamp)
    {
        if (!filteredTree.isDisposed())
        {
            filteredTree.setInput( events );
        }
        if (showSearchAndDecoder)
        {
            setTimestampInSummaryTextField( timestamp );
        }
    }

    @Override
    public void onColorAssigned(Collection<RuntimeEventChannel<?>> channels)
    {
        if (!filteredTree.isDisposed())
        {
            filteredTree.getViewer().refresh();
        }
    }

    public TreeViewer getTreeViewerOfSnapshot()
    {
        return this.filteredTree.getViewer();
    }

    @Override
    public void onAllChannelsChanged(List<RuntimeEventChannel<?>> allChannels)
    {

    }

    @Override
    public void onChannelRemoved(RuntimeEventChannel<?> channel)
    {
        List<RuntimeEventChannel<?>> channels = model.getChannels();
        channels.remove( channel );
        model.setChannels( channels );
    }
}
