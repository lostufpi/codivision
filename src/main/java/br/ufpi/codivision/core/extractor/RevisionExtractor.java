/**
 * 
 */
package br.ufpi.codivision.core.extractor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNException;

import br.ufpi.codivision.common.model.PersistenceEntity;
import br.ufpi.codivision.core.model.ExtractionPath;
import br.ufpi.codivision.core.model.Repository;
import br.ufpi.codivision.core.model.Revision;
import br.ufpi.codivision.core.model.enums.RepositoryType;
import br.ufpi.codivision.core.repository.GitHubUtil;
import br.ufpi.codivision.core.repository.SvnUtil;

/**
 * @author Werney Ayala
 *
 */
public class RevisionExtractor implements Callable<PersistenceEntity>{
	
	private Repository repository;
	private List<ExtractionPath> paths;
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	/**
	 * 
	 */
	public RevisionExtractor(Repository repository, List<ExtractionPath> paths) {
		this.repository = repository;
		this.paths = paths;
	}
	
	@Override
	public Repository call() throws Exception {
		
		if (repository.getType() == RepositoryType.SVN)
			return updateSVN(repository, paths);
		else
			return updateGit(repository);
		
	}
	
	private Repository updateSVN(Repository repository, List<ExtractionPath> paths) {
		
		try {

			SvnUtil svnUtil = new SvnUtil(repository.getUrl());

			/* Se o repositório estiver atualizado não faz nada */
			Long lastRevision = svnUtil.getLatestRevision();
			if ((repository.getLastRevision() != null) && (Long.parseLong(repository.getLastRevision()) == lastRevision)) {
				repository.setLastUpdate(new Date());
				return null;
			}
			
			/* Adiciona os caminhos quwe serão obtidos das revisões */
			String[] targetPaths = new String[paths.size()];
			String diffPath = repository.getUrl().substring(repository.getRepositoryRoot().length());
			for(int i = 0; i < targetPaths.length; i++)
				targetPaths[i] = diffPath + paths.get(i).getPath();
				
			/* Obtem os ultimos commits feitos*/
			List<Revision> revisions;
			if ((repository.getLastRevision() == null) || (repository.getLastRevision().trim().isEmpty())) {

				Calendar c = Calendar.getInstance();
				c.add(Calendar.MONTH, -6);
				revisions = svnUtil.getRevisionLog(c.getTime(),targetPaths);

			} else { // Ver isso as vezes a revisão +1 não existe tratar isso
				revisions = svnUtil.getRevisionLog(Long.parseLong(repository.getLastRevision()) + 1, lastRevision, targetPaths);
			}

			repository.setRevisions(revisions);
			repository.setLastRevision(lastRevision + "");
			repository.setLastUpdate(new Date());

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
	
	private Repository updateGit(Repository repository) {
		
		GitHubUtil gitHubUtil = new GitHubUtil(repository.getOwner(), repository.getName());
		
		List<Revision> revisions = gitHubUtil.getRevisionLog(100);
		List<Revision> toADD = new ArrayList<Revision>();
		if((repository.getLastRevision() == null) || (repository.getLastRevision().trim().isEmpty())) {
			repository.setRevisions(revisions);
		} else {
			for (int i = 0; i < revisions.size(); i++) {
				Revision temp = revisions.get(i);
				if (temp.getExternalId().equals(repository.getLastRevision())) {
					if(i == 0)
						return null;
					break;
				}
				toADD.add(temp);
			}
			repository.setRevisions(toADD);
		}
		
		repository.setLastRevision(revisions.get(0).getExternalId());
		repository.setLastUpdate(new Date());
		
		return repository;
		
	}
	
}
