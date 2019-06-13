package com.d28park.web.JSONDataParser;

public class SearchNode {
    String searchKey;
    SearchNode next;

    public SearchNode(String searchKey) {
        this.searchKey = searchKey;
        next = null;
    }

    public SearchNode(String searchKey, SearchNode next) {
        this.searchKey = searchKey;
        next = next;
    }

    public String getSearchKey() {
        return this.searchKey;
    }

    public SearchNode getNext() {
        return this.next;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public void setNext(SearchNode next) {
        this.next = next;
    }
}
