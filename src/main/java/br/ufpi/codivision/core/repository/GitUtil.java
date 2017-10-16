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
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
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
import br.ufpi.codivision.core.util.Constants;
import br.ufpi.codivision.debit.mining.CodeAnalysisProcessor;
import br.ufpi.codivision.feature.java.model.Class;


public class GitUtil {
	private Repository repository;
	private Git git;

	/**
	 * @param file
	 * @throws IOException
	 */
	public GitUtil(File file) throws IOException{
		this.git = Git.open(file);
		this.repository = this.git.getRepository();
	}


	/**
	 * @param url
	 * @param branch
	 * @throws InvalidRemoteException
	 * @throws TransportException
	 * @throws IllegalStateException
	 * @throws GitAPIException
	 */
	public GitUtil(String url, String branch) throws InvalidRemoteException, TransportException, IllegalStateException, GitAPIException{
		this.git = Git.cloneRepository()
				.setURI(url)
				.setBare(false)
				.setDirectory(new File(getDirectoryToSave().concat(url)))
				.setBranch(branch)
				.call();

		this.repository = this.git.getRepository();
	}

	

	/**
	 * @param url
	 * @param branch
	 * @param login
	 * @param password
	 * @throws InvalidRemoteException
	 * @throws TransportException
	 * @throws IllegalStateException
	 * @throws GitAPIException
	 */
	public GitUtil(String url, String branch, String login, String password) throws InvalidRemoteException, TransportException, IllegalStateException, GitAPIException{
		this.git = Git.cloneRepository()
				.setURI(url)
				.setBare(false)
				.setDirectory(new File(getDirectoryToSave().concat(url)))
				.setBranch(branch)
				.setCredentialsProvider(new UsernamePasswordCredentialsProvider(login, password))
				.call();

		this.repository = this.git.getRepository();
	}


	public void closeRepository(){
		this.repository.close();
		this.git.close();
	}
	/**
	 * @return the revisions
	 * @throws NoHeadException
	 * @throws GitAPIException
	 * @throws AmbiguousObjectException
	 * @throws IncorrectObjectTypeException
	 * @throws IOException
	 */
	public List<Revision> getRevisions() throws NoHeadException, GitAPIException, AmbiguousObjectException, IncorrectObjectTypeException, IOException{

		Iterable<RevCommit> log = this.git.log().all().call();
		List<Revision> revisions = new ArrayList<Revision>();
		
		for (RevCommit jgitCommit: log) {

			Revision revision = new Revision();
			revision.setExternalId(jgitCommit.getName());
			revision.setAuthor(jgitCommit.getAuthorIdent().getName());
			revision.setDate(jgitCommit.getAuthorIdent().getWhen());
			revision.setFiles(new ArrayList<OperationFile>());
			revision.setExtracted(true);
			

			List<DiffEntry> diffsForTheCommit = diffsForTheCommit(this.repository, jgitCommit);
			for (DiffEntry diff : diffsForTheCommit) {

				OperationFile file = new OperationFile();
				
				List<br.ufpi.codivision.debit.model.File> findFileToIdentifyCodeSmells = findFileToIdentifyCodeSmells(diff.getNewPath(), jgitCommit.getTree());
				revision.setCodeSmellsFileAlteration(findFileToIdentifyCodeSmells);
				
				if(diff.getChangeType().name().equals(Constants.ADD)){
					file.setOperationType(OperationType.ADD);
					file.setPath(Constants.FILE_SEPARATOR.concat(diff.getNewPath()));
				}else
					if(diff.getChangeType().name().equals(Constants.DELETE)){
						file.setOperationType(OperationType.DEL);
						file.setPath(Constants.FILE_SEPARATOR.concat(diff.getOldPath()));
					}else
						if(diff.getChangeType().name().equals(Constants.MODIFY)){
							file.setOperationType(OperationType.MOD);
							file.setPath(Constants.FILE_SEPARATOR.concat(diff.getNewPath()));
						}else
							continue;

				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				DiffFormatter diffFormatter = new DiffFormatter( stream );
				diffFormatter.setRepository(this.repository);
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
				diffFormatter.close();
			}
			revision.setTotalFiles(revision.getFiles().size());
			revisions.add(revision);
		}
		return revisions;
	}

	private List<br.ufpi.codivision.debit.model.File> findFileToIdentifyCodeSmells(String newPath, RevTree tree) throws IOException {
		
		List<br.ufpi.codivision.debit.model.File> files = new ArrayList<>();

		TreeWalk treeWalk = new TreeWalk(this.repository);
		treeWalk.addTree(tree); 
		treeWalk.setRecursive(true); 

		while(treeWalk.next()){
			ObjectId objectId = treeWalk.getObjectId(0);
			ObjectLoader loader = this.repository.open(objectId);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			loader.copyTo(stream);
			String filePath = Constants.FILE_SEPARATOR.concat(treeWalk.getPathString());

			if(filePath.contains(Constants.JAVA_EXTENSION) && filePath.equals("/"+newPath)){
				String fileCode = stream.toString();
				CodeAnalysisProcessor processor = new CodeAnalysisProcessor();
				br.ufpi.codivision.debit.model.File processFile = processor.processFile(fileCode);
				files.add(processFile);
			}

			if(treeWalk.isSubtree()){
				treeWalk.enterSubtree();
			}
		}
		treeWalk.close();

		return files;
		
	}

	public void testConnection() throws NoHeadException, GitAPIException {
		this.git.log().setMaxCount(1).call();
	}

