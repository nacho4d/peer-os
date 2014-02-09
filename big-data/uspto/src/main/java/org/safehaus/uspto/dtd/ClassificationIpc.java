package org.safehaus.uspto.dtd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Content;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.mongodb.BasicDBObject;

public class ClassificationIpc implements Converter{

	private static final String title = "ClassificationIpc";
	
	protected Logger logger;
	
	private String id;
	private String edition;
	private String mainClassification;
	private Collection<String> furtherClassifications;
	private String text;
	
	public ClassificationIpc(Logger logger) {
		this.logger = logger;
		furtherClassifications = new ArrayList<String>();
	}
	
	public ClassificationIpc(Element element, Logger logger)
	{
		this.logger = logger;
		furtherClassifications = new ArrayList<String>();
		
		NamedNodeMap nodemap = element.getAttributes();
		for (int i=0; i < nodemap.getLength(); i++)
		{
			Node childNode = nodemap.item(i);
			
			if (childNode.getNodeType() == Node.ATTRIBUTE_NODE) {
				Attr attribute = (Attr) childNode;
				if (attribute.getNodeName().equals("id")) {
					id = attribute.getNodeValue();
				}
				else
				{
					logger.warn("Unknown Attribute {} in {} node", attribute.getNodeName(), title);
				}
			}
		}

		NodeList nodeList = element.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) node;
				if (childElement.getNodeName().equals("edition")) {
					edition = childElement.getTextContent();
				}
				else if (childElement.getNodeName().equals("main-classification")) {
					mainClassification = childElement.getTextContent();
				}
				else if (childElement.getNodeName().equals("further-classification")) {
					furtherClassifications.add(childElement.getTextContent());
				}
				else if (childElement.getNodeName().equals("text")) {
					text = childElement.getTextContent();
				}
				else
				{
					logger.warn("Unknown Element {} in {} node", childElement.getNodeName(), title);
				}
			}
			else if (node.getNodeType() == Node.TEXT_NODE) {
				//ignore
			}
			else if (node.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE) {
				//ignore
			}
			else
			{
				logger.warn("Unknown Node {} in {} node", node.getNodeName(), title);
			}
		}

	}

	public ClassificationIpc(org.jdom2.Element element, Logger logger)
	{
		this.logger = logger;
		furtherClassifications = new ArrayList<String>();
		
		List<Attribute> attributes = element.getAttributes();
		for (int i=0; i < attributes.size(); i++)
		{
			Attribute attribute = attributes.get(i);
			if (attribute.getName().equals("id")) {
				id = attribute.getValue();
			}
			else
			{
				logger.warn("Unknown Attribute {} in {} node", attribute.getName(), title);
			}
		}

		List<Content> nodes = element.getContent();
		for (int i=0; i < nodes.size(); i++)
		{
			Content node = nodes.get(i);
			if (node.getCType() == Content.CType.Element) {
				org.jdom2.Element childElement = (org.jdom2.Element) node;
				if (childElement.getName().equals("edition")) {
					edition = childElement.getValue();
				}
				else if (childElement.getName().equals("main-classification")) {
					mainClassification = childElement.getValue();
				}
				else if (childElement.getName().equals("further-classification")) {
					furtherClassifications.add(childElement.getValue());
				}
				else if (childElement.getName().equals("text")) {
					text = childElement.getValue();
				}
				else
				{
					logger.warn("Unknown Element {} in {} node", childElement.getName(), title);
				}
			}
			else if (node.getCType() == Content.CType.Text) {
				//ignore
			}
			else if (node.getCType() == Content.CType.ProcessingInstruction) {
				//ignore
			}
			else
			{
				logger.warn("Unknown Node {} in {} node", node.getCType(), title);
			}
		}

	}


	@Override
	public String toString() {
		StringBuffer toStringBuffer = new StringBuffer(title+":");
		if (id != null)
		{
			toStringBuffer.append(" Id: ");
			toStringBuffer.append(id);
		}
		if (edition != null)
		{
			toStringBuffer.append(" Edition: ");
			toStringBuffer.append(edition);
		}
		if (mainClassification != null)
		{
			toStringBuffer.append(" MainClassification: ");
			toStringBuffer.append(mainClassification);
		}
		if (furtherClassifications != null)
		{
			toStringBuffer.append(" FurtherClassification: ");
			toStringBuffer.append(furtherClassifications);
		}
		if (text != null)
		{
			toStringBuffer.append(" Text: ");
			toStringBuffer.append(text);
		}
		return toStringBuffer.toString();
	}

	public JSONObject toJSon() {
		JSONObject jsonObject = new JSONObject();
		if (id != null)
		{
			jsonObject.put("Id", id);
		}
		if (edition != null)
		{
			jsonObject.put("Edition", edition);
		}
		if (mainClassification != null)
		{
			jsonObject.put("MainClassification", mainClassification);
		}
		if (furtherClassifications != null)
		{
			jsonObject.put("FurtherClassification", furtherClassifications);
		}
		return jsonObject;
	}

	public BasicDBObject toBasicDBObject() {
		BasicDBObject basicDBObject = new BasicDBObject();
		if (id != null)
		{
			basicDBObject.put("Id", id);
		}
		if (edition != null)
		{
			basicDBObject.put("Edition", edition);
		}
		if (mainClassification != null)
		{
			basicDBObject.put("MainClassification", mainClassification);
		}
		if (furtherClassifications != null)
		{
			basicDBObject.put("FurtherClassification", furtherClassifications);
		}
		return basicDBObject;
	}
	
	public String getTitle() {
		return title;
	}

}
