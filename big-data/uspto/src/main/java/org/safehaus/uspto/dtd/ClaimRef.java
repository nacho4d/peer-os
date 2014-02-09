package org.safehaus.uspto.dtd;

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
import org.w3c.dom.Text;

import com.mongodb.BasicDBObject;

public class ClaimRef implements Converter{

	private static final String title = "ClaimRef";
	
	protected Logger logger;
	
	private String idref;
	private String text;
	
	public ClaimRef(Logger logger) {
		this.logger = logger;
	}
	
	public ClaimRef(Element element, Logger logger)
	{
		this.logger = logger;
		
		text = element.getTextContent();
		
		NamedNodeMap nodemap = element.getAttributes();
		for (int i=0; i < nodemap.getLength(); i++)
		{
			Node node = nodemap.item(i);
			
			if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
				Attr attribute = (Attr) node;
				if (attribute.getNodeName().equals("idref")) {
					idref = attribute.getNodeValue();
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
			if (node.getNodeType() == Node.TEXT_NODE) {
				Text childText = (Text) node;
				text = childText.getNodeValue();
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

	public ClaimRef(org.jdom2.Element element, Logger logger)
	{
		this.logger = logger;
		
		List<Attribute> attributes = element.getAttributes();
		for (int i=0; i < attributes.size(); i++)
		{
			Attribute attribute = attributes.get(i);
			if (attribute.getName().equals("idref")) {
				idref = attribute.getValue();
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
			if (node.getCType() == Content.CType.Text) {
				org.jdom2.Text text = (org.jdom2.Text)node;
				this.text = text.getValue();
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

	public String getIdref() {
		return idref;
	}

	public String getText() {
		return text;
	}


	@Override
	public String toString() {
		StringBuffer toStringBuffer = new StringBuffer(title+":");
		if (idref != null)
		{
			toStringBuffer.append(" IdRef: ");
			toStringBuffer.append(idref);
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
		if (idref != null)
		{
			jsonObject.put("Idref", idref);
		}
		if (text != null)
		{
			jsonObject.put("Text", text);
		}
		return jsonObject;
	}

	public BasicDBObject toBasicDBObject() {
		BasicDBObject basicDBObject = new BasicDBObject();
		if (idref != null)
		{
			basicDBObject.put("Idref", idref);
		}
		if (text != null)
		{
			basicDBObject.put("Text", text);
		}
		return basicDBObject;
	}
	
	public String getTitle() {
		return title;
	}
	
}
