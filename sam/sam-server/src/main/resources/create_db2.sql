--
-- #%L
-- Service Activity Monitoring :: Server
-- %%
-- Copyright (c) 2006-2021 Talend Inc. - www.talend.com
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

-- SET SCHEMA TEST

CREATE TABLE EVENTS ( ID BIGINT  NOT NULL , EI_TIMESTAMP TIMESTAMP , EI_EVENT_TYPE VARCHAR (255) , ORIG_CUSTOM_ID VARCHAR (255) , ORIG_PROCESS_ID VARCHAR (255) , ORIG_HOSTNAME VARCHAR (128) , ORIG_IP VARCHAR (64) , ORIG_PRINCIPAL VARCHAR (255) , MI_PORT_TYPE VARCHAR (255) , MI_OPERATION_NAME VARCHAR (255) , MI_MESSAGE_ID VARCHAR (255) , MI_FLOW_ID VARCHAR (64) , MI_TRANSPORT_TYPE VARCHAR (255) , MESSAGE_CONTENT "LONG VARCHAR" , CONTENT_CUT SMALLINT  , CONSTRAINT CC1311339921205 PRIMARY KEY ( ID)  ) ;

CREATE TABLE EVENTS_CUSTOMINFO ( ID BIGINT  NOT NULL , EVENT_ID BIGINT  NOT NULL , CUST_KEY VARCHAR (255) , CUST_VALUE VARCHAR (255)  , CONSTRAINT CC1311340249543 PRIMARY KEY ( ID)  ) ;

CREATE SEQUENCE EVENT_ID MINVALUE 1 START WITH 1 INCREMENT BY 1 CACHE 20;

COMMIT;
