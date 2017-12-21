/**
 * 
 */
package br.ufpi.codivision.core.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import br.ufpi.codivision.common.dao.GenericDAO;
import br.ufpi.codivision.core.model.Configuration;
import br.ufpi.codivision.core.model.Repository;
import br.ufpi.codivision.core.model.vo.AuthorPercentage;
import br.ufpi.codivision.core.model.vo.CommitHistory;
import br.ufpi.codivision.core.model.vo.LineChart;
import br.ufpi.codivision.core.model.vo.RepositoryVO;
import br.ufpi.codivision.core.util.QuickSort;

/**
 * @author Werney Ayala
 *
 */
public class RepositoryDAO extends GenericDAO<Repository>{

	/**
	 * @param entityClass
	 */
	public RepositoryDAO() {
		super(Repository.class);
	}
	
	/**
	 * This method gets a repository from your URL
	 * @param url - The url of the repository
	 * @return Repository or null if it does not find
	 */
	public Repository findByUrl(String url) {
		try {
			return em.createQuery("select repository from Repository as repository "
					+ "where repository.url = :url", Repository.class)
					.setParameter("url", url)
					.getSingleResult();
		} catch (NoResultException e){
			return null;
		}
	}
	
	/**
	 * This method gets the list of repositories from a User
	 * @param userLogin - The login of the target user
	 * @return List<RepositoryVO> - The list of repositories from a User 
	 */
	public List<RepositoryVO> listMyRepositories(Long userId) {
		return em.createQuery("select new br.ufpi.codivision.core.model.vo.RepositoryVO(repository) "
				+ "from Repository as repository, IN(repository.userRepositories) as ur "
				+ "where ur.user.id = :userId and repository.deleted = false order by repository.name asc", RepositoryVO.class)
				.setParameter("userId", userId)
				.getResultList();
	}
	
	public Configuration getConfiguration(Long repositoryId) {
		return em.createQuery("select configuration from Repository as repository inner join "
				+ "repository.configuration as configuration where repository.id = :repositoryId", Configuration.class)
				.setParameter("repositoryId", repositoryId)
				.getSingleResult();
	}
	
	public List<AuthorPercentage> getDoa(Long repositoryId, String path) {
		
		String otherAlterations = "(SELECT count(*) from "
				+ "Repository as repo1, "
				+ "IN(repo1.revisions) as rev1, "
				+ "IN(rev1.files) as file1 "
			+ "WHERE "
				+ "file1.path = file.path and "
				+ "repo1.id = :repositoryId and "
				+ "rev1.author != revision.author)";
		
		String myAlterations = "(SELECT count(*) from "
				+ "Repository as repo2, "
				+ "IN(repo2.revisions) as rev2, "
				+ "IN(rev2.files) as file2 "
			+ "WHERE "
				+ "file2.path = file.path and "
				+ "repo2.id = :repositoryId and "
				+ "rev2.author = revision.author)";
		
		String firstAuthor = "(SELECT count(*) from "
				+ "Repository as repo3, "
				+ "IN(repo3.revisions) as rev3, "
				+ "IN(rev3.files) as file3 "
			+ "WHERE "
				+ "file3.path = file.path and "
				+ "repo3.id = :repositoryId and "
				+ "rev3.author = revision.author and "
				+ "file3.operationType = 'ADD' )"; 
		
		List<AuthorPercentage> at =  em.createQuery("select new br.ufpi.codivision.core.model.vo.AuthorPercentage( revision.author.name, sum(3.293 + (1.98 * " + firstAuthor + ") + (0.164 * " + myAlterations + ") - (0.321 * log( 1 + " + otherAlterations + "))), 0l) from Repository as repository, "
				+ "IN(repository.revisions) as revision, IN(revision.files) as file where repository.id = :repositoryId and file.path like :path group by revision.author.name", AuthorPercentage.class)
				.setParameter("repositoryId", repositoryId)
				.setParameter("path", path + "%")
				.getResultList();
		return at;
	}
	
