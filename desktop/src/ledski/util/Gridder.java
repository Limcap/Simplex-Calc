package ledski.util;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**<h1>Versao 5<br>To-do list:</h1>
 * <ol>
 * <li>Bug: a largura entre celulas tipo <i>text</i> e <i>math</i> não bate.</li>
 * <li>Melhoria: String e StringBuilder: Como há muita manipulação de String, foram trocados os objetos
 *     String por StringBuilder onde possível para que o heap não seja poluído com muito garbage.</li>
 * </ol> 
 * @author Leandro Lino (Ledski)
 * @version 0.5
 */
public class Gridder {
	
	// valores padrão caso seja instanciado sem parâmetro
	private int width = 0;
	private int spacing = 1;
	private char alignText = '<';
	private char alignMath = '>';
	private boolean signedPositive = false;
	private int decimalPlaces = 2;
	private boolean trailingZeroes = false;
	private boolean onlyDecimalNumbers = false;
	private List<List<String>> grid = new ArrayList<List<String>>();
	private List<Integer> header = new ArrayList<Integer>();
	private int col = 0;
	private int row = 0;


	public Gridder () {
		grid.add( new ArrayList<String>() );
	}
	public Gridder(	int width, int spacing, char alignText, char alignMath, int decimalPlaces,
							boolean trailingZeroes, boolean signedPositive, boolean onlyDecimalNumbers ) {
		this();
		this.width = width;
		this.alignText = alignText;
		this.alignMath = alignMath;
		this.signedPositive = signedPositive;
		this.decimalPlaces = decimalPlaces;
		this.trailingZeroes = trailingZeroes;
		this.onlyDecimalNumbers = onlyDecimalNumbers;
		this.spacing = spacing;
	}


	public Gridder newLine( int lines ) {
		for( int i = 0; i < lines; i++ ) newLine();
		return this;
	}
	public Gridder newLine() {
		row++;
		col = 0;
		grid.add( new ArrayList<String>() );
		return this;
	}
	
	
	@SafeVarargs
	public final <T> Gridder textLine( T ...objs ) {
		newLine().text( objs );
		return this;
	}
	

	@SafeVarargs
	public final <T> Gridder text( T ...objs ) {
		for( T obj : objs ) cell( obj.toString(), false );
		return this;
	}

	@SafeVarargs
	public final <T> Gridder math( T ...objs ) {
		for( T obj : objs ) cell( obj.toString(), true );
		return this;
	}


	private final Gridder cell( String str, boolean isMath ) {
		str = fixOnInsert1_lineBreaks( str );
		str = fixOnInsert2_number( str );
		if( isMath ) str = fixOnInsert3_mathCell( str );
		grid.get( row ).add( str );
		setColSize( str );
		col++;
		return this;
	}


	public String reset() {
		String content = publish();
		header.clear();
		return content;
	}


	public String publish() {
		String content = toString();
		grid.clear();
		grid.add( new ArrayList<String>() );
		col = 0;
		row = 0;
		return content;
	}


	public String toString() {
		StringBuilder sb = new StringBuilder();
		for( int r = 0; r < grid.size(); r++ ) {
			for( int c = 0; c < header.size(); c++ ) {
				List<String> line = grid.get( r );
				// if( line.size() >= c+1 ) builder.append( grid.get( r ).get( c ) );
				if( line.size() >= c+1 ){
					String str = grid.get( r ).get( c );
					boolean isMath = str.matches("^isMath:[\\s\\S]*");
					if( isMath ) str = str.replaceAll("^isMath:", "");
					str = fixOnOutput1_widthAndAlign( str, header.get( c ), isMath );
					str = fixOnOutput2_mathPadding( str, isMath );
					str = fixOnOutput3_rightPadding( str );
					sb.append( str );
				} 
			}
			sb.append("\n");
		}
		return sb.toString();
	}


	public Gridder append( Gridder g ) {
		// Se esse grid estiver vazio, exclua a primeira linha que é criada automaticamente ao criar,
		// pois senão a string final vai ter um \n no inicio
		if( this.grid.size() == 1 && this.grid.get( 0 ).isEmpty() ) this.grid.clear();
		// mescla as linhas da outra grid
		for( List<String> row : g.grid ) grid.add( row );
		// recalcula os tamanhos de coluna
		for( int i = 0; i < g.header.size(); i++ ) {
			if( header.size() > i ) {
				header.set( i, Math.max( header.get( i ), g.header.get( i ) ) );
			} else {
				header.add( g.header.get( i ) );
			}
		}
		// recalcula a posição do ponteiro
		row = grid.size()-1;
		col = g.col;
		return this;
	}


