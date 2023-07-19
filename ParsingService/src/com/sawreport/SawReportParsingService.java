package com.sawreport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.lti.recast.XMLParsing.SampleParsingService;
import com.lti.recast.XMLParsing.XMLUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 
 * @author 10719785
 * 
 *         SawReportParsingService class does the following:
 * 
 *         1. Establishes a connection to MySQL database 2. Reads XML input file
 *         3. Parse XML file into a map data structure 4. Extracts map data for
 *         CSV Parsing 5. Writes map data to CSV files 6. Creates MySQL tables
 *         according to XML files 7. Extracts map data for MySQL database 8.
 *         Inserts data into MySQL database
 * 
 * 
 *         SawReportParsingService class works with XML files in the LevelBased
 *         and MasterDetails Folders
 *
 */

@Component
public class SawReportParsingService {


	private static Connection connection;
	private Map<String, String> parse;
	private Workbook workbook;
    private Sheet reportSheet;
    private Sheet criteriaSheet;
    private Sheet interactiveOptionsSheet;
    private Sheet viewsSheet;
    private Sheet promptsSheet;

    public SawReportParsingService() {
        workbook = new XSSFWorkbook();

        reportSheet = workbook.createSheet("Report");
        criteriaSheet = workbook.createSheet("Criteria");
        interactiveOptionsSheet = workbook.createSheet("InteractionOptions");
        viewsSheet = workbook.createSheet("Views");
        promptsSheet = workbook.createSheet("Prompts");

    }

