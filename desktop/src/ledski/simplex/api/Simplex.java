package ledski.simplex.api;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe cuja função é calcular um problem utilizando Simplex. O problema é informado
 * no formato de um String[] (vetor de strings) representando um problema modelado.
 * A classe SimplesMapper é inicialmente chamada para transformar o String[] em um
 * objeto que fornece a esta classe o modelo tabular (em formato Double[][]) necessário para
 * relização dos cálculos. Só então é que os métodos desta classe começam a calcular o simplex.
 * Ao final do cálculo a classe SimplexViewer é utilizada para transformar o resultado em uma
 * String que pode ser lida e entendida.
 * 
 * @author Leandro Lino (Ledski)
 * @version 0.2
 */
public abstract class Simplex {
	
	/**
	 * Único método público da classe. É o método que recebe o modelo, executa os cálculos e
	 * retorna o resultado
	 * @param modeloDoProblema Um vetor de String com a modelagem do problema, onde o primeiro
	 * 			String é a função-objetivo e as demais são as restrições.
	 * @param isMostrarIteracoes Boolean representando se o String retornado deve incluir os
	 * 			cálculos das iteraçÕes ou somente o resultado.
	 * @return Um String explicando a resolução e resultado.
	 */
	public static String executar( String[] modeloDoProblema, boolean isMostrarIteracoes ) {
		try {
			SimplexMapper mapa = new SimplexMapper( modeloDoProblema );
			double[][] tabela = mapa.buildModeloTabular();
			String[] cabecalho = mapa.buildCabecalho();
			String sistemaLinear = mapa.buildSistemaLinear();
			boolean isMinimizacao = mapa.isMinimizacao;
			List<double[][]> historicoTabela = new ArrayList<double[][]>();
			List<int[]> historicoPivo = new ArrayList<>();
			// Tabela inicial
			historicoTabela.add( tabela );
			// Tipo de resultado
			boolean sistemaResolvido = false;
			boolean sistemaIndeterminado = false;
			// Loop de iteracoes
			int iteracaoNum = 0;
			while( !sistemaResolvido && !sistemaIndeterminado ) {
				if( iteracaoNum++ > 4 ) break;
				int[] pivo = calcPivo( tabela, isMinimizacao );
				tabela = calcNovaTabela( tabela, pivo );
				historicoPivo.add( pivo );
				historicoTabela.add( tabela );
				sistemaResolvido = isResolvido( tabela, isMinimizacao );
				sistemaIndeterminado = isIndeterminado( tabela, historicoTabela );
			}
			// String de resultado
			return SimplexViewer.toString( sistemaLinear, isMinimizacao, cabecalho,
					historicoTabela, historicoPivo, sistemaIndeterminado, isMostrarIteracoes );
		}
		catch (SimplexMapper.SimplexSistemaMalformado e) {
			return "Erro:\n"+ e.getMessage();
		}
	}
	
	
	
	
	
	/**
	 * Calcula e retorna um array de 2 elementos com as cordenadas do pivô na tabela.
	 * @param tabela A tabela
	 * @param isMin Boolean indicando se o simplex é de minimização (true) ou maximização (false)
	 */
	private static int[] calcPivo ( double[][] tabela, boolean isMin ) {
		int linhaPivo = Integer.MAX_VALUE; // índice da linha do pivô
		int colunaPivo = Integer.MAX_VALUE; // índice da coluna do pivô
		double[] vetorFO = extrairVetor( 'l', 0, tabela ); // vetor da linha FO
		double[] vetorInd = extrairVetor( 'c', tabela[0].length-1, tabela ); // vetor da coluna Ind
		if( isMin ) {
			linhaPivo = indiceDoMenorValor( vetorInd, 0 );
			double[] vetorPivo = extrairVetor( 'l', linhaPivo, tabela );
			colunaPivo = indiceDoMaiorQuocienteNegativo( vetorFO, vetorPivo );
		} else {
			colunaPivo = indiceDoMenorValor( vetorFO, vetorFO.length-1 );
			double[] vetorPivo = extrairVetor( 'c', colunaPivo, tabela );
			linhaPivo = indiceDoMenorQuocientePositivo( vetorInd, vetorPivo );
		}
		return new int[] { linhaPivo, colunaPivo };
	}
	
	
	
	
	
