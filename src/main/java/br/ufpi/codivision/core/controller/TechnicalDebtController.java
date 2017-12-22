package br.ufpi.codivision.core.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.view.Results;
import br.ufpi.codivision.common.annotation.Permission;
import br.ufpi.codivision.core.dao.FileDAO;
import br.ufpi.codivision.core.dao.RepositoryDAO;
import br.ufpi.codivision.core.model.Configuration;
import br.ufpi.codivision.core.model.Repository;
import br.ufpi.codivision.core.model.enums.PermissionType;
import br.ufpi.codivision.core.model.enums.TimeWindow;
import br.ufpi.codivision.core.model.vo.AuthorPercentage;
import br.ufpi.codivision.core.model.vo.RepositoryVO;
import br.ufpi.codivision.core.model.vo.TDDevChart;
import br.ufpi.codivision.core.model.vo.TDFile;
import br.ufpi.codivision.core.util.Fuzzy;
import br.ufpi.codivision.core.util.ProcessPath;
import br.ufpi.codivision.debit.model.File;
import br.ufpi.codivision.debit.model.Method;

@Controller
public class TechnicalDebtController {
	
	@Inject private Result result;
	@Inject private RepositoryDAO dao;
	@Inject private FileDAO fileDAO;
	
	@Permission(PermissionType.MEMBER)
	@Get("/repository/{repositoryId}/technicalDebit")
	public void infoTD(Long repositoryId) throws Exception {

		Repository repository = dao.findById(repositoryId);
		RepositoryVO repositoryVO = new RepositoryVO(repository);

		Configuration configuration = repository.getConfiguration();
		configuration.refreshTime();

		List<TimeWindow> windows = new ArrayList<TimeWindow>(Arrays.asList(TimeWindow.values()));

		result.include("windows", windows);
		result.include("configuration", configuration);
		result.include("repository", repositoryVO);

	}
	
	@Permission(PermissionType.MEMBER)
	@Post("/repository/{repositoryId}/td")
	public void getTDClass(Long repositoryId, String fileName){

		Repository repository = dao.findById(repositoryId);

		if(!fileName.equals("/") && !fileName.equals("/".concat(repository.getName())) ) {
			String[] split = fileName.split("/");
			String name = split[split.length - 1];

			for (File file : repository.getCodeSmallsFile()) {
				String[] split2 = file.getPath().split("/");
				String name_file = split2[split2.length - 1];

				if(name.equals(name_file)) {
					result.use(Results.json()).withoutRoot().from(file).recursive().serialize();
					return;
				}
			}


		}else {
			result.use(Results.json()).withoutRoot().from(repository.getCodeSmallsFile().size()).recursive().serialize();
		}



	}

	@Permission(PermissionType.MEMBER)
	@Post("/repository/{repositoryId}/file/{fileId}/method/td")
	public void getTDMethod(Long repositoryId, Long fileId, String methodName){

		File file = fileDAO.findById(fileId);

		for (Method method : file.getMethods()) {
			if(method.getName().equals(methodName)) {
				result.use(Results.json()).withoutRoot().from(method).recursive().serialize();
				return;
			}

		}
	}
	
	@Permission(PermissionType.MEMBER)
	@Post("/repository/{repositoryId}/files/criticality")
	public void getFilesCriticality(Long repositoryId) {
		Repository repository = dao.findById(repositoryId);
		
		List<TDFile> criticidade = Fuzzy.criticidade(repository);
		
		result.use(Results.json()).withoutRoot().from(criticidade).recursive().serialize();
	}
	
	@Permission(PermissionType.MEMBER)
	@Post("/repository/{repositoryId}/files/criticality/chart")
	public void getFilesCriticalityChart(Long repositoryId) {
		Repository repository = dao.findById(repositoryId);
		
		List<TDFile> criticidade = Fuzzy.criticidade(repository);
		
		String res = "";
		for (int i = 0; i < criticidade.size(); i++) {
			TDFile tdFile = criticidade.get(i);
			if(i==0) {
				res = res + "[[\""+ProcessPath.convertPathToName(tdFile.getPath())+"\","+Double.parseDouble(tdFile.getGc()+"");
			}else if(i == criticidade.size() - 1) {
				res = res + "],[\""+ProcessPath.convertPathToName(tdFile.getPath())+"\","+Double.parseDouble(tdFile.getGc()+"")+"]]";
			}else {
				res = res + "],[\""+ProcessPath.convertPathToName(tdFile.getPath())+"\","+Double.parseDouble(tdFile.getGc()+"");
			}
			
			
		}
		
		
		result.use(Results.json()).withoutRoot().from(res).recursive().serialize();
	}
	
	@Permission(PermissionType.MEMBER)
	@Post("/repository/{repositoryId}/td/recomendation")
	public void getTDRecomendation(Long repositoryId, String fileName){

		Repository repository = dao.findById(repositoryId);
		String res = "";

		if(!fileName.equals("/") && !fileName.equals("/".concat(repository.getName())) ) {
			String[] split = fileName.split("/");
			String name = split[split.length - 1];

			for (File file : repository.getCodeSmallsFile()) {
				String[] split2 = file.getPath().split("/");
				String name_file = split2[split2.length - 1];

				if(name.equals(name_file)) {

					List<AuthorPercentage> percentage = dao.getPercentage(repositoryId, "/src/main/java/"+file.getPath());

					List<TDDevChart> recommendation = Fuzzy.recommendation(repository, file, percentage);
					
					
					
					for (int i = 0; i < recommendation.size(); i++) {
						
						TDDevChart dev = recommendation.get(i);
						
						
						if(i==0) {
							res = res + "[[\""+dev.getDevName()+"\","+Double.parseDouble(dev.getGrp()+"");
						}else if(i == recommendation.size() - 1) {
							res = res + "],[\""+dev.getDevName()+"\","+Double.parseDouble(dev.getGrp()+"")+"]]";
						}else {
							res = res + "],[\""+dev.getDevName()+"\","+Double.parseDouble(dev.getGrp()+"");
						}
					}

					result.use(Results.json()).withoutRoot().from(res).recursive().serialize();
					return;
				}

			}


		}else {
			result.use(Results.json()).withoutRoot().from(res).recursive().serialize();
		}



	}
}
