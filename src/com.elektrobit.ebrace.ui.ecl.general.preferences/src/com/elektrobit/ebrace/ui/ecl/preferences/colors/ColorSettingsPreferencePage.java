/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.ui.ecl.preferences.colors;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.elektrobit.ebrace.core.interactor.api.channelcolor.ColorPreferencesNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.channelcolor.ColorPreferencesNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.preferences.SetColorPreferencesInteractionUseCase;
import com.elektrobit.ebsolys.core.targetdata.api.color.ColorSettingsPreferenceConstants;
import com.elektrobit.ebsolys.core.targetdata.api.color.SColor;

public class ColorSettingsPreferencePage extends PreferencePage
        implements
            IWorkbenchPreferencePage,
            ColorPreferencesNotifyCallback

{
    private final String PERCENTAGE_SIGN = "%";
    private final int BUTTON_HEIGHT = 20;
    private final Timer timer = new Timer( "ColorSettingsPreferencePage Timer" );

    private SetColorPreferencesInteractionUseCase setColorPreferencesInteractionUseCase;
    private ColorPreferencesNotifyUseCase colorPreferencesNotifyUseCase;

    private Composite parentComposite;
    private Composite colorsComp;
    private ScrolledComposite sc;
    private ResourceManager resourceManager;
    private Text transparencyText;
    private Slider slider;

    private List<SColor> raceColors = new ArrayList<SColor>();
    private Double valueDouble;

    @Override
    public void init(IWorkbench workbench)
    {
        resourceManager = getResourceManager();
        setColorPreferencesInteractionUseCase = UseCaseFactoryInstance.get().makeSetColorPreferencesInteractionUseCase();
    }

    private ResourceManager getResourceManager()
    {
        if (resourceManager == null)
        {
            resourceManager = new LocalResourceManager( JFaceResources.getResources() );
        }
        return resourceManager;
    }

    @Override
    protected Control createContents(Composite parent)
    {
        parentComposite = new Composite( parent, SWT.NONE );
        parentComposite.setLayout( GridLayoutFactory.fillDefaults().create() );

        createTransparencyPartComposite();
        createColorsPartScrolledComposite();

        colorPreferencesNotifyUseCase = UseCaseFactoryInstance.get().makeColorPreferencesNotifyUseCase( this );

        return parentComposite;
    }

    private void createTransparencyPartComposite()
    {
        Composite transparencyComposite = new Composite( parentComposite, SWT.NONE );
        transparencyComposite.setLayout( new GridLayout() );

        createTransparencyPart( transparencyComposite );
        createSlider( transparencyComposite );
    }

    private void createColorsPartScrolledComposite()
    {
        sc = new ScrolledComposite( parentComposite, SWT.V_SCROLL | SWT.RESIZE );
        sc.setLayoutData( GridDataFactory.fillDefaults().grab( false, true ).hint( SWT.DEFAULT, 50 ).create() );

        colorsComp = new Composite( sc, SWT.NONE );
        colorsComp.setLayout( new GridLayout() );
        colorsComp.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        sc.setExpandHorizontal( true );
        sc.setExpandVertical( true );
    }

    private void createPersistedColorPreferences(List<SColor> raceColors)
    {
        for (int i = 0; i < raceColors.size(); i++)
        {
            createExistingColor( raceColors.get( i ) );
        }
        if (colorsComp.getChildren().length < 20)
        {
            createAddColorButton();
        }
    }

    private void createTransparencyPart(Composite parent)
    {
        Composite comp = new Composite( parent, SWT.NONE );
        comp.setLayout( new GridLayout( 2, false ) );

        createTransparencyLabel( comp );
        createTransparencyTextBox( comp );
    }

    private void createTransparencyLabel(Composite comp)
    {
        Label transpLabel = new Label( comp, SWT.NONE );
        transpLabel.setText( "Transparency:" );
    }

    private void createTransparencyTextBox(Composite comp)
    {
        transparencyText = new Text( comp, SWT.BORDER | SWT.CENTER );
        GridData gridData = new GridData( 30, 20 );
        transparencyText.setLayoutData( gridData );

        addTransparencyBoxModifyListener();
        addTransparencyTextBoxListener();
    }

    private void addTransparencyBoxModifyListener()
    {
        transparencyText.addModifyListener( new ModifyListener()
        {
            @Override
            public void modifyText(ModifyEvent e)
            {

                timer.schedule( new TimerTask()
                {
                    @Override
                    public void run()
                    {
                        addPercentageSignIfNonexistent();
                    }
                }, 1500 );
            }
        } );
    }

    private void addPercentageSignIfNonexistent()
    {
        Display.getDefault().asyncExec( new Runnable()
        {

            @Override
            public void run()
            {
                if (!transparencyText.isDisposed() && !transparencyText.getText().contains( PERCENTAGE_SIGN ))
                {
                    transparencyText.setText( transparencyText.getText() + PERCENTAGE_SIGN );
                }
            }
        } );
    }

    private void addTransparencyTextBoxListener()
    {
        transparencyText.addModifyListener( new ModifyListener()
        {

            @Override
            public void modifyText(ModifyEvent e)
            {
                int value = getIntValueFromTranspText();
                if (value >= 0 && value <= 100)
                {
                    slider.setSelection( value );
                    changeTransparencyOfExistingColors();

                }
                else
                {
                    slider.setSelection( 0 );
                }
            }
        } );
    }

    private void createSlider(Composite content)
    {
        slider = new Slider( content, SWT.HORIZONTAL | SWT.BORDER );
        slider.setBounds( 0, 40, 300, 50 );
        slider.setMinimum( 0 );
        slider.setMaximum( 101 );
        slider.setThumb( 1 );

        addSliderListener();
    }

    private void addSliderListener()
    {
        slider.addListener( SWT.Selection, new Listener()
        {

            @Override
            public void handleEvent(Event event)
            {
                String outString = "";
                outString = slider.getSelection() + PERCENTAGE_SIGN;
                transparencyText.setText( outString );

                changeTransparencyOfExistingColors();
            }
        } );
    }

    private void changeTransparencyOfExistingColors()
    {
        if (colorsComp != null && !colorsComp.isDisposed())
        {
            Control[] children = colorsComp.getChildren();
            for (int i = 0; i < children.length - 1; i++)
            {
                Label normalColorLabel = (Label)((Composite)children[i]).getChildren()[1];
                Color color = normalColorLabel.getBackground();
                color = createTransparentColor( color );

                Label transpLabel = (Label)((Composite)children[i]).getChildren()[2];
                transpLabel.setBackground( color );
            }
        }
    }

    private void createDefaultColorPreferences()
    {
        transparencyText.setText( "80%" );
        slider.setSelection( 80 );
        for (SColor raceColor : ColorSettingsPreferenceConstants.defaultChannelColors)
        {
            createExistingColor( raceColor );
        }

        createAddColorButton();
    }

    private void createExistingColor(SColor raceColor)
    {
        final Composite singleColorComp = new Composite( colorsComp, SWT.NONE );
        GridLayout compGridlayout = new GridLayout( 3, false );
        compGridlayout.marginBottom = -5;
        singleColorComp.setLayout( compGridlayout );

        createDeleteColorButton( singleColorComp );
        createColorField( raceColor, singleColorComp );
        createTransparentColorField( raceColor, singleColorComp );

    }

    private void createDeleteColorButton(Composite singleColorComp)
    {
        ToolBar toolBar = new ToolBar( singleColorComp, SWT.FLAT );
        ToolItem item = new ToolItem( toolBar, SWT.PUSH );
        item.setImage( createImage( "delete.jpg" ) );

        addDeleteColorButtonListener( singleColorComp, item );
    }

    private Image createImage(String imageName)
    {
        URL iconUrl = FileLocator.find( Platform.getBundle( "com.elektrobit.ebrace.ui.ecl.general.preferences" ),
                                        new Path( "icons/" + imageName ),
                                        null );
        ImageDescriptor myImage = ImageDescriptor.createFromURL( iconUrl );
        Image buttonImage = myImage.createImage();
        return buttonImage;
    }

    private void addDeleteColorButtonListener(final Composite singleColorComp, ToolItem delColButton)
    {
        delColButton.addSelectionListener( new SelectionListener()
        {

            @Override
            public void widgetSelected(SelectionEvent e)
            {
                if (colorsComp.getChildren().length > 7)
                {
                    singleColorComp.dispose();
                    if (colorsComp.getChildren().length == 19)
                    {
                        createAddColorButton();
                    }
                    colorsComp.layout();
                    sc.setMinSize( colorsComp.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
                }
                else
                {
                    MessageBox box = new MessageBox( new Shell(), SWT.ICON_ERROR | SWT.OK );
                    box.setMessage( "Cannot have less than six colors." );
                    box.setText( "Cannot delete color" );
                    box.open();
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {

            }
        } );
    }

    private void createColorField(SColor raceColor, Composite singleColorComp)
    {
        GridData colorTextData = new GridData( 13, BUTTON_HEIGHT );
        colorTextData.horizontalIndent = 5;
        Label colorLabel = new Label( singleColorComp, SWT.NONE );
        colorLabel.setLayoutData( colorTextData );
        colorLabel.setBackground( new Color( Display.getDefault(),
                                             raceColor.getRed(),
                                             raceColor.getGreen(),
                                             raceColor.getBlue() ) );

        changeColorMouseListener( colorLabel );
    }

    private void createTransparentColorField(SColor raceColor, Composite singleColorComp)
    {
        GridData transpColorTextData = new GridData( 50, BUTTON_HEIGHT );
        transpColorTextData.horizontalIndent = -5;
        Label transpColorLabel = new Label( singleColorComp, SWT.NONE );
        transpColorLabel.setLayoutData( transpColorTextData );

        Color color = new Color( Display.getDefault(), raceColor.getRed(), raceColor.getGreen(), raceColor.getBlue() );
        Color transparentColor = createTransparentColor( color );
        transpColorLabel.setBackground( transparentColor );
        transpColorLabel.addPaintListener( new PaintListener()
        {

            @Override
            public void paintControl(PaintEvent e)
            {
                e.gc.setForeground( Display.getDefault().getSystemColor( SWT.COLOR_BLACK ) );
                e.gc.drawText( "text", 14, 2 );
            }
        } );

        changeColorMouseListener( transpColorLabel );
    }

    private void changeColorMouseListener(Label label)
    {
        label.addMouseListener( new MouseListener()
        {

            @Override
            public void mouseUp(MouseEvent e)
            {

            }

            @Override
            public void mouseDown(MouseEvent e)
            {
                if (e.getSource() instanceof Label)
                {
                    Color chosenColor = chooseColorFromColorPicker();
                    if (chosenColor != null)
                    {
                        Label clickedLabel = (Label)e.getSource();
                        Composite parent = clickedLabel.getParent();
                        Control[] children = parent.getChildren();

                        ((Label)children[1]).setBackground( chosenColor );
                        Color transparentColor = createTransparentColor( chosenColor );
                        ((Label)children[2]).setBackground( transparentColor );
                    }
                }
            }

            @Override
            public void mouseDoubleClick(MouseEvent e)
            {

            }
        } );
    }

    private Color chooseColorFromColorPicker()
    {
        Color chosenColor = null;
        ColorDialog dlg = new ColorDialog( Display.getCurrent().getActiveShell() );
        RGB rgb = dlg.open();
        if (rgb != null)
        {
            chosenColor = resourceManager.createColor( rgb );
        }

        return chosenColor;
    }

    private Color createTransparentColor(Color color)
    {
        Color transparentColor = null;
        int value = getIntValueFromTranspText();
        if (value >= 0 && value <= 100)
        {
            double tint_value = (double)value / (double)100;
            transparentColor = makeColorBrighter( color, tint_value );
        }
        return transparentColor;
    }

    private void createAddColorButton()
    {
        final Composite comp = new Composite( colorsComp, SWT.NONE );
        GridLayout compGridlayout = new GridLayout( 1, false );
        comp.setLayout( compGridlayout );

        ToolBar toolBar = new ToolBar( comp, SWT.FLAT );
        ToolItem item = new ToolItem( toolBar, SWT.PUSH );
        item.setImage( createImage( "add.jpg" ) );

        addNewColorButtonListener( item, toolBar );
    }

    private void addNewColorButtonListener(final ToolItem addColButton, final ToolBar toolBar)
    {
        addColButton.addSelectionListener( new SelectionListener()
        {

            @Override
            public void widgetSelected(SelectionEvent e)
            {
                Color chosenColor = chooseColorFromColorPicker();
                if (chosenColor != null)
                {
                    SColor newColor = new SColor( chosenColor.getRed(), chosenColor.getGreen(), chosenColor.getBlue() );
                    addColButton.getParent().getParent().dispose();
                    createExistingColor( newColor );

                    if (colorsComp.getChildren().length < 20)
                    {
                        createAddColorButton();
                    }

                    colorsComp.layout();
                    sc.layout();
                    sc.setMinSize( colorsComp.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {

            }
        } );
    }

    private Color makeColorBrighter(Color color, double tint_factor)
    {
        int newR = (int)(color.getRed() + (255 - color.getRed()) * tint_factor);
        int newG = (int)(color.getGreen() + (255 - color.getGreen()) * tint_factor);
        int newB = (int)(color.getBlue() + (255 - color.getBlue()) * tint_factor);
        return new Color( null, newR, newG, newB );
    }

    private int getIntValueFromTranspText()
    {
        String stringValue = transparencyText.getText();
        if (!stringValue.isEmpty() || stringValue == null)
        {
            if (stringValue.charAt( stringValue.length() - 1 ) == '%')
            {
                stringValue = stringValue.substring( 0, stringValue.length() - 1 );
            }
            if (!stringValue.isEmpty())
            {
                Double valueDouble = Double.parseDouble( stringValue );
                return valueDouble.intValue();
            }
        }
        return -1;
    }

    @Override
    protected void performDefaults()
    {
        emptyColorsComposite();
        createDefaultColorPreferences();
        colorsComp.layout();
        sc.setMinSize( colorsComp.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    }

    @Override
    protected void performApply()
    {
        persistAndApplyTransparencyValue();
        persistColors();
    }

    @Override
    public boolean performOk()
    {
        persistAndApplyTransparencyValue();
        persistColors();

        return true;
    }

    private void persistAndApplyTransparencyValue()
    {
        int value = getIntValueFromTranspText();
        double tint_value = (double)value / (double)100;

        setColorPreferencesInteractionUseCase.setColorTranspPreferences( tint_value );
    }

    private void persistColors()
    {
        List<SColor> raceColors = new ArrayList<SColor>();
        Control[] children = colorsComp.getChildren();

        int number;
        if (children.length == 20)
        {
            if (((Composite)children[19]).getChildren().length == 3)
            {
                number = 20;
            }
            else
            {
                number = 19;
            }
        }
        else
        {
            number = children.length - 1;
        }

        for (int i = 0; i < number; i++)
        {
            Label normalColorLabel = (Label)((Composite)children[i]).getChildren()[1];
            Color color = normalColorLabel.getBackground();
            SColor raceColor = new SColor( color.getRed(), color.getGreen(), color.getBlue() );
            raceColors.add( raceColor );
        }

        emptyColorsComposite();
        setColorPreferencesInteractionUseCase.setColorPreferences( raceColors );
    }

    private void emptyColorsComposite()
    {
        Control[] children = colorsComp.getChildren();
        for (int i = 0; i < children.length; i++)
        {
            children[i].dispose();
        }
    }

    @Override
    public void dispose()
    {
        resourceManager.dispose();
        timer.cancel();
        setColorPreferencesInteractionUseCase.unregister();
        colorPreferencesNotifyUseCase.unregister();
    }

    @Override
    public void onColorTransparencyChanged(double value)
    {
        valueDouble = value;
        int value2 = (int)(valueDouble * 100);
        transparencyText.setText( String.valueOf( value2 ) + PERCENTAGE_SIGN );
        slider.setSelection( value2 );
    }

    @Override
    public void onColorPaletteChanged(List<SColor> newColorPalette)
    {
        raceColors = newColorPalette;
        if (raceColors != null && !raceColors.isEmpty())
        {
            createPersistedColorPreferences( raceColors );
        }
        else
        {
            createDefaultColorPreferences();
        }

        sc.setContent( colorsComp );
        sc.setMinSize( colorsComp.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    }
}
