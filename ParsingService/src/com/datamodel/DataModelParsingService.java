package com.datamodel;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.datamodel.XMLUtil;

public class DataModelParsingService {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		File file = new File("C:\\Users\\jay\\eclipse-workspace\\ParsingService\\src\\com\\datamodel\\input.xml");

		String absolutePath = file.getAbsolutePath();

		String outputFileLocation = "C:\\Users\\jay\\eclipse-workspace\\ParsingService\\src\\com\\datamodel\\datamodelCSVs";

		Document doc = readXMLDocumentFromFile(absolutePath);
		Map<String, String> parse = parseXmlDoc(doc);

		extractMapData(parse, outputFileLocation);

		// System.out.println(parse);

		for (Map.Entry<String, String> entry : parse.entrySet()) {
			System.out.println(entry.getKey());
			System.out.println(entry.getValue());
		}

		Connection conn = databaseConnection();
		extractMapDataForDb(conn, parse);
	}



	public static Connection databaseConnection() throws SQLException {
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/levelbaseddb?useSSL=false", "root",
				"root123");
		createTables(conn);
		return conn;
	}

	public static Document readXMLDocumentFromFile(String fileNameWithPath) throws Exception {

		// Get Document Builder
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();

		// Build Document
		Document document = builder.parse(new File(fileNameWithPath));

		// Normalize the XML Structure; It's just too important !!
		document.getDocumentElement().normalize();
		return document;
	}

	public static Map<String, String> parseXmlDoc(Document doc) throws Exception {
		
		Map<String, String> map = new HashMap<>();

		Element dataModelElm = doc.getDocumentElement();

		String version = dataModelElm.getAttribute("version");
		String xmlns = dataModelElm.getAttribute("xmlns");
		String xmlnsXdm = dataModelElm.getAttribute("xmlns:xdm");
		String xmlnsXsd = dataModelElm.getAttribute("xmlns:xsd");
		String defaultDataSourceRef = dataModelElm.getAttribute("defaultDataSourceRef");
		String contOfdataModelElm = dataModelElm.getTextContent();
		Element descriptionElm = XMLUtil.getChildElement(dataModelElm, "description");
		
		StringBuilder dataModelBuilder = new StringBuilder();
		dataModelBuilder.append("Version").append("|").append("xmlns").append("|").append("xmlns:xdm")
		.append("|").append("xmlns:xsd").append("|").append("defaultDataSourceRef").append("|").append("contOfdataModelElm")
		.append("|").append(";\n");
		
		dataModelBuilder.append(version).append("|").append(xmlns).append("|").append(xmlnsXdm)
		.append("|").append(xmlnsXsd).append("|").append(defaultDataSourceRef).append("|").append(contOfdataModelElm)
		.append("|").append(";\n");
		
		
		
		
		
		StringBuilder descriptionBuilder = new StringBuilder();
		descriptionBuilder.append("defaultDataSourceRef").append("|").append("includeNewColumns").append("|").append("nullSuppress")
		.append("|").append("parentsBefore").append("|").append("textDelivery").append("|").append("isMulti1")
		.append("|").append("name1").append("|").append("xsiType4").append("|").append("IsMulti").append("|")
		.append("contOfsawCvRowElm").append("|").append("viewName").append("|").append("contOfsawCvCellElm")
		.append("|").append("contOfsawDisplayFormatElm1").append("|").append("contOfsawFormatSpecElm2")
		.append("|").append(";\n");

		String contOfdescriptionElm = descriptionElm.getTextContent();
		descriptionBuilder.append(currentView).append("|").append(includeNewColumns).append("|").append(nullSuppress)
		.append("|").append(parentsBefore).append("|").append(textDelivery).append("|").append(isMulti1)
		.append("|").append(name1).append("|").append(xsiType4).append("|").append(IsMulti).append("|")
		.append(contOfsawCvRowElm).append("|").append(viewName).append("|").append(contOfsawCvCellElm)
		.append("|").append(contOfsawDisplayFormatElm1).append("|").append(contOfsawFormatSpecElm2)
		.append("|").append(";\n");
		
		
		
		
		Element dataPropertiesElm = XMLUtil.getChildElement(dataModelElm, "dataProperties");
		StringBuilder dataPropertiesBuilder = new StringBuilder();
		
		String contOfdataPropertiesElm = dataPropertiesElm.getTextContent();
		List<Element> propertyNdList = XMLUtil.getChildElements(dataPropertiesElm, "property");
		for (Element propertyElm : propertyNdList) {

			String isMulti = propertyElm.getAttribute("IsMulti");
			String name = propertyElm.getAttribute("name");
			String value = propertyElm.getAttribute("value");

		}
		
		
		
		List<Element> dataSetsNdList = XMLUtil.getChildElements(dataModelElm, "dataSets");
		StringBuilder dataSetsBuilder = new StringBuilder();
		

		for (Element dataSetsElm : dataSetsNdList) {

			List<Element> dataSetNdList = XMLUtil.getChildElements(dataSetsElm, "dataSet");
			for (Element dataSetElm : dataSetNdList) {

				String name = dataSetElm.getAttribute("name");
				String type = dataSetElm.getAttribute("type");

				Element fileElm = XMLUtil.getChildElement(dataSetElm, "file");

				String dataSourceRef = fileElm.getAttribute("dataSourceRef");
				String contOffileElm = fileElm.getTextContent();
			}
		}
		
		
		Element outputElm = XMLUtil.getChildElement(dataModelElm, "output");
		StringBuilder outputBuilder = new StringBuilder();
		
		String rootName = outputElm.getAttribute("rootName");
		String uniqueRowName = outputElm.getAttribute("uniqueRowName");
		String contOfoutputElm = outputElm.getTextContent();
		Element nodeListElm = XMLUtil.getChildElement(outputElm, "nodeList");

		String name = nodeListElm.getAttribute("name");
		String contOfnodeListElm = nodeListElm.getTextContent();
		
		
		Element eventTriggersElm = XMLUtil.getChildElement(dataModelElm, "eventTriggers");
		StringBuilder eventTriggersBuilder = new StringBuilder();
		String contOfeventTriggersElm = eventTriggersElm.getTextContent();
		
		
		Element lexicalsElm = XMLUtil.getChildElement(dataModelElm, "lexicals");
		StringBuilder lexicalsBuilder = new StringBuilder();
		String contOflexicalsElm = lexicalsElm.getTextContent();
		
		
		Element valueSetsElm = XMLUtil.getChildElement(dataModelElm, "valueSets");
		StringBuilder valueSetsBuilder = new StringBuilder();
		String contOfvalueSetsElm = valueSetsElm.getTextContent();
		
		
		Element parametersElm = XMLUtil.getChildElement(dataModelElm, "parameters");
		StringBuilder parametersBuilder = new StringBuilder();
		String contOfparametersElm = parametersElm.getTextContent();
		
		
		Element burstingElm = XMLUtil.getChildElement(dataModelElm, "bursting");
		StringBuilder burstingBuilder = new StringBuilder();
		String contOfburstingElm = burstingElm.getTextContent();
		
		
		Element displayElm = XMLUtil.getChildElement(dataModelElm, "display");
		StringBuilder displayBuilder = new StringBuilder();
		
		
		String contOfdisplayElm = displayElm.getTextContent();
		Element layoutsElm = XMLUtil.getChildElement(displayElm, "layouts");
		String contOflayoutsElm = layoutsElm.getTextContent();
		List<Element> layoutNdList = XMLUtil.getChildElements(layoutsElm, "layout");
		for (Element layoutElm : layoutNdList) {

			String isMulti = layoutElm.getAttribute("IsMulti");
			String left = layoutElm.getAttribute("left");
			String layoutName = layoutElm.getAttribute("name");
			String top = layoutElm.getAttribute("top");

		}
		Element groupLinksElm = XMLUtil.getChildElement(displayElm, "groupLinks");
		String contOfgroupLinksElm = groupLinksElm.getTextContent();
		

		map.put("DataModel", dataModelBuilder.toString());
		map.put("Description", descriptionBuilder.toString());
		map.put("DataProperties", dataPropertiesBuilder.toString());
		map.put("DataSets", dataSetsBuilder.toString());
		map.put("Output", outputBuilder.toString());
		map.put("EventTriggers", eventTriggersBuilder.toString());
		map.put("Lexicals", lexicalsBuilder.toString());
		map.put("ValueSets", valueSetsBuilder.toString());
		map.put("Parameters", parametersBuilder.toString());
		map.put("Bursting", burstingBuilder.toString());
		map.put("Display", displayBuilder.toString());
		
		return map;
	}

	
	private static void createTables(Connection connection) throws SQLException {

		String reportTableQuery = "CREATE TABLE IF NOT EXISTS Report (" + "xmlVersion INT, "
				+ "xmlnsSaw VARCHAR(1000), " + "xmlnsSawx VARCHAR(1000), " + "xmlnsXsd  VARCHAR(1000), "
				+ "xmlnsXsi VARCHAR(1000), " + "contOfsawReportElm VARCHAR(1000), " + "PRIMARY KEY (xmlVersion)" + ")";

		String criteriaTableQuery = "CREATE TABLE IF NOT EXISTS Criteria (" + "subjectArea VARCHAR(100), "
				+ "withinHierarchy VARCHAR(50)," + "xsiTypeCriteria VARCHAR(100),"
				+ "contOfsawCriteriaElm VARCHAR(1000)," + "contOfsawColumnsElm VARCHAR(1000),"
				+ "PRIMARY KEY (subjectArea)" + ")";

		String criteriaColumnsTableQuery = "CREATE TABLE IF NOT EXISTS CriteriaColumns (" + "columnID VARCHAR(100), "
				+ "isMulti VARCHAR(100), " + "xsiType VARCHAR(100), " + "xsiType1 VARCHAR(100), "
				+ "contOfsawxExprElm VARCHAR(100), " + "fontColor VARCHAR(100), " + "suppress VARCHAR(100), "
				+ "wrapText VARCHAR(100), " + "contOfsawFormatSpecElm VARCHAR(100), " + "commas VARCHAR(100), "
				+ "maxDigits VARCHAR(100), " + "minDigits VARCHAR(100), " + "negativeType VARCHAR(100), "
				+ "xsiType2 VARCHAR(100), " + "contOfsawDisplayFormatElm VARCHAR(100), "
				+ "contOfsawFormatSpecElm1 VARCHAR(100), " + "PRIMARY KEY (columnID)" + ")";

		String criteriaFilterQuery = "CREATE TABLE IF NOT EXISTS CriteriaFilter (" + "name VARCHAR(100), "
				+ "path VARCHAR(200)," + "xsiType VARCHAR(200)," + "contOfsawxExprElm VARCHAR(200),"
				+ "PRIMARY KEY (name)" + ")";

		String interactionOptionsTableQuery = "CREATE TABLE IF NOT EXISTS InteractionOptions ("
				+ "addRemoveValues VARCHAR(100), " + "calcitemoperations VARCHAR(100), " + "drill VARCHAR(100), "
				+ "groupOperations VARCHAR(100), " + "inclexClColumns VARCHAR(100), " + "moveColumns VARCHAR(100), "
				+ "showHideRunningSum VARCHAR(100), " + "showHideSubtotal VARCHAR(100), " + "sortColumns VARCHAR(100), "
				+ "contOfsawInteractionOptionsElm VARCHAR(100) " + ")";

		String viewsTableQuery = "CREATE TABLE IF NOT EXISTS Views (" + "currentView VARCHAR(100),"
				+ "includeNewColumns VARCHAR(100), " + "nullSuppress VARCHAR(100), " + "parentsBefore VARCHAR(100), "
				+ "textDelivery VARCHAR(100), " + "isMulti1 VARCHAR(100), " + "name1 VARCHAR(100), "
				+ "xsiType4 VARCHAR(100), " + "IsMulti VARCHAR(100), " + "contOfsawCvRowElm VARCHAR(100), "
				+ "viewName VARCHAR(100), " + "contOfsawCvCellElm VARCHAR(100), "
				+ "contOfsawDisplayFormatElm1 VARCHAR(100), " + "contOfsawFormatSpecElm2 VARCHAR(100) " + ")";

		try (PreparedStatement statement = connection.prepareStatement(reportTableQuery);
				PreparedStatement statement2 = connection.prepareStatement(criteriaTableQuery);
				PreparedStatement statement3 = connection.prepareStatement(criteriaColumnsTableQuery);
				PreparedStatement statement4 = connection.prepareStatement(criteriaFilterQuery);
				PreparedStatement statement5 = connection.prepareStatement(interactionOptionsTableQuery);
				PreparedStatement statement6 = connection.prepareStatement(viewsTableQuery)) {
			statement.executeUpdate();
			statement2.executeUpdate();
			statement3.executeUpdate();
			statement4.executeUpdate();
			statement5.executeUpdate();
			statement6.executeUpdate();

		}

	}

	public static void extractMapDataForDb(Connection conn, Map<String, String> mapDoc) throws Exception {

		String[] splitDatabase = mapDoc.get("Report").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "Report", splitDatabase);

		splitDatabase = mapDoc.get("Criteria").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "Criteria", splitDatabase);

		splitDatabase = mapDoc.get("CriteriaColumns").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "CriteriaColumns", splitDatabase);

		splitDatabase = mapDoc.get("CriteriaFilter").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "CriteriaFilter", splitDatabase);

		splitDatabase = mapDoc.get("InteractionOptions").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "InteractionOptions", splitDatabase);

		splitDatabase = mapDoc.get("Views").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "Views", splitDatabase);

	}

