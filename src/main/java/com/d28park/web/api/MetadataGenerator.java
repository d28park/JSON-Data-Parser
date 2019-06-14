package com.d28park.web.api;

import com.d28park.web.JSONDataParser.SearchLinkedList;
import com.d28park.web.JSONDataParser.SearchNode;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

// Will be marshaled to JSON with Jackson library
public class MetadataGenerator {

    private final long id;
    private final String content;
    private static final JsonFactory factory = new JsonFactory();
    private static final ObjectMapper mapper = new ObjectMapper(factory);

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

    public static String[] getPrettyPrintAndElapsedTime(String json, Instant start) throws IOException {
        String[] prettyPrintAndElapsedTime = new String[2];

        Object jsonObj = mapper.readValue(json, Object.class);
        prettyPrintAndElapsedTime[0] = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObj);

        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        prettyPrintAndElapsedTime[1] = "Generated in: " + timeElapsed + "ms";

        return prettyPrintAndElapsedTime;
    }

    public static String[] generateFastMetadata(InputStream is) throws IOException {
        Instant start = Instant.now();
        StringBuffer sb = new StringBuffer();
        String metadata;
        String[] metadata_time = new String[2];
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
                        sb.append("\"" + jt + "\"");
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
                    sb.append("\"" + jt + "\", ");
                    break;
            }
        }

        return getPrettyPrintAndElapsedTime(sb.toString(), start);
    }

    public static String[] generateMetadata(InputStream is) throws IOException {
        Instant start = Instant.now();
        StringBuffer sb = new StringBuffer();
        List<InputStream> streams = Arrays.asList(new ByteArrayInputStream("{\"superwrapper\": ".getBytes()), is, new ByteArrayInputStream("}".getBytes()));
        InputStream wrappedIs = new SequenceInputStream(Collections.enumeration(streams));
        OutputStream baos = new ByteArrayOutputStream();

        JsonNode rootNode = mapper.readTree(wrappedIs);
        HashMap<String, String> resultMap = new HashMap<>();

        searchUniqueTree("", rootNode, resultMap);

        for (Map.Entry<String, String> entry : resultMap.entrySet()) {
            sb.append("Key: " + entry.getKey() + " Value: " + entry.getValue());
            sb.append(System.lineSeparator());
        }

        //final JsonGenerator jgen = new JsonFactory().createGenerator(baos);
        //mapper.writeTree(jgen, rootNode);

        //return getPrettyPrintAndElapsedTime(baos.toString(), start);
        String[] result_timeElapsed = new String[2];
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        result_timeElapsed[0] = sb.toString();
        result_timeElapsed[1] = "Generated in: " + timeElapsed + "ms";
        return result_timeElapsed;
    }

    private static void searchUniqueTree(String currentPath, JsonNode n, HashMap<String, String> hm) {
        Iterator<Map.Entry<String, JsonNode>> iter = n.fields();
        while (iter.hasNext()) {
            Map.Entry<String, JsonNode> entry = iter.next();
            if (entry.getValue().isObject()) {
                String pathPrefix = currentPath.isEmpty() ? "" : currentPath + "-";
                searchUniqueTree(pathPrefix + entry.getKey(), entry.getValue(), hm);
            } else if (entry.getValue().isArray()) {
                String pathPrefix = currentPath.isEmpty() ? "" : currentPath + "-";
                for (int i = 0; i < entry.getValue().size(); i++) {
                    if (entry.getValue().get(i).isObject()) {
                        searchUniqueTree(pathPrefix + entry.getKey() + "COL", entry.getValue().get(i), hm);
                    } else {
                        JsonNodeType jnt;
                        JsonNode jn = entry.getValue().get(i);
                        if (jn.isTextual()) {
                            jnt = JsonNodeType.STRING;
                        } else if (jn.isBoolean()) {
                            jnt = JsonNodeType.BOOLEAN;
                        } else {
                            jnt = JsonNodeType.NUMBER;
                        }
                        hm.put(pathPrefix + entry.getKey() + "COL", hm.getOrDefault(pathPrefix + entry.getKey() + "COL", jnt.toString()));
                    }
                }
            } else if (entry.getValue().isValueNode()) {
                String pathPrefix = currentPath.isEmpty() ? "" : currentPath + "-";
                hm.put(pathPrefix + entry.getKey(), hm.getOrDefault(pathPrefix + entry.getKey(), entry.getValue().getNodeType().toString()));
            }
        }
    }

    public static SearchNode[] generateQueryObject(String searchQuery) {
        String[] searchAndResultArray = searchQuery.split("///");
        if (searchAndResultArray.length > 2) {
            // Error
        }

        int counter = 0;
        String[] returnChainArray = searchAndResultArray[1].split("->");
        String[] searchQueryArray = searchAndResultArray[0].split("&&|\\Q||\\E");
        int queryCount = searchQueryArray.length;
        SearchNode[] searchNodes = new SearchNode[queryCount];

        for (int j = 0; j < queryCount; j++) {
            SearchLinkedList searchList = new SearchLinkedList();
            String s = searchQueryArray[j];
            String[] searchChainArray = s.split("->");

            char UorI;
            String valueOperation = searchChainArray[searchChainArray.length - 1];
            String value = valueOperation.split("\\Q==\\E|\\Q>>\\E|\\Q>=\\E|\\Q<<\\E|\\Q<=\\E|\\Q!=\\E")[1];
            String operation = valueOperation.substring(valueOperation.length() - value.length() - 2, valueOperation.length() - value.length());

            if (searchQuery.charAt(counter) == '&') {
                UorI = 'U';
            } else if (searchQuery.charAt(counter) == '|') {
                UorI = 'I';
            } else {
                UorI = '/';
            }
            counter += s.length() + 1;

            if (returnChainArray.length > searchChainArray.length) {
                // ERROR
            }
            for (int i = 0; i < searchChainArray.length; i++) {
                String field;
                String r;


                if (i == searchChainArray.length - 1) {
                    field = searchChainArray[i].substring(0, valueOperation.length() - value.length() - 2);
                } else {
                    field = searchChainArray[i];
                }
                if (i < returnChainArray.length) {
                    r = returnChainArray[i];
                } else {
                    r = null;
                }

                searchList.addNode(field, r, value, operation, UorI);
            }

            searchNodes[j] = searchList.getHead();
        }

        return searchNodes;
    }

    public static List<JsonNode> generateQueryResults(InputStream is, String queryString) throws IOException {
        // Wrapper to handle flat files
        List<InputStream> streams = Arrays.asList(new ByteArrayInputStream("{\"superwrapper\": ".getBytes()), is, new ByteArrayInputStream("}".getBytes()));
        InputStream wrappedIs = new SequenceInputStream(Collections.enumeration(streams));

        JsonNode rootNode = mapper.readTree(wrappedIs);
        SearchNode[] queryObjects = generateQueryObject(queryString);
        List<JsonNode> finalResultNodes = new ArrayList<>();

        List<JsonNode> resultNodes = Collections.synchronizedList(new ArrayList<>());
        searchTree(rootNode, queryObjects[0], false, null, resultNodes);

        finalResultNodes.addAll(resultNodes);

        // Async
        for (int i = 1; i < queryObjects.length; i++) {
            List<JsonNode> additionalNodes = Collections.synchronizedList(new ArrayList<>());
            searchTree(rootNode, queryObjects[i], false, null, additionalNodes);
            if (queryObjects[i].getUorI() == 'U') {
                finalResultNodes.retainAll(additionalNodes);
            } else if (queryObjects[i].getUorI() == 'I') {
                finalResultNodes.addAll(additionalNodes);
            }
        }
        return finalResultNodes;
    }

    public static String[] query(InputStream is, String queryString) throws IOException {
        Instant start = Instant.now();
        StringBuffer sb = new StringBuffer();
        String returnField;

        returnField = queryString.split("///|->")[queryString.split("///|->").length - 1];

        List<JsonNode> resultNodes = generateQueryResults(is, queryString);
        if (resultNodes.size() == 0) {
            return getPrettyPrintAndElapsedTime("[]", start);
        }
        Iterator<JsonNode> listIterator = resultNodes.listIterator();
        if (listIterator.hasNext()) {
            JsonNode jn = listIterator.next();
            if (jn.toString().charAt(0) == '{') {
                sb.append("[" + jn.toString() + ", ");
            } else {
                sb.append("[{\"" + returnField + "\": " + jn.toString() + "}");
                if (listIterator.hasNext()) {
                    sb.append(", ");
                }
            }
        }
        while(listIterator.hasNext()) {
            JsonNode jn = listIterator.next();
            if (jn.toString().charAt(0) == '{') {
                sb.append(jn.toString());
            } else {
                sb.append("{\"" + returnField + "\": " + jn.toString() + "}");
            }
            if (listIterator.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append("]");

        return getPrettyPrintAndElapsedTime(sb.toString(), start);
    }

    public static void searchTree(JsonNode n, SearchNode sn, boolean matched, JsonNode mn, List<JsonNode> rnList) {
        List<JsonNode> jnList = n.findValues(sn.getSearchKey());

        if (!jnList.isEmpty()) {
            Iterator<JsonNode> listIterator = jnList.listIterator();
            // Odd case: returnKey is the same length as searchKey
            if (sn.getNext() == null && sn.getReturnKey() != null) {
                matched = true;
            }

            SearchNode nextSn = sn.getNext();

            // Holds all potential nodes the user would like returned
            if (nextSn != null && nextSn.getReturnKey() == null && !matched) {
                matched = true;
            }

            if (nextSn == null) {
                Object value;
                if (sn.getValue().charAt(0) == '"') {
                    value = sn.getValue().substring(1, sn.getValue().length() - 1);
                } else if (sn.getValue().contains(".")) {
                    value = Float.valueOf(sn.getValue());
                } else if (sn.getValue().equals("true") || sn.getValue() .equals("false")) {
                    value = sn.getValue().equals("true");
                } else if (Long.valueOf(sn.getValue()) > Integer.MAX_VALUE) {
                    value = Long.valueOf(sn.getValue());
                } else {
                    value = Integer.valueOf(sn.getValue());
                }
                while (listIterator.hasNext()) {
                    JsonNode jn = listIterator.next();
                    Object jnObj;
                    //
                    if (mn == null) {
                        mn = n;
                    }

                    if (jn.isTextual()) {
                        jnObj = jn.textValue();
                    } else if (jn.isInt()) {
                        jnObj = jn.intValue();
                    } else if (jn.isFloat()) {
                        jnObj = jn.floatValue();
                    } else if (jn.isLong()) {
                        jnObj = jn.longValue();
                    } else {
                        jnObj = jn.booleanValue();
                    }

                    switch (sn.getOperation()) {
                        case "==":
                            if (jnObj == value) {
                                rnList.add(mn);
                            }
                            break;
/*                        case "<<":
                            if (jnObj < value) {
                                rnList.add(mn);
                            }
                            break;
                        case "<=":
                            if (jnObj <= value) {
                                rnList.add(mn);
                            }
                            break;*/
/*                        case ">>":
                            if ((Integer) jnObj > (Integer) value) {
                                rnList.add(mn);
                            }
                            break;*/
/*                        case ">=":
                            if (jnObj >= value) {
                                rnList.add(mn);
                            }
                            break;
                        case "!=":
                            if (jnObj != value) {
                                rnList.add(mn);
                            }
                            break;*/
                        default:
                            // Error
                    }
                }
                return;
            }

            while (listIterator.hasNext()) {
                JsonNode jn = listIterator.next();
                if (matched && mn == null) {
                    mn = jn;
                }
                if (jn.isArray()) {
                    for (JsonNode njn : jn) {
                        searchTree(njn, nextSn, matched, mn, rnList);
                    }
                } else if (jn.isObject()) {
                    searchTree(jn, nextSn, matched, mn, rnList);
                }
            }
        }
    }
}