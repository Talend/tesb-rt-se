/*
 * #%L
 * Talend :: ESB :: Job :: API
 * %%
 * Copyright (c) 2006-2021 Talend Inc. - www.talend.com
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
// ============================================================================
//
// Copyright (c) 2006-2021 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 5/7 rue Salomon De Rothschild, 92150 Suresnes, France
//
// ============================================================================
package routines.system;

import routines.TalendDate;

public class FormatterUtils {

    public static String format_Date(java.util.Date date, String pattern) {
        if (date != null) {
            return TalendDate.formatDate(pattern, date);
        } else {
            return null;
        }
    }

    public static String format_Date_Locale(java.util.Date date, String pattern, String locale) {
        if (date != null) {
            return TalendDate.formatDateLocale(pattern, date, locale);
        } else {
            return null;
        }
    }

    /**
     * in order to transform the number "1234567.89" to string 123,456.89
     */
    public static String format_Number(String s, Character thousandsSeparator, Character decimalSeparator) {
        if (s == null) {
            return null;
        }
        String result = s;
        int decimalIndex = s.indexOf("."); //$NON-NLS-1$

        if (decimalIndex == -1) {
            if (thousandsSeparator != null) {
                return formatNumber(result, thousandsSeparator);
            } else {
                return result;
            }
        }

        if (thousandsSeparator != null) {
            result = formatNumber(s.substring(0, decimalIndex), thousandsSeparator);
        } else {
            result = s.substring(0, decimalIndex);
        }

        if (decimalSeparator != null) {
            result += (s.substring(decimalIndex)).replace('.', decimalSeparator);
        } else {
            result += s.substring(decimalIndex);
        }
        return result;
    }

    private static String formatNumber(String s, char thousandsSeparator) {

        StringBuilder sb = new StringBuilder(s);
        int index = sb.length();

        index = index - 3;
        while (index > 0 && sb.charAt(index - 1) != '-') {
            sb.insert(index, thousandsSeparator);
            index = index - 3;
        }

        return sb.toString();
    }

    /**
     * Bug 13352 by nsun: always return the format using "." for decimal separator.
     */
    public static String unformat_Number(String s, Character thousandsSeparator, Character decimalSeparator) {
        if (s == null) {
            return null;
        }
        String result = s;
        int decimalIndex = s.indexOf(decimalSeparator);
        if (decimalIndex == -1) {
            if (thousandsSeparator != null) {
                return unformatNumber(result, thousandsSeparator);
            } else {
                return result;
            }
        }
        if (thousandsSeparator != null) {
            result = unformatNumber(s.substring(0, decimalIndex), thousandsSeparator);
        } else {
            result = s.substring(0, decimalIndex);
        }

        if (decimalSeparator != null) {
            if ("\\.".equals(decimalSeparator)) {
                result += (s.substring(decimalIndex)).replace(thousandsSeparator, decimalSeparator);
            } else {
                result += (s.substring(decimalIndex)).replace(decimalSeparator, '.');
            }
        } else {
            result += s.substring(decimalIndex);
        }
        return result;
    }

    private static String unformatNumber(String str, Character thousandsSeparator) {
        StringBuilder returnString = new StringBuilder();
        String separator = thousandsSeparator.toString();
        if (".".equals(separator))
            separator = "\\.";
        String[] s = str.split(separator);
        for (String part : s)
            returnString.append(part);
        return returnString.toString();
    }

    /**
     * DOC Administrator Comment method "formatUnwithE". In java when double more than six decimal that use toString
     * will rentru contains E scientific natation.
     *
     * @param arg like: double falot String .... e.g:1.0E-8
     * @return 0.00000001 as String
     */
    public static String formatUnwithE(Object arg) {
        String doubleString = String.valueOf(arg);
        if (doubleString.indexOf("E") != -1) {
            String position;
            if (doubleString.charAt(doubleString.indexOf("E") + 1) != '-') {
                position = doubleString.substring(doubleString.indexOf("E") + 1);
            } else {
                position = doubleString.substring(doubleString.indexOf("E") + 2);
            }
            return String.format("%1." + position + "f", arg);
        }
        return doubleString;
    }
}
