/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.tableinput.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SplitSearchStringUtil
{
    private static final String DELIMITER = " ";
    private static final String EXCLUDED_DELIMITER = "-";
    private static final String EMPTY_STRING = "";

    public static List<String> getIgnoredWordsList(String filterString)
    {
        if (filterString == null)
            return Collections.emptyList();

        List<String> wordsInQuotes = getWordsInQuotes( filterString );
        String filterStringWithoutWordsInQuotes = eliminateWordsInQuoteFromFilterString( filterString, wordsInQuotes );
        String[] trimmedWords = trimWords( filterStringWithoutWordsInQuotes );

        return createIgnoredWordsList( trimmedWords, wordsInQuotes );
    }

    public static List<String> getNotIgnoredWordsList(String filterString)
    {
        if (filterString == null)
            return Collections.emptyList();

        List<String> wordsInQuotes = getWordsInQuotes( filterString );
        String filterStringWithoutWordsInQuotes = eliminateWordsInQuoteFromFilterString( filterString, wordsInQuotes );
        String[] trimmedWords = trimWords( filterStringWithoutWordsInQuotes );

        return createNotIgnoredWordsList( trimmedWords, wordsInQuotes );
    }

    private static List<String> getWordsInQuotes(String trimmedString)
    {
        List<String> wordInQuotes = new ArrayList<String>();
        Pattern pattern = Pattern.compile( "(-\\s+)?-?\"[^\"]*\"" );
        Matcher matcher = pattern.matcher( trimmedString );
        while (matcher.find())
        {
            wordInQuotes.add( matcher.group() );
        }

        return wordInQuotes;
    }

    private static String eliminateWordsInQuoteFromFilterString(String trimmedString, List<String> wordInQuotes)
    {
        for (String string : wordInQuotes)
        {
            if (trimmedString.contains( string ))
            {
                trimmedString = trimmedString.replaceAll( string, EMPTY_STRING );
            }
        }
        return trimmedString;
    }

    private static String[] trimWords(String filterString)
    {
        if (filterString == null || filterString.equals( EMPTY_STRING ))
        {
            return null;
        }
        String trimmedWords = trimWhitespaces( filterString );
        String[] wordsSplitted = trimmedWords.toLowerCase().split( DELIMITER, -1 );
        return wordsSplitted;
    }

    private static String trimWhitespaces(String filterInput)
    {
        String trimmedWhitespaces = filterInput.replaceAll( DELIMITER + "+", DELIMITER );
        trimmedWhitespaces = trimmedWhitespaces.replaceAll( EXCLUDED_DELIMITER + DELIMITER, EXCLUDED_DELIMITER );

        if (trimmedWhitespaces.charAt( trimmedWhitespaces.length() - 1 ) == ' ')
        {
            trimmedWhitespaces = trimmedWhitespaces.substring( 0, trimmedWhitespaces.length() - 1 );
        }

        return trimmedWhitespaces;
    }

    private static List<String> createIgnoredWordsList(String[] wordsSplitted, List<String> wordsInQuotes)
    {
        List<String> ignoredWords = new ArrayList<String>();
        if (wordsSplitted != null)
            for (String string : wordsSplitted)
            {
                if (string.startsWith( EXCLUDED_DELIMITER ))
                {
                    ignoredWords.add( string.substring( 1 ) );
                }
            }
        for (String string : wordsInQuotes)
        {
            if (string.startsWith( EXCLUDED_DELIMITER ))
            {
                string = string.replaceAll( DELIMITER, EMPTY_STRING );
                ignoredWords.add( string.substring( 2, string.length() - 1 ) );
            }
        }

        return ignoredWords;
    }

    private static List<String> createNotIgnoredWordsList(String[] wordsSplitted, List<String> wordsInQuotes)
    {
        List<String> notIgnoredWords = new ArrayList<String>();
        if (wordsSplitted != null)
        {
            for (String string : wordsSplitted)
            {
                if (!string.startsWith( EXCLUDED_DELIMITER ))
                {
                    notIgnoredWords.add( string );
                }
            }
        }
        for (String string : wordsInQuotes)
        {
            if (!string.startsWith( EXCLUDED_DELIMITER ))
            {
                notIgnoredWords.add( string.substring( 1, string.length() - 1 ) );
            }
        }

        return notIgnoredWords;
    }
}
