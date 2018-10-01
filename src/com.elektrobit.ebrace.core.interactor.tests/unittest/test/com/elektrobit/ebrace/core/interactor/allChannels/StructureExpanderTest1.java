/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.interactor.allChannels;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.core.interactor.allChannels.StructureExpander;

public class StructureExpanderTest1
{

    private static final String[] INPUT = {"A.B.C.1", "A.B.C.2", "A.B.C.3", "B.C.D.E", "C.1.1", "C.1.2", "C.1.3",
            "C.2.1", "C.2.2", "D.E.F.1", "D.E.F.2", "D.E.F.3.1"};
    private static final List<String> INPUT_LIST = Arrays.asList( INPUT );

    private static final String[] CONVERTED = {"A", "A.B", "A.B.C", "A.B.C.1", "A.B.C.2", "A.B.C.3", "B", "B.C",
            "B.C.D", "B.C.D.E", "C", "C.1", "C.1.1", "C.1.2", "C.1.3", "C.2", "C.2.1", "C.2.2", "D", "D.E", "D.E.F",
            "D.E.F.1", "D.E.F.2", "D.E.F.3", "D.E.F.3.1"};
    private static final List<String> CONVERTED_LIST = Arrays.asList( CONVERTED );

    private StructureExpander underTest;
    private List<String> result;

    @Before
    public void setup()
    {
        underTest = new StructureExpander();
        result = underTest.createStructureWithAllSubgroups( INPUT_LIST );
    }

    @Test
    public void checkThatResultIsNotNull()
    {
        assertNotNull( result );
    }

    @Test
    public void checkThatResultMatches()
    {
        assertThat( result, is( CONVERTED_LIST ) );
    }

}
