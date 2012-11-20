--
-- #%L
-- Service Activity Monitoring :: Server
-- %%
-- Copyright (C) 2011 - 2012 Talend Inc.
-- %%
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
-- 
--      http://www.apache.org/licenses/LICENSE-2.0
-- 
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
-- #L%
--
DROP TABLE EVENTS;
DROP TABLE EVENTS_CUSTOMINFO;
DROP SEQUENCE EVENT_ID;

CREATE SEQUENCE EVENT_ID
    MINVALUE 1
    START WITH 1
    INCREMENT BY 1
    CACHE 20;

CREATE TABLE EVENTS(
ID NUMBER(19) NOT NULL,
EI_TIMESTAMP TIMESTAMP, 
EI_EVENT_TYPE NVARCHAR2(255), 
ORIG_CUSTOM_ID NVARCHAR2(255), 
ORIG_PROCESS_ID NVARCHAR2(255), 
ORIG_HOSTNAME NVARCHAR2(128), 
ORIG_IP NVARCHAR2(64), 
ORIG_PRINCIPAL NVARCHAR2(255),
MI_PORT_TYPE NVARCHAR2(255), 
MI_OPERATION_NAME NVARCHAR2(255), 
MI_MESSAGE_ID NVARCHAR2(255), 
MI_FLOW_ID NVARCHAR2(64), 
MI_TRANSPORT_TYPE NVARCHAR2(255),
CONTENT_CUT NUMBER(1),
MESSAGE_CONTENT CLOB, 
PRIMARY KEY (ID));

CREATE TABLE EVENTS_CUSTOMINFO(
ID NUMBER(19) NOT NULL, 
EVENT_ID NUMBER(19) NOT NULL, 
CUST_KEY NVARCHAR2(255), 
CUST_VALUE NVARCHAR2(255), 
PRIMARY KEY (ID));