/**
 * 
 */
package br.ufpi.codivision.core.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.egit.github.core.CommitFile;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.Tree;
import org.eclipse.egit.github.core.TreeEntry;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.DataService;
import org.eclipse.egit.github.core.service.RepositoryService;

import br.ufpi.codivision.core.model.DirTree;
import br.ufpi.codivision.core.model.OperationFile;
import br.ufpi.codivision.core.model.Revision;
import br.ufpi.codivision.core.model.enums.NodeType;

/**
 * @author Werney Ayala
 *
 */
public class GitHubUtil {
	
	private GitHubClient client;
	private CommitService commitService;
	private DataService dataService;
	private RepositoryService repositoryService;
	private RepositoryId repository;
	private DiffUtil diffUtil;
	
	public GitHubUtil(String owner, String repositoryName){
		this.client = new GitHubClient();
		this.client.setCredentials("git-codivision","kR8#!pQx");
		this.commitService = new CommitService(client);
		this.dataService = new DataService(client);
		this.repositoryService = new RepositoryService(client);
		this.repository = new RepositoryId(owner, repositoryName);
		this.diffUtil = new DiffUtil();
	}
	
	public List<Revision> getRevisionLog(int pageSize){
		
		List<Revision> revisions = new ArrayList<Revision>();
		Collection<RepositoryCommit> commits = new ArrayList<RepositoryCommit>();
		PageIterator<RepositoryCommit> iterator = commitService.pageCommits(repository, pageSize);
		
		for (int i = 0; i < 5; i++) {
			if(iterator.hasNext())
				commits.addAll(iterator.next());
		}
		
		for (RepositoryCommit commit : commits) {
			if(commit.getAuthor()!=null)
				revisions.add(createRevision(commit));
		}
		
		return revisions;
		
	}
	
	public boolean testConnection() throws IOException{
		repositoryService.getRepository(repository);
		return true;
	}
	
	public Revision createRevision(RepositoryCommit commit) {
		Revision revision = new Revision();
		revision.setExternalId(commit.getSha());
		revision.setAuthor(commit.getAuthor().getLogin());
		revision.setDate(commit.getCommit().getAuthor().getDate());
		return revision;
	}
	
	public int getRemainingRequests() {
		return this.client.getRemainingRequests();
	}
	
	public List<OperationFile> diff(String sha) throws IOException {
		List<OperationFile> files = new ArrayList<OperationFile>();
		RepositoryCommit commit = commitService.getCommit(repository, sha);
		for (CommitFile file : commit.getFiles()) {
			OperationFile oFile = new OperationFile(file.getStatus(), "/" + file.getFilename());
			Map<String, Integer> modifications = diffUtil.analyze(file.getPatch());
			oFile.setLineAdd(modifications.get("adds"));
			oFile.setLineMod(modifications.get("mods"));
			oFile.setLineDel(modifications.get("dels"));
			oFile.setLineCondition(modifications.get("conditions"));
			oFile.setExtracted(true);
			files.add(oFile);
		}
		return files;
	}
	
	public Set<DirTree> getDirTree(String sha) throws IOException {
		
		Set<DirTree> dirTree = new HashSet<DirTree>();
		Tree tree = dataService.getTree(repository, sha);
		
		for (TreeEntry entry : tree.getTree()) {
			
			DirTree aux = new DirTree();
			aux.setText(entry.getPath());
			
			if (entry.getPath().startsWith(".")) {
				continue;
			} else if (entry.getType().equals("blob")) {
				aux.setType(NodeType.FILE);
			} else if (entry.getType().equals("tree")) {
				aux.setType(NodeType.FOLDER);
				aux.getChildren().addAll(getDirTree(entry.getSha()));
			}
			
			dirTree.add(aux);
			
		}
		
		return dirTree;
	}
	
}
