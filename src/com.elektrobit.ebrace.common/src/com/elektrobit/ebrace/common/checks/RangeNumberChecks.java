/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.common.checks;

import java.math.BigDecimal;

public class RangeNumberChecks
{
    public static <T extends Number> void assertNumberValueGreaterThan(String paramName, T param, T borderValue)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "param", param );
        RangeCheckUtils.assertReferenceParameterNotNull( "borderValue", borderValue );

        if (param.doubleValue() <= borderValue.doubleValue())
        {
            throw new IllegalArgumentException( "Parameter " + paramName + " must be greater than " + borderValue
                    + ", but it is was smaller or equal. Value was " + param + "." );
        }
    }

    public static <T extends Number> void assertNumberValueEqualThan(String paramName, T param1, T param2)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "param", param1 );
        RangeCheckUtils.assertReferenceParameterNotNull( "borderValue", param2 );

        BigDecimal param1AsBigDecimal = new BigDecimal( param1.doubleValue() );
        BigDecimal param2AsBigDecimal = new BigDecimal( param2.doubleValue() );

        if (param1AsBigDecimal.equals( param2AsBigDecimal ))
        {
            throw new IllegalArgumentException( "Parameter " + paramName + " must be greater than " + param2
                    + ", but it is was smaller or equal. Value was " + param1 + "." );
        }
    }
}
