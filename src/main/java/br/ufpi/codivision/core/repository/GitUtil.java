package br.ufpi.codivision.core.repository;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import br.ufpi.codivision.core.model.DirTree;
import br.ufpi.codivision.core.model.OperationFile;
import br.ufpi.codivision.core.model.Revision;
import br.ufpi.codivision.core.model.TestFile;
import br.ufpi.codivision.core.model.enums.NodeType;
import br.ufpi.codivision.core.model.enums.OperationType;



public class GitUtil {
	
	private Repository repo;
	private Git git;
	
	public GitUtil(String url) throws InvalidRemoteException, TransportException, IllegalStateException, GitAPIException{
		 git = Git.cloneRepository()
				.setURI(url)
				.setBare(false)
				.setBranch("master")//define qual ramo sera extraido. Funciona tanto para branch como para tags
				.call();

		repo = git.getRepository();
	}
	public GitUtil(File file) throws IOException{
		git = Git.open(file);
		repo = git.getRepository();
	}
	
	public GitUtil(String url, String branch) throws InvalidRemoteException, TransportException, IllegalStateException, GitAPIException{
		 git = Git.cloneRepository()
				.setURI(url)
				.setBare(false)
				.setBranch(branch)//define qual ramo sera extraido. Funciona tanto para branch como para tags
				.call();

		repo = git.getRepository();
	}
	
	public GitUtil(String url, String login, String password) throws InvalidRemoteException, TransportException, IllegalStateException, GitAPIException{
		 git = Git.cloneRepository()
				.setURI(url)
				.setBare(false)
				.setBranch("master")//define qual ramo sera extraido. Funciona tanto para branch como para tags
				.setCredentialsProvider(new UsernamePasswordCredentialsProvider(login, password))
				.call();

		repo = git.getRepository();
	}
	
	public GitUtil(String url, String branch, String login, String password) throws InvalidRemoteException, TransportException, IllegalStateException, GitAPIException{
		 git = Git.cloneRepository()
				.setURI(url)
				.setBare(false)
				.setBranch(branch)//define qual ramo sera extraido. Funciona tanto para branch como para tags
				.setCredentialsProvider(new UsernamePasswordCredentialsProvider(login, password))
				.call();

		repo = git.getRepository();
	}
	
	public List<Revision> getRevisions() throws NoHeadException, GitAPIException, AmbiguousObjectException, IncorrectObjectTypeException, IOException{
		 
			Iterable<RevCommit> log = git.log().setMaxCount(500).call();
			List<Revision> revisions = new ArrayList<>();
			
			for (RevCommit jgitCommit: log) {

				Revision revision = new Revision();
				revision.setExternalId(jgitCommit.getName());
				revision.setAuthor(jgitCommit.getAuthorIdent().getName());
				revision.setDate(jgitCommit.getAuthorIdent().getWhen());
				revision.setFiles(new ArrayList<OperationFile>());
				revision.setExtracted(true);
				
				List<DiffEntry> diffsForTheCommit = diffsForTheCommit(repo, jgitCommit);
				for (DiffEntry diff : diffsForTheCommit) {
					
					OperationFile file = new OperationFile();
					
					if(diff.getChangeType().name().equals("ADD")){
						file.setOperationType(OperationType.ADD);
						file.setPath("/"+diff.getNewPath());
					}else
					if(diff.getChangeType().name().equals("DELETE")){
						file.setOperationType(OperationType.DEL);
						file.setPath("/"+diff.getOldPath());
					}else
					if(diff.getChangeType().name().equals("MODIFY")){
						file.setOperationType(OperationType.MOD);
						file.setPath("/"+diff.getNewPath());
					}else
						continue;
					
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					DiffFormatter diffFormatter = new DiffFormatter( stream );
					diffFormatter.setRepository(repo);
					diffFormatter.format(diff);
					
					String in = stream.toString();
					DiffUtil util = new DiffUtil();
					
					Map<String, Integer> modifications = util.analyze(in);
					file.setLineAdd(modifications.get("adds"));
					file.setLineMod(modifications.get("mods"));
					file.setLineDel(modifications.get("dels"));
					file.setLineCondition(modifications.get("conditions"));
					file.setLinesNumber(file.getLineAdd()+file.getLineDel()+file.getLineMod());
					file.setExtracted(true);
					revision.getFiles().add(file);
					
					diffFormatter.flush();
				}
				revision.setTotalFiles(revision.getFiles().size());
				revisions.add(revision);


			}
			
			return revisions;
	}
	