	/**
	 * @return the test files
	 * @throws IOException
	 */
	public List<TestFile> getTestFiles() throws IOException{
		List<TestFile> testFiles = new ArrayList<TestFile>();

		Ref head = this.repository.findRef(Constants.HEAD);

		RevWalk walk = new RevWalk(this.repository);
		RevCommit commit = walk.parseCommit(head.getObjectId()); 
		RevTree tree = commit.getTree(); 
		TreeWalk treeWalk = new TreeWalk(this.repository);
		treeWalk.addTree(tree); 
		treeWalk.setRecursive(true); 

		while(treeWalk.next()){
			ObjectId objectId = treeWalk.getObjectId(0);
			ObjectLoader loader = this.repository.open(objectId);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			loader.copyTo(stream);

			String filePath = Constants.FILE_SEPARATOR.concat(treeWalk.getPathString());

			if(filePath.contains(Constants.JAVA_EXTENSION)){
				String fileCode = stream.toString();

				if(fileCode.contains(Constants.TEST)){
					TestFile file = new TestFile();
					file.setPath(Constants.FILE_SEPARATOR.concat(treeWalk.getPathString()));
					testFiles.add(file);
				}
			}

			if(treeWalk.isSubtree()){
				treeWalk.enterSubtree();
			}
		}
		walk.close();
		treeWalk.close();

		return testFiles;
	}

	/**
	 * @return the test files
	 * @throws IOException
	 */
	public List<br.ufpi.codivision.debit.model.File> getCodeSmellFiles() throws IOException{
		List<br.ufpi.codivision.debit.model.File> files = new ArrayList<>();

		Ref head = this.repository.findRef(Constants.HEAD);

		RevWalk walk = new RevWalk(this.repository);
		RevCommit commit = walk.parseCommit(head.getObjectId()); 
		RevTree tree = commit.getTree(); 
		TreeWalk treeWalk = new TreeWalk(this.repository);
		treeWalk.addTree(tree); 
		treeWalk.setRecursive(true); 

		while(treeWalk.next()){
			ObjectId objectId = treeWalk.getObjectId(0);
			ObjectLoader loader = this.repository.open(objectId);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			loader.copyTo(stream);
			String filePath = Constants.FILE_SEPARATOR.concat(treeWalk.getPathString());

			if(filePath.contains(Constants.JAVA_EXTENSION)){
				String fileCode = stream.toString();
				CodeAnalysisProcessor processor = new CodeAnalysisProcessor();
				br.ufpi.codivision.debit.model.File processFile = processor.processFile(fileCode);
				files.add(processFile);
			}

			if(treeWalk.isSubtree()){
				treeWalk.enterSubtree();
			}
		}
		walk.close();
		treeWalk.close();

		return files;
	}

	/**
	 * @return the DirTree
	 * @throws IOException
	 */
	public Set<DirTree> getDirTree() throws IOException{
		Ref head = this.repository.findRef(Constants.HEAD);
		RevWalk walk = new RevWalk(this.repository);
		RevCommit commit = walk.parseCommit(head.getObjectId()); 
		RevTree tree = commit.getTree(); 
		TreeWalk treeWalk = new TreeWalk(this.repository);
		treeWalk.addTree(tree); 
		treeWalk.setRecursive(true); 
		Set<DirTree> dirTree = new HashSet<DirTree>();

		while(treeWalk.next()){
			if(treeWalk.isSubtree()){
				treeWalk.enterSubtree();
			}

			String[] path = treeWalk.getPathString().split(Constants.FILE_SEPARATOR);
			findTree(dirTree,path,0);
		}
		walk.close();
		treeWalk.close();
		return dirTree;
	}

	/**
	 * @param dirTree
	 * @param path
	 * @param i
	 */
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
	/**
	 * @param repo
	 * @param commit
	 * @return
	 * @throws IOException
	 * @throws AmbiguousObjectException
	 * @throws IncorrectObjectTypeException
	 */
	private  List<DiffEntry> diffsForTheCommit(Repository repo, RevCommit commit) throws IOException, AmbiguousObjectException, 
	IncorrectObjectTypeException { 

		AnyObjectId currentCommit = repo.resolve(commit.getName()); 
		AnyObjectId parentCommit = commit.getParentCount() > 0 ? repo.resolve(commit.getParent(0).getName()) : null; 

		DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE); 
		df.setBinaryFileThreshold(2 * 1024); //2 MB MAX A FILE
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

	/**
	 * @return
	 * @throws MissingObjectException
	 * @throws IncorrectObjectTypeException
	 * @throws CorruptObjectException
	 * @throws IOException
	 */
	public List<Class> getRepositoryJavaFiles() throws MissingObjectException, IncorrectObjectTypeException, CorruptObjectException, IOException {
		List<Class> classes = new ArrayList<>();
		Ref head = this.repository.findRef(Constants.HEAD);
		RevWalk walk = new RevWalk(repository);
		RevCommit commit = walk.parseCommit(head.getObjectId()); 
		RevTree tree = commit.getTree(); 
		TreeWalk treeWalk = new TreeWalk(this.repository);
		treeWalk.addTree(tree); 
		treeWalk.setRecursive(true); 

		while(treeWalk.next()){
			ObjectId objectId = treeWalk.getObjectId(0);
			ObjectLoader loader = this.repository.open(objectId);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			loader.copyTo(stream);
			String content = stream.toString();
			String filePath = Constants.FILE_SEPARATOR.concat(treeWalk.getPathString());

			if(filePath.contains(Constants.JAVA_EXTENSION)){
				classes.add(new Class(filePath, content, new String()));
			}

			if(treeWalk.isSubtree()){
				treeWalk.enterSubtree();
			}
		}
		walk.close();
		treeWalk.close();

		return classes;
	}

	/**
	 * @return directory to save the repository clone 
	 */
	public static String getDirectoryToSave(){
		return System.getProperty("java.io.tmpdir").concat(System.getProperty("file.separator")).concat(Constants.METADATA_FOLDER_NAME);
	}
}
