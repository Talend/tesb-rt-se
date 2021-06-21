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
package routines.system;

/*
 * Copyright (c) 2006-2021 Talend Inc. - www.talend.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * The Software shall be used for Good, not Evil.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

/**
 * A JSONObject is an unordered collection of name/value pairs. Its external form is a string wrapped in curly braces
 * with colons between the names and values, and commas between the values and names. The internal form is an object
 * having <code>get</code> and <code>opt</code> methods for accessing the values by name, and <code>put</code> methods
 * for adding or replacing values by name. The values can be any of these types: <code>Boolean</code>,
 * <code>JSONArray</code>, <code>JSONObject</code>, <code>Number</code>, <code>String</code>, or the
 * <code>JSONObject.NULL</code> object. A JSONObject constructor can be used to convert an external form JSON text into
 * an internal form whose values can be retrieved with the <code>get</code> and <code>opt</code> methods, or to convert
 * values into a JSON text using the <code>put</code> and <code>toString</code> methods. A <code>get</code> method
 * returns a value if one can be found, and throws an exception if one cannot be found. An <code>opt</code> method
 * returns a default value instead of throwing an exception, and so is useful for obtaining optional values.
 * <p>
 * The generic <code>get()</code> and <code>opt()</code> methods return an object, which you can cast or query for type.
 * There are also typed <code>get</code> and <code>opt</code> methods that do type checking and type coercion for you.
 * <p>
 * The <code>put</code> methods adds values to an object. For example,
 *
 * <pre>
 * myString = new JSONObject().put(&quot;JSON&quot;, &quot;Hello, World!&quot;).toString();
 * </pre>
 *
 * produces the string <code>{"JSON": "Hello, World"}</code>.
 * <p>
 * The texts produced by the <code>toString</code> methods strictly conform to the JSON syntax rules. The constructors
 * are more forgiving in the texts they will accept:
 * <ul>
 * <li>An extra <code>,</code>&nbsp;<small>(comma)</small> may appear just before the closing brace.</li>
 * <li>Strings may be quoted with <code>'</code>&nbsp;<small>(single quote)</small>.</li>
 * <li>Strings do not need to be quoted at all if they do not begin with a quote or single quote, and if they do not
 * contain leading or trailing spaces, and if they do not contain any of these characters:
 * <code>{ } [ ] / \ : , = ; #</code> and if they do not look like numbers and if they are not the reserved words
 * <code>true</code>, <code>false</code>, or <code>null</code>.</li>
 * <li>Keys can be followed by <code>=</code> or <code>=></code> as well as by <code>:</code>.</li>
 * <li>Values can be followed by <code>;</code> <small>(semicolon)</small> as well as by <code>,</code>
 * <small>(comma)</small>.</li>
 * <li>Numbers may have the <code>0-</code> <small>(octal)</small> or <code>0x-</code> <small>(hex)</small> prefix.</li>
 * </ul>
 *
 * @author JSON.org
 * @version 2010-01-05
 */
public class JSONObject {

    /**
     * JSONObject.NULL is equivalent to the value that JavaScript calls null, whilst Java's null is equivalent to the
     * value that JavaScript calls undefined.
     */
    private static final class Null {

        /**
         * There is only intended to be a single instance of the NULL object, so the clone method returns itself.
         *
         * @return NULL.
         */
        protected final Object clone() {
            return this;
        }

        /**
         * A Null object is equal to the null value and to itself.
         *
         * @param object An object to test for nullness.
         * @return true if the object parameter is the JSONObject.NULL object or null.
         */
        public boolean equals(Object object) {
            return object == null || object == this;
        }

        /**
         * Get the "null" string value.
         *
         * @return The string "null".
         */
        public String toString() {
            return "null";
        }
    }

    /**
     * The map where the JSONObject's properties are kept.
     */
    private Map map;

    /**
     * It is sometimes more convenient and less ambiguous to have a <code>NULL</code> object than to use Java's
     * <code>null</code> value. <code>JSONObject.NULL.equals(null)</code> returns <code>true</code>.
     * <code>JSONObject.NULL.toString()</code> returns <code>"null"</code>.
     */
    public static final Object NULL = new Null();

    /**
     * Construct an empty JSONObject.
     */
    public JSONObject() {
        this.map = new HashMap();
    }

    /**
     * Construct a JSONObject from a subset of another JSONObject. An array of strings is used to identify the keys that
     * should be copied. Missing keys are ignored.
     *
     * @param jo A JSONObject.
     * @param names An array of strings.
     * @throws JSONException
     * @exception JSONException If a value is a non-finite number or if a name is duplicated.
     */
    public JSONObject(JSONObject jo, String[] names) {
        this();
        for (int i = 0; i < names.length; i += 1) {
            try {
                putOnce(names[i], jo.opt(names[i]));
            } catch (Exception ignore) {
            }
        }
    }

    /**
     * Construct a JSONObject from a JSONTokener.
     *
     * @param x A JSONTokener object containing the source string.
     * @throws JSONException If there is a syntax error in the source string or a duplicated key.
     */
    public JSONObject(JSONTokener x) throws JSONException {
        this();
        char c;
        String key;

        if (x.nextClean() != '{') {
            throw x.syntaxError("A JSONObject text must begin with '{'");
        }
        for (;;) {
            c = x.nextClean();
            switch (c) {
            case 0:
                throw x.syntaxError("A JSONObject text must end with '}'");
            case '}':
                return;
            default:
                x.back();
                key = x.nextValue().toString();
            }

            /*
             * The key is followed by ':'. We will also tolerate '=' or '=>'.
             */

            c = x.nextClean();
            if (c == '=') {
                if (x.next() != '>') {
                    x.back();
                }
            } else if (c != ':') {
                throw x.syntaxError("Expected a ':' after a key");
            }
            putOnce(key, x.nextValue());

            /*
             * Pairs are separated by ','. We will also tolerate ';'.
             */

            switch (x.nextClean()) {
            case ';':
            case ',':
                if (x.nextClean() == '}') {
                    return;
                }
                x.back();
                break;
            case '}':
                return;
            default:
                throw x.syntaxError("Expected a ',' or '}'");
            }
        }
    }

