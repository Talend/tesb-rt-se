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

import java.util.Date;

import routines.TalendDate;

public class RuntimeUtils {

    public static boolean isDateType(Object o) {
        return getRuntimeType(o).equals("java.util.Date"); //$NON-NLS-1$
    }

    /**
     * This function is in order to check the i type for "int i = 10".
     *
     * @param o
     * @return
     */
    public static String getRuntimeType(Object o) {
        return o.getClass().getName();
    }

    /**
     * This function is in order to check the Date type in tRunJob when transmit the context to child job.
     *
     * @param o
     * @return
     */
    public static Object tRunJobConvertContext(Object o) {
        if (o == null) {
            return null;
        }

        // when tRunJob transmit the date to child job, it should format with "yyyy-MM-dd HH:mm:ss"
        if (isDateType(o)) {
            return TalendDate.formatDate("yyyy-MM-dd HH:mm:ss", (Date) o); //$NON-NLS-1$
        }

        return o;
    }

    public static void main(String[] args) {
        int i = 10;
        System.out.println(tRunJobConvertContext(i));

        Date date = new Date();
        System.out.println(tRunJobConvertContext(date));

    }
}
