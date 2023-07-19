package com.samplekpi;

//package com.lti.recast.obiee.ObieeXmlPersing;

import java.io.File;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * CatelogParsingService.
 * 
 * @author 10715837
 *
 */
public class CatelogParsingService {
	
	/**
	 * parseCatelogXml.
	 * 
	 * @throws Exception
	 */
	public static void main(String aa[])throws Exception {
		File file = new File("src/main/resources/quarterlyrevenue");

		String absolutePath = file.getAbsolutePath();

		Document doc = readXMLDocumentFromFile(absolutePath);
		Map<String, String> parse = parseXmlDoc(doc);
		extractMapData(parse);

		Connection conn = SampleParsingService.databaseConnection();

		extractMapDataForDb(conn, parse);

	}

	/**
	 * extractMapDataForDb.
	 * 
	 * @param conn {@link Connection}
	 * @param mapDoc {@link String}
	 * @throws Exception
	 */
	public static void extractMapDataForDb(Connection conn, Map<String, String> mapDoc) throws Exception {
		SampleParsingService sampleParsingService = new SampleParsingService();
		String[] splitDatabase = mapDoc.get("kpidetails").split(Pattern.quote(";\n"));
		sampleParsingService.insertDataInDB(conn, "kpidetails", splitDatabase);
		String[] splitDimension = mapDoc.get("catalogDimensions").split(Pattern.quote(";\n"));
		sampleParsingService.insertDataInDB(conn, "catalogDimensions", splitDimension);
	}

