package codivision.extraction;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;

import br.ufpi.codivision.core.model.DirTree;
import br.ufpi.codivision.core.model.ExtractionPath;
import br.ufpi.codivision.core.model.Repository;
import br.ufpi.codivision.core.model.enums.NodeType;
import br.ufpi.codivision.core.repository.GitUtil;
import br.ufpi.codivision.feature.common.model.Feature;
import br.ufpi.codivision.feature.common.model.FeatureElement;
import br.ufpi.codivision.feature.java.algorithm.ControllerDefiner;
import br.ufpi.codivision.feature.java.algorithm.FeatureDefiner;
import br.ufpi.codivision.feature.java.graph.ClassGraphBuilder;

public class ExtractionTest {

	public static void main(String[] args) throws InvalidRemoteException, TransportException, IllegalStateException, GitAPIException, AmbiguousObjectException, IncorrectObjectTypeException, IOException {
		
		Repository repository = new Repository();
		String pathClone =  "C:\\Users\\Vanderson\\git\\ARDigital\\.git";
		
		repository.setUrl(pathClone);
		
		System.out.println("Iniciando a extracao do repositorio "+repository.getUrl());
		ExtractionPath extractionPath = new ExtractionPath();
		extractionPath.setPath("/master");
		repository.setExtractionPath(extractionPath);
		
		GitUtil gitUtil = new GitUtil(new File(repository.getUrl()));

		repository.setRepositoryRoot(repository.getUrl());
		repository.setRevisions(gitUtil.getRevisions());
		repository.setLastUpdate(repository.getRevisions().get(0).getDate());
		repository.setLastRevision(repository.getRevisions().get(0).getExternalId());
		repository.setTestFiles(gitUtil.getTestFiles());
		
		DirTree tree = new DirTree();
		tree.setText("master");
		tree.setType(NodeType.FOLDER);
		tree.setChildren(gitUtil.getDirTree());
		
		repository.getExtractionPath().setDirTree(tree);
		
		
		ClassGraphBuilder builder = new ClassGraphBuilder(gitUtil.getRepositoryJavaFiles());
		ControllerDefiner controllerDefiner = new ControllerDefiner(builder.getG());
		
		FeatureDefiner definer = new FeatureDefiner(controllerDefiner.controllersDefiner(), builder.getG());
		List<Feature> features = definer.featureIdentify();
		
		for(Feature feature : features){
			for (FeatureElement featureElement : feature.getFeatureElements()) {
				System.out.println(feature.getName() + ": " + featureElement.getElement().formatFullname());
			}
		}
		gitUtil.closeRepository();
	}
}
