package es.upm.fi.oeg.rider.dataservice;

import java.util.Collection;
import java.util.LinkedList;

import es.upm.fi.oeg.rider.dataservice.datalookup.OntModelQueryService;
import es.upm.fi.oeg.rider.dataservice.ontology.OntologyService;
import es.upm.fi.oeg.rider.domain.ontology.Constants;
import es.upm.fi.oeg.rider.domain.ontology.ToolCategory;
import es.upm.fi.oeg.rider.domain.ontology.ToolVersion;
import es.upm.fi.oeg.rider.domain.ontology.qualitymodel.QualityCharacteristic;
import es.upm.fi.oeg.rider.domain.ontology.qualitymodel.QualityMeasure;
import es.upm.fi.oeg.rider.domain.ontology.qualitymodel.QualityValue;
import es.upm.fi.oeg.rider.domain.recommendation.Requirement;

public class DataService extends OntologyService {
		
	private OntModelQueryService queryService;

	public DataService() {
		super();
		this.queryService = new OntModelQueryService();
	}

	public void setQueryService(OntModelQueryService queryService) {
		this.queryService = queryService;
	}
	
		
	
	/**
	 * Checks if the tool version covers the specific quality measure
	 * @param tool
	 * @param measure
	 * @return
	 */
	public boolean coversQualityMeasure(String toolVersionUri,
			String measureUri) {

		String queryString = 
			"PREFIX qmo: <"+Constants.QUALITY_MODEL_NS+">\n " +
			"PREFIX muo: <"+Constants.MUO+">\n " +
			"PREFIX smd: <"+Constants.SEALS_METADATA_NS+">\n " +
			"PREFIX qmo: <"+Constants.QUALITY_MODEL_NS+">\n " +			
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
			"SELECT DISTINCT ?qvalue \n" +
			"WHERE {\n" +
			"      ?qvalue rdf:type muo:QualityValue; \n" +
			"      qmo:forMeasure <"+measureUri+">; \n" +
			"      qmo:obtainedFrom ?request. \n" +
			
			"      ?request rdf:type smd:ExecutionRequest; \n" +
			"      smd:evaluatesTool <"+toolVersionUri+">; \n" +

			"      }";

//		System.out.println(queryString);

		Collection<String> qualityValueUris = queryService
				.executeOneVariableSelectSparqlQuery(queryString, "qvalue",
						getDataModel());

		if(qualityValueUris.size() != 0)
			return true;
		return false;

	}

	
	/**
	 * Returns the the quality value for the given measure of the specific tool
	 * @param tool
	 * @param measure
	 * @return
	 */
	public QualityValue getQualityValue(String toolVersionUri,
			String measureUri) {

		String queryString = 
			"PREFIX qmo: <"+Constants.QUALITY_MODEL_NS+">\n " +
			"PREFIX muo: <"+Constants.MUO+">\n " +
			"PREFIX smd: <"+Constants.SEALS_METADATA_NS+">\n " +
			"PREFIX qmo: <"+Constants.QUALITY_MODEL_NS+">\n " +			
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
			"SELECT DISTINCT ?qvalue \n" +
			"WHERE {\n" +
			"      ?qvalue rdf:type muo:QualityValue; \n" +
			"      qmo:forMeasure <"+measureUri+">; \n" +
			"      qmo:obtainedFrom ?request. \n" +
			
			"      ?request rdf:type smd:ExecutionRequest; \n" +
			"      smd:evaluatesTool <"+toolVersionUri+">; \n" +

			"      }";

//		System.out.println(queryString);

		Collection<String> qualityValueUris = queryService
				.executeOneVariableSelectSparqlQuery(queryString, "qvalue",
						getDataModel());
		
		Collection<QualityValue> col = null;
		try {
			col = loadResourcesByURIs(QualityValue.class,
					qualityValueUris, true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if(col.isEmpty())
			return null;
		
		return col.iterator().next();
		
	}

	
	/**
	 * Returns the Uri of the quality characteristic that is measured by the quality measure
	 * @param measureUri Uri of the quality measure
	 * @return
	 */
	public String getCharacteristicUriOfMeasure(String measureUri) {
		
		if(measureUri.contains("Alternatives"))
			return "Alternatives";
		
		String queryString = 
			"PREFIX smd: <"+Constants.SEALS_METADATA_NS+">\n " +
			"PREFIX qmo: <"+Constants.QUALITY_MODEL_NS+">\n " +			
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
			"SELECT DISTINCT ?uri \n" +
			"WHERE {\n" +
			"      <"+measureUri+"> rdf:type qmo:QualityMeasure; \n" +
			"      qmo:measuresCharacteristic ?uri. \n" +
			"      }";

//		System.out.println(queryString);

		Collection<String> qualityMeasureUris = queryService
				.executeOneVariableSelectSparqlQuery(queryString, "uri",
						getDataModel());

		return qualityMeasureUris.iterator().next();
		
	}

	
	/**
	 * Returns the tool category of the tools which are evaluated with the given quality measure
	 * @param measureUri Uri of the quality measure
	 * @return
	 */
	public ToolCategory getToolCategory(String measureUri) {
		
		String queryString = 
			"PREFIX qmo: <"+Constants.QUALITY_MODEL_NS+">\n " +
			"PREFIX muo: <"+Constants.MUO+">\n " +
			"PREFIX smd: <"+Constants.SEALS_METADATA_NS+">\n " +
			"PREFIX qmo: <"+Constants.QUALITY_MODEL_NS+">\n " +			
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
			"SELECT DISTINCT ?toolCategoryUri \n" +
			"WHERE {\n" +
			"      ?qvalue rdf:type muo:QualityValue; \n" +
			"      qmo:forMeasure <"+measureUri+">; \n" +
			"      qmo:obtainedFrom ?request. \n" +
			
			"      ?request rdf:type smd:ExecutionRequest; \n" +
			"      smd:evaluatesTool ?toolVersion. \n" +
			
			"      ?toolVersion rdf:type smd:ToolVersion; \n" +
			"      smd:isToolVersionOf ?tool. \n" +
			
			"      ?tool rdf:type smd:Tool; \n" +
			"      smd:hasToolCategory ?toolCategoryUri. \n" +

			"      }";

//		System.out.println(queryString);

		Collection<String> toolCategoriesUris = queryService
				.executeOneVariableSelectSparqlQuery(queryString, "toolCategoryUri",
						getDataModel());
		
		Collection<ToolCategory> col = null;
		try {
			col = loadResourcesByURIs(ToolCategory.class,
					toolCategoriesUris, true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if(!col.isEmpty())
			return col.iterator().next();
		return null;
	}

	/**
	 * Returns all tool versions that belong to a tool category
	 * @param toolCategoryUri Uri of the tool category
	 * @return
	 */
	public Collection<ToolVersion> getToolVersions(String toolCategoryUri) {

		String queryString = 
			"PREFIX qmo: <"+Constants.QUALITY_MODEL_NS+">\n " +
			"PREFIX smd: <"+Constants.SEALS_METADATA_NS+">\n " +
			"PREFIX qmo: <"+Constants.QUALITY_MODEL_NS+">\n " +			
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
			"SELECT DISTINCT ?toolVersionUri \n" +
			"WHERE {\n" +
			
			"      ?toolVersionUri rdf:type smd:ToolVersion; \n" +
			"      smd:isToolVersionOf ?tool. \n" +
			
			"      ?tool rdf:type smd:Tool; \n" +
			"      smd:hasToolCategory <"+toolCategoryUri+">. \n" +

			"      }";

//		System.out.println(queryString);

		Collection<String> toolVersionsUris = queryService
				.executeOneVariableSelectSparqlQuery(queryString, "toolVersionUri",
						getDataModel());
		
		Collection<ToolVersion> col = null;
		try {
			col = loadResourcesByURIs(ToolVersion.class,
					toolVersionsUris, true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return col;

	}
	
	
	/**
	 * Returns all quality characteristics of which evaluated quality measures exist
	 * @return
	 */
	public Collection<QualityCharacteristic> getAllQualityCharacteristics() {		
		String queryString = 
			"PREFIX qmo: <"+Constants.QUALITY_MODEL_NS+">\n " +
			"PREFIX smd: <"+Constants.SEALS_METADATA_NS+">\n " +
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
			"SELECT DISTINCT ?uri " +
			"WHERE {\n" +
			"      ?uri rdf:type qmo:QualityCharacteristic. \n" +
			"      }";

//		System.out.println(queryString);
		
		Collection<String> characteristicsUris = queryService
				.executeOneVariableSelectSparqlQuery(queryString, "uri",
						getDataModel());

		Collection<QualityCharacteristic> col = null;
		try {
			col = loadResourcesByURIs(QualityCharacteristic.class,
					characteristicsUris, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		Collections.sort((List<QualityCharacteristic>) col, new QualityCharacteristicComparator());
		
		for (QualityCharacteristic qualityCharacteristic : col) {
			qualityCharacteristic.setQualityMeasures(getQualityMeasuresForCharacteristic(
					qualityCharacteristic.getUri().toString()));
			
//			Collections.sort((List<QualityMeasure>) qualityCharacteristic.getQualityMeasures(), 
//					new QualityMeasureComparator());
		}
		
		return col;
	}
	
		
	/**
	 * Returns all quality measures for a given quality characteristic
	 * @param characteristicUri
	 * @return
	 */
	public Collection<QualityMeasure> getQualityMeasuresForCharacteristic(String characteristicUri) {
		
		String queryString = 
			"PREFIX smd: <"+Constants.SEALS_METADATA_NS+">\n " +
			"PREFIX qmo: <"+Constants.QUALITY_MODEL_NS+">\n " +			
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
			"SELECT DISTINCT ?uri \n" +
			"WHERE {\n" +
			"      ?uri rdf:type qmo:QualityMeasure; \n" +
			"      qmo:measuresCharacteristic <"+ characteristicUri + ">. \n" +
			"      }";

//		System.out.println(queryString);

		Collection<String> qualityMeasureUris = queryService
				.executeOneVariableSelectSparqlQuery(queryString, "uri",
						getDataModel());
		
		Collection<QualityMeasure> col = null;
		try {
			col = loadResourcesByURIs(QualityMeasure.class,
					qualityMeasureUris, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return col;
		
	}
	
	/**
	 * Checks if the evaluation results for a given quality measure exist
	 * @param string
	 * @return
	 */
	public boolean isEvaluated(String qualityMeasureUri) {
//		String queryString = 
//			"PREFIX qmo: <"+Constants.QUALITY_MODEL_NS+">\n " +
//			"PREFIX smd: <"+Constants.SEALS_METADATA_NS+">\n " +
//			"PREFIX qmo: <"+Constants.QUALITY_MODEL_NS+">\n " +			
//			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
//			"SELECT DISTINCT ?qvalue \n" +
//			"WHERE {\n" +
//			"      ?qvalue rdf:type qmo:QualityValue; \n" +
//			"      qmo:forMeasure <"+qualityMeasureUri+">; \n" +
//			"      }";
//
//		System.out.println(queryString);
//
//		Collection<String> qualityValueUris = queryService
//				.executeOneVariableSelectSparqlQuery(queryString, "qvalue",
//						getDataModel());
//		
//		if(qualityValueUris.isEmpty())
//			return false;
//		return true;
		
		if(getToolCategory(qualityMeasureUri)==null)
			return false;
		return true;
	}
	
	
	//-------------------------------------------
	// OLD METHODS
	//-------------------------------------------
	
	/**
	 * Returns all categories of tools that are evaluated
	 * @return
	 */
	public Collection<ToolCategory> getToolCategories() {		
		String queryString = 
			"PREFIX qmo: <"+Constants.QUALITY_MODEL_NS+">\n " +
			"PREFIX smd: <"+Constants.SEALS_METADATA_NS+">\n " +
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
			"SELECT DISTINCT ?uri " +
			"WHERE {\n" +
			"      ?uri rdf:type smd:ToolCategory. \n" +
			"      }";

//		System.out.println(queryString);

		Collection<String> toolCategoriUris = queryService
				.executeOneVariableSelectSparqlQuery(queryString, "uri",
						getDataModel());

		Collection<ToolCategory> col = null;
		try {
			col = loadResourcesByURIs(ToolCategory.class,
					toolCategoriUris, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		LinkedList<ToolCategory> sorted = new LinkedList<ToolCategory>();
//		for (ToolCategory toolCategory : col) {
//			if(toolCategory.getUri().toString().equalsIgnoreCase(ToolCategory.ONTOLOGY_ENGINEERING_TOOL.getUri().toString()))
//				sorted.addFirst(toolCategory);
//			else
//				sorted.addLast(toolCategory);
//		}

		return col;
	}
	
	/**
	 * Returns the Java instance of a tool version which has the specified URI
	 * @param toolVersionUri
	 * @return
	 */
	public ToolVersion getToolVersion(String toolVersionUri) {
		ToolVersion toolVersion = null;
		try {
			toolVersion = loadResourceByURI(ToolVersion.class,
					toolVersionUri, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return toolVersion;
	}
	
	
	/**
	 * Returns all quality values for a given tool version
	 * @param toolVersionUri
	 * @return
	 */
	public Collection<QualityValue> getResultsForTool(String toolVersionUri) {
		String queryString = 
			"PREFIX qmo: <"+Constants.QUALITY_MODEL_NS+">\n " +
			"PREFIX muo: <"+Constants.MUO+">\n " +
			"PREFIX smd: <"+Constants.SEALS_METADATA_NS+">\n " +
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
			"SELECT DISTINCT ?uri " +
			"WHERE {\n" +
			"      ?uri rdf:type muo:QualityValue; \n" +
			"      qmo:obtainedFrom ?evaluation. \n" +
			"      ?evaluation rdf:type smd:ExecutionRequest; \n" +
			"      smd:evaluatesTool <"+ toolVersionUri +">. \n" +
			"      }";

//		System.out.println(queryString);

		Collection<String> qualityValuesUris = queryService
				.executeOneVariableSelectSparqlQuery(queryString, "uri",
						getDataModel());

		Collection<QualityValue> col = null;
		try {
			col = loadResourcesByURIs(QualityValue.class,
					qualityValuesUris, true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return col;
	
	}

	/**
	 * Returns all quality measures for a given tool category
	 * @param toolCategoryUri
	 * @return
	 */
	public Collection<QualityMeasure> getQualityMeasuresForToolCategory(String toolCategoryUri) {
		String queryString = 
			"PREFIX qmo: <"+Constants.QUALITY_MODEL_NS+">\n " +
			"PREFIX smd: <"+Constants.SEALS_METADATA_NS+">\n " +
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
			"SELECT DISTINCT ?uri " +
			"WHERE {\n" +
			"      ?uri rdf:type qmo:QualityMeasure; \n" +
			"      qmo:forToolCategory <" + toolCategoryUri + ">. \n" +
			"      }";

		
		Collection<String> measuresUri = queryService
				.executeOneVariableSelectSparqlQuery(queryString, "uri",
						getDataModel());

		Collection<QualityMeasure> col = null;
		try {
			col = loadResourcesByURIs(QualityMeasure.class,
					measuresUri, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		Collections.sort((List<QualityMeasure>) col, new QualityMeasureComparator());
						
		return col;
	}

	
	/**
	 * Returns all quality values for the specific tool version for quality measures which are specified as requirements
	 * @param toolVersionUri
	 * @param requirements
	 * @return
	 */
	public Collection<QualityValue> getQualityValuesForRequirements(
			String toolVersionUri, LinkedList<Requirement> requirements) {

		LinkedList<QualityValue> values = new LinkedList<QualityValue>();
		
		for (Requirement requirement : requirements) {
			QualityValue value = getQualityValue(toolVersionUri, requirement.getMeasure().getUri().toString());
			if(value != null)
				values.add(value);
		}
		
		return values;
	}
	
	
	/**
	 * Returns all URIs for quality measures for a given tool category
	 * @param toolCategoryUri
	 * @return
	 */
	public Collection<String> getQualityMeasureUrisForToolType(String toolCategoryUri) {
		String queryString = 
			"PREFIX qmo: <"+Constants.QUALITY_MODEL_NS+">\n " +
			"PREFIX smd: <"+Constants.SEALS_METADATA_NS+">\n " +
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
			"SELECT DISTINCT ?uri " +
			"WHERE {\n" +
			"      ?uri rdf:type qmo:QualityMeasure; \n" +
			"      qmo:forToolCategory <" + toolCategoryUri + ">. \n" +
			"      }";

//		System.out.println(queryString);
		
		Collection<String> measuresUri = queryService
				.executeOneVariableSelectSparqlQuery(queryString, "uri",
						getDataModel());
								
		return measuresUri;
	}
	
	
	/**
	 * Returns the Java instance of the quality measure with the specified URI
	 * @param measureUri
	 * @return
	 */
	public QualityMeasure getQualityMeasureObject(String measureUri) {
		QualityMeasure m = null;
		try {
			m = loadResourceByURI(QualityMeasure.class,
					measureUri, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
								
		return m;
	}

	public double getResultsDifference(String measureUri) {
		String queryString = 
				"PREFIX smd: <"+Constants.SEALS_METADATA_NS+">\n " +
				"PREFIX qmo: <"+Constants.QUALITY_MODEL_NS+">\n " +	
				"PREFIX ctic: <"+Constants.MUO+">\n " +	
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
				"SELECT DISTINCT ?result \n" +
				"WHERE {\n" +
				"      ?qv a ctic:QualityValue; \n" +
				"      ctic:qualityLiteralValue ?result; \n" +
				"      qmo:forMeasure <"+measureUri+">. \n"    +
				"      <"+measureUri+"> rdf:type qmo:QualityMeasure. \n" +
				"      }";

//		System.out.println(queryString);

			Collection<String> results = queryService
					.executeOneVariableSelectSparqlQuery(queryString, "result",
							getDataModel());

			double min = 100;
			double max = 0;
			for (String res : results) {
				double r = Double.parseDouble(res);
				if(r>max)
					max=r;
				if(r<min)
					min=r;
			}
					
		return max-min;
	}
	
	
}

