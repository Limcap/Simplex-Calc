package ledski.simplex.api;

import java.util.List;

import ledski.util.Gridder;


/**
 * @author Leandro Lino (Ledski)
 * @version 0.1
 */
public abstract class SimplexViewer {
	
	
	private static String DIVISOR = "-------------------------------------------------------------------------------------------------------";
	
	
	public static String toString( String sistemaLinear, boolean isMinimizacao, String[] cabecalho,
			List<double[][]> historicoTabela, List<int[]> historicoPivo, boolean indeterminado, boolean isMostrarIteracoes ) {
		StringBuilder sb = new StringBuilder();
		sb.append( "SISTEMA LINEAR\n\n" );
		sb.append( sistemaLinear );
		if( isMostrarIteracoes ) sb.append( exibirIteracoes( isMinimizacao, cabecalho, historicoTabela, historicoPivo ) );
		sb.append( DIVISOR + "\nSOLUÇÃO\n" );
		double[][] ultimaTabela =  historicoTabela.get( historicoTabela.size()-1 );
		sb.append( exibirSolucao( ultimaTabela, cabecalho, isMinimizacao, indeterminado ) );
		return sb.toString();
	}
	
	
	
	
	
	/**
	 * Retorna a representação textual das iterações
	 */
	private static String exibirIteracoes( boolean isMinimizacao, String[] cabecalho,
			List<double[][]> historicoTabela, List<int[]> historicoPivo ) {
		StringBuilder sb = new StringBuilder();
		int iteracaoNum = 0;
		int[] pivo;
		double[][] tabela = historicoTabela.get( 0 );
		double[][] tabelaAnterior;
		sb.append( DIVISOR + "\nMODELO TABULAR\n\n" );
		sb.append( exibirTabela( tabela, cabecalho, 0 ) );
		for( int i = 1; i < historicoTabela.size(); i++ ) {
			iteracaoNum++;
			pivo = historicoPivo.get( iteracaoNum-1 );
			tabela = historicoTabela.get( iteracaoNum );
			tabelaAnterior = historicoTabela.get( iteracaoNum-1 );
			sb.append( DIVISOR + "\nITERAÇÃO " + iteracaoNum + "\n\n" );
//			sb.append( "===== ITERAÇÃO " + iteracaoNum + " ==========\n" );
			sb.append( "PIVÔ:\n" + exibirPivo( tabelaAnterior, pivo, cabecalho, iteracaoNum-1 ) + "\n" );
			sb.append( "\nNOVAS LINHAS:" + exibirCalculoDeNovasLinhas( tabelaAnterior, pivo, iteracaoNum ) );
			sb.append( "\nNOVA TABELA:\n" + exibirTabela( tabela, cabecalho, iteracaoNum ) );
		}
		return sb.toString();
	}
	
	
	
	
	
//	private static String exibirIteracao( int iteracaoNum, List<String> cabecalho, double[][] tabela, int[] pivoXY, double[][] tabelaNova, boolean isMinimizacao ) {
//		StringBuilder sb = new StringBuilder();
//		sb.append( "===== ITERAÇÃO " + iteracaoNum + " ==========\n" );
//		sb.append( "\nPIVÔ:\n" + exibirPivo( tabela, pivoXY, cabecalho ) + "\n" );
//		sb.append( "\nNOVAS LINHAS:" + exibirCalculoDeNovasLinhas( tabela, pivoXY, iteracaoNum ) );
//		sb.append( "\nNOVA TABELA:\n" + exibirTabela( tabelaNova, cabecalho, iteracaoNum ) );
//		return sb.toString();
//	}
	
	
	
	
	
	/** Retorna uma representação em String da tabela
	*/
	public static String exibirTabela( double[][] tabela, String[] cabecalho, int iteracaoNum  ) {
		Gridder gr = new Gridder( -4, 3, '>', '>', 2, false, false, false );
		// Cabecalho
		gr.text( "" ); // primeira coluna do cabeçalho nao tem nada
		for( String head : cabecalho ) gr.text( head );
		gr.newLine();
		// Corpo
		for( int row=0; row < tabela.length; row++ ) {
			gr.text( row==0 ? "fo'" + iteracaoNum + ":" : ( "r" + row + "'" + iteracaoNum + ":" ) );
			for( int col=0; col < cabecalho.length; col++ ) {
				gr.text( tabela[row][col] );
			}
			gr.newLine();
		}
		return gr.publish();
	}
	
	
	
	
	
