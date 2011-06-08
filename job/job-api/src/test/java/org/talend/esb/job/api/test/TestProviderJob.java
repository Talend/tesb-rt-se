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

//the import part of tJavaFlex_2
//import java.util.List;

//the import part of tJavaFlex_3
//import java.util.List;

//the import part of tJavaFlex_4
//import java.util.List;

//the import part of tJavaFlex_7
//import java.util.List;

/**
 * Job: TestProviderJob Purpose: <br>
 * Description: <br>
 *
 * @author
 * @version 4.2.0.NB_r58065
 * @status
 */
public class TestProviderJob implements TalendESBJob {

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
	private final String jobName = "TestProviderJob";
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
					TestProviderJob.this.exception = e;
				}
			}
			if (!(e instanceof TalendException)) {
				try {
					for (java.lang.reflect.Method m : this.getClass()
							.getEnclosingClass().getMethods()) {
						if (m.getName().compareTo(currentComponent + "_error") == 0) {
							m.invoke(TestProviderJob.this, new Object[] { e,
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

	public void tESBProviderRequest_1_error(Exception exception,
			String errorComponent, final java.util.Map<String, Object> globalMap)
			throws TalendException {
		end_Hash.put("tESBProviderRequest_1", System.currentTimeMillis());

		tESBProviderRequest_1_onSubJobError(exception, errorComponent,
				globalMap);

	}

	public void tLogRow_1_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap)
			throws TalendException {
		end_Hash.put("tLogRow_1", System.currentTimeMillis());

		tESBProviderRequest_1_onSubJobError(exception, errorComponent,
				globalMap);

	}

	public void tJavaFlex_2_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap)
			throws TalendException {
		end_Hash.put("tJavaFlex_2", System.currentTimeMillis());

		tESBProviderRequest_1_onSubJobError(exception, errorComponent,
				globalMap);

	}

	public void tLogRow_2_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap)
			throws TalendException {
		end_Hash.put("tLogRow_2", System.currentTimeMillis());

		tESBProviderRequest_1_onSubJobError(exception, errorComponent,
				globalMap);

	}

	public void tFilterRow_1_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap)
			throws TalendException {
		end_Hash.put("tFilterRow_1", System.currentTimeMillis());

		tESBProviderRequest_1_onSubJobError(exception, errorComponent,
				globalMap);

	}

	public void tFilterRow_2_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap)
			throws TalendException {
		end_Hash.put("tFilterRow_2", System.currentTimeMillis());

		tESBProviderRequest_1_onSubJobError(exception, errorComponent,
				globalMap);

	}

	public void tJavaFlex_3_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap)
			throws TalendException {
		end_Hash.put("tJavaFlex_3", System.currentTimeMillis());

		tESBProviderRequest_1_onSubJobError(exception, errorComponent,
				globalMap);

	}

	public void tLogRow_4_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap)
			throws TalendException {
		end_Hash.put("tLogRow_4", System.currentTimeMillis());

		tESBProviderRequest_1_onSubJobError(exception, errorComponent,
				globalMap);

	}

	public void tESBProviderResponse_1_error(Exception exception,
			String errorComponent, final java.util.Map<String, Object> globalMap)
			throws TalendException {
		end_Hash.put("tESBProviderResponse_1", System.currentTimeMillis());

		tESBProviderRequest_1_onSubJobError(exception, errorComponent,
				globalMap);

	}

	public void tJavaFlex_4_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap)
			throws TalendException {
		end_Hash.put("tJavaFlex_4", System.currentTimeMillis());

		tESBProviderRequest_1_onSubJobError(exception, errorComponent,
				globalMap);

	}

	public void tLogRow_3_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap)
			throws TalendException {
		end_Hash.put("tLogRow_3", System.currentTimeMillis());

		tESBProviderRequest_1_onSubJobError(exception, errorComponent,
				globalMap);

	}

	public void tESBProviderFault_2_error(Exception exception,
			String errorComponent, final java.util.Map<String, Object> globalMap)
			throws TalendException {
		end_Hash.put("tESBProviderFault_2", System.currentTimeMillis());

		tESBProviderRequest_1_onSubJobError(exception, errorComponent,
				globalMap);

	}

	public void tJavaFlex_7_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap)
			throws TalendException {
		end_Hash.put("tJavaFlex_7", System.currentTimeMillis());

		tESBProviderRequest_1_onSubJobError(exception, errorComponent,
				globalMap);

	}

	public void tESBProviderFault_1_error(Exception exception,
			String errorComponent, final java.util.Map<String, Object> globalMap)
			throws TalendException {
		end_Hash.put("tESBProviderFault_1", System.currentTimeMillis());

		tESBProviderRequest_1_onSubJobError(exception, errorComponent,
				globalMap);

	}

	public void tESBProviderRequest_1_onSubJobError(Exception exception,
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
	}

	public void setProviderCallback(ESBProviderCallback callback) {
		this.callback = callback;
	}

	public ESBEndpointInfo getEndpoint() {
		return new ESBEndpointInfo() {
			private String uri = "http://127.0.0.1:8088/esb/provider";
			@SuppressWarnings("serial")
			private java.util.Map<String, Object> props = new java.util.HashMap<String, Object>() {
				{
					// "request-response" or "one-way"
					put("COMMUNICATION_STYLE", "request-response");
					//
					put("dataFormat", "PAYLOAD");
					// static
					put("portName",
							"{http://talend.org/esb/service/job}TalendJobAsWebService");
					// local part is: projectName + "_" + processName
					put("serviceName",
							"{http://talend.org/esb/service/job}TEST_TestProviderJob");
					// static
					put("defaultOperationName", "invoke");
					// static
					put("defaultOperationNameSpace", "");
					// endpoint URI configured in tESBProviderInput
					put("publishedEndpointUrl", uri);
				}
			};

			public String getEndpointKey() {
				return "cxf";
			}

			public String getEndpointUri() {
				// projectName + "_" + processName
				return "TEST_TestProviderJob";
				// return uri;
			}

			public java.util.Map<String, Object> getEndpointProperties() {
				return props;
			}
		};
	}

	public class ProviderFault {

		final String message;
		final Object detail;

		public ProviderFault(String message) {
			this(message, null);
		}

		public ProviderFault(String message, Object detail) {
			this.message = message;
			this.detail = detail;
		}

		public String getMessage() {
			return message;
		}

		public Object getDetail() {
			return detail;
		}
	}

	public static class row8Struct implements
			routines.system.IPersistableRow<row8Struct> {
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
		public int compareTo(row8Struct other) {

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

	public static class row7Struct implements
			routines.system.IPersistableRow<row7Struct> {
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
		public int compareTo(row7Struct other) {

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

	public static class row10Struct implements
			routines.system.IPersistableRow<row10Struct> {
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
		public int compareTo(row10Struct other) {

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

	public static class row11Struct implements
			routines.system.IPersistableRow<row11Struct> {
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
		public int compareTo(row11Struct other) {

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

		public String content;

		public String getContent() {
			return this.content;
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

					this.content = readString(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				}

			}

		}

		public void writeData(ObjectOutputStream dos) {
			try {

				// String

				writeString(this.content, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append(super.toString());
			sb.append("[");
			sb.append("content=" + content);
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

	public static class row12Struct implements
			routines.system.IPersistableRow<row12Struct> {
		final static byte[] commonByteArrayLock = new byte[0];
		static byte[] commonByteArray = new byte[0];

		public String content;

		public String getContent() {
			return this.content;
		}

		public String errorMessage;

		public String getErrorMessage() {
			return this.errorMessage;
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

					this.content = readString(dis);

					this.errorMessage = readString(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				}

			}

		}

		public void writeData(ObjectOutputStream dos) {
			try {

				// String

				writeString(this.content, dos);

				// String

				writeString(this.errorMessage, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append(super.toString());
			sb.append("[");
			sb.append("content=" + content);
			sb.append(",errorMessage=" + errorMessage);
			sb.append("]");

			return sb.toString();
		}

		/**
		 * Compare keys
		 */
		public int compareTo(row12Struct other) {

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

	public static class row9Struct implements
			routines.system.IPersistableRow<row9Struct> {
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
		public int compareTo(row9Struct other) {

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

	public static class row5Struct implements
			routines.system.IPersistableRow<row5Struct> {
		final static byte[] commonByteArrayLock = new byte[0];
		static byte[] commonByteArray = new byte[0];

		public String content;

		public String getContent() {
			return this.content;
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

					this.content = readString(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				}

			}

		}

		public void writeData(ObjectOutputStream dos) {
			try {

				// String

				writeString(this.content, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append(super.toString());
			sb.append("[");
			sb.append("content=" + content);
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

	public static class row13Struct implements
			routines.system.IPersistableRow<row13Struct> {
		final static byte[] commonByteArrayLock = new byte[0];
		static byte[] commonByteArray = new byte[0];

		public String content;

		public String getContent() {
			return this.content;
		}

		public String errorMessage;

		public String getErrorMessage() {
			return this.errorMessage;
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

					this.content = readString(dis);

					this.errorMessage = readString(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				}

			}

		}

		public void writeData(ObjectOutputStream dos) {
			try {

				// String

				writeString(this.content, dos);

				// String

				writeString(this.errorMessage, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append(super.toString());
			sb.append("[");
			sb.append("content=" + content);
			sb.append(",errorMessage=" + errorMessage);
			sb.append("]");

			return sb.toString();
		}

		/**
		 * Compare keys
		 */
		public int compareTo(row13Struct other) {

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

		public String content;

		public String getContent() {
			return this.content;
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

					this.content = readString(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				}

			}

		}

		public void writeData(ObjectOutputStream dos) {
			try {

				// String

				writeString(this.content, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append(super.toString());
			sb.append("[");
			sb.append("content=" + content);
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

	public static class row3Struct implements
			routines.system.IPersistableRow<row3Struct> {
		final static byte[] commonByteArrayLock = new byte[0];
		static byte[] commonByteArray = new byte[0];

		public String content;

		public String getContent() {
			return this.content;
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

					this.content = readString(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				}

			}

		}

		public void writeData(ObjectOutputStream dos) {
			try {

				// String

				writeString(this.content, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append(super.toString());
			sb.append("[");
			sb.append("content=" + content);
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

	public void tESBProviderRequest_1Process(
			final java.util.Map<String, Object> globalMap)
			throws TalendException {
		globalMap.put("tESBProviderRequest_1_SUBPROCESS_STATE", 0);

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
				row1Struct row2 = row1;
				row3Struct row3 = new row3Struct();
				row3Struct row4 = row3;
				row5Struct row5 = new row5Struct();
				row6Struct row6 = new row6Struct();
				row7Struct row7 = new row7Struct();
				row7Struct row8 = row7;
				row12Struct row12 = new row12Struct();
				row11Struct row11 = new row11Struct();
				row11Struct row10 = row11;
				row13Struct row13 = new row13Struct();
				row9Struct row9 = new row9Struct();

				/**
				 * [tESBProviderResponse_1 begin ] start
				 */

				ok_Hash.put("tESBProviderResponse_1", false);
				start_Hash.put("tESBProviderResponse_1",
						System.currentTimeMillis());
				currentComponent = "tESBProviderResponse_1";

				int tos_count_tESBProviderResponse_1 = 0;

				/**
				 * [tESBProviderResponse_1 begin ] stop
				 */

				/**
				 * [tLogRow_4 begin ] start
				 */

				ok_Hash.put("tLogRow_4", false);
				start_Hash.put("tLogRow_4", System.currentTimeMillis());
				currentComponent = "tLogRow_4";

				int tos_count_tLogRow_4 = 0;

				// /////////////////////

				final String OUTPUT_FIELD_SEPARATOR_tLogRow_4 = "\n";
				java.io.PrintStream consoleOut_tLogRow_4 = null;

				int nb_line_tLogRow_4 = 0;

				// /////////////////////

				/**
				 * [tLogRow_4 begin ] stop
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
				 * [tESBProviderFault_2 begin ] start
				 */

				ok_Hash.put("tESBProviderFault_2", false);
				start_Hash.put("tESBProviderFault_2",
						System.currentTimeMillis());
				currentComponent = "tESBProviderFault_2";

				int tos_count_tESBProviderFault_2 = 0;

				/**
				 * [tESBProviderFault_2 begin ] stop
				 */

				/**
				 * [tLogRow_3 begin ] start
				 */

				ok_Hash.put("tLogRow_3", false);
				start_Hash.put("tLogRow_3", System.currentTimeMillis());
				currentComponent = "tLogRow_3";

				int tos_count_tLogRow_3 = 0;

				// /////////////////////

				final String OUTPUT_FIELD_SEPARATOR_tLogRow_3 = "\n";
				java.io.PrintStream consoleOut_tLogRow_3 = null;

				int nb_line_tLogRow_3 = 0;

				// /////////////////////

				/**
				 * [tLogRow_3 begin ] stop
				 */

				/**
				 * [tJavaFlex_4 begin ] start
				 */

				ok_Hash.put("tJavaFlex_4", false);
				start_Hash.put("tJavaFlex_4", System.currentTimeMillis());
				currentComponent = "tJavaFlex_4";

				int tos_count_tJavaFlex_4 = 0;

				// start part of your Java code

				/**
				 * [tJavaFlex_4 begin ] stop
				 */

				/**
				 * [tFilterRow_2 begin ] start
				 */

				ok_Hash.put("tFilterRow_2", false);
				start_Hash.put("tFilterRow_2", System.currentTimeMillis());
				currentComponent = "tFilterRow_2";

				int tos_count_tFilterRow_2 = 0;
				int nb_line_tFilterRow_2 = 0;
				int nb_line_ok_tFilterRow_2 = 0;
				int nb_line_reject_tFilterRow_2 = 0;

				class Operator_tFilterRow_2 {
					private String sErrorMsg = "";
					private boolean bMatchFlag = true;
					private String sUnionFlag = "&&";

					public Operator_tFilterRow_2(String unionFlag) {
						sUnionFlag = unionFlag;
						bMatchFlag = "||".equals(unionFlag) ? false : true;
					}

					public String getErrorMsg() {
						if (sErrorMsg != null && sErrorMsg.length() > 1)
							return sErrorMsg.substring(1);
						else
							return null;
					}

					public boolean getMatchFlag() {
						return bMatchFlag;
					}

					public void matches(boolean partMatched, String reason) {
						// no need to care about the next judgement
						if ("||".equals(sUnionFlag) && bMatchFlag) {
							return;
						}

						if (!partMatched) {
							sErrorMsg += "|" + reason;
						}

						if ("||".equals(sUnionFlag))
							bMatchFlag = bMatchFlag || partMatched;
						else
							bMatchFlag = bMatchFlag && partMatched;
					}
				}

				/**
				 * [tFilterRow_2 begin ] stop
				 */

				/**
				 * [tESBProviderFault_1 begin ] start
				 */

				ok_Hash.put("tESBProviderFault_1", false);
				start_Hash.put("tESBProviderFault_1",
						System.currentTimeMillis());
				currentComponent = "tESBProviderFault_1";

				int tos_count_tESBProviderFault_1 = 0;

				/**
				 * [tESBProviderFault_1 begin ] stop
				 */

				/**
				 * [tJavaFlex_7 begin ] start
				 */

				ok_Hash.put("tJavaFlex_7", false);
				start_Hash.put("tJavaFlex_7", System.currentTimeMillis());
				currentComponent = "tJavaFlex_7";

				int tos_count_tJavaFlex_7 = 0;

				// start part of your Java code

				/**
				 * [tJavaFlex_7 begin ] stop
				 */

				/**
				 * [tFilterRow_1 begin ] start
				 */

				ok_Hash.put("tFilterRow_1", false);
				start_Hash.put("tFilterRow_1", System.currentTimeMillis());
				currentComponent = "tFilterRow_1";

				int tos_count_tFilterRow_1 = 0;
				int nb_line_tFilterRow_1 = 0;
				int nb_line_ok_tFilterRow_1 = 0;
				int nb_line_reject_tFilterRow_1 = 0;

				class Operator_tFilterRow_1 {
					private String sErrorMsg = "";
					private boolean bMatchFlag = true;
					private String sUnionFlag = "&&";

					public Operator_tFilterRow_1(String unionFlag) {
						sUnionFlag = unionFlag;
						bMatchFlag = "||".equals(unionFlag) ? false : true;
					}

					public String getErrorMsg() {
						if (sErrorMsg != null && sErrorMsg.length() > 1)
							return sErrorMsg.substring(1);
						else
							return null;
					}

					public boolean getMatchFlag() {
						return bMatchFlag;
					}

					public void matches(boolean partMatched, String reason) {
						// no need to care about the next judgement
						if ("||".equals(sUnionFlag) && bMatchFlag) {
							return;
						}

						if (!partMatched) {
							sErrorMsg += "|" + reason;
						}

						if ("||".equals(sUnionFlag))
							bMatchFlag = bMatchFlag || partMatched;
						else
							bMatchFlag = bMatchFlag && partMatched;
					}
				}

				/**
				 * [tFilterRow_1 begin ] stop
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
				 * [tLogRow_1 begin ] start
				 */

				ok_Hash.put("tLogRow_1", false);
				start_Hash.put("tLogRow_1", System.currentTimeMillis());
				currentComponent = "tLogRow_1";

				int tos_count_tLogRow_1 = 0;

				// /////////////////////

				final String OUTPUT_FIELD_SEPARATOR_tLogRow_1 = "\n";
				java.io.PrintStream consoleOut_tLogRow_1 = null;

				int nb_line_tLogRow_1 = 0;

				// /////////////////////

				/**
				 * [tLogRow_1 begin ] stop
				 */

				/**
				 * [tESBProviderRequest_1 begin ] start
				 */

				ok_Hash.put("tESBProviderRequest_1", false);
				start_Hash.put("tESBProviderRequest_1",
						System.currentTimeMillis());
				currentComponent = "tESBProviderRequest_1";

				int tos_count_tESBProviderRequest_1 = 0;
				// *** ESB context initialization
				final String endpointUrl = "http://127.0.0.1:8088/esb/provider";
				// *** ESB context initialization finish

				/**
				 * queued message exchange
				 */
				class QueuedExchangeContextImpl<T> {

					/**
					 * Exchange timeout in seconds
					 */
					private static final long EXCHANGE_TIMEOUT = 50;

					private final java.util.concurrent.Exchanger<Exception> exceptionExchange = new java.util.concurrent.Exchanger<Exception>();
					private final java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(
							1);

					private final T input;

					private T output = null;
					private T faultDetails = null;
					private Throwable fault = null;
					private String faultMessage = null;

					public QueuedExchangeContextImpl(T inMsg) {
						this.input = inMsg;
					}

					/**
					 * Don't forget to call this method when you are done with
					 * processing of the {@link QueuedExchangeContext}
					 */
					public void release() throws Exception {
						latch.countDown();
						Exception exception;
						try {
							exception = exceptionExchange.exchange(null,
									EXCHANGE_TIMEOUT,
									java.util.concurrent.TimeUnit.SECONDS);
						} catch (InterruptedException e) {
							throw new Exception(e);
						} catch (java.util.concurrent.TimeoutException e) {
							throw new Exception(e);
						}
						if (exception != null) {
							throw exception;
						}
					}

					/**
					 * This operation have to be called on the Web Service
					 * thread to send response if required
					 *
					 * @throws InterruptedException
					 */
					public void completeQueuedProcessing()
							throws InterruptedException {
						exceptionExchange.exchange(null);
					}

					/**
					 * @throws InterruptedException
					 */
					void waitForRelease(long timeout,
							java.util.concurrent.TimeUnit unit)
							throws InterruptedException {
						latch.await(timeout, unit);
					}

					public T getInputMessage() {
						return input;
					}

					public void serveOutputMessage(T response) {
						output = response;
					}

					public void serveBusinessFault(String faultMessage,
							T faultDetails) {
						this.faultMessage = (null == faultMessage || 0 == faultMessage
								.trim().length()) ? "Talend job business fault"
								: faultMessage;
						this.faultDetails = faultDetails;
					}

					public void serveFault(String faultMessage, Throwable fault) {
						this.faultMessage = (null == faultMessage || 0 == faultMessage
								.trim().length()) ? "Talend job error"
								: faultMessage;
						this.fault = fault;
					}

					public boolean isFault() {
						return (faultMessage != null);
					}

					public boolean isBusinessFault() {
						return isFault() && null == fault;
					}

					public T getResponse() {
						return output;
					}

					public String getFaultMessage() {
						return faultMessage;
					}

					public T getBusinessFaultDetails() {
						return faultDetails;
					}

					public Throwable getFault() {
						return fault;
					}

				}

				/**
				 * message exchange controller
				 */
				class QueuedMessageHandlerImpl<T> implements
						ESBProviderCallback {
					private final int MAX_QUEUE_SIZE = 1000;

					private final int WAIT_TIMEOUT_SECONDS = 120;

					private final java.util.concurrent.BlockingQueue<QueuedExchangeContextImpl<?>> queue = new java.util.concurrent.LinkedBlockingQueue<QueuedExchangeContextImpl<?>>(
							MAX_QUEUE_SIZE);

					/**
					 * This method add a newly created
					 * {@link QueuedExchangeContextImpl} into the internal
					 * blocking queue where consumer thread is waiting for it.
					 * Then it waits until the {@link QueuedExchangeContextImpl}
					 * will be completed for request-response operations
					 */
					public QueuedExchangeContextImpl<T> invoke(T request) {
						QueuedExchangeContextImpl<T> context = new QueuedExchangeContextImpl<T>(
								request);
						boolean inserted = queue.offer(context);
						if (!inserted) {
							try {
								context.release();
							} catch (Exception e) {
								e.printStackTrace();
							}
							// context.serveFault("job pool overflow exceed",
							// null);
							throw new RuntimeException(
									"Can't queue request, queue size of "
											+ MAX_QUEUE_SIZE + " is exceeded");
						} else {
							try {
								context.waitForRelease(WAIT_TIMEOUT_SECONDS,
										java.util.concurrent.TimeUnit.SECONDS);
							} catch (InterruptedException ie) {
								// context.serveFault("job execution timeout",
								// ie);
								throw new RuntimeException(
										"job execution timeout: "
												+ ie.getMessage());
							}
						}
						return context;
					}

					QueuedExchangeContextImpl<T> currentExchangeContext;

					public Object getRequest()
							throws ESBJobInterruptedException {
						currentExchangeContext = null;
						try {
							currentExchangeContext = (QueuedExchangeContextImpl<T>) queue
									.take();
						} catch (InterruptedException e) {
							// e.printStackTrace();
							throw new RuntimeException(e);
						}
						return currentExchangeContext.getInputMessage();
					}

					public void sendResponse(Object output) {
						if (null == currentExchangeContext) {
							throw new RuntimeException(
									"sendResponse() invoked before getRequest()");
						}

						// business fault
						if (output instanceof ProviderFault) {
							ProviderFault providerFault = (ProviderFault) output;
							currentExchangeContext.serveBusinessFault(
									providerFault.getMessage(),
									(T) providerFault.getDetail());
						}

						// job error
						if (output instanceof Exception) {
							currentExchangeContext.serveFault(
									"Talend job execution error",
									(Throwable) output);
						}

						// payload
						currentExchangeContext.serveOutputMessage((T) output);

						try {
							currentExchangeContext.release();
						} catch (Exception e) {
							// e.printStackTrace();
							throw new RuntimeException(e);
						}
					}
				}

				/**
				 * web service provider implementation
				 */
				@javax.jws.WebService(name = "TalendJobAsWebService", targetNamespace = "http://talend.org/esb/service/job")
				@javax.jws.soap.SOAPBinding(parameterStyle = javax.jws.soap.SOAPBinding.ParameterStyle.BARE)
				@javax.xml.ws.ServiceMode(value = javax.xml.ws.Service.Mode.PAYLOAD)
				@javax.xml.ws.WebServiceProvider()
				class ESBProvider_tESBProviderRequest_1 implements
						javax.xml.ws.Provider<javax.xml.transform.Source> {

					private javax.xml.transform.TransformerFactory factory = javax.xml.transform.TransformerFactory
							.newInstance();
					private QueuedMessageHandlerImpl<org.dom4j.Document> messageHandler;

					private final String TNS = ESBProvider_tESBProviderRequest_1.class
							.getAnnotation(javax.jws.WebService.class)
							.targetNamespace();

					public ESBProvider_tESBProviderRequest_1(
							QueuedMessageHandlerImpl<org.dom4j.Document> messageHandler) {
						this.messageHandler = messageHandler;
					}

					@javax.jws.WebMethod(operationName = "invoke", action = "http://talend.org/esb/service/job/invoke")
					@javax.jws.WebResult(name = "jobOutput", targetNamespace = "http://talend.org/esb/service/job",
					// targetNamespace = "",
					partName = "response")
					public javax.xml.transform.Source invoke(
							@javax.jws.WebParam(name = "jobInput", targetNamespace = "http://talend.org/esb/service/job",
							// targetNamespace = "",
							partName = "request") javax.xml.transform.Source request) {

						// System.out.println(System.currentTimeMillis() +
						// " -> handleMessage");

						try {
							org.dom4j.io.DocumentResult docResult = new org.dom4j.io.DocumentResult();
							factory.newTransformer().transform(request,
									docResult);
							org.dom4j.Document requestDoc = docResult
									.getDocument();
							// System.out.println("request: " +
							// requestDoc.asXML());

							QueuedExchangeContextImpl<org.dom4j.Document> messageExchange = messageHandler
									.invoke(requestDoc);

							try {
								if (messageExchange.isFault()) {
									String faultString = messageExchange
											.getFaultMessage();
									// System.out.println("fault: " +
									// faultString);

									if (messageExchange.isBusinessFault()) {
										org.dom4j.Document faultDoc = messageExchange
												.getBusinessFaultDetails();
										javax.xml.soap.SOAPFactory soapFactory = javax.xml.soap.SOAPFactory
												.newInstance();
										javax.xml.soap.SOAPFault soapFault = soapFactory
												.createFault(
														faultString,
														new javax.xml.namespace.QName(
																TNS,
																"businessFault"));
										if (null != faultDoc) {
											// System.out.println("business fault details: "
											// + faultDoc.asXML());
											org.dom4j.io.DOMWriter writer = new org.dom4j.io.DOMWriter();
											org.w3c.dom.Document faultDetailDom = writer
													.write(faultDoc);
											soapFault
													.addDetail()
													.appendChild(
															soapFault
																	.getOwnerDocument()
																	.importNode(
																			faultDetailDom
																					.getDocumentElement(),
																			true));
										}
										throw new javax.xml.ws.soap.SOAPFaultException(
												soapFault);
									} else {
										Throwable error = messageExchange
												.getFault();
										// System.out.println("job error: " +
										// error.getMessage());
										if (error instanceof RuntimeException) {
											throw (RuntimeException) error;
										} else {
											throw new RuntimeException(
													faultString, error);
										}
									}
								} else {
									org.dom4j.Document responseDoc = messageExchange
											.getResponse();
									if (null == responseDoc) {
										// System.out.println("response: empty");
										throw new RuntimeException(
												"no response provided by Talend job");
									}
									// System.out.println("response: " +
									// responseDoc.asXML());

									return new org.dom4j.io.DocumentSource(
											responseDoc);
								}
							} finally {
								messageExchange.completeQueuedProcessing();
							}

						} catch (RuntimeException ex) {
							throw ex;
						} catch (Throwable ex) {
							ex.printStackTrace();
							throw new RuntimeException(ex);
						} finally {
							// System.out.println(System.currentTimeMillis() +
							// " <- handleMessage");
						}
					}
				}

				class HandlerThread_tESBProviderRequest_1 extends Thread {

					private final String TNS = ESBProvider_tESBProviderRequest_1.class
							.getAnnotation(javax.jws.WebService.class)
							.targetNamespace();

					private final String serviceName;

					private javax.xml.ws.Endpoint endpoint;
					QueuedMessageHandlerImpl<org.dom4j.Document> handler;

					public HandlerThread_tESBProviderRequest_1(
							QueuedMessageHandlerImpl<org.dom4j.Document> handler,
							String serviceName) {
						this.handler = handler;
						this.serviceName = serviceName;
					}

					public void run() {
						ESBProvider_tESBProviderRequest_1 esbProvider = new ESBProvider_tESBProviderRequest_1(
								handler);

						endpoint = javax.xml.ws.Endpoint.create(esbProvider);
						@SuppressWarnings("serial")
						java.util.Map<String, Object> map = new java.util.HashMap<String, Object>() {
							{
								put(javax.xml.ws.Endpoint.WSDL_SERVICE,
										new javax.xml.namespace.QName(TNS,
												serviceName));
								put(javax.xml.ws.Endpoint.WSDL_PORT,
										new javax.xml.namespace.QName(TNS,
												serviceName + "SoapBinding"));
							}
						};
						endpoint.setProperties(map);
						endpoint.publish(endpointUrl);

						System.out.println("web service [endpoint: "
								+ endpointUrl + "] published");
					}

					public void stopEndpoint() {
						if (null != endpoint) {
							endpoint.stop();
							System.out.println("web service [endpoint: "
									+ endpointUrl + "] unpublished");
						}
					}
				}

				// *** external processor(s) initialization
				ESBProviderCallback providerCallback;
				HandlerThread_tESBProviderRequest_1 handlerThread_tESBProviderRequest_1 = null;
				if (null == callback) {
					final QueuedMessageHandlerImpl<org.dom4j.Document> handler = new QueuedMessageHandlerImpl<org.dom4j.Document>();
					handlerThread_tESBProviderRequest_1 = new HandlerThread_tESBProviderRequest_1(
							handler, "TEST_" + this.getClass().getSimpleName());
					handlerThread_tESBProviderRequest_1.start();
					providerCallback = handler;
				} else {
					providerCallback = callback;
				}

				globalMap.put("esbHandler", providerCallback);
				// *** external processor(s) initialization finish

				int nb_line_tESBProviderRequest_1 = 0;

				try {
					// This is a beginning of the ESB provider request component
					// cycle
					while (true) {
						try {

							/**
							 * [tESBProviderRequest_1 begin ] stop
							 */
							/**
							 * [tESBProviderRequest_1 main ] start
							 */

							currentComponent = "tESBProviderRequest_1";

							ESBProviderCallback esbHandler_tESBProviderRequest_1 = (ESBProviderCallback) globalMap
									.get("esbHandler");

							org.dom4j.Document requestMessage_tESBProviderRequest_1 = (org.dom4j.Document) esbHandler_tESBProviderRequest_1
									.getRequest();

							Document talendDocument_tESBProviderRequest_1 = new Document();
							talendDocument_tESBProviderRequest_1
									.setDocument(requestMessage_tESBProviderRequest_1);
							row1.payload = talendDocument_tESBProviderRequest_1;

							tos_count_tESBProviderRequest_1++;

							/**
							 * [tESBProviderRequest_1 main ] stop
							 */

							/**
							 * [tLogRow_1 main ] start
							 */

							currentComponent = "tLogRow_1";

							// /////////////////////

							StringBuilder strBuffer_tLogRow_1 = new StringBuilder();
							strBuffer_tLogRow_1.append("[tLogRow_1] ");

							if (row1.payload != null) { //

								strBuffer_tLogRow_1.append(String
										.valueOf(row1.payload));

							} //

							if (globalMap.get("tLogRow_CONSOLE") != null) {
								consoleOut_tLogRow_1 = (java.io.PrintStream) globalMap
										.get("tLogRow_CONSOLE");
							} else {
								consoleOut_tLogRow_1 = new java.io.PrintStream(
										new java.io.BufferedOutputStream(
												System.out));
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

							row2 = row1;

							tos_count_tLogRow_1++;

							/**
							 * [tLogRow_1 main ] stop
							 */

							/**
							 * [tJavaFlex_2 main ] start
							 */

							currentComponent = "tJavaFlex_2";

							// here is the main part of the component,
							// a piece of code executed in the row loop
							Document requestTalendDoc = row2.payload;
							org.dom4j.Document requestDoc = requestTalendDoc
									.getDocument();
							org.dom4j.Element rootElement = requestDoc
									.getRootElement();
							String text = rootElement.getTextTrim();
							System.out.println("### " + text);
							row3.content = text;

							tos_count_tJavaFlex_2++;

							/**
							 * [tJavaFlex_2 main ] stop
							 */

							/**
							 * [tLogRow_2 main ] start
							 */

							currentComponent = "tLogRow_2";

							// /////////////////////

							StringBuilder strBuffer_tLogRow_2 = new StringBuilder();
							strBuffer_tLogRow_2.append("[tLogRow_2] ");

							if (row3.content != null) { //

								strBuffer_tLogRow_2.append(String
										.valueOf(row3.content));

							} //

							if (globalMap.get("tLogRow_CONSOLE") != null) {
								consoleOut_tLogRow_2 = (java.io.PrintStream) globalMap
										.get("tLogRow_CONSOLE");
							} else {
								consoleOut_tLogRow_2 = new java.io.PrintStream(
										new java.io.BufferedOutputStream(
												System.out));
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

							row4 = row3;

							tos_count_tLogRow_2++;

							/**
							 * [tLogRow_2 main ] stop
							 */

							/**
							 * [tFilterRow_1 main ] start
							 */

							currentComponent = "tFilterRow_1";

							row5 = null;
							row13 = null;
							Operator_tFilterRow_1 ope_tFilterRow_1 = new Operator_tFilterRow_1(
									"&&");
							ope_tFilterRow_1.matches(
									(row4.content == null ? false
											: row4.content.length() > 0),
									" content.length() > 0 faild");

							if (ope_tFilterRow_1.getMatchFlag()) {
								if (row5 == null) {
									row5 = new row5Struct();
								}
								row5.content = row4.content;
								nb_line_ok_tFilterRow_1++;
							} else {
								if (row13 == null) {
									row13 = new row13Struct();
								}
								row13.content = row4.content;
								row13.errorMessage = ope_tFilterRow_1
										.getErrorMsg();
								nb_line_reject_tFilterRow_1++;
							}

							nb_line_tFilterRow_1++;

							tos_count_tFilterRow_1++;

							/**
							 * [tFilterRow_1 main ] stop
							 */
							// Start of branch "row5"
							if (row5 != null) {
								row12 = null;

								/**
								 * [tFilterRow_2 main ] start
								 */

								currentComponent = "tFilterRow_2";

								row6 = null;
								row12 = null;
								Operator_tFilterRow_2 ope_tFilterRow_2 = new Operator_tFilterRow_2(
										"&&");
								ope_tFilterRow_2
										.matches(
												(row5.content == null ? false
														: row5.content
																.toLowerCase()
																.compareTo(
																		"xxx") != 0),
												" content.toLowerCase().compareTo(\"xxx\") != 0 faild");

								if (ope_tFilterRow_2.getMatchFlag()) {
									if (row6 == null) {
										row6 = new row6Struct();
									}
									row6.content = row5.content;
									nb_line_ok_tFilterRow_2++;
								} else {
									if (row12 == null) {
										row12 = new row12Struct();
									}
									row12.content = row5.content;
									row12.errorMessage = ope_tFilterRow_2
											.getErrorMsg();
									nb_line_reject_tFilterRow_2++;
								}

								nb_line_tFilterRow_2++;

								tos_count_tFilterRow_2++;

								/**
								 * [tFilterRow_2 main ] stop
								 */
								// Start of branch "row6"
								if (row6 != null) {

									/**
									 * [tJavaFlex_3 main ] start
									 */

									currentComponent = "tJavaFlex_3";

									// here is the main part of the component,
									// a piece of code executed in the row loop
									String name = row6.content;
									String responseText = "Hello, " + name
											+ "!";

									org.dom4j.Document responseDoc = org.dom4j.DocumentHelper
											.createDocument();
									responseDoc
											.addElement(
													new org.dom4j.QName(
															"jobOutput",
															org.dom4j.Namespace
																	.get("http://talend.org/esb/service/job")))
											.addText(responseText);

									Document responseTalendDoc = new Document();
									responseTalendDoc.setDocument(responseDoc);
									row7.payload = responseTalendDoc;

									tos_count_tJavaFlex_3++;

									/**
									 * [tJavaFlex_3 main ] stop
									 */

									/**
									 * [tLogRow_4 main ] start
									 */

									currentComponent = "tLogRow_4";

									// /////////////////////

									StringBuilder strBuffer_tLogRow_4 = new StringBuilder();
									strBuffer_tLogRow_4.append("[tLogRow_4] ");

									if (row7.payload != null) { //

										strBuffer_tLogRow_4.append(String
												.valueOf(row7.payload));

									} //

									if (globalMap.get("tLogRow_CONSOLE") != null) {
										consoleOut_tLogRow_4 = (java.io.PrintStream) globalMap
												.get("tLogRow_CONSOLE");
									} else {
										consoleOut_tLogRow_4 = new java.io.PrintStream(
												new java.io.BufferedOutputStream(
														System.out));
										globalMap.put("tLogRow_CONSOLE",
												consoleOut_tLogRow_4);
									}

									consoleOut_tLogRow_4
											.println(strBuffer_tLogRow_4
													.toString());
									consoleOut_tLogRow_4.flush();
									nb_line_tLogRow_4++;
									// ////

									// ////

									// /////////////////////

									row8 = row7;

									tos_count_tLogRow_4++;

									/**
									 * [tLogRow_4 main ] stop
									 */

									/**
									 * [tESBProviderResponse_1 main ] start
									 */

									currentComponent = "tESBProviderResponse_1";

									Document esbProviderResponseDoc_tESBProviderResponse_1 = row8.payload;

									((ESBProviderCallback) globalMap
											.get("esbHandler"))
											.sendResponse(esbProviderResponseDoc_tESBProviderResponse_1
													.getDocument());

									tos_count_tESBProviderResponse_1++;

									/**
									 * [tESBProviderResponse_1 main ] stop
									 */

								} // End of branch "row6"

								// Start of branch "row12"
								if (row12 != null) {

									/**
									 * [tJavaFlex_4 main ] start
									 */

									currentComponent = "tJavaFlex_4";

									// here is the main part of the component,
									// a piece of code executed in the row loop

									String name = row12.content;
									String faultText = "unknown person: "
											+ name;

									org.dom4j.Document faultDoc = org.dom4j.DocumentHelper
											.createDocument();
									faultDoc.addElement(
											new org.dom4j.QName(
													"jobFault",
													org.dom4j.Namespace
															.get("http://talend.org/esb/service/job")))
											.addText(faultText);

									Document faultTalendDoc = new Document();
									faultTalendDoc.setDocument(faultDoc);
									row10.payload = faultTalendDoc;

									tos_count_tJavaFlex_4++;

									/**
									 * [tJavaFlex_4 main ] stop
									 */

									/**
									 * [tLogRow_3 main ] start
									 */

									currentComponent = "tLogRow_3";

									// /////////////////////

									StringBuilder strBuffer_tLogRow_3 = new StringBuilder();
									strBuffer_tLogRow_3.append("[tLogRow_3] ");

									if (row11.payload != null) { //

										strBuffer_tLogRow_3.append(String
												.valueOf(row11.payload));

									} //

									if (globalMap.get("tLogRow_CONSOLE") != null) {
										consoleOut_tLogRow_3 = (java.io.PrintStream) globalMap
												.get("tLogRow_CONSOLE");
									} else {
										consoleOut_tLogRow_3 = new java.io.PrintStream(
												new java.io.BufferedOutputStream(
														System.out));
										globalMap.put("tLogRow_CONSOLE",
												consoleOut_tLogRow_3);
									}

									consoleOut_tLogRow_3
											.println(strBuffer_tLogRow_3
													.toString());
									consoleOut_tLogRow_3.flush();
									nb_line_tLogRow_3++;
									// ////

									// ////

									// /////////////////////

									row10 = row11;

									tos_count_tLogRow_3++;

									/**
									 * [tLogRow_3 main ] stop
									 */

									/**
									 * [tESBProviderFault_2 main ] start
									 */

									currentComponent = "tESBProviderFault_2";

									String esbProviderFaultTitle_tESBProviderFault_2 = "access forbidden"
											+ " [tESBProviderFault_2]";
									Document esbProviderFaultDoc_tESBProviderFault_2 = null;
									esbProviderFaultDoc_tESBProviderFault_2 = row10.payload;
									((ESBProviderCallback) globalMap
											.get("esbHandler"))
											.sendResponse(new ProviderFault(
													esbProviderFaultTitle_tESBProviderFault_2,
													(null == esbProviderFaultDoc_tESBProviderFault_2) ? null
															: esbProviderFaultDoc_tESBProviderFault_2
																	.getDocument()));

									tos_count_tESBProviderFault_2++;

									/**
									 * [tESBProviderFault_2 main ] stop
									 */

								} // End of branch "row12"

							} // End of branch "row5"

							// Start of branch "row13"
							if (row13 != null) {

								/**
								 * [tJavaFlex_7 main ] start
								 */

								currentComponent = "tJavaFlex_7";

								// here is the main part of the component,
								// a piece of code executed in the row loop

								tos_count_tJavaFlex_7++;

								/**
								 * [tJavaFlex_7 main ] stop
								 */

								/**
								 * [tESBProviderFault_1 main ] start
								 */

								currentComponent = "tESBProviderFault_1";

								String esbProviderFaultTitle_tESBProviderFault_1 = "empty user name"
										+ " [tESBProviderFault_1]";
								Document esbProviderFaultDoc_tESBProviderFault_1 = null;
								esbProviderFaultDoc_tESBProviderFault_1 = row9.payload;
								((ESBProviderCallback) globalMap
										.get("esbHandler"))
										.sendResponse(new ProviderFault(
												esbProviderFaultTitle_tESBProviderFault_1,
												(null == esbProviderFaultDoc_tESBProviderFault_1) ? null
														: esbProviderFaultDoc_tESBProviderFault_1
																.getDocument()));

								tos_count_tESBProviderFault_1++;

								/**
								 * [tESBProviderFault_1 main ] stop
								 */

							} // End of branch "row13"

							/**
							 * [tESBProviderRequest_1 end ] start
							 */

							currentComponent = "tESBProviderRequest_1";

						} catch (ESBJobInterruptedException e) {
							// job interrupted from outside
							break;
						} catch (Throwable e) {
							((ESBProviderCallback) globalMap.get("esbHandler"))
									.sendResponse(e);
						} finally {
							// Exit from this loop is made by the configuring
							// "Keep listening"
							// parameter to false. Then we will have a break
							// before.
							if ("false".equals("true")) {
								break;
							}
						}
						nb_line_tESBProviderRequest_1++;
						globalMap.put("tESBProviderRequest_1_NB_LINE",
								nb_line_tESBProviderRequest_1);
					} // This is the end of the ESB Service Provider loop
				} finally {
					// for "keep listening" == false web service need a time to
					// serve response
					Thread.currentThread();
					Thread.sleep(500);
					// unsubscribe
					if (null != handlerThread_tESBProviderRequest_1) {
						// stop endpoint in case it was opened by job
						handlerThread_tESBProviderRequest_1.stopEndpoint();
					}
				}

				ok_Hash.put("tESBProviderRequest_1", true);
				end_Hash.put("tESBProviderRequest_1",
						System.currentTimeMillis());

				/**
				 * [tESBProviderRequest_1 end ] stop
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

				/**
				 * [tFilterRow_1 end ] start
				 */

				currentComponent = "tFilterRow_1";

				globalMap.put("tFilterRow_1_NB_LINE", nb_line_tFilterRow_1);
				globalMap.put("tFilterRow_1_NB_LINE_OK",
						nb_line_ok_tFilterRow_1);
				globalMap.put("tFilterRow_1_NB_LINE_REJECT",
						nb_line_reject_tFilterRow_1);

				ok_Hash.put("tFilterRow_1", true);
				end_Hash.put("tFilterRow_1", System.currentTimeMillis());

				/**
				 * [tFilterRow_1 end ] stop
				 */

				/**
				 * [tFilterRow_2 end ] start
				 */

				currentComponent = "tFilterRow_2";

				globalMap.put("tFilterRow_2_NB_LINE", nb_line_tFilterRow_2);
				globalMap.put("tFilterRow_2_NB_LINE_OK",
						nb_line_ok_tFilterRow_2);
				globalMap.put("tFilterRow_2_NB_LINE_REJECT",
						nb_line_reject_tFilterRow_2);

				ok_Hash.put("tFilterRow_2", true);
				end_Hash.put("tFilterRow_2", System.currentTimeMillis());

				/**
				 * [tFilterRow_2 end ] stop
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
				 * [tLogRow_4 end ] start
				 */

				currentComponent = "tLogRow_4";

				// ////
				// ////
				globalMap.put("tLogRow_4_NB_LINE", nb_line_tLogRow_4);

				// /////////////////////

				ok_Hash.put("tLogRow_4", true);
				end_Hash.put("tLogRow_4", System.currentTimeMillis());

				/**
				 * [tLogRow_4 end ] stop
				 */

				/**
				 * [tESBProviderResponse_1 end ] start
				 */

				currentComponent = "tESBProviderResponse_1";

				ok_Hash.put("tESBProviderResponse_1", true);
				end_Hash.put("tESBProviderResponse_1",
						System.currentTimeMillis());

				/**
				 * [tESBProviderResponse_1 end ] stop
				 */

				/**
				 * [tJavaFlex_4 end ] start
				 */

				currentComponent = "tJavaFlex_4";

				// end of the component, outside/closing the loop

				ok_Hash.put("tJavaFlex_4", true);
				end_Hash.put("tJavaFlex_4", System.currentTimeMillis());

				/**
				 * [tJavaFlex_4 end ] stop
				 */

				/**
				 * [tLogRow_3 end ] start
				 */

				currentComponent = "tLogRow_3";

				// ////
				// ////
				globalMap.put("tLogRow_3_NB_LINE", nb_line_tLogRow_3);

				// /////////////////////

				ok_Hash.put("tLogRow_3", true);
				end_Hash.put("tLogRow_3", System.currentTimeMillis());

				/**
				 * [tLogRow_3 end ] stop
				 */

				/**
				 * [tESBProviderFault_2 end ] start
				 */

				currentComponent = "tESBProviderFault_2";

				ok_Hash.put("tESBProviderFault_2", true);
				end_Hash.put("tESBProviderFault_2", System.currentTimeMillis());

				/**
				 * [tESBProviderFault_2 end ] stop
				 */

				/**
				 * [tJavaFlex_7 end ] start
				 */

				currentComponent = "tJavaFlex_7";

				// end of the component, outside/closing the loop

				ok_Hash.put("tJavaFlex_7", true);
				end_Hash.put("tJavaFlex_7", System.currentTimeMillis());

				/**
				 * [tJavaFlex_7 end ] stop
				 */

				/**
				 * [tESBProviderFault_1 end ] start
				 */

				currentComponent = "tESBProviderFault_1";

				ok_Hash.put("tESBProviderFault_1", true);
				end_Hash.put("tESBProviderFault_1", System.currentTimeMillis());

				/**
				 * [tESBProviderFault_1 end ] stop
				 */

			}// end the resume

		} catch (Exception e) {

			throw new TalendException(e, currentComponent, globalMap);

		} catch (Error error) {

			throw new Error(error);

		}

		globalMap.put("tESBProviderRequest_1_SUBPROCESS_STATE", 1);
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
		final TestProviderJob TestProviderJobClass = new TestProviderJob();

		int exitCode = TestProviderJobClass.runJobInTOS(args);

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
			java.io.InputStream inContext = TestProviderJob.class
					.getClassLoader().getResourceAsStream(
							"test/testproviderjob_0_1/contexts/" + contextStr
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
			tESBProviderRequest_1Process(globalMap);
			status = "end";
		} catch (TalendException e_tESBProviderRequest_1) {
			status = "failure";
			e_tESBProviderRequest_1.printStackTrace();
			globalMap.put("tESBProviderRequest_1_SUBPROCESS_STATE", -1);

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
					+ " bytes memory increase when running : TestProviderJob");
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
 * 92370 characters generated by Talend Open Studio on the April 15, 2011
 * 5:49:01 PM EEST
 ************************************************************************************************/
