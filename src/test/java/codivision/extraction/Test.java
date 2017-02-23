package codivision.extraction;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.jgrapht.graph.DefaultEdge;

import br.ufpi.codivision.core.model.DirTree;
import br.ufpi.codivision.core.model.ExtractionPath;
import br.ufpi.codivision.core.model.Repository;
import br.ufpi.codivision.core.model.enums.NodeType;
import br.ufpi.codivision.core.repository.GitUtil;
import br.ufpi.codivision.core.util.DeleteDir;
import br.ufpi.codivision.feature.java.algorithm.ControllerDefiner;
import br.ufpi.codivision.feature.java.algorithm.FeatureDefiner;
import br.ufpi.codivision.feature.java.graph.ClassGraphBuilder;
import br.ufpi.codivision.feature.java.model.Feature;
import br.ufpi.codivision.feature.java.model.NodeInfo;

public class Test {

	public static void main(String[] args) throws InvalidRemoteException, TransportException, IllegalStateException, GitAPIException, AmbiguousObjectException, IncorrectObjectTypeException, IOException {
		boolean success = DeleteDir.deleteDir(new File("metadata-codivision"));
	    if (!success) {
	        System.out.println("arquivo nao existe"); 
	    }else{
	    	System.out.println("arquivo removido");
	    }
		
		Repository repository = new Repository();
		
		//para repositorios do git lab, deve ser adicionado o .git no final. PAra git hub nao eh necessario
		repository.setUrl("http://gitlab.infoway-pi.com.br/ihealth-maa/MAA.git");
		
		System.out.println("Iniciando a extracao do repositorio "+repository.getUrl());
		ExtractionPath path = new ExtractionPath();
		path.setPath("/master");
		repository.getExtractionPaths().add(path);
		

		
		
		GitUtil util = new GitUtil(new File("C:\\Users\\Irvayne Matheus\\Desktop\\Computação\\Iniciação Científica\\Iniciação2016-2017\\Extração InfoWay 07012017\\ihealth-maa MAA\\master\\MAA/.git"));
		
		repository.setRepositoryRoot(repository.getUrl());
		repository.setRevisions(util.getRevisions());
		repository.setLastUpdate(repository.getRevisions().get(0).getDate());
		repository.setLastRevision(repository.getRevisions().get(0).getExternalId());
		repository.setTestFiles(util.getTestFiles());
		
		DirTree tree = new DirTree();
		tree.setText("master");
		tree.setType(NodeType.FOLDER);
		tree.setChildren(util.getDirTree());
		
		repository.getExtractionPaths().get(0).setDirTree(tree);
		
		
		ClassGraphBuilder builder = new ClassGraphBuilder(util.getRepositoryJavaFiles());
		ControllerDefiner controllerDefiner = new ControllerDefiner(builder.getG());
		
		FeatureDefiner definer = new FeatureDefiner();
		List<Feature> features = definer.definer(controllerDefiner.controllersDefiner(), builder.getG());
		
		for (Iterator<NodeInfo> iterator = builder.getG().vertexSet().iterator(); iterator.hasNext();) {
			NodeInfo c = iterator.next();
			System.out.print(c.getC().getName() + "["+ c.getDegreeIN() + "-" + c.getDegreeOUT()+"]"+ "["+ c.hasCycle() + "]"+ "["+ builder.getG().edgesOf(c).size()+"]"+" -> " );
			for (Iterator<DefaultEdge> i = builder.getG().edgesOf(c).iterator(); i.hasNext();) {
			DefaultEdge de = (DefaultEdge) i.next();
			System.out.print("(" + builder.getG().getEdgeSource(de).getC() + " : " + builder.getG().getEdgeTarget(de).getC() + ")");
			}
			System.out.println("");
			}
	
		for(Feature aux: features){
			System.out.println(aux.getName());
			for(br.ufpi.codivision.feature.java.model.Class c: aux.getClasses()){
				System.out.println(c.getFullname());
			}
		}
		
				
		
		util.closeRepository();
	}

}
