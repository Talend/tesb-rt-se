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
CREATE NONCLUSTERED INDEX [EVENTS_FLOW_ID_idx] ON [dbo].[EVENTS] ([MI_FLOW_ID]) WITH (PAD_INDEX = OFF, DROP_EXISTING = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON);

CREATE NONCLUSTERED INDEX [EVENTS_TIMESPAMP_idx] ON [dbo].[EVENTS] ([EI_TIMESTAMP]) WITH ( PAD_INDEX = OFF, DROP_EXISTING = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON);

CREATE NONCLUSTERED INDEX [EVENTS_FLOW_ID_TIMESTAMP_SORT_idx] ON [dbo].[EVENTS] ([MI_FLOW_ID] ASC, [EI_TIMESTAMP] DESC);

CREATE NONCLUSTERED INDEX [EVENTS_MI_PORT_TYPE_idx] ON [dbo].[EVENTS] ([MI_PORT_TYPE]);

CREATE NONCLUSTERED INDEX [EVENTS_CUSTOMINFO_EVENT_ID_idx] ON [dbo].[EVENTS_CUSTOMINFO] ([EVENT_ID]) WITH ( PAD_INDEX = OFF, DROP_EXISTING = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON);

COMMIT;