	public void parse(Document document, String filePath, String outputFileLocation) {
		try {
			parse = parseXmlDoc(document);
			extractMapData(parse, filePath);
			Connection connection = SampleParsingService.databaseConnection();
			extractMapDataForDb(connection, parse);

		} catch (Exception e) {
			System.out.println("Exception occurred");
			System.out.println("Exception Message: " + e.getMessage());
		}

		for (Map.Entry<String, String> entry : parse.entrySet()) {
			System.out.println(entry.getKey());
			System.out.println(entry.getValue());
		}
	}
	/**
	 * 
	 * @param doc
	 * @return
	 * @throws Exception
	 */
	public static Map<String, String> parseXmlDoc(Document doc) throws Exception {

		Map<String, String> map = new HashMap<>();

		// Reading XML data from Report child node
		Element sawReportElm = doc.getDocumentElement();
		// .replaceAll("\\s+", ""); removes all trailing whitespaces that may occur from
		// xml formatting
		String xmlVersion = sawReportElm.getAttribute("xmlVersion").replaceAll("\\s+", "");
		String xmlnsSaw = sawReportElm.getAttribute("xmlns:saw").replaceAll("\\s+", "");
		String xmlnsSawx = sawReportElm.getAttribute("xmlns:sawx").replaceAll("\\s+", "");
		String xmlnsXsd = sawReportElm.getAttribute("xmlns:xsd").replaceAll("\\s+", "");
		String xmlnsXsi = sawReportElm.getAttribute("xmlns:xsi").replaceAll("\\s+", "");
		String contOfsawReportElm = sawReportElm.getTextContent().replaceAll("\\s+", "");

		StringBuilder reportBuilder = new StringBuilder();
		StringBuilder interactionOptionsBuilder = new StringBuilder();
		StringBuilder viewsBuilder = new StringBuilder();
		StringBuilder promptsBuilder = new StringBuilder();
		StringBuilder criteriaBuilder = new StringBuilder();

		boolean isHeaderPresent = false;
		
		if(!isHeaderPresent) {
		// Creating Column names for InteractionOptions CSV file
		interactionOptionsBuilder.append("addRemoveValues").append("|").append("calcitemoperations").append("|")
				.append("drill").append("|").append("groupOperations").append("|").append("inclexClColumns").append("|")
				.append("moveColumns").append("|").append("showHideRunningSum").append("|").append("showHideSubtotal")
				.append("|").append("sortColumns").append("|").append("contOfsawInteractionOptionsElm").append("|")
				.append(";\n");

		// Report Column names in Report node CSV
		reportBuilder.append("xmlVersion").append("|").append("xmlnsSaw").append("|").append("xmlnsSawx").append("|")
				.append("xmlnsXsd").append("|").append("xmlnsXsi").append("|").append("contOfsawReportElm").append("|")
				.append(";\n");


		viewsBuilder.append("currentView").append("|").append("autoPreview").append("|").append("includeName")
				.append("|").append("viewsName").append("|").append("scrollingEnabled").append("|")
				.append("showMeasureValue").append("|").append("startedDisplay").append("|").append("viewsXsiType")
				.append("|").append("viewName").append("|").append("borderColor").append("|").append("borderPosition")
				.append("|").append("borderStyle").append("|").append("viewsWrapText").append("|").append("edges")
				.append("|").append("edgeLayers").append("|").append("animateOnDisplay").append("|").append("mode")
				.append("|").append("renderFormat").append("|").append("subtype").append("|").append("type").append("|")
				.append("barStyle").append("|").append("bubblePercentSize").append("|").append("effect").append("|")
				.append("fillStyle").append("|").append("lineStyle").append("|").append("scatterStyle").append("|")
				.append("height").append("|").append("width").append("|").append("display").append("|").append("label")
				.append("|").append("position").append("|").append("transparentBackground").append("|")
				.append("valueAs").append("|").append("viewsMode").append("|").append("canvasText").append("|")
				.append("showMeasureLabelsOnCategory").append("|").append("viewsColumnID").append("|")
				.append("measureType").append("|").append("measuresColumnID").append("|").append("seriesColumnID")
				.append("|").append("legendPosition").append("|").append("transparentFill").append("|")
				.append("fontSize").append("|").append("axisFormat").append("|").append("seriesFormat").append("|")
				.append("funnelProperties").append("|").append("measuresList").append("|").append("treeBinType")
				.append("|").append("treeNumBins").append("|").append("treeColumnID").append("|").append("treeRampItem")
				.append("|").append("treeHeight").append("|").append("showGroupLabel").append("|")
				.append("treeShowLegend").append("|").append("showTileLabel").append("|").append("treeWidth")
				.append("|").append("heatBinType").append("|").append("heatNumBins").append("|").append("heatColumnID")
				.append("|").append("heatRampItem").append("|").append("heatShowLegend").append("|").append(";\n");

		promptsBuilder.append("scope").append("|").append("subjectArea").append(";\n");

		// Report values in Report node CSV
		reportBuilder.append(xmlVersion).append("|").append(xmlnsSaw).append("|").append(xmlnsSawx).append("|")
				.append(xmlnsXsd).append("|").append(xmlnsXsi).append("|").append(contOfsawReportElm).append("|")
				.append(";\n");

		// Reading XML data from Criteria child node


		// Criteria Column names in Criteria node CSV
		criteriaBuilder.append("PrimaryXsiType").append("|").append("SecondaryXsiType").append("|")
				.append("SubjectArea").append("|").append("WithinHierarchy").append("|").append("ContOfsawCriteriaElm")
				.append("|").append("Op").append("|").append("Path").append("|").append("Name").append("|")
				.append("ContOfFilterSawxExprElm").append("|").append("ColumnID").append("|").append("ExprXsiType")
				.append("|").append("ContOfsawxExprElm").append("|").append("ContOfsawDisplayFormatElm").append("|")
				.append("Suppress").append("|").append("FontColor").append("|").append("WrapText").append("|")
				.append("ContOfsawFormatSpecElm").append("|").append("DataFormatXsiType").append("|").append("Commas")
				.append("|").append("NegativeType").append("|").append("MinDigits").append("|").append("MaxDigits")
				.append("|").append("ContOfsawTableHeadingElm").append("|").append("fmt").append("|")
				.append("ContOfsawCaptionElm").append("|").append("ContOfsawTextElm").append("|")
				.append("ContOfColHeadSawDisplayFormatElm").append("|").append("ContOfColHeadSawFormatSpecElm")
				.append("|").append("ColumnFMT").append("|").append("ContOfColHeadSawCaptionElm").append("|")
				.append("ContOfColHeadSawTextElm").append("|").append("ContOfsawColumnsElm").append("|")

				// Start Filter Append from here
				.append(";\n");
		isHeaderPresent = true;
		}
		String exprXsiType = null;
		String contOfsawxExprElm = null;
		String dataFormatXsiType = null;
		String commas = null;
		String negativeType = null;
		String minDigits = null;
		String maxDigits = null;
		String contOfsawTextElm = null;
		String contOfColHeadSawDisplayFormatElm = null;
		String contOfColHeadSawFormatSpecElm = null;
		String colHeadFmt = null;
		String contOfColHeadSawCaptionElm = null;
		String contOfColHeadSawTextElm = null;

		String secondaryXsiType = null;
		// String columnXsiType = null;
		String columnID = null;
		String contOfsawDisplayFormatElm = null;
		String suppress = null;
		String fontColor = null;
		String wrapText = null;
		String contOfsawFormatSpecElm = null;
		String contOfsawTableHeadingElm = null;
		String fmt = null;
		String contOfsawCaptionElm = null;
		// String filterExprXsiType = null;
		String name = null;
		String path = null;
		String op = null;
		String contOfFilterSawxExprElm = null;

		String xsiTypeInnerExpr = null;
		String contOfsawxExprElmInner = null;

		Element sawCriteriaElm = XMLUtil.getChildElement(sawReportElm, "saw:criteria");

		String subjectArea = sawCriteriaElm.getAttribute("subjectArea").replaceAll("\\s+", "");
		String withinHierarchy = sawCriteriaElm.getAttribute("withinHierarchy").replaceAll("\\s+", "");
		// String criteriaXsiType= sawCriteriaElm.getAttribute("xsi:type");
		String primaryXsiType = sawCriteriaElm.getAttribute("xsi:type").replaceAll("\\s+", "");
		String contOfsawCriteriaElm = sawCriteriaElm.getTextContent().replaceAll("\\s+", "");

		Element sawColumnsElm = XMLUtil.getChildElement(sawCriteriaElm, "saw:columns");

		String contOfsawColumnsElm = sawColumnsElm.getTextContent().replaceAll("\\s+", "");
		if (contOfsawColumnsElm.isEmpty()) {
			contOfsawColumnsElm = null;
		}
		List<Element> sawColumnNdList = XMLUtil.getChildElements(sawColumnsElm, "saw:column");
		for (Element sawColumnElm : sawColumnNdList) {

			secondaryXsiType = sawColumnElm.getAttribute("xsi:type").replaceAll("\\s+", "");
			if (secondaryXsiType.isEmpty()) {
				secondaryXsiType = null;
			}
			columnID = sawColumnElm.getAttribute("columnID").replaceAll("\\s+", "");
			if (columnID.isEmpty()) {
				columnID = null;
			}

			List<Element> sawColumnFormulaNdList = XMLUtil.getChildElements(sawColumnElm, "saw:columnFormula");
			for (Element sawColumnFormulaElm : sawColumnFormulaNdList) {

				Element sawxExprElm = XMLUtil.getChildElement(sawColumnFormulaElm, "sawx:expr");
				exprXsiType = sawxExprElm.getAttribute("xsi:type").replaceAll("\\s+", "");
				if (exprXsiType.isEmpty()) {
					exprXsiType = null;
				}
				contOfsawxExprElm = sawxExprElm.getTextContent().replaceAll("\\s+", "");
				if (contOfsawxExprElm.isEmpty()) {
					contOfsawxExprElm = null;
				}
			}
			Element sawDisplayFormatElm = XMLUtil.getChildElement(sawColumnElm, "saw:displayFormat");

			contOfsawDisplayFormatElm = sawDisplayFormatElm.getTextContent().replaceAll("\\s+", "");
			if (contOfsawDisplayFormatElm.isEmpty()) {
				contOfsawDisplayFormatElm = null;
			}
			Element sawFormatSpecElm = XMLUtil.getChildElement(sawDisplayFormatElm, "saw:formatSpec");

			fontColor = sawFormatSpecElm.getAttribute("fontColor").replaceAll("\\s+", "");
			if (fontColor.isEmpty()) {
				fontColor = null;
			}
			suppress = sawFormatSpecElm.getAttribute("suppress").replaceAll("\\s+", "");
			if (suppress.isEmpty()) {
				suppress = null;
			}
			wrapText = sawFormatSpecElm.getAttribute("wrapText").replaceAll("\\s+", "");
			if (wrapText.isEmpty()) {
				wrapText = null;
			}
			contOfsawFormatSpecElm = sawFormatSpecElm.getTextContent().replaceAll("\\s+", "");
			if (contOfsawFormatSpecElm.isEmpty()) {
				contOfsawFormatSpecElm = null;
			}
			List<Element> sawDataFormatNdList = XMLUtil.getChildElements(sawFormatSpecElm, "saw:dataFormat");
			for (Element sawDataFormatElm : sawDataFormatNdList) {

				commas = sawDataFormatElm.getAttribute("commas").replaceAll("\\s+", "");
				if (commas.isEmpty()) {
					commas = null;
				}
				maxDigits = sawDataFormatElm.getAttribute("maxDigits").replaceAll("\\s+", "");
				if (maxDigits.isEmpty()) {
					maxDigits = null;
				}
				minDigits = sawDataFormatElm.getAttribute("minDigits").replaceAll("\\s+", "");
				if (minDigits.isEmpty()) {
					minDigits = null;
				}
				negativeType = sawDataFormatElm.getAttribute("negativeType").replaceAll("\\s+", "");
				if (negativeType.isEmpty()) {
					negativeType = null;
				}
				dataFormatXsiType = sawDataFormatElm.getAttribute("xsi:type").replaceAll("\\s+", "");
				if (dataFormatXsiType.isEmpty()) {
					dataFormatXsiType = null;
				}

			}
			Element sawTableHeadingElm = XMLUtil.getChildElement(sawColumnElm, "saw:tableHeading");

			contOfsawTableHeadingElm = sawTableHeadingElm.getTextContent().replaceAll("\\s+", "");
			if (contOfsawTableHeadingElm.isEmpty()) {
				contOfsawTableHeadingElm = null;
			}
			Element sawCaptionElm = XMLUtil.getChildElement(sawTableHeadingElm, "saw:caption");

			fmt = sawCaptionElm.getAttribute("fmt").replaceAll("\\s+", "");
			if (fmt.isEmpty()) {
				fmt = null;
			}
			contOfsawCaptionElm = sawCaptionElm.getTextContent().replaceAll("\\s+", "");
			if (contOfsawCaptionElm.isEmpty()) {
				contOfsawCaptionElm = null;
			}
			List<Element> sawTextNdList = XMLUtil.getChildElements(sawCaptionElm, "saw:text");
			for (Element sawTextElm : sawTextNdList) {

				contOfsawTextElm = sawTextElm.getTextContent().replaceAll("\\s+", "");
				if (contOfsawTextElm.isEmpty()) {
					contOfsawTextElm = null;
				}
			}
			List<Element> sawColumnHeadingNdList = XMLUtil.getChildElements(sawColumnElm, "saw:columnHeading");
			for (Element sawColumnHeadingElm : sawColumnHeadingNdList) {

				Element colHeadSawDisplayFormatElm = XMLUtil.getChildElement(sawColumnHeadingElm, "saw:displayFormat");

				contOfColHeadSawDisplayFormatElm = colHeadSawDisplayFormatElm.getTextContent().replaceAll("\\s+", "");
				if (contOfColHeadSawDisplayFormatElm.isEmpty()) {
					contOfColHeadSawDisplayFormatElm = null;
				}
				Element colHeadSawFormatSpecElm = XMLUtil.getChildElement(colHeadSawDisplayFormatElm, "saw:formatSpec");

				contOfColHeadSawFormatSpecElm = colHeadSawFormatSpecElm.getTextContent().replaceAll("\\s+", "");
				if (contOfColHeadSawFormatSpecElm.isEmpty()) {
					contOfColHeadSawFormatSpecElm = null;
				}
				Element colHeadSawCaptionElm = XMLUtil.getChildElement(sawColumnHeadingElm, "saw:caption");

				colHeadFmt = colHeadSawCaptionElm.getAttribute("fmt").replaceAll("\\s+", "");
				if (colHeadFmt.isEmpty()) {
					colHeadFmt = null;
				}
				contOfColHeadSawCaptionElm = colHeadSawCaptionElm.getTextContent().replaceAll("\\s+", "");
				if (contOfColHeadSawCaptionElm.isEmpty()) {
					contOfColHeadSawCaptionElm = null;
				}
				List<Element> colHeadSawTextNdList = XMLUtil.getChildElements(colHeadSawCaptionElm, "saw:text");
				for (Element colHeadSawTextElm : colHeadSawTextNdList) {

					contOfColHeadSawTextElm = colHeadSawTextElm.getTextContent().replaceAll("\\s+", "");
					if (contOfColHeadSawTextElm.isEmpty()) {
						contOfColHeadSawTextElm = null;
					}
				}
			}

			criteriaBuilder.append(primaryXsiType).append("|").append(secondaryXsiType).append("|").append(subjectArea)
					.append("|").append(withinHierarchy).append("|").append(contOfsawCriteriaElm).append("|").append(op)
					.append("|").append(path).append("|").append(name).append("|").append(contOfFilterSawxExprElm)
					.append("|").append(columnID).append("|").append(exprXsiType).append("|").append(contOfsawxExprElm)
					.append("|").append(contOfsawDisplayFormatElm).append("|").append(suppress).append("|")
					.append(fontColor).append("|").append(wrapText).append("|").append(contOfsawFormatSpecElm)
					.append("|").append(dataFormatXsiType).append("|").append(commas).append("|").append(negativeType)
					.append("|").append(minDigits).append("|").append(maxDigits).append("|")
					.append(contOfsawTableHeadingElm).append("|").append(fmt).append("|").append(contOfsawCaptionElm)
					.append("|").append(contOfsawTextElm).append("|").append(contOfColHeadSawDisplayFormatElm)
					.append("|").append(contOfColHeadSawFormatSpecElm).append("|").append(colHeadFmt).append("|")
					.append(contOfColHeadSawCaptionElm).append("|").append(contOfColHeadSawTextElm).append("|")
					.append(contOfsawColumnsElm).append("|")
					// .append(filterExprXsiType).append("|")
					.append(";\n");

		}

		List<Element> sawFilterNdList = XMLUtil.getChildElements(sawCriteriaElm, "saw:filter");
		for (Element sawFilterElm : sawFilterNdList) {

			Element sawxExprElm = XMLUtil.getChildElement(sawFilterElm, "sawx:expr");

			op = sawxExprElm.getAttribute("op").replaceAll("\\s+", "");
			name = sawxExprElm.getAttribute("name").replaceAll("\\s+", "");
			path = sawxExprElm.getAttribute("path").replaceAll("\\s+", "");
			secondaryXsiType = sawxExprElm.getAttribute("xsi:type").replaceAll("\\s+", "");
			contOfFilterSawxExprElm = sawxExprElm.getTextContent().replaceAll("\\s+", "");
			if (contOfFilterSawxExprElm.isEmpty()) {
				contOfFilterSawxExprElm = null;
			}

			Element sawxExprElmInner = XMLUtil.getChildElement(sawxExprElm, "sawx:expr");
			xsiTypeInnerExpr = sawxExprElmInner.getAttribute("xsi:type").replaceAll("\\s+", "");
			if (xsiTypeInnerExpr.isEmpty()) {
				xsiTypeInnerExpr = null;
			}

			contOfsawxExprElmInner = sawxExprElmInner.getTextContent().replaceAll("\\s+", "");
			if (contOfsawxExprElmInner.isEmpty()) {
				contOfsawxExprElmInner = null;
			}

			// columnXsiType = null;
			columnID = null;
			exprXsiType = null;
			contOfsawxExprElm = null;
			contOfsawDisplayFormatElm = null;
			suppress = null;
			fontColor = null;
			wrapText = null;
			contOfsawFormatSpecElm = null;
			dataFormatXsiType = null;
			commas = null;
			negativeType = null;
			minDigits = null;
			maxDigits = null;
			contOfsawTableHeadingElm = null;
			fmt = null;
			contOfsawCaptionElm = null;
			contOfsawTextElm = null;
			contOfColHeadSawDisplayFormatElm = null;
			contOfColHeadSawFormatSpecElm = null;
			colHeadFmt = null;
			contOfColHeadSawCaptionElm = null;
			contOfColHeadSawTextElm = null;
			contOfsawColumnsElm = null;

			criteriaBuilder.append(primaryXsiType).append("|").append(secondaryXsiType).append("|").append(subjectArea)
					.append("|").append(withinHierarchy).append("|").append(contOfsawCriteriaElm).append("|").append(op)
					.append("|").append(path).append("|").append(name).append("|").append(contOfFilterSawxExprElm)
					.append("|").append(columnID).append("|").append(exprXsiType).append("|").append(contOfsawxExprElm)
					.append("|").append(contOfsawDisplayFormatElm).append("|").append(suppress).append("|")
					.append(fontColor).append("|").append(wrapText).append("|").append(contOfsawFormatSpecElm)
					.append("|").append(dataFormatXsiType).append("|").append(commas).append("|").append(negativeType)
					.append("|").append(minDigits).append("|").append(maxDigits).append("|")
					.append(contOfsawTableHeadingElm).append("|").append(fmt).append("|").append(contOfsawCaptionElm)
					.append("|").append(contOfsawTextElm).append("|").append(contOfColHeadSawDisplayFormatElm)
					.append("|").append(contOfColHeadSawFormatSpecElm).append("|").append(colHeadFmt).append("|")
					.append(contOfColHeadSawCaptionElm).append("|").append(contOfColHeadSawTextElm).append("|")
					.append(contOfsawColumnsElm).append("|")
					// .append(filterExprXsiType).append("|")
					.append(";\n");

		}

		// Reading XML data from InteractionOptions child node
		Element sawInteractionOptionsElm = XMLUtil.getChildElement(sawReportElm, "saw:interactionOptions");

		String addRemoveValues = sawInteractionOptionsElm.getAttribute("addremovevalues").replaceAll("\\s+", "");
		if (addRemoveValues.isEmpty()) {
			addRemoveValues = null;
		}

		String calcitemoperations = sawInteractionOptionsElm.getAttribute("calcitemoperations").replaceAll("\\s+", "");
		if (calcitemoperations.isEmpty()) {
			calcitemoperations = null;
		}

		String drill = sawInteractionOptionsElm.getAttribute("drill").replaceAll("\\s+", "");
		if (drill.isEmpty()) {
			drill = null;
		}

		String groupOperations = sawInteractionOptionsElm.getAttribute("groupoperations").replaceAll("\\s+", "");
		if (groupOperations.isEmpty()) {
			groupOperations = null;
		}

		String inclexClColumns = sawInteractionOptionsElm.getAttribute("inclexclcolumns").replaceAll("\\s+", "");
		if (inclexClColumns.isEmpty()) {
			inclexClColumns = null;
		}

		String moveColumns = sawInteractionOptionsElm.getAttribute("movecolumns").replaceAll("\\s+", "");
		if (moveColumns.isEmpty()) {
			moveColumns = null;
		}

		String showHideRunningSum = sawInteractionOptionsElm.getAttribute("showhiderunningsum").replaceAll("\\s+", "");
		if (showHideRunningSum.isEmpty()) {
			showHideRunningSum = null;
		}

		String showHideSubtotal = sawInteractionOptionsElm.getAttribute("showhidesubtotal").replaceAll("\\s+", "");
		if (showHideSubtotal.isEmpty()) {
			showHideSubtotal = null;
		}

		String sortColumns = sawInteractionOptionsElm.getAttribute("sortcolumns").replaceAll("\\s+", "");
		if (sortColumns.isEmpty()) {
			sortColumns = null;
		}

		String contOfsawInteractionOptionsElm = sawInteractionOptionsElm.getTextContent().replaceAll("\\s+", "");
		if (contOfsawInteractionOptionsElm.isEmpty()) {
			contOfsawInteractionOptionsElm = null;
		}

		

		// InteractionOption Values in CSV
		interactionOptionsBuilder.append(addRemoveValues).append("|").append(calcitemoperations).append("|")
				.append(drill).append("|").append(groupOperations).append("|").append(inclexClColumns).append("|")
				.append(moveColumns).append("|").append(showHideRunningSum).append("|").append(showHideSubtotal)
				.append("|").append(sortColumns).append("|").append(contOfsawInteractionOptionsElm).append("|")
				.append(";\n");


		String currentView = null;
		String autoPreview = null;
		String includeName = null;
		String viewsName = null;

		StringBuilder viewNameBuilder = new StringBuilder();

		String scrollingEnabled = null;
		String showMeasureValue = null;
		String startedDisplay = null;
		String viewsXsiType = null;

		// from a list of CV cells
		String viewName = null;

		String borderColor = null;
		String borderPosition = null;
		String borderStyle = null;
		String viewsWrapText = null;

		StringBuilder edgeBuilder = new StringBuilder();

		// String prefix = null;
		String axis = null;
		String showColumnHeader = null;

		StringBuilder edgeLayerBuilder = new StringBuilder();
		// String innerPrefix = null;

		String edgeLayerType = null;
		String edgeLayerColumnID = null;

		String animateOnDisplay = null;
		String mode = null;
		String renderFormat = null;
		String subtype = null;
		String type = null;

		String barStyle = null;
		String bubblePercentSize = null;
		String effect = null;
		String fillStyle = null;
		String lineStyle = null;
		String scatterStyle = null;

		String height = null;
		String width = null;

		String display = null;
		String label = null;
		String position = null;
		String transparentBackground = null;
		String valueAs = null;

		String viewsMode = null;

		String canvasText = null;

		String showMeasureLabelsOnCategory = null;

		// multiple columnID values
		String viewsColumnID = null;

		String measureType = null;

		String measuresColumnID = null;

		String seriesColumnID = null;

		String legendPosition = null;
		String transparentFill = null;

		String fontSize = null;

		StringBuilder axisFormatBuilder = new StringBuilder();

//		String axisFormat = null;
		String axisMode = null;

		String defaultTicks = null;
		String majorTicks = null;
		String showMajorTicks = null;

		String rotate = null;
		String rotateLabels = null;
		String skipLabels = null;
		String labelAbbreviation = null;

		String textFont = null;
//		String textAbbreviation = null;

		StringBuilder seriesBuilder = new StringBuilder();
		String seriesName = null;

		String seriesPosition = null;

		String visualLineStyle = null;
		String lineWidth = null;
		String symbol = null;

		StringBuilder funnelPropertiesBuilder = new StringBuilder();

		String color = null;
		String labelType = null;
		String rangeType = null;

		String captionID = null;

		String captionText = null;

		String rangeXsiType = null;
		String rangeText = null;

		StringBuilder measuresListBuilder = new StringBuilder();

		String innerMeasureColumnID = null;
		String visualType = null;

		String treeBinType = null;
		String treeNumBins = null;

		String treeColumnID = null;

		StringBuilder treeRampBuilder = new StringBuilder();
		String treeRampId = null;

		String gClass = null;
		String gFill = null;

		String treeHeight = null;
		String showGroupLabel = null;
		String treeShowLegend = null;
		String showTileLabel = null;
		String treeWidth = null;

		String heatBinType = null;
		String heatNumBins = null;

		String heatColumnID = null;

		StringBuilder heatRampBuilder = new StringBuilder();

		String heatRampId = null;

		String gClass2 = null;
		String gFill2 = null;

		String heatShowLegend = null;

		List<Element> sawViewsNdList = XMLUtil.getChildElements(sawReportElm, "saw:views");
		for (Element sawViewsElm : sawViewsNdList) {

			currentView = sawViewsElm.getAttribute("currentView");
			if (currentView.isEmpty()) {
				currentView = null;
			}

			List<Element> sawViewNdList = XMLUtil.getChildElements(sawViewsElm, "saw:view");
			for (Element sawViewElm : sawViewNdList) {

				autoPreview = sawViewElm.getAttribute("autoPreview");
				if (autoPreview.isEmpty()) {
					autoPreview = null;
				}

				includeName = sawViewElm.getAttribute("includeName");
				if (includeName.isEmpty()) {
					includeName = null;
				}

				viewsName = sawViewElm.getAttribute("name");
				if (viewsName.isEmpty()) {
					viewsName = null;
				}

				scrollingEnabled = sawViewElm.getAttribute("scrollingEnabled");
				if (scrollingEnabled.isEmpty()) {
					scrollingEnabled = null;
				}

				showMeasureValue = sawViewElm.getAttribute("showMeasureValue");
				if (showMeasureValue.isEmpty()) {
					showMeasureValue = null;
				}

				startedDisplay = sawViewElm.getAttribute("startedDisplay");
				if (startedDisplay.isEmpty()) {
					startedDisplay = null;
				}

				viewsXsiType = sawViewElm.getAttribute("xsi:type");
				if (viewsXsiType.isEmpty()) {
					viewsXsiType = null;
				}

				List<Element> sawCvTableNdList = XMLUtil.getChildElements(sawViewElm, "saw:cvTable");
				for (Element sawCvTableElm : sawCvTableNdList) {

					List<Element> sawCvRowNdList = XMLUtil.getChildElements(sawCvTableElm, "saw:cvRow");

					for (Element sawCvRowElm : sawCvRowNdList) {
						viewNameBuilder.append("{");
						List<Element> sawCvCellNdList = XMLUtil.getChildElements(sawCvRowElm, "saw:cvCell");
						for (Element sawCvCellElm : sawCvCellNdList) {

							viewName = sawCvCellElm.getAttribute("viewName");
							if (viewName.isEmpty()) {
								viewName = null;
							}

							else {
								viewNameBuilder.append(viewName).append(", ");
							}

						}
						viewNameBuilder.append("}, ");
					}
				}

				List<Element> sawTitleNdList = XMLUtil.getChildElements(sawViewElm, "saw:title");
				for (Element sawTitleElm : sawTitleNdList) {

					List<Element> sawDisplayFormatNdList = XMLUtil.getChildElements(sawTitleElm, "saw:displayFormat");
					for (Element sawDisplayFormatElm : sawDisplayFormatNdList) {

						Element sawFormatSpecElm = XMLUtil.getChildElement(sawDisplayFormatElm, "saw:formatSpec");

						borderColor = sawFormatSpecElm.getAttribute("borderColor");
						if (borderColor.isEmpty()) {
							borderColor = null;
						}

						borderPosition = sawFormatSpecElm.getAttribute("borderPosition");
						if (borderPosition.isEmpty()) {
							borderPosition = null;
						}

						borderStyle = sawFormatSpecElm.getAttribute("borderStyle");
						if (borderStyle.isEmpty()) {
							borderStyle = null;
						}

						viewsWrapText = sawFormatSpecElm.getAttribute("wrapText");
						if (viewsWrapText.isEmpty()) {
							viewsWrapText = null;
						}
					}
				}
				List<Element> sawEdgesNdList = XMLUtil.getChildElements(sawViewElm, "saw:edges");
				for (Element sawEdgesElm : sawEdgesNdList) {

					List<Element> sawEdgeNdList = XMLUtil.getChildElements(sawEdgesElm, "saw:edge");
					for (Element sawEdgeElm : sawEdgeNdList) {

						axis = sawEdgeElm.getAttribute("axis");
						if (axis.isEmpty()) {
							axis = null;
						}

						showColumnHeader = sawEdgeElm.getAttribute("showColumnHeader");
						if (showColumnHeader.isEmpty()) {
							showColumnHeader = null;
						}

//						prefix = sawEdgeElm.getAttribute("prefix");
//						if (prefix.isEmpty()) {
//							prefix = null;
//						}

						edgeBuilder.append("{").append(axis).append(", ").append(showColumnHeader).append("}, ");

						List<Element> sawEdgeLayersNdList = XMLUtil.getChildElements(sawEdgeElm, "saw:edgeLayers");
						for (Element sawEdgeLayersElm : sawEdgeLayersNdList) {

							List<Element> sawEdgeLayerNdList = XMLUtil.getChildElements(sawEdgeLayersElm,
									"saw:edgeLayer");
							for (Element sawEdgeLayerElm : sawEdgeLayerNdList) {

//								innerPrefix = sawEdgeLayerElm.getAttribute("prefix");
//								if (innerPrefix.isEmpty()) {
//									innerPrefix = null;
//								}

								edgeLayerType = sawEdgeLayerElm.getAttribute("type");
								if (edgeLayerType.isEmpty()) {
									edgeLayerType = null;
								}

								edgeLayerColumnID = sawEdgeLayerElm.getAttribute("columnID");
								if (edgeLayerColumnID.isEmpty()) {
									edgeLayerColumnID = null;
								}

								edgeLayerBuilder.append("{").append(edgeLayerType).append(", ")
										.append(edgeLayerColumnID).append("}, ");

							}
						}
					}

				}

				List<Element> sawDisplayNdList = XMLUtil.getChildElements(sawViewElm, "saw:display");
				for (Element sawDisplayElm : sawDisplayNdList) {

					animateOnDisplay = sawDisplayElm.getAttribute("animateOnDisplay");
					if (animateOnDisplay.isEmpty()) {
						animateOnDisplay = null;
					}

					mode = sawDisplayElm.getAttribute("mode");
					if (mode.isEmpty()) {
						mode = null;
					}

					renderFormat = sawDisplayElm.getAttribute("renderFormat");
					if (renderFormat.isEmpty()) {
						renderFormat = null;
					}

					subtype = sawDisplayElm.getAttribute("subtype");
					if (subtype.isEmpty()) {
						subtype = null;
					}

					type = sawDisplayElm.getAttribute("type");
					if (type.isEmpty()) {
						type = null;
					}

					Element sawStyleElm = XMLUtil.getChildElement(sawDisplayElm, "saw:style");

					barStyle = sawStyleElm.getAttribute("barStyle");
					if (barStyle.isEmpty()) {
						barStyle = null;
					}

					bubblePercentSize = sawStyleElm.getAttribute("bubblePercentSize");
					if (bubblePercentSize.isEmpty()) {
						bubblePercentSize = null;
					}

					effect = sawStyleElm.getAttribute("effect");
					if (effect.isEmpty()) {
						effect = null;
					}

					fillStyle = sawStyleElm.getAttribute("fillStyle");
					if (fillStyle.isEmpty()) {
						fillStyle = null;
					}

					lineStyle = sawStyleElm.getAttribute("lineStyle");
					if (lineStyle.isEmpty()) {
						lineStyle = null;
					}

					scatterStyle = sawStyleElm.getAttribute("scatterStyle");
					if (scatterStyle.isEmpty()) {
						scatterStyle = null;
					}

				}
				List<Element> sawCanvasFormatNdList = XMLUtil.getChildElements(sawViewElm, "saw:canvasFormat");
				for (Element sawCanvasFormatElm : sawCanvasFormatNdList) {

					height = sawCanvasFormatElm.getAttribute("height");
					if (height.isEmpty()) {
						height = null;
					}

					width = sawCanvasFormatElm.getAttribute("width");
					if (width.isEmpty()) {
						width = null;
					}

					Element sawDataLabelsElm = XMLUtil.getChildElement(sawCanvasFormatElm, "saw:dataLabels");

					display = sawDataLabelsElm.getAttribute("display");
					if (display.isEmpty()) {
						display = null;
					}

					label = sawDataLabelsElm.getAttribute("label");
					if (label.isEmpty()) {
						label = null;
					}

					position = sawDataLabelsElm.getAttribute("position");
					if (position.isEmpty()) {
						position = null;
					}

					transparentBackground = sawDataLabelsElm.getAttribute("transparentBackground");
					if (transparentBackground.isEmpty()) {
						transparentBackground = null;
					}

					valueAs = sawDataLabelsElm.getAttribute("valueAs");
					if (valueAs.isEmpty()) {
						valueAs = null;
					}

					Element sawTitleElm = XMLUtil.getChildElement(sawCanvasFormatElm, "saw:title");

					viewsMode = sawTitleElm.getAttribute("mode");
					if (viewsMode.isEmpty()) {
						viewsMode = null;
					}

					Element sawCaptionElm = XMLUtil.getChildElement(sawTitleElm, "saw:caption");

					List<Element> sawTextNdList = XMLUtil.getChildElements(sawCaptionElm, "saw:text");
					for (Element sawTextElm : sawTextNdList) {

						canvasText = sawTextElm.getTextContent();
						if (canvasText.isEmpty()) {
							canvasText = null;
						}
					}
				}
				List<Element> sawSelectionsNdList = XMLUtil.getChildElements(sawViewElm, "saw:selections");
				for (Element sawSelectionsElm : sawSelectionsNdList) {

					List<Element> sawCategoriesNdList = XMLUtil.getChildElements(sawSelectionsElm, "saw:categories");
					for (Element sawCategoriesElm : sawCategoriesNdList) {

						List<Element> sawCategoryNdList = XMLUtil.getChildElements(sawCategoriesElm, "saw:category");
						for (Element sawCategoryElm : sawCategoryNdList) {

							Element sawColumnRefElm = XMLUtil.getChildElement(sawCategoryElm, "saw:columnRef");

							viewsColumnID = sawColumnRefElm.getAttribute("columnID");
							if (viewsColumnID.isEmpty()) {
								viewsColumnID = null;
							}
						}
					}
					Element sawMeasuresElm = XMLUtil.getChildElement(sawSelectionsElm, "saw:measures");

					showMeasureLabelsOnCategory = sawMeasuresElm.getAttribute("showMeasureLabelsOnCategory");
					if (showMeasureLabelsOnCategory.isEmpty()) {
						showMeasureLabelsOnCategory = null;
					}

					Element sawColumnElm = XMLUtil.getChildElement(sawMeasuresElm, "saw:column");

					measureType = sawColumnElm.getAttribute("measureType");
					if (measureType.isEmpty()) {
						measureType = null;
					}

					Element sawColumnRefElm = XMLUtil.getChildElement(sawColumnElm, "saw:columnRef");

					measuresColumnID = sawColumnRefElm.getAttribute("columnID");
					if (measuresColumnID.isEmpty()) {
						measuresColumnID = null;
					}

					List<Element> sawSeriesGeneratorsNdList = XMLUtil.getChildElements(sawSelectionsElm,
							"saw:seriesGenerators");
					for (Element sawSeriesGeneratorsElm : sawSeriesGeneratorsNdList) {

						List<Element> sawSeriesGeneratorNdList = XMLUtil.getChildElements(sawSeriesGeneratorsElm,
								"saw:seriesGenerator");
						for (Element sawSeriesGeneratorElm : sawSeriesGeneratorNdList) {

							Element sawColumnRefElmInner = XMLUtil.getChildElement(sawSeriesGeneratorElm,
									"saw:columnRef");

							seriesColumnID = sawColumnRefElmInner.getAttribute("columnID");
							if (seriesColumnID.isEmpty()) {
								seriesColumnID = null;
							}
						}
						Element sawMeasureLabelsElm = XMLUtil.getChildElement(sawSeriesGeneratorsElm,
								"saw:measureLabels");

					}
					Element sawPageElm = XMLUtil.getChildElement(sawSelectionsElm, "saw:page");

				}
				List<Element> sawLegendFormatNdList = XMLUtil.getChildElements(sawViewElm, "saw:legendFormat");
				for (Element sawLegendFormatElm : sawLegendFormatNdList) {

					legendPosition = sawLegendFormatElm.getAttribute("position");
					if (legendPosition.isEmpty()) {
						legendPosition = null;
					}

					transparentFill = sawLegendFormatElm.getAttribute("transparentFill");
					if (transparentFill.isEmpty()) {
						transparentFill = null;
					}

					Element sawTextFormatElm = XMLUtil.getChildElement(sawLegendFormatElm, "saw:textFormat");

					fontSize = sawTextFormatElm.getAttribute("fontSize");
					if (fontSize.isEmpty()) {
						fontSize = null;
					}
				}

				List<Element> sawAxesFormatsNdList = XMLUtil.getChildElements(sawViewElm, "saw:axesFormats");
				for (Element sawAxesFormatsElm : sawAxesFormatsNdList) {

					List<Element> sawAxisFormatNdList = XMLUtil.getChildElements(sawAxesFormatsElm, "saw:axisFormat");
					for (Element sawAxisFormatElm : sawAxisFormatNdList) {

						axisFormatBuilder.append("{");

//						axisFormat = sawAxisFormatElm.getAttribute("axis");
//						if (axisFormat.isEmpty()) {
//							axisFormat = null;
//						}

						List<Element> sawTitleInnerNdList = XMLUtil.getChildElements(sawAxisFormatElm, "saw:title");
						for (Element sawTitleElm : sawTitleInnerNdList) {

							axisMode = sawTitleElm.getAttribute("mode");
							if (axisMode.isEmpty()) {
								axisMode = null;
							}

						}
						List<Element> sawScaleNdList = XMLUtil.getChildElements(sawAxisFormatElm, "saw:scale");
						for (Element sawScaleElm : sawScaleNdList) {

							defaultTicks = sawScaleElm.getAttribute("defaultTicks");
							if (defaultTicks.isEmpty()) {
								defaultTicks = null;
							}

							majorTicks = sawScaleElm.getAttribute("majorTicks");
							if (majorTicks.isEmpty()) {
								majorTicks = null;
							}

							showMajorTicks = sawScaleElm.getAttribute("showMajorTicks");
							if (showMajorTicks.isEmpty()) {
								showMajorTicks = null;
							}

						}

						List<Element> sawLabelsInnerNdList = XMLUtil.getChildElements(sawAxisFormatElm, "saw:labels");
						for (Element sawLabelsInnerElm : sawLabelsInnerNdList) {

							rotate = sawLabelsInnerElm.getAttribute("rotate");
							if (rotate.isEmpty()) {
								rotate = null;
							}

							rotateLabels = sawLabelsInnerElm.getAttribute("rotateLabels");
							if (rotateLabels.isEmpty()) {
								rotateLabels = null;
							}

							skipLabels = sawLabelsInnerElm.getAttribute("skipLabels");
							if (skipLabels.isEmpty()) {
								skipLabels = null;
							}

							labelAbbreviation = sawLabelsInnerElm.getAttribute("abbreviation");
							if (labelAbbreviation.isEmpty()) {
								labelAbbreviation = null;
							}
						}

						List<Element> sawTextFormatInnerNdList = XMLUtil.getChildElements(sawAxisFormatElm,
								"saw:textFormat");
						for (Element sawTextFormatInnerElm : sawTextFormatInnerNdList) {

							textFont = sawTextFormatInnerElm.getAttribute("fontsize");
							if (textFont.isEmpty()) {
								textFont = null;
							}
//
//							textAbbreviation = sawTextFormatInnerElm.getAttribute("abbreviation");
//							if (textAbbreviation.isEmpty()) {
//								textAbbreviation = null;
//							}

						}

						axisFormatBuilder.append("[").append(axisMode).append("], ").append("[").append(defaultTicks)
								.append(", ").append(majorTicks).append(", ").append(showMajorTicks).append("], ")
								.append("[").append(rotate).append(", ").append(rotateLabels).append(", ")
								.append(skipLabels).append(", ").append(labelAbbreviation).append("], ").append("[").append(textFont).append("]");

						axisFormatBuilder.append("}");
					}

				}
				List<Element> sawSeriesFormatsNdList = XMLUtil.getChildElements(sawViewElm, "saw:seriesFormats");
				for (Element sawSeriesFormatsElm : sawSeriesFormatsNdList) {

					List<Element> sawSeriesFormatGroupNdList = XMLUtil.getChildElements(sawSeriesFormatsElm,
							"saw:seriesFormatGroup");
					for (Element sawSeriesFormatGroupElm : sawSeriesFormatGroupNdList) {

						seriesName = sawSeriesFormatGroupElm.getAttribute("name");
						if (seriesName.isEmpty()) {
							seriesName = null;
						}

						List<Element> sawSeriesFormatRuleNdList = XMLUtil.getChildElements(sawSeriesFormatGroupElm,
								"saw:seriesFormatRule");
						for (Element sawSeriesFormatRuleElm : sawSeriesFormatRuleNdList) {

							List<Element> sawSeriesConditionNdList = XMLUtil.getChildElements(sawSeriesFormatRuleElm,
									"saw:seriesCondition");
							for (Element sawSeriesConditionElm : sawSeriesConditionNdList) {

								seriesPosition = sawSeriesConditionElm.getAttribute("position");
								if (seriesPosition.isEmpty()) {
									seriesPosition = null;
								}

							}
							List<Element> sawVisualFormatsNdList = XMLUtil.getChildElements(sawSeriesFormatRuleElm,
									"saw:visualFormats");
							for (Element sawVisualFormatsElm : sawVisualFormatsNdList) {

								List<Element> sawVisualFormatNdList = XMLUtil.getChildElements(sawVisualFormatsElm,
										"saw:visualFormat");
								for (Element sawVisualFormatElm : sawVisualFormatNdList) {

									visualLineStyle = sawVisualFormatElm.getAttribute("lineStyle");
									if (visualLineStyle.isEmpty()) {
										visualLineStyle = null;
									}

									lineWidth = sawVisualFormatElm.getAttribute("lineWidth");
									if (lineWidth.isEmpty()) {
										lineWidth = null;
									}

									symbol = sawVisualFormatElm.getAttribute("symbol");
									if (symbol.isEmpty()) {
										symbol = null;
									}

								}
							}

							seriesBuilder.append("{").append(seriesName).append(", ").append(seriesPosition)
									.append(", ").append(visualLineStyle).append(", ").append(lineWidth).append(", ")
									.append(symbol).append("}, ");
						}
					}

				}
				List<Element> sawFunnelPropertiesNdList = XMLUtil.getChildElements(sawViewElm, "saw:funnelProperties");
				for (Element sawFunnelPropertiesElm : sawFunnelPropertiesNdList) {

					Element sawFunnelThresholdsElm = XMLUtil.getChildElement(sawFunnelPropertiesElm,
							"saw:funnelThresholds");

					List<Element> sawColorRangeNdList = XMLUtil.getChildElements(sawFunnelThresholdsElm,
							"saw:colorRange");
					for (Element sawColorRangeElm : sawColorRangeNdList) {

						color = sawColorRangeElm.getAttribute("color");
						if (color.isEmpty()) {
							color = null;
						}

						labelType = sawColorRangeElm.getAttribute("labelType");
						if (labelType.isEmpty()) {
							labelType = null;
						}

						rangeType = sawColorRangeElm.getAttribute("rangeType");
						if (rangeType.isEmpty()) {
							rangeType = null;
						}

						List<Element> sawMarkerLabelNdList = XMLUtil.getChildElements(sawColorRangeElm,
								"saw:markerLabel");
						for (Element sawMarkerLabelElm : sawMarkerLabelNdList) {

							Element sawCaptionElm = XMLUtil.getChildElement(sawMarkerLabelElm, "saw:caption");

							captionID = sawCaptionElm.getAttribute("captionID");
							if (captionID.isEmpty()) {
								captionID = null;
							}

							List<Element> sawTextNdList = XMLUtil.getChildElements(sawCaptionElm, "saw:text");
							for (Element sawTextElm : sawTextNdList) {

								captionText = sawTextElm.getTextContent();
								if (captionText.isEmpty()) {
									captionText = null;
								}
							}
						}
						List<Element> sawRangeLowNdList = XMLUtil.getChildElements(sawColorRangeElm, "saw:rangeLow");
						for (Element sawRangeLowElm : sawRangeLowNdList) {

							Element sawxExprElm = XMLUtil.getChildElement(sawRangeLowElm, "sawx:expr");

							rangeXsiType = sawxExprElm.getAttribute("xsi:type");
							if (rangeXsiType.isEmpty()) {
								rangeXsiType = null;
							}

							rangeText = sawxExprElm.getAttribute("text");
							if (rangeText.isEmpty()) {
								rangeText = null;
							}
						}
						List<Element> sawRangeHighNdList = XMLUtil.getChildElements(sawColorRangeElm, "saw:rangeHigh");
//						for (Element sawRangeHighElm : sawRangeHighNdList) {
//
//						}

						funnelPropertiesBuilder.append("[").append(color).append(", ").append(labelType).append(", ")
								.append(rangeType).append(", ").append(captionID).append(", ").append(captionText)
								.append(", ").append(rangeXsiType).append(", ").append(rangeText).append("] ");
					}
				}
				List<Element> sawMeasuresListNdList = XMLUtil.getChildElements(sawViewElm, "saw:measuresList");
				for (Element sawMeasuresListElm : sawMeasuresListNdList) {

					List<Element> sawMeasureListNdList = XMLUtil.getChildElements(sawMeasuresListElm, "saw:measure");

					for (Element sawMeasureListElm : sawMeasureListNdList) {

						innerMeasureColumnID = sawMeasureListElm.getAttribute("columnID");
						if (innerMeasureColumnID.isEmpty()) {
							innerMeasureColumnID = null;
						}

						visualType = sawMeasureListElm.getAttribute("visualType");
						if (visualType.isEmpty()) {
							visualType = null;
						}

						measuresListBuilder.append("{").append(innerMeasureColumnID).append(", ").append(visualType)
								.append("}, ");
					}
				}
				List<Element> sawTreemapColorNdList = XMLUtil.getChildElements(sawViewElm, "saw:treemapColor");
				for (Element sawTreemapColorElm : sawTreemapColorNdList) {

					treeBinType = sawTreemapColorElm.getAttribute("binType");
					if (treeBinType.isEmpty()) {
						treeBinType = null;
					}

					treeNumBins = sawTreemapColorElm.getAttribute("numBins");
					if (treeNumBins.isEmpty()) {
						treeNumBins = null;
					}

					Element sawColumnRefElm = XMLUtil.getChildElement(sawTreemapColorElm, "saw:columnRef");

					treeColumnID = sawColumnRefElm.getAttribute("columnID");
					if (treeColumnID.isEmpty()) {
						treeColumnID = null;
					}

					Element sawRampStyleElm = XMLUtil.getChildElement(sawTreemapColorElm, "saw:rampStyle");

					List<Element> sawRampItemNdList = XMLUtil.getChildElements(sawRampStyleElm, "saw:rampItem");
					for (Element sawRampItemElm : sawRampItemNdList) {

						treeRampId = sawRampItemElm.getAttribute("id");
						if (treeRampId.isEmpty()) {
							treeRampId = null;
						}

						List<Element> sawGNdList = XMLUtil.getChildElements(sawRampItemElm, "saw:g");
						for (Element sawGElm : sawGNdList) {

							gClass = sawGElm.getAttribute("class");
							if (gClass.isEmpty()) {
								gClass = null;
							}

							gFill = sawGElm.getAttribute("fill");
							if (gFill.isEmpty()) {
								gFill = null;
							}

						}

						treeRampBuilder.append("[").append(treeRampId).append(", ").append(gClass).append(", ")
								.append(gFill).append(", ").append("], ");
					}
				}
				List<Element> sawTreemapPropertiesNdList = XMLUtil.getChildElements(sawViewElm,
						"saw:treemapProperties");
				for (Element sawTreemapPropertiesElm : sawTreemapPropertiesNdList) {

					treeHeight = sawTreemapPropertiesElm.getAttribute("height");
					if (treeHeight.isEmpty()) {
						treeHeight = null;
					}

					showGroupLabel = sawTreemapPropertiesElm.getAttribute("showGroupLabel");
					if (showGroupLabel.isEmpty()) {
						showGroupLabel = null;
					}

					treeShowLegend = sawTreemapPropertiesElm.getAttribute("showLegend");
					if (treeShowLegend.isEmpty()) {
						treeShowLegend = null;
					}

					showTileLabel = sawTreemapPropertiesElm.getAttribute("showTileLabel");
					if (showTileLabel.isEmpty()) {
						showTileLabel = null;
					}

					treeWidth = sawTreemapPropertiesElm.getAttribute("width");
					if (treeWidth.isEmpty()) {
						treeWidth = null;
					}

					Element sawGroupFormatElm = XMLUtil.getChildElement(sawTreemapPropertiesElm, "saw:groupFormat");

					Element sawFormatSpecElm = XMLUtil.getChildElement(sawGroupFormatElm, "saw:formatSpec");

					Element sawTileFormatElm = XMLUtil.getChildElement(sawTreemapPropertiesElm, "saw:tileFormat");

					Element sawInnerFormatSpecElm = XMLUtil.getChildElement(sawTileFormatElm, "saw:formatSpec");

				}
				List<Element> sawHeatmapColorNdList = XMLUtil.getChildElements(sawViewElm, "saw:heatmapColor");
				for (Element sawHeatmapColorElm : sawHeatmapColorNdList) {

					heatBinType = sawHeatmapColorElm.getAttribute("binType");
					if (heatBinType.isEmpty()) {
						heatBinType = null;
					}

					heatNumBins = sawHeatmapColorElm.getAttribute("numBins");
					if (heatNumBins.isEmpty()) {
						heatNumBins = null;
					}

					Element sawColumnRefElm = XMLUtil.getChildElement(sawHeatmapColorElm, "saw:columnRef");

					heatColumnID = sawColumnRefElm.getAttribute("columnID");
					if (heatColumnID.isEmpty()) {
						heatColumnID = null;
					}

					Element sawRampStyleElm = XMLUtil.getChildElement(sawHeatmapColorElm, "saw:rampStyle");

					List<Element> sawRampItemNdList = XMLUtil.getChildElements(sawRampStyleElm, "saw:rampItem");
					for (Element sawRampItemElm : sawRampItemNdList) {

						heatRampId = sawRampItemElm.getAttribute("id");
						if (heatRampId.isEmpty()) {
							heatRampId = null;
						}

						List<Element> sawGNdList2 = XMLUtil.getChildElements(sawRampItemElm, "saw:g");
						for (Element sawGElm2 : sawGNdList2) {

							gClass2 = sawGElm2.getAttribute("class");
							if (gClass2.isEmpty()) {
								gClass2 = null;
							}

							gFill2 = sawGElm2.getAttribute("fill");
							if (gFill2.isEmpty()) {
								gFill2 = null;
							}

						}

						heatRampBuilder.append("[").append(heatRampId).append(", ").append(gClass2).append(", ")
								.append(gFill2).append(", ").append("], ");
					}
				}
				List<Element> sawHeatmapPropertiesNdList = XMLUtil.getChildElements(sawViewElm,
						"saw:heatmapProperties");
				for (Element sawHeatmapPropertiesElm : sawHeatmapPropertiesNdList) {

					heatShowLegend = sawHeatmapPropertiesElm.getAttribute("showLegend");

				}

				viewsBuilder.append(currentView).append("|").append(autoPreview).append("|").append(includeName)
						.append("|").append(viewsName).append("|").append(scrollingEnabled).append("|")
						.append(showMeasureValue).append("|").append(startedDisplay).append("|").append(viewsXsiType)
						.append("|").append(viewNameBuilder).append("|").append(borderColor).append("|")
						.append(borderPosition).append("|").append(borderStyle).append("|").append(viewsWrapText)
						.append("|").append(edgeBuilder).append("|").append(edgeLayerBuilder).append("|")
						.append(animateOnDisplay).append("|").append(mode).append("|").append(renderFormat).append("|")
						.append(subtype).append("|").append(type).append("|").append(barStyle).append("|")
						.append(bubblePercentSize).append("|").append(effect).append("|").append(fillStyle).append("|")
						.append(lineStyle).append("|").append(scatterStyle).append("|").append(height).append("|")
						.append(width).append("|").append(display).append("|").append(label).append("|")
						.append(position).append("|").append(transparentBackground).append("|").append(valueAs)
						.append("|").append(viewsMode).append("|").append(canvasText).append("|")
						.append(showMeasureLabelsOnCategory).append("|").append(viewsColumnID).append("|")
						.append(measureType).append("|").append(measuresColumnID).append("|").append(seriesColumnID)
						.append("|").append(legendPosition).append("|").append(transparentFill).append("|")
						.append(fontSize).append("|").append(axisFormatBuilder).append("|").append(seriesBuilder)
						.append("|").append(funnelPropertiesBuilder).append("|").append(measuresListBuilder).append("|")
						.append(treeBinType).append("|").append(treeNumBins).append("|").append(treeColumnID)
						.append("|").append(treeRampBuilder).append("|").append(treeHeight).append("|")
						.append(showGroupLabel).append("|").append(treeShowLegend).append("|").append(showTileLabel)
						.append("|").append(treeWidth).append("|").append(heatBinType).append("|").append(heatNumBins)
						.append("|").append(heatColumnID).append("|").append(heatRampBuilder).append("|")
						.append(heatShowLegend).append("|").append(";\n");
			}
		}

		// Reading XML data from prompts child node

		String scope = null;
		String subjectAreaPrompt = null;

		List<Element> sawPromptsNdList = XMLUtil.getChildElements(sawReportElm, "saw:prompts");
		for (Element sawPromptsElm : sawPromptsNdList) {

			scope = sawPromptsElm.getAttribute("scope").replaceAll("\\s+", "");
			if (scope.isEmpty()) {
				scope = null;
			}

			subjectAreaPrompt = sawPromptsElm.getAttribute("subjectArea").replaceAll("\\s+", "");
			if (subjectAreaPrompt.isEmpty()) {
				subjectAreaPrompt = null;
			}
		}

		// Prompt column names in Prompt CSV file

		// Prompt node values in Prompt CSV file
		promptsBuilder.append(scope).append("|").append(subjectAreaPrompt).append(";\n");

		map.put("Report", reportBuilder.toString());
		map.put("Criteria", criteriaBuilder.toString());
		map.put("InteractionOptions", interactionOptionsBuilder.toString());
		map.put("Views", viewsBuilder.toString());
		map.put("Prompts", promptsBuilder.toString());

		return map;

	}