	/**
	 * This method obtains the percentage of changes in a directory or file held by each developer
	 * @param repositoryId - The id of target repository
	 * @param path - The path of the directory of file
	 * @return List<AuthorPercentage> - The list with the percentage of alterations made by each developer
	 * 									 in the specified directory or file
	 */
	public List<AuthorPercentage> getPercentage(Long repositoryId, String path){
		
		/* Obtem a quantidade de alterações realizadas por outros desenvolvedores no arquivo 
		 * Essas alterações são calculadas em nível de arquivo pois foi a única forma que encotrei de fazer
		 * Essa é uma subconsulta baseada na consulta principal, então ela depende da data da consulta principal,
		 * 		pois só são contabilizadas alterações posteriores a data informada */
		String futureAlterations = "SELECT case when count(*) < 20 then count(*) else 20 end from "
										+ "Repository as r, "
										+ "IN(r.revisions) as rev, "
										+ "IN(rev.files) as f "
									+ "WHERE "
										+ "f.path = file.path and "
										+ "r.id = :repositoryId and "
										+ "rev.author != revision.author and "
										+ "rev.date > revision.date";
		
		/* Cria um objeto do tipo AuthorPercentage
		 * Recebe como parâmetros um desenvolvedor e a quantidade de alterações realizadas por ele
		 * A quantidade de alterações é calculada da seguinte forma:
		 * 		- São somados os valores de ADD MOD e DEL para arquivo dentro do caminho selecionado
		 * 				- Esses valores são multiplicados pelos seus respectivos pesos
		 * 		- O valor resultante é penalizado de acordo com a data em que foi feita essa alteração 
		 * 				- 10% por mês decorrido 
		 * 		- O valor resultante é penalizado de acordo com a quantidade de alterações realizadas 
		 * 				por outros desenvolvedores (5% por cada alteração realizada por outro desenvolvedor). 
		 * 		- Todo esse calculo é feito para cada arquivo e depois agregado por todos os arquivos 
		 * 				por meio da função SUM e agrupado por cada desenvolvedor */
		String newAuthorPercentace = "new br.ufpi.codivision.core.model.vo.AuthorPercentage("
										+ "revision.author.name, "
								   		+ "sum( "
								   			+ "((file.lineAdd * configuration.addWeight) + (file.lineMod * configuration.modWeight) + (file.lineDel * configuration.delWeight)) * "
								   			+ "(1.0 - ( datediff(current_date(),cast(revision.date as date)) * configuration.monthlyDegradationPercentage)) * "
								   			+ "(1.0 - ((" + futureAlterations + ") * (configuration.changeDegradation/100) )) "
					   					+ "), "
					   					+ "sum(file.lineAdd + file.lineMod + file.lineDel + 0)"
				   					+ ")";
		
		/* A query principal */
		String query = "SELECT " + newAuthorPercentace + " from "
							+ "Repository as repository "
							+ "inner join repository.configuration as configuration, "
							+ "IN(repository.revisions) as revision, "
							+ "IN(revision.files) as file "
					+ "WHERE "
						+ "file.path LIKE :path and "
						+ "repository.id = :repositoryId and "
						+ "revision.date >= configuration.initDate and "
						+ "revision.date <= configuration.endDate "
					+ "GROUP BY "
						+ "revision.author.name "
					+ "ORDER BY "
						+ "revision.author.name ASC";
		
		return em.createQuery(query, AuthorPercentage.class)
				.setParameter("repositoryId", repositoryId)
				.setParameter("path", path+"%")
				.getResultList();
		
	}
	
public List<AuthorPercentage> getFeaturePercentage(Long repositoryId, Long featureId){
		
		/* Obtem a quantidade de alterações realizadas por outros desenvolvedores no arquivo 
		 * Essas alterações são calculadas em nível de arquivo pois foi a única forma que encotrei de fazer
		 * Essa é uma subconsulta baseada na consulta principal, então ela depende da data da consulta principal,
		 * 		pois só são contabilizadas alterações posteriores a data informada */
		String futureAlterations = "SELECT case when count(*) < 20 then count(*) else 20 end from "
										+ "Repository as r, "
										+ "IN(r.revisions) as rev, "
										+ "IN(rev.files) as f "
									+ "WHERE "
										+ "f.path = file.path and "
										+ "r.id = :repositoryId and "
										+ "rev.author != revision.author and "
										+ "rev.date > revision.date";
		
		/* Cria um objeto do tipo AuthorPercentage
		 * Recebe como parâmetros um desenvolvedor e a quantidade de alterações realizadas por ele
		 * A quantidade de alterações é calculada da seguinte forma:
		 * 		- São somados os valores de ADD MOD e DEL para arquivo dentro do caminho selecionado
		 * 				- Esses valores são multiplicados pelos seus respectivos pesos
		 * 		- O valor resultante é penalizado de acordo com a data em que foi feita essa alteração 
		 * 				- 10% por mês decorrido 
		 * 		- O valor resultante é penalizado de acordo com a quantidade de alterações realizadas 
		 * 				por outros desenvolvedores (5% por cada alteração realizada por outro desenvolvedor). 
		 * 		- Todo esse calculo é feito para cada arquivo e depois agregado por todos os arquivos 
		 * 				por meio da função SUM e agrupado por cada desenvolvedor */
		String newAuthorPercentace = "new br.ufpi.codivision.core.model.vo.AuthorPercentage("
										+ "revision.author.name, "
								   		+ "sum( "
								   			+ "((file.lineAdd * configuration.addWeight) + (file.lineMod * configuration.modWeight) + (file.lineDel * configuration.delWeight)) * "
								   			+ "(1.0 - ( datediff(current_date(),cast(revision.date as date)) * configuration.monthlyDegradationPercentage)) * "
								   			+ "(1.0 - ((" + futureAlterations + ") * (configuration.changeDegradation/100) )) "
					   					+ "), "
					   					+ "sum(file.lineAdd + file.lineMod + file.lineDel + 0)"
				   					+ ")";
		
		String queryFiles = new String();
		
		if(featureId != null) {
			queryFiles = "SELECT f.element.fullname FROM FeatureElement f WHERE f.feature.id = :featureId";
		}else {
			queryFiles = "SELECT f.element.fullname FROM FeatureElement f";
		}
		
		/* A query principal */
		String query = "SELECT " + newAuthorPercentace + " from "
							+ "Repository as repository "
							+ "inner join repository.configuration as configuration, "
							+ "IN(repository.revisions) as revision, "
							+ "IN(revision.files) as file "
					+ "WHERE "
						+ "file.path IN (" + queryFiles + ") and "
						+ "repository.id = :repositoryId and "
						+ "revision.date >= configuration.initDate and "
						+ "revision.date <= configuration.endDate "
					+ "GROUP BY "
						+ "revision.author.name "
					+ "ORDER BY "
						+ "revision.author.name ASC";
		
		TypedQuery<AuthorPercentage> typedQuery = em.createQuery(query, AuthorPercentage.class).setParameter("repositoryId", repositoryId);
		if(featureId != null) {
			typedQuery.setParameter("featureId", featureId);
		}
		
		return typedQuery.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public LineChart getTestCommitsHistory(long repositoryId){
		
		Repository repository = findById(repositoryId);
		String testPathNot = "";
		String testPath = "AND file.path LIKE '' ";
		if(repository.getTestFiles().size()!=0){
			testPath = "AND (";
			testPathNot = "AND NOT(";
		}
		for(int i = 0; i < repository.getTestFiles().size(); i++){
			testPathNot = testPathNot + "file.path LIKE '"+repository.getTestFiles().get(i).getPath()+"%'";
			testPath = testPath + "file.path LIKE '"+repository.getTestFiles().get(i).getPath()+"%'";
			if(i < repository.getTestFiles().size()-1){
				testPathNot = testPathNot + " OR ";
				testPath = testPath + " OR ";
			}
		}
		if(repository.getTestFiles().size()!=0){
			testPathNot = testPathNot + ") ";
			testPath = testPath + ") ";
		}
		//todos os commits
		String query = "SELECT DAY(revision.date) as day, MONTHNAME(revision.date) as month "
				+ "FROM Repository AS repository "
				+ "inner join repository.configuration as configuration, "
				+ "IN (repository.revisions) as revision "
				+ "WHERE repository.id = "+repositoryId+" "
				+ "and revision.date >= configuration.initDate and "
						+ "revision.date <= configuration.endDate "
				+ "GROUP BY MONTH(revision.date) , DAY(revision.date) "
				+ "ORDER BY revision.date";
		//commits sem ser de testes
		String query1 = "SELECT DAY(revision.date) as dia, MONTHNAME(revision.date) as mes, "
				+ "SUM(file.lineAdd + file.lineMod + file.lineDel) as soma "
				+ "FROM Repository AS repository "
				+ "inner join repository.configuration as configuration, "
				+ "IN(repository.revisions) as revision, "
				+ "IN(revision.files) as file "
				+ "WHERE repository.id = "+repositoryId+" "+testPathNot
				+ "and revision.date >= configuration.initDate and "
				+ "revision.date <= configuration.endDate "
				+ "GROUP BY MONTH(revision.date) , DAY(revision.date) "
				+ "ORDER BY revision.date";
		//commits de testes
		String query2 = "SELECT DAY(revision.date) as dia, MONTHNAME(revision.date) as mes, "
				+ "SUM(file.lineAdd + file.lineMod + file.lineDel) as soma "
				+ "FROM Repository AS repository "
				+ "inner join repository.configuration as configuration, "
				+ "IN(repository.revisions) as revision, "
				+ "IN(revision.files) as file "
				+ "WHERE repository.id = "+repositoryId+" "+testPath
				+ "and revision.date >= configuration.initDate and "
				+ "revision.date <= configuration.endDate "
				+ "GROUP BY MONTH(revision.date) , DAY(revision.date) "
				+ "ORDER BY revision.date";
		
		LineChart chart = new LineChart();
		
		
		
		List<Object[]> list = em.createQuery(query).getResultList();
		String[] categories = new String[list.size()];
		
		for(int i = 0; i < list.size(); i++){
			categories[i] =   list.get(i)[0] + " - " +  list.get(i)[1];
			
		}
		List<Object[]> list1 = em.createQuery(query1).getResultList();
		CommitHistory history = new CommitHistory();
		history.setName("Contribuição");
		
		long[] vetor = new long[list.size()];
		fodefora: for(int i = 0; i < list.size(); i++){
			for(int j = 0;j < list1.size(); j++){
				vetor[i] = 0l;
			if(categories[i].equals(list1.get(j)[0] + " - " +  list1.get(j)[1])){
				vetor[i] = (long) list1.get(j)[2];
				continue fodefora;
			}
			}
		}
		
		history.setData(vetor);
		
		List<Object[]> list2 = em.createQuery(query2).getResultList();
		CommitHistory history2 = new CommitHistory();
		history2.setName("Contribuição nos Testes");
		
		long[] vetor2 = new long[list.size()];
		
		fora: for(int i = 0; i < list.size(); i++){
			for(int j = 0;j < list2.size(); j++){
				vetor2[i] = 0l;
				if(categories[i].equals(list2.get(j)[0] + " - " +  list2.get(j)[1])){
					vetor2[i] = (long) list2.get(j)[2];
					continue fora;
				}
			}
		}
		
		history2.setData(vetor2);
		
		List<CommitHistory> commits = new ArrayList<CommitHistory>();
		commits.add(history);
		commits.add(history2);
		chart.setDataSeries(commits);
		chart.setDataCategories(categories);
		
		
		
		return chart;
		
		
	}
	
	@SuppressWarnings("unchecked")
	public List<CommitHistory> getContribuitionAuthorTest(long repositoryId){
		
		Repository repository = findById(repositoryId);
		String testPathNot = "";
		String testPath = "AND file.path LIKE '' ";
		if(repository.getTestFiles().size()!=0){
			testPath = "AND (";
			testPathNot = "AND NOT(";
		}
		for(int i = 0; i < repository.getTestFiles().size(); i++){
			testPathNot = testPathNot + "file.path LIKE '"+repository.getTestFiles().get(i).getPath()+"%'";
			testPath = testPath + "file.path LIKE '"+repository.getTestFiles().get(i).getPath()+"%'";
			if(i < repository.getTestFiles().size()-1){
				testPathNot = testPathNot + " OR ";
				testPath = testPath + " OR ";
			}
		}
		if(repository.getTestFiles().size()!=0){
			testPathNot = testPathNot + ") ";
			testPath = testPath + ") ";
		}
		
				String query = "SELECT revision.author.name as author, "
						+ "SUM(file.lineAdd + file.lineMod + file.lineDel) as soma "
						+ "FROM Repository AS repository "
						+ "inner join repository.configuration as configuration, "
						+ "IN(repository.revisions) as revision, "
						+ "IN(revision.files) as file "
						+ "WHERE repository.id = "+repositoryId+" "+testPath
						+ "and revision.date >= configuration.initDate and "
						+ "revision.date <= configuration.endDate "
						+ "GROUP BY revision.author.name "
						+ "ORDER BY revision.author.name";
				
				List<Object[]> list = em.createQuery(query).getResultList();
				
				ArrayList<CommitHistory> lista = new ArrayList<CommitHistory>();
				for(int i = 0; i<list.size(); i++){
					CommitHistory history = new CommitHistory();
					history.setName((String) list.get(i)[0]);
					long[] vetor = new long[1];
					history.setName((String) list.get(i)[0]);
					vetor[0] = (long) list.get(i)[1];
					history.setData(vetor);
					lista.add(history);
				}
				
				return lista;
				
	}
	@SuppressWarnings("unchecked")
	public LineChart getContribuitionQntLine(long repositoryId, String path){
	
		Repository repository = findById(repositoryId);
		String testPathNot = "";
		String testPath = "AND file.path LIKE '' ";
		if(repository.getTestFiles().size()!=0){
			testPath = "AND (";
			testPathNot = "AND NOT(";
		}
		for(int i = 0; i < repository.getTestFiles().size(); i++){
			testPathNot = testPathNot + "file.path LIKE '"+repository.getTestFiles().get(i).getPath()+"%'";
			testPath = testPath + "file.path LIKE '"+repository.getTestFiles().get(i).getPath()+"%'";
			if(i < repository.getTestFiles().size()-1){
				testPathNot = testPathNot + " OR ";
				testPath = testPath + " OR ";
			}
		}
		if(repository.getTestFiles().size()!=0){
			testPathNot = testPathNot + ") ";
			testPath = testPath + ") ";
		}
		
		String query = "SELECT revision.author.name as author, "
				+ "SUM(file.lineAdd + file.lineMod + file.lineDel) as soma "
				+ "FROM Repository AS repository "
				+ "inner join repository.configuration as configuration, "
				+ "IN(repository.revisions) as revision, "
				+ "IN(revision.files) as file "
				+ "WHERE repository.id = "+repositoryId+" AND file.path LIKE '"+path+"%' "+testPathNot
				+ "and revision.date >= configuration.initDate and "
				+ "revision.date <= configuration.endDate "
				+ "GROUP BY revision.author.name "
				+ "ORDER BY revision.author.name";
		
		List<Object[]> list = em.createQuery(query).getResultList();
		
		ArrayList<CommitHistory> lista = new ArrayList<CommitHistory>();
		for(int i = 0; i<list.size(); i++){
			CommitHistory history = new CommitHistory();
			history.setName((String) list.get(i)[0]);
			long[] vetor = new long[1];
			history.setName((String) list.get(i)[0]);
			vetor[0] = (long) list.get(i)[1];
			history.setData(vetor);
			lista.add(history);
		}
		
		QuickSort.sort(lista);
		
		List<CommitHistory> listaTest = getContribuitionQntLineTest(repositoryId, path);
		
		
		
		List<String> dev = new ArrayList<String>();
		
		for(int i = 0; i <lista.size(); i++){
			dev.add(lista.get(i).getName());
		}
		
		for(int i = 0; i< listaTest.size(); i++){
			int qnt = 0;
			for(String aux: dev){
				if(aux.equals(listaTest.get(i).getName())){
					qnt++;
				}
			}
			if(qnt==0){
				dev.add(listaTest.get(i).getName());
			}
		}
		
		long[] vetor = new long[dev.size()];
		long[] vetor1 = new long[dev.size()];
		
		int j = 0;
		
		CommitHistory contri = new CommitHistory();
		contri.setName("Contribuicao");
		
		CommitHistory contriTest = new CommitHistory();
		contriTest.setName("Contribuicao nos Testes");
		
		for(String aux: dev){
			
			vetor[j] = 0;
			for(int i = 0; i <lista.size(); i++){
				if(aux.equals(lista.get(i).getName())){
					vetor[j] = lista.get(i).getData()[0];
				}
			}
			
			vetor1[j] = 0;
			for(int i = 0; i <listaTest.size(); i++){
				if(aux.equals(listaTest.get(i).getName())){
					vetor1[j] = listaTest.get(i).getData()[0];
				}
			}
			j++;
		}
		
		contri.setData(vetor);
		
		contriTest.setData(vetor1);
		
		List<CommitHistory> total = new ArrayList<CommitHistory>();
		total.add(contri);
		total.add(contriTest);
		
		
		String[] desen = new String[dev.size()];
		int k = 0;
		for(String aux:dev){
			desen[k] = aux;
			k++;
		}
		
		LineChart chart = new LineChart();
		chart.setDataSeries(total);
		chart.setDataCategories(desen);
		
		return chart;
		
		
	}
	@SuppressWarnings("unchecked")
	public List<CommitHistory> getContribuitionQntLineTest(long repositoryId, String path){
		
		Repository repository = findById(repositoryId);
		String testPathNot = "";
		String testPath = "AND file.path LIKE '' ";
		if(repository.getTestFiles().size()!=0){
			testPath = "AND (";
			testPathNot = "AND NOT(";
		}
		for(int i = 0; i < repository.getTestFiles().size(); i++){
			testPathNot = testPathNot + "file.path LIKE '"+repository.getTestFiles().get(i).getPath()+"%'";
			testPath = testPath + "file.path LIKE '"+repository.getTestFiles().get(i).getPath()+"%'";
			if(i < repository.getTestFiles().size()-1){
				testPathNot = testPathNot + " OR ";
				testPath = testPath + " OR ";
			}
		}
		if(repository.getTestFiles().size()!=0){
			testPathNot = testPathNot + ") ";
			testPath = testPath + ") ";
		}
		String query = "SELECT revision.author.name as author, "
				+ "SUM(file.lineAdd + file.lineMod + file.lineDel) as soma "
				+ "FROM Repository AS repository "
				+ "inner join repository.configuration as configuration, "
				+ "IN(repository.revisions) as revision, "
				+ "IN(revision.files) as file "
				+ "WHERE repository.id = "+repositoryId+" AND file.path LIKE '"+path+"%' "+testPath
				+ "and revision.date >= configuration.initDate and "
				+ "revision.date <= configuration.endDate "
				+ "GROUP BY revision.author.name "
				+ "ORDER BY revision.author.name";
		
		List<Object[]> list = em.createQuery(query).getResultList();
		
		ArrayList<CommitHistory> lista = new ArrayList<CommitHistory>();
		for(int i = 0; i<list.size(); i++){
			CommitHistory history = new CommitHistory();
			history.setName((String) list.get(i)[0]);
			long[] vetor = new long[1];
			history.setName((String) list.get(i)[0]);
			vetor[0] = (long) list.get(i)[1];
			history.setData(vetor);
			lista.add(history);
		}
		
		QuickSort.sort(lista);
		return lista;
		
	}
	@SuppressWarnings("unchecked")
	public LineChart getTestCommitsHistoryAuthor(long repositoryId, String author){
	
		Repository repository = findById(repositoryId);
		String testPathNot = "";
		String testPath = "AND file.path LIKE '' ";
		if(repository.getTestFiles().size()!=0){
			testPath = "AND (";
			testPathNot = "AND NOT(";
		}
		for(int i = 0; i < repository.getTestFiles().size(); i++){
			testPathNot = testPathNot + "file.path LIKE '"+repository.getTestFiles().get(i).getPath()+"%'";
			testPath = testPath + "file.path LIKE '"+repository.getTestFiles().get(i).getPath()+"%'";
			if(i < repository.getTestFiles().size()-1){
				testPathNot = testPathNot + " OR ";
				testPath = testPath + " OR ";
			}
		}
		if(repository.getTestFiles().size()!=0){
			testPathNot = testPathNot + ") ";
			testPath = testPath + ") ";
		}
		
		//todos os commits
		String query = "SELECT DAY(revision.date) as day, MONTHNAME(revision.date) as month "
				+ "FROM Repository AS repository "
				+ "inner join repository.configuration as configuration, "
				+ "IN (repository.revisions) as revision "
				+ "WHERE repository.id = "+repositoryId+" "
				+ "and revision.date >= configuration.initDate and "
				+ "revision.date <= configuration.endDate "
				+ "GROUP BY MONTH(revision.date) , DAY(revision.date) "
				+ "ORDER BY revision.date";
		//commits sem ser de testes
		String query1 = "SELECT DAY(revision.date) as dia, MONTHNAME(revision.date) as mes, "
				+ "SUM(file.lineAdd + file.lineMod + file.lineDel) as soma "
				+ "FROM Repository AS repository "
				+ "inner join repository.configuration as configuration, "
				+ "IN(repository.revisions) as revision, "
				+ "IN(revision.files) as file "
				+ "WHERE repository.id = "+repositoryId+" AND revision.author.name LIKE '"+author+"' "+testPathNot
				+ "and revision.date >= configuration.initDate and "
				+ "revision.date <= configuration.endDate "
				+ "GROUP BY MONTH(revision.date) , DAY(revision.date) "
				+ "ORDER BY revision.date";
		//commits de testes
		String query2 = "SELECT DAY(revision.date) as dia, MONTHNAME(revision.date) as mes, "
				+ "SUM(file.lineAdd + file.lineMod + file.lineDel) as soma "
				+ "FROM Repository AS repository "
				+ "inner join repository.configuration as configuration, "
				+ "IN(repository.revisions) as revision, "
				+ "IN(revision.files) as file "
				+ "WHERE repository.id = "+repositoryId+" AND revision.author.name LIKE '"+author+"' "+testPath
				+ "and revision.date >= configuration.initDate and "
				+ "revision.date <= configuration.endDate "
				+ "GROUP BY MONTH(revision.date) , DAY(revision.date) "
				+ "ORDER BY revision.date";
		
		LineChart chart = new LineChart();
		
		
		List<Object[]> list = em.createQuery(query).getResultList();
		String[] categories = new String[list.size()];
		
		for(int i = 0; i < list.size(); i++){
			categories[i] =   list.get(i)[0] + " - " +  list.get(i)[1];
			
		}
		List<Object[]> list1 = em.createQuery(query1).getResultList();
		CommitHistory history = new CommitHistory();
		history.setName("Contribuição");
		
		long[] vetor = new long[list.size()];
		fodefora: for(int i = 0; i < list.size(); i++){
			for(int j = 0;j < list1.size(); j++){
				vetor[i] = 0l;
			if(categories[i].equals(list1.get(j)[0] + " - " +  list1.get(j)[1])){
				vetor[i] = (long) list1.get(j)[2];
				continue fodefora;
			}
			}
		}
		
		history.setData(vetor);
		
		List<Object[]> list2 = em.createQuery(query2).getResultList();
		CommitHistory history2 = new CommitHistory();
		history2.setName("Contribuição nos Testes");
		
		long[] vetor2 = new long[list.size()];
		
		fora: for(int i = 0; i < list.size(); i++){
			for(int j = 0;j < list2.size(); j++){
				vetor2[i] = 0l;
				if(categories[i].equals(list2.get(j)[0] + " - " +  list2.get(j)[1])){
					vetor2[i] = (long) list2.get(j)[2];
					continue fora;
				}
			}
		}
		
		history2.setData(vetor2);
		
		List<CommitHistory> commits = new ArrayList<CommitHistory>();
		commits.add(history);
		commits.add(history2);
		chart.setDataSeries(commits);
		chart.setDataCategories(categories);
		
		
		
		return chart;
		
		
	}
	@SuppressWarnings("unchecked")
	public List<String> getAuthors(long repositoryId){
		String query = "SELECT revision.author.name as author "
				+ "FROM Repository AS repository "
				+ "inner join repository.configuration as configuration, "
				+ "IN(repository.revisions) as revision "
				+ "WHERE revision.date >= configuration.initDate AND "
				+ "revision.date <= configuration.endDate "
				+ "AND repository.id = "+repositoryId+" GROUP BY revision.author.name";
		
		List<String> list = em.createQuery(query).getResultList();
		return list;
	}
	@SuppressWarnings("unchecked")
	public List<CommitHistory> getContribuitionQntLine(long repositoryId){
		String path = "/";
		Repository repository = findById(repositoryId);
		String testPathNot = "";
		String testPath = "AND file.path LIKE '' ";
		if(repository.getTestFiles().size()!=0){
			testPath = "AND (";
			testPathNot = "AND NOT(";
		}
		for(int i = 0; i < repository.getTestFiles().size(); i++){
			testPathNot = testPathNot + "file.path LIKE '"+repository.getTestFiles().get(i).getPath()+"%'";
			testPath = testPath + "file.path LIKE '"+repository.getTestFiles().get(i).getPath()+"%'";
			if(i < repository.getTestFiles().size()-1){
				testPathNot = testPathNot + " OR ";
				testPath = testPath + " OR ";
			}
		}
		if(repository.getTestFiles().size()!=0){
			testPathNot = testPathNot + ") ";
			testPath = testPath + ") ";
		}
		
		
		String query = "SELECT revision.author.name as author, "
				+ "SUM(file.lineAdd + file.lineMod + file.lineDel) as soma "
				+ "FROM Repository AS repository "
				+ "inner join repository.configuration as configuration, "
				+ "IN(repository.revisions) as revision, "
				+ "IN(revision.files) as file "
				+ "WHERE repository.id = "+repositoryId+" AND file.path LIKE '"+path+"%' "+testPathNot
				+ "and revision.date >= configuration.initDate and "
				+ "revision.date <= configuration.endDate "
				+ "GROUP BY revision.author.name "
				+ "ORDER BY revision.author.name";
		
		List<Object[]> list = em.createQuery(query).getResultList();
		
		ArrayList<CommitHistory> lista = new ArrayList<CommitHistory>();
		for(int i = 0; i<list.size(); i++){
			CommitHistory history = new CommitHistory();
			history.setName((String) list.get(i)[0]);
			long[] vetor = new long[1];
			history.setName((String) list.get(i)[0]);
			vetor[0] = (long) list.get(i)[1];
			history.setData(vetor);
			lista.add(history);
		}
		
		QuickSort.sort(lista);
		return lista;
		
		
	}
	@SuppressWarnings("unchecked")
	public List<CommitHistory> getContribuitionQntLineTest(long repositoryId){
		String path = "/";
		Repository repository = findById(repositoryId);
		String testPathNot = "";
		String testPath = "AND file.path LIKE '' ";
		if(repository.getTestFiles().size()!=0){
			testPath = "AND (";
			testPathNot = "AND NOT(";
		}
		for(int i = 0; i < repository.getTestFiles().size(); i++){
			testPathNot = testPathNot + "file.path LIKE '"+repository.getTestFiles().get(i).getPath()+"%'";
			testPath = testPath + "file.path LIKE '"+repository.getTestFiles().get(i).getPath()+"%'";
			if(i < repository.getTestFiles().size()-1){
				testPathNot = testPathNot + " OR ";
				testPath = testPath + " OR ";
			}
		}
		if(repository.getTestFiles().size()!=0){
			testPathNot = testPathNot + ") ";
			testPath = testPath + ") ";
		}
		
		
		
		String query = "SELECT revision.author.name as author, "
				+ "SUM(file.lineAdd + file.lineMod + file.lineDel) as soma "
				+ "FROM Repository AS repository "
				+ "inner join repository.configuration as configuration, "
				+ "IN(repository.revisions) as revision, "
				+ "IN(revision.files) as file "
				+ "WHERE repository.id = "+repositoryId+" AND file.path LIKE '"+path+"%' "+testPath
				+ "and revision.date >= configuration.initDate and "
				+ "revision.date <= configuration.endDate "
				+ "GROUP BY revision.author.name "
				+ "ORDER BY revision.author.name";
		
		List<Object[]> list = em.createQuery(query).getResultList();
		
		ArrayList<CommitHistory> lista = new ArrayList<CommitHistory>();
		for(int i = 0; i<list.size(); i++){
			CommitHistory history = new CommitHistory();
			history.setName((String) list.get(i)[0]);
			long[] vetor = new long[1];
			history.setName((String) list.get(i)[0]);
			vetor[0] = (long) list.get(i)[1];
			history.setData(vetor);
			lista.add(history);
		}
		
		QuickSort.sort(lista);
		return lista;
		
	}
	
	public List<AuthorPercentage> getPercentageContribuition(Long repositoryId){
		
		List<CommitHistory> contri = getContribuitionQntLine(repositoryId);
		List<CommitHistory> contriTest = getContribuitionQntLineTest(repositoryId);
		
		long sumContri = 0;
		for(CommitHistory aux: contri){
			sumContri = sumContri + aux.getData()[0];
		}
		
		long sumContriTest = 0;
		for(CommitHistory aux: contriTest){
			sumContriTest = sumContriTest + aux.getData()[0];
		}
		
		List<AuthorPercentage> list = new ArrayList<AuthorPercentage>();
		list.add(new AuthorPercentage("Contribuição", (double) sumContri, sumContri+sumContriTest));
		list.add(new AuthorPercentage("Contribuição nos Testes", (double) sumContriTest, sumContri+sumContriTest));
		
		return list;
		
	}
}