/**
 * 
 */
package br.ufpi.codivision.core.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import com.google.gson.Gson;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.validator.Severity;
import br.com.caelum.vraptor.validator.SimpleMessage;
import br.com.caelum.vraptor.view.Results;
import br.ufpi.codivision.common.annotation.Permission;
import br.ufpi.codivision.common.annotation.Public;
import br.ufpi.codivision.common.security.UserSession;
import br.ufpi.codivision.core.dao.AuthorDAO;
import br.ufpi.codivision.core.dao.ConfigurationDAO;
import br.ufpi.codivision.core.dao.RepositoryDAO;
import br.ufpi.codivision.core.dao.UserDAO;
import br.ufpi.codivision.core.dao.UserRepositoryDAO;
import br.ufpi.codivision.core.extractor.model.Extraction;
import br.ufpi.codivision.core.extractor.model.ExtractionType;
import br.ufpi.codivision.core.extractor.model.RepositoryCredentials;
import br.ufpi.codivision.core.extractor.service.TaskService;
import br.ufpi.codivision.core.model.Author;
import br.ufpi.codivision.core.model.Configuration;
import br.ufpi.codivision.core.model.ExtractionPath;
import br.ufpi.codivision.core.model.Repository;
import br.ufpi.codivision.core.model.Revision;
import br.ufpi.codivision.core.model.TestFile;
import br.ufpi.codivision.core.model.User;
import br.ufpi.codivision.core.model.UserRepository;
import br.ufpi.codivision.core.model.enums.PermissionType;
import br.ufpi.codivision.core.model.enums.RepositoryType;
import br.ufpi.codivision.core.model.enums.TimeWindow;
import br.ufpi.codivision.core.model.validator.ConfigurationValidator;
import br.ufpi.codivision.core.model.validator.RepositoryValidator;
import br.ufpi.codivision.core.model.vo.AuthorPercentage;
import br.ufpi.codivision.core.model.vo.LineChart;
import br.ufpi.codivision.core.model.vo.RepositoryVO;
import br.ufpi.codivision.core.repository.GitUtil;
import br.ufpi.codivision.core.util.QuickSort;
import br.ufpi.codivision.debit.model.File;

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
	@Inject private AuthorDAO authorDAO;
	@Inject private UserRepositoryDAO userRepositoryDAO;
	@Inject private ConfigurationDAO configurationDAO;

	@Inject private RepositoryValidator validator;
	@Inject private ConfigurationValidator configurationValidator;

	@Inject private TaskService taskService;


	@Permission(PermissionType.MEMBER)
	@Get("/repository/{repositoryId}")
	public void show(Long repositoryId) {

		Repository repository = dao.findById(repositoryId);
		RepositoryVO repositoryVO = new RepositoryVO(repository);
		List<RepositoryVO> repositoryList = dao.listMyRepositories(userSession.getUser().getId());
		List<UserRepository> userRepositoryList = userRepositoryDAO.listByRepositoryId(repositoryId);
		List<Revision> revisions = repository.getRevisions();
		List<Author> authors = new ArrayList<Author>();
		Author aux = null;
		for (Revision rev : revisions) {
			aux = rev.getAuthor();
			if(aux.getAutFather()!=null) {
				aux=authorDAO.findById(aux.getAutFather());
			}
			if (!authors.contains(aux)) {
				authors.add(aux);
			}
		}
		
		result.include("authors", authors);
		result.include("revisions", revisions);
		result.include("repository", repositoryVO);
		result.include("repositoryList", repositoryList);
		result.include("userRepositoryList", userRepositoryList);

	}
	
	/**
	 * This method modifies the email of an author
	 * @param Author - The author to modify the email
	 * @param String - New email of the author
	 */
	@Permission(PermissionType.MEMBER)
	@Post("/repository/{repositoryId}/changemail")
	public void changemail(Long repositoryId,Long authorName,String newEmail){
		Author author = authorDAO.findById(authorName);
		author.setEmail(newEmail);
		authorDAO.save(author);
		result.redirectTo(this).show(repositoryId);

	}
	
	
	@Permission(PermissionType.MEMBER)
	@Post("/repository/{repositoryId}/removeAuthor")
	public void removeAuthor(Long repositoryId,Long authorName,String removeAuthors){
		String[] ids = removeAuthors.split(";");
		Long id = null;
		Author son;
		Author father = authorDAO.findById(authorName);
		for(int i=0;i<ids.length;i++) {
			id=Long.parseLong(ids[i]);
			if (!id.equals(authorName)) {
				son = authorDAO.findById(id);
				son.setAutFather(authorName);
				if (son.getLastGamePoint() != null) {
					father.addNumberOfLinesCode(son.getNumberOfLinesCode());
					father.addNumberOfLinesTest(son.getNumberOfLinesTest());
					father.setBmedal(son.getBmedal());
					father.setGmedal(son.getGmedal());
					father.setSmedal(son.getSmedal());
				}
				authorDAO.save(father);
				authorDAO.save(son);
			}
		}
		result.redirectTo(this).show(repositoryId);

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
		//TODO
		if(taskService.getTaskQueue().size()!=0) {
			result.include("notice", new SimpleMessage("success", "repository.update.message", Severity.INFO));
		}
	}

	@Post("/repository/add")
	public void add(String url, String branch, boolean local, String login, String password){

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

		//para repositorios do git lab, deve ser adicionado o .git no final.
		if(url.contains(".git")) {
			repository.setName(url.split("/")[url.split("/").length - 1].split("[.]")[0]);
		}else {
			repository.setName(url.split("/")[url.split("/").length-1]);
		}
		repository.setUrl(url);
		repository.setRepositoryRoot(url);
		repository.setConfiguration(configuration);
		repository.setType(RepositoryType.GIT);

		ExtractionPath path = new ExtractionPath();
		path.setPath("/"+branch);

		repository.setExtractionPath(path);
		
		if(local) {
			repository = dao.save(repository);

			User user = userDAO.findById(userSession.getUser().getId());

			UserRepository permission = new UserRepository();
			permission.setPermission(PermissionType.OWNER);
			permission.setRepository(repository);
			permission.setUser(user);
			userRepositoryDAO.save(permission);
			
			result.use(Results.json()).withoutRoot().from("").recursive().serialize();
		}else {

			try {
				if(login == null && password == null) {
					GitUtil.testInfoRepository(repository.getUrl(), path.getPath().substring(1));
				} else
					GitUtil.testInfoRepository(repository.getUrl(), path.getPath().substring(1), login, password);	

				User user = userDAO.findById(userSession.getUser().getId());
				
				repository.setOwner(user.getId());
				repository = dao.save(repository);
				
				UserRepository permission = new UserRepository();
				permission.setPermission(PermissionType.OWNER);
				permission.setRepository(repository);
				permission.setUser(user);
				userRepositoryDAO.save(permission);
				
				Extraction extraction = new Extraction(repository.getId(),
						ExtractionType.REPOSITORY,
						new RepositoryCredentials(login, password));
				extraction.setForce(true);
				taskService.addTask(extraction);

				result.use(Results.json()).withoutRoot().from("").recursive().serialize();

			} catch (Exception e) {
				result.use(Results.json()).withoutRoot().from(e.getMessage()).recursive().serialize();
			}
		}
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
	@Post("/repository/{repositoryId}/configuration")
	public void config(Long repositoryId, Configuration configuration) {

		configurationValidator.validate(configuration);
		configurationValidator.onErrorRedirectTo(this.getClass()).chart(repositoryId);

		Configuration config = dao.getConfiguration(repositoryId);
		configuration.setId(config.getId());

		configurationDAO.save(configuration);

		result.redirectTo(this).chart(repositoryId);
	}

	@Permission(PermissionType.MEMBER)
	@Get("/repository/{repositoryId}/chart")
	public void chart(Long repositoryId) {

		Repository repository = dao.findById(repositoryId);
		RepositoryVO repositoryVO = new RepositoryVO(repository);

		ExtractionPath extractionPath = repository.getExtractionPath();

		Configuration configuration = repository.getConfiguration();
		configuration.refreshTime();

		List<TimeWindow> windows = new ArrayList<TimeWindow>(Arrays.asList(TimeWindow.values()));

		result.include("windows", windows);
		result.include("configuration", configuration);
		result.include("repository", repositoryVO);
		result.include("extractionPath", extractionPath);

	}

	@Permission(PermissionType.MEMBER)
	@Post("/repository/{repositoryId}/alterations")
	public void getAlterations(Long repositoryId, String newPath){

		Repository repository = dao.findById(repositoryId);

		if(repository.getType()==RepositoryType.SVN && newPath.equals("/")){
			newPath = repository.getExtractionPath().getPath();
		}
		//referente ao /master
		if(repository.getType()==RepositoryType.GITHUB || repository.getType()==RepositoryType.GIT){
			if(!newPath.equals("/")) 
				newPath = newPath.substring(repository.getExtractionPath().getPath().length());
		}
		List<AuthorPercentage> percentage = dao.getPercentage(repositoryId,repository.getUrl().substring(repository.getRepositoryRoot().length())+newPath);
		result.use(Results.json()).withoutRoot().from(percentage).recursive().serialize();
	}

	@Permission(PermissionType.MEMBER)
	@Post("/gamification/{repositoryId}/update")
	public void update(Long repositoryId) {

		System.out.println("RepositoryController.update()");
		validator.canUpdate(repositoryId);
		validator.onErrorRedirectTo(this.getClass()).show(repositoryId);
		
		Repository repository = dao.findById(repositoryId);
		
		Extraction extraction = new Extraction(repository.getId(),
				ExtractionType.REPOSITORY,
				new RepositoryCredentials(null, null));
		extraction.setForce(true);

		taskService.addTaskUpdate(extraction);

		result.include("notice", new SimpleMessage("info", "repository.update.message", Severity.INFO));
		result.redirectTo(this).show(repositoryId);

	}


	@Permission(PermissionType.MEMBER)
	@Post("/repository/{repositoryId}/testConfiguration")
	public void testConfig(Long repositoryId, Configuration configuration) {

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

		result.redirectTo(this).testInformation(repositoryId);
	}


	@Permission(PermissionType.MEMBER)
	@Get("/repository/{repositoryId}/test/information")
	public void testInformation(Long repositoryId) {

		Repository repository = dao.findById(repositoryId);

		RepositoryVO repositoryVO = new RepositoryVO(repository);

		ExtractionPath extractionPath = repository.getExtractionPath();

		Configuration configuration = repository.getConfiguration();
		configuration.refreshTime();

		List<TimeWindow> windows = new ArrayList<TimeWindow>(Arrays.asList(TimeWindow.values()));

		result.include("testFiles",repository.getTestFiles());
		result.include("windows", windows);
		result.include("configuration", configuration);
		result.include("repository", repositoryVO);
		result.include("extractionPath", extractionPath);

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
	@Post("/repository/{repositoryId}/alterationsQntLine")
	public void getAlterationsQntLine(Long repositoryId, String newPath){

		Repository repository = dao.findById(repositoryId);

		if(repository.getType()==RepositoryType.SVN && newPath.equals("/")){
			newPath = repository.getExtractionPath().getPath();
		}
		//referente ao /master
		if(repository.getType()==RepositoryType.GITHUB || repository.getType()==RepositoryType.GIT){
			if(!newPath.equals("/"))
				newPath = newPath.substring(repository.getExtractionPath().getPath().length());
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


			repositoryCurrent.getExtractionPath().setDirTree(repositoryUpdate.getExtractionPath().getDirTree());

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
			
			for (File file3 : repositoryUpdate.getCodeSmallsFile()) {
				repositoryCurrent.getCodeSmallsFile().add(file3);
			}

			dao.save(repositoryCurrent);

			result.use(Results.json()).withoutRoot().from("Repositorio atualizado").recursive().serialize();
		}
	}
	
	@Permission(PermissionType.MEMBER)
	@Post("/repository/{repositoryId}/javaFilesAlterations")
	public void getJavaFilesAlterations(Long repositoryId){
		List<AuthorPercentage> percentage = new ArrayList<>();
		percentage = dao.getJavaFilesPercentege(repositoryId);
		result.use(Results.json()).withoutRoot().from(percentage).recursive().serialize();
	}
}