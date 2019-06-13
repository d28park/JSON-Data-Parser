package com.d28park.web.api;

import com.d28park.web.JSONDataParser.SearchLinkedList;
import com.d28park.web.JSONDataParser.SearchNode;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

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


    public static String[] fastMetadataGeneration(InputStream is) throws IOException {
        StringBuffer sb = new StringBuffer();
        String metadata;
        String[] metadata_time = new String[2];
        int objectNest = 0;
        boolean[] arrayStart = new boolean[400];
        Instant start = Instant.now();

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
                        sb.append("], ");
                        arrayStart[objectNest] = false;
                    }
                    break;
                case END_OBJECT:
                    objectNest--;
                    if (arrayStart[objectNest]) {
                        int returnLevel = objectNest;
                        while (!(jt == JsonToken.END_ARRAY && objectNest == returnLevel)) {
                            jt = parser.nextToken();
                            if (jt == JsonToken.START_OBJECT) {
                                objectNest++;
                            } else if (jt == JsonToken.END_OBJECT) {
                                objectNest--;
                            }
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
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        metadata_time[0] = metadata;
        metadata_time[1] = "Generated in: " + timeElapsed + "ms";

        return metadata_time;
    }

    public static SearchNode filterJson(String returnChain) {
        String[] returnParent = returnChain.split("->");
        SearchLinkedList searchList = new SearchLinkedList();

        for (String s : returnParent) {
            searchList.addNode(s);
        }

        return searchList.getHead();
    }

    public static void streamTreeTest(InputStream is) throws IOException {
        Instant start = Instant.now();

        // Wrapper to handle flat files
        List<InputStream> streams = Arrays.asList(new ByteArrayInputStream("{\"superwrapper\": ".getBytes()), is, new ByteArrayInputStream("}".getBytes()));
        InputStream wrappedIs = new SequenceInputStream(Collections.enumeration(streams));

        JsonFactory factory = new JsonFactory();
        ObjectMapper mapper = new ObjectMapper(factory);
        JsonNode rootNode = mapper.readTree(wrappedIs);
        String testString = "tests->friends->id";

        searchTree(rootNode, filterJson(testString));

        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();

        System.out.println("Filtered in: " + timeElapsed + "ms");
    }

/*    public static void recurseTree(JsonNode node) {
        Iterator<Map.Entry<String, JsonNode>> fieldsIterator = node.fields();
        while (fieldsIterator.hasNext()) {
            Map.Entry<String, JsonNode> field = fieldsIterator.next();
            // System.out.println("Key: " + field.getKey());
            String s = field.getKey();
            JsonNode jn = field.getValue();

            if (jn.isArray()) {
                for (JsonNode n : jn) {
                    recurseTree(n);
                }
            } else if (jn.isObject()) {
                recurseTree(jn);
            } else {
                // System.out.println("Value: " + jn);
            }
        }
    }*/

    public static void searchTree(JsonNode node, SearchNode sn) {
        List<JsonNode> jnList = node.findValues(sn.getSearchKey());
        SearchNode nextSn = sn.getNext();
        if (!jnList.isEmpty()) {
            Iterator<JsonNode> listIterator = jnList.listIterator();
            if (nextSn == null) {
                while (listIterator.hasNext()) {
                    JsonNode jn = listIterator.next();
                    if (jn.intValue() != 1) {
                        listIterator.remove();
                    }
                }
                System.out.println(jnList.size());
                return;
            }
            while (listIterator.hasNext()) {
                JsonNode jn = listIterator.next();
                if (jn.isArray()) {
                    for (JsonNode njn : jn) {
                        searchTree(njn, nextSn);
                    }
                } else if (jn.isObject()) {
                    searchTree(jn, nextSn);
                }
            }
        }
    }
}