package com.lector.util.tabular.document;

import com.lector.util.tabular.annotation.Row;
import com.lector.util.tabular.exception.NoModelException;
import com.lector.util.tabular.util.AnnotationUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by Reza Mousavi reza.mousavi@lector.dk on 7/5/2016
 */
public class TabularDocumentBuilder<T> {

    private static final Log logger = LogFactory.getLog(TabularDocumentBuilder.class);

    private Class<T> clazz;

    public TabularDocumentBuilder setClass(Class<T> clazz) {
        this.clazz = clazz;
        return this;
    }

    public TabularDocument<T> build() {
        if (!clazz.isAnnotationPresent(Row.class)) {
            throw new NoModelException(
                    "Class : " + clazz.getName() + " is not a valid model. " +
                            "It should have the annotation <Row> over it.");
        }

        final TabularDocument<T> tabularDocument = AnnotationUtil.getTabularDocument(clazz);
        AnnotationUtil.getTabularFields(clazz).forEach(tabularDocument::addField);
        logger.debug("------------All fields has been read -----------.");
        return tabularDocument;
    }

}
