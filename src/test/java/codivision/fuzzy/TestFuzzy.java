//package codivision.fuzzy;
//
//import java.util.Map;
//
//import br.ufpi.codivision.core.model.Repository;
//import br.ufpi.codivision.core.model.Revision;
//import br.ufpi.codivision.core.util.Fuzzy;
//import br.ufpi.codivision.debit.model.File;
//
//public class TestFuzzy {
//
//	public static void main(String[] args) {
//		Repository repository = new Repository();
//		
//		File f1 = new File();
//		File f2 = new File();
//		File f3 = new File();
//		File f4 = new File();
//		File f5 = new File();
//		
//		f1.setPath("f1");
//		f2.setPath("f2");
//		f3.setPath("f3");
//		f4.setPath("f4");
//		f5.setPath("f5");
//
//		f1.setQntBadSmellComment(3);
//		f2.setQntBadSmellComment(0);
//		f3.setQntBadSmellComment(0);
//		f4.setQntBadSmellComment(0);
//		f5.setQntBadSmellComment(5);
//		
//		repository.getCodeSmallsFile().add(f1);
//		repository.getCodeSmallsFile().add(f2);
//		repository.getCodeSmallsFile().add(f3);
//		repository.getCodeSmallsFile().add(f4);
//		repository.getCodeSmallsFile().add(f5);
//		
//		
//		Revision r1 = new Revision();
//		Revision r2 = new Revision();
//		Revision r3 = new Revision();
//		
//		r1.setAuthor("irva");
//		r2.setAuthor("toin");
//		r3.setAuthor("nilton");
//		
//		File f2r1 = new File();
//		f2r1.setPath("f2");
//		f2r1.setQntBadSmellComment(3);
//		
//		File f3r1 = new File();
//		f3r1.setPath("f3");
//		f3r1.setQntBadSmellComment(1);
//		
//		r1.getCodeSmellsFileAlteration().add(f2r1);
//		r1.getCodeSmellsFileAlteration().add(f3r1);
//		
//		File f2r2 = new File();
//		f2r2.setPath("f2");
//		f2r2.setQntBadSmellComment(1);
//		
//		File f3r2 = new File();
//		f3r2.setPath("f3");
//		f3r2.setQntBadSmellComment(0);
//		
//		r2.getCodeSmellsFileAlteration().add(f2r2);
//		r2.getCodeSmellsFileAlteration().add(f3r2);
//		
//		Revision r0 = new Revision();
//		r0.setAuthor("toin");
//		
//		File f2r0 = new File();
//		f2r0.setPath("f2");
//		f2r0.setQntBadSmellComment(5);
//		
//		File f3r0 = new File();
//		f3r0.setPath("f3");
//		f3r0.setQntBadSmellComment(2);
//		
//		r0.getCodeSmellsFileAlteration().add(f2r0);
//		r0.getCodeSmellsFileAlteration().add(f3r0);
//		
//		
//		
//		repository.getRevisions().add(r3);
//		repository.getRevisions().add(r2);
//		repository.getRevisions().add(r1);
//		repository.getRevisions().add(r0);
//		
//		Map<String, Map<String, Integer>> resolverTD = Fuzzy.historicTDRemove(repository);
//		System.out.println(resolverTD.size());
//	}
//}
