/**
 * 
 */
package br.ufpi.codivision.core.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.observer.download.Download;
import br.com.caelum.vraptor.observer.download.FileDownload;
import br.com.caelum.vraptor.observer.upload.UploadSizeLimit;
import br.com.caelum.vraptor.observer.upload.UploadedFile;
import br.com.caelum.vraptor.validator.Severity;
import br.com.caelum.vraptor.validator.SimpleMessage;
import br.com.caelum.vraptor.view.Results;
import br.ufpi.codivision.common.annotation.Permission;
import br.ufpi.codivision.common.security.UserSession;
import br.ufpi.codivision.core.dao.ConfigurationDAO;
import br.ufpi.codivision.core.dao.ExtractionPathDAO;
import br.ufpi.codivision.core.dao.OperationFileDAO;
import br.ufpi.codivision.core.dao.RepositoryDAO;
import br.ufpi.codivision.core.dao.RevisionDAO;
import br.ufpi.codivision.core.dao.UserDAO;
import br.ufpi.codivision.core.dao.UserRepositoryDAO;
import br.ufpi.codivision.core.extractor.service.TaskService;
import br.ufpi.codivision.core.model.Configuration;
import br.ufpi.codivision.core.model.DirTree;
import br.ufpi.codivision.core.model.ExtractionPath;
import br.ufpi.codivision.core.model.OperationFile;
import br.ufpi.codivision.core.model.Repository;
import br.ufpi.codivision.core.model.Revision;
import br.ufpi.codivision.core.model.User;
import br.ufpi.codivision.core.model.UserRepository;
import br.ufpi.codivision.core.model.enums.NodeType;
import br.ufpi.codivision.core.model.enums.PermissionType;
import br.ufpi.codivision.core.model.enums.RepositoryType;
import br.ufpi.codivision.core.model.enums.TimeWindow;
import br.ufpi.codivision.core.model.validator.ConfigurationValidator;
import br.ufpi.codivision.core.model.validator.ExtractionPathValidator;
import br.ufpi.codivision.core.model.validator.RepositoryValidator;
import br.ufpi.codivision.core.model.vo.AuthorPercentage;
import br.ufpi.codivision.core.model.vo.RepositoryVO;
import br.ufpi.codivision.core.util.BinaryFile;

/**
 * @author Werney Ayala
 *
 */
@Controller
public class RepositoryController {
	
	@Inject private Result result;
	@Inject private UserSession userSession;
	
	@Inject private RepositoryDAO dao;
	@Inject private UserDAO userDAO;
	@Inject private UserRepositoryDAO userRepositoryDAO;
	@Inject private ConfigurationDAO configurationDAO;
	@Inject private ExtractionPathDAO extractionPathDAO;
	@Inject private OperationFileDAO fileDAO;
	@Inject private RevisionDAO revisionDAO;
	

	@Inject private RepositoryValidator validator;
	@Inject private ConfigurationValidator configurationValidator;
	@Inject private ExtractionPathValidator extractionPathValidator;
	
	@Inject private TaskService taskService;
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Permission(PermissionType.MEMBER)
	@Get("/repository/{repositoryId}")
	public void show(Long repositoryId) {
		
		Repository repository = dao.findById(repositoryId);
		RepositoryVO repositoryVO = new RepositoryVO(repository);
		List<ExtractionPath> extractionPathList = extractionPathDAO.getExtractionPaths(repositoryId);
		List<RepositoryVO> repositoryList = dao.listMyRepositories(userSession.getUser().getId());
		List<UserRepository> userRepositoryList = userRepositoryDAO.listByRepositoryId(repositoryId);
		
		result.include("repository", repositoryVO);
		result.include("repositoryList", repositoryList);
		result.include("extractionPathList", extractionPathList);
		result.include("userRepositoryList", userRepositoryList);
		
	}
	
	@Get
	public void list(){
		List<RepositoryVO> repositoryList = dao.listMyRepositories(userSession.getUser().getId());
		String urlImage = userSession.getUser().getGravatarImageUrl() + "?s=217";
		List<RepositoryType> types = new ArrayList<RepositoryType>(Arrays.asList(RepositoryType.values()));
		result.include("urlImage", urlImage);
		result.include("types", types);
		result.include("repositoryList", repositoryList);
	}
	