    /**
     * Construct a JSONObject from a Map.
     *
     * @param map A map object that can be used to initialize the contents of the JSONObject.
     * @throws JSONException
     */
    public JSONObject(Map map) {
        this.map = new HashMap();
        if (map != null) {
            Iterator i = map.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry e = (Map.Entry) i.next();
                this.map.put(e.getKey(), wrap(e.getValue()));
            }
        }
    }

    /**
     * Construct a JSONObject from an Object using bean getters. It reflects on all of the public methods of the object.
     * For each of the methods with no parameters and a name starting with <code>"get"</code> or <code>"is"</code>
     * followed by an uppercase letter, the method is invoked, and a key and the value returned from the getter method
     * are put into the new JSONObject.
     *
     * The key is formed by removing the <code>"get"</code> or <code>"is"</code> prefix. If the second remaining
     * character is not upper case, then the first character is converted to lower case.
     *
     * For example, if an object has a method named <code>"getName"</code>, and if the result of calling
     * <code>object.getName()</code> is <code>"Larry Fine"</code>, then the JSONObject will contain
     * <code>"name": "Larry Fine"</code>.
     *
     * @param bean An object that has getter methods that should be used to make a JSONObject.
     */
    public JSONObject(Object bean) {
        this();
        populateMap(bean);
    }

    /**
     * Construct a JSONObject from an Object, using reflection to find the public members. The resulting JSONObject's
     * keys will be the strings from the names array, and the values will be the field values associated with those keys
     * in the object. If a key is not found or not visible, then it will not be copied into the new JSONObject.
     *
     * @param object An object that has fields that should be used to make a JSONObject.
     * @param names An array of strings, the names of the fields to be obtained from the object.
     */
    public JSONObject(Object object, String names[]) {
        this();
        Class c = object.getClass();
        for (int i = 0; i < names.length; i += 1) {
            String name = names[i];
            try {
                putOpt(name, c.getField(name).get(object));
            } catch (Exception ignore) {
            }
        }
    }

    /**
     * Construct a JSONObject from a source JSON text string. This is the most commonly used JSONObject constructor.
     *
     * @param source A string beginning with <code>{</code>&nbsp;<small>(left brace)</small> and ending with
     * <code>}</code>&nbsp;<small>(right brace)</small>.
     * @exception JSONException If there is a syntax error in the source string or a duplicated key.
     */
    public JSONObject(String source) throws JSONException {
        this(new JSONTokener(source));
    }

    /**
     * Accumulate values under a key. It is similar to the put method except that if there is already an object stored
     * under the key then a JSONArray is stored under the key to hold all of the accumulated values. If there is already
     * a JSONArray, then the new value is appended to it. In contrast, the put method replaces the previous value.
     *
     * @param key A key string.
     * @param value An object to be accumulated under the key.
     * @return this.
     * @throws JSONException If the value is an invalid number or if the key is null.
     */
    public JSONObject accumulate(String key, Object value) throws JSONException {
        testValidity(value);
        Object o = opt(key);
        if (o == null) {
            put(key, value instanceof JSONArray ? new JSONArray().put(value) : value);
        } else if (o instanceof JSONArray) {
            ((JSONArray) o).put(value);
        } else {
            put(key, new JSONArray().put(o).put(value));
        }
        return this;
    }

    /**
     * Append values to the array under a key. If the key does not exist in the JSONObject, then the key is put in the
     * JSONObject with its value being a JSONArray containing the value parameter. If the key was already associated
     * with a JSONArray, then the value parameter is appended to it.
     *
     * @param key A key string.
     * @param value An object to be accumulated under the key.
     * @return this.
     * @throws JSONException If the key is null or if the current value associated with the key is not a JSONArray.
     */
    public JSONObject append(String key, Object value) throws JSONException {
        testValidity(value);
        Object o = opt(key);
        if (o == null) {
            put(key, new JSONArray().put(value));
        } else if (o instanceof JSONArray) {
            put(key, ((JSONArray) o).put(value));
        } else {
            throw new JSONException("JSONObject[" + key + "] is not a JSONArray.");
        }
        return this;
    }

    /**
     * Produce a string from a double. The string "null" will be returned if the number is not finite.
     *
     * @param d A double.
     * @return A String.
     */
    static public String doubleToString(double d) {
        if (Double.isInfinite(d) || Double.isNaN(d)) {
            return "null";
        }

        // Shave off trailing zeros and decimal point, if possible.

        String s = Double.toString(d);
        if (s.indexOf('.') > 0 && s.indexOf('e') < 0 && s.indexOf('E') < 0) {
            while (s.endsWith("0")) {
                s = s.substring(0, s.length() - 1);
            }
            if (s.endsWith(".")) {
                s = s.substring(0, s.length() - 1);
            }
        }
        return s;
    }

    /**
     * Get the value object associated with a key.
     *
     * @param key A key string.
     * @return The object associated with the key.
     * @throws JSONException if the key is not found.
     */
    public Object get(String key) throws JSONException {
        Object o = opt(key);
        if (o == null) {
            throw new JSONException("JSONObject[" + quote(key) + "] not found.");
        }
        return o;
    }

    /**
     * Get the boolean value associated with a key.
     *
     * @param key A key string.
     * @return The truth.
     * @throws JSONException if the value is not a Boolean or the String "true" or "false".
     */
    public boolean getBoolean(String key) throws JSONException {
        Object o = get(key);
        if (o.equals(Boolean.FALSE) || (o instanceof String && ((String) o).equalsIgnoreCase("false"))) {
            return false;
        } else if (o.equals(Boolean.TRUE) || (o instanceof String && ((String) o).equalsIgnoreCase("true"))) {
            return true;
        }
        throw new JSONException("JSONObject[" + quote(key) + "] is not a Boolean.");
    }

    /**
     * Get the double value associated with a key.
     *
     * @param key A key string.
     * @return The numeric value.
     * @throws JSONException if the key is not found or if the value is not a Number object and cannot be converted to a
     * number.
     */
    public double getDouble(String key) throws JSONException {
        Object o = get(key);
        try {
            return o instanceof Number ? ((Number) o).doubleValue() : Double.valueOf((String) o).doubleValue();
        } catch (Exception e) {
            throw new JSONException("JSONObject[" + quote(key) + "] is not a number.");
        }
    }

    /**
     * Get the int value associated with a key. If the number value is too large for an int, it will be clipped.
     *
     * @param key A key string.
     * @return The integer value.
     * @throws JSONException if the key is not found or if the value cannot be converted to an integer.
     */
    public int getInt(String key) throws JSONException {
        Object o = get(key);
        return o instanceof Number ? ((Number) o).intValue() : (int) getDouble(key);
    }

    /**
     * Get the JSONArray value associated with a key.
     *
     * @param key A key string.
     * @return A JSONArray which is the value.
     * @throws JSONException if the key is not found or if the value is not a JSONArray.
     */
    public JSONArray getJSONArray(String key) throws JSONException {
        Object o = get(key);
        if (o instanceof JSONArray) {
            return (JSONArray) o;
        }
        throw new JSONException("JSONObject[" + quote(key) + "] is not a JSONArray.");
    }

    /**
     * Get the JSONObject value associated with a key.
     *
     * @param key A key string.
     * @return A JSONObject which is the value.
     * @throws JSONException if the key is not found or if the value is not a JSONObject.
     */
    public JSONObject getJSONObject(String key) throws JSONException {
        Object o = get(key);
        if (o instanceof JSONObject) {
            return (JSONObject) o;
        }
        throw new JSONException("JSONObject[" + quote(key) + "] is not a JSONObject.");
    }

    /**
     * Get the long value associated with a key. If the number value is too long for a long, it will be clipped.
     *
     * @param key A key string.
     * @return The long value.
     * @throws JSONException if the key is not found or if the value cannot be converted to a long.
     */
    public long getLong(String key) throws JSONException {
        Object o = get(key);
        return o instanceof Number ? ((Number) o).longValue() : (long) getDouble(key);
    }

    /**
     * Get an array of field names from a JSONObject.
     *
     * @return An array of field names, or null if there are no names.
     */
    public static String[] getNames(JSONObject jo) {
        int length = jo.length();
        if (length == 0) {
            return null;
        }
        Iterator i = jo.keys();
        String[] names = new String[length];
        int j = 0;
        while (i.hasNext()) {
            names[j] = (String) i.next();
            j += 1;
        }
        return names;
    }

    /**
     * Get an array of field names from an Object.
     *
     * @return An array of field names, or null if there are no names.
     */
    public static String[] getNames(Object object) {
        if (object == null) {
            return null;
        }
        Class klass = object.getClass();
        Field[] fields = klass.getFields();
        int length = fields.length;
        if (length == 0) {
            return null;
        }
        String[] names = new String[length];
        for (int i = 0; i < length; i += 1) {
            names[i] = fields[i].getName();
        }
        return names;
    }

    /**
     * Get the string associated with a key.
     *
     * @param key A key string.
     * @return A string which is the value.
     * @throws JSONException if the key is not found.
     */
    public String getString(String key) throws JSONException {
        return get(key).toString();
    }

    /**
     * Determine if the JSONObject contains a specific key.
     *
     * @param key A key string.
     * @return true if the key exists in the JSONObject.
     */
    public boolean has(String key) {
        return this.map.containsKey(key);
    }

    /**
     * Increment a property of a JSONObject. If there is no such property, create one with a value of 1. If there is
     * such a property, and if it is an Integer, Long, Double, or Float, then add one to it.
     *
     * @param key A key string.
     * @return this.
     * @throws JSONException If there is already a property with this name that is not an Integer, Long, Double, or
     * Float.
     */
    public JSONObject increment(String key) throws JSONException {
        Object value = opt(key);
        if (value == null) {
            put(key, 1);
        } else {
            if (value instanceof Integer) {
                put(key, ((Integer) value).intValue() + 1);
            } else if (value instanceof Long) {
                put(key, ((Long) value).longValue() + 1);
            } else if (value instanceof Double) {
                put(key, ((Double) value).doubleValue() + 1);
            } else if (value instanceof Float) {
                put(key, ((Float) value).floatValue() + 1);
            } else {
                throw new JSONException("Unable to increment [" + key + "].");
            }
        }
        return this;
    }

    /**
     * Determine if the value associated with the key is null or if there is no value.
     *
     * @param key A key string.
     * @return true if there is no value associated with the key or if the value is the JSONObject.NULL object.
     */
    public boolean isNull(String key) {
        return JSONObject.NULL.equals(opt(key));
    }

    /**
     * Get an enumeration of the keys of the JSONObject.
     *
     * @return An iterator of the keys.
     */
    public Iterator keys() {
        return this.map.keySet().iterator();
    }

    /**
     * Get the number of keys stored in the JSONObject.
     *
     * @return The number of keys in the JSONObject.
     */
    public int length() {
        return this.map.size();
    }

    /**
     * Produce a JSONArray containing the names of the elements of this JSONObject.
     *
     * @return A JSONArray containing the key strings, or null if the JSONObject is empty.
     */
    public JSONArray names() {
        JSONArray ja = new JSONArray();
        Iterator keys = keys();
        while (keys.hasNext()) {
            ja.put(keys.next());
        }
        return ja.length() == 0 ? null : ja;
    }

    /**
     * Produce a string from a Number.
     *
     * @param n A Number
     * @return A String.
     * @throws JSONException If n is a non-finite number.
     */
    static public String numberToString(Number n) throws JSONException {
        if (n == null) {
            throw new JSONException("Null pointer");
        }
        testValidity(n);

        // Shave off trailing zeros and decimal point, if possible.

        String s = n.toString();
        if (s.indexOf('.') > 0 && s.indexOf('e') < 0 && s.indexOf('E') < 0) {
            while (s.endsWith("0")) {
                s = s.substring(0, s.length() - 1);
            }
            if (s.endsWith(".")) {
                s = s.substring(0, s.length() - 1);
            }
        }
        return s;
    }

    /**
     * Get an optional value associated with a key.
     *
     * @param key A key string.
     * @return An object which is the value, or null if there is no value.
     */
    public Object opt(String key) {
        return key == null ? null : this.map.get(key);
    }

    /**
     * Get an optional boolean associated with a key. It returns false if there is no such key, or if the value is not
     * Boolean.TRUE or the String "true".
     *
     * @param key A key string.
     * @return The truth.
     */
    public boolean optBoolean(String key) {
        return optBoolean(key, false);
    }

    /**
     * Get an optional boolean associated with a key. It returns the defaultValue if there is no such key, or if it is
     * not a Boolean or the String "true" or "false" (case insensitive).
     *
     * @param key A key string.
     * @param defaultValue The default.
     * @return The truth.
     */
    public boolean optBoolean(String key, boolean defaultValue) {
        try {
            return getBoolean(key);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Get an optional double associated with a key, or NaN if there is no such key or if its value is not a number. If
     * the value is a string, an attempt will be made to evaluate it as a number.
     *
     * @param key A string which is the key.
     * @return An object which is the value.
     */
    public double optDouble(String key) {
        return optDouble(key, Double.NaN);
    }

    /**
     * Get an optional double associated with a key, or the defaultValue if there is no such key or if its value is not
     * a number. If the value is a string, an attempt will be made to evaluate it as a number.
     *
     * @param key A key string.
     * @param defaultValue The default.
     * @return An object which is the value.
     */
    public double optDouble(String key, double defaultValue) {
        try {
            Object o = opt(key);
            return o instanceof Number ? ((Number) o).doubleValue() : new Double((String) o).doubleValue();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Get an optional int value associated with a key, or zero if there is no such key or if the value is not a number.
     * If the value is a string, an attempt will be made to evaluate it as a number.
     *
     * @param key A key string.
     * @return An object which is the value.
     */
    public int optInt(String key) {
        return optInt(key, 0);
    }

    /**
     * Get an optional int value associated with a key, or the default if there is no such key or if the value is not a
     * number. If the value is a string, an attempt will be made to evaluate it as a number.
     *
     * @param key A key string.
     * @param defaultValue The default.
     * @return An object which is the value.
     */
    public int optInt(String key, int defaultValue) {
        try {
            return getInt(key);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Get an optional JSONArray associated with a key. It returns null if there is no such key, or if its value is not
     * a JSONArray.
     *
     * @param key A key string.
     * @return A JSONArray which is the value.
     */
    public JSONArray optJSONArray(String key) {
        Object o = opt(key);
        return o instanceof JSONArray ? (JSONArray) o : null;
    }

    /**
     * Get an optional JSONObject associated with a key. It returns null if there is no such key, or if its value is not
     * a JSONObject.
     *
     * @param key A key string.
     * @return A JSONObject which is the value.
     */
    public JSONObject optJSONObject(String key) {
        Object o = opt(key);
        return o instanceof JSONObject ? (JSONObject) o : null;
    }

    /**
     * Get an optional long value associated with a key, or zero if there is no such key or if the value is not a
     * number. If the value is a string, an attempt will be made to evaluate it as a number.
     *
     * @param key A key string.
     * @return An object which is the value.
     */
    public long optLong(String key) {
        return optLong(key, 0);
    }

    /**
     * Get an optional long value associated with a key, or the default if there is no such key or if the value is not a
     * number. If the value is a string, an attempt will be made to evaluate it as a number.
     *
     * @param key A key string.
     * @param defaultValue The default.
     * @return An object which is the value.
     */
    public long optLong(String key, long defaultValue) {
        try {
            return getLong(key);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Get an optional string associated with a key. It returns an empty string if there is no such key. If the value is
     * not a string and is not null, then it is coverted to a string.
     *
     * @param key A key string.
     * @return A string which is the value.
     */
    public String optString(String key) {
        return optString(key, "");
    }

    /**
     * Get an optional string associated with a key. It returns the defaultValue if there is no such key.
     *
     * @param key A key string.
     * @param defaultValue The default.
     * @return A string which is the value.
     */
    public String optString(String key, String defaultValue) {
        Object o = opt(key);
        return o != null ? o.toString() : defaultValue;
    }

    private void populateMap(Object bean) {
        Class klass = bean.getClass();

        // If klass is a System class then set includeSuperClass to false.

        boolean includeSuperClass = klass.getClassLoader() != null;

        Method[] methods = (includeSuperClass) ? klass.getMethods() : klass.getDeclaredMethods();
        for (int i = 0; i < methods.length; i += 1) {
            try {
                Method method = methods[i];
                if (Modifier.isPublic(method.getModifiers())) {
                    String name = method.getName();
                    String key = "";
                    if (name.startsWith("get")) {
                        if (name.equals("getClass") || name.equals("getDeclaringClass")) {
                            key = "";
                        } else {
                            key = name.substring(3);
                        }
                    }
                    // modified by wyang: remove the "isEmpty" case in HashMap
                    // else if (name.startsWith("is")) {
                    // key = name.substring(2);
                    // }
                    if (key.length() > 0 && Character.isUpperCase(key.charAt(0)) && method.getParameterTypes().length == 0) {
                        if (key.length() == 1) {
                            key = key.toLowerCase();
                        } else if (!Character.isUpperCase(key.charAt(1))) {
                            key = key.substring(0, 1).toLowerCase() + key.substring(1);
                        }

                        Object result = method.invoke(bean, (Object[]) null);

                        map.put(key, wrap(result));
                    }
                }
            } catch (Exception ignore) {
            }
        }
    }

    /**
     * Put a key/boolean pair in the JSONObject.
     *
     * @param key A key string.
     * @param value A boolean which is the value.
     * @return this.
     * @throws JSONException If the key is null.
     */
    public JSONObject put(String key, boolean value) throws JSONException {
        put(key, value ? Boolean.TRUE : Boolean.FALSE);
        return this;
    }

    /**
     * Put a key/value pair in the JSONObject, where the value will be a JSONArray which is produced from a Collection.
     *
     * @param key A key string.
     * @param value A Collection value.
     * @return this.
     * @throws JSONException
     */
    public JSONObject put(String key, Collection value) throws JSONException {
        put(key, new JSONArray(value));
        return this;
    }

    /**
     * Put a key/double pair in the JSONObject.
     *
     * @param key A key string.
     * @param value A double which is the value.
     * @return this.
     * @throws JSONException If the key is null or if the number is invalid.
     */
    public JSONObject put(String key, double value) throws JSONException {
        put(key, new Double(value));
        return this;
    }

    /**
     * Put a key/int pair in the JSONObject.
     *
     * @param key A key string.
     * @param value An int which is the value.
     * @return this.
     * @throws JSONException If the key is null.
     */
    public JSONObject put(String key, int value) throws JSONException {
        put(key, new Integer(value));
        return this;
    }

    /**
     * Put a key/long pair in the JSONObject.
     *
     * @param key A key string.
     * @param value A long which is the value.
     * @return this.
     * @throws JSONException If the key is null.
     */
    public JSONObject put(String key, long value) throws JSONException {
        put(key, new Long(value));
        return this;
    }

    /**
     * Put a key/value pair in the JSONObject, where the value will be a JSONObject which is produced from a Map.
     *
     * @param key A key string.
     * @param value A Map value.
     * @return this.
     * @throws JSONException
     */
    public JSONObject put(String key, Map value) throws JSONException {
        put(key, new JSONObject(value));
        return this;
    }

    /**
     * Put a key/value pair in the JSONObject. If the value is null, then the key will be removed from the JSONObject if
     * it is present.
     *
     * @param key A key string.
     * @param value An object which is the value. It should be of one of these types: Boolean, Double, Integer,
     * JSONArray, JSONObject, Long, String, or the JSONObject.NULL object.
     * @return this.
     * @throws JSONException If the value is non-finite number or if the key is null.
     */
    public JSONObject put(String key, Object value) throws JSONException {
        if (key == null) {
            throw new JSONException("Null key.");
        }
        if (value != null) {
            testValidity(value);
            this.map.put(key, value);
        } else {
            remove(key);
        }
        return this;
    }

    /**
     * Put a key/value pair in the JSONObject, but only if the key and the value are both non-null, and only if there is
     * not already a member with that name.
     *
     * @param key
     * @param value
     * @return his.
     * @throws JSONException if the key is a duplicate
     */
    public JSONObject putOnce(String key, Object value) throws JSONException {
        if (key != null && value != null) {
            if (opt(key) != null) {
                throw new JSONException("Duplicate key \"" + key + "\"");
            }
            put(key, value);
        }
        return this;
    }

    /**
     * Put a key/value pair in the JSONObject, but only if the key and the value are both non-null.
     *
     * @param key A key string.
     * @param value An object which is the value. It should be of one of these types: Boolean, Double, Integer,
     * JSONArray, JSONObject, Long, String, or the JSONObject.NULL object.
     * @return this.
     * @throws JSONException If the value is a non-finite number.
     */
    public JSONObject putOpt(String key, Object value) throws JSONException {
        if (key != null && value != null) {
            put(key, value);
        }
        return this;
    }

    /**
     * Produce a string in double quotes with backslash sequences in all the right places. A backslash will be inserted
     * within </, allowing JSON text to be delivered in HTML. In JSON text, a string cannot contain a control character
     * or an unescaped quote or backslash.
     *
     * @param string A String
     * @return A String correctly formatted for insertion in a JSON text.
     */
    public static String quote(String string) {
        if (string == null || string.length() == 0) {
            return "\"\"";
        }

        char b;
        char c = 0;
        int i;
        int len = string.length();
        StringBuffer sb = new StringBuffer(len + 4);
        String t;

        sb.append('"');
        for (i = 0; i < len; i += 1) {
            b = c;
            c = string.charAt(i);
            switch (c) {
            case '\\':
            case '"':
                sb.append('\\');
                sb.append(c);
                break;
            case '/':
                if (b == '<') {
                    sb.append('\\');
                }
                sb.append(c);
                break;
            case '\b':
                sb.append("\\b");
                break;
            case '\t':
                sb.append("\\t");
                break;
            case '\n':
                sb.append("\\n");
                break;
            case '\f':
                sb.append("\\f");
                break;
            case '\r':
                sb.append("\\r");
                break;
            default:
                if (c < ' ' || (c >= '\u0080' && c < '\u00a0') || (c >= '\u2000' && c < '\u2100')) {
                    t = "000" + Integer.toHexString(c);
                    sb.append("\\u" + t.substring(t.length() - 4));
                } else {
                    sb.append(c);
                }
            }
        }
        sb.append('"');
        return sb.toString();
    }

    /**
     * Remove a name and its value, if present.
     *
     * @param key The name to be removed.
     * @return The value that was associated with the name, or null if there was no value.
     */
    public Object remove(String key) {
        return this.map.remove(key);
    }

    /**
     * Get an enumeration of the keys of the JSONObject. The keys will be sorted alphabetically.
     *
     * @return An iterator of the keys.
     */
    public Iterator sortedKeys() {
        return new TreeSet(this.map.keySet()).iterator();
    }

    /**
     * Try to convert a string into a number, boolean, or null. If the string can't be converted, return the string.
     *
     * @param s A String.
     * @return A simple JSON value.
     */
    static public Object stringToValue(String s) {
        if (s.equals("")) {
            return s;
        }
        if (s.equalsIgnoreCase("true")) {
            return Boolean.TRUE;
        }
        if (s.equalsIgnoreCase("false")) {
            return Boolean.FALSE;
        }
        if (s.equalsIgnoreCase("null")) {
            return JSONObject.NULL;
        }

        /*
         * If it might be a number, try converting it. We support the non-standard 0x- convention. If a number cannot be
         * produced, then the value will just be a string. Note that the 0x-, plus, and implied string conventions are
         * non-standard. A JSON parser may accept non-JSON forms as long as it accepts all correct JSON forms.
         */

        char b = s.charAt(0);
        if ((b >= '0' && b <= '9') || b == '.' || b == '-' || b == '+') {
            if (b == '0' && s.length() > 2 && (s.charAt(1) == 'x' || s.charAt(1) == 'X')) {
                try {
                    return new Integer(Integer.parseInt(s.substring(2), 16));
                } catch (Exception ignore) {
                }
            }
            try {
                if (s.indexOf('.') > -1 || s.indexOf('e') > -1 || s.indexOf('E') > -1) {
                    return Double.valueOf(s);
                } else {
                    Long myLong = new Long(s);
                    if (myLong.longValue() == myLong.intValue()) {
                        return new Integer(myLong.intValue());
                    } else {
                        return myLong;
                    }
                }
            } catch (Exception ignore) {
            }
        }
        return s;
    }

    /**
     * Throw an exception if the object is an NaN or infinite number.
     *
     * @param o The object to test.
     * @throws JSONException If o is a non-finite number.
     */
    static void testValidity(Object o) throws JSONException {
        if (o != null) {
            if (o instanceof Double) {
                if (((Double) o).isInfinite() || ((Double) o).isNaN()) {
                    throw new JSONException("JSON does not allow non-finite numbers.");
                }
            } else if (o instanceof Float) {
                if (((Float) o).isInfinite() || ((Float) o).isNaN()) {
                    throw new JSONException("JSON does not allow non-finite numbers.");
                }
            }
        }
    }

    /**
     * Produce a JSONArray containing the values of the members of this JSONObject.
     *
     * @param names A JSONArray containing a list of key strings. This determines the sequence of the values in the
     * result.
     * @return A JSONArray of values.
     * @throws JSONException If any of the values are non-finite numbers.
     */
    public JSONArray toJSONArray(JSONArray names) throws JSONException {
        if (names == null || names.length() == 0) {
            return null;
        }
        JSONArray ja = new JSONArray();
        for (int i = 0; i < names.length(); i += 1) {
            ja.put(this.opt(names.getString(i)));
        }
        return ja;
    }

    /**
     * Make a JSON text of this JSONObject. For compactness, no whitespace is added. If this would not result in a
     * syntactically correct JSON text, then null will be returned instead.
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @return a printable, displayable, portable, transmittable representation of the object, beginning with
     * <code>{</code>&nbsp;<small>(left brace)</small> and ending with <code>}</code>&nbsp;<small>(right brace)</small>.
     */
    public String toString() {
        try {
            Iterator keys = keys();
            StringBuffer sb = new StringBuffer("{");

            while (keys.hasNext()) {
                if (sb.length() > 1) {
                    sb.append(',');
                }
                Object o = keys.next();
                sb.append(quote(o.toString()));
                sb.append(':');
                sb.append(valueToString(this.map.get(o)));
            }
            sb.append('}');
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Make a prettyprinted JSON text of this JSONObject.
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @param indentFactor The number of spaces to add to each level of indentation.
     * @return a printable, displayable, portable, transmittable representation of the object, beginning with
     * <code>{</code>&nbsp;<small>(left brace)</small> and ending with <code>}</code>&nbsp;<small>(right brace)</small>.
     * @throws JSONException If the object contains an invalid number.
     */
    public String toString(int indentFactor) throws JSONException {
        return toString(indentFactor, 0);
    }

    /**
     * Make a prettyprinted JSON text of this JSONObject.
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @param indentFactor The number of spaces to add to each level of indentation.
     * @param indent The indentation of the top level.
     * @return a printable, displayable, transmittable representation of the object, beginning with <code>{</code>
     * &nbsp;<small>(left brace)</small> and ending with <code>}</code>&nbsp;<small>(right brace)</small>.
     * @throws JSONException If the object contains an invalid number.
     */
    String toString(int indentFactor, int indent) throws JSONException {
        int j;
        int n = length();
        if (n == 0) {
            return "{}";
        }
        Iterator keys = sortedKeys();
        StringBuffer sb = new StringBuffer("{");
        int newindent = indent + indentFactor;
        Object o;
        if (n == 1) {
            o = keys.next();
            sb.append(quote(o.toString()));
            sb.append(": ");
            sb.append(valueToString(this.map.get(o), indentFactor, indent));
        } else {
            while (keys.hasNext()) {
                o = keys.next();
                if (sb.length() > 1) {
                    sb.append(",\n");
                } else {
                    sb.append('\n');
                }
                for (j = 0; j < newindent; j += 1) {
                    sb.append(' ');
                }
                sb.append(quote(o.toString()));
                sb.append(": ");
                sb.append(valueToString(this.map.get(o), indentFactor, newindent));
            }
            if (sb.length() > 1) {
                sb.append('\n');
                for (j = 0; j < indent; j += 1) {
                    sb.append(' ');
                }
            }
        }
        sb.append('}');
        return sb.toString();
    }

    /**
     * Make a JSON text of an Object value. If the object has an value.toJSONString() method, then that method will be
     * used to produce the JSON text. The method is required to produce a strictly conforming text. If the object does
     * not contain a toJSONString method (which is the most common case), then a text will be produced by other means.
     * If the value is an array or Collection, then a JSONArray will be made from it and its toJSONString method will be
     * called. If the value is a MAP, then a JSONObject will be made from it and its toJSONString method will be called.
     * Otherwise, the value's toString method will be called, and the result will be quoted.
     *
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @param value The value to be serialized.
     * @return a printable, displayable, transmittable representation of the object, beginning with <code>{</code>
     * &nbsp;<small>(left brace)</small> and ending with <code>}</code>&nbsp;<small>(right brace)</small>.
     * @throws JSONException If the value is or contains an invalid number.
     */
    static String valueToString(Object value) throws JSONException {
        if (value == null || value.equals(null)) {
            return "null";
        }
        if (value instanceof JSONString) {
            Object o;
            try {
                o = ((JSONString) value).toJSONString();
            } catch (Exception e) {
                throw new JSONException(e);
            }
            if (o instanceof String) {
                return (String) o;
            }
            throw new JSONException("Bad value from toJSONString: " + o);
        }
        if (value instanceof Number) {
            return numberToString((Number) value);
        }
        if (value instanceof Boolean || value instanceof JSONObject || value instanceof JSONArray) {
            return value.toString();
        }
        if (value instanceof Map) {
            return new JSONObject((Map) value).toString();
        }
        if (value instanceof Collection) {
            return new JSONArray((Collection) value).toString();
        }
        if (value.getClass().isArray()) {
            return new JSONArray(value).toString();
        }
        return quote(value.toString());
    }

    /**
     * Make a prettyprinted JSON text of an object value.
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @param value The value to be serialized.
     * @param indentFactor The number of spaces to add to each level of indentation.
     * @param indent The indentation of the top level.
     * @return a printable, displayable, transmittable representation of the object, beginning with <code>{</code>
     * &nbsp;<small>(left brace)</small> and ending with <code>}</code>&nbsp;<small>(right brace)</small>.
     * @throws JSONException If the object contains an invalid number.
     */
    static String valueToString(Object value, int indentFactor, int indent) throws JSONException {
        if (value == null || value.equals(null)) {
            return "null";
        }
        try {
            if (value instanceof JSONString) {
                Object o = ((JSONString) value).toJSONString();
                if (o instanceof String) {
                    return (String) o;
                }
            }
        } catch (Exception ignore) {
        }
        if (value instanceof Number) {
            return numberToString((Number) value);
        }
        if (value instanceof Boolean) {
            return value.toString();
        }
        if (value instanceof JSONObject) {
            return ((JSONObject) value).toString(indentFactor, indent);
        }
        if (value instanceof JSONArray) {
            return ((JSONArray) value).toString(indentFactor, indent);
        }
        if (value instanceof Map) {
            return new JSONObject((Map) value).toString(indentFactor, indent);
        }
        if (value instanceof Collection) {
            return new JSONArray((Collection) value).toString(indentFactor, indent);
        }
        if (value.getClass().isArray()) {
            return new JSONArray(value).toString(indentFactor, indent);
        }
        return quote(value.toString());
    }

    /**
     * Wrap an object, if necessary. If the object is null, return the NULL object. If it is an array or collection,
     * wrap it in a JSONArray. If it is a map, wrap it in a JSONObject. If it is a standard property (Double, String, et
     * al) then it is already wrapped. Otherwise, if it comes from one of the java packages, turn it into a string. And
     * if it doesn't, try to wrap it in a JSONObject. If the wrapping fails, then null is returned.
     *
     * @param object The object to wrap
     * @return The wrapped value
     */
    static Object wrap(Object object) {
        try {
            if (object == null) {
                return NULL;
            }
            if (object instanceof JSONObject || object instanceof JSONArray || object instanceof Byte
                    || object instanceof Character || object instanceof Short || object instanceof Integer
                    || object instanceof Long || object instanceof Boolean || object instanceof Float || object instanceof Double
                    || object instanceof String || NULL.equals(object)) {
                return object;
            }

            if (object instanceof Collection) {
                return new JSONArray((Collection) object);
            }
            if (object.getClass().isArray()) {
                return new JSONArray(object);
            }
            if (object instanceof Map) {
                return new JSONObject((Map) object);
            }
            Package objectPackage = object.getClass().getPackage();
            String objectPackageName = (objectPackage != null ? objectPackage.getName() : "");
            if (objectPackageName.startsWith("java.") || objectPackageName.startsWith("javax.")
                    || object.getClass().getClassLoader() == null) {
                return object.toString();
            }
            return new JSONObject(object);
        } catch (Exception exception) {
            return null;
        }
    }

    /**
     * Write the contents of the JSONObject as JSON text to a writer. For compactness, no whitespace is added.
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @return The writer.
     * @throws JSONException
     */
    public Writer write(Writer writer) throws JSONException {
        try {
            boolean b = false;
            Iterator keys = keys();
            writer.write('{');

            while (keys.hasNext()) {
                if (b) {
                    writer.write(',');
                }
                Object k = keys.next();
                writer.write(quote(k.toString()));
                writer.write(':');
                Object v = this.map.get(k);
                if (v instanceof JSONObject) {
                    ((JSONObject) v).write(writer);
                } else if (v instanceof JSONArray) {
                    ((JSONArray) v).write(writer);
                } else {
                    writer.write(valueToString(v));
                }
                b = true;
            }
            writer.write('}');
            return writer;
        } catch (IOException exception) {
            throw new JSONException(exception);
        }
    }
}

/**
 * A JSONTokener takes a source string and extracts characters and tokens from it. It is used by the JSONObject and
 * JSONArray constructors to parse JSON source strings.
 *
 * @author JSON.org
 * @version 2010-02-02
 */
class JSONTokener {

    private int character;

    private boolean eof;

    private int index;

    private int line;

    private char previous;

    private Reader reader;

    private boolean usePrevious;

    /**
     * Construct a JSONTokener from a reader.
     *
     * @param reader A reader.
     */
    public JSONTokener(Reader reader) {
        this.reader = reader.markSupported() ? reader : new BufferedReader(reader);
        this.eof = false;
        this.usePrevious = false;
        this.previous = 0;
        this.index = 0;
        this.character = 1;
        this.line = 1;
    }

    /**
     * Construct a JSONTokener from a string.
     *
     * @param s A source string.
     */
    public JSONTokener(String s) {
        this(new StringReader(s));
    }

    /**
     * Back up one character. This provides a sort of lookahead capability, so that you can test for a digit or letter
     * before attempting to parse the next number or identifier.
     */
    public void back() throws JSONException {
        if (usePrevious || index <= 0) {
            throw new JSONException("Stepping back two steps is not supported");
        }
        this.index -= 1;
        this.character -= 1;
        this.usePrevious = true;
        this.eof = false;
    }

    /**
     * Get the hex value of a character (base16).
     *
     * @param c A character between '0' and '9' or between 'A' and 'F' or between 'a' and 'f'.
     * @return An int between 0 and 15, or -1 if c was not a hex digit.
     */
    public static int dehexchar(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        if (c >= 'A' && c <= 'F') {
            return c - ('A' - 10);
        }
        if (c >= 'a' && c <= 'f') {
            return c - ('a' - 10);
        }
        return -1;
    }

    public boolean end() {
        return eof && !usePrevious;
    }

    /**
     * Determine if the source string still contains characters that next() can consume.
     *
     * @return true if not yet at the end of the source.
     */
    public boolean more() throws JSONException {
        next();
        if (end()) {
            return false;
        }
        back();
        return true;
    }

    /**
     * Get the next character in the source string.
     *
     * @return The next character, or 0 if past the end of the source string.
     */
    public char next() throws JSONException {
        int c;
        if (this.usePrevious) {
            this.usePrevious = false;
            c = this.previous;
        } else {
            try {
                c = this.reader.read();
            } catch (IOException exception) {
                throw new JSONException(exception);
            }

            if (c <= 0) { // End of stream
                this.eof = true;
                c = 0;
            }
        }
        this.index += 1;
        if (this.previous == '\r') {
            this.line += 1;
            this.character = c == '\n' ? 0 : 1;
        } else if (c == '\n') {
            this.line += 1;
            this.character = 0;
        } else {
            this.character += 1;
        }
        this.previous = (char) c;
        return this.previous;
    }

    /**
     * Consume the next character, and check that it matches a specified character.
     *
     * @param c The character to match.
     * @return The character.
     * @throws JSONException if the character does not match.
     */
    public char next(char c) throws JSONException {
        char n = next();
        if (n != c) {
            throw syntaxError("Expected '" + c + "' and instead saw '" + n + "'");
        }
        return n;
    }

    /**
     * Get the next n characters.
     *
     * @param n The number of characters to take.
     * @return A string of n characters.
     * @throws JSONException Substring bounds error if there are not n characters remaining in the source string.
     */
    public String next(int n) throws JSONException {
        if (n == 0) {
            return "";
        }

        char[] buffer = new char[n];
        int pos = 0;

        while (pos < n) {
            buffer[pos] = next();
            if (end()) {
                throw syntaxError("Substring bounds error");
            }
            pos += 1;
        }
        return new String(buffer);
    }

    /**
     * Get the next char in the string, skipping whitespace.
     *
     * @throws JSONException
     * @return A character, or 0 if there are no more characters.
     */
    public char nextClean() throws JSONException {
        for (;;) {
            char c = next();
            if (c == 0 || c > ' ') {
                return c;
            }
        }
    }

    /**
     * Return the characters up to the next close quote character. Backslash processing is done. The formal JSON format
     * does not allow strings in single quotes, but an implementation is allowed to accept them.
     *
     * @param quote The quoting character, either <code>"</code>&nbsp;<small>(double quote)</small> or <code>'</code>
     * &nbsp;<small>(single quote)</small>.
     * @return A String.
     * @throws JSONException Unterminated string.
     */
    public String nextString(char quote) throws JSONException {
        char c;
        StringBuffer sb = new StringBuffer();
        for (;;) {
            c = next();
            switch (c) {
            case 0:
            case '\n':
            case '\r':
                throw syntaxError("Unterminated string");
            case '\\':
                c = next();
                switch (c) {
                case 'b':
                    sb.append('\b');
                    break;
                case 't':
                    sb.append('\t');
                    break;
                case 'n':
                    sb.append('\n');
                    break;
                case 'f':
                    sb.append('\f');
                    break;
                case 'r':
                    sb.append('\r');
                    break;
                case 'u':
                    sb.append((char) Integer.parseInt(next(4), 16));
                    break;
                case '"':
                case '\'':
                case '\\':
                case '/':
                    sb.append(c);
                    break;
                default:
                    throw syntaxError("Illegal escape.");
                }
                break;
            default:
                if (c == quote) {
                    return sb.toString();
                }
                sb.append(c);
            }
        }
    }

    /**
     * Get the text up but not including the specified character or the end of line, whichever comes first.
     *
     * @param d A delimiter character.
     * @return A string.
     */
    public String nextTo(char d) throws JSONException {
        StringBuffer sb = new StringBuffer();
        for (;;) {
            char c = next();
            if (c == d || c == 0 || c == '\n' || c == '\r') {
                if (c != 0) {
                    back();
                }
                return sb.toString().trim();
            }
            sb.append(c);
        }
    }

    /**
     * Get the text up but not including one of the specified delimiter characters or the end of line, whichever comes
     * first.
     *
     * @param delimiters A set of delimiter characters.
     * @return A string, trimmed.
     */
    public String nextTo(String delimiters) throws JSONException {
        char c;
        StringBuffer sb = new StringBuffer();
        for (;;) {
            c = next();
            if (delimiters.indexOf(c) >= 0 || c == 0 || c == '\n' || c == '\r') {
                if (c != 0) {
                    back();
                }
                return sb.toString().trim();
            }
            sb.append(c);
        }
    }

    /**
     * Get the next value. The value can be a Boolean, Double, Integer, JSONArray, JSONObject, Long, or String, or the
     * JSONObject.NULL object.
     *
     * @throws JSONException If syntax error.
     *
     * @return An object.
     */
    public Object nextValue() throws JSONException {
        char c = nextClean();
        String s;

        switch (c) {
        case '"':
        case '\'':
            return nextString(c);
        case '{':
            back();
            return new JSONObject(this);
        case '[':
        case '(':
            back();
            return new JSONArray(this);
        }

        /*
         * Handle unquoted text. This could be the values true, false, or null, or it can be a number. An implementation
         * (such as this one) is allowed to also accept non-standard forms.
         *
         * Accumulate characters until we reach the end of the text or a formatting character.
         */

        StringBuffer sb = new StringBuffer();
        while (c >= ' ' && ",:]}/\\\"[{;=#".indexOf(c) < 0) {
            sb.append(c);
            c = next();
        }
        back();

        s = sb.toString().trim();
        if (s.equals("")) {
            throw syntaxError("Missing value");
        }
        return JSONObject.stringToValue(s);
    }

    /**
     * Skip characters until the next character is the requested character. If the requested character is not found, no
     * characters are skipped.
     *
     * @param to A character to skip to.
     * @return The requested character, or zero if the requested character is not found.
     */
    public char skipTo(char to) throws JSONException {
        char c;
        try {
            int startIndex = this.index;
            int startCharacter = this.character;
            int startLine = this.line;
            reader.mark(Integer.MAX_VALUE);
            do {
                c = next();
                if (c == 0) {
                    reader.reset();
                    this.index = startIndex;
                    this.character = startCharacter;
                    this.line = startLine;
                    return c;
                }
            } while (c != to);
        } catch (IOException exc) {
            throw new JSONException(exc);
        }

        back();
        return c;
    }

    /**
     * Make a JSONException to signal a syntax error.
     *
     * @param message The error message.
     * @return A JSONException object, suitable for throwing
     */
    public JSONException syntaxError(String message) {
        return new JSONException(message + toString());
    }

    /**
     * Make a printable string of this JSONTokener.
     *
     * @return " at {index} [character {character} line {line}]"
     */
    public String toString() {
        return " at " + index + " [character " + this.character + " line " + this.line + "]";
    }
}

/**
 * The JSONException is thrown by the JSON.org classes when things are amiss.
 *
 * @author JSON.org
 * @version 2008-09-18
 */
class JSONException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 0;

    private Throwable cause;

    /**
     * Constructs a JSONException with an explanatory message.
     *
     * @param message Detail about the reason for the exception.
     */
    public JSONException(String message) {
        super(message);
    }

    public JSONException(Throwable t) {
        super(t.getMessage());
        this.cause = t;
    }

    public Throwable getCause() {
        return this.cause;
    }
}

/**
 * A JSONArray is an ordered sequence of values. Its external text form is a string wrapped in square brackets with
 * commas separating the values. The internal form is an object having <code>get</code> and <code>opt</code> methods for
 * accessing the values by index, and <code>put</code> methods for adding or replacing values. The values can be any of
 * these types: <code>Boolean</code>, <code>JSONArray</code>, <code>JSONObject</code>, <code>Number</code>,
 * <code>String</code>, or the <code>JSONObject.NULL object</code>.
 * <p>
 * The constructor can convert a JSON text into a Java object. The <code>toString</code> method converts to JSON text.
 * <p>
 * A <code>get</code> method returns a value if one can be found, and throws an exception if one cannot be found. An
 * <code>opt</code> method returns a default value instead of throwing an exception, and so is useful for obtaining
 * optional values.
 * <p>
 * The generic <code>get()</code> and <code>opt()</code> methods return an object which you can cast or query for type.
 * There are also typed <code>get</code> and <code>opt</code> methods that do type checking and type coercion for you.
 * <p>
 * The texts produced by the <code>toString</code> methods strictly conform to JSON syntax rules. The constructors are
 * more forgiving in the texts they will accept:
 * <ul>
 * <li>An extra <code>,</code>&nbsp;<small>(comma)</small> may appear just before the closing bracket.</li>
 * <li>The <code>null</code> value will be inserted when there is <code>,</code>&nbsp;<small>(comma)</small> elision.</li>
 * <li>Strings may be quoted with <code>'</code>&nbsp;<small>(single quote)</small>.</li>
 * <li>Strings do not need to be quoted at all if they do not begin with a quote or single quote, and if they do not
 * contain leading or trailing spaces, and if they do not contain any of these characters:
 * <code>{ } [ ] / \ : , = ; #</code> and if they do not look like numbers and if they are not the reserved words
 * <code>true</code>, <code>false</code>, or <code>null</code>.</li>
 * <li>Values can be separated by <code>;</code> <small>(semicolon)</small> as well as by <code>,</code>
 * <small>(comma)</small>.</li>
 * <li>Numbers may have the <code>0x-</code> <small>(hex)</small> prefix.</li>
 * </ul>
 *
 * @author JSON.org
 * @version 2009-04-14
 */
class JSONArray {

    /**
     * The arrayList where the JSONArray's properties are kept.
     */
    private ArrayList myArrayList;

    /**
     * Construct an empty JSONArray.
     */
    public JSONArray() {
        this.myArrayList = new ArrayList();
    }

    /**
     * Construct a JSONArray from a JSONTokener.
     *
     * @param x A JSONTokener
     * @throws JSONException If there is a syntax error.
     */
    public JSONArray(JSONTokener x) throws JSONException {
        this();
        char c = x.nextClean();
        char q;
        if (c == '[') {
            q = ']';
        } else if (c == '(') {
            q = ')';
        } else {
            throw x.syntaxError("A JSONArray text must start with '['");
        }
        if (x.nextClean() == ']') {
            return;
        }
        x.back();
        for (;;) {
            if (x.nextClean() == ',') {
                x.back();
                this.myArrayList.add(null);
            } else {
                x.back();
                this.myArrayList.add(x.nextValue());
            }
            c = x.nextClean();
            switch (c) {
            case ';':
            case ',':
                if (x.nextClean() == ']') {
                    return;
                }
                x.back();
                break;
            case ']':
            case ')':
                if (q != c) {
                    throw x.syntaxError("Expected a '" + new Character(q) + "'");
                }
                return;
            default:
                throw x.syntaxError("Expected a ',' or ']'");
            }
        }
    }

    /**
     * Construct a JSONArray from a source JSON text.
     *
     * @param source A string that begins with <code>[</code>&nbsp;<small>(left bracket)</small> and ends with
     * <code>]</code>&nbsp;<small>(right bracket)</small>.
     * @throws JSONException If there is a syntax error.
     */
    public JSONArray(String source) throws JSONException {
        this(new JSONTokener(source));
    }

    /**
     * Construct a JSONArray from a Collection.
     *
     * @param collection A Collection.
     */
    public JSONArray(Collection collection) {
        this.myArrayList = new ArrayList();
        if (collection != null) {
            Iterator iter = collection.iterator();
            ;
            while (iter.hasNext()) {
                Object o = iter.next();
                this.myArrayList.add(JSONObject.wrap(o));
            }
        }
    }

    /**
     * Construct a JSONArray from an array
     *
     * @throws JSONException If not an array.
     */
    public JSONArray(Object array) throws JSONException {
        this();
        if (array.getClass().isArray()) {
            int length = Array.getLength(array);
            for (int i = 0; i < length; i += 1) {
                this.put(JSONObject.wrap(Array.get(array, i)));
            }
        } else {
            throw new JSONException("JSONArray initial value should be a string or collection or array.");
        }
    }

    /**
     * Get the object value associated with an index.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return An object value.
     * @throws JSONException If there is no value for the index.
     */
    public Object get(int index) throws JSONException {
        Object o = opt(index);
        if (o == null) {
            throw new JSONException("JSONArray[" + index + "] not found.");
        }
        return o;
    }

    /**
     * Get the boolean value associated with an index. The string values "true" and "false" are converted to boolean.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return The truth.
     * @throws JSONException If there is no value for the index or if the value is not convertable to boolean.
     */
    public boolean getBoolean(int index) throws JSONException {
        Object o = get(index);
        if (o.equals(Boolean.FALSE) || (o instanceof String && ((String) o).equalsIgnoreCase("false"))) {
            return false;
        } else if (o.equals(Boolean.TRUE) || (o instanceof String && ((String) o).equalsIgnoreCase("true"))) {
            return true;
        }
        throw new JSONException("JSONArray[" + index + "] is not a Boolean.");
    }

    /**
     * Get the double value associated with an index.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return The value.
     * @throws JSONException If the key is not found or if the value cannot be converted to a number.
     */
    public double getDouble(int index) throws JSONException {
        Object o = get(index);
        try {
            return o instanceof Number ? ((Number) o).doubleValue() : Double.valueOf((String) o).doubleValue();
        } catch (Exception e) {
            throw new JSONException("JSONArray[" + index + "] is not a number.");
        }
    }

    /**
     * Get the int value associated with an index.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return The value.
     * @throws JSONException If the key is not found or if the value cannot be converted to a number. if the value
     * cannot be converted to a number.
     */
    public int getInt(int index) throws JSONException {
        Object o = get(index);
        return o instanceof Number ? ((Number) o).intValue() : (int) getDouble(index);
    }

    /**
     * Get the JSONArray associated with an index.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return A JSONArray value.
     * @throws JSONException If there is no value for the index. or if the value is not a JSONArray
     */
    public JSONArray getJSONArray(int index) throws JSONException {
        Object o = get(index);
        if (o instanceof JSONArray) {
            return (JSONArray) o;
        }
        throw new JSONException("JSONArray[" + index + "] is not a JSONArray.");
    }

    /**
     * Get the JSONObject associated with an index.
     *
     * @param index subscript
     * @return A JSONObject value.
     * @throws JSONException If there is no value for the index or if the value is not a JSONObject
     */
    public JSONObject getJSONObject(int index) throws JSONException {
        Object o = get(index);
        if (o instanceof JSONObject) {
            return (JSONObject) o;
        }
        throw new JSONException("JSONArray[" + index + "] is not a JSONObject.");
    }

    /**
     * Get the long value associated with an index.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return The value.
     * @throws JSONException If the key is not found or if the value cannot be converted to a number.
     */
    public long getLong(int index) throws JSONException {
        Object o = get(index);
        return o instanceof Number ? ((Number) o).longValue() : (long) getDouble(index);
    }

    /**
     * Get the string associated with an index.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return A string value.
     * @throws JSONException If there is no value for the index.
     */
    public String getString(int index) throws JSONException {
        return get(index).toString();
    }

    /**
     * Determine if the value is null.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return true if the value at the index is null, or if there is no value.
     */
    public boolean isNull(int index) {
        return JSONObject.NULL.equals(opt(index));
    }

    /**
     * Make a string from the contents of this JSONArray. The <code>separator</code> string is inserted between each
     * element. Warning: This method assumes that the data structure is acyclical.
     *
     * @param separator A string that will be inserted between the elements.
     * @return a string.
     * @throws JSONException If the array contains an invalid number.
     */
    public String join(String separator) throws JSONException {
        int len = length();
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < len; i += 1) {
            if (i > 0) {
                sb.append(separator);
            }
            sb.append(JSONObject.valueToString(this.myArrayList.get(i)));
        }
        return sb.toString();
    }

    /**
     * Get the number of elements in the JSONArray, included nulls.
     *
     * @return The length (or size).
     */
    public int length() {
        return this.myArrayList.size();
    }

    /**
     * Get the optional object value associated with an index.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return An object value, or null if there is no object at that index.
     */
    public Object opt(int index) {
        return (index < 0 || index >= length()) ? null : this.myArrayList.get(index);
    }

    /**
     * Get the optional boolean value associated with an index. It returns false if there is no value at that index, or
     * if the value is not Boolean.TRUE or the String "true".
     *
     * @param index The index must be between 0 and length() - 1.
     * @return The truth.
     */
    public boolean optBoolean(int index) {
        return optBoolean(index, false);
    }

    /**
     * Get the optional boolean value associated with an index. It returns the defaultValue if there is no value at that
     * index or if it is not a Boolean or the String "true" or "false" (case insensitive).
     *
     * @param index The index must be between 0 and length() - 1.
     * @param defaultValue A boolean default.
     * @return The truth.
     */
    public boolean optBoolean(int index, boolean defaultValue) {
        try {
            return getBoolean(index);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Get the optional double value associated with an index. NaN is returned if there is no value for the index, or if
     * the value is not a number and cannot be converted to a number.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return The value.
     */
    public double optDouble(int index) {
        return optDouble(index, Double.NaN);
    }

    /**
     * Get the optional double value associated with an index. The defaultValue is returned if there is no value for the
     * index, or if the value is not a number and cannot be converted to a number.
     *
     * @param index subscript
     * @param defaultValue The default value.
     * @return The value.
     */
    public double optDouble(int index, double defaultValue) {
        try {
            return getDouble(index);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Get the optional int value associated with an index. Zero is returned if there is no value for the index, or if
     * the value is not a number and cannot be converted to a number.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return The value.
     */
    public int optInt(int index) {
        return optInt(index, 0);
    }

    /**
     * Get the optional int value associated with an index. The defaultValue is returned if there is no value for the
     * index, or if the value is not a number and cannot be converted to a number.
     *
     * @param index The index must be between 0 and length() - 1.
     * @param defaultValue The default value.
     * @return The value.
     */
    public int optInt(int index, int defaultValue) {
        try {
            return getInt(index);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Get the optional JSONArray associated with an index.
     *
     * @param index subscript
     * @return A JSONArray value, or null if the index has no value, or if the value is not a JSONArray.
     */
    public JSONArray optJSONArray(int index) {
        Object o = opt(index);
        return o instanceof JSONArray ? (JSONArray) o : null;
    }

    /**
     * Get the optional JSONObject associated with an index. Null is returned if the key is not found, or null if the
     * index has no value, or if the value is not a JSONObject.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return A JSONObject value.
     */
    public JSONObject optJSONObject(int index) {
        Object o = opt(index);
        return o instanceof JSONObject ? (JSONObject) o : null;
    }

    /**
     * Get the optional long value associated with an index. Zero is returned if there is no value for the index, or if
     * the value is not a number and cannot be converted to a number.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return The value.
     */
    public long optLong(int index) {
        return optLong(index, 0);
    }

    /**
     * Get the optional long value associated with an index. The defaultValue is returned if there is no value for the
     * index, or if the value is not a number and cannot be converted to a number.
     *
     * @param index The index must be between 0 and length() - 1.
     * @param defaultValue The default value.
     * @return The value.
     */
    public long optLong(int index, long defaultValue) {
        try {
            return getLong(index);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Get the optional string value associated with an index. It returns an empty string if there is no value at that
     * index. If the value is not a string and is not null, then it is coverted to a string.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return A String value.
     */
    public String optString(int index) {
        return optString(index, "");
    }

    /**
     * Get the optional string associated with an index. The defaultValue is returned if the key is not found.
     *
     * @param index The index must be between 0 and length() - 1.
     * @param defaultValue The default value.
     * @return A String value.
     */
    public String optString(int index, String defaultValue) {
        Object o = opt(index);
        return o != null ? o.toString() : defaultValue;
    }

    /**
     * Append a boolean value. This increases the array's length by one.
     *
     * @param value A boolean value.
     * @return this.
     */
    public JSONArray put(boolean value) {
        put(value ? Boolean.TRUE : Boolean.FALSE);
        return this;
    }

    /**
     * Put a value in the JSONArray, where the value will be a JSONArray which is produced from a Collection.
     *
     * @param value A Collection value.
     * @return this.
     */
    public JSONArray put(Collection value) {
        put(new JSONArray(value));
        return this;
    }

    /**
     * Append a double value. This increases the array's length by one.
     *
     * @param value A double value.
     * @throws JSONException if the value is not finite.
     * @return this.
     */
    public JSONArray put(double value) throws JSONException {
        Double d = new Double(value);
        JSONObject.testValidity(d);
        put(d);
        return this;
    }

    /**
     * Append an int value. This increases the array's length by one.
     *
     * @param value An int value.
     * @return this.
     */
    public JSONArray put(int value) {
        put(new Integer(value));
        return this;
    }

    /**
     * Append an long value. This increases the array's length by one.
     *
     * @param value A long value.
     * @return this.
     */
    public JSONArray put(long value) {
        put(new Long(value));
        return this;
    }

    /**
     * Put a value in the JSONArray, where the value will be a JSONObject which is produced from a Map.
     *
     * @param value A Map value.
     * @return this.
     */
    public JSONArray put(Map value) {
        put(new JSONObject(value));
        return this;
    }

    /**
     * Append an object value. This increases the array's length by one.
     *
     * @param value An object value. The value should be a Boolean, Double, Integer, JSONArray, JSONObject, Long, or
     * String, or the JSONObject.NULL object.
     * @return this.
     */
    public JSONArray put(Object value) {
        this.myArrayList.add(value);
        return this;
    }

    /**
     * Put or replace a boolean value in the JSONArray. If the index is greater than the length of the JSONArray, then
     * null elements will be added as necessary to pad it out.
     *
     * @param index The subscript.
     * @param value A boolean value.
     * @return this.
     * @throws JSONException If the index is negative.
     */
    public JSONArray put(int index, boolean value) throws JSONException {
        put(index, value ? Boolean.TRUE : Boolean.FALSE);
        return this;
    }

    /**
     * Put a value in the JSONArray, where the value will be a JSONArray which is produced from a Collection.
     *
     * @param index The subscript.
     * @param value A Collection value.
     * @return this.
     * @throws JSONException If the index is negative or if the value is not finite.
     */
    public JSONArray put(int index, Collection value) throws JSONException {
        put(index, new JSONArray(value));
        return this;
    }

    /**
     * Put or replace a double value. If the index is greater than the length of the JSONArray, then null elements will
     * be added as necessary to pad it out.
     *
     * @param index The subscript.
     * @param value A double value.
     * @return this.
     * @throws JSONException If the index is negative or if the value is not finite.
     */
    public JSONArray put(int index, double value) throws JSONException {
        put(index, new Double(value));
        return this;
    }

    /**
     * Put or replace an int value. If the index is greater than the length of the JSONArray, then null elements will be
     * added as necessary to pad it out.
     *
     * @param index The subscript.
     * @param value An int value.
     * @return this.
     * @throws JSONException If the index is negative.
     */
    public JSONArray put(int index, int value) throws JSONException {
        put(index, new Integer(value));
        return this;
    }

    /**
     * Put or replace a long value. If the index is greater than the length of the JSONArray, then null elements will be
     * added as necessary to pad it out.
     *
     * @param index The subscript.
     * @param value A long value.
     * @return this.
     * @throws JSONException If the index is negative.
     */
    public JSONArray put(int index, long value) throws JSONException {
        put(index, new Long(value));
        return this;
    }

    /**
     * Put a value in the JSONArray, where the value will be a JSONObject which is produced from a Map.
     *
     * @param index The subscript.
     * @param value The Map value.
     * @return this.
     * @throws JSONException If the index is negative or if the the value is an invalid number.
     */
    public JSONArray put(int index, Map value) throws JSONException {
        put(index, new JSONObject(value));
        return this;
    }

    /**
     * Put or replace an object value in the JSONArray. If the index is greater than the length of the JSONArray, then
     * null elements will be added as necessary to pad it out.
     *
     * @param index The subscript.
     * @param value The value to put into the array. The value should be a Boolean, Double, Integer, JSONArray,
     * JSONObject, Long, or String, or the JSONObject.NULL object.
     * @return this.
     * @throws JSONException If the index is negative or if the the value is an invalid number.
     */
    public JSONArray put(int index, Object value) throws JSONException {
        JSONObject.testValidity(value);
        if (index < 0) {
            throw new JSONException("JSONArray[" + index + "] not found.");
        }
        if (index < length()) {
            this.myArrayList.set(index, value);
        } else {
            while (index != length()) {
                put(JSONObject.NULL);
            }
            put(value);
        }
        return this;
    }

    /**
     * Remove an index and close the hole.
     *
     * @param index The index of the element to be removed.
     * @return The value that was associated with the index, or null if there was no value.
     */
    public Object remove(int index) {
        Object o = opt(index);
        this.myArrayList.remove(index);
        return o;
    }

    /**
     * Produce a JSONObject by combining a JSONArray of names with the values of this JSONArray.
     *
     * @param names A JSONArray containing a list of key strings. These will be paired with the values.
     * @return A JSONObject, or null if there are no names or if this JSONArray has no values.
     * @throws JSONException If any of the names are null.
     */
    public JSONObject toJSONObject(JSONArray names) throws JSONException {
        if (names == null || names.length() == 0 || length() == 0) {
            return null;
        }
        JSONObject jo = new JSONObject();
        for (int i = 0; i < names.length(); i += 1) {
            jo.put(names.getString(i), this.opt(i));
        }
        return jo;
    }

    /**
     * Make a JSON text of this JSONArray. For compactness, no unnecessary whitespace is added. If it is not possible to
     * produce a syntactically correct JSON text then null will be returned instead. This could occur if the array
     * contains an invalid number.
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @return a printable, displayable, transmittable representation of the array.
     */
    public String toString() {
        try {
            return '[' + join(",") + ']';
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Make a prettyprinted JSON text of this JSONArray. Warning: This method assumes that the data structure is
     * acyclical.
     *
     * @param indentFactor The number of spaces to add to each level of indentation.
     * @return a printable, displayable, transmittable representation of the object, beginning with <code>[</code>
     * &nbsp;<small>(left bracket)</small> and ending with <code>]</code>&nbsp;<small>(right bracket)</small>.
     * @throws JSONException
     */
    public String toString(int indentFactor) throws JSONException {
        return toString(indentFactor, 0);
    }

    /**
     * Make a prettyprinted JSON text of this JSONArray. Warning: This method assumes that the data structure is
     * acyclical.
     *
     * @param indentFactor The number of spaces to add to each level of indentation.
     * @param indent The indention of the top level.
     * @return a printable, displayable, transmittable representation of the array.
     * @throws JSONException
     */
    String toString(int indentFactor, int indent) throws JSONException {
        int len = length();
        if (len == 0) {
            return "[]";
        }
        int i;
        StringBuffer sb = new StringBuffer("[");
        if (len == 1) {
            sb.append(JSONObject.valueToString(this.myArrayList.get(0), indentFactor, indent));
        } else {
            int newindent = indent + indentFactor;
            sb.append('\n');
            for (i = 0; i < len; i += 1) {
                if (i > 0) {
                    sb.append(",\n");
                }
                for (int j = 0; j < newindent; j += 1) {
                    sb.append(' ');
                }
                sb.append(JSONObject.valueToString(this.myArrayList.get(i), indentFactor, newindent));
            }
            sb.append('\n');
            for (i = 0; i < indent; i += 1) {
                sb.append(' ');
            }
        }
        sb.append(']');
        return sb.toString();
    }

    /**
     * Write the contents of the JSONArray as JSON text to a writer. For compactness, no whitespace is added.
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @return The writer.
     * @throws JSONException
     */
    public Writer write(Writer writer) throws JSONException {
        try {
            boolean b = false;
            int len = length();

            writer.write('[');

            for (int i = 0; i < len; i += 1) {
                if (b) {
                    writer.write(',');
                }
                Object v = this.myArrayList.get(i);
                if (v instanceof JSONObject) {
                    ((JSONObject) v).write(writer);
                } else if (v instanceof JSONArray) {
                    ((JSONArray) v).write(writer);
                } else {
                    writer.write(JSONObject.valueToString(v));
                }
                b = true;
            }
            writer.write(']');
            return writer;
        } catch (IOException e) {
            throw new JSONException(e);
        }
    }
}

/**
 * The <code>JSONString</code> interface allows a <code>toJSONString()</code> method so that a class can change the
 * behavior of <code>JSONObject.toString()</code>, <code>JSONArray.toString()</code>, and <code>JSONWriter.value(</code>
 * Object<code>)</code>. The <code>toJSONString</code> method will be used instead of the default behavior of using the
 * Object's <code>toString()</code> method and quoting the result.
 */
interface JSONString {

    /**
     * The <code>toJSONString</code> method allows a class to produce its own JSON serialization.
     *
     * @return A strictly syntactically correct JSON text.
     */
    public String toJSONString();
}
