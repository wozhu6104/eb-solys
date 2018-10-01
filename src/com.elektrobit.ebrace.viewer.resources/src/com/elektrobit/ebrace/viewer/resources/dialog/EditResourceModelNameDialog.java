/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.resources.dialog;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.createresource.CreateResourceInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.createresource.CreateResourceInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.viewer.common.dialog.BaseSaveSettingsDialog;

public abstract class EditResourceModelNameDialog extends BaseSaveSettingsDialog
        implements
            CreateResourceInteractionCallback
{

    protected ResourceModel resourceModel;
    private boolean okButtonFlag = false;
    protected CreateResourceInteractionUseCase createResourceUseCase;

    public EditResourceModelNameDialog(Shell parentShell, ResourceModel modelToEdit)
    {
        super( parentShell );
        resourceModel = modelToEdit;
    }

    protected abstract String getResourceModelSimpleName();

    private void setOkButtonFlagToTrue()
    {
        okButtonFlag = true;
    }

    protected void setOkButtonFlagToFalse()
    {
        this.okButtonFlag = false;
    }

    @Override
    protected Control createContents(Composite parent)
    {
        Composite container = new Composite( parent, SWT.BORDER );
        container.setLayout( new GridLayout() );
        container.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
        super.createContents( container );
        Composite content = new Composite( (Composite)getDialogArea(), SWT.NONE );
        content.setLayout( new GridLayout( 2, false ) );
        content.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false ) );
        setTitleMessage();
        validateFields();
        createResourceUseCase = UseCaseFactoryInstance.get().makeCreateResourceUseCase( this );
        return container;
    }

    private void setTitleMessage()
    {
        setTitle( "Select channels for " + getResourceModelSimpleName() );
        setMessage( resourceModel.getName() );
        getShell().setText( "Select channels" );
    }

    protected void validateFields()
    {
        validateResourceNameTextField();
        checkOkButton();
    }

    protected void checkOkButton()
    {
        if (getButton( IDialogConstants.OK_ID ).isEnabled() != okButtonFlag)
        {
            getButton( IDialogConstants.OK_ID ).setEnabled( okButtonFlag );
        }
    }

    protected void validateResourceNameTextField()
    {
        setOkButtonFlagToTrue();
        setErrorMessage( null );
    }

    @Override
    protected void okPressed()
    {
        changeResourceModel();
        super.okPressed();
    }

    protected abstract void changeResourceModel();

    @Override
    public void onChartChannelsTypeMismatch()
    {
    }

    @Override
    public void onDerivedResourceAlreadyExists()
    {
    }
}
