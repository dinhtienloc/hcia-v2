/* File : Parser.class 
 * Author : hieusonson9x@gmail.com
 * Details: to  parser XML to List<Table> . Key API parserAllToListTable 
 * */

package uet.jcia.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import uet.jcia.entities.Column;
import uet.jcia.entities.Relationship;
import uet.jcia.entities.Table;

public class Parser {
    
    private static int tempIdGenerator = 0;
    
    private static final String ONE_TO_ONE = "one-to-one";
    private static final String ONE_TO_MANY = "one-to-many";
    private static final String MANY_TO_ONE = "many-to-one";
    
    private static DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    
    static {
        try {
            dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
            dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }
    
	private HashMap<String,String> mappingType;
	private HashMap<String, String> classTableMapper;
	private HashMap<String, Document> tagMapper;
	
	private HashMap<String, Column> cachedColumns;
	
	public Parser(){
		mappingType = new HashMap<>();
		classTableMapper = new HashMap<>();
		tagMapper = new HashMap<>();
		cachedColumns = new HashMap<>();
		
		mappingType.put("java.lang.Integer", "INTEGER");
		mappingType.put("int", "INTEGER");
		mappingType.put("java.lang.Long", "BIGINT");
		mappingType.put("long", "BIGINT");
		mappingType.put("java.lang.Short", "SMALLINT");
		mappingType.put("short", "SMALLINT");
		mappingType.put("java.lang.Float", "FLOAT");
		mappingType.put("float", "FLOAT");
		mappingType.put("java.lang.Double", "DOUBLE");
		mappingType.put("double", "DOUBLE");
		mappingType.put("java.math.BigDecimal", "NUMERIC");
		mappingType.put("java.lang.String", "VARCHAR");
		mappingType.put("string", "VARCHAR");
		mappingType.put("java.lang.Byte", "TINYINT");
		mappingType.put("byte", "TINYINT");
		mappingType.put("java.lang.Boolean", "BIT");
		mappingType.put("boolean", "BIT");
		mappingType.put("java.sql.Date", "DATE");
		mappingType.put("date", "DATE");
		mappingType.put("java.util.Date", "DATE");
		mappingType.put("java.sql.Time", "TIME");
		mappingType.put("java.sql.Timestamp", "TIMESTAMP");
		mappingType.put("java.util.Calendar", "TIMESTAMP");
		mappingType.put("byte[]", "VARBINARY()");
		mappingType.put("java.io.Serializable", "BLOB()");
		mappingType.put("java.sql.Clob", "CLOB()");
		mappingType.put("java.sql.Blob", "BLOB()");
		mappingType.put("java.lang.Class", "VARCHAR");
		mappingType.put("java.util.Locale", "VARCHAR");
		mappingType.put("java.util.TimeZone", "VARCHAR");
		mappingType.put("java.util.Currency", "VARCHAR");	
	}
	
	
	// key API for controller layer 
	public List<Table> parseXmlList(List<String> xmlList){
	    System.out.println("[Parser] starting....");

	    List<Table> result = new ArrayList<Table>();
		
		for(String xml : xmlList){
			result.addAll(parseOneXmlToListTable(xml));
		}
		
		System.out.println("[Parser] add relationships....");
		// get information of reference tables 
        for (Table t : result) {
            List<Relationship> relationshipList = t.getListRelationship();
            for (Relationship r : relationshipList) {
                String refTableName = classTableMapper.get(r.getReferClass());
                String refColumnName = r.getReferColumn();
                r.setReferTable(refTableName);
                
                if (r.getType().equals(MANY_TO_ONE)) {
                    Column refColumn = cachedColumns.get(refTableName.toUpperCase() 
                            + "." 
                            + refColumnName.toUpperCase());
                    // clone reference column to own table
                    if (refColumn != null) {
                        refColumn.setPrimaryKey(false);
                        refColumn.setAutoIncrement(false);
                        refColumn.setTempId("" + tempIdGenerator++);
                    }
                    t.getListColumn().add(refColumn);
                }
            }
        }
		
        System.out.println("[Parser] done!");
		return result;
	}
	
	
	public List<Table> parseOneXmlToListTable(String xmlLink){
		List<Table> result = new ArrayList<>();
		try {
			File fXmlFile = new File(xmlLink);
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			
			System.out.println("[Parser] parsing [" + xmlLink + "]....");
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			System.out.println("[Parser] parsed....");
			
			System.out.println("[Parser] analylising....");
			// invalid hbm xml must contain hibernate-mapping tag
			if (doc.getElementsByTagName("hibernate-mapping").getLength() == 0) {
			    return result;
			}
			
			// map xmlLink and document
			tagMapper.put(xmlLink, doc);
			
			NodeList listClass = doc.getElementsByTagName("class");
			
			// add one table to result with each class tag
			for(int temp = 0; temp < listClass.getLength(); temp++){
			    Node classNode = listClass.item(temp);
				Table table = parseOneTagClassToTable(classNode);
				table.setRefXml(xmlLink);
				
				String tempId = "" + tempIdGenerator++;
				table.setTempId(tempId);
				((Element)classNode).setAttribute("temp_id", tempId);
				result.add(table);
			}
			
			System.out.println("[Parser] analylised....");
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private Table parseOneTagClassToTable(Node classNode){
		Table result = new Table();
		//write code here/
		Element element = (Element)classNode;
		
		String className = element.getAttribute("name");
		String tableName = element.getAttribute("table").toUpperCase();
		result.setClassName(className);
		result.setTableName(tableName);
		
		// add to mapper of class and table
		classTableMapper.put(className, tableName);
		
		//add list column to Table result
		List<Column> columns = new ArrayList<>();
		
		// Add column Id if have tag
		NodeList id = element.getElementsByTagName("id");
		if(id.getLength()!=0){
		    Column c = parseIdTagToColumn(id.item(0));
			columns.add(c);
			
			cachedColumns.put(tableName.toUpperCase() + "." + c.getName().toUpperCase(), c);
		}
		
		//add list column PK if have tag composite-id
		NodeList composite = element.getElementsByTagName("composite-id");
		if(composite.getLength() != 0){
			columns.addAll(parserOneCompositeIdTagToListColumn(composite.item(0)));
		}
		
		//add list column by property tags
		NodeList pNodeList = element.getElementsByTagName("property");
		for(int temp = 0; temp < pNodeList.getLength(); temp++){
		    Column c = parseOnePropertyTagToColumn(pNodeList.item(temp));
			columns.add(c);
			
			cachedColumns.put(tableName.toUpperCase() + "." + c.getName().toUpperCase(), c);
		}
		
		// add list relationship
		List<Relationship> relationships = new ArrayList<>();
		NodeList r1NodeList = element.getElementsByTagName("many-to-one");
		for(int temp = 0; temp < r1NodeList.getLength(); temp++){
			relationships.add(parseOneTagManyToOne(r1NodeList.item(temp)));
		
		}
		
		// actually, this is 1-n relationship
		NodeList r2NodeList = element.getElementsByTagName("set");
		for(int temp = 0; temp < r2NodeList.getLength(); temp++){
			relationships.add(parseSet(r2NodeList.item(temp)));
		}
		
		//set column and relation to table result
		result.setListRelationship(relationships);
		result.setListColumn(columns);
		return result;
	}
	
	private Column parseIdTagToColumn(Node idNode){
		if(idNode== null ){ return null;}
		
		Column result = new Column();
		Element element = (Element) idNode;
		
		String tempId = "" + tempIdGenerator++;
        result.setTempId(tempId);
        element.setAttribute("temp_id", tempId);
		
		//get Attribute name and type ,primary key , not null property
		Element col = (Element) element.getElementsByTagName("column").item(0);
		result.setName(col.getAttribute("name").toUpperCase());
		result.setLength(col.getAttribute("length"));
		
		result.setType(mappingType.get(element.getAttribute("type")));
		result.setPrimaryKey(true);
		result.setNotNull(true);
		
		//get generator
		Element generatorNode = (Element) element.getElementsByTagName("generator").item(0);
		String test =  generatorNode.getAttribute("class");
		if(!test.equals("select")){
		    result.setAutoIncrement(true);
		}
		else result.setAutoIncrement(false);
		
		return result;
	}
	
	private Column parseOnePropertyTagToColumn(Node pNode){
		Column result = new Column();
		Element element = (Element) pNode;
		
		String tempId = "" + tempIdGenerator++;
        result.setTempId(tempId);
        element.setAttribute("temp_id", tempId);
		
		//set name , type , primary key and AutoIcrement
		Element col = (Element) element.getElementsByTagName("column").item(0);
		result.setName(col.getAttribute("name").toUpperCase());
		result.setLength(col.getAttribute("length"));

		result.setType(mappingType.get(element.getAttribute("type")));
		result.setPrimaryKey(false);
		result.setAutoIncrement(false);
		
		//set not-null
		Element generatorNode = (Element) element.getElementsByTagName("column").item(0);
		String test = generatorNode.getAttribute("not-null");
		if(test.equals("true")){
			result.setNotNull(true);
		} else result.setNotNull(false);
		
		return result;
	}
	
	private Relationship parseOneTagManyToOne(Node rNode){
		Relationship result = new Relationship();
		Element element = (Element)rNode;
		
		String tempId = "" + tempIdGenerator++;
        result.setTempId(tempId);
        element.setAttribute("temp_id", tempId);
		
		result.setReferClass(element.getAttribute("class"));
		// n-1 tag enable unique will be an 1-1 relationship 
		String unique = element.getAttribute("unique");
		if (unique != null && unique.equalsIgnoreCase("true")) {
		    result.setType(ONE_TO_ONE);
		} else {
		    result.setType(MANY_TO_ONE);
		}
		
		Element col = (Element) element.getElementsByTagName("column").item(0);
		result.setReferColumn(col.getAttribute("name").toUpperCase());
		
		return result; 
	}
	
	private Relationship parseSet(Node rNode){
		Relationship result = new Relationship(); 
		result.setType(ONE_TO_MANY);
		
		Element element = (Element)rNode;
		
		String tempId = "" + tempIdGenerator++;
        result.setTempId(tempId);
        element.setAttribute("temp_id", tempId);
		
		Element key = (Element) element.getElementsByTagName("key").item(0);
		Element col = (Element) key.getElementsByTagName("column").item(0);
		
		// set key column
        result.setReferColumn(col.getAttribute("name").toUpperCase());
		
		Element oneToManyTag = (Element) 
		        element.getElementsByTagName("one-to-many").item(0);
		result.setReferClass(oneToManyTag.getAttribute("class"));
		
		return result;
	}
	
	private List<Column> parserOneCompositeIdTagToListColumn(Node cpNode){
		if(cpNode == null ) return null;
		List<Column> result = new ArrayList<>();
		Element element = (Element)cpNode;
		NodeList listKey = element.getElementsByTagName("key-property");
		
		// create one column by each key-property tag
		for(int temp = 0 ; temp < listKey.getLength() ; temp++){
			Element e = (Element)listKey.item(temp);
			Column column = new Column();
			
			String tempId = "" + tempIdGenerator++;
	        column.setTempId(tempId);
	        e.setAttribute("temp_id", tempId);
			
			// set type, default not-null , default PK , default AI
			column.setType(mappingType.get(e.getAttribute("type")));
			column.setNotNull(true);
			column.setPrimaryKey(true);
			column.setAutoIncrement(false);
			
			Element col = (Element) e.getElementsByTagName("column").item(0);
			column.setName(col.getAttribute("name"));
			column.setLength(col.getAttribute("length"));
			result.add(column);
		}
		return result;
	}
	
	public HashMap<String, String> getClassTableMapper() {
        return classTableMapper;
    }
	
	public HashMap<String, Document> getTagMapper() {
        return tagMapper;
    }
	
}
