/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.franca.common.validator.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

import org.franca.core.franca.FContract;
import org.franca.core.franca.FEventOnIf;
import org.franca.core.franca.FInterface;
import org.franca.core.franca.FModel;
import org.franca.tools.contracts.tracevalidator.TraceValidationResult;
import org.franca.tools.contracts.tracevalidator.TraceValidator;
import org.franca.tools.contracts.tracevalidator.parser.ITraceParser;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.franca.common.franca.modelloader.api.FrancaModelFactory;
import com.elektrobit.ebrace.franca.common.impl.FrancaModelHelper;
import com.elektrobit.ebrace.franca.common.validator.api.FrancaTraceValidator;

public class DefaultFrancaTraceValidator implements FrancaTraceValidator
{
    private final ITraceParser parser;
    private List<FModel> fModels = null;
    private final FrancaModelFactory modelFactory;

    public DefaultFrancaTraceValidator(ITraceParser parser, FrancaModelFactory modelFactory)
    {
        this.parser = parser;
        this.modelFactory = modelFactory;
    }

    @Override
    public boolean validate(String interfaceFullQualifiedName, List<String> traceList)
    {
        RangeCheckUtils.assertStringParameterNotNullOrEmpty( "interfaceFullQualifiedName", interfaceFullQualifiedName );
        RangeCheckUtils.assertReferenceParameterNotNull( "traceList", traceList );
        RangeCheckUtils.assertListIsNotEmpty( "traceList", traceList );

        FModel foundFModel = FrancaModelHelper.getFrancaModelOfFrancaInterface( getFrancaModelsLazy(),
                                                                                interfaceFullQualifiedName );

        if (foundFModel != null)
        {
            InputStream tracesAsInputStream = convertTraceMessagesToInputStream( traceList );

            List<Set<FEventOnIf>> single = parser.parseAll( foundFModel, tracesAsInputStream );

            TraceValidationResult result = TraceValidator
                    .isValidTrace( getFInterfaceByFullQualifier( interfaceFullQualifiedName ), single );

            return result.valid;

        }

        return false;
    }

    private List<FModel> getFrancaModelsLazy()
    {
        if (fModels == null)
            fModels = modelFactory.getFrancaModels();
        return fModels;
    }

    private InputStream convertTraceMessagesToInputStream(List<String> trace)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        for (String nextTrace : trace)
        {
            try
            {
                nextTrace += "\n";
                outputStream.write( nextTrace.getBytes() );
            }
            catch (Exception e)
            {
                // Ignore: should never fail
            }
        }

        byte[] tracesAsByteArray = outputStream.toByteArray();

        return new ByteArrayInputStream( tracesAsByteArray );
    }

    private FContract getFInterfaceByFullQualifier(String interfaceFullQualifiedName)
    {
        for (FModel nextFModel : getFrancaModelsLazy())
        {
            FInterface francaInterface = FrancaModelHelper.getFrancaInterface( nextFModel, interfaceFullQualifiedName );
            if (francaInterface != null)
            {
                return francaInterface.getContract();
            }
        }

        return null;
    }
}
