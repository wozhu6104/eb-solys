/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;

public class ImagePreviewDialog extends Dialog
{
    private static final int PREVIEW_IMAGE_FACTOR = 2;
    private Button okButton;
    private final Shell childShell;
    private final Image capturedScreenshot;

    public ImagePreviewDialog(Shell parent, Composite editorComposite)
    {
        super( parent );
        childShell = new Shell( parent );
        RangeCheckUtils.assertReferenceParameterNotNull( "editorComposite", editorComposite );
        capturedScreenshot = captureImage( editorComposite );
    }

    @Override
    protected Control createDialogArea(Composite parent)
    {
        Composite area = (Composite)super.createDialogArea( parent );
        Composite container = new Composite( area, SWT.NONE );
        container.setLayout( new FillLayout() );

        Label labelWithImage = new Label( container, SWT.NONE );
        Image resizedImage = resizeImage( capturedScreenshot,
                                          capturedScreenshot.getImageData().width / PREVIEW_IMAGE_FACTOR,
                                          capturedScreenshot.getImageData().height / 2 );
        labelWithImage.setImage( resizedImage );
        labelWithImage.pack();
        container.pack();
        area.pack();
        parent.pack();

        return container;
    }

    private Image resizeImage(Image image, int width, int height)
    {
        Image scaled = new Image( Display.getDefault(), width, height );
        GC gc = new GC( scaled );
        gc.setAntialias( SWT.ON );
        gc.setInterpolation( SWT.HIGH );
        gc.drawImage( image, 0, 0, image.getImageData().width, image.getImageData().height, 0, 0, width, height );
        gc.dispose();
        return scaled;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent)
    {
        super.createButtonsForButtonBar( parent );
        okButton = getButton( IDialogConstants.OK_ID );
        okButton.setText( "Save" );
        setButtonLayoutData( okButton );
        okButton.setEnabled( true );
    }

    @Override
    protected void configureShell(Shell newShell)
    {
        super.configureShell( newShell );
        newShell.setText( "Screenshot Preview" );
    }

    @Override
    protected boolean isResizable()
    {
        return false;
    }

    @Override
    protected void okPressed()
    {
        String fileName = openFileDialog();

        if (fileName != null)
        {
            saveImageToFile( capturedScreenshot, fileName );
        }
        super.okPressed();
    }

    private Image captureImage(Composite composite)
    {
        GC gc = null;
        Image image = null;
        Display display = Display.getCurrent();
        try
        {
            gc = new GC( composite );
            image = new Image( display, composite.getBounds() );
            gc.copyArea( image, 0, 0 );
        }
        finally
        {
            if (gc != null)
            {
                gc.dispose();
            }
        }
        return image;
    }

    private void saveImageToFile(Image img, String fileName)
    {
        ImageLoader loader = new ImageLoader();
        loader.data = new ImageData[]{img.getImageData()};

        loader.save( fileName, SWT.IMAGE_PNG );

        img.dispose();
    }

    private String openFileDialog()
    {
        FileDialog fileDialog = new FileDialog( childShell, SWT.SAVE );
        String[] extensions = {"*.png"};
        fileDialog.setFilterExtensions( extensions );
        String filename = fileDialog.open();
        if (filename != null)
        {
            if (!filename.endsWith( ".png" ))
            {
                filename = filename + ".png";
            }
            return filename;
        }
        return null;
    }
}
