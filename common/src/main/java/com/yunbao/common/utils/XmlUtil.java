package com.yunbao.common.utils;

import org.w3c.dom.Document;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class XmlUtil {

    public static Document loadNode(InputStream inputStream){
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            return  builder.parse(inputStream);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }




}