	/**
	 * Extrai um vetor de uma tabela, na horizontal (linha) ou vertical (coluna).
	 * @param colunaOuLinha Carectere 'c' para coluna ou 'l' para linha
	 * @param indiceDoVetor Indice do vetor na matriz
	 * @param matriz Matriz contendo o vetor a ser extraído
	 */
	private static double[] extrairVetor( char colunaOuLinha, int indiceDoVetor, double[][] matriz ) {
		double[] vetor = new double[ colunaOuLinha == 'c' ? matriz.length : matriz[0].length ];
		for( int i = 0; i < ( colunaOuLinha == 'c' ? matriz.length : matriz[0].length ); i++ ) {
			vetor[ i ] = colunaOuLinha == 'c' ? matriz[ i ][ indiceDoVetor ] : matriz[ indiceDoVetor ][ i ];
		}
		return vetor;
	}
	
	
	
	
	
	/**
	 * Retorna o índice do elemento de menor valor no vetor.
	 * Ignora o elemento com o índice indicado no parâmetro 
	 * @param vetor O vetor para analizar
	 * @param excluirIndice Indice do elemento a não considerar
	 */
	private static int indiceDoMenorValor( double[] vetor, int excluirIndice ) {
		double menorValor = Double.POSITIVE_INFINITY;
		int indice = Integer.MAX_VALUE;
		for( int i = 0; i < vetor.length; i++ ) {
			if( i != excluirIndice && vetor[ i ] < menorValor ) {
				indice = i;
				menorValor = vetor[ i ];
			}
		}
		return indice;
	}
	
	
	
	
	
//	/**
//	 * Realiza a divisão dos elementos correspondentes de 2 vetores do mesmo tamanho
//	 * e retorna o índice dos elementos cujo valor absoluto do quociente foi o menor dentre todos.
//	 * Não considera o quociente onde o dividendo ou o divisor é 0. 
//	 * @param vetorDividendo
//	 * @param vetorDivisor
//	 */
//	private static int indiceDoQuocienteComMenorValorAbsoluto( double[] vetorDividendo, double[] vetorDivisor ) {
//		double menorQuociente = Double.POSITIVE_INFINITY;
//		int indice = Integer.MAX_VALUE;
//		for( int i = 0; i < vetorDividendo.length; i++ ) {
//			if( vetorDividendo[i] != 0 && vetorDivisor[i] != 0 ) {
//				double quociente = Math.abs( vetorDividendo[i] / vetorDivisor[i] );
//				if( quociente < menorQuociente) {
//					indice = i;
//					menorQuociente = quociente;
//				}
//			}
//		}
//		return indice;
//	}
	
	
	
	
	
	/**
	 * Realiza a divisão dos elementos correspondentes de 2 vetores do mesmo tamanho
	 * e retorna o índice dos elementos cujo valor do quociente foi o menor positivo dentre todos.
	 * Não considera o quociente onde o dividendo ou o divisor é 0. 
	 * @param vetorDividendo
	 * @param vetorDivisor
	 */
	private static int indiceDoMenorQuocientePositivo( double[] vetorDividendo, double[] vetorDivisor ) {
		double menorQuociente = Double.POSITIVE_INFINITY;
		int indice = Integer.MAX_VALUE;
		for( int i = 0; i < vetorDividendo.length; i++ ) {
			if( vetorDividendo[i] != 0 && vetorDivisor[i] != 0 ) {
				double quociente = vetorDividendo[i] / vetorDivisor[i];
				if( quociente < menorQuociente && quociente > 0 ) {
					indice = i;
					menorQuociente = quociente;
				}
			}
		}
		return indice;
	}
	
	
	
	
	
