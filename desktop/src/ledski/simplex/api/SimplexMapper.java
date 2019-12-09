package ledski.simplex.api;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import ledski.util.Gridder;


/** 
 * O propósito dessa classe é simplesmente de receber um String[] contendo as expressões de
 * um problema modelado para simplex, e transformá-lo no sistema linear de equações e no
 * modelo tabular para a resolução do simplex. Isso é feito através de um mapeamento desses
 * Strings, identificando termos, variáveis, coeficientes, funções-objetivos e restrições.
 * <br><br>Essa classe termina sua função aí; Não faz nenhum cálculo de resolução do modelo
 * tabular. Todos os cálculos são delegados para outra classe: SimplexCalc.
 * <h1>Mudanças na versão 0.3:</h1>
 * <ul>
 *     <li>O método <i>buildCabecalho</i> agora retorna um array em vez de lista</li>
 *     <li>O membro de classe <i>xps</i> foi desmembrado em <i>funcaoObjetivo</i>
 *         e <i>restricoes</i> para melhor legibilidade</li>
 *     <li>O membro <i>termos</i> da subclasse <i>XpMapper</i> foi desmembrado nos membros
 *         <i>variaveis</i> e <i>termosIndependente</i> para melhor legibilidade.
 *     <li>Modificações em outros métodos para ajustá-los às alterações citadas acima.
 *     <li>Criada mais um constante na classe <i>XpMapper</i>: <i>NomeTermoIndependente</i> 
 *     <li>A função-objetivo e restrições podem ser escritas com variáveis sem coeficiente
 *         explicito que este será considerado 1</li>
 *     <!--li>A verificacao de variaveis de restricao nao existentes na FO foi retirada. Neste
 *         caso elas serão consideradas 0.</li-->
 *     <li>Nova verificação: as expressoes não podem ter todos os coeficientes como 0. 
 * </ul>
 * <hr>
 * @version 0.3
 * @author Leandro Lino (Ledski)
 */
public class SimplexMapper {
	private XpMapper funcaoObjetivo;
	private XpMapper[] restricoes; // xp significa "expressão"
	public final boolean isMinimizacao;
	private final String regexNumero = "-?[0-9]+(\\.[0-9]+)?";
	private final String regexTermo = "(" + regexNumero + ")?[a-z]";
	private final String regexTermoInicial = "^\\s*[+-]?\\s*" + regexTermo;
	private final String regexTermoOpcional = "(\\s*[+-]\\s*" + regexTermo + ")*";
	private final String regexTermoMaxOuMin = "\\s*=\\s*(max|min)\\s*$";
	private final String regexTermoIndependente = "\\s*(>=|<=)\\s*" + regexNumero + "\\s*$";


	public SimplexMapper( String[] sistema ) throws SimplexSistemaMalformado {
		// validacao
		try {
			validarFO( sistema[0] );
		} catch ( ArrayIndexOutOfBoundsException e ) {
			throw new SimplexSistemaMalformado( "função-objetivo não informada" );
		}
		// criacao
		funcaoObjetivo = new XpMapper( sistema[0], 0 );
		restricoes = new XpMapper[sistema.length-1];
		for( int i=1; i < sistema.length; i++ ) {
			validarRestricao( sistema[i], i );
			restricoes[i-1] = new XpMapper( sistema[i], i );
		}
		// verificacao
		assertNumeroDeRestricoes();
		assertImpossivelComBigM();
		assertTodosOsCoeficienteSaoZeros();
		assertVariaveisNaoExistentes();
		this.isMinimizacao = sistema[0].contains( "min" );
	}


	/** Verifica através de um RegEx se a expressao é uma função-objetivo válida.
	 * @param xp String Expressao a ser avaliada
	 */
	private void validarFO( String fo ) throws SimplexSistemaMalformado {
		if( !fo.matches( regexTermoInicial + regexTermoOpcional + regexTermoMaxOuMin ) ) {
			throw new SimplexSistemaMalformado( "Função-objetivo - O formato da expressão está incorreto." );
		}
	}
	

	/** Verifica através de um RegEx se a expressao é uma restrição válida.
	 * @param xp String Expressao a ser avaliada
	 * @param indice Integer Índice da restrição no sistema
	 */
	private void validarRestricao( String xp, Integer indice ) throws SimplexSistemaMalformado {
		if( !xp.matches( regexTermoInicial + regexTermoOpcional + regexTermoIndependente ) ) {
			throw new SimplexSistemaMalformado( "Restricao " + indice + " - O formato da expressão está incorreto." );
		}
	}


