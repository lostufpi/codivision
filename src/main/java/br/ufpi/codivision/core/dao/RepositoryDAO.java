/**
 * 
 */
package br.ufpi.codivision.core.dao;

import java.util.List;

import javax.persistence.NoResultException;

import br.ufpi.codivision.common.dao.GenericDAO;
import br.ufpi.codivision.core.model.Configuration;
import br.ufpi.codivision.core.model.Repository;
import br.ufpi.codivision.core.model.vo.AuthorPercentage;
import br.ufpi.codivision.core.model.vo.RepositoryVO;

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
		return em.createQuery("select new br.ufpi.ppgcc.mi.core.model.vo.RepositoryVO(repository) "
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
		
		List<AuthorPercentage> at =  em.createQuery("select new br.ufpi.ppgcc.mi.core.model.vo.AuthorPercentage( revision.author, sum(3.293 + (1.98 * " + firstAuthor + ") + (0.164 * " + myAlterations + ") - (0.321 * log( 1 + " + otherAlterations + "))), 0l) from Repository as repository, "
				+ "IN(repository.revisions) as revision, IN(revision.files) as file where repository.id = :repositoryId and file.path like :path group by revision.author", AuthorPercentage.class)
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
		String newAuthorPercentace = "new br.ufpi.ppgcc.mi.core.model.vo.AuthorPercentage("
										+ "revision.author, "
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
						+ "revision.author "
					+ "ORDER BY "
						+ "revision.author ASC";
		
		return em.createQuery(query, AuthorPercentage.class)
				.setParameter("repositoryId", repositoryId)
				.setParameter("path", path+"%")
				.getResultList();
		
	}
	
}
