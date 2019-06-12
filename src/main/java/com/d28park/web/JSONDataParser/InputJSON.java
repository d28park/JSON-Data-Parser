package com.d28park.web.JSONDataParser;

import com.d28park.web.api.MetadataGenerator;

import java.io.*;
import java.net.URL;

public class InputJSON implements Serializable {
    private String input;
    private String url;
    private String filename;

    public InputJSON() {
        this.input = "";
        this.url = "";
        this.filename = "";
    }

    public InputJSON(String input, String url, String filename) {
        this.input = input;
        this.url = url;
        this.filename = filename;
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

    public void setInput(String input) {
        this.input = input;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String generateMetadata() throws IOException {
        String metadata = "";
        StringBuffer sb = new StringBuffer();

        /*if (this.input != "") {

        } else*/
        if (this.url != "") {
            URL url = new URL(this.url);
            try (InputStream is = url.openStream()) {
                // metadata = MetadataGenerator.getJsonTokens(is);
                metadata = MetadataGenerator.fastMetadataGeneration(is);
            }
        } else if (this.filename != "") {
            File f = new File("src/main/resources/" + this.filename);
            try (InputStream is = new FileInputStream(f)) {
                metadata = MetadataGenerator.getJsonTokens(is);
            }
        }

        return metadata;
    }
}