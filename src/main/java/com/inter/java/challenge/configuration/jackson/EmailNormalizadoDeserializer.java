package com.inter.java.challenge.configuration.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

import static com.inter.java.challenge.utils.NormalizadorEmail.normalizar;

public final class EmailNormalizadoDeserializer
        extends StdDeserializer<String> {

    public EmailNormalizadoDeserializer() {
        super(String.class);
    }

    @Override
    public String deserialize(
            JsonParser parser,
            DeserializationContext context
    ) throws IOException {
        return normalizar(parser.getValueAsString());
    }
}
