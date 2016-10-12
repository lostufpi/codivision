/**
 * 
 */
package br.ufpi.codivision.core.model.validator;

import javax.inject.Inject;

import org.tmatesoft.svn.core.SVNException;

import br.com.caelum.vraptor.validator.SimpleMessage;
import br.ufpi.codivision.common.validator.BaseValidator;
import br.ufpi.codivision.core.dao.ExtractionPathDAO;
import br.ufpi.codivision.core.model.ExtractionPath;
import br.ufpi.codivision.core.model.Repository;
import br.ufpi.codivision.core.model.enums.RepositoryType;
import br.ufpi.codivision.core.repository.SvnUtil;

/**
 * @author Werney Ayala
 *
 */
public class ExtractionPathValidator extends BaseValidator{
	
	@Inject private ExtractionPathDAO extractionPathDAO;
	
	public void validateAdd(ExtractionPath extractionPath, Repository repository){
		
		if (extractionPath == null) {
			extractionPath = new ExtractionPath();
			extractionPath.setPath("/");
		}
		
		if(!extractionPath.getPath().startsWith("/"))
			extractionPath.setPath("/" + extractionPath.getPath());
		if(!repository.isLocal())
		if (repository.getType().equals(RepositoryType.SVN)) {
			try {
				//verifica se não possui outro extractionPath cadastrado no mesmo repositorio
				//caso ja exista algum extractionPath ja cadastrado com esse mesmo caimnho,
				//entao cria mensagem de error e retorna para não realizar outros testes desnecessários
				if(extractionPathDAO.findByPath(repository.getId(), extractionPath.getPath()) != null){
					validator.add(new SimpleMessage("error", "repository.extraction.path.unique"));
					return;
				}
				
				//concatena e faz o teste de conexão 	
				SvnUtil svnUtil = new SvnUtil(repository.getUrl()+extractionPath.getPath());
				svnUtil.getRepository().testConnection();

			} catch (SVNException e) {
				validator.add(new SimpleMessage("error", "repository.extraction.path.invalid"));
			}
		} else {
			
			if(!extractionPath.getPath().equals("/master")){
				validator.add(new SimpleMessage("error", "repository.git.type"));
				return;
			}
			
				//verifica se não possui outro extractionPath cadastrado no mesmo repositorio
				validator.check(extractionPathDAO.findByPath(repository.getId(), extractionPath.getPath()) == null, new SimpleMessage("error", "repository.extraction.path.unique"));
			
		}
		
	}
	
	public void validateDelete(Long extractionPathId, Repository repository){
		//caso os repositorios sejam git, não é possivel remover extractionPAth
		if(repository.getType().equals(RepositoryType.GITHUB)){
			validator.add(new SimpleMessage("error", "repository.extraction.path.git"));
			return;
		}
		//verifica se o extractionPath pertence ao repositorio
		validator.check(extractionPathDAO.findById(repository.getId(), extractionPathId) != null, new SimpleMessage("error", "repository.permission.error"));
	}
	
}
