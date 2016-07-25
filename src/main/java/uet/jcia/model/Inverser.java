package uet.jcia.model;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import uet.jcia.entities.Column;
import uet.jcia.entities.Relationship;
import uet.jcia.entities.Table;
import uet.jcia.utils.Mappers;

public class Inverser {
    
    private static DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    
    public Inverser() {
        try {
            dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
            dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }
    
    public void updateTable(Table tbl, Document doc) {
        Element rootNode = doc.getDocumentElement();
        NodeList classNodes = rootNode.getElementsByTagName("class");
        
        System.out.println("[Inverser] modifying...");
        for (int i = 0; i < classNodes.getLength(); i++) {
            Element classElement = (Element) classNodes.item(i);
            
            // update table name
            if (classElement.getAttribute(Parser.HBM_ATT_TEMP_ID).equals(tbl.getTempId())) {
                if (tbl.getTableName() != null) {
                    classElement.setAttribute("table", tbl.getTableName());
                }
            }
            
            // update columns
            List<Column> columnList = tbl.getListColumn();
            if (columnList != null) {
                for (Column c : columnList) {
                    Element colElement = getElementByTempId(
                            classElement, c.getTempId());
                    if (colElement.getTagName().equals("id")) {
                        updateHbmId(c, colElement);
                        
                    } else if (colElement.getTagName().equals("property")) {
                        updateHbmProperty(c, colElement);
                    }
                }
            }
            
            // update relationships
            List<Relationship> relationshipList = tbl.getListRelationship();
            if (relationshipList != null) {
                for (Relationship r : relationshipList) {
                    Element relElement = getElementByTempId(
                            classElement, r.getTempId());
                    
                    if (r.getType().equals(Parser.ONE_TO_MANY)) {
                        
                    } else if (r.getType().equals(Parser.MANY_TO_ONE)) {
                        updateHbmManyToOne(r, relElement);
                    }
                }
            }
        }
        
        System.out.println("[Inverser] done!");
        
    }
    
//    public void removeNode(String xmlPath, String tempId) {
//        Document document = parser.getDocumentByXmlPath(xmlPath);
//        Element rootElement = document.getDocumentElement();
//        Element removedNode = getElementByTempId(rootElement, tempId);
//        removedNode.getParentNode().removeChild(removedNode);
//    }
    
    public void updateHbmSet(Relationship relationship, Element setElement) {
        
    }
    
    public void updateHbmManyToOne(Relationship rela, Element mtoElement) {
        if (rela.getReferColumn() != null && rela.getReferTable() != null) {
            Table refTable = rela.getReferTable();
            Column refColumn = rela.getReferColumn();
            mtoElement.setAttribute("class", refTable.getClassName());
            
            Element columnElement = (Element) mtoElement
                    .getElementsByTagName("column").item(0);
            columnElement.setAttribute("name", refColumn.getName());
            
        }
    }
    
    public void updateHbmProperty(Column col, Element propertyElement) {
        if (col.getType() != null) {
            propertyElement.setAttribute("type", Mappers.getSqltoHbm(col.getType()));
        }
        if (col.getLength() != null) {
            propertyElement.setAttribute("length", col.getLength());
        }
        
        Element childCol = (Element) propertyElement.getElementsByTagName("column").item(0);
        if (col.getName() != null) {
            childCol.setAttribute("name", col.getName());
        }
        if (col.isNotNull()) {
            childCol.setAttribute("not-null", "true");
        } else {
            childCol.setAttribute("not-null", "false");
        }
    }
    
    public void updateHbmId(Column col, Element idElement) {
        if (col.getType() != null) {
            idElement.setAttribute("type", Mappers.getSqltoHbm(col.getType()));
        }
        if (col.getLength() != null) {
            idElement.setAttribute("length", col.getLength());
        }
        
        Element childCol = (Element) idElement.getElementsByTagName("column").item(0);
        if (col.getName() != null) {
            childCol.setAttribute("name", col.getName());
        }
        if (col.isNotNull()) {
            childCol.setAttribute("not-null", "true");
        } else {
            childCol.setAttribute("not-null", "false");
        }
        
        Element childGen = (Element) idElement.getElementsByTagName("generator").item(0);
        if (col.isAutoIncrement()) {
            childGen.setAttribute("class", "increment");
        } else {
            childGen.setAttribute("class", "assigned");
        }
    }
    
    public void saveXml(String xmlPath, Document doc) {
        try {
            XPathFactory xFactory = XPathFactory.newInstance();
            XPath xPath = xFactory.newXPath();
            XPathExpression exprs = xPath.compile("//*[@" + Parser.HBM_ATT_TEMP_ID + "]");
            
            NodeList nodeList = (NodeList) exprs.evaluate(doc, XPathConstants.NODESET);
            for (int count = 0; count < nodeList.getLength(); count++) {
                Element e = (Element) nodeList.item(count);
                e.removeAttribute(Parser.HBM_ATT_TEMP_ID);
            }
            
            XMLSerializer serializer = new XMLSerializer();
            serializer.setOutputCharStream(new java.io.FileWriter(xmlPath));
            OutputFormat format = new OutputFormat();
            format.setStandalone(true);
            serializer.setOutputFormat(format);
            serializer.serialize(doc);
            
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e1) {
            e1.printStackTrace();
        }
    }
    
    private Element getElementByTempId(Element rootElement, String tempId) {
        NodeList nodeList = rootElement.getChildNodes();
        for (int count = 0; count < nodeList.getLength(); count++) {
            Node childNode = nodeList.item(count);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) childNode;
                
                if (childElement.getAttribute(Parser.HBM_ATT_TEMP_ID) != null &&
                        childElement.getAttribute(Parser.HBM_ATT_TEMP_ID).equals(tempId)) {
                    return childElement;
                    
                } else {
                    Element recursiveResult = getElementByTempId(childElement, tempId);
                    if (recursiveResult != null) return recursiveResult;
                }
            }
        }
        return null;
    }
    
}
