package no.hauglum.flightlog.service;

import org.jsoup.nodes.Document;

public class DocumentWrapper {
    private Document mDocument;
    private int mYear;

    public DocumentWrapper(Document document, int year) {
        mDocument = document;
        mYear = year;
    }

    public DocumentWrapper(Document document) {
        mDocument = document;
    }

    public Document getDocument() {
        return mDocument;
    }

    public void setDocument(Document document) {
        mDocument = document;
    }

    public int getYear() {
        return mYear;
    }

    public void setYear(int year) {
        mYear = year;
    }
}
