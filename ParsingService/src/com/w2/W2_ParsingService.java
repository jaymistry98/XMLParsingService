package com.w2;

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

import com.samp_products_d.XMLUtil;

/**
 * The class W2_Parsing_Service
 * 
 * @author 10719785
 */
public class W2_ParsingService {

	/**
	 *
	 * Main
	 *
	 * @param args[]
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		//File file = new File("C:\\Users\\jay\\eclipse-workspace\\ParsingService\\src\\com\\w2\\w2+data+model%2exdm\\_xdo_local%2ew2b%2exml");
		
		File file = new File(
				"C:\\Users\\jay\\eclipse-workspace\\ParsingService\\src\\com\\w2\\w2+data+model%2exdm\\sample%2exml");
		
		//File file = new File("C:\\Users\\jay\\eclipse-workspace\\ParsingService\\src\\com\\w2\\w2+data+model%2exdm\\w2b%2exml");
		
		String absolutePath = file.getAbsolutePath();

		String outputFileLocation = "C:\\Users\\jay\\eclipse-workspace\\ParsingService\\src\\com\\w2\\W2_Parsed";

		Document doc = readXMLDocumentFromFile(absolutePath);
		Map<String, String> parse = parseXmlDoc(doc);

		extractMapData(parse, outputFileLocation);

		// System.out.println(parse);

		for (Map.Entry<String, String> entry : parse.entrySet()) {
			System.out.println(entry.getKey() + ":" + "\n");
			System.out.println(entry.getValue());
		}

		Connection conn = databaseConnection();
		extractMapDataForDb(conn, parse);
	}

	/**
	 *
	 * Database Connection
	 *
	 * @param
	 * @return Connection
	 * @throws SQLException
	 */
	public static Connection databaseConnection() throws SQLException {
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/W2DB?useSSL=false",
				"root", "root123");
		createTables(conn);
		return conn;
	}

	/**
	 *
	 * Read XML document from file
	 *
	 * @param fileNameWithPath {@link String}
	 * @return Document
	 * @throws Exception
	 */
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

	/**
	 *
	 * Parse xml document
	 *
	 * @param doc {@link Document}
	 * @return Map<String, String>
	 * @throws Exception
	 */
	public static Map<String, String> parseXmlDoc(Document doc) throws Exception {

		Map<String, String> map = new HashMap<>();
		
		Element w2Elm = doc.getDocumentElement();

		String contOfw2Elm = w2Elm.getTextContent();
		if (contOfw2Elm.isEmpty()) {
			contOfw2Elm = null;
		}
		
		
		StringBuilder w2_Builder = new StringBuilder();

		w2_Builder.append("Control_Number").append("|").append("federal_ein").append("|").append("employer_name").append("|")
				.append("employer_address").append("|").append("SSN").append("|").append("emp_name").append("|")
				.append("last_name").append("|").append("employee_address").append("|")
				
				.append("wages_tips_compensation").append("|")
				.append("FIT_WITHHELD").append("|").append("ss_wages").append("|").append("ss_withheld").append("|")
				.append("med_wages").append("|").append("med_withheld").append("|").append("ss_tips").append("|")
				.append("allocated_tips").append("|").append("eic_payment").append("|").append("dependent_care").append("|")
				.append("non_qual_plan").append("|")
				
				.append("box12a_code").append("|").append("box12a_meaning").append("|")
				.append("box12b_code").append("|").append("box12b_meaning").append("|").append("box12c_code").append("|")
				.append("box12c_meaning").append("|").append("box12d_code").append("|").append("box12d_meaning").append("|")
				
				.append("stat_employee").append("|").append("retirement_plan").append("|").append("sick_pay").append("|")
				
				.append("Box14_codea").append("|").append("Box14_meaninga").append("|").append("Box14_codeb").append("|")
				.append("Box14_meaningb").append("|").append("Box14_codec").append("|").append("Box14_meaningc").append("|")
				
				.append("state_code").append("|").append("STATE_EIN").append("|").append("state1_wages").append("|")
				.append("state1_tax").append("|").append("local1_wages").append("|").append("local1_tax").append("|")
				.append("locality1").append("|")
				
				//.append("ContentsOfW2").append("|")
				
				.append(";\n");

		
		Element control_NumberElm = XMLUtil.getChildElement(w2Elm, "Control_Number");
		String contOfControl_NumberElm = control_NumberElm.getTextContent();
		if (contOfControl_NumberElm.isEmpty()) {
			contOfControl_NumberElm = null;
		}
		
		Element federal_einElm = XMLUtil.getChildElement(w2Elm, "federal_ein");
		String contOffederal_einElm = federal_einElm.getTextContent();
		if (contOffederal_einElm.isEmpty()) {
			contOffederal_einElm = null;
		}
		
		Element employer_nameElm = XMLUtil.getChildElement(w2Elm, "employer_name");
		String contOfemployer_nameElm = employer_nameElm.getTextContent();
		if (contOfemployer_nameElm.isEmpty()) {
			contOfemployer_nameElm = null;
		}
		
		Element employer_addressElm = XMLUtil.getChildElement(w2Elm, "employer_address");
		String contOfemployer_addressElm = employer_addressElm.getTextContent().replaceAll("\\s+", "");
		if (contOfemployer_addressElm.isEmpty()) {
			contOfemployer_addressElm = null;
		}
		
		Element sSNElm = XMLUtil.getChildElement(w2Elm, "SSN");
		String contOfSSNElm = sSNElm.getTextContent();
		if (contOfSSNElm.isEmpty()) {
			contOfSSNElm = null;
		}
		
		Element emp_nameElm = XMLUtil.getChildElement(w2Elm, "emp_name");
		String contOfemp_nameElm = emp_nameElm.getTextContent();
		if (contOfemp_nameElm.isEmpty()) {
			contOfemp_nameElm = null;
		}
		
		Element last_nameElm = XMLUtil.getChildElement(w2Elm, "last_name");
		String contOflast_nameElm = last_nameElm.getTextContent();
		if (contOflast_nameElm.isEmpty()) {
			contOflast_nameElm = null;
		}
		
		Element employee_addressElm = XMLUtil.getChildElement(w2Elm, "employee_address");
		String contOfemployee_addressElm = employee_addressElm.getTextContent().replaceAll("\\s+", "");;
		if (contOfemployee_addressElm.isEmpty()) {
			contOfemployee_addressElm = null;
		}
		
		Element wages_tips_compensationElm = XMLUtil.getChildElement(w2Elm, "wages_tips_compensation");
		String contOfwages_tips_compensationElm = wages_tips_compensationElm.getTextContent();
		if (contOfwages_tips_compensationElm.isEmpty()) {
			contOfwages_tips_compensationElm = null;
		}
		
		Element fIT_WITHHELDElm = XMLUtil.getChildElement(w2Elm, "FIT_WITHHELD");
		String contOfFIT_WITHHELDElm = fIT_WITHHELDElm.getTextContent();
		if (contOfFIT_WITHHELDElm.isEmpty()) {
			contOfFIT_WITHHELDElm = null;
		}
		
		Element ss_wagesElm = XMLUtil.getChildElement(w2Elm, "ss_wages");
		String contOfss_wagesElm = ss_wagesElm.getTextContent();
		if (contOfss_wagesElm.isEmpty()) {
			contOfss_wagesElm = null;
		}
		
		Element ss_withheldElm = XMLUtil.getChildElement(w2Elm, "ss_withheld");
		String contOfss_withheldElm = ss_withheldElm.getTextContent();
		if (contOfss_withheldElm.isEmpty()) {
			contOfss_withheldElm = null;
		}
		
		Element med_wagesElm = XMLUtil.getChildElement(w2Elm, "med_wages");
		String contOfmed_wagesElm = med_wagesElm.getTextContent();
		if (contOfmed_wagesElm.isEmpty()) {
			contOfmed_wagesElm = null;
		}
		
		Element med_withheldElm = XMLUtil.getChildElement(w2Elm, "med_withheld");
		String contOfmed_withheldElm = med_withheldElm.getTextContent();
		if (contOfmed_withheldElm.isEmpty()) {
			contOfmed_withheldElm = null;
		}
		
		Element ss_tipsElm = XMLUtil.getChildElement(w2Elm, "ss_tips");
		String contOfss_tipsElm = ss_tipsElm.getTextContent();
		if (contOfss_tipsElm.isEmpty()) {
			contOfss_tipsElm = null;
		}
		
		Element allocated_tipsElm = XMLUtil.getChildElement(w2Elm, "allocated_tips");
		String contOfallocated_tipsElm = allocated_tipsElm.getTextContent();
		if (contOfallocated_tipsElm.isEmpty()) {
			contOfallocated_tipsElm = null;
		}
		
		Element eic_paymentElm = XMLUtil.getChildElement(w2Elm, "eic_payment");
		String contOfeic_paymentElm = eic_paymentElm.getTextContent();
		if (contOfeic_paymentElm.isEmpty()) {
			contOfeic_paymentElm = null;
		}
		
		Element dependent_careElm = XMLUtil.getChildElement(w2Elm, "dependent_care");
		String contOfdependent_careElm = dependent_careElm.getTextContent();
		if (contOfdependent_careElm.isEmpty()) {
			contOfdependent_careElm = null;
		}
		
		Element non_qual_planElm = XMLUtil.getChildElement(w2Elm, "non_qual_plan");
		String contOfnon_qual_planElm = non_qual_planElm.getTextContent();
		if (contOfnon_qual_planElm.isEmpty()) {
			contOfnon_qual_planElm = null;
		}
		
		Element box12a_codeElm = XMLUtil.getChildElement(w2Elm, "box12a_code");
		String contOfbox12a_codeElm = box12a_codeElm.getTextContent();
		if (contOfbox12a_codeElm.isEmpty()) {
			contOfbox12a_codeElm = null;
		}
		
		Element box12a_meaningElm = XMLUtil.getChildElement(w2Elm, "box12a_meaning");
		String contOfbox12a_meaningElm = box12a_meaningElm.getTextContent();
		if (contOfbox12a_meaningElm.isEmpty()) {
			contOfbox12a_meaningElm = null;
		}
		
		Element box12b_codeElm = XMLUtil.getChildElement(w2Elm, "box12b_code");
		String contOfbox12b_codeElm = box12b_codeElm.getTextContent();
		if (contOfbox12b_codeElm.isEmpty()) {
			contOfbox12b_codeElm = null;
		}
		
		Element box12b_meaningElm = XMLUtil.getChildElement(w2Elm, "box12b_meaning");
		String contOfbox12b_meaningElm = box12b_meaningElm.getTextContent();
		if (contOfbox12b_meaningElm.isEmpty()) {
			contOfbox12b_meaningElm = null;
		}
		
		Element box12c_codeElm = XMLUtil.getChildElement(w2Elm, "box12c_code");
		String contOfbox12c_codeElm = box12c_codeElm.getTextContent();
		if (contOfbox12c_codeElm.isEmpty()) {
			contOfbox12c_codeElm = null;
		}
		
		Element box12c_meaningElm = XMLUtil.getChildElement(w2Elm, "box12c_meaning");
		String contOfbox12c_meaningElm = box12c_meaningElm.getTextContent();
		if (contOfbox12c_meaningElm.isEmpty()) {
			contOfbox12c_meaningElm = null;
		}
		
		Element box12d_codeElm = XMLUtil.getChildElement(w2Elm, "box12d_code");
		String contOfbox12d_codeElm = box12d_codeElm.getTextContent();
		if (contOfbox12d_codeElm.isEmpty()) {
			contOfbox12d_codeElm = null;
		}
		
		Element box12d_meaningElm = XMLUtil.getChildElement(w2Elm, "box12d_meaning");
		String contOfbox12d_meaningElm = box12d_meaningElm.getTextContent();
		if (contOfbox12d_meaningElm.isEmpty()) {
			contOfbox12d_meaningElm = null;
		}
		
		Element stat_employeeElm = XMLUtil.getChildElement(w2Elm, "stat_employee");
		String contOfstat_employeeElm = stat_employeeElm.getTextContent();
		if (contOfstat_employeeElm.isEmpty()) {
			contOfstat_employeeElm = null;
		}
		
		Element retirement_planElm = XMLUtil.getChildElement(w2Elm, "retirement_plan");
		String contOfretirement_planElm = retirement_planElm.getTextContent();
		if (contOfretirement_planElm.isEmpty()) {
			contOfretirement_planElm = null;
		}
		
		Element sick_payElm = XMLUtil.getChildElement(w2Elm, "sick_pay");
		String contOfsick_payElm = sick_payElm.getTextContent();
		if (contOfsick_payElm.isEmpty()) {
			contOfsick_payElm = null;
		}
		
		Element box14_codeaElm = XMLUtil.getChildElement(w2Elm, "Box14_codea");
		String contOfBox14_codeaElm = box14_codeaElm.getTextContent();
		if (contOfBox14_codeaElm.isEmpty()) {
			contOfBox14_codeaElm = null;
		}
		
		Element box14_meaningaElm = XMLUtil.getChildElement(w2Elm, "Box14_meaninga");
		String contOfBox14_meaningaElm = box14_meaningaElm.getTextContent();
		if (contOfBox14_meaningaElm.isEmpty()) {
			contOfBox14_meaningaElm = null;
		}
		
		Element box14_codebElm = XMLUtil.getChildElement(w2Elm, "Box14_codeb");
		String contOfBox14_codebElm = box14_codebElm.getTextContent();
		if (contOfBox14_codebElm.isEmpty()) {
			contOfBox14_codebElm = null;
		}
		
		Element box14_meaningbElm = XMLUtil.getChildElement(w2Elm, "Box14_meaningb");
		String contOfBox14_meaningbElm = box14_meaningbElm.getTextContent();
		if (contOfBox14_meaningbElm.isEmpty()) {
			contOfBox14_meaningbElm = null;
		}
		
		Element box14_codecElm = XMLUtil.getChildElement(w2Elm, "Box14_codec");
		String contOfBox14_codecElm = box14_codecElm.getTextContent();
		if (contOfBox14_codecElm.isEmpty()) {
			contOfBox14_codecElm = null;
		}
		
		Element box14_meaningcElm = XMLUtil.getChildElement(w2Elm, "Box14_meaningc");
		String contOfBox14_meaningcElm = box14_meaningcElm.getTextContent();
		if (contOfBox14_meaningcElm.isEmpty()) {
			contOfBox14_meaningcElm = null;
		}
		
		Element state_codeElm = XMLUtil.getChildElement(w2Elm, "state_code");
		String contOfstate_codeElm = state_codeElm.getTextContent();
		if (contOfstate_codeElm.isEmpty()) {
			contOfstate_codeElm = null;
		}
		
		Element sTATE_EINElm = XMLUtil.getChildElement(w2Elm, "STATE_EIN");
		String contOfSTATE_EINElm = sTATE_EINElm.getTextContent();
		if (contOfSTATE_EINElm.isEmpty()) {
			contOfSTATE_EINElm = null;
		}
		
		Element state1_wagesElm = XMLUtil.getChildElement(w2Elm, "state1_wages");
		String contOfstate1_wagesElm = state1_wagesElm.getTextContent();
		if (contOfstate1_wagesElm.isEmpty()) {
			contOfstate1_wagesElm = null;
		}
		
		Element state1_taxElm = XMLUtil.getChildElement(w2Elm, "state1_tax");
		String contOfstate1_taxElm = state1_taxElm.getTextContent();
		if (contOfstate1_taxElm.isEmpty()) {
			contOfstate1_taxElm = null;
		}
		
		Element local1_wagesElm = XMLUtil.getChildElement(w2Elm, "local1_wages");
		String contOflocal1_wagesElm = local1_wagesElm.getTextContent();
		if (contOflocal1_wagesElm.isEmpty()) {
			contOflocal1_wagesElm = null;
		}
		
		Element local1_taxElm = XMLUtil.getChildElement(w2Elm, "local1_tax");
		String contOflocal1_taxElm = local1_taxElm.getTextContent();
		if (contOflocal1_taxElm.isEmpty()) {
			contOflocal1_taxElm = null;
		}
		
		Element locality1Elm = XMLUtil.getChildElement(w2Elm, "locality1");
		String contOflocality1Elm = locality1Elm.getTextContent();
		if (contOflocality1Elm.isEmpty()) {
			contOflocality1Elm = null;
		}
		
		
		w2_Builder.append(contOfControl_NumberElm).append("|").append(contOffederal_einElm).append("|")
				.append(contOfemployer_nameElm).append("|").append(contOfemployer_addressElm).append("|")
				.append(contOfSSNElm).append("|").append(contOfemp_nameElm).append("|").append(contOflast_nameElm)
				.append("|").append(contOfemployee_addressElm).append("|")
				
				.append(contOfwages_tips_compensationElm).append("|").append(contOfFIT_WITHHELDElm).append("|").append(contOfss_wagesElm).append("|")
				.append(contOfss_withheldElm).append("|").append(contOfmed_wagesElm).append("|").append(contOfmed_withheldElm).append("|")
				.append(contOfss_tipsElm).append("|").append(contOfallocated_tipsElm).append("|").append(contOfeic_paymentElm).append("|")
				.append(contOfdependent_careElm).append("|").append(contOfnon_qual_planElm).append("|")
				
				.append(contOfbox12a_codeElm).append("|").append(contOfbox12a_meaningElm).append("|").append(contOfbox12b_codeElm).append("|")
				.append(contOfbox12b_meaningElm).append("|").append(contOfbox12c_codeElm).append("|").append(contOfbox12c_meaningElm).append("|")
				.append(contOfbox12d_codeElm).append("|").append(contOfbox12d_meaningElm).append("|")
				
				.append(contOfstat_employeeElm).append("|").append(contOfretirement_planElm).append("|").append(contOfsick_payElm).append("|")
				
				.append(contOfBox14_codeaElm).append("|").append(contOfBox14_meaningaElm).append("|").append(contOfBox14_codebElm).append("|")
				.append(contOfBox14_meaningbElm).append("|").append(contOfBox14_codecElm).append("|").append(contOfBox14_meaningcElm).append("|")
				
				.append(contOfstate_codeElm).append("|").append(contOfSTATE_EINElm).append("|").append(contOfstate1_wagesElm).append("|")
				.append(contOfstate1_taxElm).append("|").append(contOflocal1_wagesElm).append("|").append(contOflocal1_taxElm).append("|")
				.append(contOflocality1Elm).append("|")
				
				//.append(contOfw2Elm).append("|")
				
				.append(";\n");


		map.put("W2", w2_Builder.toString());
		
		return map;
	}

	/**
	 *
	 * Extract map data
	 *
	 * @param mapDoc             {@link Map<String, String>}
	 * @param outputFileLocation {@link String}
	 * @throws Exception
	 */
	public static void extractMapData(Map<String, String> mapDoc, String outputFileLocation) throws Exception {

		String[] splitDatabase = mapDoc.get("W2").split(Pattern.quote(";"));
		writeCsv("W2", splitDatabase, outputFileLocation);

	}

	/**
	 *
	 * Write csv
	 *
	 * @param csvfile            the csvfile.
	 * @param data               the data.
	 * @param outputFileLocation the output file location.
	 */
	public static void writeCsv(String csvfile, String[] data, String outputFileLocation) {

		try {

			File file = new File(outputFileLocation + "\\" + csvfile + ".csv");
			new File(file.getParent()).mkdirs();
			FileWriter fr = new FileWriter(file, true);
			PrintWriter printWriter = new PrintWriter(fr);

			for (int dataInput = 0; dataInput < data.length; dataInput++) {
				printWriter.write(data[dataInput]);
			}

			printWriter.flush();
			printWriter.close();
			// ZipUtil.pack(new File(outputFileLocation + "\\"), new File(outputFileLocation
			// + ".zip"));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 *
	 * Create tables
	 *
	 * @param connection {@link Connection}
	 * @throws SQLException
	 */
	private static void createTables(Connection connection) throws SQLException {

		String w2TableQuery = "CREATE TABLE IF NOT EXISTS W2 (" 
				+ "Control_Number VARCHAR(100), " + "federal_ein VARCHAR(100), " + "employer_name VARCHAR(100), " + "employer_address VARCHAR(100), "
				+ "SSN VARCHAR(100), " + "emp_name VARCHAR(100), " + "last_name VARCHAR(100), " + "employee_address VARCHAR(100), "
				
				+ "wages_tips_compensation VARCHAR(100), " + "FIT_WITHHELD VARCHAR(100), " + "ss_wages VARCHAR(100), " + "ss_withheld VARCHAR(100), "
				+ "med_wages VARCHAR(100), " + "med_withheld VARCHAR(100), " + "ss_tips VARCHAR(100), " + "allocated_tips VARCHAR(100), " 
				+ "eic_payment VARCHAR(100), " + "dependent_care VARCHAR(100), " + "non_qual_plan VARCHAR(100), "
				
				+ "box12a_code VARCHAR(100), " + "box12a_meaning VARCHAR(100), " + "box12b_code VARCHAR(100), " + "box12b_meaning VARCHAR(100), "
				+ "box12c_code VARCHAR(100), " + "box12c_meaning VARCHAR(100), " + "box12d_code VARCHAR(100), " + "box12d_meaning VARCHAR(100), "
				
				+ "stat_employee VARCHAR(100), " + "retirement_plan VARCHAR(100), " + "sick_pay VARCHAR(100), "
				
				+ "Box14_codea VARCHAR(100), " + "Box14_meaninga VARCHAR(100), " + "Box14_codeb VARCHAR(100), " + "Box14_meaningb VARCHAR(100), "
				+ "Box14_codec VARCHAR(100), " + "Box14_meaningc VARCHAR(100), " 
				
				+ "state_code VARCHAR(100), " + "STATE_EIN VARCHAR(100), " + "state1_wages VARCHAR(100), " + "state1_tax VARCHAR(100), "
				+ "local1_wages VARCHAR(100), " + "local1_tax VARCHAR(100), " + "locality1 VARCHAR(100), "
				
				//+ "ContentsOfW2 VARCHAR(100), "
				
				+ "PRIMARY KEY (Control_Number)" + ")";

		try (PreparedStatement statement = connection.prepareStatement(w2TableQuery)) {
			statement.executeUpdate();

		}

	}

	/**
	 *
	 * Extract map data for db
	 *
	 * @param conn   {@link String}
	 * @param mapDoc {@link Map<String, String>}
	 * @throws Exception
	 */
	public static void extractMapDataForDb(Connection conn, Map<String, String> mapDoc) throws Exception {

		String[] splitDatabase = mapDoc.get("W2").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "W2", splitDatabase);

	}

	/**
	 *
	 * Insert data in DB
	 *
	 * @param conn        {@link Connection}
	 * @param dbTableName {@link String}
	 * @param data        {@link String[]}
	 * @throws Exception
	 */
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
