
CREATE TABLE IF NOT EXISTS shipments
(
    shipment_id bigint NOT NULL,
    order_id bigint NOT NULL,
    date_created character varying(255) COLLATE pg_catalog."default",
    status character varying(25) COLLATE pg_catalog."default",
    CONSTRAINT shipments_pkey PRIMARY KEY (shipment_id)
);


INSERT INTO shipments values (30500,10500,'2021-01-21','COMPLETED');
INSERT INTO shipments values (31500,11500,'2021-04-21','COMPLETED');
INSERT INTO shipments values (32500,12500,'2021-05-31','PROCESSING');

--alter table shipments replica identity full

--For a consumer to be able to process a delete event generated for a table that does not have a primary key, set the table’s REPLICA IDENTITY to FULL.
--When a table does not have a primary key and the table’s REPLICA IDENTITY is set to DEFAULT or NOTHING, a delete event has no before field.