	/** Retorna uma lista com todas as variáveis do sistema, incluindo as auxiliares
	 *  na ordem certa para o cabeçalho da tabela inicial do simplex.
	 */
	public String[] buildCabecalho() {
		// cria o novo array
		int tamanho = funcaoObjetivo.variaveis.size() + restricoes.length + 1; // +1 do termo independente
		String[] cabecalho = new String[tamanho];
		// asdiciona variaveis da funcao objetivo
		Iterator<String> it = funcaoObjetivo.variaveis.keySet().iterator();
		int index = 0;
		while( it.hasNext() ) cabecalho[ index++ ] = it.next();
		// adiciona as variaveis auxiliares
		for( int i=1; i<=restricoes.length; i++ ) cabecalho[ index++ ] = XpMapper.NomeVarAuxiliar+i;
		// adiciona a variavel independente no final do array
		cabecalho[ index++ ] = XpMapper.NomeTermoIndependente;
		return cabecalho;
	}


	/** Verifica se todas as variáveis das restrições estão presentes na função-objetivo.
	 *  Emite uma SimplexSistemaMalformado ao encontrar discrepâncias.
	 */
	private void assertVariaveisNaoExistentes() throws SimplexSistemaMalformado {
		Set<String> variaveisFO = funcaoObjetivo.getVariaveis();
		for( int i=0; i<restricoes.length; i++ ) {
			Set<String> keySet = restricoes[i].variaveis.keySet();
			for( String key : keySet ) {
				if( !variaveisFO.contains( key ) ) {
					throw new SimplexSistemaMalformado( "Restrição "+i+" - A variável "+key+" não está presente na função-objetivo." );
				}
			}
		}
	}


	private void assertNumeroDeRestricoes() throws SimplexSistemaMalformado {
		if( restricoes.length < funcaoObjetivo.variaveis.size() ) {
			throw new SimplexSistemaMalformado(
				"O sistema deve conter um número de restrições igual "
				+ "ou maior ao número de variáveis na função-objetivo."
			);
		}
	}
	
	
	private void assertTodosOsCoeficienteSaoZeros() throws SimplexSistemaMalformado {
		for( int i=0; i<restricoes.length+1; i++ ) {
			Stream<Double> valueSet = i==0
				? funcaoObjetivo.variaveis.values().stream()
				: restricoes[i-1].variaveis.values().stream();
			if( valueSet.allMatch( x -> x==0 ) ) {
				String nomeXp = i==0 ? "Função-objetivo" : ( "Restrição " + (i-1) );
				throw new SimplexSistemaMalformado( nomeXp + " - Todos os coeficientes são zeros." );
			}
		}
	}
	
	
	private void assertImpossivelComBigM() throws SimplexSistemaMalformado {
		for( int r=0; r<restricoes.length; r++ ) {
			XpMapper restricao = restricoes[r];
			if( funcaoObjetivo.tipo == TipoXP.max && restricao.tipo == TipoXP.maiorIgual && restricao.termoIndependente > 0 ) {
				throw new SimplexSistemaMalformado( "Impossível resolver pelo método padrão.\nO problema é de " +
					"maximização porém a restrição " + (r+1) + " é do tipo >= e tem termo independente positivo." );
			}
			else if( funcaoObjetivo.tipo == TipoXP.min && restricao.tipo == TipoXP.menorIgual && restricao.termoIndependente < 0 ) {
				throw new SimplexSistemaMalformado( "Impossível resolver pelo método padrão.\nO problema é de " +
					"minimização porém a restrição " + (r+1) + " é do tipo <= e tem termo independente negativo." );
			}
		}
	}


	/** Constroi a tabela para o simplex
	 */
	public double[][] buildModeloTabular() {
		String[] cabecalho = buildCabecalho();
		// A tabela tem numero de linhas igual a quantidade de restricoes + 1 da funcao-objetivo
		// e numero de colunas igual ao tamanho do vetor cabecalho
		double[][] tabela = new double[restricoes.length + 1][cabecalho.length];
		for( int i=0; i < restricoes.length + 1; i++ ) {
			XpMapper linha = i==0 ? funcaoObjetivo : restricoes[i-1];
			for( int v=0; v < cabecalho.length; v++ ) {
				String variavel = cabecalho[ v ];
				if( variavel.equals( XpMapper.NomeTermoIndependente ) ) {
					tabela[i][v] = linha.getTermoIndependenteAjustado();
				} else {
					tabela[i][v] = linha.getCoeficienteAjustado(variavel);
				}
			}
		}
		return tabela;
	}


