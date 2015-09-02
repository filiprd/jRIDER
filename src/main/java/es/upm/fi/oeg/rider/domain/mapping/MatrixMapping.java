package es.upm.fi.oeg.rider.domain.mapping;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.InvalidPropertiesFormatException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;


/**
 * This class contains mappings of matrix elements into criteria
 * @author Filip
 *
 */
public class MatrixMapping implements Serializable,Cloneable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3522378426466888037L;

	/**
	 * The list of mapping items
	 */
	private LinkedList<MappingItem> mappingItems;
	
	
	/**
	 * Creates a mapping for the matrix
	 * @param id
	 * 			An Id of the matrix mapping
	 * @param mappingItems
	 * 			A list of mapping items
	 */
	public MatrixMapping(String id, LinkedList<MappingItem> mappingItems) {		
		if(id == null)
			throw new IllegalArgumentException("Id cannot be null");
		this.mappingItems = new LinkedList<MappingItem>();
		this.mappingItems = mappingItems;
		this.id = id;
	}


	/**
	 * The Id of the matrix mapping. This Id should be the same as the Id of the matrix that this
	 * mapping belongs to
	 */
	private String id;

	/**
	 * Returns the Id of the matrix mapping
	 * @return Id of the matrix mapping
	 */
	public String getId() {
		return id;
	}


	/**
	 * Returns the list of the mapping items
	 * @return Matrix mappings
	 */
	public LinkedList<MappingItem> getMappingItems() {
		return mappingItems;
	}

	
//	/**
//	 * Adds a mapping item to the matrix mapping
//	 * @param item Mapping item to add
//	 * @return Returns <i>true</i> if the mapping item is successfully added, <i>false</i> otherwise
//	 */
//	public boolean addMappingItem(MappingItem item){
//		if(item == null){
//			System.err.println("The mapping item provided is null");
//			return false;
//		}
//		if(getMappingItems().contains(item)){
//			System.err.println("Matrix mapping already contains item: " + item);
//			return false;
//		}
//		getMappingItems().add(item);
//		return true;
//	}
	
	/**
	 * Returns the criterion Id which corresponds to the given row in the matrix mapping
	 * @param row Row number (position in the matrix)
	 * @return The criterion Id that corresponds to the given row, <i>null</i> if it does not exist
	 */
	public String getCriterionId(int row){
		for (MappingItem m : mappingItems) {
			if(m.getRowNumber() == row)
				return m.getCriterionId();
		}
		return null;
	}
	
	/**
	 * Returns the row number which corresponds to the given criterion Id in the matrix mapping
	 * @param criterionId
	 * @return Row number (position in the matrix) that corresponds to the given criterion <br/>
	 * -1 if the criterion does not exist in the matrix mapping
	 */
	public int getRowNumber(String criterionId){
		for (MappingItem m : mappingItems) {
			if(m.getCriterionId().equals(criterionId))
				return m.getRowNumber();
		}
		return -1;
	}
	
	/**
	 * Returns the positions of the given set of criteria Ids in the matrix mapping
	 * @param criteriaIds A set of criteria Ids to look for
	 * @return An array of integers that corresponds to a given set of criteria Ids
	 */
	public int[] getCriteriaIndices(List<String> criteriaIds){
		int[] indexes = new int[criteriaIds.size()];
		int i = 0;
		for (String uri : criteriaIds) {
			indexes[i++] = getRowNumber(uri);
		}
		return indexes;
	}
	
	/**
	 * Loads a mapping from the property file.
	 * It is assumed that property file contains keys which represent criteria Ids, and values
	 * for those keys which represent the positions in the matrix
	 * @param fileName - the name of the file to load the mapping from
	 * @param id - an id to set for the mapping
	 * @return The matrix mapping loaded from the property file
	 * @throws IOException 
	 * @throws InvalidPropertiesFormatException 
	 */
	public static MatrixMapping loadMappingFromFile(String fileName, String id) throws InvalidPropertiesFormatException, IOException{
		Properties characteristics = new Properties();
		InputStream in;
		
		URL url = Thread.currentThread().getContextClassLoader()
		.getResource(fileName);
		String path = url.getFile();
		path = path.replaceAll("%20", " ");
		
		File dataFile = new File(path);

		in = new FileInputStream(dataFile);
		characteristics.loadFromXML(in);
		
		
		Set<Object> keys =  characteristics.keySet();
		LinkedList<MappingItem> items = new LinkedList<MappingItem>();
		for (Object key : keys) {
			MappingItem item = new MappingItem(Integer.parseInt(characteristics.get(key).toString()),
					key.toString());
			items.add(item);
		}
		MatrixMapping pm = new MatrixMapping(id,items);
		return pm;
	}
	
	@SuppressWarnings("unchecked")
	public Object clone() throws CloneNotSupportedException{
		MatrixMapping m = (MatrixMapping) super.clone();
		m.mappingItems = (LinkedList<MappingItem>) mappingItems.clone();
		return m;
	}
}
