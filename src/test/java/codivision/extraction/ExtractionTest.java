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
import br.ufpi.codivision.feature.java.algorithm.ControllerDefiner;
import br.ufpi.codivision.feature.java.algorithm.FeatureDefiner;
import br.ufpi.codivision.feature.java.graph.ClassGraphBuilder;
import br.ufpi.codivision.feature.java.model.Feature;

public class ExtractionTest {

	public static void main(String[] args) throws InvalidRemoteException, TransportException, IllegalStateException, GitAPIException, AmbiguousObjectException, IncorrectObjectTypeException, IOException {
//		String repositoryClonePath = System.getProperty( "java.io.tmpdir" ).concat( System.getProperty( "file.separator" ));
//		String folder = new String("metadata-codivision");

//		boolean success = DeleteDir.deleteDir(new File(repositoryClonePath.concat(folder)));
//	    if (!success) {
//	        System.out.println("arquivo nao existe"); 
//	    }else{
//	    	System.out.println("arquivo removido");
//	    }
		
		Repository repository = new Repository();
		String pathClone = "D:\\Projects\\Mestrado\\Repositorios\\codivision\\.git";
		
		//para repositorios do git lab, deve ser adicionado o .git no final. PAra git hub nao eh necessario
	//	repository.setUrl("https://vandersonmoura@bitbucket.org/werney/codivision.git");
		repository.setUrl(pathClone);
		
		System.out.println("Iniciando a extracao do repositorio "+repository.getUrl());
		ExtractionPath extractionPath = new ExtractionPath();
		extractionPath.setPath("/master");
		repository.getExtractionPaths().add(extractionPath);
		
//		GitUtil gitUtil = new GitUtil(repository.getUrl(), test.util.ExtractionPath.INTEGRATION.getName(), repositoryClonePath.concat(folder), LoginData.USER, LoginData.PASSWORD);
//		GitUtil gitUtil = new GitUtil(repository.getUrl(), test.util.ExtractionPath.INTEGRATION.getName());
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
		
		repository.getExtractionPaths().get(0).setDirTree(tree);
		
		
		ClassGraphBuilder builder = new ClassGraphBuilder(gitUtil.getRepositoryJavaFiles());
		ControllerDefiner controllerDefiner = new ControllerDefiner(builder.getG());
		
		FeatureDefiner definer = new FeatureDefiner();
		List<Feature> features = definer.definer(controllerDefiner.controllersDefiner(), builder.getG());
		
//		for (Iterator<NodeInfo> iterator = builder.getG().vertexSet().iterator(); iterator.hasNext();) {
//			NodeInfo c = iterator.next();
//			System.out.print(c.getC().getName() + "["+ c.getDegreeIN() + "-" + c.getDegreeOUT()+"]"+ "["+ c.hasCycle() + "]"+ "["+ builder.getG().edgesOf(c).size()+"]"+" -> " );
//			for (Iterator<DefaultEdge> i = builder.getG().edgesOf(c).iterator(); i.hasNext();) {
//			DefaultEdge de = (DefaultEdge) i.next();
//			System.out.print("(" + builder.getG().getEdgeSource(de).getC() + " : " + builder.getG().getEdgeTarget(de).getC() + ")");
//			}
//			System.out.println("");
//			}
	
		for(Feature aux: features){
			System.out.println(aux.getName());
			for(br.ufpi.codivision.feature.java.model.Class c: aux.getClasses()){
				System.out.println(c.getFullname());
			}
		}
		
				
		
		gitUtil.closeRepository();
	}

}