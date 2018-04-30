package br.ufpi.codivision.core.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.view.Results;
import br.ufpi.codivision.common.annotation.Permission;
import br.ufpi.codivision.core.dao.ConfigurationDAO;
import br.ufpi.codivision.core.dao.RepositoryDAO;
import br.ufpi.codivision.core.model.Configuration;
import br.ufpi.codivision.core.model.ExtractionPath;
import br.ufpi.codivision.core.model.Repository;
import br.ufpi.codivision.core.model.enums.PermissionType;
import br.ufpi.codivision.core.model.enums.TimeWindow;
import br.ufpi.codivision.core.model.validator.ConfigurationValidator;
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
import br.ufpi.codivision.feature.dao.FeatureUseCaseDAO;
import br.ufpi.codivision.feature.dao.UseCaseDAO;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

@Controller
public class FeatureController {
	@Inject private FeatureDAO featureDAO;
	@Inject private FeatureElementDAO featureElementDAO;
	@Inject private UseCaseDAO useCaseDAO;
	@Inject private FeatureUseCaseDAO featureUseCaseDAO;
	@Inject private RepositoryDAO dao;
	@Inject private Result result;
	@Inject private ConfigurationValidator configurationValidator;
	@Inject private ConfigurationDAO configurationDAO;

	
	@Permission(PermissionType.MEMBER)
	@Get("/repository/{repositoryId}/usecase/")
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
	
	@Permission(PermissionType.MEMBER)
	@Post("/repository/{repositoryId}/feature/remove")
	public void featureRemove(Long repositoryId, String idFeature) {
		if(idFeature != null) {
			this.featureElementDAO.removeFeatureElementByFeatureId(Long.valueOf(idFeature.substring(4)));
			this.featureUseCaseDAO.removeFeatureUseCaseByFeatureId(Long.valueOf(idFeature.substring(4)));
			this.featureDAO.delete(Long.valueOf(idFeature.substring(4)));
		}
	}
	
	@Permission(PermissionType.MEMBER)
	@Post("/agroup/repository/{repositoryId}/feature")
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
	
	@Permission(PermissionType.MEMBER)
	@Post("/repository/{repositoryId}/usecase/remove")
	public void useCaseRemove(Long repositoryId, Long idUseCase) throws Exception {
		if(idUseCase != null) {
			this.featureUseCaseDAO.removeFeatureUseCaseByUseCaseId(idUseCase);
			this.useCaseDAO.delete(idUseCase);
		}
	}
	
	@Permission(PermissionType.MEMBER)
	@Post("/agroup/repository/{repositoryId}/feature/automatic")
	public void automaticAgroupFeatures(Long repositoryId) {
		Repository repository = dao.findById(repositoryId);
		ExtractionPath extractionPath = repository.getExtractionPath();
		List<Feature> features = extractionPath.getFeatures();
		Map<String, List<Feature>> agroupFeatures = new HashMap<>();
		List<UseCase> useCases = new ArrayList<>();
		
		for (Feature feature : features) {
			String controllerName = feature.getName().substring(0, feature.getName().indexOf(Constants.DOT));
			List<Feature> ftrs = agroupFeatures.get(controllerName);
			if(ftrs == null) {
				agroupFeatures.put(controllerName, new ArrayList<>(Arrays.asList(feature)));
			}else {
				ftrs.add(feature);
			}
		}
		
		for (Entry<String, List<Feature>> entry : agroupFeatures.entrySet()) {
			UseCase uc = new UseCase(entry.getKey());
			for (Feature feature : entry.getValue()) {
				FeatureUseCase fuc = new FeatureUseCase(feature, uc);
				uc.getFeatureUseCases().add(fuc);
			}
			useCases.add(uc);
		}
		
		if(repository.getExtractionPath().getUseCases() != null) {
			repository.getExtractionPath().getUseCases().addAll(useCases);
		}else {
			repository.getExtractionPath().setUseCases(useCases);
		}
		this.dao.save(repository);
		
//		Map<String, List<Double>> map = new HashMap<String, List<Double>>();
//		inicializarMap(map);
//		List<String> names = new ArrayList<>();
//		List<UseCase> useCases = useCaseDAO.useCasesOrderByName();
//		List<AuthorPercentage> percentage = new ArrayList<>();
//		
//		for (UseCase useCase : useCases) {
//			percentage = dao.getUseCasePercentage(repositoryId, useCase.getId());
//			
//			Double total = calcularTotal(percentage);
//			for (AuthorPercentage authorPercentage : percentage) {
//				names.add(authorPercentage.getName());
//				map.get(authorPercentage.getName()).add(authorPercentage.getY()/total);
//			}
//			
//			for (Entry<String, List<Double>> entry : map.entrySet()) {
//				if(!names.contains(entry.getKey())) {
//					map.get(entry.getKey()).add(-1D);
//				}
//			}
//			names = new ArrayList<>();
//		}
//		
//		try {
//			gerarDocExcel(map, useCases);
//		} catch (WriteException | IOException e) {
//			e.printStackTrace();
//		}
	}
	
	private Double calcularTotal(List<AuthorPercentage> ap) {
		Double total = 0D;
		for (AuthorPercentage authorPercentage : ap) {
			total += authorPercentage.getY();
		}
		return total;
	}
	
	private static void inicializarMap(Map<String, List<Double>> map) {
		map.put("Cledjan", new ArrayList<Double>());
		map.put("Danniel", new ArrayList<Double>());
		map.put("Euclydes Melo", new ArrayList<Double>());
		map.put("Francisco", new ArrayList<Double>());
		map.put("Joelson Oliveira", new ArrayList<Double>());
		map.put("Kannya Leal", new ArrayList<Double>());
		map.put("Marcos Raniere", new ArrayList<Double>());
		map.put("Matheus", new ArrayList<Double>());
		map.put("mauriliojr21", new ArrayList<Double>());
		map.put("Pacheco", new ArrayList<Double>());
		map.put("Taison", new ArrayList<Double>());
	}
	
	private static void gerarDocExcel(Map<String, List<Double>> map, List<UseCase> useCases) throws IOException, RowsExceededException, WriteException {
		WritableWorkbook wwb = Workbook.createWorkbook(new File("C:\\Users\\Vanderson\\Documents\\result.xls"));
		WritableSheet sheet = wwb.createSheet("Resultados", 0);
		Label l = new Label(0, 0, "Caso de Uso");
		sheet.addCell(l);
		int c = 1;
		
		for (Entry<String, List<Double>> entry : map.entrySet()) {
			l = new Label(c++, 0, entry.getKey());
			sheet.addCell(l);
		}
		
		int r = 1; 
		for (UseCase useCase : useCases) {
			l = new Label(0, r++, useCase.getName());
			sheet.addCell(l);
		}
		
		c = r = 1;
		Number n;
		for (Entry<String, List<Double>> entry : map.entrySet()) {
			for (Double value : entry.getValue()) {
				n = new Number (c, r++, value);
				sheet.addCell(n);
			}
			r = 1;
			c++;
		}
		wwb.write();
		wwb.close();
	}
	
	@Permission(PermissionType.MEMBER)
	@Post("/repository/{repositoryId}/feature/configuration")
	public void config(Long repositoryId, Configuration configuration) throws Exception{
		configurationValidator.validate(configuration);
		configurationValidator.onErrorRedirectTo(this.getClass()).features(repositoryId);
		Configuration config = dao.getConfiguration(repositoryId);
		configuration.setId(config.getId());
		configurationDAO.save(configuration);
		result.redirectTo(this).features(repositoryId);
	}
}
