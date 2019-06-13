package com.d28park.web.JSONDataParser;

public class SearchLinkedList {
    SearchNode head;
    int count;

    public SearchLinkedList() {
        this.head = null;
        this.count = 0;
    }

    public SearchLinkedList(SearchNode head) {
        this.head = head;
        this.count = 1;
    }

    public SearchNode getHead() {
        return this.head;
    }

    public void addNode(String searchKey) {
        SearchNode current = head;
        SearchNode temp = new SearchNode(searchKey);

        if (current == null) {
            this.head = temp;
            count++;
            return;
        }

        while (current.getNext() != null) {
            current = current.getNext();
        }
        current.setNext(temp);
        count++;
    }

    public String get(int nodeNum) {
        if (nodeNum <= 0) {
            return "";
        }
        SearchNode current = head;
        for(int i = 1; i < nodeNum; i++) {
            current = current.getNext();
        }

        return current.getSearchKey();
    }
}
