package org.safehaus.uspto.dtd;

import java.util.List;

import org.jdom2.Content;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.mongodb.BasicDBObject;

public class Citation  implements Converter{

	private static final String title = "Citation";
	
	protected Logger logger;
	
	private PatentCitation patentCitation;
	private NplCitation nplCitation;
	private String category;
	private ClassificationIpc classificationIpc;
	private ClassificationNational classificationNational;
	
	public Citation(Logger logger) {
		this.logger = logger;
	}
	
	public Citation(Element element, Logger logger)
	{
		this.logger = logger;
		
		NodeList nodeList = element.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) node;
				if (childElement.getNodeName().equals("patcit")) {
					patentCitation = new PatentCitation(childElement, logger);
				}
				else if (childElement.getNodeName().equals("category")) {
					category = childElement.getTextContent();
				}
				else if (childElement.getNodeName().equals("classification-ipc")) {
					classificationIpc = new ClassificationIpc(childElement, logger);
				}
				else if (childElement.getNodeName().equals("classification-national")) {
					classificationNational = new ClassificationNational(childElement, logger);
				}
				else if (childElement.getNodeName().equals("nplcit")) {
					nplCitation = new NplCitation(childElement, logger);
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

	public Citation(org.jdom2.Element element, Logger logger)
	{
		this.logger = logger;
		
		List<Content> nodes = element.getContent();
		for (int i=0; i < nodes.size(); i++)
		{
			Content node = nodes.get(i);
			if (node.getCType() == Content.CType.Element) {
				org.jdom2.Element childElement = (org.jdom2.Element) node;
				if (childElement.getName().equals("patcit")) {
					patentCitation = new PatentCitation(childElement, logger);
				}
				else if (childElement.getName().equals("category")) {
					category = childElement.getValue();
				}
				else if (childElement.getName().equals("classification-ipc")) {
					classificationIpc = new ClassificationIpc(childElement, logger);
				}
				else if (childElement.getName().equals("classification-national")) {
					classificationNational = new ClassificationNational(childElement, logger);
				}
				else if (childElement.getName().equals("nplcit")) {
					nplCitation = new NplCitation(childElement, logger);
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

	public PatentCitation getPatentCitation() {
		return patentCitation;
	}

	public NplCitation getNplCitation() {
		return nplCitation;
	}
	
	public String getCategory() {
		return category;
	}

	public ClassificationIpc getClassificationIpc() {
		return classificationIpc;
	}
	
	public ClassificationNational getClassificationNational() {
		return classificationNational;
	}

	@Override
	public String toString() {
		StringBuffer toStringBuffer = new StringBuffer(title+":");
		if (patentCitation != null)
		{
			toStringBuffer.append(" ");
			toStringBuffer.append(patentCitation);
		}
		if (nplCitation != null)
		{
			toStringBuffer.append(" ");
			toStringBuffer.append(nplCitation);
		}
		if (category != null)
		{
			toStringBuffer.append(" Category: ");
			toStringBuffer.append(category);
		}
		if (classificationIpc != null)
		{
			toStringBuffer.append(" ");
			toStringBuffer.append(classificationIpc);
		}
		if (classificationNational != null)
		{
			toStringBuffer.append(" ");
			toStringBuffer.append(classificationNational);
		}
		return toStringBuffer.toString();
	}
	
	public JSONObject toJSon() {
		JSONObject jsonObject = new JSONObject();
		if (category != null)
		{
			jsonObject.put("Category", category);
		}
		if (patentCitation != null)
		{
			jsonObject.put(patentCitation.getTitle(), patentCitation.toJSon());
		}
		if (nplCitation != null)
		{
			jsonObject.put(nplCitation.getTitle(), nplCitation.toJSon());
		}
		if (classificationIpc != null)
		{
			jsonObject.put(classificationIpc.getTitle(), classificationIpc.toJSon());
		}
		if (classificationNational != null)
		{
			jsonObject.put(classificationNational.getTitle(), classificationNational.toJSon());
		}
		return jsonObject;
	}

	public BasicDBObject toBasicDBObject() {
		BasicDBObject basicDBObject = new BasicDBObject();
		if (category != null)
		{
			basicDBObject.put("Category", category);
		}
		if (patentCitation != null)
		{
			basicDBObject.put(patentCitation.getTitle(), patentCitation.toBasicDBObject());
		}
		if (nplCitation != null)
		{
			basicDBObject.put(nplCitation.getTitle(), nplCitation.toBasicDBObject());
		}
		if (classificationIpc != null)
		{
			basicDBObject.put(classificationIpc.getTitle(), classificationIpc.toBasicDBObject());
		}
		if (classificationNational != null)
		{
			basicDBObject.put(classificationNational.getTitle(), classificationNational.toBasicDBObject());
		}
		return basicDBObject;
	}
	
	public String getTitle() {
		return title;
	}

}
