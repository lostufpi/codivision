/**
 * 
 */
package br.ufpi.codivision.core.repository;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.wc2.ng.SvnDiffGenerator;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc2.SvnDiff;
import org.tmatesoft.svn.core.wc2.SvnOperationFactory;
import org.tmatesoft.svn.core.wc2.SvnTarget;

import br.ufpi.codivision.core.model.DirTree;
import br.ufpi.codivision.core.model.OperationFile;
import br.ufpi.codivision.core.model.Revision;
import br.ufpi.codivision.core.model.enums.NodeType;

/**
 * @author Werney Ayala
 *
 */
public class SvnUtil {
	
	private SVNRepository repository;
	private AuthUtil auth;
	private SvnOperationFactory svnOperationFactory;
	private SvnDiffGenerator diffGenerator;
	private SVNURL repositoryRoot;
	
	public SvnUtil(String repositoryUrl) throws SVNException {
		
		this.auth = new AuthUtil("myislands", "kR8#!pQx");
		SVNURL url = SVNURL.parseURIEncoded(repositoryUrl);
		this.repository = SVNRepositoryFactory.create(url);
		this.repository.setAuthenticationManager(auth.getAuthManager());
		
		this.svnOperationFactory = new SvnOperationFactory();
		this.svnOperationFactory.setAuthenticationManager(auth.getAuthManager());
		
		this.diffGenerator = new SvnDiffGenerator(); 
		this.diffGenerator.setBasePath(new File(""));
		
	}
	
	public SvnUtil(String repositoryUrl, String repositoryRoot) throws SVNException {
		
		this.auth = new AuthUtil("myislands", "kR8#!pQx");
		SVNURL url = SVNURL.parseURIEncoded(repositoryUrl);
		this.repository = SVNRepositoryFactory.create(url);
		this.repository.setAuthenticationManager(auth.getAuthManager());
		this.repositoryRoot = SVNURL.parseURIEncoded(repositoryRoot);
		
		this.svnOperationFactory = new SvnOperationFactory();
		this.svnOperationFactory.setAuthenticationManager(auth.getAuthManager());
		
		this.diffGenerator = new SvnDiffGenerator(); 
		this.diffGenerator.setBasePath(new File(""));
		
	}
	
	public List<Revision> getRevionLog(String[] targetPaths) throws FileNotFoundException, SVNException, IOException{
		return getRevisionLog(1l, -1l, targetPaths);
	}
	
	public Revision getRevisionLog(Long revisionId, String[] targetPaths) throws SVNException, FileNotFoundException, IOException{
		return getRevisionLog(revisionId, revisionId, targetPaths).get(0);
	}
	
	public List<Revision> getRevisionLog(Date date, String[] targetPaths) throws SVNException, FileNotFoundException, IOException {
		Long initRevision = repository.getDatedRevision(date);
		return getRevisionLog(initRevision, -1l, targetPaths);
	}
	
	public List<Revision> getRevisionLog(Date date, Long revisionId, String[] targetPaths) throws SVNException, FileNotFoundException, IOException {
		Long initRevision = repository.getDatedRevision(date);
		return getRevisionLog(initRevision, revisionId, targetPaths);
	}
	
	@SuppressWarnings("rawtypes")
	public List<Revision> getRevisionLog(Long initRevision, Long lastRevision, String[] targetPaths) throws SVNException, FileNotFoundException, IOException{
		
		if(initRevision == null)
			initRevision = 1l;
		if(lastRevision == null)
			lastRevision = -1l;
		
		List<Revision> revisions = new ArrayList<Revision>();
		Collection entries =  repository.log( targetPaths , null , initRevision, lastRevision, true, true);
		Iterator iterator = entries.iterator();
		while(iterator.hasNext()){ 
			
			SVNLogEntry logEntry = (SVNLogEntry) iterator.next();
			Revision revision = createRevision(logEntry);
			revisions.add(revision);
			
		}
		
		return revisions; 	
	}
	
