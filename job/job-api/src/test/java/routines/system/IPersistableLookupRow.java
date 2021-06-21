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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public interface IPersistableLookupRow<R> {

    public void writeKeysData(ObjectOutputStream out);

    public void readKeysData(ObjectInputStream in);

    public void writeValuesData(DataOutputStream dataOut, ObjectOutputStream objectOut);

    public void readValuesData(DataInputStream dataIn, ObjectInputStream objectIn);

    public void copyDataTo(R other);

    public void copyKeysDataTo(R other);

}
