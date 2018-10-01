/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.franca.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.franca.core.franca.FEventOnIf;
import org.franca.core.franca.FInterface;
import org.franca.core.franca.FModel;
import org.franca.tools.contracts.tracevalidator.parser.ITraceParser;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.franca.common.franca.modelloader.api.FrancaModelFactory;
import com.elektrobit.ebrace.franca.common.validator.api.FrancaTraceValidator;
import com.elektrobit.ebrace.franca.common.validator.impl.DefaultFrancaTraceValidator;

public class DefaultFrancaTraceValidatorTest
{
    private final String FMODEL_NAME = "myModel";
    private final String FINTERFACE_NAME = "myInterface";
    private final String FMETHOD_NAME = "myMethod";

    @Test
    public void validationTest()
    {
        final FModel fModel = createTestFModel();
        FrancaModelFactory mockedModelFactory = createTestFactory( Arrays.asList( fModel ) );

        ITraceParser parser = new ITraceParser()
        {

            @Override
            public Set<FEventOnIf> parseSingle(FModel model, String traceElement)
            {
                Assert.fail( "Should not be used." );
                return null;
            }

            @Override
            public List<Set<FEventOnIf>> parseAll(FModel model, InputStream inputStream)
            {
                Assert.assertEquals( fModel, model );
                Assert.assertEquals( "CALL_" + FMETHOD_NAME, convertInputStreamToString( inputStream ) );

                return new ArrayList<Set<FEventOnIf>>();
            }

            private String convertInputStreamToString(InputStream inputStream)
            {
                byte[] buffer = new byte[1000];
                try
                {
                    while (inputStream.read( buffer ) > 0)
                    {
                        return new String( buffer ).trim();
                    }
                }
                catch (IOException e)
                {
                }
                return "";
            }
        };

        FrancaTraceValidator francaTraceValidator = new DefaultFrancaTraceValidator( parser, mockedModelFactory );
        francaTraceValidator.validate( FMODEL_NAME + "." + FINTERFACE_NAME, Arrays.asList( "CALL_" + FMETHOD_NAME ) );
    }

    private FModel createTestFModel()
    {
        final FInterface fInterface = Mockito.mock( FInterface.class );
        Mockito.when( fInterface.getName() ).thenReturn( FINTERFACE_NAME );

        final FModel fModel = Mockito.mock( FModel.class );
        Mockito.when( fModel.getName() ).thenReturn( FMODEL_NAME );

        EList<FInterface> fInterfaces = new BasicEList<FInterface>();
        fInterfaces.add( fInterface );
        Mockito.when( fModel.getInterfaces() ).thenReturn( fInterfaces );

        return fModel;
    }

    private FrancaModelFactory createTestFactory(List<FModel> fModels)
    {
        FrancaModelFactory mockedModelFactory = Mockito.mock( FrancaModelFactory.class );
        Mockito.when( mockedModelFactory.getFrancaModels() ).thenReturn( fModels );
        return mockedModelFactory;
    }
}
