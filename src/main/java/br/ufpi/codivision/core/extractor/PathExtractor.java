package br.ufpi.codivision.core.extractor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNException;

import br.ufpi.codivision.common.model.PersistenceEntity;
import br.ufpi.codivision.core.model.ExtractionPath;
import br.ufpi.codivision.core.model.Repository;
import br.ufpi.codivision.core.model.Revision;
import br.ufpi.codivision.core.repository.SvnUtil;

/**
 * @author Irvayne Matheus
 *
 */
public class PathExtractor implements Callable<PersistenceEntity> {
	
	private Repository repository;
	private ExtractionPath path;
	private final Logger log = LoggerFactory.getLogger(getClass());

	public PathExtractor(Repository repository, ExtractionPath path) {
		this.repository = repository;
		this.path = path;
	}
	
	@Override
	public PersistenceEntity call() throws Exception {
		
		return updateRepository(repository,path);
	}
	
	private Repository updateRepository(Repository repository, ExtractionPath path){
		
		
		try {
		
		SvnUtil svnUtil = new SvnUtil(repository.getUrl());
		
		String diffPath = repository.getUrl().substring(repository.getRepositoryRoot().length());
		
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, -6);
		
		String[] pathRevisionExtraction = new String[1];
		pathRevisionExtraction[0] = diffPath + path.getPath();
		
		List<Revision> revisions = svnUtil.getRevisionLog(c.getTime(), Long.parseLong(repository.getLastRevision()), pathRevisionExtraction);
		
		repository.setRevisions(revisions);
		
		return repository;
		
		} catch (SVNException e) {
			log.error(e.getMessage());
		} catch (FileNotFoundException e) {
			log.error(e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		
		return null;
	}

}
