select 
(select cust_value from events_custominfo where events.id = events_custominfo.event_id and cust_key = 'address') as address,
(select cust_value from events_custominfo where events.id = events_custominfo.event_id and cust_key = 'Application Name') as app,
events.*
from events
