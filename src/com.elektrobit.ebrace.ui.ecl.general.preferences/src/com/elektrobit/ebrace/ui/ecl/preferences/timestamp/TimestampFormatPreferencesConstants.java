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

public interface TimestampFormatPreferencesConstants
{
    public static String TIME_MILLISECONDS = "HH:mm:ss.SSS";

    public static String TIME_MICROSECONDS = "HH:mm:ss.SSSSSS";

    public static String DATE_MILLISECONDS = "yyyy-MM-dd HH:mm:ss.SSS";

    public static String DATE_MICROSECONDS = "yyyy-MM-dd HH:mm:ss.SSSSSS";

    public static String CUSTOM_TIMESTAMP_FORMAT = "custom_timestamp_format";

    public static String TIMESTAMP_FORMAT_ERROR_MESSAGE = "TIMESTAMP FORMAT NOT VALID";

    public static String TIMESTAMP_MILLISECONDS_LABEL = "TIMESTAMP_MILLISECONDS";

    public static String TIMESTAMP_MICROSECONDS_LABEL = "TIMESTAMP_MICROSECONDS";

    public static String TIME_MILLISECONDS_LABEL = "TIME_MILLISECONDS (HH:mm:ss.SSS)";

    public static String TIME_MICROSECONDS_LABEL = "TIME_MICROSECONDS (HH:mm:ss.SSSSSS)";

    public static String DATE_MILLISECONDS_LABEL = "DATE_MILLISECONDS (yyyy-MM-dd HH:mm:ss.SSS)";

    public static String DATE_MICROSECONDS_LABEL = "DATE_MICROSECONDS (yyyy-MM-dd HH:mm:ss.SSSSSS)";

    public static String CUSTOM_TIMESTAMP_FORMAT_LABEL = "Custom timestamp format:";

    public static String TIMESTAMP_PREFERENCES_PAGE_LABEL = "Select the timestamp format: ";

    public static String TIMESTAMP_PREFERENCES_PAGE_INFO_LABEL = "Find more information about custom time format <a href=\"https://docs.oracle.com/javase/tutorial/i18n/format/simpleDateFormat.html\">here...</a>";

}
