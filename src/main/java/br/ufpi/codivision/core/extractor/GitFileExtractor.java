/**
 * 
 */
package br.ufpi.codivision.core.extractor;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.ufpi.codivision.common.model.PersistenceEntity;
import br.ufpi.codivision.core.model.OperationFile;
import br.ufpi.codivision.core.model.Revision;
import br.ufpi.codivision.core.repository.GitHubUtil;

/**
 * @author Werney Ayala
 *
 */
public class GitFileExtractor implements Callable<PersistenceEntity>{
	
	private String repositoryName;
	private String repositoryOwner;
	private Revision revision;
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	/**
	 * @param repositoryName
	 * @param repositoryOwner
	 * @param revision
	 */
	public GitFileExtractor(String repositoryName, String repositoryOwner, Revision revision) {
		super();
		this.repositoryName = repositoryName;
		this.repositoryOwner = repositoryOwner;
		this.revision = revision;
	}
	

	@Override
	public Revision call() throws Exception {
		
		GitHubUtil gitHubUtil = new GitHubUtil(repositoryOwner, repositoryName);
		
		try {
			
			List<OperationFile> files = gitHubUtil.diff(revision.getExternalId());
			revision.setFiles(files);
			revision.setTotalFiles(files.size());
			revision.setExtracted(true);
			
			return revision;
			
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		
		return null;
	}


}
