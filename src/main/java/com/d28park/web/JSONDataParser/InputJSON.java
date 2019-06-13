package com.d28park.web.JSONDataParser;

import com.d28park.web.api.MetadataGenerator;

import java.io.*;
import java.net.URL;

public class InputJSON implements Serializable {
    private String input;
    private String url;
    private String filename;
    private Boolean fastMode;

    public InputJSON() {
        this.input = "";
        this.url = "";
        this.filename = "";
        this.fastMode = false;
    }

    public InputJSON(String input, String url, String filename, Boolean fastMode) {
        this.input = input;
        this.url = url;
        this.filename = filename;
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

    public String[] generateMetadata() throws IOException {
        String metadata[] = new String[2];
        InputStream is = getIs(this.input, this.url, this.filename);

        if (this.fastMode) {
            metadata = MetadataGenerator.fastMetadataGeneration(is);
        }

        return metadata;
    }

    public void generateMap() throws IOException {
        InputStream is = getIs(this.input, this.url, this.filename);

        if (this.fastMode) {
            MetadataGenerator.streamTreeTest(is);
        }
    }
}