	/** Retorna uma representação em String da tabela inicial
	*/
	public String getModeluTabularEmString() {
		Gridder gr = new Gridder( -4, 3, '>', '>', 2, false, false, false );
		double[][] tabela = buildModeloTabular();
		String[] cabecalho = buildCabecalho();
		// Cabecalho
		gr.text( "" ); // primeira coluna do cabeçalho nao tem nada
		for( String head : cabecalho ) gr.text( head );
		gr.newLine();
		// Corpo
		for( int row=0; row<tabela.length; row++ ) {
			gr.text( row==0 ? "fo:" : ( "r" + row + ":" ) );
			for( int col=0; col<cabecalho.length; col++ ) {
				gr.text( tabela[row][col] );
			}
			gr.newLine();
		}
		return gr.publish();
	}


	/** Retorna a representação desse mapa em String. Essa representação na verdade é
	 *  o modelo do problema transformado em sistema linear. É o único método da classe
	 *  a retornar uma representação em String. Esse método retorna exatamente o método
	 *  toString da classe.
	*/
	public String buildSistemaLinear() { return toString(); }


	public String toString() {
		Set<String> vars = restricoes[0].getVariaveis();
		Gridder grid = new Gridder( -4, 2, '<', '>', 2, false, false, false );
		grid.append( funcaoObjetivo.toEquacaoGrid( vars ) );
		for( XpMapper mapa : restricoes ) {
			grid.append( mapa.toEquacaoGrid( vars ) );
		}
		return grid.reset();
	}



	
	
	//====================================================================================================
	//====================================================================================================
	// CLASSE: MapaDeXp
	//====================================================================================================
	//====================================================================================================

	/** Recebe um String contendo uma expressao, e mapeia os termos, variaveis e coeficientes
	 * bem como o tipo de expressão, facilitando a consulta desses parâmetros da expressao.
	 */
	private class XpMapper {
		TipoXP tipo;
		Integer indice;
		Map<String, Double> variaveis = new LinkedHashMap<String, Double>();
		double termoIndependente;
		// LinkedHashMap para preservar a ordem em que os termos são inseridos, para posteriormente recuperar
		// o keySet() na ordem certa.
		public static final String NomeVarAuxiliar = "@";
		public static final String NomeTermoIndependente = "ind";


		/** Construtor do objeto.
		 * @param xp String que representa a expressão.
		 * @param indice O índice da expressão dentro do sistema
		 */
		public XpMapper( String xp, int indice) throws SimplexSistemaMalformado {
			this.tipo = definirTipo(xp);
			this.indice = indice;
			Pattern regex = Pattern.compile( "\\s*([+-]?)\\s*([0-9]+(\\.[0-9]+)?)?(?<![a-z])([a-z])(?![a-z])" );
			Matcher termoMatcher = regex.matcher( xp );
			while( termoMatcher.find() ) {
				String g1 = termoMatcher.group(1) == null ? "+" : termoMatcher.group(1);
				String g2 = termoMatcher.group(2) == null ? "1" : termoMatcher.group(2);
				Double coeficiente = Double.parseDouble( g1 + g2 );
				String variavel = termoMatcher.group(4);
				if( variaveis.containsKey( variavel )) {
					String nomeDaLinha = indice == 0
						? "Função-objetivo: "
						: ( "Restrição " + indice );
					throw new SimplexSistemaMalformado( nomeDaLinha + ": A variável " + variavel + " está presente em mais de 1 termo. " );
				}
				variaveis.put( variavel, coeficiente );
			}
			termoIndependente = definirTermoIndependente( xp );
		}


		/** Analisa a string de uma expressao e retorna seu tipo
		 */
		private TipoXP definirTipo( String xp ) {
			if(		xp.contains("<=") )  return TipoXP.menorIgual;
			else if( xp.contains(">=") )  return TipoXP.maiorIgual;
			else if( xp.contains("max") ) return TipoXP.max;
			else if( xp.contains("min") ) return TipoXP.min;
			else return null;
		}


