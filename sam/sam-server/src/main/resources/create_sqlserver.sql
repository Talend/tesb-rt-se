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
CREATE TABLE [dbo].[EVENTS]([ID] [bigint] NOT NULL, [EI_TIMESTAMP] [datetime] NULL, [EI_EVENT_TYPE] [nvarchar](255) NULL, [ORIG_CUSTOM_ID] [nvarchar](255) NULL, [ORIG_PROCESS_ID] [nvarchar](255) NULL, [ORIG_HOSTNAME] [nvarchar](128) NULL, [ORIG_IP] [nvarchar](64) NULL, [ORIG_PRINCIPAL] [nvarchar](255) NULL, [MI_PORT_TYPE] [nvarchar](255) NULL, [MI_OPERATION_NAME] [nvarchar](255) NULL, [MI_MESSAGE_ID] [nvarchar](255) NULL, [MI_FLOW_ID] [nvarchar](64) NULL, [MI_TRANSPORT_TYPE] [nvarchar](255) NULL, [MESSAGE_CONTENT] [ntext] NULL, [CONTENT_CUT] [bit] NULL, PRIMARY KEY CLUSTERED ([ID] ASC)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY];

CREATE TABLE [dbo].[EVENTS_CUSTOMINFO]([ID] [bigint] NOT NULL,[EVENT_ID] [bigint] NOT NULL,[CUST_KEY] [nvarchar](255) NULL,[CUST_VALUE] [nvarchar](255) NULL,PRIMARY KEY CLUSTERED ([ID] ASC,[EVENT_ID] ASC)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]) ON [PRIMARY];

CREATE TABLE [dbo].[SEQUENCE]([SEQ_NAME] [nvarchar](50) NOT NULL,[SEQ_COUNT] [decimal] (38, 0) IDENTITY(1,1) NOT NULL, PRIMARY KEY CLUSTERED ( [SEQ_COUNT] ASC )WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY] ) ON [PRIMARY];

ALTER TABLE [dbo].[SEQUENCE] ADD  CONSTRAINT [DF_SEQUENCE_SEQ_NAME]  DEFAULT (N'EVENT_SEQ') FOR [SEQ_NAME];

-- INSERT INTO SEQUENCE(SEQ_NAME, SEQ_COUNT) values ('EVENT_SEQ', 0);
-- INSERT INTO SEQUENCE(SEQ_NAME) values ('EVENT_SEQ');

COMMIT;
