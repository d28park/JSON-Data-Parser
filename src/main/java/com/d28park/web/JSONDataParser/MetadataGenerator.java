package com.d28park.web.JSONDataParser;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;
import java.io.InputStream;

// Will be marshaled to JSON with Jackson library
// Watch out for same keys, mixed types, mixture of objects and primitives
public class MetadataGenerator {

    private final long id;
    private final String content;

    public MetadataGenerator(long id, String content) {
        this.id = id;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

/*  START_OBJECT
    END_OBJECT
    START_ARRAY
    END_ARRAY
    FIELD_NAME
    VALUE_EMBEDDED_OBJECT
    VALUE_FALSE
    VALUE_TRUE
    VALUE_NULL
    VALUE_STRING
    VALUE_NUMBER_INT
    VALUE_NUMBER_FLOAT*/

    public static String getJsonTokens(InputStream is) throws IOException {
        StringBuffer sb = new StringBuffer();
        String tokens;

        JsonFactory jsonFactory = new JsonFactory();
        JsonParser parser = jsonFactory.createParser(is);
        while (!parser.isClosed()) {
            JsonToken jt = parser.nextToken();
            sb.append("/");
            sb.append(jt);
        }

        tokens = sb.toString();

        return tokens;
    }

/*    public void streamTreeTest() throws IOException {
        // URL Input
        // URL url = new URL("https://demo.omegasys.eu/ps/ips/getWinnerList?domain=demo.omegasys.eu&period=10&size=15");
        //InputStream is = url.openStream();

        // File Input
        File initialFile = new File("src/main/resources/sample.txt");
        InputStream is = new FileInputStream(initialFile);

        JsonFactory factory = new JsonFactory();
        ObjectMapper mapper = new ObjectMapper(factory);
        JsonNode rootNode = mapper.readTree(is);

        Iterator<Map.Entry<String, JsonNode>> fieldsIterator = rootNode.fields();
        while (fieldsIterator.hasNext()) {
            Map.Entry<String, JsonNode> field = fieldsIterator.next();
            System.out.println("Key: " + field.getKey() + "\tValue: " + field.getValue());
        }
    }*/
}