		/** Procura um termo independente na equação passada, e o retorna como um Double.
		 *  Caso não haja um termo independente válido (numérico), retorna 0 como termo independente.
		 *  É o caso, por exemplo, da função-objetivo, em que o termo independente será 0.
		 *  @param xp - Uma String contendo uma expressão (equação/inequação)
		 */
		private Double definirTermoIndependente( String xp ) {
			String[] linhaSplit = xp.split( "=|<=|>=" );
			return linhaSplit.length == 2 && linhaSplit[1].matches( "\\s*" + regexNumero + "+\\s*" )
				? Double.parseDouble( linhaSplit[1] )
				: 0.0;
		}


		/** Retorna as variáveis dos termos da expressão. Não inclui a variavel de folga
		*/
		public Set<String> getVariaveis() {
			return variaveis.keySet();
		}


		/** Retorna o coeficiente da variavel indicada, já aplicadas todas as transformações necessarias
		 *  de acordo com o tipo e indice da expressao
		 */
		public Double getCoeficienteAjustado( String variavel ) {
			Double coeficiente;
			if( variaveis.containsKey( variavel ) && variaveis.get( variavel ) != 0.0 )
				coeficiente = variaveis.get( variavel ) * ( tem( tipo, TipoXP.maiorIgual, TipoXP.max ) ? -1 : 1 );
			else
				coeficiente = variavel.equals(XpMapper.NomeVarAuxiliar + indice) ? 1.0 : 0.0;
			return coeficiente;
		}
		
		
		public double getTermoIndependenteAjustado() {
			return termoIndependente * ( tipo == TipoXP.maiorIgual ? -1 : 1 );
		}


		/** Retorna um string da expressão transformada em equação para simplex.
		 *  Inclui a variável auxiliar, e o tipo da expressao (função-objetivo ou restrição).
		 *  As variáveis aparecem na ordem em que foram inseridas na expressão.
		 */
		public String toString() {
			return toEquacaoString( getVariaveis() );
		}


		/** Retorna um string da expressão transformada em equação para simplex. As variáveis
		 *  aparecem na ordem em que aparecem na Set passada. Útil ao pegar a equação de
		 *  todas as expressões do sistema para que as variáveis fiquem na mesma ordem.
		 */
		public String toEquacaoString( Set<String> ordemVars ) {
			Gridder g = toEquacaoGrid( ordemVars );
			return g.publish();
		}


		/** Retorna um Gridder contendo a expressão transformada em equação para simplex. As variáveis
		 *  aparecem na ordem em que aparecem na Set passada. Método feito para ser chamado pela
		 *  classe {@link MapaDeSistema} ao montar o Gridder do sistema completo do simplex contendo
		 *  todas as equações ajustadas.
		 * @param ordemVars
		 * @return {@link Gridder}
		 */
		public Gridder toEquacaoGrid( Set<String> ordemVars ) {
			Gridder gr = new Gridder( 0, 2, '<', '>', 2, false, false, false);
			// coluna linha
			String indiceStr = indice == null ? "?" : indice.toString();
			Boolean isFO = tem( tipo, TipoXP.max, TipoXP.min );
			String head = isFO ? "fo" : ( "r" + indiceStr );
			gr.text( head + ":" );
			// coluna variavel extra
			if( tipo == TipoXP.max ) gr.text( "max" ); 
			else if( tipo == TipoXP.min ) gr.text( "-max" );
			else gr.text( XpMapper.NomeVarAuxiliar + indice );		
			// colunas termos
			for( String variavel : ordemVars ) {
				gr.math( getCoeficienteAjustado( variavel ) + variavel );
			}
			gr.math( "=" + getTermoIndependenteAjustado() );
			return gr;
		}
	}





	//====================================================================================================
	//====================================================================================================
	// ENUM: TipoDeXp
	//====================================================================================================
	//====================================================================================================

	private enum TipoXP {
		max, min, maiorIgual, menorIgual
	}





	//====================================================================================================
	//====================================================================================================
	// CLASSE: Exception SimplexSistemaMalformado 
	//====================================================================================================
	//====================================================================================================

	@SuppressWarnings("serial")
	public class SimplexSistemaMalformado extends Exception {
		public SimplexSistemaMalformado( String message ) { super(message); }
	}





	//====================================================================================================
	//====================================================================================================
	// MÉTODOS UTILITARIOS
	//====================================================================================================
	//====================================================================================================

	@SafeVarargs
	final private <T> boolean tem(T agulha, T ...palheiro ) {
		for( T item : palheiro )
			if( agulha.equals( item ) )
				return true;
		return false;
	}
	
	
}


