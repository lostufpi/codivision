package br.ufpi.codivision.feature.java.algorithm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import br.ufpi.codivision.feature.java.algorithm.model.PackageReference;
import br.ufpi.codivision.feature.java.model.NodeInfo;
import br.ufpi.codivision.feature.java.model.Class;
import br.ufpi.codivision.feature.java.model.Package;;


/**
 * @author Vanderson Moura
 *
 */
public class ControllerDefiner {
	private Graph<NodeInfo, DefaultEdge> graph;
	
	/**
	 * @param graph
	 */
	public ControllerDefiner(Graph<NodeInfo, DefaultEdge> graph) {
		this.graph = graph;
	}
	
	/**
	 * DEFINE UMA LISTA DE PACOTES COM AS CLASSES CONTROLLERS
	 * 
	 * @return
	 */
	public List<Package> controllersDefiner(){
		System.out.println("INICIO DEFINIÇÃO DE CLASSES CONTROLLERS");
		Set<NodeInfo> nodes = this.graph.vertexSet();
		List<NodeInfo> candidateControllers = new ArrayList<NodeInfo>();
		double averageIN, averageOUT, sumIN, sumOUT;
		
		sumIN = sumOUT = 0;
		
		// SOMATÓRIOS DO GRAUS DE SAÍDA E ENTRADA DE CADA NÓ (CLASSE)  
		for (NodeInfo node : nodes) {
			sumIN += node.getDegreeIN();
			sumOUT += node.getDegreeOUT();
		}
		
		// MÉDIA DOS GRAUS DE SAÍDA E ENTRADA DE CADA NÓ (CLASSE) 
		averageIN = sumIN/nodes.size();
		averageOUT = sumOUT/nodes.size();
		
		//BUSCA DE CLASSES MAIS PROVÁVEIS A SEREM CONTROLADORAS (CONTROLLERS)
		for (Iterator<NodeInfo> i = nodes.iterator(); i.hasNext();) {
			NodeInfo node = (NodeInfo) i.next();
			if(!(node.getDegreeOUT() == 0) && !node.hasCycle()){ //SE A CLASSE REFERENCIA OUTRAS CLASSES E NÃO POSSUI REFERÊNCIA À SI PRÓPRIA 
				if(node.getDegreeIN() == 0){ //SE A CLASSE NÃO É REFERENCIADA POR NENHUMA OUTRA CLASSE 
					candidateControllers.add(node); 
				}else{ 
					//SE O NÚMERO DE CLASSES REFERENCIADAS FOR MAIOR QUE A MÉDIA DE GRAU DE SÁIDA E O NÚMERO DE VEZES EM QUE É REFERENCIADA É MENOR QUE A MÉDIA DE GRAU DE ENTRADA  
					if(node.getDegreeOUT() > averageOUT && node.getDegreeIN() < averageIN){
						candidateControllers.add(node);
					}
				}
			}
		}
		
		List<Package> p = candidateControllersFilter(candidateControllers);
		completePackages(p);
		return checkReferenceBetweenPackages(p);
	}
	

	/**
	 * MÉTODO RESPONSÁVEL POR FORMAR PACOTES CONTROLLERS, FILTRANDO OS CONTROLLERS FALSOS POSITIVOS
	 * 
	 * @param canditateControllers
	 * @return
	 */
	private List<Package> candidateControllersFilter(List<NodeInfo> canditateControllers){
		List<Package> packageControllers = new ArrayList<Package>();
		boolean added;
		
		for (NodeInfo c : canditateControllers) {
			added = false;
			if(c.getDegreeIN() == 0){ //SE NÃO EXISTE REFERÊNCIA À CLASSE, ESTA É ASSUMIDA COMO CONTROLLER
				for (Package p : packageControllers) {
					if(p.getName().equals(c.getC().getPackageName())){ //VERIFICA SE O PACOTE DO CONTROLLER JÁ FOI CRIADO 
						p.getClasses().add(c.getC()); //SE SIM, APENAS ADICIONA A CLASSE AO PACOTE CONTROLLER
						added = true; //INDICA QUE O PACOTE JÁ HAVIA SIDO CRIADO 
						break;
					}
				}
				if(!added){ //SE O PACOTE DO CONTROLLER NÃO HAVIA SIDO CRIADO
					Package p = new Package(c.getC().getPackageName()); //CRIA O PACOTE
					p.getClasses().add(c.getC()); //ADICIONA A CLASSE AO PACOTE
					packageControllers.add(p); //ADICIONA O PACOTE À LISTA DE PACOTES CONTROLLERS
				}
			}
		}
		
		//ADICIONANDO CLASSES CANDIDATAS QUE NÃO FORAM ADICIONADAS À PACOTES, MAS QUE PERTECEM À ALGUM PACOTE CONTROLLER
		for (NodeInfo c : canditateControllers) {
			for (Package p : packageControllers) {
				if(p.getName().equals(c.getC().getPackageName()) && c.getDegreeIN() > 0){
					p.getClasses().add(c.getC());
					break;
				}
			}
		}
			
//		Package p = checkPackagesWithAllClassesReferenced(packageControllers, canditateControllers); 
//		if(p != null){
//			packageControllers.add(p);
//		}
		
		return packageControllers;
	}
	
