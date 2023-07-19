package com.samp_products_d;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLUtil {

	public static Document getDocument() throws Exception {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		return documentBuilder.newDocument();
	}

	public static Document createDocument(String elementName) throws ParserConfigurationException {
		DocumentBuilder dbdr = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = dbdr.newDocument();
		Element ele = doc.createElement(elementName);
		doc.appendChild(ele);
		return doc;
	}

	public static Element createChildElement(Element parentElement, String childName) {
		Element child = null;
		if (parentElement != null && (!"".equals(childName))) {
			child = parentElement.getOwnerDocument().createElement(childName);
			parentElement.appendChild(child);
		}
		return child;
	}

	public static Element getChildElement(Element element, String tagName) throws Exception {

		return getChildElement(element, tagName, true);

	}

	public static Element getChildElement(Element element, String tagName, boolean create) throws Exception {
		Node node = null;
		NodeList nodeList = element.getChildNodes();
		Element childElm = null;
		for (int i = 0; i < nodeList.getLength(); i++) {
			node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE && tagName.equals(node.getNodeName())) {
				childElm = (Element) node;
				break;
			}
		}

		if ((childElm == null) && (create)) {
			childElm = createChildElement(element, tagName);
		}

		return childElm;
	}

	public static List<Element> getChildElements(Element element, String tagName) throws Exception {
		Node node = null;
		List<Element> elementList = new ArrayList();
		NodeList nodeList = element.getChildNodes();
		Element childElm = null;
		for (int i = 0; i < nodeList.getLength(); i++) {
			node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE && tagName.equals(node.getNodeName())) {
				childElm = (Element) node;
				elementList.add(childElm);
			}
		}

		return elementList;
	}

}
