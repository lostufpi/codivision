/**
 * 
 */
package br.ufpi.codivision.core.extractor;

import java.io.IOException;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNException;

import br.ufpi.codivision.common.model.PersistenceEntity;
import br.ufpi.codivision.core.model.DirTree;
import br.ufpi.codivision.core.model.ExtractionPath;
import br.ufpi.codivision.core.model.Repository;
import br.ufpi.codivision.core.model.enums.RepositoryType;
import br.ufpi.codivision.core.repository.GitHubUtil;
import br.ufpi.codivision.core.repository.SvnUtil;

/**
 * @author Werney Ayala
 *
 */
public class DirTreeExtractor implements Callable<PersistenceEntity>{
	
	private ExtractionPath extractionPath;
	private Repository repository;
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	/**
	 * 
	 */
	public DirTreeExtractor(Repository repository, ExtractionPath extractionPath) {
		this.repository = repository;
		this.extractionPath = extractionPath;
	}

	@Override
	public DirTree call() throws Exception {
		
		try {
			
			DirTree dirTree = extractionPath.getDirTree();
			
			if (repository.getType().equals(RepositoryType.SVN)) {
				SvnUtil svnUtil = new SvnUtil(repository.getUrl());
				//Gera uma string que a a diferença entre a url completa com a url raiz
				String diffPath = repository.getUrl().substring(repository.getRepositoryRoot().length());
				if(!extractionPath.getPath().equals("/")){
					diffPath = diffPath + extractionPath.getPath();
				}
				dirTree.setChildren(svnUtil.getDirTree(diffPath));
			} else {
				GitHubUtil gitHubUtil = new GitHubUtil(repository.getOwner(), repository.getName());
				dirTree.setChildren(gitHubUtil.getDirTree(repository.getLastRevision()));
			}
			
			return dirTree;

		} catch (SVNException e) {
			log.error(e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		
		return null;
		
	}

}
