package com.lectorl.util.excel;

import com.lectorl.util.excel.document.TabularDocument;
import com.lectorl.util.excel.document.ExcelDocumentBuilder;
import com.lectorl.util.excel.exception.ExcelDocumentCreationException;
import com.lectorl.util.excel.exception.ModelNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * Created by Reza Mousavi reza.mousavi@lector.dk on 7/7/2016
 */
public class Configuration {

    private static final Log logger = LogFactory.getLog(Configuration.class);

    private Map<Class, TabularDocument> excelDocuments;
    private DocumentManipulator documentManipulator;

    public Configuration() {
        this.excelDocuments = new HashMap<>();
    }

    public DocumentManipulator getDocumentManipulator() {
        return documentManipulator;
    }

    public Configuration setDocumentManipulator(DocumentManipulator documentManipulator) {
        this.documentManipulator = documentManipulator;
        return this;
    }

    public Configuration addModel(Set<Class<?>> classes) {
        classes.stream()
                .forEach(this::addModel);
        return this;
    }

    public <T> Configuration addModel(Class<T> clazz) {
        final TabularDocument tabularDocument = new ExcelDocumentBuilder<T>()
                .setClass(clazz)
                .build();
        logger.info("Adding excel model for class : " + clazz);
        excelDocuments.put(clazz, tabularDocument);
        return this;
    }

    public <T> TabularDocument<T> lookupForDocument(Class<T> clazz) {
        final Optional<TabularDocument> document = Optional.ofNullable(excelDocuments.get(clazz));
        document.ifPresent(e -> logger.debug("Excel document found for class : " + clazz));
        return document.orElseThrow(() -> new ModelNotFoundException("Cannot find any model for given class : " + clazz));
    }

    public <T> List<T> read(Class<T> resultClazz, InputStream inputStream) throws ExcelDocumentCreationException {
        logger.debug("Reading for class type : " + resultClazz);
        final TabularDocument<T> tabularDocument = lookupForDocument(resultClazz);
        return documentManipulator.read(tabularDocument, inputStream);
    }

    public <T> void write(Class<T> clazz, boolean createHeader, List<T> elements, OutputStream outputStream) {
        logger.debug("Writing for class type : " + clazz);
        final TabularDocument<T> tabularDocument = lookupForDocument(clazz);
        documentManipulator.write(tabularDocument, createHeader, elements, outputStream);
    }

}