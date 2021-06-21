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
CREATE INDEX IF NOT EXISTS EVENTS_FLOW_ID ON EVENTS (MI_FLOW_ID);
CREATE INDEX IF NOT EXISTS EVENTS_FLOW_TIMESTAMP ON EVENTS (MI_FLOW_ID, EI_TIMESTAMP DESC);
CREATE INDEX IF NOT EXISTS EVENTS_MI_PORT_TYPE ON EVENTS (MI_PORT_TYPE);
CREATE INDEX IF NOT EXISTS EVENTS_CUSTOMINFO_EVENT_ID ON EVENTS_CUSTOMINFO(EVENT_ID);

CREATE TABLE FLOWS (
ID VARCHAR(64) NOT NULL,
FI_TIMESTAMP TIMESTAMP,
PRIMARY KEY (ID));

CREATE INDEX IF NOT EXISTS FLOWS_TIMESTAMP ON FLOWS (FI_TIMESTAMP DESC);

INSERT INTO FLOWS (ID,FI_TIMESTAMP)
SELECT MI_FLOW_ID, MAX(EI_TIMESTAMP) FROM EVENTS WHERE (MI_FLOW_ID is not null) GROUP BY MI_FLOW_ID;
COMMIT;