	@Post
	public void add(Repository repository, ExtractionPath extractionPath) {
		
		if (repository.getType().equals(RepositoryType.GITHUB)) {
			
			if(repository.getUrl().endsWith(".git"))
				repository.setUrl(repository.getUrl().replace(".git", ""));
			
			repository.setRepositoryRoot(repository.getUrl());
			
			String url = repository.getUrl().replace("https://github.com/", "");
			String[] split = url.split("/"); 
			repository.setOwner(split[0]);
			repository.setName(split[1]);
			
		}
		
		repository = validator.validateRepository(repository);
		validator.onErrorRedirectTo(this.getClass()).list();
		
		extractionPathValidator.validateAdd(extractionPath, repository);
		extractionPathValidator.onErrorRedirectTo(this.getClass()).list();
		
		Repository repositoryPersisted = dao.findByUrl(repository.getUrl());
		
		if (repositoryPersisted == null) {
			
			/* O repositório é novo */
			
			Configuration configuration = new Configuration();
			configuration.setAddWeight(1.0);
			configuration.setModWeight(1.0);
			configuration.setDelWeight(0.5);
			configuration.setConditionWeight(1.0);
			configuration.setChangeDegradation(5);
			configuration.setMonthlyDegradation(10);
			configuration.setAlertThreshold(60);
			configuration.setExistenceThreshold(80);
			configuration.setTruckFactorThreshold(50);
			configuration.setTimeWindow(TimeWindow.LAST_SIX_MONTHS);

			configurationValidator.validate(configuration);
			configurationValidator.onErrorRedirectTo(this.getClass()).list();
			
			DirTree dirTree = new DirTree();
			dirTree.setText(extractionPath.getPath().substring(1));
			dirTree.setType(NodeType.FOLDER);
			extractionPath.setDirTree(dirTree);

			repository.setId(null);
			repository.getExtractionPaths().add(extractionPath);
			repository.setConfiguration(configuration);

			repository = dao.save(repository);

			User user = userDAO.findById(userSession.getUser().getId());

			UserRepository permission = new UserRepository();
			permission.setPermission(PermissionType.OWNER);
			permission.setRepository(repository);
			permission.setUser(user);
			userRepositoryDAO.save(permission);
			
			//Adiciona na Fila para obter a arvore de diretório o primeiro elemento da lista de ExtractionPath
			//pois é considerado que só exista ele nessa lista, pois o repositório acaba de ser criado
			//isso só acontece se o repositório não for local!!!!!
			if(!repository.isLocal())
				taskService.addToUpdate(repository.getId());
			
			if(repository.isLocal())
				result.include("notice", new SimpleMessage("info", "Para realizar a atualização, é necessário realizar a extração por meio da ferramenta CoDiVisionDesktop", Severity.INFO));
			else
				result.include("notice", new SimpleMessage("info", "repository.update.message", Severity.INFO));
			
		} else {
			
			/* O repositório já existe */
			
			validator.validateReactivate(repositoryPersisted, userSession.getUser().getId());
			validator.onErrorRedirectTo(this.getClass()).list();
			
			repositoryPersisted.setDeleted(false);
			dao.save(repositoryPersisted);
			
		}
		
		result.redirectTo(this).list();

	}
	
	/**
	 * This method modifies the name of a repository
	 * @param repositoryId - Id repository to be modified
	 * @param repositoryName - New repository name to be saved
	 */
	@Permission(PermissionType.MEMBER)
	@Post("/repository/{repositoryId}/edit")
	public void edit(Long repositoryId,String repositoryName){
		
		Repository repository = dao.findById(repositoryId);
		repository.setName(repositoryName);
		dao.save(repository);
		result.redirectTo(this).show(repositoryId);
		
	}
	
	/**
	 * This method is responsible for deleting a repository
	 * @param repositoryId - Id of the repository to be deleted
	 */
	@Permission(PermissionType.MEMBER)
	@Get("/repository/{repositoryId}/delete")
	public void delete(Long repositoryId){
		
		Repository repository = dao.findById(repositoryId);
		UserRepository ur = userRepositoryDAO.findByIds(userSession.getUser().getId(), repositoryId);
		
		if(ur.getPermission() == PermissionType.OWNER){
			//repository.setDeleted(true);
			userRepositoryDAO.delete(userRepositoryDAO.findByLogin(userSession.getUser().getLogin(), repositoryId).getId());
			repository.setConfiguration(null);
			repository.setDeleted(true);
			repository.setUrl(null);
			repository.setName(null);
			dao.save(repository);
			
			for(ExtractionPath aux: repository.getExtractionPaths()){
			//	dirTreeDAO.delete(aux.getDirTree().getId());
				extractionPathDAO.delete(aux.getId());
			}
			//configurationDAO.delete(repositoryId);
			
			for(Revision aux: repository.getRevisions()){
				for(OperationFile aux1: aux.getFiles()){
					fileDAO.delete(aux1.getId());
				}
				revisionDAO.delete(aux.getId());
			}
			dao.delete(repositoryId);
		} else {
			userRepositoryDAO.delete(ur.getId());		
		}
		
		result.include("notice", new SimpleMessage("success", "repository.delete.success", Severity.INFO));
		result.redirectTo(this).list();
	}
	
