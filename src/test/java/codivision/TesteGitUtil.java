package codivision;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;

import br.ufpi.codivision.core.model.DirTree;
import br.ufpi.codivision.core.model.ExtractionPath;
import br.ufpi.codivision.core.model.Repository;
import br.ufpi.codivision.core.model.Revision;
import br.ufpi.codivision.core.model.enums.NodeType;
import br.ufpi.codivision.core.repository.GitUtil;
import br.ufpi.codivision.core.util.Outliers;
import br.ufpi.codivision.feature.java.graph.ClassGraphBuilder;
import br.ufpi.codivision.feature.java.model.Class;
import br.ufpi.codivision.feature.java.model.NodeInfo;

public class TesteGitUtil {
	
	public static void main(String[] args) throws Exception {
		//caminho do projeto. exemplo: /home/irvayne/Documentos/workspace-codivision/codivision/.git
		GitUtil util = new GitUtil(new File("/home/irvayne/Documentos/workspace-codivision/codivision/.git"));
		
		Repository repository = new Repository();
		//url cadastrada na ferramenta
		repository.setUrl("sigaa");
		
		ExtractionPath path = new ExtractionPath();
		path.setPath("/master");

		repository.setExtractionPath(path);
		
		repository.setRevisions(util.getRevisions());
		
		repository.setLastUpdate(repository.getRevisions().get(0).getDate());
		repository.setLastRevision(repository.getRevisions().get(0).getExternalId());
		
		/* Calcula as revisoes que possuem mais arquivos que o normal */
		List<Integer> qntFileRevision = new ArrayList<Integer>();
		for(Revision revision: repository.getRevisions()) {
			qntFileRevision.add(revision.getTotalFiles());
		}

		Collections.sort(qntFileRevision);

		int limiar = (int) Outliers.indentify(qntFileRevision);


		List<Revision> revisions = new ArrayList<Revision>();
		for(Revision revision: repository.getRevisions()) {
			if(revision.getTotalFiles() <= limiar) {
				revisions.add(revision);
			}
		}

		repository.setRevisions(revisions);
		//repository.setTestFiles(util.getTestFiles());
		

		DirTree tree = new DirTree();
		tree.setText(repository.getExtractionPath().getPath().substring(1));
		tree.setType(NodeType.FOLDER);
		tree.setChildren(util.getDirTree());
		repository.getExtractionPath().setDirTree(tree);
		
		repository.setCodeSmallsFile(util.getCodeSmellFiles());
		
		List<Class> arquivosJava = util.getRepositoryJavaFiles();
		
		if(!arquivosJava.isEmpty()) {
			ClassGraphBuilder graph = new ClassGraphBuilder(arquivosJava);
			
			for (NodeInfo nodeInfo : graph.getG().vertexSet()) {
				for (br.ufpi.codivision.debit.model.File file : repository.getCodeSmallsFile()) {
					String full = "/src/main/java/" + file.getPath();	
					full = full.concat(".java");

					if(nodeInfo.getC().getFullname().equals(full)) {
						file.setAcoplamento(nodeInfo.getDegreeOUT());
					}
						
				}
				
			}
			
		}
		
//		if(!arquivosJava.isEmpty()) {
//			ClassGraphBuilder builder = new ClassGraphBuilder(arquivosJava);
//			ControllerDefiner controllerDefiner = new ControllerDefiner(builder.getG());
//			FeatureDefiner fd = new FeatureDefiner(controllerDefiner.controllersDefiner(), builder.getG());
//			List<Feature> features = fd.featureIdentify();
//			repository.getExtractionPath().setFeatures(features);
//		}
		
		
//		Map<String, Map<String, Integer>> tdRemove = Fuzzy.historicTDRemove(repository);
//		List<TDAuthor> list = new ArrayList<TDAuthor>();
//		for (String nameAuthor : tdRemove.keySet()) {
//			TDAuthor tdAuthor = new TDAuthor(nameAuthor);
//			Map<String, Integer> map = tdRemove.get(nameAuthor);
//			for (String dt : map.keySet()) {
//				InfoTD infoTD = new InfoTD();
//				infoTD.setCodeSmellType(dt);
//				infoTD.setQnt(map.get(dt));
//				tdAuthor.getYoursCodeSmell().add(infoTD);
//			}
//			
//			list.add(tdAuthor);
//		}
//		
//		repository.setTdAuthor(list);
		
		util.closeRepository();
		
		saveRepository(repository);
		System.out.println("extraido");
		
	}
	
	public static void saveRepository(Repository repository){
		String url = "https://easii.ufpi.br/codivision/repository/remoteUpdate";
	    
	    try {
	    	DefaultHttpClient client = new DefaultHttpClient();
	        HttpPost post = new HttpPost(url);
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	        Gson json = new Gson();
	        nameValuePairs.add(new BasicNameValuePair("repository",json.toJson(repository)));
	        post.setEntity(new UrlEncodedFormEntity(nameValuePairs)); 
	        
	        // Execute your request and catch response
	        HttpResponse response = client.execute(post);
	        if(response.getStatusLine().getStatusCode() == 200){
	        	System.out.println("Repositorio atualizado");
	        	System.exit(0);
	        }
		
	        // Check for HTTP response code: 200 = success
	        if (response.getStatusLine().getStatusCode() != 200) {
	        	System.out.println("Error");
	        }
	    } catch (Exception e) {
	    	System.out.println(e.getMessage());
	    }
	}
	

}
