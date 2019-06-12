package com.d28park.web.api;

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

    public static String fastMetadataGeneration(InputStream is) throws IOException {
        StringBuffer sb = new StringBuffer();
        String metadata;
        int objectNest = 0;
        boolean[] arrayStart = new boolean[400];

        JsonFactory jsonFactory = new JsonFactory();
        JsonParser parser = jsonFactory.createParser(is);
        while (!parser.isClosed()) {
            JsonToken jt = parser.nextToken();
            if (jt == null) {
                break;
            }
            switch (jt) {
                case START_OBJECT:
                    objectNest++;
                    sb.append("{");
                    parser.nextToken();
                    sb.append("\"" + parser.getText() + "\":");
                    break;
                case FIELD_NAME:
                    sb.append("\"" + parser.getText() + "\":");
                    break;
                case START_ARRAY:
                    arrayStart[objectNest] = true;
                    sb.append("[");
                    jt = parser.nextToken();
                    if (jt == JsonToken.START_OBJECT) {
                        objectNest++;
                        sb.append("{");
                    } else if (jt == JsonToken.END_ARRAY) {
                        sb.append("]");
                        arrayStart[objectNest] = false;
                    } else {
                        sb.append(jt);
                        while (parser.nextToken() != JsonToken.END_ARRAY) {
                            // Do nothing
                        }
                        sb.append("]");
                        arrayStart[objectNest] = false;
                    }
                    break;
                case END_OBJECT:
                    objectNest--;
                    if (arrayStart[objectNest]) {
                        while (parser.nextToken() != JsonToken.END_ARRAY) {
                            // Do nothing
                        }
                        arrayStart[objectNest] = false;
                        sb.replace(sb.length() - 2, sb.length(), "}]");
                        if (objectNest > 0) {
                            sb.append(", ");
                        }
                    } else if (objectNest == 0) {
                        sb.replace(sb.length() - 2, sb.length(), "}");
                    }
                    break;
                default:
                    sb.append(jt + ", ");
                    break;
            }
        }

        metadata = sb.toString();
        return metadata;
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
