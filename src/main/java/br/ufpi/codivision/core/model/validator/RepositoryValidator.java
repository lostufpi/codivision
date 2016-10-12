/**
 * 
 */
package br.ufpi.codivision.core.model.validator;

import java.io.IOException;

import javax.inject.Inject;

import org.tmatesoft.svn.core.SVNException;

import br.com.caelum.vraptor.validator.SimpleMessage;
import br.ufpi.codivision.common.validator.BaseValidator;
import br.ufpi.codivision.core.dao.UserRepositoryDAO;
import br.ufpi.codivision.core.extractor.service.TaskService;
import br.ufpi.codivision.core.model.Repository;
import br.ufpi.codivision.core.model.UserRepository;
import br.ufpi.codivision.core.model.enums.PermissionType;
import br.ufpi.codivision.core.model.enums.RepositoryType;
import br.ufpi.codivision.core.repository.GitHubUtil;
import br.ufpi.codivision.core.repository.SvnUtil;

/**
 * @author Werney Ayala
 *
 */
public class RepositoryValidator extends BaseValidator{
	
	@Inject private UserRepositoryDAO userRepositoryDAO;
	
	@Inject private TaskService taskService;
	
	public Repository validateRepository(Repository repository){
		
		if(repository.getType().equals(RepositoryType.SVN) && repository.getUrl().endsWith("/"))
			repository.setUrl(repository.getUrl().substring(0, repository.getUrl().lastIndexOf("/")));
		
		if(repository.isLocal() && (repository.getType() == RepositoryType.GITHUB))
			validator.add(new SimpleMessage("erro","repository.git.local.error"));
		
		if(!repository.isLocal()) {
			if (repository.getType().equals(RepositoryType.SVN)) {
				try {
					SvnUtil svnUtil = new SvnUtil(repository.getUrl());
					svnUtil.getRepository().testConnection();
					repository.setRepositoryRoot(svnUtil.getRepositoryRoot()+"");
				} catch (SVNException e) {
					if(e.getMessage().contains("URL"))
						validator.add(new SimpleMessage("error", "repository.url.error"));
					if(e.getMessage().contains("Authentication"))
						validator.add(new SimpleMessage("error", "repository.auth.error"));
				}
			} else {
				repository.setUrl("https://github.com/" + repository.getOwner() + "/" + repository.getName());
				try {
					GitHubUtil gitHubUtil = new GitHubUtil(repository.getOwner(), repository.getName());
					gitHubUtil.testConnection();
				} catch (IOException e) {
					validator.add(new SimpleMessage("error", "repository.git.credentials.error"));
				}
			}
			
		}
		return repository;
		
	}
	
	public void validateReactivate(Repository repository, Long userId) {
		
		if (!repository.isDeleted()) {
			validator.add(new SimpleMessage("error", "repository.add.error"));
		}
		
		UserRepository ur =  userRepositoryDAO.findByIds(userId, repository.getId());
		if ((ur == null)||(ur.getPermission().equals(PermissionType.MEMBER))) {
			validator.add(new SimpleMessage("error", "repository.reactivate.error"));
		}
		
	}
	
	public void canUpdate(Long repositoryId){
		validator.check(!taskService.getToUpdateQueue().contains(repositoryId), new SimpleMessage("error", "repository.update.error"));
	}

	public void validateUpload(Repository repository, Repository rUpdate) {
		validator.check(repository.getUrl().equals(rUpdate.getUrl()), new SimpleMessage("error", "repository.uplaod.invalid"));
	}
	
}