	@Permission(PermissionType.MEMBER)
	@Post("/repository/{repositoryId}/path/{extractionPathId}/configuration")
	public void config(Long repositoryId, Long extractionPathId, Configuration configuration) {
		
		configurationValidator.validate(configuration);
		configurationValidator.onErrorRedirectTo(this.getClass()).chart(repositoryId,extractionPathId);
		
		Configuration config = dao.getConfiguration(repositoryId);
		configuration.setId(config.getId());
		
		configurationDAO.save(configuration);
		
		result.redirectTo(this).chart(repositoryId,extractionPathId);
	}
	
	@Permission(PermissionType.MEMBER)
	@Get("/repository/{repositoryId}/path/{extractionPathId}/chart")
	public void chart(Long repositoryId, Long extractionPathId) {
		
		Repository repository = dao.findById(repositoryId);
		RepositoryVO repositoryVO = new RepositoryVO(repository);

		ExtractionPath extractionPath = extractionPathDAO.findById(extractionPathId);
		
		Configuration configuration = repository.getConfiguration();
		configuration.refreshTime();
		
		List<TimeWindow> windows = new ArrayList<TimeWindow>(Arrays.asList(TimeWindow.values()));
		
		List<ExtractionPath> extractionPaths = extractionPathDAO.getExtractionPaths(repositoryId);
		
		result.include("windows", windows);
		result.include("configuration", configuration);
		result.include("repository", repositoryVO);
		result.include("extractionPath", extractionPath);
		result.include("extractionPaths", extractionPaths);
		
	}
	
	@Permission(PermissionType.MEMBER)
	@Post("/repository/{repositoryId}/path/{extractionPathId}/alterations")
	public void getAlterations(Long repositoryId,Long extractionPathId, String newPath){
		
		Repository repository = dao.findById(repositoryId);
		
		if(repository.getType()==RepositoryType.SVN && newPath.equals("/")){
			newPath = extractionPathDAO.findById(repositoryId, extractionPathId).getPath();
		}
		//referente ao /master
		if(repository.getType()==RepositoryType.GITHUB){
			if(!newPath.equals("/"))
				newPath = newPath.substring(7);
		}
			
		List<AuthorPercentage> percentage = dao.getPercentage(repositoryId,repository.getUrl().substring(repository.getRepositoryRoot().length())+newPath);
		result.use(Results.json()).withoutRoot().from(percentage).recursive().serialize();
		
		
	}
	
	@Permission(PermissionType.MEMBER)
	@Post("/repository/{repositoryId}/update")
	public void update(Long repositoryId) {
		
		validator.canUpdate(repositoryId);
		validator.onErrorRedirectTo(this.getClass()).show(repositoryId);
		
		taskService.addToUpdate(repositoryId);
		
		result.include("notice", new SimpleMessage("info", "repository.update.message", Severity.INFO));
		result.redirectTo(this).show(repositoryId);
		
	}
	
	@Permission(PermissionType.MEMBER)
	@Post("/repository/{repositoryId}/upload")
	@UploadSizeLimit(sizeLimit=100 * 1024 * 1024, fileSizeLimit=100 * 1024 * 1024)
	public void upload(long repositoryId, UploadedFile fileRepository){

		try {

			Repository repositoryUpdate = (Repository) BinaryFile.readObject(fileRepository.getFile());

			Repository repositoryCurrent = dao.findById(repositoryId);

			validator.validateUpload(repositoryCurrent,repositoryUpdate);
			validator.onErrorRedirectTo(this.getClass()).list();
			
			repositoryCurrent.setRepositoryRoot(repositoryUpdate.getRepositoryRoot());
			repositoryCurrent.setExtractionPaths(repositoryUpdate.getExtractionPaths());	
			repositoryCurrent.setRevisions(repositoryUpdate.getRevisions());
			repositoryCurrent.setLastRevision(repositoryUpdate.getLastRevision());
			repositoryCurrent.setLastUpdate(repositoryUpdate.getLastUpdate());
			
			dao.save(repositoryCurrent);
			
			result.include("notice", new SimpleMessage("success", "repository.upload.sucess", Severity.INFO));
			
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		
		result.redirectTo(this).list();

	}
	
	@Permission(PermissionType.MEMBER)
	@Post("/repository/{repositoryId}")
	public Download download(Long repositoryId) throws IOException {
		
		Repository repository = dao.findById(repositoryId);
		Repository newRepo = new Repository();
		
		newRepo.setUrl(repository.getUrl());
		
		newRepo.setExtractionPaths(new ArrayList<ExtractionPath>());
		for(ExtractionPath aux:repository.getExtractionPaths()){
			aux.setDirTree(null);
			newRepo.getExtractionPaths().add(aux);
		}
		
	    return new FileDownload(BinaryFile.writeObject(newRepo, repository.getName()), "application/octet-stream");
	}

}
