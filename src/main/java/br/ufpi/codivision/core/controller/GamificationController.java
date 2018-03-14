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

@Controller
public class GamificationController {
	
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
		
		result.include("authors", authors);
		result.include("revisions", revisions);
		result.include("repository", repositoryVO);
		result.include("repositoryList", repositoryList);
		result.include("userRepositoryList", userRepositoryList);

	}
	
}