	private void setColSize( String str ) {
		str = str.replaceAll( "^isMath:", "" );
		if( header.size()-1 < col )
			for( int i=header.size()-1; i<col; i++ )
				header.add( 0 );
		int cellSize = header.get( col );
		header.set( col, Math.max( cellSize, str.length() ) );
	}


	private String fixOnInsert2_number( String str ) {
//		String regex = "(?<=\\s|^)[+-]?[0-9]+(\\.[0-9]+)" + ( onlyDecimalNumbers ? "+" : "*" );
		String regex = "(?<=[\\s=\\+\\-\\*\\/]|^)[0-9]+(\\.[0-9]+)" + ( onlyDecimalNumbers ? "+" : "*" );
		Pattern patNumber = Pattern.compile( regex );
		Matcher m = patNumber.matcher( str );
		StringBuffer sb = new StringBuffer( str.length() );
		while( m.find() ) {
			Double numDbl = Double.valueOf( m.group() );
			String signed = numDbl > 0 && signedPositive ? "+" : "";
			String numStr = ( String.format( "%" + signed + "." + decimalPlaces + "f", numDbl )
				.replace( "-0", "0" )
				.replaceAll( ( trailingZeroes ? "^$" : "\\.?0+$" ), "" )
			);
			m.appendReplacement(sb, Matcher.quoteReplacement( numStr ) );
		}
		m.appendTail(sb);
		return sb.toString();
	}


	private String fixOnInsert3_mathCell( String str ) {
		if( str.matches( "^[=\\s\\+\\-\\/\\*]*[0-9].*" ) ) {
//			str = str.replaceAll("^\\s+", ""); //remove os espaços entre o inicio da linha e o numero, se houver
//			str = str.replaceAll("^=\\s+", "="); //remove os espaços entre o igual e o número, se houver
			str = str.replaceAll( "\\s", "" );
			str = str.replaceAll( "--|\\+\\+", "+" );
			str = str.replaceAll( "-\\+|\\+-", "-" );
			str = str.replaceAll( "\\*\\+", "*" );
			str = str.replaceAll( "/\\+", "/" );
			str = addPlusSign( str );
		}
		return "isMath:" + str;
	}


	private String fixOnInsert1_lineBreaks( String str ) {
		return str.replaceAll("[\\r\\n\\t]", "");
	}


	private String addPlusSign( String str ) {
		String sign = "+";
		if( str.matches( "^[=+-/\\*].*" ) ) {
			sign = str.substring(0,1) ;
			str = str.substring(1);
		}
		return sign + str;
	}


	private String fixOnOutput1_widthAndAlign( String str, int colWidth, boolean isMath ) {
		// largura ajustável é o padrão
		if( width > 0 ) colWidth = width; // largura fixa
		else if( width < 0 ) colWidth = Math.max( Math.abs( width ), colWidth ); // largura mínima
		int dif = colWidth - str.length();
		str = dif < 0 ? str.substring(0, colWidth) : str;
		String empty = dif > 0 ? repeat( " ", dif ) : "";
		char align = isMath ? alignMath : alignText;
		return align == '>' ? empty + str : str + empty;
	}


	private String fixOnOutput2_mathPadding( String str, boolean isMath ) {
		if( isMath ) {
			String pad = repeat(" ", spacing);
			Matcher m = Pattern.compile("^(\\s*)([=+-/\\*])(.*)").matcher( str );
			StringBuilder sb = new StringBuilder();
			if( m.find() ) {
				sb.append( m.group(2) + pad + m.group(1) + m.group(3)); // replaceTrailingZerosforSpace( m.group(3) )
				return sb.toString();
			}
		}
		return str;
	}
	
	
//	private String replaceTrailingZerosforSpace( String str ) {
//		StringBuilder sb = new StringBuilder( str );
//		if( trailingZeroes && sb.indexOf( "." ) > -1 ) {
//			for( int i=sb.length()-1; i >= 0; i-- ) {
//				if( sb.charAt(i) == '0' ) { sb.replace( i, i+1, " " ); }
//				else if( sb.charAt(i) == '.' ) { sb.replace( i, i+1, " " ); break; }
//				else break;
//			}
//			return sb.toString();
//		}
//		return str;
//	}


	private String fixOnOutput3_rightPadding( String str ) {
		String pad = repeat(" ", spacing);
		str = str + pad;
		return str;
	}


	public static String repeat( String s, int times ) {
		StringBuilder sb = new StringBuilder();
		for( int i = 0; i < times; i ++ ) sb.append( s );
		return sb.toString();
	}


}