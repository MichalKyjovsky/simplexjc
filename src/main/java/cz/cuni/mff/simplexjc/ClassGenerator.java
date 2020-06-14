package cz.cuni.mff.simplexjc;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.*;
import java.nio.MappedByteBuffer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class ClassGenerator {
    private static StringBuilder outClass = new StringBuilder();
    private static int indentLevel = 0;
    private static final int TAB_SIZE = 4;

    public static String generateClass(final InputStream is) {
        try {
            DocumentBuilderFactory factory =  DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            Document document = documentBuilder.parse(is);

            Element root = document.getDocumentElement();
            NodeList nl = root.getChildNodes();
            prepareHeader(root);
            indentLevel++;
            processChildren(nl);
            indentLevel--;
            outClass.append(System.lineSeparator())
                    .append(System.lineSeparator())
                    .append("}");
            BufferedWriter bw = new BufferedWriter(new FileWriter("simple.out"));
            bw.write(outClass.toString());
            bw.close();

        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
        return outClass.toString();
    }

    private static void processChildren (NodeList nl) {
        Element currentNode;

        for (int i = 0; i < nl.getLength(); i++) {
            if (nl.item(i).getNodeType() == Node.TEXT_NODE) {
                continue;
            }
            currentNode = (Element) nl.item(i);
            if (currentNode.getNodeName().equals("attribute")) {
                createAttribute(currentNode);
            } else if (currentNode.getNodeName().equals("method")) {
                processMethods(currentNode);
            }
        }
    }

    private static void processMethods(Element method) {
        StringBuilder content = new StringBuilder();
        boolean inParam = false;

        outClass.append(String.format("%1$" + (indentLevel * TAB_SIZE) + "s", ""));

        if (method.hasAttribute("access")) {
            outClass.append(method.getAttribute("access"))
                    .append(" ");
        }
        outClass.append(method.getAttribute("returnType"))
                .append(" ")
                .append(method.getAttribute("name"))
                .append("(");
        for (int i = 0; i < method.getChildNodes().getLength(); i++) {

            if (method.getChildNodes().item(i).getNodeName().equals("parameter")) {
                if (!inParam) {
                    addMethodParameters((Element) method.getChildNodes().item(i));
                    inParam = true;
                } else {
                    outClass.append(", ");
                }
            } else if (method.getChildNodes().item(i).getNodeName().equals("content")) {
                indentLevel++;
                Element cont = (Element) method.getChildNodes().item(i);
                content.append(String.format("%1$" + (indentLevel * TAB_SIZE) + "s", ""))
                        .append(cont.getTextContent().trim())
                        .append(System.lineSeparator());
                indentLevel--;
            }
        }
        outClass.append(")")
                .append(" {")
                .append(System.lineSeparator())
                .append(content.toString())
                .append(String.format("%1$" + (indentLevel * TAB_SIZE) + "s", ""))
                .append("}");

    }

    private static void addMethodParameters(Element param) {
        outClass.append(param.getAttribute("type"))
                .append(" ")
                .append(param.getAttribute("name"));
    }

    private static void createAttribute(Element attr) {
        outClass.append(String.format("%1$" + (indentLevel * TAB_SIZE) + "s", ""));

        if (attr.hasAttribute("access")) {
            outClass.append(attr.getAttribute("access"))
            .append(" ");
        }
        outClass.append(attr.getAttribute("type"))
                .append(" ")
                .append(attr.getAttribute("name"));
        if (attr.hasAttribute("value")) {
            outClass.append(" = ")
                    .append(attr.getAttribute("value"));
        }
        outClass.append(";")
                .append(System.lineSeparator())
                .append(System.lineSeparator());
    }

    private static void prepareHeader(Element root) {
        outClass.append("package ")
                .append(root.getAttribute("package"))
                .append(";")
                .append(System.lineSeparator())
                .append(System.lineSeparator());
        outClass.append("class ")
                .append(root.getAttribute("name"));
        if (root.hasAttribute("superclass")) {
            outClass.append(" extends ")
                    .append(root.getAttribute("superclass"));
        }

        if (root.hasAttribute("interfaces")) {
            outClass.append(" implements ")
                    .append(root.getAttribute("interfaces"));
        }
        outClass.append(" {")
                .append(System.lineSeparator())
                .append(System.lineSeparator());
    }

}
