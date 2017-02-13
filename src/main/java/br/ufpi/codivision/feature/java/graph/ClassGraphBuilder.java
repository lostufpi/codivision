package br.ufpi.codivision.feature.java.graph;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import br.ufpi.codivision.feature.java.model.Class;
import br.ufpi.codivision.feature.java.model.NodeInfo;
import br.ufpi.codivision.feature.java.model.ReferenceSet;
import br.ufpi.codivision.feature.java.parser.ClassReferenceManager;

/**
 * @author Vanderson Moura
 *
 */
public class ClassGraphBuilder {
	
	private DirectedGraph<NodeInfo, DefaultEdge> graph;
	
	/**
	 * @param systemClasses 
	 */
	public ClassGraphBuilder(List<Class> systemClasses) {
		this.graph = new DefaultDirectedGraph<NodeInfo, DefaultEdge>(DefaultEdge.class);
		this.buildClassGraph(systemClasses);
	}

	/**
	 * @param systemClasses to build system graph 
	 */
	private void buildClassGraph(List<Class> systemClasses) {
		ClassReferenceManager crm = new ClassReferenceManager();
		ClassVerificator cv = new ClassVerificator();
		List<NodeInfo> nodes = new ArrayList<NodeInfo>();
		ReferenceSet referenceSet;
		List<NodeInfo> validsReferences;
		
		inicializeNodes(nodes, systemClasses);
		
		for (NodeInfo n : nodes) {
			referenceSet = new ReferenceSet(); 
			referenceSet = crm.references(n.getC());
			validsReferences = new ArrayList<NodeInfo>(); 
			validsReferences = cv.filterClasses(nodes, n.getC(), referenceSet); 
			
			if(!graph.containsVertex(n)){
				graph.addVertex(n);
			}
			
			for (NodeInfo reference : validsReferences) {
				if(!graph.containsVertex(reference)){
					graph.addVertex(reference);
				}
				n.incrementOUT();
				reference.incrementIN();
				graph.addEdge(n,reference);
				
				if(n.getC().getFullname().equals(reference.getC().getFullname())){
					n.setHasCycle(true);
				}
			}
		}
	}
	
	/**
	 * @return the graph  
	 */
	public DirectedGraph<NodeInfo, DefaultEdge> getG() {
		return graph;
	}

	/**
	 * @param graph the graph to set
	 */
	public void setG(DirectedGraph<NodeInfo, DefaultEdge> graph) {
		this.graph = graph;
	}
	
	/**
	 * @param nodes
	 * @param classes
	 */
	private void inicializeNodes(List<NodeInfo> nodes, List<Class> classes){
		for (Class c : classes) {
			nodes.add(new NodeInfo(c));
		}
	}
}
