/*
 * #%L
 * Talend :: ESB :: Job :: API
 * %%
 * Copyright (C) 2011 Talend Inc.
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
package org.talend.esb.job.api.test;

import routines.TalendString;
import routines.system.*;
import routines.system.api.*;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

//the import part of tJavaFlex_1
//import java.util.List;

//the import part of tJavaFlex_2
//import java.util.List;

//the import part of tJavaFlex_3
//import java.util.List;

/**
 * Job: FakeESBJob Purpose: <br>
 * Description: <br>
 *
 * @author
 * @version 4.2.0.NB_r58065
 * @status
 */
public class TestConsumerJob implements TalendESBJob {

	public final Object obj = new Object();

	// for transmiting parameters purpose
	private Object valueObject = null;

	public Object getValueObject() {
		return this.valueObject;
	}

	public void setValueObject(Object valueObject) {
		this.valueObject = valueObject;
	}

	private final static String defaultCharset = java.nio.charset.Charset
			.defaultCharset().name();

	private final static String utf8Charset = "UTF-8";

	// create and load default properties
	private java.util.Properties defaultProps = new java.util.Properties();

	// create application properties with default
	public class ContextProperties extends java.util.Properties {

		public ContextProperties(java.util.Properties properties) {
			super(properties);
		}

		public ContextProperties() {
			super();
		}

		public void synchronizeContext() {

		}

	}

	private ContextProperties context = new ContextProperties();

	public ContextProperties getContext() {
		return this.context;
	}

	private final String jobVersion = "0.1";
	private final String jobName = "FakeESBJob";
	private final String projectName = "TEST";
	public Integer errorCode = null;
	private String currentComponent = "";
	private final java.util.Map<String, Long> start_Hash = new java.util.HashMap<String, Long>();
	private final java.util.Map<String, Long> end_Hash = new java.util.HashMap<String, Long>();
	private final java.util.Map<String, Boolean> ok_Hash = new java.util.HashMap<String, Boolean>();
	private final java.util.Map<String, Object> globalMap = new java.util.HashMap<String, Object>();
	public final java.util.List<String[]> globalBuffer = new java.util.ArrayList<String[]>();

	private final java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
	private final java.io.PrintStream errorMessagePS = new java.io.PrintStream(
			new java.io.BufferedOutputStream(baos));

	public String getExceptionStackTrace() {
		if ("failure".equals(this.getStatus())) {
			errorMessagePS.flush();
			return baos.toString();
		}
		return null;
	}

	private Exception exception = null;

	public Exception getException() {
		if ("failure".equals(this.getStatus())) {
			return this.exception;
		}
		return null;
	}

	private class TalendException extends Exception {
		private java.util.Map<String, Object> globalMap = null;
		private Exception e = null;
		private String currentComponent = null;

		private TalendException(Exception e, String errorComponent,
				final java.util.Map<String, Object> globalMap) {
			this.currentComponent = errorComponent;
			this.globalMap = globalMap;
			this.e = e;
		}

		@Override
		public void printStackTrace() {
			if (!(e instanceof TalendException || e instanceof TDieException)) {
				globalMap.put(currentComponent + "_ERROR_MESSAGE",
						e.getMessage());
				System.err
						.println("Exception in component " + currentComponent);
			}
			if (!(e instanceof TDieException)) {
				if (e instanceof TalendException) {
					e.printStackTrace();
				} else {
					e.printStackTrace();
					e.printStackTrace(errorMessagePS);
					TestConsumerJob.this.exception = e;
				}
			}
			if (!(e instanceof TalendException)) {
				try {
					for (java.lang.reflect.Method m : this.getClass()
							.getEnclosingClass().getMethods()) {
						if (m.getName().compareTo(currentComponent + "_error") == 0) {
							m.invoke(TestConsumerJob.this, new Object[] { e,
									currentComponent, globalMap });
							break;
						}
					}

					if (!(e instanceof TDieException)) {
					}
				} catch (java.lang.SecurityException e) {
					this.e.printStackTrace();
				} catch (java.lang.IllegalArgumentException e) {
					this.e.printStackTrace();
				} catch (java.lang.IllegalAccessException e) {
					this.e.printStackTrace();
				} catch (java.lang.reflect.InvocationTargetException e) {
					this.e.printStackTrace();
				}
			}
		}
	}

	public void tFixedFlowInput_1_error(Exception exception,
			String errorComponent, final java.util.Map<String, Object> globalMap)
			throws TalendException {
		end_Hash.put("tFixedFlowInput_1", System.currentTimeMillis());

		tFixedFlowInput_1_onSubJobError(exception, errorComponent, globalMap);

	}

