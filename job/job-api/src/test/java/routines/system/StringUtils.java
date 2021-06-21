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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    public static String[] split(String str, String separator) {
        return str.split(separator);
    }

    public static String deleteChar(String s, char delChar) {
        int len = s.length();
        char[] val = s.toCharArray();
        char buf[] = new char[len];
        int m = 0;
        for (int k = 0; k < len; k++) {
            char c = val[k];
            if (c != delChar) {
                buf[m] = c;
                m++;
            }
        }

        return new String(buf, 0, m);
    }

    public static String list(String[] stringArray) {
        return list(stringArray, null, null, null, null);
    }

    public static String list(String[] stringArray, String separator) {
        return list(stringArray, separator, null, null, null);
    }

    public static String list(String[] stringArray, String separator, String startEnclosure, String endEnclosure) {
        return list(stringArray, separator, startEnclosure, endEnclosure, null);
    }

    public static String list(String[] stringArray, String separator, String escaper) {
        return list(stringArray, separator, null, null, escaper);
    }

    public static String list(String[] stringArray, String separator, String startEnclosure, String endEnclosure, String escaper) {
        if (separator == null) {
            separator = ""; //$NON-NLS-1$
        } else {
            separator = separator.trim();
        }
        if (startEnclosure == null) {
            startEnclosure = ""; //$NON-NLS-1$
        } else {
            startEnclosure = startEnclosure.trim();
        }
        if (endEnclosure == null) {
            endEnclosure = ""; //$NON-NLS-1$
        } else {
            endEnclosure = endEnclosure.trim();
        }
        if (escaper == null) {
            escaper = ""; //$NON-NLS-1$
        } else {
            escaper = escaper.trim();
        }
        StringBuilder result = new StringBuilder();

        result.append(startEnclosure);

        boolean flag = false;
        for (String item : stringArray) {
            item = item.trim();
            if (flag) {
                result.append(separator);
            } else {
                flag = true;
            }
            result.append(escaper);
            result.append(item);
            result.append(escaper);
        }
        result.append(endEnclosure);

        return result.toString();
    }

    /**
     * to discuss the case: src == null || regex == null || replacement == null
     *
     */
    public static String replaceAll(String src, String regex, String replacement) {

        // case 1:
        if (regex == null) {
            if (src == null) {
                return replacement; // regex == null && src == null
            } else {
                return src; // regex == null && src != null
            }
        } else {
            // case 2:
            if (src == null) {
                return null; // regex != null && src == null
            } else {
                // case 3:
                if (replacement == null) {
                    if (src.matches(regex)) {
                        // regex != null && src != null && replacement != null, and match the whole src
                        return replacement;
                    } else {
                        return src; // can't match the whole src
                    }

                } else {
                    // regex != null && src != null && replacement != null
                    return src.replaceAll(regex, replacement);

                }
            }
        }
    }

    /**
     * ignore regex
     *
     */
    public static String replaceAllStrictly(String src, String search, String replacement, boolean entirelyMatch,
            boolean caseSensitive) {
        // case 1:
        if (search == null) {
            if (src == null) {
                return replacement; // regex == null && src == null
            } else {
                return src; // regex == null && src != null
            }
        } else {
            // case 2:
            if (src == null) {
                return null; // regex != null && src == null
            } else {
                // case 3:
                if (replacement == null) {
                    if (src.equals(search)) {
                        // regex != null && src != null && replacement != null, and match the whole src
                        return replacement;
                    } else {
                        return src; // can't match the whole src
                    }

                } else {
                    // regex != null && src != null && replacement != null
                    if (entirelyMatch) {
                        String upperSrc = caseSensitive ? src : src.toUpperCase();
                        String upperSearch = caseSensitive ? search : search.toUpperCase();
                        return upperSrc.equals(upperSearch) ? replacement : src;
                    } else {
                        int flag = caseSensitive ? Pattern.LITERAL : Pattern.LITERAL | Pattern.CASE_INSENSITIVE;
                        return Pattern.compile(search, flag).matcher(src).replaceAll(Matcher.quoteReplacement(replacement));
                    }
                }
            }
        }
    }

    /**
     * make \n to \\n. It will process these chars: \n, \r, \t, \f, \\, \", \', \b
     *
     */
    public static String escapeChar(String s) {
        if (s == null) {
            return null;
        }

        int length = s.length();
        StringBuffer sb = new StringBuffer(length);
        for (int i = 0; i < length; i++) {

            char c = s.charAt(i);

            switch (c) {
            case '\n':
                sb.append("\\").append('n'); //$NON-NLS-1$
                break;
            case '\r':
                sb.append("\\").append('r'); //$NON-NLS-1$
                break;
            case '\t':
                sb.append("\\").append('t'); //$NON-NLS-1$
                break;
            case '\f':
                sb.append("\\").append('f'); //$NON-NLS-1$
                break;
            case '\b':
                sb.append("\\").append('b'); //$NON-NLS-1$
                break;
            case '\"':
                sb.append("\\").append('\"'); //$NON-NLS-1$
                break;
            case '\'':
                sb.append("\\").append('\''); //$NON-NLS-1$
                break;
            default:
                sb.append(c);
            }
        }

        return sb.toString();
    }
}