	@SuppressWarnings("rawtypes")
	public Revision createRevision(SVNLogEntry logEntry){
		Revision revision = new Revision();
		revision.setExternalId(logEntry.getRevision()+"");
		revision.setAuthor(logEntry.getAuthor());
		revision.setDate(logEntry.getDate());
		revision.setFiles(new ArrayList<OperationFile>());
		
		Set changedPathsSet = logEntry.getChangedPaths( ).keySet( );
		for ( Iterator changedPaths = changedPathsSet.iterator( ); changedPaths.hasNext( ); ) {
			SVNLogEntryPath entryPath = ( SVNLogEntryPath ) logEntry.getChangedPaths( ).get( changedPaths.next( ) );
			if(entryPath.getKind() == SVNNodeKind.FILE){
				OperationFile oFile = new OperationFile(entryPath.getType(), entryPath.getPath(), entryPath.getCopyPath(), entryPath.getCopyRevision());
				revision.getFiles().add(oFile);
			}
		}
		
		revision.setTotalFiles(revision.getFiles().size());
		revision.setExtracted(true);
		
		return revision;
	}
	
	public ByteArrayOutputStream diff(String filePath, long oldRevision, long newRevision) throws SVNException, FileNotFoundException, IOException{
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		SVNURL fileURL = SVNURL.parseURIEncoded(repositoryRoot + filePath);
		
	    final SvnDiff diff = svnOperationFactory.createDiff();
	    diff.setSources(SvnTarget.fromURL(fileURL, SVNRevision.create(oldRevision)), SvnTarget.fromURL(fileURL, SVNRevision.create(newRevision)));
	    diff.setDiffGenerator(diffGenerator);
	    diff.setOutput(baos);
	    diff.run();
	    
		return baos;
		
	}
	
	public ByteArrayOutputStream diff(String oldFilePath, String newFilePath, long oldRevision, long newRevision) throws SVNException, FileNotFoundException, IOException{
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		SVNURL oldFileURL = SVNURL.parseURIEncoded(repositoryRoot + oldFilePath);
		SVNURL newFileURL = SVNURL.parseURIEncoded(repositoryRoot + newFilePath);
		
	    final SvnDiff diff = svnOperationFactory.createDiff();
	    diff.setSources(SvnTarget.fromURL(oldFileURL, SVNRevision.create(oldRevision)), SvnTarget.fromURL(newFileURL, SVNRevision.create(newRevision)));
	    diff.setDiffGenerator(diffGenerator);
	    diff.setOutput(baos);
	    diff.run();
	    
	    return baos;
		
	}
	
	@SuppressWarnings("rawtypes")
	public Set<DirTree> getDirTree(String path) throws SVNException{
		
		Set<DirTree> nodes = new HashSet<DirTree>();
		Collection entries = repository.getDir(path, -1, null, (Collection) null);
		Iterator iterator = entries.iterator();
		
        while (iterator.hasNext()) {
        	
            SVNDirEntry entry = (SVNDirEntry) iterator.next();
            
            /* Nao adicionar arquivos ocultos */
            if(entry.getName().startsWith("."))
            	continue;
            
            DirTree dirTree = new DirTree();
            dirTree.setText(entry.getName());
            
            /* Checking up if the entry is a directory. */
            if (entry.getKind() == SVNNodeKind.DIR) {
            	dirTree.setType(NodeType.FOLDER);
                dirTree.getChildren().addAll(getDirTree((path.equals("")) ? entry.getName() : path + "/" + entry.getName()));
            } else {
            	dirTree.setType(NodeType.FILE);
//            	dirTree.setType(NodeType.valueOf(entry.getName().substring(entry.getName().lastIndexOf("."))));
            }
            
            if((entry.getKind() == SVNNodeKind.DIR) && (!dirTree.getChildren().isEmpty()))
            	nodes.add(dirTree);
            
            if(entry.getKind() == SVNNodeKind.FILE)
            	nodes.add(dirTree);
            
        }
        
        return nodes;
	}
	
	public ByteArrayOutputStream getFile(String path) throws SVNException {
		return this.getFile(path, -1);
	}
	
	public ByteArrayOutputStream getFile(String path, long revision) throws SVNException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		repository.getFile(path, revision, null, baos);
		return baos;
	}
	
	public Long getLatestRevision() throws SVNException{
		return repository.getLatestRevision();
	}

	/**
	 * @return the repository
	 */
	public SVNRepository getRepository() {
		return repository;
	}

	/**
	 * @param repository the repository to set
	 */
	public void setRepository(SVNRepository repository) {
		this.repository = repository;
	}

	/**
	 * @return the repositoryRoot
	 * @throws SVNException 
	 */
	public SVNURL getRepositoryRoot() throws SVNException {
		return repository.getRepositoryRoot(true);
	}

}
