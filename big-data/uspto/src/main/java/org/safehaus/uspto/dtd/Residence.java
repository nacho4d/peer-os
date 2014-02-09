package org.safehaus.uspto.dtd;

import java.util.List;

import org.jdom2.Content;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.mongodb.BasicDBObject;

public class Residence  implements Converter{

	private static final String title = "Residence";
	
	protected Logger logger;
	
	private String country;
	
	public Residence(Logger logger) {
		this.logger = logger;
	}

	public Residence(Element element, Logger logger)
	{
		this.logger = logger;
		
		NodeList nodeList = element.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) node;
				if (childElement.getNodeName().equals("country")) {
					country = childElement.getTextContent();
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

	public Residence(org.jdom2.Element element, Logger logger)
	{
		this.logger = logger;
		
		List<Content> nodes = element.getContent();
		for (int i=0; i < nodes.size(); i++)
		{
			Content node = nodes.get(i);
			if (node.getCType() == Content.CType.Element) {
				org.jdom2.Element childElement = (org.jdom2.Element) node;
				if (childElement.getName().equals("country")) {
					country = childElement.getValue();
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

	public String getCountry() {
		return country;
	}

	@Override
	public String toString() {
		StringBuffer toStringBuffer = new StringBuffer(title+":");
		if (country != null)
		{
			toStringBuffer.append(" Country: ");
			toStringBuffer.append(country);
		}
		return toStringBuffer.toString();
	}
	
	public JSONObject toJSon() {
		JSONObject jsonObject = new JSONObject();
		if (country != null)
		{
			jsonObject.put("Country", country);
		}
		return jsonObject;
	}

	public BasicDBObject toBasicDBObject() {
		BasicDBObject basicDBObject = new BasicDBObject();
		if (country != null)
		{
			basicDBObject.put("Country", country);
		}
		return basicDBObject;
	}
	
	public String getTitle() {
		return title;
	}
	
}
