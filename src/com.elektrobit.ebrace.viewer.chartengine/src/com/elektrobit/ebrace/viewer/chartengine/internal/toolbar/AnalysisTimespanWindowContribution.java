/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.chartengine.internal.toolbar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;

import com.elektrobit.ebrace.core.interactor.api.analysisTimespan.AnalysisTimespanInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.analysisTimespan.AnalysisTimespanInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.chartdata.AnalysisTimespanNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.chartdata.AnalysisTimespanNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;

public class AnalysisTimespanWindowContribution extends WorkbenchWindowControlContribution
        implements
            AnalysisTimespanInteractionCallback,
            AnalysisTimespanNotifyCallback
{
    private static final String SECONDS_UNIT_LABEL = " s";

    private static final int USEC_TO_SEC = 1000000;

    private static final int TEXT_MAX_CHARACTERS = 5;

    private Composite container;

    private AnalysisTimespanInteractionUseCase analysisTimespanInteractionUseCase;

    private Text textField;

    private AnalysisTimespanNotifyUseCase analysisTimespanNotifyUseCase;

    private long currentAnalysistimespanMicros;

    public AnalysisTimespanWindowContribution()
    {

    }

    public AnalysisTimespanWindowContribution(String id)
    {
        super( id );
    }

    @Override
    protected Control createControl(Composite parent)
    {
        container = new Composite( parent, SWT.NONE );
        GridLayout glContainer = new GridLayout( 1, false );
        glContainer.marginTop = 0;
        glContainer.marginHeight = 0;
        glContainer.marginWidth = 0;
        container.setLayout( glContainer );
        createTextField( container );
        registerFieldListener( textField );

        analysisTimespanInteractionUseCase = UseCaseFactoryInstance.get().makeAnalysisTimespanInteractionUseCase( this );
        analysisTimespanNotifyUseCase = UseCaseFactoryInstance.get().makeAnalysisTimespanNotifyUseCase( this );

        return container;
    }

    private void createTextField(Composite parent)
    {
        GridData gridData = new GridData( SWT.FILL, SWT.CENTER, false, true, 1, 1 );
        gridData.widthHint = 25;
        textField = new Text( parent, SWT.RIGHT );
        textField.setTextLimit( TEXT_MAX_CHARACTERS );
        textField.setLayoutData( gridData );
        textField.setToolTipText( "Analysis timespan in seconds" );
    }

    private void registerFieldListener(Text text)
    {
        text.addKeyListener( new KeyListener()
        {

            @Override
            public void keyReleased(KeyEvent e)
            {
            }

            @Override
            public void keyPressed(KeyEvent e)
            {
                if (e.character == SWT.CR || e.character == SWT.LF)
                {
                    onEnterPressed();
                }
            }

        } );
    }

    private void onEnterPressed()
    {
        String currentText = textField.getText();
        analysisTimespanInteractionUseCase.setAnalysisTimespan( currentText );
    }

    @Override
    public void dispose()
    {
        analysisTimespanInteractionUseCase.unregister();
        analysisTimespanNotifyUseCase.unregister();
        super.dispose();
    }

    @Override
    public void onAnalysisTimespanTextInputOutOfRange(int minSeconds, int maxSeconds)
    {
        MessageBox box = new MessageBox( container.getShell(), SWT.ICON_INFORMATION );
        box.setMessage( "Analysis timespan length has to be in range " + minSeconds + "s-" + maxSeconds + "s." );
        box.setText( "Value Out of Range" );
        box.open();
    }

    @Override
    public void onAnalysisTimespanTextInputInvalidFormat()
    {
        String text = getFieldText( currentAnalysistimespanMicros );
        textField.setText( text );

        MessageBox box = new MessageBox( container.getShell(), SWT.ICON_INFORMATION );
        box.setMessage( "Entered value has an invalid format." );
        box.setText( "Format Invalid" );
        box.open();
    }

    @Override
    public void onAnalysisTimespanChanged(long analysisTimespanStart, long analysisTimespanEnd)
    {
    }

    @Override
    public void onAnalysisTimespanLengthChanged(long analysistimespanMicros)
    {
        currentAnalysistimespanMicros = analysistimespanMicros;
        String text = getFieldText( analysistimespanMicros );
        textField.setText( text );
    }

    private String getFieldText(long timespanMicros)
    {
        timespanMicros = timespanMicros / USEC_TO_SEC;
        return String.valueOf( timespanMicros ) + SECONDS_UNIT_LABEL;
    }

    @Override
    public void onFullTimespanChanged(long start, long end)
    {
    }
}
