package com.d28park.web.JSONDataParser;

public class SearchNode {
    String searchKey;
    String returnKey;
    String value;
    String operation;
    char UorI;
    SearchNode next;

    public SearchNode(String searchKey, String returnKey, String value, String operation, char UorI) {
        this.searchKey = searchKey;
        this.returnKey = returnKey;
        this.value = value;
        this.operation = operation;
        this.UorI = UorI;
        next = null;
    }

    public SearchNode(String searchKey, String returnKey, String value, String operation, char UorI, SearchNode next) {
        this.searchKey = searchKey;
        this.returnKey = returnKey;
        this.value = value;
        this.operation = operation;
        this.UorI = UorI;
        next = next;
    }

    public String getSearchKey() {
        return this.searchKey;
    }
    public String getReturnKey() {
        return this.returnKey;
    }
    public String getValue() {
        return this.value;
    }
    public String getOperation() {
        return this.operation;
    }
    public char getUorI() {
        return this.UorI;
    }
    public SearchNode getNext() {
        return this.next;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }
    public void setReturnKey(String returnKey) {
        this.returnKey = returnKey;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public void setOperation(String operation) {
        this.operation= operation;
    }
    public void setUorI(char UorI) {
        this.UorI= UorI;
    }
    public void setNext(SearchNode next) {
        this.next = next;
    }
}
