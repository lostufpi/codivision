package codivision.feature;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jgrapht.graph.DefaultEdge;

import br.ufpi.codivision.feature.java.algorithm.ControllerDefiner;
import br.ufpi.codivision.feature.java.algorithm.FeatureDefiner;
import br.ufpi.codivision.feature.java.graph.ClassGraphBuilder;
import br.ufpi.codivision.feature.java.model.Class;
import br.ufpi.codivision.feature.java.model.Feature;
import br.ufpi.codivision.feature.java.model.FeatureClass;
import br.ufpi.codivision.feature.java.model.NodeInfo;
import br.ufpi.codivision.feature.java.model.Package;

public class TestFeatureDefiner {

	public static void main(String[] args) {
		//creatingGraphTest();
		//defineCandidatesControllerTest();
		defineFeaturesTest();
	}
	
	public static void creatingGraphTest(){
		List<Class> classes = new ArrayList<Class>();
//		classes = ReadClassFiles.readFiles("D:\\Projects\\Mestrado\\Automatos");
//		classes = ReadClassFiles.readFiles("D:\\Projects\\Mestrado\\Sagres");
		ClassGraphBuilder cgb = new ClassGraphBuilder(classes);
		
		for (Iterator<NodeInfo> iterator = cgb.getG().vertexSet().iterator(); iterator.hasNext();) {
			NodeInfo c = iterator.next();
			System.out.print(c.getC().getName() + "["+ c.getDegreeIN() + "-" + c.getDegreeOUT()+"]"+ "["+ c.hasCycle() + "]"+ "["+ cgb.getG().edgesOf(c).size()+"]"+" -> " );
			for (Iterator<DefaultEdge> i = cgb.getG().edgesOf(c).iterator(); i.hasNext();) {
				DefaultEdge de = (DefaultEdge) i.next();
				System.out.print("(" + cgb.getG().getEdgeSource(de).getC() + " : " + cgb.getG().getEdgeTarget(de).getC() + ")");
			}
			System.out.println("");
		}
		
		
	}
	
	public static void defineCandidatesControllerTest(){
		List<Class> classes = new ArrayList<Class>();
//		classes = ReadClassFiles.readFiles("D:\\Projects\\Mestrado\\Automatos");
//		classes = ReadClassFiles.readFiles("D:\\Projects\\Mestrado\\Sagres");
		
		ClassGraphBuilder cgb = new ClassGraphBuilder(classes);
		ControllerDefiner cd = new ControllerDefiner(cgb.getG());

		List<Package> packages = new ArrayList<Package>();
		packages = cd.controllersDefiner();
		
		for (Package p : packages) {
			System.out.print(p.getName() + ": ");
			for (Class c : p.getClasses()) {
				System.out.print(c.getName() + " ");
			}
			System.out.println("");
		}
	}
	
	public static void defineFeaturesTest(){
		List<Class> classes = new ArrayList<Class>();
//		classes = ReadClassFiles.readFiles("D:\\Projects\\Mestrado\\Sagres");
//		classes = ReadClassFiles.readFiles("D:\\Projects\\Mestrado\\Automatos");
		
		ClassGraphBuilder cgb = new ClassGraphBuilder(classes);
		ControllerDefiner cd = new ControllerDefiner(cgb.getG());

		List<Package> packages = new ArrayList<Package>();
		packages = cd.controllersDefiner();
//		Class controller = new Class();
//		Method m = null; 
//		
//		for (Package p : packages) {
//			for (Class c : p.getClasses()) {
//				for(Method method : c.getMethods()) {
//					if(c.formatName().equals("HomeController") && method.getName().equals("getAutomatoByLabel")){
//						controller = c;
//						m = method;
//					}
//				}
//			}
//		}
		
		List<Feature> features = new ArrayList<>();
		FeatureDefiner fd = new FeatureDefiner();
		features = fd.definer(packages, cgb.getG());
		//List<Class> classList = fd.featureClasses(controller, m, cgb.getG());
		for(Feature feature : features){
			for (FeatureClass featureClasse : feature.getFeatureClasses()) {
				System.out.println(feature.getName() + ": " + featureClasse.getClass_().formatFullname());
			}
		}
	}
}