	private static String exibirPivo( double[][] tabela, int[] pivo, String[] cabecalho, int iteracaoNum ) {
		Gridder gr = new Gridder();
		gr.text( "Linha = " + nomeDaLinha( pivo[0] ) + "'" + iteracaoNum );
		gr.text( ", Coluna = " + cabecalho[ pivo[1] ] );
		gr.text( ", Valor = " + tabela[ pivo[ 0 ] ][pivo[ 1 ] ] );
		return gr.publish();
	}
	
	
	
	
	
	/**
	 * Retorna um string com o cálculo das linhas da tabela de iteração numero 'iteracaoNum'
	 */
	private static String exibirCalculoDeNovasLinhas( double[][] tabela, int[] pivo, int iteracaoNum ) {
		int linhas = tabela.length;
		int linhaPivo = pivo[0];
		int colunaPivo = pivo[1];
		double pivoValor = tabela[ linhaPivo ][ colunaPivo ];
		
		StringBuilder sb = new StringBuilder();
		Gridder gr = new Gridder( 0, 2, '<', '>', 2, false, false, false );
		for( int l = 0; l < linhas; l++ ) {
			Double multiplicador = tabela[ l ][ colunaPivo ] * -1;
			gr.newLine().text( nomeDaLinha( l ) + "'" + iteracaoNum  + ":    " );
			if( l == linhaPivo ) {
				gr.math( nomeDaLinha( l ) + "'" + ( iteracaoNum - 1 ) );
				gr.math( "/" + pivoValor );
			} else {
				gr.math( nomeDaLinha( l ) + "'" + ( iteracaoNum - 1 ) );
				gr.math( multiplicador );
				gr.math( "*" + nomeDaLinha( linhaPivo ) + "'" + iteracaoNum );
			}
		}
		sb.append( gr.reset() );
		return sb.toString();
	}
	
	
	
	
	
	/**
	 * Retorna um string com a solução do problema.
	 */
	private static String exibirSolucao( double[][] tabela, String[] cabecalho, boolean isMinimizacao, boolean indeterminado ) {
		int colunas = tabela[0].length;
		int linhaFO = 0;
		int colunaInd = colunas-1;
		double valorResultado = tabela[ linhaFO ][ colunaInd ];
		Gridder gr = new Gridder(0,2,'<','>',2,false,false,false);
		if( indeterminado ) {
			gr.textLine( "O sistema é indeterminado. As iterações irão se repetir indefinidamente." );
		}
		else {
			gr.textLine( isMinimizacao ? "Mínimo" : "Máximo" );
			gr.math( "=" + valorResultado * (isMinimizacao ? -1 : 1 ) );
//			gr.textLine( isMinimizacao
//				? "Mínimo\t= " + valorResultado * -1
//				: "Máximo\t= " + valorResultado 
//			);
			for( int i=0; i<cabecalho.length-1; i++ ) { //length-1 para nao fazer para a coluna do termo independente
				gr.textLine( cabecalho[ i ] );
				gr.math( "=" + getValorFinalDaVariavel( tabela, i ) );
			}
		}
		return gr.publish();
	}
	
	
	
	
	
	/**
	 * Verifica se a coluna representa uma variável básica. Se sim retorna seu valor, se não, retorna 0.
	 * Retorna null se for a ultima coluna, já que esta representa o termo independente e não uma variável.
	 * @param tabela A tabela
	 * @param indexColuna A coluna a ser verificada
	 */
	private static Double getValorFinalDaVariavel( double[][] tabela, int indexColuna ) {
		if( indexColuna == tabela[0].length-1 ) return null;
		int linhasCom1 = 0;
		int linhasCom0 = 0;
		double valorInd = 0;
		for( int l = 0; l < tabela.length; l++ ) {
			if( tabela[ l ][ indexColuna ] == 1 ) {
				linhasCom1++;
				valorInd = tabela[ l ][ tabela[0].length-1 ]; // valor da coluna Ind
			} else if( tabela[ l ][ indexColuna ] == 0 ) {
				linhasCom0++;
			}
		}
		return linhasCom1 + linhasCom0 == tabela.length ? valorInd : 0;
	}
	
	
	
	
	
	private static String nomeDaLinha( int linhaNum ) {
		return linhaNum == 0 ? "fo" : ("r" + linhaNum);
	}

}
