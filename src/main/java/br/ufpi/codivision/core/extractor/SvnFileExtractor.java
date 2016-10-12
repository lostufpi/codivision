/**
 * 
 */
package br.ufpi.codivision.core.extractor;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNException;

import br.ufpi.codivision.common.model.PersistenceEntity;
import br.ufpi.codivision.core.model.OperationFile;
import br.ufpi.codivision.core.repository.DiffUtil;
import br.ufpi.codivision.core.repository.SvnUtil;

/**
 * @author Werney Ayala
 *
 */
public class SvnFileExtractor implements Callable<PersistenceEntity> {
	
	private String url;
	private Long revisionNumber;
	private OperationFile oFile;
	private String repositoryRoot;
	
	private DiffUtil diffUtil;
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	/**
	 * @param url
	 * @param revisonNumber
	 * @param oFile
	 */
	public SvnFileExtractor(String url, Long revisionNumber, OperationFile oFile, String repositoryRoot) {
		super();
		this.url = url;
		this.revisionNumber = revisionNumber;
		this.oFile = oFile;
		this.repositoryRoot = repositoryRoot;
		
		this.diffUtil = new DiffUtil();
	}

	@Override
	public PersistenceEntity call() throws Exception {
		
		try {

			SvnUtil svnUtil = new SvnUtil(url,repositoryRoot);
			
			/* Realiza o diff do arquivo*/
			ByteArrayOutputStream fileDiff;
			
			if(oFile.getCopyPath() != null)
				fileDiff = svnUtil.diff(oFile.getCopyPath(), oFile.getPath(), oFile.getCopyRevision(), revisionNumber);
			else
				fileDiff = svnUtil.diff(oFile.getPath(), revisionNumber-1, revisionNumber);
			
			/* Extrai a quantidade de alteracoes e salva o objeto no banco */
			Map<String, Integer> modifications = diffUtil.analyze(fileDiff.toString());
			oFile.setLineAdd(modifications.get("adds"));
			oFile.setLineMod(modifications.get("mods"));
			oFile.setLineDel(modifications.get("dels"));
			oFile.setLineCondition(modifications.get("conditions"));
			oFile.setExtracted(true);
			
			return oFile;
			
		} catch (SVNException e) {
			log.error(e.getMessage());
		} catch (FileNotFoundException e) {
			log.error(e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		
		return null;
		
	}

}
