# Debezium Google Tink Aead Encryption Converter
This project created for encrypt column values with Google Tink Aead Encryption library on debezium connector and It referenced from https://debezium.io/documentation/reference/stable/development/converters.html


## Deploy
Firstly you have to create jar file and then move jar file into connector directory or directories. For example;`/kafka/connect/debezium-connector-postgres/AeadEncryptionConverter.jar`

## Configuration

Give some name for this converter and user your converter name prefixes:
Sample Kafka connector create request body;
```json
{
  "name": "customer-connector1",
  "config": {
    "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
    "plugin.name": "pgoutput",
    "database.hostname": "postgres",
    "database.port": "5432",
    "database.user": "postgresuser",
    "database.password": "postgrespw",
    "database.dbname": "customer_db",
    "database.server.name": "postgres",
    "table.include.list": "public.customers",
    "converters": "aeadEncryptionConverter",
    "aeadEncryptionConverter.type": "gokhancetin.kafka.connect.util.AeadEncryptionConverter",
    "aeadEncryptionConverter.format.columnNames": "username,surname,age",
    "aeadEncryptionConverter.format.associatedData": "dfjhkgkdfhgdk",
    "aeadEncryptionConverter.format.aeadKeyFilePath": "/kafka/connect/debezium-connector-postgres/key_file.json",
    "aeadEncryptionConverter.debug": "true"
  }
}
```