	public void tJavaFlex_1_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap)
			throws TalendException {
		end_Hash.put("tJavaFlex_1", System.currentTimeMillis());

		tFixedFlowInput_1_onSubJobError(exception, errorComponent, globalMap);

	}

	public void tESBConsumer_1_error(Exception exception,
			String errorComponent, final java.util.Map<String, Object> globalMap)
			throws TalendException {
		end_Hash.put("tESBConsumer_1", System.currentTimeMillis());

		tFixedFlowInput_1_onSubJobError(exception, errorComponent, globalMap);

	}

	public void tJavaFlex_2_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap)
			throws TalendException {
		end_Hash.put("tJavaFlex_2", System.currentTimeMillis());

		tFixedFlowInput_1_onSubJobError(exception, errorComponent, globalMap);

	}

	public void tLogRow_1_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap)
			throws TalendException {
		end_Hash.put("tLogRow_1", System.currentTimeMillis());

		tFixedFlowInput_1_onSubJobError(exception, errorComponent, globalMap);

	}

	public void tJavaFlex_3_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap)
			throws TalendException {
		end_Hash.put("tJavaFlex_3", System.currentTimeMillis());

		tFixedFlowInput_1_onSubJobError(exception, errorComponent, globalMap);

	}

	public void tLogRow_2_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap)
			throws TalendException {
		end_Hash.put("tLogRow_2", System.currentTimeMillis());

		tFixedFlowInput_1_onSubJobError(exception, errorComponent, globalMap);

	}

	public void tFixedFlowInput_1_onSubJobError(Exception exception,
			String errorComponent, final java.util.Map<String, Object> globalMap)
			throws TalendException {

		resumeUtil.addLog("SYSTEM_LOG", "NODE:" + errorComponent, "", Thread
				.currentThread().getId() + "", "FATAL", "",
				exception.getMessage(),
				ResumeUtil.getExceptionStackTrace(exception), "");

	}

	private ESBEndpointRegistry registry = null;
	private ESBProviderCallback callback = null;

	public void setEndpointRegistry(ESBEndpointRegistry registry) {
		this.registry = registry;
	}

	public void setProviderCallback(ESBProviderCallback callback) {
	}

	public ESBEndpointInfo getEndpoint() {
		return null;
	}

	public static class row5Struct implements
			routines.system.IPersistableRow<row5Struct> {
		final static byte[] commonByteArrayLock = new byte[0];
		static byte[] commonByteArray = new byte[0];

		public String payloadString;

		public String getPayloadString() {
			return this.payloadString;
		}

		private String readString(ObjectInputStream dis) throws IOException {
			String strReturn = null;
			int length = 0;
			length = dis.readInt();
			if (length == -1) {
				strReturn = null;
			} else {
				if (length > commonByteArray.length) {
					if (length < 1024 && commonByteArray.length == 0) {
						commonByteArray = new byte[1024];
					} else {
						commonByteArray = new byte[2 * length];
					}
				}
				dis.readFully(commonByteArray, 0, length);
				strReturn = new String(commonByteArray, 0, length, utf8Charset);
			}
			return strReturn;
		}

		private void writeString(String str, ObjectOutputStream dos)
				throws IOException {
			if (str == null) {
				dos.writeInt(-1);
			} else {
				byte[] byteArray = str.getBytes(utf8Charset);
				dos.writeInt(byteArray.length);
				dos.write(byteArray);
			}
		}

		public void readData(ObjectInputStream dis) {

			synchronized (commonByteArrayLock) {

				try {

					int length = 0;

					this.payloadString = readString(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				}

			}

		}

		public void writeData(ObjectOutputStream dos) {
			try {

				// String

				writeString(this.payloadString, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append(super.toString());
			sb.append("[");
			sb.append("payloadString=" + payloadString);
			sb.append("]");

			return sb.toString();
		}

		/**
		 * Compare keys
		 */
		public int compareTo(row5Struct other) {

			int returnValue = -1;

			return returnValue;
		}

		private int checkNullsAndCompare(Object object1, Object object2) {
			int returnValue = 0;
			if (object1 instanceof Comparable && object2 instanceof Comparable) {
				returnValue = ((Comparable) object1).compareTo(object2);
			} else if (object1 != null && object2 != null) {
				returnValue = compareStrings(object1.toString(),
						object2.toString());
			} else if (object1 == null && object2 != null) {
				returnValue = 1;
			} else if (object1 != null && object2 == null) {
				returnValue = -1;
			} else {
				returnValue = 0;
			}

			return returnValue;
		}

		private int compareStrings(String string1, String string2) {
			return string1.compareTo(string2);
		}

	}

	public static class row6Struct implements
			routines.system.IPersistableRow<row6Struct> {
		final static byte[] commonByteArrayLock = new byte[0];
		static byte[] commonByteArray = new byte[0];

		public String faultString;

		public String getFaultString() {
			return this.faultString;
		}

		public String faultDetailString;

		public String getFaultDetailString() {
			return this.faultDetailString;
		}

		private String readString(ObjectInputStream dis) throws IOException {
			String strReturn = null;
			int length = 0;
			length = dis.readInt();
			if (length == -1) {
				strReturn = null;
			} else {
				if (length > commonByteArray.length) {
					if (length < 1024 && commonByteArray.length == 0) {
						commonByteArray = new byte[1024];
					} else {
						commonByteArray = new byte[2 * length];
					}
				}
				dis.readFully(commonByteArray, 0, length);
				strReturn = new String(commonByteArray, 0, length, utf8Charset);
			}
			return strReturn;
		}

		private void writeString(String str, ObjectOutputStream dos)
				throws IOException {
			if (str == null) {
				dos.writeInt(-1);
			} else {
				byte[] byteArray = str.getBytes(utf8Charset);
				dos.writeInt(byteArray.length);
				dos.write(byteArray);
			}
		}

		public void readData(ObjectInputStream dis) {

			synchronized (commonByteArrayLock) {

				try {

					int length = 0;

					this.faultString = readString(dis);

					this.faultDetailString = readString(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				}

			}

		}

		public void writeData(ObjectOutputStream dos) {
			try {

				// String

				writeString(this.faultString, dos);

				// String

				writeString(this.faultDetailString, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append(super.toString());
			sb.append("[");
			sb.append("faultString=" + faultString);
			sb.append(",faultDetailString=" + faultDetailString);
			sb.append("]");

			return sb.toString();
		}

		/**
		 * Compare keys
		 */
		public int compareTo(row6Struct other) {

			int returnValue = -1;

			return returnValue;
		}

		private int checkNullsAndCompare(Object object1, Object object2) {
			int returnValue = 0;
			if (object1 instanceof Comparable && object2 instanceof Comparable) {
				returnValue = ((Comparable) object1).compareTo(object2);
			} else if (object1 != null && object2 != null) {
				returnValue = compareStrings(object1.toString(),
						object2.toString());
			} else if (object1 == null && object2 != null) {
				returnValue = 1;
			} else if (object1 != null && object2 == null) {
				returnValue = -1;
			} else {
				returnValue = 0;
			}

			return returnValue;
		}

		private int compareStrings(String string1, String string2) {
			return string1.compareTo(string2);
		}

	}

	public static class row3Struct implements
			routines.system.IPersistableRow<row3Struct> {
		final static byte[] commonByteArrayLock = new byte[0];
		static byte[] commonByteArray = new byte[0];

		public Document payload;

		public Document getPayload() {
			return this.payload;
		}

		public void readData(ObjectInputStream dis) {

			synchronized (commonByteArrayLock) {

				try {

					int length = 0;

					this.payload = (Document) dis.readObject();

				} catch (IOException e) {
					throw new RuntimeException(e);

				} catch (ClassNotFoundException eCNFE) {
					throw new RuntimeException(eCNFE);

				}

			}

		}

		public void writeData(ObjectOutputStream dos) {
			try {

				// Document

				dos.writeObject(this.payload);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append(super.toString());
			sb.append("[");
			sb.append("payload=" + String.valueOf(payload));
			sb.append("]");

			return sb.toString();
		}

		/**
		 * Compare keys
		 */
		public int compareTo(row3Struct other) {

			int returnValue = -1;

			return returnValue;
		}

		private int checkNullsAndCompare(Object object1, Object object2) {
			int returnValue = 0;
			if (object1 instanceof Comparable && object2 instanceof Comparable) {
				returnValue = ((Comparable) object1).compareTo(object2);
			} else if (object1 != null && object2 != null) {
				returnValue = compareStrings(object1.toString(),
						object2.toString());
			} else if (object1 == null && object2 != null) {
				returnValue = 1;
			} else if (object1 != null && object2 == null) {
				returnValue = -1;
			} else {
				returnValue = 0;
			}

			return returnValue;
		}

		private int compareStrings(String string1, String string2) {
			return string1.compareTo(string2);
		}

	}

	public static class row4Struct implements
			routines.system.IPersistableRow<row4Struct> {
		final static byte[] commonByteArrayLock = new byte[0];
		static byte[] commonByteArray = new byte[0];

		public String faultString;

		public String getFaultString() {
			return this.faultString;
		}

		public Document faultDetail;

		public Document getFaultDetail() {
			return this.faultDetail;
		}

		private String readString(ObjectInputStream dis) throws IOException {
			String strReturn = null;
			int length = 0;
			length = dis.readInt();
			if (length == -1) {
				strReturn = null;
			} else {
				if (length > commonByteArray.length) {
					if (length < 1024 && commonByteArray.length == 0) {
						commonByteArray = new byte[1024];
					} else {
						commonByteArray = new byte[2 * length];
					}
				}
				dis.readFully(commonByteArray, 0, length);
				strReturn = new String(commonByteArray, 0, length, utf8Charset);
			}
			return strReturn;
		}

		private void writeString(String str, ObjectOutputStream dos)
				throws IOException {
			if (str == null) {
				dos.writeInt(-1);
			} else {
				byte[] byteArray = str.getBytes(utf8Charset);
				dos.writeInt(byteArray.length);
				dos.write(byteArray);
			}
		}

		public void readData(ObjectInputStream dis) {

			synchronized (commonByteArrayLock) {

				try {

					int length = 0;

					this.faultString = readString(dis);

					this.faultDetail = (Document) dis.readObject();

				} catch (IOException e) {
					throw new RuntimeException(e);

				} catch (ClassNotFoundException eCNFE) {
					throw new RuntimeException(eCNFE);

				}

			}

		}

		public void writeData(ObjectOutputStream dos) {
			try {

				// String

				writeString(this.faultString, dos);

				// Document

				dos.writeObject(this.faultDetail);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append(super.toString());
			sb.append("[");
			sb.append("faultString=" + faultString);
			sb.append(",faultDetail=" + String.valueOf(faultDetail));
			sb.append("]");

			return sb.toString();
		}

		/**
		 * Compare keys
		 */
		public int compareTo(row4Struct other) {

			int returnValue = -1;

			return returnValue;
		}

		private int checkNullsAndCompare(Object object1, Object object2) {
			int returnValue = 0;
			if (object1 instanceof Comparable && object2 instanceof Comparable) {
				returnValue = ((Comparable) object1).compareTo(object2);
			} else if (object1 != null && object2 != null) {
				returnValue = compareStrings(object1.toString(),
						object2.toString());
			} else if (object1 == null && object2 != null) {
				returnValue = 1;
			} else if (object1 != null && object2 == null) {
				returnValue = -1;
			} else {
				returnValue = 0;
			}

			return returnValue;
		}

		private int compareStrings(String string1, String string2) {
			return string1.compareTo(string2);
		}

	}

	public static class row2Struct implements
			routines.system.IPersistableRow<row2Struct> {
		final static byte[] commonByteArrayLock = new byte[0];
		static byte[] commonByteArray = new byte[0];

		public Document payload;

		public Document getPayload() {
			return this.payload;
		}

		public void readData(ObjectInputStream dis) {

			synchronized (commonByteArrayLock) {

				try {

					int length = 0;

					this.payload = (Document) dis.readObject();

				} catch (IOException e) {
					throw new RuntimeException(e);

				} catch (ClassNotFoundException eCNFE) {
					throw new RuntimeException(eCNFE);

				}

			}

		}

		public void writeData(ObjectOutputStream dos) {
			try {

				// Document

				dos.writeObject(this.payload);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append(super.toString());
			sb.append("[");
			sb.append("payload=" + String.valueOf(payload));
			sb.append("]");

			return sb.toString();
		}

		/**
		 * Compare keys
		 */
		public int compareTo(row2Struct other) {

			int returnValue = -1;

			return returnValue;
		}

		private int checkNullsAndCompare(Object object1, Object object2) {
			int returnValue = 0;
			if (object1 instanceof Comparable && object2 instanceof Comparable) {
				returnValue = ((Comparable) object1).compareTo(object2);
			} else if (object1 != null && object2 != null) {
				returnValue = compareStrings(object1.toString(),
						object2.toString());
			} else if (object1 == null && object2 != null) {
				returnValue = 1;
			} else if (object1 != null && object2 == null) {
				returnValue = -1;
			} else {
				returnValue = 0;
			}

			return returnValue;
		}

		private int compareStrings(String string1, String string2) {
			return string1.compareTo(string2);
		}

	}

	public static class row1Struct implements
			routines.system.IPersistableRow<row1Struct> {
		final static byte[] commonByteArrayLock = new byte[0];
		static byte[] commonByteArray = new byte[0];

		public String payloadString;

		public String getPayloadString() {
			return this.payloadString;
		}

		private String readString(ObjectInputStream dis) throws IOException {
			String strReturn = null;
			int length = 0;
			length = dis.readInt();
			if (length == -1) {
				strReturn = null;
			} else {
				if (length > commonByteArray.length) {
					if (length < 1024 && commonByteArray.length == 0) {
						commonByteArray = new byte[1024];
					} else {
						commonByteArray = new byte[2 * length];
					}
				}
				dis.readFully(commonByteArray, 0, length);
				strReturn = new String(commonByteArray, 0, length, utf8Charset);
			}
			return strReturn;
		}

		private void writeString(String str, ObjectOutputStream dos)
				throws IOException {
			if (str == null) {
				dos.writeInt(-1);
			} else {
				byte[] byteArray = str.getBytes(utf8Charset);
				dos.writeInt(byteArray.length);
				dos.write(byteArray);
			}
		}

		public void readData(ObjectInputStream dis) {

			synchronized (commonByteArrayLock) {

				try {

					int length = 0;

					this.payloadString = readString(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				}

			}

		}

		public void writeData(ObjectOutputStream dos) {
			try {

				// String

				writeString(this.payloadString, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append(super.toString());
			sb.append("[");
			sb.append("payloadString=" + payloadString);
			sb.append("]");

			return sb.toString();
		}

		/**
		 * Compare keys
		 */
		public int compareTo(row1Struct other) {

			int returnValue = -1;

			return returnValue;
		}

		private int checkNullsAndCompare(Object object1, Object object2) {
			int returnValue = 0;
			if (object1 instanceof Comparable && object2 instanceof Comparable) {
				returnValue = ((Comparable) object1).compareTo(object2);
			} else if (object1 != null && object2 != null) {
				returnValue = compareStrings(object1.toString(),
						object2.toString());
			} else if (object1 == null && object2 != null) {
				returnValue = 1;
			} else if (object1 != null && object2 == null) {
				returnValue = -1;
			} else {
				returnValue = 0;
			}

			return returnValue;
		}

		private int compareStrings(String string1, String string2) {
			return string1.compareTo(string2);
		}

	}

	public void tFixedFlowInput_1Process(
			final java.util.Map<String, Object> globalMap)
			throws TalendException {
		globalMap.put("tFixedFlowInput_1_SUBPROCESS_STATE", 0);

		final boolean execStat = this.execStat;

		String iterateId = "";
		String currentComponent = "";

		try {

			String currentMethodName = new Exception().getStackTrace()[0]
					.getMethodName();
			boolean resumeIt = currentMethodName.equals(resumeEntryMethodName);
			if (resumeEntryMethodName == null || resumeIt || globalResumeTicket) {// start
																					// the
																					// resume
				globalResumeTicket = true;

				row1Struct row1 = new row1Struct();
				row2Struct row2 = new row2Struct();
				row3Struct row3 = new row3Struct();
				row5Struct row5 = new row5Struct();
				row4Struct row4 = new row4Struct();
				row6Struct row6 = new row6Struct();

				/**
				 * [tLogRow_1 begin ] start
				 */

				ok_Hash.put("tLogRow_1", false);
				start_Hash.put("tLogRow_1", System.currentTimeMillis());
				currentComponent = "tLogRow_1";

				int tos_count_tLogRow_1 = 0;

				// /////////////////////

				final String OUTPUT_FIELD_SEPARATOR_tLogRow_1 = "|";
				java.io.PrintStream consoleOut_tLogRow_1 = null;

				int nb_line_tLogRow_1 = 0;

				// /////////////////////

				/**
				 * [tLogRow_1 begin ] stop
				 */

				/**
				 * [tJavaFlex_2 begin ] start
				 */

				ok_Hash.put("tJavaFlex_2", false);
				start_Hash.put("tJavaFlex_2", System.currentTimeMillis());
				currentComponent = "tJavaFlex_2";

				int tos_count_tJavaFlex_2 = 0;

				// start part of your Java code

				/**
				 * [tJavaFlex_2 begin ] stop
				 */

				/**
				 * [tLogRow_2 begin ] start
				 */

				ok_Hash.put("tLogRow_2", false);
				start_Hash.put("tLogRow_2", System.currentTimeMillis());
				currentComponent = "tLogRow_2";

				int tos_count_tLogRow_2 = 0;

				// /////////////////////

				final String OUTPUT_FIELD_SEPARATOR_tLogRow_2 = "|";
				java.io.PrintStream consoleOut_tLogRow_2 = null;

				int nb_line_tLogRow_2 = 0;

				// /////////////////////

				/**
				 * [tLogRow_2 begin ] stop
				 */

				/**
				 * [tJavaFlex_3 begin ] start
				 */

				ok_Hash.put("tJavaFlex_3", false);
				start_Hash.put("tJavaFlex_3", System.currentTimeMillis());
				currentComponent = "tJavaFlex_3";

				int tos_count_tJavaFlex_3 = 0;

				// start part of your Java code

				/**
				 * [tJavaFlex_3 begin ] stop
				 */

				/**
				 * [tESBConsumer_1 begin ] start
				 */

				ok_Hash.put("tESBConsumer_1", false);
				start_Hash.put("tESBConsumer_1", System.currentTimeMillis());
				currentComponent = "tESBConsumer_1";

				int tos_count_tESBConsumer_1 = 0;
				javax.xml.namespace.QName serviceName_tESBConsumer_1 = null;
				javax.xml.namespace.QName portName_tESBConsumer_1 = null;
// [TA] commented run in TOS part due to external dependencies :: begin
//				org.talend.ws.helper.ServiceInvokerHelper serviceInvokerHelper_tESBConsumer_1 = null;
// [TA] commented run in TOS part due to external dependencies :: end

				ESBConsumer consumer_tESBConsumer_1 = null;
				if (registry != null) {
					consumer_tESBConsumer_1 = registry
							.createConsumer(new ESBEndpointInfo() {
								@SuppressWarnings("serial")
								private java.util.Map<String, Object> props = new java.util.HashMap<String, Object>() {
									{
										put("wsdlURL",
												"http://www.deeptraining.com/webservices/weather.asmx?WSDL");
										put("dataFormat", "PAYLOAD");
										put("portName", "{}");
										put("serviceName", "{}");
										put("defaultOperationName", "");
										put("defaultOperationNameSpace", "");
									}
								};

								public String getEndpointUri() {
									// projectName + "_" + processName + "_" +
									// componentName
									return "TEST_FakeESBJob_tESBConsumer_1";
								}

								public java.util.Map<String, Object> getEndpointProperties() {
									return props;
								}

								public String getEndpointKey() {
									return "cxf";
								}
							});
				} else {
					System.out.println("");
					System.out.println("");
					System.out.println("|");

					class Util_tESBConsumer_1 {

						public final String LIST_SIZE_SYMBOL = ".size";

						public final String LEFT_SQUARE_BRACKET = "[";

						public final String RIGHT_SQUARE_BRACKET = "]";

						public final String ALL_LIST_SYMBOL = "[*]";

						public Object getValue(
								java.util.Map<String, Object> map, String path) {
							if (path == null || "".equals(path)) {
								return null;
							}
							if (map == null || map.isEmpty()) {
								return null;
							}
							java.util.List<String> paths = new java.util.ArrayList<String>();
							resolvePath(map, path, paths);
							if (paths.size() > 0) {
								if (path.indexOf(ALL_LIST_SYMBOL) == -1) {
									return map.get(paths.get(0));
								} else {
									int size = paths.size();
									java.util.List<Object> out = new java.util.ArrayList<Object>(
											size);
									for (int i = 0; i < size; i++) {
										out.add(map.get(paths.get(i)));
									}
									return out;
								}
							} else {
								return null;
							}
						}

						public void resolveInputPath(
								java.util.Map<String, Object> inputMap) {
							java.util.Map<String, Object> tempStoreMap = new java.util.HashMap<String, Object>();
							java.util.List<String> tempRemovePath = new java.util.ArrayList<String>();
							for (String key : inputMap.keySet()) {
								if (key.indexOf(ALL_LIST_SYMBOL) != -1) {
									String listHeadPath = key.substring(0,
											key.indexOf(ALL_LIST_SYMBOL));
									String listFootPath = key.substring(key
											.indexOf(ALL_LIST_SYMBOL)
											+ ALL_LIST_SYMBOL.length());
									java.util.List listElement = (java.util.List) inputMap
											.get(key);
									for (int i = 0; i < listElement.size(); i++) {
										tempStoreMap.put(listHeadPath
												+ LEFT_SQUARE_BRACKET + i
												+ RIGHT_SQUARE_BRACKET
												+ listFootPath,
												listElement.get(i));
									}
									tempRemovePath.add(key);
								}
							}
							inputMap.putAll(tempStoreMap);
							for (String removePath : tempRemovePath) {
								inputMap.remove(removePath);
							}
						}

						public void resolvePath(
								java.util.Map<String, Object> map, String path,
								java.util.List<String> paths) {
							String listHeadPath = "";
							String listFootPath = "";
							int size = 0;
							String tempPath = "";
							if (path.indexOf(ALL_LIST_SYMBOL) != -1) {
								listHeadPath = path.substring(0,
										path.indexOf(ALL_LIST_SYMBOL));
								listFootPath = path.substring(path
										.indexOf(ALL_LIST_SYMBOL)
										+ ALL_LIST_SYMBOL.length());
								if (map.get(listHeadPath) == null
										&& map.get(listHeadPath
												+ LIST_SIZE_SYMBOL) != null) {
									size = Integer.parseInt(map.get(
											listHeadPath + LIST_SIZE_SYMBOL)
											.toString());
									for (int i = 0; i < size; i++) {
										tempPath = listHeadPath
												+ LEFT_SQUARE_BRACKET + i
												+ RIGHT_SQUARE_BRACKET
												+ listFootPath;
										if (tempPath.indexOf(ALL_LIST_SYMBOL) != -1) {
											resolvePath(map, tempPath, paths);
										} else {
											paths.add(tempPath);
										}
									}
								}
							} else {
								paths.add(path);
							}
						}

						public java.util.List<Object> normalize(
								String inputValue, String delimiter) {
							if (inputValue == null || "".equals(inputValue)) {
								return null;
							}
							Object[] inputValues = inputValue.split(delimiter);
							return java.util.Arrays.asList(inputValues);
						}

						public String denormalize(java.util.List inputValues,
								String delimiter) {
							if (inputValues == null || inputValues.isEmpty()) {
								return null;
							}
							StringBuffer sb = new StringBuffer();
							for (Object o : inputValues) {
								sb.append(String.valueOf(o));
								sb.append(delimiter);
							}
							if (sb.length() > 0) {
								sb.delete(sb.length() - delimiter.length(),
										sb.length());
							}
							return sb.toString();
						}
					}
// [TA] commented run in TOS part due to external dependencies :: begin
//					System.setProperty("org.apache.commons.logging.Log",
//							"org.apache.commons.logging.impl.NoOpLog");
//					// shade the log level for DynamicClientFactory.class
//					java.util.logging.Logger LOG_tESBConsumer_1 = org.apache.cxf.common.logging.LogUtils
//							.getL7dLogger(org.apache.cxf.endpoint.dynamic.DynamicClientFactory.class);
//					LOG_tESBConsumer_1
//							.setLevel(java.util.logging.Level.WARNING);
//
//					Util_tESBConsumer_1 util_tESBConsumer_1 = new Util_tESBConsumer_1();
//
//					org.talend.ws.helper.conf.ServiceHelperConfiguration config_tESBConsumer_1 =
//						new org.talend.ws.helper.conf.ServiceHelperConfiguration();
//
//					config_tESBConsumer_1.setConnectionTimeout(Long
//							.valueOf(20000));
//					config_tESBConsumer_1
//							.setReceiveTimeout(Long.valueOf(20000));
//
//					config_tESBConsumer_1.setKeyStoreFile(System
//							.getProperty("javax.net.ssl.keyStore"));
//					config_tESBConsumer_1.setKeyStoreType(System
//							.getProperty("javax.net.ssl.keyStoreType"));
//					config_tESBConsumer_1.setKeyStorePwd(System
//							.getProperty("javax.net.ssl.keyStorePassword"));
//					org.talend.ws.helper.ServiceDiscoveryHelper serviceDiscoveryHelper_tESBConsumer_1 = null;
//
//					java.net.URI uri_tESBConsumer_1 = new java.net.URI(
//							"http://www.deeptraining.com/webservices/weather.asmx?WSDL");
//					if ("http".equals(uri_tESBConsumer_1.getScheme())
//							|| "https".equals(uri_tESBConsumer_1.getScheme())) {
//						serviceInvokerHelper_tESBConsumer_1 = new org.talend.ws.helper.ServiceInvokerHelper(
//								"http://www.deeptraining.com/webservices/weather.asmx?WSDL",
//								config_tESBConsumer_1, "");
//					} else {
//						serviceDiscoveryHelper_tESBConsumer_1 = new org.talend.ws.helper.ServiceDiscoveryHelper(
//								"http://www.deeptraining.com/webservices/weather.asmx?WSDL",
//								"");
//						serviceInvokerHelper_tESBConsumer_1 = new org.talend.ws.helper.ServiceInvokerHelper(
//								serviceDiscoveryHelper_tESBConsumer_1,
//								config_tESBConsumer_1);
//					}
//
//					serviceName_tESBConsumer_1 = new javax.xml.namespace.QName(
//							"", "");
//					portName_tESBConsumer_1 = new javax.xml.namespace.QName("",
//							"");
//
//					java.util.Map<String, Object> inMap_tESBConsumer_1 = null;
// [TA] commented run in TOS part due to external dependencies :: end

				}

				/**
				 * [tESBConsumer_1 begin ] stop
				 */

				/**
				 * [tJavaFlex_1 begin ] start
				 */

				ok_Hash.put("tJavaFlex_1", false);
				start_Hash.put("tJavaFlex_1", System.currentTimeMillis());
				currentComponent = "tJavaFlex_1";

				int tos_count_tJavaFlex_1 = 0;

				// start part of your Java code

				/**
				 * [tJavaFlex_1 begin ] stop
				 */

				/**
				 * [tFixedFlowInput_1 begin ] start
				 */

				ok_Hash.put("tFixedFlowInput_1", false);
				start_Hash.put("tFixedFlowInput_1", System.currentTimeMillis());
				currentComponent = "tFixedFlowInput_1";

				int tos_count_tFixedFlowInput_1 = 0;

				globalMap.put("NB_LINE", 1);
				for (int i_tFixedFlowInput_1 = 0; i_tFixedFlowInput_1 < 1; i_tFixedFlowInput_1++) {

//					row1.payloadString = "<request>hi</request>";
					row1.payloadString = "<GetWeather xmlns='http://litwinconsulting.com/webservices/'><City>bonn</City></GetWeather>";

					/**
					 * [tFixedFlowInput_1 begin ] stop
					 */
					/**
					 * [tFixedFlowInput_1 main ] start
					 */

					currentComponent = "tFixedFlowInput_1";

					tos_count_tFixedFlowInput_1++;

					/**
					 * [tFixedFlowInput_1 main ] stop
					 */

					/**
					 * [tJavaFlex_1 main ] start
					 */

					currentComponent = "tJavaFlex_1";

					// here is the main part of the component,
					// a piece of code executed in the row
					// loop
					org.dom4j.Document doc = org.dom4j.DocumentHelper
							.parseText(row1.payloadString);
					Document talendDoc = new Document();
					talendDoc.setDocument(doc);
					row2.payload = talendDoc;

					tos_count_tJavaFlex_1++;

					/**
					 * [tJavaFlex_1 main ] stop
					 */

					/**
					 * [tESBConsumer_1 main ] start
					 */

					currentComponent = "tESBConsumer_1";

					row3 = null;
					row4 = null;
					org.dom4j.Document responseDoc_tESBConsumer_1 = null;
					try {
						Document requestTalendDoc = row2.payload;

						if (consumer_tESBConsumer_1 == null) {
// [TA] commented run in TOS part due to external dependencies :: begin
//							try {
//								responseDoc_tESBConsumer_1 = serviceInvokerHelper_tESBConsumer_1
//										.invoke(serviceName_tESBConsumer_1,
//												portName_tESBConsumer_1, "",
//												requestTalendDoc.getDocument());
//							} catch (javax.xml.ws.soap.SOAPFaultException e) {
//								String faultString = e.getFault()
//										.getFaultString();
//								Document faultTalendDoc = null;
//								if (null != e.getFault().getDetail()
//										&& null != e.getFault().getDetail()
//												.getFirstChild()) {
//									try {
//										javax.xml.transform.Source faultSource = new javax.xml.transform.dom.DOMSource(
//												e.getFault().getDetail()
//														.getFirstChild());
//
//										org.dom4j.io.DocumentResult result = new org.dom4j.io.DocumentResult();
//										javax.xml.transform.TransformerFactory
//												.newInstance().newTransformer()
//												.transform(faultSource, result);
//										org.dom4j.Document faultDoc = result
//												.getDocument();
//
//										faultTalendDoc = new Document();
//										faultTalendDoc.setDocument(faultDoc);
//									} catch (Exception e1) {
//										e1.printStackTrace();
//									}
//								}
//								if (row4 == null) {
//									row4 = new row4Struct();
//								}
//								row4.faultString = faultString;
//								row4.faultDetail = faultTalendDoc;
//							}
// [TA] commented run in TOS part due to external dependencies :: end
						} else {
							try {
								responseDoc_tESBConsumer_1 = (org.dom4j.Document) consumer_tESBConsumer_1
										.invoke(requestTalendDoc.getDocument());
							} catch (Exception e) {
								String faultMessage = e.getMessage();
								if (row4 == null) {
									row4 = new row4Struct();
								}
								row4.faultString = faultMessage;
								row4.faultDetail = null;
							}
						}
					} catch (Exception e) {
						throw (e);
					}

					if (null != responseDoc_tESBConsumer_1) {
						if (row3 == null) {
							row3 = new row3Struct();
						}

						Document responseTalendDoc_tESBConsumer_1 = new Document();
						responseTalendDoc_tESBConsumer_1
								.setDocument(responseDoc_tESBConsumer_1);
						row3.payload = responseTalendDoc_tESBConsumer_1;
					}

					tos_count_tESBConsumer_1++;

					/**
					 * [tESBConsumer_1 main ] stop
					 */
					// Start of branch "row3"
					if (row3 != null) {

						/**
						 * [tJavaFlex_2 main ] start
						 */

						currentComponent = "tJavaFlex_2";

						// here is the main part of the component,
						// a piece of code executed in the row
						// loop
						row5.payloadString = row3.payload.getDocument().asXML();

						tos_count_tJavaFlex_2++;

						/**
						 * [tJavaFlex_2 main ] stop
						 */

						/**
						 * [tLogRow_1 main ] start
						 */

						currentComponent = "tLogRow_1";

						// /////////////////////

						StringBuilder strBuffer_tLogRow_1 = new StringBuilder();
						strBuffer_tLogRow_1.append("[tLogRow_1] ");

						if (row5.payloadString != null) { //

							strBuffer_tLogRow_1.append(String
									.valueOf(row5.payloadString));

						} //

						if (globalMap.get("tLogRow_CONSOLE") != null) {
							consoleOut_tLogRow_1 = (java.io.PrintStream) globalMap
									.get("tLogRow_CONSOLE");
						} else {
							consoleOut_tLogRow_1 = new java.io.PrintStream(
									new java.io.BufferedOutputStream(System.out));
							globalMap.put("tLogRow_CONSOLE",
									consoleOut_tLogRow_1);
						}

						consoleOut_tLogRow_1.println(strBuffer_tLogRow_1
								.toString());
						consoleOut_tLogRow_1.flush();
						nb_line_tLogRow_1++;
						// ////

						// ////

						// /////////////////////

						tos_count_tLogRow_1++;

						/**
						 * [tLogRow_1 main ] stop
						 */

					} // End of branch "row3"

					// Start of branch "row4"
					if (row4 != null) {

						/**
						 * [tJavaFlex_3 main ] start
						 */

						currentComponent = "tJavaFlex_3";

						row6.faultString = row4.faultString;

						// here is the main part of the component,
						// a piece of code executed in the row
						// loop
						// row6.faultString = row4.faultString;
						if (null != row4.faultDetail) {
							row6.faultDetailString = row4.faultDetail
									.getDocument().asXML();
						}

						tos_count_tJavaFlex_3++;

						/**
						 * [tJavaFlex_3 main ] stop
						 */

						/**
						 * [tLogRow_2 main ] start
						 */

						currentComponent = "tLogRow_2";

						// /////////////////////

						StringBuilder strBuffer_tLogRow_2 = new StringBuilder();
						strBuffer_tLogRow_2.append("[tLogRow_2] ");

						if (row6.faultString != null) { //

							strBuffer_tLogRow_2.append(String
									.valueOf(row6.faultString));

						} //

						strBuffer_tLogRow_2.append("|");

						if (row6.faultDetailString != null) { //

							strBuffer_tLogRow_2.append(String
									.valueOf(row6.faultDetailString));

						} //

						if (globalMap.get("tLogRow_CONSOLE") != null) {
							consoleOut_tLogRow_2 = (java.io.PrintStream) globalMap
									.get("tLogRow_CONSOLE");
						} else {
							consoleOut_tLogRow_2 = new java.io.PrintStream(
									new java.io.BufferedOutputStream(System.out));
							globalMap.put("tLogRow_CONSOLE",
									consoleOut_tLogRow_2);
						}

						consoleOut_tLogRow_2.println(strBuffer_tLogRow_2
								.toString());
						consoleOut_tLogRow_2.flush();
						nb_line_tLogRow_2++;
						// ////

						// ////

						// /////////////////////

						tos_count_tLogRow_2++;

						/**
						 * [tLogRow_2 main ] stop
						 */

					} // End of branch "row4"

					/**
					 * [tFixedFlowInput_1 end ] start
					 */

					currentComponent = "tFixedFlowInput_1";

				}

				ok_Hash.put("tFixedFlowInput_1", true);
				end_Hash.put("tFixedFlowInput_1", System.currentTimeMillis());

				/**
				 * [tFixedFlowInput_1 end ] stop
				 */

				/**
				 * [tJavaFlex_1 end ] start
				 */

				currentComponent = "tJavaFlex_1";

				// end of the component, outside/closing the loop

				ok_Hash.put("tJavaFlex_1", true);
				end_Hash.put("tJavaFlex_1", System.currentTimeMillis());

				/**
				 * [tJavaFlex_1 end ] stop
				 */

				/**
				 * [tESBConsumer_1 end ] start
				 */

				currentComponent = "tESBConsumer_1";

				ok_Hash.put("tESBConsumer_1", true);
				end_Hash.put("tESBConsumer_1", System.currentTimeMillis());

				/**
				 * [tESBConsumer_1 end ] stop
				 */

				/**
				 * [tJavaFlex_2 end ] start
				 */

				currentComponent = "tJavaFlex_2";

				// end of the component, outside/closing the loop

				ok_Hash.put("tJavaFlex_2", true);
				end_Hash.put("tJavaFlex_2", System.currentTimeMillis());

				/**
				 * [tJavaFlex_2 end ] stop
				 */

				/**
				 * [tLogRow_1 end ] start
				 */

				currentComponent = "tLogRow_1";

				// ////
				// ////
				globalMap.put("tLogRow_1_NB_LINE", nb_line_tLogRow_1);

				// /////////////////////

				ok_Hash.put("tLogRow_1", true);
				end_Hash.put("tLogRow_1", System.currentTimeMillis());

				/**
				 * [tLogRow_1 end ] stop
				 */

				/**
				 * [tJavaFlex_3 end ] start
				 */

				currentComponent = "tJavaFlex_3";

				// end of the component, outside/closing the loop

				ok_Hash.put("tJavaFlex_3", true);
				end_Hash.put("tJavaFlex_3", System.currentTimeMillis());

				/**
				 * [tJavaFlex_3 end ] stop
				 */

				/**
				 * [tLogRow_2 end ] start
				 */

				currentComponent = "tLogRow_2";

				// ////
				// ////
				globalMap.put("tLogRow_2_NB_LINE", nb_line_tLogRow_2);

				// /////////////////////

				ok_Hash.put("tLogRow_2", true);
				end_Hash.put("tLogRow_2", System.currentTimeMillis());

				/**
				 * [tLogRow_2 end ] stop
				 */

			}// end the resume

		} catch (Exception e) {

			throw new TalendException(e, currentComponent, globalMap);

		} catch (Error error) {

			throw new Error(error);

		}

		globalMap.put("tFixedFlowInput_1_SUBPROCESS_STATE", 1);
	}

	public String resuming_logs_dir_path = null;
	public String resuming_checkpoint_path = null;
	public String parent_part_launcher = null;
	private String resumeEntryMethodName = null;
	private boolean globalResumeTicket = false;

	public boolean watch = false;
	// portStats is null, it means don't execute the statistics
	public Integer portStats = null;
	public int portTraces = 4334;
	public String clientHost;
	public String defaultClientHost = "localhost";
	public String contextStr = "Default";
	public String pid = "0";
	public String rootPid = null;
	public String fatherPid = null;
	public String fatherNode = null;
	public long startTime = 0;
	public boolean isChildJob = false;

	private boolean execStat = true;

	private ThreadLocal threadLocal = new ThreadLocal();
	{
		java.util.Map threadRunResultMap = new java.util.HashMap();
		threadRunResultMap.put("errorCode", null);
		threadRunResultMap.put("status", "");
		threadLocal.set(threadRunResultMap);
	}

	private java.util.Properties context_param = new java.util.Properties();
	public java.util.Map<String, Object> parentContextMap = new java.util.HashMap<String, Object>();

	public String status = "";

	public static void main(String[] args) {
		final TestConsumerJob FakeESBJobClass = new TestConsumerJob();

		int exitCode = FakeESBJobClass.runJobInTOS(args);

		System.exit(exitCode);
	}

	public String[][] runJob(String[] args) {

		int exitCode = runJobInTOS(args);
		String[][] bufferValue = new String[][] { { Integer.toString(exitCode) } };

		return bufferValue;
	}

	public int runJobInTOS(String[] args) {

		String lastStr = "";
		for (String arg : args) {
			if (arg.equalsIgnoreCase("--context_param")) {
				lastStr = arg;
			} else if (lastStr.equals("")) {
				evalParam(arg);
			} else {
				evalParam(lastStr + " " + arg);
				lastStr = "";
			}
		}

		if (clientHost == null) {
			clientHost = defaultClientHost;
		}

		if (pid == null || "0".equals(pid)) {
			pid = TalendString.getAsciiRandomString(6);
		}

		if (rootPid == null) {
			rootPid = pid;
		}
		if (fatherPid == null) {
			fatherPid = pid;
		} else {
			isChildJob = true;
		}

		try {
			// call job/subjob with an existing context, like:
			// --context=production. if without this parameter, there will use
			// the default context instead.
			java.io.InputStream inContext = TestConsumerJob.class.getClassLoader()
					.getResourceAsStream(
							"test/fakeesbjob_0_1/contexts/" + contextStr
									+ ".properties");
			if (inContext != null) {
				// defaultProps is in order to keep the original context value
				defaultProps.load(inContext);
				inContext.close();
				context = new ContextProperties(defaultProps);
			} else {
				// print info and job continue to run, for case: context_param
				// is not empty.
				System.err.println("Could not find the context " + contextStr);
			}

			if (!context_param.isEmpty()) {
				context.putAll(context_param);
			}

		} catch (java.io.IOException ie) {
			System.err.println("Could not load context " + contextStr);
			ie.printStackTrace();
		}

		// get context value from parent directly
		if (parentContextMap != null && !parentContextMap.isEmpty()) {

		}

		// Resume: init the resumeUtil
		resumeEntryMethodName = ResumeUtil
				.getResumeEntryMethodName(resuming_checkpoint_path);
		resumeUtil = new ResumeUtil(resuming_logs_dir_path, isChildJob, rootPid);
		resumeUtil.initCommonInfo(pid, rootPid, fatherPid, projectName,
				jobName, contextStr, jobVersion);

		// Resume: jobStart
		resumeUtil.addLog("JOB_STARTED", "JOB:" + jobName,
				parent_part_launcher, Thread.currentThread().getId() + "", "",
				"", "", "", resumeUtil.convertToJsonText(context));

		long startUsedMemory = Runtime.getRuntime().totalMemory()
				- Runtime.getRuntime().freeMemory();
		long endUsedMemory = 0;
		long end = 0;

		startTime = System.currentTimeMillis();

		this.globalResumeTicket = true;// to run tPreJob

		this.globalResumeTicket = false;// to run others jobs

		try {
			errorCode = null;
			tFixedFlowInput_1Process(globalMap);
			status = "end";
		} catch (TalendException e_tFixedFlowInput_1) {
			status = "failure";
			e_tFixedFlowInput_1.printStackTrace();
			globalMap.put("tFixedFlowInput_1_SUBPROCESS_STATE", -1);

		} finally {
		}

		this.globalResumeTicket = true;// to run tPostJob

		end = System.currentTimeMillis();

		if (watch) {
			System.out.println((end - startTime) + " milliseconds");
		}

		endUsedMemory = Runtime.getRuntime().totalMemory()
				- Runtime.getRuntime().freeMemory();
		if (false) {
			System.out.println((endUsedMemory - startUsedMemory)
					+ " bytes memory increase when running : FakeESBJob");
		}

		int returnCode = 0;
		if (errorCode == null) {
			returnCode = status != null && status.equals("failure") ? 1 : 0;
		} else {
			returnCode = errorCode.intValue();
		}
		resumeUtil.addLog("JOB_ENDED", "JOB:" + jobName, parent_part_launcher,
				Thread.currentThread().getId() + "", "", "" + returnCode, "",
				"", "");

		return returnCode;

	}

	private void evalParam(String arg) {
		if (arg.startsWith("--resuming_logs_dir_path")) {
			resuming_logs_dir_path = arg.substring(25);
		} else if (arg.startsWith("--resuming_checkpoint_path")) {
			resuming_checkpoint_path = arg.substring(27);
		} else if (arg.startsWith("--parent_part_launcher")) {
			parent_part_launcher = arg.substring(23);
		} else if (arg.startsWith("--watch")) {
			watch = true;
		} else if (arg.startsWith("--stat_port=")) {
			String portStatsStr = arg.substring(12);
			if (portStatsStr != null && !portStatsStr.equals("null")) {
				portStats = Integer.parseInt(portStatsStr);
			}
		} else if (arg.startsWith("--trace_port=")) {
			portTraces = Integer.parseInt(arg.substring(13));
		} else if (arg.startsWith("--client_host=")) {
			clientHost = arg.substring(14);
		} else if (arg.startsWith("--context=")) {
			contextStr = arg.substring(10);
		} else if (arg.startsWith("--father_pid=")) {
			fatherPid = arg.substring(13);
		} else if (arg.startsWith("--root_pid=")) {
			rootPid = arg.substring(11);
		} else if (arg.startsWith("--father_node=")) {
			fatherNode = arg.substring(14);
		} else if (arg.startsWith("--pid=")) {
			pid = arg.substring(6);
		} else if (arg.startsWith("--context_param")) {
			String keyValue = arg.substring(16);
			int index = -1;
			if (keyValue != null && (index = keyValue.indexOf('=')) > -1) {
				context_param.put(keyValue.substring(0, index),
						keyValue.substring(index + 1));
			}
		}

	}

	public Integer getErrorCode() {
		return errorCode;
	}

	public String getStatus() {
		return status;
	}

	ResumeUtil resumeUtil = null;
}
/************************************************************************************************
 * 52511 characters generated by Talend Open Studio on the April 18, 2011
 * 10:53:09 AM EEST
 ************************************************************************************************/

