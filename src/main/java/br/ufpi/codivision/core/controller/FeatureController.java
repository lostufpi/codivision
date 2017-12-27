package br.ufpi.codivision.core.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.view.Results;
import br.ufpi.codivision.common.annotation.Permission;
import br.ufpi.codivision.core.dao.RepositoryDAO;
import br.ufpi.codivision.core.model.Configuration;
import br.ufpi.codivision.core.model.ExtractionPath;
import br.ufpi.codivision.core.model.Repository;
import br.ufpi.codivision.core.model.enums.PermissionType;
import br.ufpi.codivision.core.model.enums.TimeWindow;
import br.ufpi.codivision.core.model.vo.AuthorPercentage;
import br.ufpi.codivision.core.model.vo.RepositoryVO;
import br.ufpi.codivision.core.model.vo.UseCaseVO;
import br.ufpi.codivision.core.util.Constants;
import br.ufpi.codivision.feature.common.model.Element;
import br.ufpi.codivision.feature.common.model.Feature;
import br.ufpi.codivision.feature.common.model.FeatureElement;
import br.ufpi.codivision.feature.common.model.FeatureUseCase;
import br.ufpi.codivision.feature.common.model.UseCase;
import br.ufpi.codivision.feature.common.util.FeatureNodeType;
import br.ufpi.codivision.feature.common.util.FeatureTree;
import br.ufpi.codivision.feature.dao.FeatureDAO;
import br.ufpi.codivision.feature.dao.FeatureElementDAO;

@Controller
public class FeatureController {
	@Inject private FeatureDAO featureDAO;
	@Inject private FeatureElementDAO featureElementDAO;
	@Inject private RepositoryDAO dao;
	@Inject private Result result;
	
