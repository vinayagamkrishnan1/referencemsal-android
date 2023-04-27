package com.eygsl.cbs.referencemsal.models;

public class DocumentModel {

    String id;
    String docmentName;
    String documentSize;
    String webUrl;
    String downloadUrl;
    String localFilePath;

    public DocumentModel(String id, String docmentName, String docSize, String webUrl, String downloadUrl, String localFilePath) {
        this.id = id;
        this.docmentName = docmentName;
        this.documentSize = docSize;
        this.webUrl = webUrl;
        this.downloadUrl = downloadUrl;
        this.localFilePath = localFilePath;
    }

    public String getDocumentName() {
        return docmentName;
    }

    public String getDocumentsize() {
        return documentSize;
    }

    public String getFileDownloadURL() {
        return downloadUrl;
    }

    public String getLocalFilePath() {
        return localFilePath;
    }

    public void setLocalFilePath(String filepath) {
        this.localFilePath = filepath;
    }

}