	/**
	 * REMOVE OS PACOTES CONTROLLERS FALSOS POSITIVOS, POR MEIO DE UMA ANÁLISE DE REFERENCIAMENTO ENTRE CLASSES DE PACOTES DIFERENTES
	 * SE CLASSES DE UM PACOTE "A" POSSUI REFERENCIA (INDIRETAMENTE, A PARTIR DA SEGUNDA GERAÇÃO DE FILHOS) CLASSES DE UM PACOTE "B", ESTE
	 * PACOTE É CONSIDERADO UM CONTROLLER FALSO POSITIVO. 
	 * 
	 * @param packageControllers
	 * @return
	 */
	private List<Package> checkReferenceBetweenPackages(List<Package> packageControllers){
		List<NodeInfo> front = new ArrayList<NodeInfo>();
		List<NodeInfo> visited = new ArrayList<NodeInfo>();
		List<Package> packagesToRemove = new ArrayList<Package>();
		List<NodeInfo> ancestralClasses = new ArrayList<NodeInfo>();
		
		System.out.println("INICIO CHECAGEM DE REFERENCIA ENTRE PACOTES");
		System.out.println("QUANTIDADE PACOTES CONTROLLERS: " + packageControllers.size());
		for (Package p1 : packageControllers) {
			List<NodeInfo> classesFromFirstPackage = new ArrayList<NodeInfo>();
			for (Package p2 : packageControllers) {
				if(!p1.getName().equals(p2.getName()) && !checkPackagesReferencesNumberFilter(packageControllers, p2)){ //SE NÃO SÃO OS MESMOS PACOTES
					//ADICIONA TODAS AS CLASSES DO PRIMEIRO PACOTE À UMA LISTA
					for (Class c: p1.getClasses()) {
						NodeInfo n = searchNodeInfoFromClass(c);
						classesFromFirstPackage.add(n);
					}
					//PERCORRE TODAS AS CLASSES DO PRIMEIRO PACOTE
					for (NodeInfo node : classesFromFirstPackage) {
						//PECORRE TODAS AS ARESTAS LIGADAS À DETERMINADA CLASSE
						for (Iterator<DefaultEdge> i = this.graph.edgesOf(node).iterator(); i.hasNext();) {
							DefaultEdge de = (DefaultEdge) i.next();
							//SE A CLASSE FOR O NÓ (NÓ SOURCE) QUE FAZ REFERÊNCIA À OUTRA CLASSE
							if(this.graph.getEdgeSource(de).getC().formatFullname().equals(node.getC().formatFullname())){
								NodeInfo childClass = this.graph.getEdgeTarget(de); //GUARDA ENTÃO A CLASSE (NÓ TARGET) QUE É REFERENCIADA (CLASSE DA PRIMEIRA GERAÇÃO DE NÓS FILHOS DO NÓ DO PRIMEIRO PACOTE)
								Set<DefaultEdge> s = this.graph.edgesOf(childClass); //OBTEM AS ARESTAS LIGADAS AO NÓ REFERENCIADO (FILHO DO NÓ DO PRIMEIRO PACOTE)
								for (Iterator<DefaultEdge> i2 = s.iterator(); i2.hasNext();) {//PECORRE AS ARESTAS LIGADAS AO NÓ REFERENCIADO
									DefaultEdge de2 = (DefaultEdge) i2.next();
									//SE A CLASSE FILHA FOR O NÓ (SOURCE) QUE FAZ REFERÊNCIA À OUTRA CLASSE
									if(this.graph.getEdgeSource(de2).getC().formatFullname().equals(childClass.getC().formatFullname())){
										if(!ancestralClasses.contains(this.graph.getEdgeTarget(de2))){ //SE A CLASSE REFERENCIADA (FILHO DE SEGUNDA GERAÇÃO, ATÉ ESSE PONTO DO PROGRAMA) NÃO ESTÁ CONTIDA NA LISTA DE ANCESTRAIS (LISTA DE FILHOS DA SEGUNDA GERAÇÃO)
											ancestralClasses.add(this.graph.getEdgeTarget(de2)); //ADICIONA À LISTA A CLASSE FILHA REFERENCIADA PELA CLASSE DO PRIMEIRO PACOTE
										}
									}
								}
							}
						}
					}
					
					front.addAll(ancestralClasses); //ADICIONA AS CLASSES ANCESTRAIS REFERENCIADAS PELAS CLASSES DO PRIMEIRO PACOTE EM UM LISTA AUXILIAR DE BUSCA
					NodeInfo actualNode = front.remove(0); //RECEBE (E REMOVE) A PRIMEIRA CLASSE DA LISTA DE ANCESTRAIS
					
					while(front.size() > 0){ //ENQUANDO A LISTA DE ANCESTRAIS NÃO FOR VAZIA
						//SE AO MENOS UMA CLASSE DO SEGUNDO PACOTE É REFERENCIADA POR UMA CLASSE DO PRIMEIRO, INDICA QUE O SEGUNDO PACOTE (E SUAS CLASSES) NÃO É CONTROLLER!!!
						if(actualNode.getC().getPackageName().equals(p2.getName())){
							packagesToRemove.add(p2); //ADICIONA O PACOTE A SER EXCLUÍDO DA LISTA DE PACOTES CONTROLLERS
							break; //NÃO É NECESSÁRIO VERIFICAR AS DEMAIS CLASSES
						}
						
						//SE A CLASSE ATUAL (PRIMEIRA NA LISTA DE ANCESTRAIS) AINDA NÃO FOI VISITADA 
						if(!visited.contains(actualNode)){
							//PECORRE AS ARESTAS DA CLASSE ATUAL
							for (Iterator<DefaultEdge> i = this.graph.edgesOf(actualNode).iterator(); i.hasNext();) {
								DefaultEdge de = (DefaultEdge) i.next();
								//ADICIONA OS FILHOS DO NÓ ATUAL À LISTA DE ANCESTRAIS
								if(this.graph.getEdgeSource(de).getC().formatFullname().equals(actualNode.getC().formatFullname())){
									if(!front.contains(this.graph.getEdgeTarget(de))){
										front.add(this.graph.getEdgeTarget(de));
									}
								}
							}
							visited.add(actualNode); //MARCA O NÓ ATUAL COMO VISITADO
						}
						
						//SE A LISTA DE BUSCA NÃO ESTÁ VAZIA, O NÓ ATUAL RECEBE O PRÓXIMO ELEMENTO DA LISTA
						actualNode = front.size() > 0 ? front.remove(0) : actualNode; 
					}
				}
			}
			System.out.println("PACOTE CONTROLLER ANALISADO: " + p1.getName() + " POSIÇÃO: " + packageControllers.indexOf(p1));
		}
		System.out.println("FIM CHECAGEM DE REFERENCIA ENTRE PACOTES");
		packageControllers.removeAll(packagesToRemove); //REMOVE OS PACOTES CONTROLLERS FALSOS POSITIVOS
		System.out.println("FIM DEFINIÇÃO DE CLASSES CONTROLLERS");
		return packageControllers;
	}
	
