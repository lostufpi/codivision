/**
 * 
 */
package br.ufpi.codivision.core.controller;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

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
import br.ufpi.codivision.common.annotation.Public;
import br.ufpi.codivision.common.security.UserSession;
import br.ufpi.codivision.core.dao.ConfigurationDAO;
import br.ufpi.codivision.core.dao.ExtractionPathDAO;
import br.ufpi.codivision.core.dao.RepositoryDAO;
import br.ufpi.codivision.core.dao.UserDAO;
import br.ufpi.codivision.core.dao.UserRepositoryDAO;
import br.ufpi.codivision.core.extractor.service.TaskService;
import br.ufpi.codivision.core.model.Configuration;
import br.ufpi.codivision.core.model.DirTree;
import br.ufpi.codivision.core.model.ExtractionPath;
import br.ufpi.codivision.core.model.Repository;
import br.ufpi.codivision.core.model.Revision;
import br.ufpi.codivision.core.model.TestFile;
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
import br.ufpi.codivision.core.model.vo.LineChart;
import br.ufpi.codivision.core.model.vo.RepositoryVO;
import br.ufpi.codivision.core.repository.GitUtil;
import br.ufpi.codivision.core.util.BinaryFile;
import br.ufpi.codivision.core.util.DeleteDir;
import br.ufpi.codivision.core.util.GenerateHashPasswordUtil;
import br.ufpi.codivision.core.util.Outliers;
import br.ufpi.codivision.core.util.QuickSort;

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
		//List<RepositoryType> types = new ArrayList<RepositoryType>(Arrays.asList(RepositoryType.values()));
		List<RepositoryType> types = new ArrayList<RepositoryType>();
		types.add(RepositoryType.GIT);
		result.include("urlImage", urlImage);
		result.include("types", types);
		result.include("repositoryList", repositoryList);
	}
	
	@Post("/repository/addRepository")
	public void addRepository(String url, String branch,
			boolean local, String login, String password){
		
		System.out.println(url+" - "+branch+" - "+local+" - "+login+" - "+password);
		
		String hash = url;
		try {
			hash = GenerateHashPasswordUtil.generateHash(url);
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		System.out.println(hash);
		System.out.println(DeleteDir.deleteDir(new File(GitUtil.getDirectoryToSave().concat(hash))));
	    
	    Configuration configuration = new Configuration();
		configuration.setAddWeight(1.0);
		configuration.setModWeight(1.0);
		configuration.setDelWeight(0.5);
		configuration.setConditionWeight(1.0);
		configuration.setChangeDegradation(5);
		configuration.setMonthlyDegradation(0);
		configuration.setAlertThreshold(60);
		configuration.setExistenceThreshold(80);
		configuration.setTruckFactorThreshold(50);
		configuration.setTimeWindow(TimeWindow.EVER);
		
		Repository repository = new Repository();
		
		//para repositorios do git lab, deve ser adicionado o .git no final. PAra git hub nao eh necessario
		repository.setName(url.split("/")[url.split("/").length-1]);
		repository.setUrl(url);
		repository.setConfiguration(configuration);
		repository.setType(RepositoryType.GIT);
		
		System.out.println("Iniciando a extracao do repositorio "+repository.getUrl());
		
		ExtractionPath path = new ExtractionPath();
		if(branch.startsWith("/"))
			path.setPath(branch);
		else
			path.setPath("/"+branch);
		
		repository.getExtractionPaths().add(path);
		
		System.out.println(path.getPath().substring(1));

		GitUtil util;
		try {
			if(login == null && password == null)
				util = new GitUtil(repository.getUrl(), path.getPath().substring(1), hash);
			else
				util = new GitUtil(repository.getUrl(), path.getPath().substring(1), login, password, hash);	
			
			repository.setRepositoryRoot(repository.getUrl());
			repository.setRevisions(util.getRevisions());
			repository.setLastUpdate(repository.getRevisions().get(0).getDate());
			repository.setLastRevision(repository.getRevisions().get(0).getExternalId());
			
			/* Calcula as revisoes que possuem mais arquivos que o normal */
			List<Integer> qntFileRevision = new ArrayList<Integer>();
			for(Revision revision: repository.getRevisions()) {
				qntFileRevision.add(revision.getTotalFiles());
			}
			
			Collections.sort(qntFileRevision);
			
			int limiar = (int) Outliers.indentify(qntFileRevision);
			
			
			List<Revision> revisions = new ArrayList<Revision>();
			for(Revision revision: repository.getRevisions()) {
				if(revision.getTotalFiles() <= limiar) {
					revisions.add(revision);
				}
			}
			
			repository.setRevisions(revisions);
			
			DirTree tree = new DirTree();
			tree.setText(path.getPath().substring(1));
			tree.setType(NodeType.FOLDER);
			tree.setChildren(util.getDirTree());
			
			repository.getExtractionPaths().get(0).setDirTree(tree);
			
//			Gson json = new Gson();
//			
//			repository.getExtractionPaths().get(0).setDirTreeJson(json.toJson(tree));
//			
			repository = dao.save(repository);

			User user = userDAO.findById(userSession.getUser().getId());

			UserRepository permission = new UserRepository();
			permission.setPermission(PermissionType.OWNER);
			permission.setRepository(repository);
			permission.setUser(user);
			userRepositoryDAO.save(permission);
			
			util.closeRepository();
			System.out.println("Extracao concluida do repositorio "+repository.getUrl());
			result.use(Results.json()).withoutRoot().from("").recursive().serialize();
			
		} catch (Exception e) {
			result.use(Results.json()).withoutRoot().from(e.getMessage()).recursive().serialize();
			e.printStackTrace();
		}

		System.out.println(DeleteDir.deleteDir(new File(GitUtil.getDirectoryToSave().concat(hash))));
	    
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
		if(repository.getType().equals(RepositoryType.GIT)){
			repository.setRepositoryRoot(repository.getUrl());
			repository.setLocal(true);
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
				result.include("notice", new SimpleMessage("info", "repository.update.desktop.message", Severity.INFO));
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
			repository.setDeleted(true);
			
			dao.save(repository);
		
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
		if(repository.getType()==RepositoryType.GITHUB || repository.getType()==RepositoryType.GIT){
			if(!newPath.equals("/")) 
				newPath = newPath.substring(extractionPathDAO.findById(repositoryId, extractionPathId).getPath().length());
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
			
			for(ExtractionPath path: repositoryCurrent.getExtractionPaths()){
				for(ExtractionPath path2:repositoryUpdate.getExtractionPaths()){
					if(path.getPath().equals(path2.getPath())){
						path.setDirTree(path2.getDirTree());
						path.setFeatures(path2.getFeatures());
					}
				}
			}
			
			List<Revision> newRevision = new ArrayList<Revision>();
			
			for(int i = 0; i < repositoryUpdate.getRevisions().size(); i++){
				if(repositoryCurrent.getLastUpdate() == null){
					newRevision.add(repositoryUpdate.getRevisions().get(i));
				}else if(repositoryUpdate.getRevisions().get(i).getDate().getTime() > repositoryCurrent.getLastUpdate().getTime()){
					newRevision.add(repositoryUpdate.getRevisions().get(i));
				}
			}
			
			repositoryCurrent.setLastRevision(repositoryUpdate.getLastRevision());
			repositoryCurrent.getRevisions().addAll(newRevision);
			repositoryCurrent.setLastUpdate(repositoryUpdate.getLastUpdate());
			repositoryCurrent.setTestFiles(repositoryUpdate.getTestFiles());
			
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
		newRepo.setType(repository.getType());
		
		newRepo.setExtractionPaths(new ArrayList<ExtractionPath>());
		for(ExtractionPath aux:repository.getExtractionPaths()){
			aux.setDirTree(null);
			newRepo.getExtractionPaths().add(aux);
		}
		
	    return new FileDownload(BinaryFile.writeObject(newRepo, repository.getName()), "application/octet-stream");
	}
	
	@Permission(PermissionType.MEMBER)
	@Post("/repository/{repositoryId}/path/{extractionPathId}/testConfiguration")
	public void testConfig(Long repositoryId, Long extractionPathId, Configuration configuration) {
		
		Configuration config = dao.getConfiguration(repositoryId);
	
		
		if(configuration.getTimeWindow()!=null){
			if(configuration.getTimeWindow()==TimeWindow.CUSTOM){
				if(configuration.getEndDate()!=null && configuration.getInitDate()!=null){
				config.setTimeWindow(TimeWindow.CUSTOM);
				config.setEndDate(configuration.getEndDate());
				config.setInitDate(configuration.getInitDate());
				configurationDAO.save(config);
				}else if(configuration.getEndDate()!=null){
					Date date = new Date(configuration.getEndDate().getTime() + (24 * 60 * 60 * 1000));
					config.setTimeWindow(TimeWindow.CUSTOM);
					config.setEndDate(date);
					config.setInitDate(configuration.getEndDate());
					configurationDAO.save(config);
				}else if(configuration.getInitDate()!=null){
					config.setTimeWindow(TimeWindow.CUSTOM);
					Date date = new Date(configuration.getInitDate().getTime() + (24 * 60 * 60 * 1000));
					config.setEndDate(date);
					config.setInitDate(configuration.getInitDate());
					configurationDAO.save(config);
				}
				
			}else{
			
				config.setTimeWindow(configuration.getTimeWindow());
				config.refreshTime();
				configurationDAO.save(config);
			}
		}else{
			config.refreshTime();
		}
		
		result.redirectTo(this).testInformation(repositoryId, extractionPathId);
	}
	
	
	@Permission(PermissionType.MEMBER)
	@Get("/repository/{repositoryId}/test/{extractionPathId}/information")
	public void testInformation(Long repositoryId, Long extractionPathId) {
		
		Repository repository = dao.findById(repositoryId);
		
//		/* Calcula as revisoes que possuem mais arquivos que o normal */
//		int limiar = (int) Outliers.indentify(revisionDAO.getTotalFilesOfRevisions(repository.getId()));
//		List<String> revisionOutliers = revisionDAO.getRevisionOutliers(repository.getId(), limiar);
//		repository.setDeletedRevisions(revisionOutliers);
//
//		/* Exclui as revisoes com mais arquivos que o normal */
//		if(!revisionOutliers.isEmpty()){
//			for(Long temp : revisionDAO.getIdFromRevisionOutliers(repository.getId(), revisionOutliers)) {
//				Revision revision = revisionDAO.findById(temp);
//				for(OperationFile aux: revision.getFiles()){
//					aux.setLineAdd(0);
//					aux.setLineCondition(0);
//					aux.setLineDel(0);
//					aux.setLineMod(0);
//					aux.setPath("");
//					fileDAO.save(aux);
//				}
//					
//				
//				
//			}
//		}
		
		RepositoryVO repositoryVO = new RepositoryVO(repository);

		ExtractionPath extractionPath = extractionPathDAO.findById(extractionPathId);
		
		Configuration configuration = repository.getConfiguration();
		configuration.refreshTime();
		
		List<TimeWindow> windows = new ArrayList<TimeWindow>(Arrays.asList(TimeWindow.values()));
		
		List<ExtractionPath> extractionPaths = extractionPathDAO.getExtractionPaths(repositoryId);
		
		result.include("testFiles",repository.getTestFiles());
		result.include("windows", windows);
		result.include("configuration", configuration);
		result.include("repository", repositoryVO);
		result.include("extractionPath", extractionPath);
		result.include("extractionPaths", extractionPaths);
		
	}
	
	@Permission(PermissionType.MEMBER)
	@Post("/repository/{repositoryId}/authors")
	public void getAuthors(Long repositoryId) {
		
		result.use(Results.json()).withoutRoot().from(dao.getAuthors(repositoryId)).recursive().serialize();
	}
	
	@Permission(PermissionType.MEMBER)
	@Post("/repository/{repositoryId}/rankingDevelops")
	public void getRankingDevelops(Long repositoryId) {
		
		LineChart chart = dao.getContribuitionQntLine(repositoryId, "/");
		
		List<AuthorPercentage> list = new ArrayList<AuthorPercentage>();
		for(int i = 0; i<chart.getDataCategories().length; i++){
			AuthorPercentage author = new AuthorPercentage(chart.getDataCategories()[i], (double) chart.getDataSeries().get(0).getData()[i], chart.getDataSeries().get(1).getData()[i]);
			list.add(author);
		}
		
		QuickSort.sort2(list);
		
		result.use(Results.json()).withoutRoot().from(list).recursive().serialize();
	}
	
	@Permission(PermissionType.MEMBER)
	@Post("/repository/{repositoryId}/authorHistoric")
	public void getAuthorHistoric(Long repositoryId, String author) {
		
		LineChart chart = new LineChart();
		chart = dao.getTestCommitsHistoryAuthor(repositoryId, author);
		result.use(Results.json()).withoutRoot().from(chart).recursive().serialize();
	}
	
	@Permission(PermissionType.MEMBER)
	@Post("/repository/{repositoryId}/projectHistoric")
	public void getProjectHistoric(Long repositoryId) {
		
		LineChart chart = new LineChart();
		chart = dao.getTestCommitsHistory(repositoryId);
		result.use(Results.json()).withoutRoot().from(chart).recursive().serialize();
	}
	
	@Permission(PermissionType.MEMBER)
	@Post("/repository/{repositoryId}/path/{extractionPathId}/alterationsQntLine")
	public void getAlterationsQntLine(Long repositoryId,Long extractionPathId, String newPath){

		Repository repository = dao.findById(repositoryId);
		
		if(repository.getType()==RepositoryType.SVN && newPath.equals("/")){
			newPath = extractionPathDAO.findById(repositoryId, extractionPathId).getPath();
		}
		//referente ao /master
		if(repository.getType()==RepositoryType.GITHUB || repository.getType()==RepositoryType.GIT){
			if(!newPath.equals("/"))
				newPath = newPath.substring(extractionPathDAO.findById(repositoryId, extractionPathId).getPath().length());
		}
		LineChart percentage = dao.getContribuitionQntLine(repositoryId, repository.getUrl().substring(repository.getRepositoryRoot().length())+newPath);
		result.use(Results.json()).withoutRoot().from(percentage).recursive().serialize();
		
		
	}
	
	@Permission(PermissionType.MEMBER)
	@Post("/repository/{repositoryId}/percentageContribuition")
	public void getAlterationsQntLine(Long repositoryId){
		
		result.use(Results.json()).withoutRoot().from(dao.getPercentageContribuition(repositoryId)).recursive().serialize();
			
	}

	@Permission(PermissionType.MEMBER)
	@Post("/repository/{repositoryId}/testFile")
	public void getTestFiles(Long repositoryId) {
		
		result.use(Results.json()).withoutRoot().from(dao.findById(repositoryId).getTestFiles()).recursive().serialize();
	}
	
	@Permission(PermissionType.MEMBER)
	@Post("/repository/{repositoryId}/addTestPath")
	public void addTestPath(Long repositoryId, String newPathTest) {
		Repository repository = dao.findById(repositoryId);
		for(TestFile file: repository.getTestFiles()){
			if(file.getPath().equals(newPathTest)){
				result.use(Results.json()).withoutRoot().from("").recursive().serialize();
				return;
			}
		}
		
		TestFile file = new TestFile();
		file.setPath(newPathTest);
		repository.getTestFiles().add(file);
		dao.save(repository);
		
		result.use(Results.json()).withoutRoot().from("").recursive().serialize();
		return;
	}
	
	@Permission(PermissionType.MEMBER)
	@Post("/repository/{repositoryId}/deleteTestPath")
	public void deleteTestPath(Long repositoryId, int pathId) {
		Repository repository = dao.findById(repositoryId);
		
		repository.getTestFiles().remove(pathId);
		
		dao.save(repository);
		
		result.use(Results.json()).withoutRoot().from("").recursive().serialize();
		
	}
	
	@Public
	@Post("/repository/remoteUpdate")
	public void remoteUpdate(String repository) {
		Gson json = new Gson();
		Repository repositoryUpdate = json.fromJson(repository, Repository.class);
		Repository repositoryCurrent = dao.findByUrl(repositoryUpdate.getUrl());
		if(repositoryCurrent==null){
			result.use(Results.json()).withoutRoot().from("Repositorio inexistente").recursive().serialize();
		}else{

		repositoryCurrent.setRepositoryRoot(repositoryUpdate.getRepositoryRoot());
		
		for(ExtractionPath path: repositoryCurrent.getExtractionPaths()){
			for(ExtractionPath path2: repositoryUpdate.getExtractionPaths()){
				if(path.getPath().equals(path2.getPath())){
					path.setDirTree(path2.getDirTree());
				}
			}
		}
		
		List<Revision> newRevision = new ArrayList<Revision>();
		
		for(int i = 0; i < repositoryUpdate.getRevisions().size(); i++){
			if(repositoryCurrent.getLastUpdate() == null){
				newRevision.add(repositoryUpdate.getRevisions().get(i));
			}else if(repositoryUpdate.getRevisions().get(i).getDate().getTime() > repositoryCurrent.getLastUpdate().getTime()){
				newRevision.add(repositoryUpdate.getRevisions().get(i));
			}
		}
		
		repositoryCurrent.setLastRevision(repositoryUpdate.getLastRevision());
		repositoryCurrent.getRevisions().addAll(newRevision);
		repositoryCurrent.setLastUpdate(repositoryUpdate.getLastUpdate());
		
		for(TestFile file:repositoryUpdate.getTestFiles()){
			boolean check = false;
			for(TestFile file2:repositoryCurrent.getTestFiles()){
				if(file.getPath().equals(file2.getPath())){
					check = true;
				}
			}
			if(!check){
				repositoryCurrent.getTestFiles().add(file);
			}
		}
		
		dao.save(repositoryCurrent);
		
		result.use(Results.json()).withoutRoot().from("Repositorio atualizado").recursive().serialize();
	}
		
	}
	

}