	/**
	 * Realiza a divisão dos elementos correspondentes de 2 vetores do mesmo tamanho
	 * e retorna o índice dos elementos cujo valor do quociente foi o maior negativo dentre todos.
	 * Não considera o quociente onde o dividendo ou o divisor é 0. 
	 * @param vetorDividendo
	 * @param vetorDivisor
	 */
	private static int indiceDoMaiorQuocienteNegativo( double[] vetorDividendo, double[] vetorDivisor ) {
		double maiorQuociente = Double.NEGATIVE_INFINITY;
		int indice = Integer.MAX_VALUE;
		for( int i = 0; i < vetorDividendo.length; i++ ) {
			if( vetorDividendo[i] != 0 && vetorDivisor[i] != 0 ) {
				double quociente = vetorDividendo[i] / vetorDivisor[i];
				if( quociente > maiorQuociente && quociente < 0 ) {
					indice = i;
					maiorQuociente = quociente;
				}
			}
		}
		return indice;
	}





	/**
	 * Calcula a nova tabela, a partir do pivo passado.
	 * Unitariza a linha do pivê e depois recalcula as outras linhas utilizando método de Gauss.
	 * @param tabela A tabela para cálculo da nova tabela
	 * @param pivo Array de 2 elementos com as coordenadas do pivo na tabela
	 */
	private static double[][] calcNovaTabela ( double[][] tabela, int[] pivo ) {
		int linhas = tabela.length;
		int colunas = tabela[0].length;
		int linhaPivo = pivo[0];
		int colunaPivo = pivo[1];
		double pivoValor = tabela[ linhaPivo ][ colunaPivo ];
		// criar nova tabela
		double[][] novaTabela = new double[ linhas ][ colunas ];
		// unitarizar a linha do pivo
		for( int c = 0; c < colunas; c++ ) { 
			novaTabela[ linhaPivo ][ c ] = tabela[ linhaPivo ][ c ] / pivoValor;
		}
		// calcular novas linhas com método de Gauss
		for( int l = 0; l < linhas; l++ ) if( l != linhaPivo ) {
			// achar o multiplicador
			double multiplicador = tabela[ l ][ colunaPivo ] * -1;
			// preencher a nova linha
			for( int c = 0; c < colunas; c++ ) {
				novaTabela[ l ][ c ] = tabela[ l ][ c ] + multiplicador * novaTabela[ linhaPivo ][ c ];
			}
		}
		return novaTabela;
	}
	
	
	
	
	
	/**
	 * Verifica se o cálculo do simplex chegou ao fim. Isso é feito verificando se
	 * existe algum valor negativo na coluna Ind se o simplex for de minimização
	 * e na linha FO se for de maximização.
	 * @param tabela A tabela a ser verificada
	 * @param isMin Boolean indicando simplex de minimização
	 */
	private static boolean isResolvido( double[][] tabela, boolean isMin ) {
		boolean fim = true;
		int linhaFO = 0;
		int colunaInd = tabela[0].length - 1;
		// se for minimização, fim=true se não existe valores negativos
		// na coluna independente, excluindo-se a linha FO
		if( isMin ) for( int l = 1; l < tabela.length; l++ ) {
			if( tabela[ l ][ colunaInd ] < 0 ) fim = false;
		}
		// se for maximização, fim=true se não existe valores negativos
		// na linha da FO, excluindo-se a coluna independente
		else for( int c = 0; c < tabela[0].length-1; c++ ) {
			if( tabela[ linhaFO ][ c ] < 0 ) fim = false;
		}
		return fim;
	}
	
	
	
	
	
	/** Verifica se o sistema é indeterminado.
	 * Compara o valor da coluna Ind na linha FO da ultima tabela com os das tabelas anteriores.
	 * Caso um valor igual seja encontrado, significa que os valores da tabela nas iterações vão
	 * se repetindo, o que significa que não é possível calcular o sistema.
	 * @param ultimaTabela
	 * @param tabelaHist
	 */
	private static boolean isIndeterminado( double[][] ultimaTabela, List<double[][]> tabelaHist ) {
		if( tabelaHist.size() < 2 ) return false;
		boolean achouTabelaIgual = false;
		double ultimoValorIndFO = ultimaTabela[ 0 ][ ultimaTabela[0].length-1 ];
		for( int i = tabelaHist.size()-2; i >= 0; i-- ) {
			double[][] tabela = tabelaHist.get( i );
			double valorIndFO = tabela[ 0 ][ tabela[0].length-1 ];
			if( valorIndFO == ultimoValorIndFO ) {
				achouTabelaIgual = true;
				break;
			}
		}
		return achouTabelaIgual;
	}

}
