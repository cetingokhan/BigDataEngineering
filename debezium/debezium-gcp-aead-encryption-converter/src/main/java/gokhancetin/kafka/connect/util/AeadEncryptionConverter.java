package gokhancetin.kafka.connect.util;

import com.google.crypto.tink.Aead;
import com.google.crypto.tink.CleartextKeysetHandle;
import com.google.crypto.tink.JsonKeysetReader;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.aead.AeadConfig;
import io.debezium.spi.converter.CustomConverter;
import io.debezium.spi.converter.RelationalColumn;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.connect.data.SchemaBuilder;

public class AeadEncryptionConverter implements CustomConverter<SchemaBuilder, RelationalColumn> {

    public static final String DEFAULT_COLUMN_NAME = "";
    public String strColumnNames;
    public String strAssociatedData;
    public String strAeadKeyFilePath;
    public byte[] associatedData = null;
    public Boolean debug;
    static KeysetHandle handle = null;
    public Aead aead = null;
    public List<String> columnNames = null;

    boolean isBlankString(String string) {
        return string == null || string.trim().isEmpty();
    }

    @Override
    public void configure(Properties props) {

        this.strColumnNames = props.getProperty("format.columnNames", DEFAULT_COLUMN_NAME);
        this.strAssociatedData = props.getProperty("format.associatedData", "sample_associated_data");
        this.strAeadKeyFilePath = props.getProperty("format.aeadKeyFilePath", "sample_associated_data");
        this.debug = props.getProperty("debug", "false").equals("true");

        if (!isBlankString(this.strColumnNames))
            this.columnNames = Arrays.asList(strColumnNames.split(","));


        try {
            AeadConfig.register();
        } catch (GeneralSecurityException e) {
            System.err.println("AeadConfig.register(), got error: " + e);
        }
        this.associatedData = this.strAssociatedData.getBytes(StandardCharsets.UTF_8);

        try {
            handle = CleartextKeysetHandle.read(JsonKeysetReader.withFile(new File(this.strAeadKeyFilePath)));
        } catch (GeneralSecurityException | IOException ex) {
            System.err.println("Cannot read keyset, got error: " + ex);
        }

        try {
            aead = handle.getPrimitive(Aead.class);
        } catch (GeneralSecurityException ex) {
            System.err.println("Cannot create primitive, got error: " + ex);
        }

        if(debug)
            System.out.printf(
                    "[AeadEncryptionConverter.configure] Finished configuring formats. FormatColumnNames: %s", this.strColumnNames);
    }

    @Override
    public void converterFor(RelationalColumn column, ConverterRegistration<SchemaBuilder> registration) {
        if (debug) {
            System.out.printf(
                    "[AeadEncryptionConverter.converterFor] Starting to register column. column.name: %s, column.typeName: %s, column.hasDefaultValue: %s, column.defaultValue: %s, column.isOptional: %s%n",
                    column.name(), column.typeName(), column.hasDefaultValue(), column.defaultValue(), column.isOptional());
        }

        if (this.columnNames.contains(column.name())) {

            if (debug) {
                System.out.printf(
                        "[AeadEncryptionConverter.converterFor] %s column will be encrypt", column.name());
            }

            registration.register(SchemaBuilder.string().optional(), rawValue -> {
                if (debug)
                    System.out.printf("[TimestampConverter.converterFor] %s column value: %s",column.name(), rawValue.toString());

                try {
                    byte[] plaintext = rawValue.toString().getBytes(StandardCharsets.UTF_8);
                    byte[] ciphertext = aead.encrypt(plaintext, this.associatedData);

                    String strEncodedChiperText = Base64.getEncoder().encodeToString(ciphertext);
                    if (debug) {
                        System.out.println("Encyrepted Value:");
                        System.out.println(strEncodedChiperText);
                    }
                    return strEncodedChiperText;
                } catch (Exception ex) {
                    System.err.println("Error while encryption:" + ex);
                }

                return null;
            });
        }

    }
}