	/**
	 * 
	 * @param mapDoc
	 * @param outputFileLocation
	 * @throws Exception
	 */



	/**
	 * 
	 * @param csvfile
	 * @param data
	 * @param outputFileLocation
	 */

	 public void writeExcel(Sheet sheet, String[] data) {
//	        int startRow = sheet.getLastRowNum() + 1; 
	        int startRow = sheet.getLastRowNum(); 

	        
	        
	        for (int dataInput = 0; dataInput < data.length; dataInput++) {
	            String[] rowContent = data[dataInput].split("\\|");
	            Row row = sheet.createRow(startRow + dataInput);
	            for (int i = 0; i < rowContent.length; i++) {
	                Cell cell = row.createCell(i);
	                cell.setCellValue(rowContent[i]);
	            }
	        }
	    }
	    
	    public void close(String outputDirectory, String fileName) {
	        File directory = new File(outputDirectory);
	        if (!directory.exists()) {
	            directory.mkdirs();  
	        }
	        File file = new File(directory, fileName + ".xlsx");

	        try (FileOutputStream outputStream = new FileOutputStream(file)) {
	            workbook.write(outputStream);
	        } catch (IOException e) {
	            throw new RuntimeException("Error writing to workbook", e);
	        }
	    }
	



	public void extractMapData(Map<String, String> mapDoc, String fileName) throws Exception {
	    String[] splitDatabase = mapDoc.get("Report").split(Pattern.quote(";"));
	    writeExcel(reportSheet, splitDatabase);

	    splitDatabase = mapDoc.get("Criteria").split(Pattern.quote(";"));
	    writeExcel(criteriaSheet, splitDatabase);

	    splitDatabase = mapDoc.get("InteractionOptions").split(Pattern.quote(";"));
	    writeExcel(interactiveOptionsSheet, splitDatabase);

	    splitDatabase = mapDoc.get("Views").split(Pattern.quote(";"));
	    writeExcel(viewsSheet, splitDatabase);

	    splitDatabase = mapDoc.get("Prompts").split(Pattern.quote(";"));
	    writeExcel(promptsSheet, splitDatabase);
	}
	/**
	 * 
	 * @param connection
	 * @throws SQLException
	 */
	private static void createTables(Connection connection) throws SQLException {

		String reportTableQuery = "CREATE TABLE IF NOT EXISTS Report (" + "xmlVersion INT, "
				+ "xmlnsSaw VARCHAR(1000), " + "xmlnsSawx VARCHAR(1000), " + "xmlnsXsd  VARCHAR(1000), "
				+ "xmlnsXsi VARCHAR(1000), " + "contOfsawReportElm VARCHAR(1000), " + "PRIMARY KEY (xmlVersion)" + ")";

		String criteriaTableQuery = "CREATE TABLE IF NOT EXISTS Criteria (" + "PrimaryXsiType VARCHAR(100), "
				+ "SecondaryXsiType VARCHAR(100), " + "SubjectArea VARCHAR(100), " + "WithinHierarchy VARCHAR(50),"
				+ "ContOfsawCriteriaElm VARCHAR(1000), " + "Op VARCHAR(100), " + "Path VARCHAR(100), "
				+ "Name VARCHAR(100), " + "ContOfFilterSawxExprElm VARCHAR(1000), " + "ColumnID VARCHAR(100), "
				+ "ExprXsiType VARCHAR(100), " + "ContOfsawxExprElm VARCHAR(1000), "
				+ "ContOfsawDisplayFormatElm VARCHAR(1000), " + "Suppress VARCHAR(100), " + "FontColor VARCHAR(100), "
				+ "WrapText VARCHAR(100), " + "ContOfsawFormatSpecElm VARCHAR(1000), "
				+ "DataFormatXsiType VARCHAR(100), " + "Commas VARCHAR(100), " + "NegativeType VARCHAR(100), "
				+ "MinDigits VARCHAR(100), " + "MaxDigits VARCHAR(100), " + "ContOfsawTableHeadingElm VARCHAR(1000), "
				+ "fmt VARCHAR(100), " + "ContOfsawCaptionElm VARCHAR(1000), " + "ContOfsawTextElm VARCHAR(1000), "
				+ "ContOfColHeadSawDisplayFormatElm VARCHAR(1000), " + "ContOfColHeadSawFormatSpecElm VARCHAR(1000), "
				+ "ColumnFMT VARCHAR(100), " + "ContOfColHeadSawCaptionElm VARCHAR(1000), "
				+ "ContOfColHeadSawTextElm VARCHAR(1000), " + "ContOfsawColumnsElm VARCHAR(1000)" + ")";

		String interactionOptionsTableQuery = "CREATE TABLE IF NOT EXISTS InteractionOptions ("
				+ "addRemoveValues VARCHAR(100), " + "calcitemoperations VARCHAR(100), " + "drill VARCHAR(100), "
				+ "groupOperations VARCHAR(100), " + "inclexClColumns VARCHAR(100), " + "moveColumns VARCHAR(100), "
				+ "showHideRunningSum VARCHAR(100), " + "showHideSubtotal VARCHAR(100), " + "sortColumns VARCHAR(100), "
				+ "contOfsawInteractionOptionsElm VARCHAR(100) " + ")";

		String viewsTableQuery = "CREATE TABLE IF NOT EXISTS Views (" + "currentView VARCHAR(100), "
				+ "autoPreview VARCHAR(100), " + "includeName VARCHAR(100), " + "viewsName VARCHAR(100), "
				+ "scrollingEnabled VARCHAR(100), " + "showMeasureValue VARCHAR(100), "
				+ "startedDisplay VARCHAR(100), " + "viewsXsiType VARCHAR(100), " + "viewName VARCHAR(1000), "
				+ "borderColor VARCHAR(100), " + "borderPosition VARCHAR(100), " + "borderStyle VARCHAR(100), "
				+ "viewsWrapText VARCHAR(100), " + "edges VARCHAR(1000), " + "edgeLayers VARCHAR(1000), "
				+ "animateOnDisplay VARCHAR(100), " + "mode VARCHAR(100), " + "renderFormat VARCHAR(100), "
				+ "subtype VARCHAR(100), " + "type VARCHAR(100), " + "barStyle VARCHAR(100), "
				+ "bubblePercentSize VARCHAR(100), " + "effect VARCHAR(100), " + "fillStyle VARCHAR(100), "
				+ "lineStyle VARCHAR(100), " + "scatterStyle VARCHAR(100), " + "height VARCHAR(100), "
				+ "width VARCHAR(100), " + "display VARCHAR(100), " + "label VARCHAR(100), " + "position VARCHAR(100), "
				+ "transparentBackground VARCHAR(100), " + "valueAs VARCHAR(100), " + "viewsMode VARCHAR(100), "
				+ "canvasText VARCHAR(1000), " + "showMeasureLabelsOnCategory VARCHAR(100), "
				+ "viewsColumnID VARCHAR(100), " + "measureType VARCHAR(100), " + "measuresColumnID VARCHAR(100), "
				+ "seriesColumnID VARCHAR(100), " + "legendPosition VARCHAR(100), " + "transparentFill VARCHAR(100), "
				+ "fontSize VARCHAR(100), " + "axisFormat VARCHAR(1000), " + "seriesFormat VARCHAR(1000), "
				+ "funnelProperties VARCHAR(1000), " + "measuresList VARCHAR(1000)," + "treeBinTypeVARCHAR(100), "
				+ "treeNumBins VARCHAR(100), " + "treeColumnID VARCHAR(100), " + "treeRampItem VARCHAR(1000), "
				+ "treeHeight VARCHAR(100), " + "showGroupLabel VARCHAR(100), " + "treeShowLegend VARCHAR(100), "
				+ "showTileLabel VARCHAR(100), " + "treeWidth VARCHAR(100), " + "heatBinType VARCHAR(100), "
				+ "heatNumBins VARCHAR(100), " + "heatColumnID VARCHAR(100), " + "heatRampItem VARCHAR(1000), "
				+ "heatShowLegend VARCHAR(100) " + ")";

		String promptsTableQuery = "CREATE TABLE IF NOT EXISTS Prompts (" + "scope VARCHAR(100),"
				+ "subjectArea VARCHAR(100) " + ")";

		try (PreparedStatement reportStmt = connection.prepareStatement(reportTableQuery);
				PreparedStatement criteriaStmt = connection.prepareStatement(criteriaTableQuery);
				PreparedStatement intOptStmt = connection.prepareStatement(interactionOptionsTableQuery);
				PreparedStatement viewsStmt = connection.prepareStatement(viewsTableQuery);
				PreparedStatement promptsStmt = connection.prepareStatement(promptsTableQuery)) {
			reportStmt.executeUpdate();
			criteriaStmt.executeUpdate();
			intOptStmt.executeUpdate();
			viewsStmt.executeUpdate();
			promptsStmt.executeUpdate();

		}

	}

	/**
	 * 
	 * @param conn
	 * @param mapDoc
	 * @throws Exception
	 */
	public static void extractMapDataForDb(Connection conn, Map<String, String> mapDoc) throws Exception {

		String[] splitDatabase = mapDoc.get("Report").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "Report", splitDatabase);

		splitDatabase = mapDoc.get("Criteria").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "Criteria", splitDatabase);

		splitDatabase = mapDoc.get("InteractionOptions").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "InteractionOptions", splitDatabase);

		splitDatabase = mapDoc.get("Views").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "Views", splitDatabase);

		splitDatabase = mapDoc.get("Prompts").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "Prompts", splitDatabase);

	}

	/**
	 * 
	 * @param conn
	 * @param dbTableName
	 * @param data
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
//			PreparedStatement deletePstmt = conn.prepareStatement(deleteQuery);
//			deletePstmt.executeUpdate();

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