public static void insertDataInDB(Connection conn, String dbTableName, String[] data) throws Exception {

	try {
		String[] header = data[0].split(Pattern.quote("|"));
		StringBuilder headerBuilder = new StringBuilder();
		headerBuilder.append("(");

		StringBuilder valueBuilder = new StringBuilder();
		valueBuilder.append("(");

		for (int i = 0; i < header.length; i++) {
			headerBuilder.append(header[i]);
			valueBuilder.append("?");
			if (i != header.length - 1) {
				headerBuilder.append(",");
				valueBuilder.append(",");
			}
		}
		headerBuilder.append(")");
		valueBuilder.append(")");

		// Query to delete all records in a table
		String deleteQuery = "Truncate table" + " " + dbTableName;

		String query = "INSERT INTO " + dbTableName + headerBuilder.toString() + " VALUES "
				+ valueBuilder.toString();

		// // Executing the query
		PreparedStatement deletePstmt = conn.prepareStatement(deleteQuery);
		deletePstmt.executeUpdate();

		PreparedStatement pstmt = conn.prepareStatement(query);
		// skipping first line as header
		for (int dataInput = 1; dataInput < data.length; dataInput++) {
			String[] splitFtrData = data[dataInput].split(Pattern.quote("|"));
			for (int splitData = 0; splitData < splitFtrData.length; splitData++) {
				pstmt.setString(splitData + 1, splitFtrData[splitData]);

			}
			pstmt.executeUpdate();
		}

	}

	catch (Exception e) {
		System.out.println("failure:" + e.getMessage());
	}

}
}

