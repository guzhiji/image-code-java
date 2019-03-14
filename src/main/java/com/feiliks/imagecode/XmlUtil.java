package com.feiliks.imagecode;

import java.io.File;
import java.io.OutputStream;
import java.io.Writer;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Node;


public class XmlUtil {

    public static void convert(Node node, Result result) throws TransformerException {
        Source src = new DOMSource(node);
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer trans = factory.newTransformer();
        trans.transform(src, result);
    }

    public static void convert(Node node, Writer writer) throws TransformerException {
        convert(node, new StreamResult(writer));
    }

    public static void convert(Node node, OutputStream stream) throws TransformerException {
        convert(node, new StreamResult(stream));
    }

    public static void convert(Node node, File file) throws TransformerException {
        convert(node, new StreamResult(file));
    }

}
