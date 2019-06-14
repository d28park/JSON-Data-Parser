package com.d28park.web.JSONDataParser;

import com.d28park.web.api.MetadataGenerator;

import java.io.*;
import java.net.URL;

public class JSONToolModel implements Serializable {
    private String input;
    private String url;
    private String filename;
    private String queryString;
    private String metadata;
    private String elapsedTimeMetadata;
    private String queryResult;
    private String elapsedTimeQuery;
    private Boolean fastMode;

    public JSONToolModel() {
        this.input = null;
        this.url = null;
        this.filename = null;
        this.queryString = null;
        this.metadata = null;
        this.elapsedTimeMetadata = null;
        this.queryResult = null;
        this.elapsedTimeQuery = null;
        this.fastMode = false;
    }

    public JSONToolModel(String input, String url, String filename, String queryString, String metadata, String elapsedTimeMetadata,
                         String queryResult, String elapsedTimeQuery, Boolean fastMode) {
        this.input = input;
        this.url = url;
        this.filename = filename;
        this.queryString = queryString;
        this.metadata = metadata;
        this.elapsedTimeMetadata = elapsedTimeMetadata;
        this.queryResult = queryResult;
        this.elapsedTimeQuery = elapsedTimeQuery;
        this.fastMode = fastMode;
    }

    public String getInput() {
        return this.input;
    }
    public String getUrl() {
        return this.url;
    }
    public String getFilename() {
        return this.filename;
    }
    public String getQueryString() {
        return this.queryString;
    }
    public String getMetadata() {
        return this.metadata;
    }
    public String getElapsedTimeMetadata() {
        return this.elapsedTimeMetadata;
    }
    public String getQueryResult() {
        return this.queryResult;
    }
    public String getElapsedTimeQuery() {
        return this.elapsedTimeQuery;
    }
    public boolean getFastMode() {
        return this.fastMode;
    }

    public void setInput(String input) {
        this.input = input;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }
    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
    public void setElapsedTimeMetadata(String elapsedTimeMetadata) {
        this.elapsedTimeMetadata= elapsedTimeMetadata;
    }
    public void setQueryResult(String queryResult) {
        this.queryResult= queryResult;
    }
    public void setElapsedTimeQuery(String elapsedTimeQuery) {
        this.elapsedTimeQuery= elapsedTimeQuery;
    }
    public void setFastMode(boolean fastMode) {
        this.fastMode = fastMode;
    }

    public InputStream textArea2InputStream(String string) {
        return new InputStream() {
            String s = string;
            int inPtr = 0;

            public int read() {
                if (inPtr >= s.length()) {
                    return -1;
                } else {
                    inPtr++; return s.charAt(inPtr - 1);
                }
            }
        };
    }

    public InputStream getIs(String input, String url, String file) throws IOException {
        if (this.input != "") {
            InputStream is = textArea2InputStream(input);
            return is;
        } else if (this.url != "") {
            URL url_obj = new URL(url);
            InputStream is = url_obj.openStream();
            return is;
        } else if (this.filename != "") {
            File f = new File("src/main/resources/" + file);
            InputStream is = new FileInputStream(f);
            return is;
        }

        return null;
    }

    public void generateMetadata() throws IOException {
        String[] metadata;
        InputStream is = getIs(this.input, this.url, this.filename);

        if (this.fastMode) {
            metadata = MetadataGenerator.generateFastMetadata(is);
        } else {
            metadata = MetadataGenerator.generateMetadata(is);
        }

        this.metadata = metadata[0];
        this.elapsedTimeMetadata = metadata[1];
    }

    public void generateQueryResults() throws IOException {
        InputStream is = getIs(this.input, this.url, this.filename);
        String[] queryResults;

        queryResults = MetadataGenerator.query(is, this.queryString);

        this.queryResult = queryResults[0];
        this.elapsedTimeQuery = queryResults[1];
    }
}