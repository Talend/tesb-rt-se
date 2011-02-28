create table EVENTS (
	ID varchar(30), 
	EI_TIMESTAMP timestamp,
	EI_EVENT_TYPE varchar(30),
	ORIG_PROCESS_ID varchar(30),
	ORIG_IP varchar(30),
	ORIG_HOSTNAME varchar(30),
	ORIG_CUSTOM_ID varchar(30),
	MI_MESSAGE_ID varchar(30),
	MI_FLOW_ID varchar(30),
	MI_PORT_TYPE varchar(100),
	MI_OPERATION_NAME varchar(100),
	MI_TRANSPORT_TYPE varchar(100),
	MESSAGE_CONTENT clob,
	EVENT_EXTENSION clob
);