	/**
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

	static Map<String, String> parseXmlDoc(Document doc) throws Exception {
		Map<String, String> map = new HashMap<>();
		String contOfsawCaptionElm = null;
		String op = null;
		String nodeValue = null;
		String statusRange = null;
		String statusRange1 = null;
		String statusRange2 = null;
		String goal = null;
		String treatNoDataAs = null;
		String scoreType = null;
		String thresholdType = null;
		String dimensionID = null;
		String contOfsawxExprElm = null;
		String dimensionType = null;
		Element sawkpiKpiElm = doc.getDocumentElement();
		String[] splitFileType = sawkpiKpiElm.toString().split(Pattern.quote(":"));
		String fileType = splitFileType[1];
		String contOfsawxExprElmTarget = null;
		String actualValueSqlExpression = null;
		String isTrendEnabled = sawkpiKpiElm.getAttribute("isTrendEnabled");
		String kpiDetailsId = sawkpiKpiElm.getAttribute("ID");
		String name = sawkpiKpiElm.getAttribute("name");
		String subjectArea = sawkpiKpiElm.getAttribute("subjectArea");
		String dimensionColumnID = null;
		String xmlVersion = sawkpiKpiElm.getAttribute("xmlVersion");
		String xmlnsSaw = sawkpiKpiElm.getAttribute("xmlns:saw");
		String xmlnsSawbsc = sawkpiKpiElm.getAttribute("xmlns:sawbsc");
		String xmlnsSawkpi = sawkpiKpiElm.getAttribute("xmlns:sawkpi");
		String xmlnsSawx = sawkpiKpiElm.getAttribute("xmlns:sawx");
		String xmlnsXsd = sawkpiKpiElm.getAttribute("xmlns:xsd");
		String xmlnsXsi = sawkpiKpiElm.getAttribute("xmlns:xsi");
		String contOfsawkpiKpiElm = sawkpiKpiElm.getTextContent();
		Element sawkpiKpiPeriodElm = XMLUtilCatalog.getChildElement(sawkpiKpiElm, "sawkpi:kpiPeriod");

		StringBuilder sbBuilder = new StringBuilder();
		StringBuilder dimensionBuilder = new StringBuilder();
		sbBuilder.append("kpiDetailsId").append("|").append("type").append("|").append("subjectArea").append("|")
				.append("reportName").append("|").append("period").append("|").append("actualValueSqlExpression")
				.append("|").append("targetSet").append("|").append("goal").append("|").append("treatNoDataAs")
				.append("|").append("scoreType").append("|").append("thresholdType").append("|").append("statusRange")
				.append("|").append("statusRange1").append("|").append("statusRange2").append(";\n");
		dimensionBuilder.append("kpiDetailsId").append("|").append("name").append("|").append("expression").append("|")
				.append("columnId").append("|").append("type").append(";\n");
		String changeToleranceType = sawkpiKpiPeriodElm.getAttribute("changeToleranceType");
		String contOfsawkpiKpiPeriodElm = sawkpiKpiPeriodElm.getTextContent();
		Element sawColumnElm = XMLUtilCatalog.getChildElement(sawkpiKpiPeriodElm, "saw:column");

		String columnID = sawColumnElm.getAttribute("columnID");
		String xsiType = sawColumnElm.getAttribute("xsi:type");
		String contOfsawColumnElm = sawColumnElm.getTextContent();
		List<Element> sawColumnFormulaNdList = XMLUtilCatalog.getChildElements(sawColumnElm, "saw:columnFormula");
		for (Element sawColumnFormulaElm : sawColumnFormulaNdList) {

			Element sawxExprElm = XMLUtilCatalog.getChildElement(sawColumnFormulaElm, "sawx:expr");

			String xsiType1 = sawxExprElm.getAttribute("xsi:type");
			contOfsawxExprElm = sawxExprElm.getTextContent();
		}
		Element sawkpiOwnerElm = XMLUtilCatalog.getChildElement(sawkpiKpiElm, "sawkpi:owner");

		String id = sawkpiOwnerElm.getAttribute("id");
		Element sawkpiRelatedDocumentsElm = XMLUtilCatalog.getChildElement(sawkpiKpiElm, "sawkpi:relatedDocuments");

		Element sawkpiDimensionsElm = XMLUtilCatalog.getChildElement(sawkpiKpiElm, "sawkpi:dimensions");

		String contOfsawkpiDimensionsElm = sawkpiDimensionsElm.getTextContent();
		List<Element> sawkpiDimensionNdList = XMLUtilCatalog.getChildElements(sawkpiDimensionsElm, "sawkpi:dimension");
		for (Element sawkpiDimensionElm : sawkpiDimensionNdList) {

			String isMulti = sawkpiDimensionElm.getAttribute("IsMulti");
			dimensionID = sawkpiDimensionElm.getAttribute("dimensionID");

			List<Element> sawColumnNdList = XMLUtilCatalog.getChildElements(sawkpiDimensionElm, "saw:column");
			for (Element sawColumnElm1 : sawColumnNdList) {

				dimensionColumnID = sawColumnElm1.getAttribute("columnID");
				dimensionType = sawColumnElm1.getAttribute("xsi:type");

				List<Element> sawColumnFormulaNdList1 = XMLUtilCatalog.getChildElements(sawColumnElm1,
						"saw:columnFormula");
				for (Element sawColumnFormulaElm : sawColumnFormulaNdList1) {

					Element sawxExprElm = XMLUtilCatalog.getChildElement(sawColumnFormulaElm, "sawx:expr");

					String xsiType2 = sawxExprElm.getAttribute("xsi:type");
					contOfsawxExprElm = sawxExprElm.getTextContent();
				}

			}
			dimensionBuilder.append(kpiDetailsId).append("|").append(dimensionID).append("|").append(contOfsawxExprElm)
					.append("|").append(dimensionColumnID).append("|").append(dimensionType).append(";\n");

		}
		Element sawkpiActualValueElm = XMLUtilCatalog.getChildElement(sawkpiKpiElm, "sawkpi:actualValue");

		String isWritable = sawkpiActualValueElm.getAttribute("isWritable");
		String contOfsawkpiActualValueElm = sawkpiActualValueElm.getTextContent();
		Element sawColumnElm1 = XMLUtilCatalog.getChildElement(sawkpiActualValueElm, "saw:column");

		String columnID1 = sawColumnElm1.getAttribute("columnID");
		String xsiType1 = sawColumnElm1.getAttribute("xsi:type");
		String contOfsawColumnElm1 = sawColumnElm1.getTextContent();
		List<Element> sawColumnFormulaNdList1 = XMLUtilCatalog.getChildElements(sawColumnElm1, "saw:columnFormula");
		for (Element sawColumnFormulaElm : sawColumnFormulaNdList1) {

			Element sawxExprElm = XMLUtilCatalog.getChildElement(sawColumnFormulaElm, "sawx:expr");

			String xsiType2 = sawxExprElm.getAttribute("xsi:type");
			actualValueSqlExpression = sawxExprElm.getTextContent();
		}
		Element sawDisplayFormatElm = XMLUtilCatalog.getChildElement(sawColumnElm, "saw:displayFormat");

		Element sawFormatSpecElm = XMLUtilCatalog.getChildElement(sawDisplayFormatElm, "saw:formatSpec");

		String suppress = sawFormatSpecElm.getAttribute("suppress");
		String wrapText = sawFormatSpecElm.getAttribute("wrapText");
		List<Element> sawDataFormatNdList = XMLUtilCatalog.getChildElements(sawFormatSpecElm, "saw:dataFormat");
		for (Element sawDataFormatElm : sawDataFormatNdList) {

			String commas = sawDataFormatElm.getAttribute("commas");
			String maxDigits = sawDataFormatElm.getAttribute("maxDigits");
			String minDigits = sawDataFormatElm.getAttribute("minDigits");
			String negativeType = sawDataFormatElm.getAttribute("negativeType");
			String xsiType2 = sawDataFormatElm.getAttribute("xsi:type");

		}
		Element sawTableHeadingElm = XMLUtilCatalog.getChildElement(sawColumnElm, "saw:tableHeading");

		Element sawCaptionElm = XMLUtilCatalog.getChildElement(sawTableHeadingElm, "saw:caption");

		List<Element> sawTextNdList = XMLUtilCatalog.getChildElements(sawCaptionElm, "saw:text");
		for (Element sawTextElm : sawTextNdList) {

		}
		Element sawColumnHeadingElm = XMLUtilCatalog.getChildElement(sawColumnElm, "saw:columnHeading");

		List<Element> sawCaptionNdList = XMLUtilCatalog.getChildElements(sawColumnHeadingElm, "saw:caption");
		for (Element sawCaptionElm1 : sawCaptionNdList) {

			Element sawTextElm = XMLUtilCatalog.getChildElement(sawCaptionElm1, "saw:text");

		}
		Element sawkpiTargetSetsElm = XMLUtilCatalog.getChildElement(sawkpiKpiElm, "sawkpi:targetSets");

		String contOfsawkpiTargetSetsElm = sawkpiTargetSetsElm.getTextContent();
		List<Element> sawkpiTargetSetNdList = XMLUtilCatalog.getChildElements(sawkpiTargetSetsElm, "sawkpi:targetSet");
		for (Element sawkpiTargetSetElm : sawkpiTargetSetNdList) {

			String isDefault = sawkpiTargetSetElm.getAttribute("isDefault");
			String name1 = sawkpiTargetSetElm.getAttribute("name");

			List<Element> sawkpiTargetValueNdList = XMLUtilCatalog.getChildElements(sawkpiTargetSetElm,
					"sawkpi:targetValue");
			for (Element sawkpiTargetValueElm : sawkpiTargetValueNdList) {

				String isWritable1 = sawkpiTargetValueElm.getAttribute("isWritable");

				Element sawColumnElm2 = XMLUtilCatalog.getChildElement(sawkpiTargetValueElm, "saw:column");

				String columnID2 = sawColumnElm2.getAttribute("columnID");
				String xsiType2 = sawColumnElm2.getAttribute("xsi:type");
				String contOfsawColumnElm2 = sawColumnElm2.getTextContent();
				List<Element> sawColumnFormulaNdList2 = XMLUtilCatalog.getChildElements(sawColumnElm2,
						"saw:columnFormula");
				for (Element sawColumnFormulaElm : sawColumnFormulaNdList2) {

					Element sawxExprElm = XMLUtilCatalog.getChildElement(sawColumnFormulaElm, "sawx:expr");

					String xsiType3 = sawxExprElm.getAttribute("xsi:type");
					contOfsawxExprElmTarget = sawxExprElm.getTextContent();
				}
				Element sawTableHeadingElm1 = XMLUtilCatalog.getChildElement(sawColumnElm, "saw:tableHeading");

				Element sawCaptionElm1 = XMLUtilCatalog.getChildElement(sawTableHeadingElm1, "saw:caption");

				List<Element> sawTextNdList1 = XMLUtilCatalog.getChildElements(sawCaptionElm, "saw:text");
				for (Element sawTextElm : sawTextNdList1) {

				}
				Element sawColumnHeadingElm3 = XMLUtilCatalog.getChildElement(sawColumnElm, "saw:columnHeading");

				List<Element> sawCaptionNdList2 = XMLUtilCatalog.getChildElements(sawColumnHeadingElm, "saw:caption");
				for (Element sawCaptionElm2 : sawCaptionNdList2) {

					Element sawTextElm = XMLUtilCatalog.getChildElement(sawCaptionElm, "saw:text");

				}
			}
			List<Element> sawkpiThresholdsNdList = XMLUtilCatalog.getChildElements(sawkpiTargetSetElm,
					"sawkpi:thresholds");
			for (Element sawkpiThresholdsElm : sawkpiThresholdsNdList) {

				goal = sawkpiThresholdsElm.getAttribute("goal");
				scoreType = sawkpiThresholdsElm.getAttribute("scoreType");
				thresholdType = sawkpiThresholdsElm.getAttribute("thresholdType");
				treatNoDataAs = sawkpiThresholdsElm.getAttribute("treatNoDataAs");

				List<Element> sawkpiThresholdRangeNdList = XMLUtilCatalog.getChildElements(sawkpiThresholdsElm,
						"sawkpi:thresholdRange");
				for (Element sawkpiThresholdRangeElm : sawkpiThresholdRangeNdList) {

					String iDs = sawkpiThresholdRangeElm.getAttribute("ID");
					String isMulti = sawkpiThresholdRangeElm.getAttribute("IsMulti");
					String assessmentStateKey = sawkpiThresholdRangeElm.getAttribute("assessmentStateKey");
					String value = sawkpiThresholdRangeElm.getAttribute("value");

					List<Element> sawkpiStatusTextNdList = XMLUtilCatalog.getChildElements(sawkpiThresholdRangeElm,
							"sawkpi:statusText");
					for (Element sawkpiStatusTextElm : sawkpiStatusTextNdList) {

						Element sawCaptionElm1 = XMLUtilCatalog.getChildElement(sawkpiStatusTextElm, "saw:caption");

						contOfsawCaptionElm = sawCaptionElm1.getTextContent();
						List<Element> sawTextNdList1 = XMLUtilCatalog.getChildElements(sawCaptionElm, "saw:text");
						for (Element sawTextElm : sawTextNdList) {

						}

					}
					List<Element> sawkpiRangeExpressionNdList = XMLUtilCatalog.getChildElements(sawkpiThresholdRangeElm,
							"sawkpi:rangeExpression");
					for (Element sawkpiRangeExpressionElm : sawkpiRangeExpressionNdList) {

						Element sawxExprElm = XMLUtilCatalog.getChildElement(sawkpiRangeExpressionElm, "sawx:expr");

						op = sawxExprElm.getAttribute("op");
						String xsiType2 = sawxExprElm.getAttribute("xsi:type");
						Element sawxExprElm1 = XMLUtilCatalog.getChildElement(sawxExprElm, "sawx:expr");

						String isMulti1 = sawxExprElm1.getAttribute("IsMulti");
						String valueType = sawxExprElm1.getAttribute("valueType");
						String xsiType3 = sawxExprElm1.getAttribute("xsi:type");

						nodeValue = sawxExprElm1.getNextSibling().getFirstChild().getNodeValue();
						if (nodeValue == null) {
							nodeValue = sawxExprElm1.getNextSibling().getLastChild().getFirstChild().getNodeValue();
						}
					}
					List<Element> sawkpiIconSchemeNdList = XMLUtilCatalog.getChildElements(sawkpiThresholdRangeElm,
							"sawkpi:iconScheme");
					for (Element sawkpiIconSchemeElm : sawkpiIconSchemeNdList) {

						String color = sawkpiIconSchemeElm.getAttribute("color");
						String icon = sawkpiIconSchemeElm.getAttribute("icon");
						if (color.equals("green")) {
							statusRange = new StringBuilder(contOfsawCaptionElm).append("*").append(op).append("*")
									.append(String.valueOf(nodeValue)).append("*").append(color).append("*")
									.append(icon).toString();
						}

						if (color.equals("yellow")) {
							statusRange1 = new StringBuilder(contOfsawCaptionElm).append("*").append(op).append("*")
									.append(String.valueOf(nodeValue)).append("*").append(color).append("*")
									.append(icon).toString();
						}
						if (color.equals("red")) {
							statusRange2 = new StringBuilder(contOfsawCaptionElm).append("*").append(op).append("*")
									.append(String.valueOf(nodeValue)).append("*").append(color).append("*")
									.append(icon).toString();

						}
					}
				}

			}
		}

		sbBuilder.append(kpiDetailsId).append("|").append(fileType).append("|").append(subjectArea).append("|")
				.append(name).append("|").append(contOfsawkpiKpiPeriodElm).append("|").append(actualValueSqlExpression)
				.append("|").append(contOfsawxExprElmTarget).append("|").append(goal).append("|").append(treatNoDataAs)
				.append("|").append(scoreType).append("|").append(thresholdType).append("|").append(statusRange)
				.append("|").append(statusRange1).append("|").append(statusRange2).append(";\n");

		map.put("kpidetails", sbBuilder.toString());
		map.put("catalogDimensions", dimensionBuilder.toString());

		return map;
	}

	/**
	 * extractMapData.
	 * 
	 * @param mapDoc
	 * @throws Exception
	 */
	public static void extractMapData(Map<String, String> mapDoc) throws Exception {
		SampleParsingService sampleParsingService = new SampleParsingService();

		String[] splitDatabase = mapDoc.get("kpidetails").split(Pattern.quote(";"));
		sampleParsingService.writeCsv("kpidetails", splitDatabase);
		String[] splitDataForDimension = mapDoc.get("catalogDimensions").split(Pattern.quote(";"));
		sampleParsingService.writeCsv("catalogDimensions", splitDataForDimension);
	}
}
