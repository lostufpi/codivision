package codivision.fuzzy;

import java.io.InputStream;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.rule.Rule;

public class TesteJava {

	public static void main(String[] args) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream input = classLoader.getResourceAsStream("criticidade.fcl");

		FIS fis = FIS.load(input, true);

		if(fis == null){
			System.err.println("Não foi possivel carregar o arquivo!!");
			return;
		}

		double v2 = (double) 10/14;
		fis.setVariable("ACOPLAMENTO", v2 );

		double val = (double) 4/7;
		fis.setVariable("DIVIDA", val);
		

		fis.evaluate();
		for( Rule r : fis.getFunctionBlock("criticidade").getFuzzyRuleBlock("ConjuntoRegras").getRules() )
			System.out.println(r);

		double valor = fis.getVariable("GC").getValue();

		System.out.println("GC = "+valor);
		System.out.println();

	}

}