	/**
	 * RECEBE UMA CLASSE E RETORNA O NODE INFO REFERENTE À MESMA
	 * 
	 * @param c
	 * @return
	 */
	private NodeInfo searchNodeInfoFromClass(Class c){
		for (Iterator<NodeInfo> iterator = this.graph.vertexSet().iterator(); iterator.hasNext();) {
			NodeInfo node = (NodeInfo) iterator.next();
			if(node.getC().formatFullname().equals(c.formatFullname())){
				return node;
			}
		}
		return null;
	}
	
	/**
	 * ADICIONA AOS PACOTES CONTROLLERS AS CLASSE QUE NÃO FORAM CONSIDERADAS CONTROLLERS (FALSOS NEGATIVOS) APÓS A FILTRAGEM 
	 *  
	 * @param packages
	 */
	private void completePackages(List<Package> packages){
		for (Package p : packages) {
			for (Iterator<NodeInfo> iterator = this.graph.vertexSet().iterator(); iterator.hasNext();) {
				NodeInfo n = (NodeInfo) iterator.next();
				if(p.getName().equals(n.getC().getPackageName())){
					if(!p.getClasses().contains(n.getC())){
						p.getClasses().add(n.getC());
					}
				}
			}
		}
	}
	
	/**
	 * 
	 * ESTE MÉTODO RETORNA TRUE SE UM PACOTE ESPECÍFICO POSSUI GRAU DE SAÍDA MAIOR QUE A MÉRDIA ENTRE OS PACOTES CONTROLLERS 
	 * E GRAU DE ENTRA MENOR ENTRE OS MESMO PACOTES
	 * 
	 * @param packages
	 * @param _package
	 * @return
	 */
	private boolean checkPackagesReferencesNumberFilter (List<Package> packages, Package _package){
		List<PackageReference> packagesReferences = new ArrayList<PackageReference>();
		int referencesInNumberByPackage = 0;
		int referencesOutNumberByPackage = 0;
		
		double averageInPackagesReferences = 0;
		double averageOutPackagesReferences = 0;
		
		for (Package p : packages) {
			for (Class c : p.getClasses()) {
				referencesInNumberByPackage += searchNodeInfoFromClass(c).getDegreeIN();
				referencesOutNumberByPackage += searchNodeInfoFromClass(c).getDegreeOUT();
			}
			PackageReference pr = new PackageReference(p);
			pr.setReferencesInNumber(referencesInNumberByPackage);
			pr.setReferencesOutNumber(referencesOutNumberByPackage);
			referencesInNumberByPackage = 0;
			referencesOutNumberByPackage = 0;
			packagesReferences.add(pr);
		}
		
		double sumReferencesInNumber = 0;
		double sumReferencesOutNumber = 0;
		
		for (PackageReference packageReference : packagesReferences) {
			sumReferencesInNumber += packageReference.getReferencesInNumber();
			sumReferencesOutNumber += packageReference.getReferencesOutNumber();
		}
		
		averageInPackagesReferences = sumReferencesInNumber/packagesReferences.size();
		averageOutPackagesReferences = sumReferencesOutNumber/packagesReferences.size();
		
		for (PackageReference packageReference : packagesReferences) {
			if(packageReference.getP().equals(_package)){
				if(packageReference.getReferencesInNumber() < averageInPackagesReferences || packageReference.getReferencesOutNumber() > averageOutPackagesReferences ){
					return true;
				}
			}
		}
		return false;
	}
	
	private Package checkPackagesWithAllClassesReferenced(List<Package> completededPackages, List<NodeInfo> canditateControllers){
		int outDegreeNumber = -1;
		boolean contens = false;
		Class classWithBigDegreeOfOutReference = new Class();
		Package p = null;
		
		for (NodeInfo n : canditateControllers) {
			for (Package completedPackage : completededPackages) {
				if(completedPackage.getClasses().contains(n.getC())){
					contens = true;
					break;
				}
			} 
			if(!contens){
				if(n.getDegreeOUT() > outDegreeNumber){
					p = new Package();
					outDegreeNumber = n.getDegreeOUT();
					classWithBigDegreeOfOutReference = n.getC();
				}
			}
			contens = false;
		}
		
		if(p != null){
			p.setName(classWithBigDegreeOfOutReference.getPackageName());
			for (NodeInfo n : canditateControllers) {
				if(n.getC().getPackageName().equals(p.getName())){
					p.getClasses().add(n.getC());
				}
			}
		}
		
		return p;
	}
}

