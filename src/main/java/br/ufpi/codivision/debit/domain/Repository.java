package br.ufpi.codivision.debit.domain;

import java.util.List;

import org.bson.Document;

/**
 * This class represents a repository.
 */
public class Repository {

	private String id;
	private String key;
	private String name;
	private String path;
	private SCMType scm;
	private String description;
	private List<PersonIdent> contributors;

	/**
	 * Converts a repository to a document.
	 * 
	 * @return the document.
	 */
	public Document toDocument() {
		Document doc = new Document();
		doc.append("key", key).append("name", name).append("path", path).append("scm", scm.toString())
		.append("description", description).append("contributors", PersonIdent.toDocumentList(contributors));
		return doc;
	}

	/**
	 * Converts a document to a repository.
	 * 
	 * @param doc
	 *            the document.
	 * @return a repository.
	 */
	@SuppressWarnings("unchecked")
	public static Repository parseDocument(Document doc) {
		if (doc == null) {
			return null;
		}

		return new Repository(doc.getObjectId("_id").toHexString(), doc.getString("key"), doc.getString("name"),
				doc.getString("path"), SCMType.valueOf(doc.getString("scm")), doc.getString("description"),
				PersonIdent.parseDocuments(doc.get("contributors", List.class)));
	}

	public Repository() {
	}

	public Repository(String id, String key, String name, String path, SCMType scm, String description,
			List<PersonIdent> contributors) {
		this.id = id;
		this.key = key;
		this.name = name;
		this.path = path;
		this.scm = scm;
		this.description = description;
		this.contributors = contributors;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public SCMType getScm() {
		return scm;
	}

	public void setScm(SCMType scm) {
		this.scm = scm;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<PersonIdent> getContributors() {
		return contributors;
	}

	public void setContributors(List<PersonIdent> contributors) {
		this.contributors = contributors;
	}

}