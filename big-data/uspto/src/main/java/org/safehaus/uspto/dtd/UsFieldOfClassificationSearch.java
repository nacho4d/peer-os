package org.safehaus.uspto.dtd;

import java.util.List;

import org.jdom2.Content;
import org.slf4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class UsFieldOfClassificationSearch extends SingleCollection<Converter>{

	private static final String title = "UsFieldOfClassificationSearch";
	
	protected Logger logger;
	
	public UsFieldOfClassificationSearch(Logger logger) {
		super();
		this.logger = logger;
	}
	
	public UsFieldOfClassificationSearch(Element element, Logger logger)
	{
		super(element);
		this.logger = logger;
		
		NodeList nodeList = element.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) node;
				if (childElement.getNodeName().equals("us-classifications-ipcr")) {
					elements.add(new UsClassificationsIpcr(childElement, logger));
				}
				else if (childElement.getNodeName().equals("classification-national")) {
					elements.add(new ClassificationNational(childElement, logger));
				}
				else if (childElement.getNodeName().equals("classification-cpc-text")) {
					elements.add(new ClassificationCpcText(childElement, logger));
				}
				else if (childElement.getNodeName().equals("classification-cpc-combination-text")) {
					elements.add(new ClassificationCpcCombinationText(childElement, logger));
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

	public UsFieldOfClassificationSearch(org.jdom2.Element element, Logger logger)
	{
		super(element);
		this.logger = logger;
		
		List<Content> nodes = element.getContent();
		for (int i=0; i < nodes.size(); i++)
		{
			Content node = nodes.get(i);
			if (node.getCType() == Content.CType.Element) {
				org.jdom2.Element childElement = (org.jdom2.Element) node;
				if (childElement.getName().equals("us-classifications-ipcr")) {
					elements.add(new UsClassificationsIpcr(childElement, logger));
				}
				else if (childElement.getName().equals("classification-national")) {
					elements.add(new ClassificationNational(childElement, logger));
				}
				else if (childElement.getName().equals("classification-cpc-text")) {
					elements.add(new ClassificationCpcText(childElement, logger));
				}
				else if (childElement.getName().equals("classification-cpc-combination-text")) {
					elements.add(new ClassificationCpcCombinationText(childElement, logger));
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
		toStringBuffer.append(super.toString());
		return toStringBuffer.toString();
	}
	
	@Override
	public String getTitle() {
		return title;
	}
	
}