	@Permission(PermissionType.MEMBER)
	@Get("/usecase/repository/{repositoryId}")
	public void usecase(Long repositoryId) throws Exception {
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
	@Get("/features/repository/{repositoryId}")
	public void features(Long repositoryId) throws Exception {

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
	@Post("/feature/repository/{repositoryId}/tree")
	public void getFeaturesTree(Long repositoryId) {
		Repository repository = dao.findById(repositoryId);
		ExtractionPath extractionPath = repository.getExtractionPath();
		List<Feature> features = extractionPath.getFeatures();

		FeatureTree root = new FeatureTree();
		root.setType(FeatureNodeType.FEATURE);
		root.setText(repository.getName());

		String a, b;

		for (Feature f : features) {
			a = String.valueOf((int) (1000 + Math.random() * (10000 - 1000 + 1)));
			b = String.valueOf(f.getId());
			FeatureTree featureTree = new FeatureTree();
			featureTree.setId(a.concat(b));
			featureTree.setText(f.getName());
			featureTree.setType(FeatureNodeType.FEATURE);
			for (FeatureElement fe : f.getFeatureElements()) {
				a = String.valueOf((int) (1000 + Math.random() * (10000 - 1000 + 1)));
				b = String.valueOf(fe.getElement().getId());
				FeatureTree elementTree = new FeatureTree();
				elementTree.setId(a.concat(b));
				elementTree.setText(fe.getElement().getFullname());
				elementTree.setType(FeatureNodeType.ELEMENT);
				featureTree.getChildren().add(elementTree);
			}
			root.getChildren().add(featureTree);
		}
		result.use(Results.json()).withoutRoot().from(root).recursive().serialize();
	}
	
	@Post("/feature/remove")
	@Permission(PermissionType.MEMBER)
	public void featureRemove(String idFeature) {
		if(idFeature != null) {
			this.featureElementDAO.removeFeatureElementByFeatureId(Long.valueOf(idFeature.substring(4)));
			this.featureDAO.delete(Long.valueOf(idFeature.substring(4)));
		}
	}
	
	@Post("/agroup/repository/{repositoryId}/feature")
	@Permission(PermissionType.MEMBER)
	public void agroupFeature(Long repositoryId, String name, String[] features) {
		Repository repository = dao.findById(repositoryId);
		UseCase uc = new UseCase();
		List<FeatureUseCase> featureUseCases = new ArrayList<>();
		uc.setName(name);
		
		for (String featureID : features) {
			Feature f = this.featureDAO.findById(Long.valueOf(featureID.substring(4)));
			if(f != null) {
				FeatureUseCase fuc = new FeatureUseCase();
				fuc.setFeature(f);
				fuc.setUseCase(uc);
				featureUseCases.add(fuc);
			}
		}
		uc.setFeatureUseCases(featureUseCases);
		
		if(repository.getExtractionPath().getUseCases() != null) {
			repository.getExtractionPath().getUseCases().add(uc);
		}else {
			List<UseCase> useCases = new ArrayList<>();
			useCases.add(uc);
			repository.getExtractionPath().setUseCases(useCases);
		}
		
		this.dao.save(repository);
	}
	
	@Permission(PermissionType.MEMBER)
	@Post("/usecases/repository/{repositoryId}")
	public void useCases(Long repositoryId) throws Exception {
		Repository repository = dao.findById(repositoryId);
		List<UseCase> useCases = repository.getExtractionPath().getUseCases();
		List<UseCaseVO> useCasesVO = new ArrayList<>();
		
		for (UseCase uc : useCases) {
			useCasesVO.add(new UseCaseVO(uc));
		}
		result.use(Results.json()).withoutRoot().from(useCasesVO).recursive().serialize();
	}
	
	@Permission(PermissionType.MEMBER)
	@Post("/usecase/repository/{repositoryId}/alterations")
	public void getUseCaseAlterations(Long repositoryId, String newPath, String nodeId){
		List<AuthorPercentage> percentage = new ArrayList<>();
		Repository repository = dao.findById(repositoryId);

		if(newPath.contains(Constants.JAVA_EXTENSION)) {
			newPath = newPath.substring(newPath.indexOf(Constants.FILE_SEPARATOR.concat(Constants.FILE_SEPARATOR))+1);
			percentage = dao.getPercentage(repositoryId, newPath);
		}else {
			Long useCaseId = newPath.equals(Constants.FILE_SEPARATOR.concat(repository.getName())) || newPath.equals(Constants.FILE_SEPARATOR) ? null : Long.valueOf(nodeId.substring(4));
			percentage = dao.getUseCasePercentage(repositoryId, useCaseId);
		}
		result.use(Results.json()).withoutRoot().from(percentage).recursive().serialize();
	}
	
	@Permission(PermissionType.MEMBER)
	@Post("/usecase/repository/{repositoryId}/tree")
	public void getUseCasesTree(Long repositoryId) {
		Repository repository = dao.findById(repositoryId);
		ExtractionPath extractionPath = repository.getExtractionPath();
		List<UseCase> useCases = extractionPath.getUseCases();
		HashMap<Long, Element> elementsUnique = new HashMap<>();

		FeatureTree root = new FeatureTree();
		root.setType(FeatureNodeType.FEATURE);
		root.setText(repository.getName());

		String a, b;

		for (UseCase uc : useCases) {
			elementsUnique = new HashMap<>();
			a = String.valueOf((int) (1000 + Math.random() * (10000 - 1000 + 1)));
			b = String.valueOf(uc.getId());
			FeatureTree featureTree = new FeatureTree();
			featureTree.setId(a.concat(b));
			featureTree.setText(uc.getName());
			featureTree.setType(FeatureNodeType.FEATURE);
			for (FeatureUseCase fuc : uc.getFeatureUseCases()) {
				for (FeatureElement fe : fuc.getFeature().getFeatureElements()) {
					if(elementsUnique.get(fe.getElement().getId()) == null) {
						elementsUnique.put(fe.getElement().getId(), fe.getElement());
						a = String.valueOf((int) (1000 + Math.random() * (10000 - 1000 + 1)));
						b = String.valueOf(fe.getElement().getId());
						FeatureTree elementTree = new FeatureTree();
						elementTree.setId(a.concat(b));
						elementTree.setText(fe.getElement().getFullname());
						elementTree.setType(FeatureNodeType.ELEMENT);
						featureTree.getChildren().add(elementTree);
					}
				}
			}
			root.getChildren().add(featureTree);
		}
		result.use(Results.json()).withoutRoot().from(root).recursive().serialize();
	}
}
