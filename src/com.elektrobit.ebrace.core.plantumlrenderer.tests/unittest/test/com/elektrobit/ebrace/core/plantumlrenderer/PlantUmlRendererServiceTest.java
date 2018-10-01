/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.plantumlrenderer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.elektrobit.ebrace.core.plantumlrenderer.api.PlantUmlRendererService;
import com.elektrobit.ebrace.core.plantumlrenderer.impl.PlantUmlRendererServiceImpl;

public class PlantUmlRendererServiceTest
{
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    //@formatter:off
    private final String sequenceChartExample=""
            +"@startuml\n"
            + "Alice->Bob: Authentication Request\n"
            + "Bob-->Alice: Authentication Response\n"
            + "Alice->Bob: Another authentication Request\n"
            + "Alice<--Bob: another authentication Response\n"
            + "@enduml\n";
    
    private final String inCorrectSequenceChartExample=""
            +"@startuml\n"
            + "Alice->Bob: Authentication Request\n"
            + "Bob-->Alice: Authentication Response\n"
            + "Alice->Bob: Another authentication Request\n"
            + "Error Alice<--Bob: another authentication Response\n"
            + "@enduml\n";
    //@formatter:on

    private String sequenceChartFilePath;
    private PlantUmlRendererService plantUmlRenderer;

    @Before
    public void setup()
    {
        sequenceChartFilePath = tmpFolder.getRoot().getPath() + "/sequence-chart.svg";
        plantUmlRenderer = new PlantUmlRendererServiceImpl();
    }

    @Test
    public void sequenceChartSVGWasRendered() throws Exception
    {
        boolean result = plantUmlRenderer.plantumlToSVG( sequenceChartExample, sequenceChartFilePath );

        assertTrue( "Plant UML Generation returned Error. UML syntax not correct.", result );
        assertTrue( new File( sequenceChartFilePath ).length() > 0 );
    }

    @Test
    public void sequenceChartPngWasRendered() throws Exception
    {
        boolean result = plantUmlRenderer.plantumlToPNG( sequenceChartExample, sequenceChartFilePath );

        assertTrue( "Plant UML Generation returned Error. UML syntax not correct.", result );
        assertTrue( new File( sequenceChartFilePath ).length() > 0 );
    }

    @Test
    public void wrongUMLInputReturningFalse() throws Exception
    {
        boolean result = plantUmlRenderer.plantumlToSVG( inCorrectSequenceChartExample, sequenceChartFilePath );

        assertFalse( "Expecting false, because UML is wrong.", result );
    }
}
