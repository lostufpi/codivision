package br.ufpi.codivision.core.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.swing.plaf.synth.SynthSeparatorUI;

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
import br.ufpi.codivision.core.dao.GamificationDAO;
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
import br.ufpi.codivision.core.model.Gamification;
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

@Controller
public class GamificationController {
	
	@Inject private Result result;
	@Inject private UserSession userSession;

	@Inject private RepositoryDAO dao;
	@Inject private UserDAO userDAO;
	@Inject private GamificationDAO gDAO;
	@Inject private AuthorDAO authorDAO;
	@Inject private UserRepositoryDAO userRepositoryDAO;
	@Inject private ConfigurationDAO configurationDAO;

	@Inject private RepositoryValidator validator;
	@Inject private ConfigurationValidator configurationValidator;

	@Inject private TaskService taskService;

	
	@Permission(PermissionType.MEMBER)
	@Get("/gamification/{repositoryId}/painel")
	public void painel(Long repositoryId) {

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
		Collections.sort(authors);
		result.include("authors", authors);
		result.include("revisions", revisions);
		result.include("repository", repositoryVO);
		result.include("repositoryList", repositoryList);
		result.include("userRepositoryList", userRepositoryList);

	}
	
	
	@Permission(PermissionType.MEMBER)
	@Post("/gamification/{repositoryId}/stop")
	public void stop(Long repositoryId, Gamification start) {
		Repository repository = dao.findById(repositoryId);
		repository.setGameId(false);
		dao.save(repository);
		start.setActive(false);
		gDAO.save(start);
		result.redirectTo(this).painel(repositoryId);
	}


	
	
	@Permission(PermissionType.MEMBER)
	@Post("/gamification/{repositoryId}/start")
	public void start(Long repositoryId, Gamification start) {
		System.out.println(start.getCicle());
		Repository repository = dao.findById(repositoryId);
		ExtractionPath extractionPath = repository.getExtractionPath();
		System.out.println(extractionPath.getPath());
		start.setId(repositoryId);
		start.setDateInicial(new Date());
		repository.setGameId(true);
		dao.save(repository);
		gDAO.save(start);
		Extraction extraction = new Extraction(repository.getId(),
				ExtractionType.REPOSITORY,
				new RepositoryCredentials(null, null));
		Long[] array = new Long[8];
		for (int i=0 ; i<5; i++) {
			if (i<4) {
				array[i]=(long) 0;
			}
			else {
				array[i]=start.getTaskAtt()*6;
				array[i+1]=start.getMsgTimer()*144;
				array[i+2]=start.getAwardsAtt()*144;
				array[i+3]=start.getCicle()*4280;
			}
		}
		extraction.setData(array);
		extraction.setFirst(true);
		taskService.addTaskUpdate(extraction);
		result.redirectTo(this).painel(repositoryId);
	}

	
}