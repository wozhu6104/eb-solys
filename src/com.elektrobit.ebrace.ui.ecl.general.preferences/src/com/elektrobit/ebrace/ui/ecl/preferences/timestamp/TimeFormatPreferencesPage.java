/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.ui.ecl.preferences.timestamp;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.elektrobit.ebrace.common.time.format.TimeFormatter;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.preferences.PreferencesNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.preferences.PreferencesNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.preferences.SetPreferencesInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.preferences.SetTimestampPreferencesInteractionUseCase;

public class TimeFormatPreferencesPage extends PreferencePage
        implements
            IWorkbenchPreferencePage,
            SetPreferencesInteractionCallback,
            PreferencesNotifyCallback
{
    private static final String CUSTOM_FORMAT_HINT = "e.g. HH:mm:ss EEE 'text'";
    private SetTimestampPreferencesInteractionUseCase setTimestampPreferencesInteractionUseCase;
    private PreferencesNotifyUseCase preferencesNotifyUseCase;
    private Button time_milliseconds;
    private Button timestamp_milliseconds;
    private Button timestamp_microseconds;
    private Button time_microseconds;
    private Button date_milliseconds;
    private Button date_microseconds;
    private Button custom_format;
    private String pattern;
    private Text custom_timestamp_format;
    private final List<Button> radioButtons = new ArrayList<Button>();

    @Override
    public void init(IWorkbench workbench)
    {
        setTimestampPreferencesInteractionUseCase = UseCaseFactoryInstance.get()
                .makeSetTimestampPreferencesInteractionUseCase( this );
    }

    @Override
    public void dispose()
    {
        preferencesNotifyUseCase.unregister();
        super.dispose();
    }

    @Override
    protected Control createContents(Composite parent)
    {
        Composite content = new Composite( parent, SWT.NONE );
        content.setLayout( new GridLayout() );
        new Label( content, SWT.NONE ).setText( TimestampFormatPreferencesConstants.TIMESTAMP_PREFERENCES_PAGE_LABEL );
        Composite space = new Composite( content, SWT.NONE );
        space.setLayoutData( new GridData( 0, 10 ) );
        createRadioButtons( content );
        createCustomDateFormatPart( content );
        createInformationLink( content );
        preferencesNotifyUseCase = UseCaseFactoryInstance.get().makePreferencesNotifyUseCase( this );
        return content;
    }

    private void createRadioButtons(Composite parent)
    {
        timestamp_milliseconds = new Button( parent, SWT.RADIO );
        timestamp_milliseconds.setText( TimestampFormatPreferencesConstants.TIMESTAMP_MILLISECONDS_LABEL );
        timestamp_milliseconds
                .addSelectionListener( new TimestampFormatSelectionListener( TimeFormatter.TIMESTAMP_MILLISECONDS,
                                                                             this ) );
        radioButtons.add( timestamp_milliseconds );

        timestamp_microseconds = new Button( parent, SWT.RADIO );
        timestamp_microseconds.setText( TimestampFormatPreferencesConstants.TIMESTAMP_MICROSECONDS_LABEL );
        timestamp_microseconds
                .addSelectionListener( new TimestampFormatSelectionListener( TimeFormatter.TIMESTAMP_MICROSECONDS,
                                                                             this ) );
        radioButtons.add( timestamp_microseconds );

        time_milliseconds = new Button( parent, SWT.RADIO );
        time_milliseconds.setText( TimestampFormatPreferencesConstants.TIME_MILLISECONDS_LABEL );
        time_milliseconds
                .addSelectionListener( new TimestampFormatSelectionListener( TimestampFormatPreferencesConstants.TIME_MILLISECONDS,
                                                                             this ) );
        radioButtons.add( time_milliseconds );

        time_microseconds = new Button( parent, SWT.RADIO );
        time_microseconds.setText( TimestampFormatPreferencesConstants.TIME_MICROSECONDS_LABEL );
        time_microseconds
                .addSelectionListener( new TimestampFormatSelectionListener( TimestampFormatPreferencesConstants.TIME_MICROSECONDS,
                                                                             this ) );
        radioButtons.add( time_microseconds );

        date_milliseconds = new Button( parent, SWT.RADIO );
        date_milliseconds.setText( TimestampFormatPreferencesConstants.DATE_MILLISECONDS_LABEL );
        date_milliseconds
                .addSelectionListener( new TimestampFormatSelectionListener( TimestampFormatPreferencesConstants.DATE_MILLISECONDS,
                                                                             this ) );
        radioButtons.add( date_milliseconds );

        date_microseconds = new Button( parent, SWT.RADIO );
        date_microseconds.setText( TimestampFormatPreferencesConstants.DATE_MICROSECONDS_LABEL );
        date_microseconds
                .addSelectionListener( new TimestampFormatSelectionListener( TimestampFormatPreferencesConstants.DATE_MICROSECONDS,
                                                                             this ) );
        radioButtons.add( date_microseconds );
    }

    private void createCustomDateFormatPart(Composite parent)
    {
        custom_format = new Button( parent, SWT.RADIO );
        custom_format.setText( TimestampFormatPreferencesConstants.CUSTOM_TIMESTAMP_FORMAT_LABEL );
        custom_timestamp_format = new Text( parent, SWT.BORDER );
        custom_timestamp_format.setMessage( CUSTOM_FORMAT_HINT );
        custom_timestamp_format.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false ) );
        radioButtons.add( custom_format );

        custom_timestamp_format.addModifyListener( new ModifyListener()
        {

            @Override
            public void modifyText(ModifyEvent e)
            {
                updateApplyButton();
                getContainer().updateButtons();
            }
        } );
        custom_format.addSelectionListener( new TimestampFormatSelectionListener( pattern, this ) );
        custom_timestamp_format.setEnabled( custom_format.getSelection() );
    }

    private void createInformationLink(Composite content)
    {
        Link link = new Link( content, SWT.NONE );
        link.setText( TimestampFormatPreferencesConstants.TIMESTAMP_PREFERENCES_PAGE_INFO_LABEL );
        link.setSize( 400, 100 );
        link.addSelectionListener( new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                try
                {
                    PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL( new URL( e.text ) );
                }
                catch (PartInitException ex)
                {
                    ex.printStackTrace();
                }
                catch (MalformedURLException ex)
                {
                    ex.printStackTrace();
                }
            }
        } );
    }

    @Override
    public void onCustomFormatNotValid()
    {
        setErrorMessage( TimestampFormatPreferencesConstants.TIMESTAMP_FORMAT_ERROR_MESSAGE );
    }

    @Override
    public boolean isValid()
    {
        if (custom_format.getSelection() == true)
        {
            String text = custom_timestamp_format.getText();
            if (setTimestampPreferencesInteractionUseCase.isTimestampFormatValid( text ))
            {
                pattern = text;
                setErrorMessage( null );
                return true;
            }
            else
            {
                onCustomFormatNotValid();
                return false;
            }
        }
        else
        {
            setErrorMessage( null );
            return true;
        }
    }

    @Override
    public void onTimestampPreferencesSaved()
    {
    }

    @Override
    public boolean performOk()
    {
        saveAndStorePreferences();
        return super.performOk();
    }

    @Override
    protected void performApply()
    {
        saveAndStorePreferences();
    }

    private void saveAndStorePreferences()
    {
        setTimestampPreferencesInteractionUseCase.setTimestampFormatPreferences( pattern );
    }

    @Override
    protected void performDefaults()
    {
        resetButtonsAndTextField();
        setTimestampPreferencesInteractionUseCase
                .setTimestampFormatPreferences( TimestampFormatPreferencesConstants.TIME_MILLISECONDS );
        super.performDefaults();
    }

    private void resetButtonsAndTextField()
    {
        for (Button b : radioButtons)
        {
            b.setSelection( false );
        }
        custom_timestamp_format.setText( "" );
    }

    @Override
    public void onTimestampFormatChanged(String timestampFormatPreferences)
    {
        pattern = timestampFormatPreferences;

        if (pattern.equalsIgnoreCase( TimeFormatter.TIMESTAMP_MILLISECONDS ))
        {
            timestamp_milliseconds.setSelection( true );
        }
        else
        {
            if (pattern.equalsIgnoreCase( TimeFormatter.TIMESTAMP_MICROSECONDS ))
            {
                timestamp_microseconds.setSelection( true );
            }
            else
            {
                if (pattern.equalsIgnoreCase( TimestampFormatPreferencesConstants.TIME_MILLISECONDS ))
                {
                    time_milliseconds.setSelection( true );
                }
                else
                {
                    if (pattern.equalsIgnoreCase( TimestampFormatPreferencesConstants.TIME_MICROSECONDS ))
                    {
                        time_microseconds.setSelection( true );
                    }
                    else
                    {
                        if (pattern.equalsIgnoreCase( TimestampFormatPreferencesConstants.DATE_MILLISECONDS ))
                        {
                            date_milliseconds.setSelection( true );
                        }
                        else
                        {
                            if (pattern.equalsIgnoreCase( TimestampFormatPreferencesConstants.DATE_MICROSECONDS ))
                            {
                                date_microseconds.setSelection( true );
                            }
                            else
                            {
                                if (custom_format != null)
                                {
                                    custom_format.setSelection( true );
                                    custom_timestamp_format.setText( pattern );
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void setSelectedOption(String pattern)
    {
        this.pattern = pattern;
    }

    public void updateApply()
    {
        updateApplyButton();
    }

    public void toggleEnabledCustomTimestampFormat()
    {
        custom_timestamp_format.setEnabled( custom_format.getSelection() );
    }
}
