/**
 * 
 */
package br.ufpi.codivision.core.controller;

import java.util.ArrayList;
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
import br.ufpi.codivision.core.model.DirTree;
import br.ufpi.codivision.core.model.ExtractionPath;
import br.ufpi.codivision.core.model.Repository;
import br.ufpi.codivision.core.model.enums.NodeType;
import br.ufpi.codivision.core.model.enums.PermissionType;
import br.ufpi.codivision.core.model.vo.AuthorPercentage;
import br.ufpi.codivision.debit.model.File;

/**
 * @author Werney Ayala
 *
 */
@Controller
public class ExtractionPathController {
	
	@Inject private Result result;
	@Inject private RepositoryDAO repositoryDAO;
	
	@Permission(PermissionType.MEMBER)
	@Get("/repository/{repositoryId}/warningpaths")
	public void getWarningPaths(Long repositoryId) {
		
		DirTree dirTree = repositoryDAO.findById(repositoryId).getExtractionPath().getDirTree();
		Configuration configuration = repositoryDAO.getConfiguration(repositoryId);
		double threshold = configuration.getAlertThreshold() / 100.0;
		List<DirTree> warningPaths = new ArrayList<DirTree>();
		
		for (DirTree temp : dirTree.getChildren()) {
			warningPaths.addAll(getWarningPaths(repositoryId, "/" + temp.getText(), temp, threshold));
		}
		
		result.use(Results.json()).withoutRoot().from(warningPaths).recursive().serialize();
	}
	
	@Permission(PermissionType.MEMBER)
	@Post("/repository/{repositoryId}/tree")
	public void getDirTree(Long repositoryId){
		
		ExtractionPath path= repositoryDAO.findById(repositoryId).getExtractionPath();
		
		if(path.getDirTreeJson()!=null && !path.getDirTreeJson().equals("")){
			System.out.println("Retorno via json");
			result.use(Results.json()).withoutRoot().from(path.getDirTreeJson()).recursive().serialize();
			return;
		}
		
		DirTree dirTree = path.getDirTree();
		DirTree tree = new DirTree();
		tree.setText(dirTree.getText());
		tree.setType(NodeType.FOLDER);
		tree.setChildren(dirTree.getChildren());
		
		result.use(Results.json()).withoutRoot().from(tree).recursive().serialize();
	}
	
	
	private List<DirTree> getWarningPaths(Long repositoryId, String path, DirTree dirTree, double threshold) {

		List<AuthorPercentage> result = repositoryDAO.getPercentage(repositoryId, path);
		List<DirTree> warningPaths = new ArrayList<DirTree>();
		
		double total = 0;
		for (AuthorPercentage percentage : result)
			total += percentage.getY();
		
		for (AuthorPercentage percentage : result) {
			if(percentage.getY()/total > threshold){
				warningPaths.add(dirTree);
				return warningPaths;
			}
		}
		
		for(DirTree temp : dirTree.getChildren()) {
			if (temp.getType().equals(NodeType.FOLDER))
				warningPaths.addAll(getWarningPaths(repositoryId, path + "/" + temp.getText(), temp, threshold));
		}
		
		return warningPaths;
	}
	
	@Permission(PermissionType.MEMBER)
	@Post("/repository/{repositoryId}/tree/td")
	public void getDirTreeTD(Long repositoryId){
		
		Repository repository = repositoryDAO.findById(repositoryId);
		
		DirTree tree = new DirTree();
		tree.setType(NodeType.FOLDER);
		tree.setText(repository.getName());
		
		for (File file : repository.getCodeSmallsFile()) {
			DirTree dirTree = new DirTree();
			dirTree.setType(NodeType.FILE);
			
			//pega a ultima porcao do nome
			String[] split = file.getPath().split("/");
			String name = split[split.length - 1];
			
			dirTree.setText(name);
			
			tree.getChildren().add(dirTree);
		}
		
		result.use(Results.json()).withoutRoot().from(tree).recursive().serialize();
	}

}