	public List<TestFile> getTestFiles() throws IOException{
		List<TestFile> testFiles = new ArrayList<TestFile>();
		
		Ref head = repo.getRef("HEAD");

		RevWalk walk = new RevWalk(repo);
		RevCommit commit = walk.parseCommit(head.getObjectId()); 
        RevTree tree = commit.getTree(); 
        TreeWalk treeWalk = new TreeWalk(repo);
	    treeWalk.addTree(tree); 
        treeWalk.setRecursive(true); 
        
        Set<DirTree> dirTree = new HashSet<DirTree>();
 		
        while(treeWalk.next()){
        	
        	ObjectId objectId = treeWalk.getObjectId(0);
            ObjectLoader loader = repo.open(objectId);
            
            
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
			
            loader.copyTo(stream);
            
            String fileCode = stream.toString();
            
            if(fileCode.contains("@Test")){
    			TestFile file = new TestFile();
    			file.setPath("/"+treeWalk.getPathString());
    			testFiles.add(file);
    		}
        
 			
        	if(treeWalk.isSubtree()){
        		treeWalk.enterSubtree();
        	}
 		
        	
 		}
 		 
        return testFiles;
	}

	public Set<DirTree> getDirTree() throws IOException{
		Ref head = repo.getRef("HEAD");

		RevWalk walk = new RevWalk(repo);
		RevCommit commit = walk.parseCommit(head.getObjectId()); 
        RevTree tree = commit.getTree(); 
        TreeWalk treeWalk = new TreeWalk(repo);
	    treeWalk.addTree(tree); 
        treeWalk.setRecursive(true); 
        
        Set<DirTree> dirTree = new HashSet<DirTree>();
 		
        while(treeWalk.next()){
        	
        	if(treeWalk.isSubtree()){
        		treeWalk.enterSubtree();
        	}
 		
        	String[] path = treeWalk.getPathString().split("/");
        	findTree(dirTree,path,0);
 		}
 		 
        return dirTree;

	}
	
	private void findTree(Set<DirTree> dirTree, String[] path, int i) {
		for(DirTree tree:dirTree){
			if(tree.getText().equals(path[i])){
				findTree(tree.getChildren(),path,i+1);
				return;
			}
		}
		DirTree tree = new DirTree();
		tree.setText(path[i]);
		
		if(path.length == (i+1))
			tree.setType(NodeType.FILE);
		else
			tree.setType(NodeType.FOLDER);
		
		dirTree.add(tree);
		
		if(path.length > (i+1)){
			for(DirTree tree1:dirTree){
				if(tree1.getText().equals(path[i])){
					findTree(tree1.getChildren(),path,i+1);
					return;
					}
				}
			
		}
	}
	private  List<DiffEntry> diffsForTheCommit(Repository repo, RevCommit commit) throws IOException, AmbiguousObjectException, 
	IncorrectObjectTypeException { 

		AnyObjectId currentCommit = repo.resolve(commit.getName()); 
		AnyObjectId parentCommit = commit.getParentCount() > 0 ? repo.resolve(commit.getParent(0).getName()) : null; 

		DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE); 
		df.setBinaryFileThreshold(2 * 1024); // 2 mb max a file 
		df.setRepository(repo); 
		df.setDiffComparator(RawTextComparator.DEFAULT); 
		df.setDetectRenames(true); 
		List<DiffEntry> diffs = null; 

		if (parentCommit == null) { 
			RevWalk rw = new RevWalk(repo); 
			diffs = df.scan(new EmptyTreeIterator(), new CanonicalTreeParser(null, rw.getObjectReader(), commit.getTree())); 
			rw.close(); 
		} else { 
			diffs = df.scan(parentCommit, currentCommit); 
		} 

		df.close(); 

		return diffs; 
	}
}
