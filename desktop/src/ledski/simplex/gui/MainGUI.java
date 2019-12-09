package ledski.simplex.gui;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import ledski.simplex.api.Simplex;
import ledski.util.EasyMenuBar;
import ledski.util.EasyMenuBar.ItemAction;
import ledski.util.MyComponents.MyLink;
import ledski.util.MyComponents.MyPanel;


/**
 * @author Leandro Lino (Ledski)
 * @version 0.2
 *
 */
public class MainGUI extends JFrame {
	private static final long serialVersionUID = 1L;
	
	// COMPONENTES DA JANELA
	MainGUI thisFrame;
	MyPanel panel0;
	BoxLayout panel0layout;
	JScrollPane scrollPane;
	
	// COMPONENTES DO FORMULARIO
	Font monoFont;
	JLabel lbTitulo;
	JLabel lbFO;
	JTextField txtFO;
	JLabel lbRestricoes;
	JTextArea txaRestricoes;
	JButton btSolve;
	JCheckBox chkIteracoes;
	JTextArea txaSolution;
	JLabel lbExemplos;
	MyLink lnkVersao;
	MyLink lnkExemploMin;
	JPanel endPanel;
	
	// STRINGS CONSTANTES
	final static String VERSAO = "0.1";
	
	final static String INSTRUCOES =
	"Instruções:" +
	"\n- Para calcular o Simplex, digite a função objetivo e as restrições nos campos apropriados." +
	"\n- Na função objetivo, digite somente 'max' ou 'min' como termo independente. " +
	"\n- Escolha uma letra para cada variável." +
	"\n- Em cada expressão não deve haver termos com variáveis repetidas." +
	"\n- Serão consideradas, automaticamente, restrições de >= 0 para todas as variáveis." +
	"\n- As variáveis de ajuste (folga e excesso) são nomeadas como @1, @2, etc." +
	"\n- Consulte os exemplos fornecidos para verificar como preencher os campos corretamente." +
	"\n" +
	"\nObservação:" +
	"\nO método utilizado para a resolução dos problemas é o método padrão, portando todas as restrições de" +
	"\nmaximização devem ser >= e de minimização, <=, e todos os termos independentes devem ser não-negativos." +
	"\nPara os casos que não obedecem a essas regras, outros métodos de resolução devem ser aplicados," +
	"\ncomo o Duas Fases e o Big M, porém, estes métodos não estão implementados nesta versão" +
	"\ne uma mensagem de erro será exibida explicando a limitação.";

	final static String SOBRE =
	"Calculador de Simplex v." + VERSAO + "\nDesenvolvido como trabalo acadêmico para a disciplina " +
	"Pesquisa Operacional\nFaculdade Cotemig\nAutor: Ledski";
//	final static String INFOSIMPLEX =
//	"O método Simplex é um processo iterativo que permite melhorar a solução da função objetivo em cada etapa."+
//	"\nO processo finaliza quando não é possível continuar melhorando este valor, ou seja, quando se obtenha a"+
//	"\nsolução ótima (o maior ou menor valor possível, segundo o caso, para que todas as restrições sejam"+
//	"satisfeitas)."+
//	"\n\nSerá necessário considerar que o método Simplex trabalha apenas com restrições do problema cujas"+
//	"\ndesigualdades sejam do tipo \"≤\" (menor ou igual) e seus coeficientes independentes sejam maiores ou"+
//	"\niguais a 0. Portanto, é preciso padronizar as restrições para atender aos requisitos antes de iniciar o"+
//	"\nalgoritmo Simplex. Caso apareçam, depois deste processo, restrições do tipo \"≥\" (maior ou igual) ou"+
//	"\"=\" (igualdade), ou não seja possível alterá-las, será necessário utilizar outros métodos de resolução,"+
//	"\nsendo o mais comum, o método das Duas Fases.";
	
	
	
	
	
	/**
	 * Construtor da GUI
	 */
	public MainGUI() {
		
		super( "Cálculador de Simplex" );
		configurarJanela();
		registrarFonte();
		configurarFormulario();
		montarLayout();
		acoplarEventos();
	}
	
	
	
	
	/**
	 * Faz as configurações relacionadas ao Frame (Janela):
	 * tamanho, posicionamento e inicializa a variável thisFrame.
	 */
	private void configurarJanela() {
		
		thisFrame = this;
		setPreferredSize( new Dimension( 800, 600) );
		setMinimumSize( new Dimension( 300, 500 ) );
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		pack();
		centralizarNaTela();
	}
	
	
	
	
	
	/**
	 * Registra uma fonte para poder ser usada nos componentes
	 */
	private void registrarFonte() {
		
		// como listar fontes disponíveis: for( String f : ge.getAvailableFontFamilyNames() ) System.out.println( f );
		try {
			// O jeito de carregar a fonte abaixo só funciona no projeto. Em um arquivo JAR deve ser feito através do getResourcesAsStrem
			// Font customFont = Font.createFont(Font.TRUETYPE_FONT, new File("./res/RobotoMono-Regular.ttf")).deriveFont(12f);
			
			// Carregar o arquivo de fonte. Funciona em um JAR 
			InputStream fontInputStream= getClass().getResourceAsStream("/resources/RobotoMono-Regular.ttf" );
			Font customFont = Font.createFont(Font.TRUETYPE_FONT, fontInputStream ).deriveFont(12f);
			
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(customFont);
		} catch (IOException e) {
			e.printStackTrace();
		} catch(FontFormatException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
	/**
	 * Define o contentPane, define a barra de menu do frame
	 * e faz o posicionamento dos elementos
	 */
	private void montarLayout() {
		
		// JANELA
		setJMenuBar( barraDeMenu() );
		setContentPane( scrollPane );
		
		// FORMULARIO
		adicionar( lbFO);
		espacamento( 1 );
		adicionar( txtFO );
		espacamento( 2 );
		adicionar( lbRestricoes );
		espacamento( 1 );
		adicionar( txaRestricoes );
		espacamento( 3 );
		adicionar( btSolve, chkIteracoes );
		espacamento( 2 );
		adicionar( txaSolution );
		espacamento( 2 );
		adicionar( lnkVersao );
		
		// AREA EXPANSÍVEL
		panel0.add( endPanel );
	}
	
	
	
	
	
	/**
	 * Inicializa e configura os componentes do formulario
	 */
	private void configurarFormulario() {
		
		// FONTS
		monoFont = new Font("Roboto Mono", Font.PLAIN, 12);
		
		// MAIN PANEL

		panel0 = new MyPanel( "/resources/background.png" );
		panel0layout = new BoxLayout( panel0, BoxLayout.Y_AXIS );
		panel0.setLayout( panel0layout );
		panel0.setBorder( new EmptyBorder( 20, 20, 20, 20 ) );
		
		// SCROLL
		scrollPane = new JScrollPane( panel0 );
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setViewportView( panel0 );
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		
//		// TITULO
//		lbTitulo = new JLabel( "Cálculo de Simplex" );
//		lbTitulo.setFont( new Font( lbTitulo.getFont().getName(), Font.BOLD, 24 ) );
//		adicionar( lbTitulo );
//		espacamento( 1 );
		
		// LABEL FUNCAO OBJETIVO
		lbFO = new JLabel( "Função Objetivo" );
		
		// CAMPO FUNCAO OBJETIVO
		txtFO = new JTextField( 50 );
		txtFO.setFont( monoFont );
		
		// LABEL RESTRICOES
		lbRestricoes = new JLabel( "Restrições" );
		
		// CAMPO RESTRICOES
		txaRestricoes = new JTextArea( 3, 50 );
		txaRestricoes.setFont( monoFont );
		txaRestricoes.setBorder( txtFO.getBorder() );
		
		// BOTAO
		btSolve = new JButton( "Resolver" );
		
		// CHECKBOX RESOLVER
		chkIteracoes = new JCheckBox( "Mostrar iterações" );
		chkIteracoes.setSelected( false );
		chkIteracoes.setOpaque( false );
		chkIteracoes.setBorder( new JTextField().getBorder() );
		
		// CAMPO SOLUCAO
		txaSolution = new JTextArea(10, 50);
		txaSolution.setFont( monoFont );
		txaSolution.setEditable( false );
		txaSolution.setBorder( txtFO.getBorder() );
		txaSolution.setText( INSTRUCOES );
		
		// LINKS
		lnkVersao = new MyLink( "v." + VERSAO );
		
		// AREA EXPANSIVEL
		// ## alow for texareas to expand down when pressing enter after preventVerticalStretch has been applied
		endPanel = new JPanel( new GridBagLayout() );
		endPanel.setOpaque( false );
		//endPanel.setBackground( new Color( 0,0,0,0 ) );
	}
	
	
	
	
	
	/** 
	 * Cria a barra de menus
	 */
	private JMenuBar barraDeMenu() {
		
		EasyMenuBar menubar = new EasyMenuBar();
		
		menubar
		
		// ITENS DO MENU
		.menu( "Exemplos de maximização" )
		.item( "Slide - Atividade 1" ).onClick( "fill", "3x + 5y = max", "x <= 4\ny <= 6\n3x + 2y <= 18", "" )
		.item( "Slide - Atividade 2" ).onClick( "fill", "30x + 50y = max", "2x + y <= 16\nx + 2y <= 11\nx + 3y <= 15", "" )
		.item( "Slide - Atividade 3" ).onClick( "fill", "2x + y = max", "x + y <= 5\nx + 2y <= 8\nx <= 4", "" )
		.item( "Slide - Atividade 4" ).onClick( "fill", "4x + y = max", "2x + 3y <= 12\n2x + y <= 8", "" )
		.item( "Prova 1 - Questão 4" ).onClick( "fill", "4x + 5y = max", "4x + 7y <= 28\n6x+3y <= 18", "" )
		.item( "Problema - Fábrica de mármores" ).onClick( "fill", "7s + 8.5p = max", "0.6s + 0.8p <= 16\n24s + 20p <= 360", "" )
		.item( "Problema - Fábrica de bicicletas" ).onClick( "fill", "38p + 49c = max", "p + 1.5c <= 160\n2.5p + 2.5c <= 256\nc <= 40", "" )

		.menu( "Exemplos de minimização" )
		.item( "Slide - Atividade 1" ).onClick( "fill", "3x + 4y = min", "5x + 3y >= 14\n2x + 7y >= 11", "" )
		.item( "Slide - Atividade 2" ).onClick( "fill", "7x + 6y = min", "8x + 9y >= 70\n9x + 5y >= 47", "" )
		.item( "Slide - Atividade 3" ).onClick( "fill", "5x + 4y = min", "10x + 2y >= 20\n8x + 4y >= 32\n4x + 10y >= 40", "" )
		.item( "Problema - Granja" ).onClick( "fill", "0.6a + 0.8b = min", "10a + 20b >= 2\n40a + 60b >= 64\n50a + 20b >= 34", "" )
		.item( "Prova 1 - Questao 5" ).onClick( "fill", "4x + 5y = min", "4x + 7y >= 28\n6x + 3y >= 18", "" )

		.menu( "Opções" )
		.item( "Ajustar janela" ).onClick( "pack" )
		.item( "Limpar campos" ).onClick( "fill", "", "", "" )
		
		.menu( "Ajuda" )
		.item( "Mostrar instruções" ).onClick( "fill", null, null, INSTRUCOES )
//		.item( "Informações sobre Simplex" ).onClick( "fill", null, null, INFOSIMPLEX )
		.item( "Sobre" ).onClick( "fill", null, null, SOBRE )
		
		// ACOES DO MENU
		.action( "fill", new ItemAction() { public void onClick( ActionEvent e, Object[] args ) {
			if( args.length > 0 && args[0] != null ) txtFO.setText( (String) args[0] );
			if( args.length > 1 && args[1] != null ) txaRestricoes.setText( (String) args[1] );
			if( args.length > 2 && args[2] != null ) txaSolution.setText( (String) args[2] );
		}})
		.action( "pack", new ItemAction() { public void onClick( ActionEvent e, Object[] args ) {
			thisFrame.pack();
		}})
		;
		
		return menubar;
	}
	
	
	
	
	
	/**
	 * Acopla eventos de usuário aos elementos do formulário.
	 * No caso somente ao botão Resolver.
	 */
	private void acoplarEventos() {
		
		btSolve.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e ) {
				String[] modeloDoProblema = empacotarFormulario();
				if( modeloDoProblema.length > 0 ) {
					String resultado = Simplex.executar( modeloDoProblema, chkIteracoes.isSelected() );
					txaSolution.setText( resultado );
				}
				// É necessario colocar a posicão do caret no inicio para que o setvalue da scrollbar possa
				// consiga ser colocado no 0 se o texto for muito longo.
				txaSolution.setCaretPosition( 0 );
				scrollPane.getVerticalScrollBar().setValue( 0 );

			}
		});
	}
	
	
	
	
	
	// ====================================================================================================================
	// MÉTODOS UTILITARIOS
	//=====================================================================================================================
	
	private String[] empacotarFormulario() {
		String strSistema = txtFO.getText() + "\n" + txaRestricoes.getText();
		return strSistema.split( "\n" );
	}
	
	
	
	
	
	public void centralizarNaTela() {
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - this.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - this.getHeight()) / 2);
		this.setLocation(x, y);
	}
	
	
	
	
	
	private void espacamento( int s ) {
		panel0.add( Box.createRigidArea(new Dimension(5*s,5*s)) );
	}
	
	
	
	
	
	private void preventVerticalStretch( Component c ) {
		c.setMaximumSize( new Dimension( Integer.MAX_VALUE, c.getPreferredSize().height ) );
	}
	
	
	
	
	
	private void adicionar( Component ...cs ) {
		JPanel pH = new JPanel();
//		pH.setBorder( BorderFactory.createLineBorder( Color.CYAN ) );
//		pH.setBackground( new Color( 0,0,0,0 ) );
		pH.setOpaque( false );
		pH.setLayout( new GridBagLayout() );
//		pH.setLayout( new FlowLayout() );
		preventVerticalStretch( pH );
//		int stretch = cs.length == 1 ? GridBagConstraints.BOTH : GridBagConstraints.NONE;
		for( int i = 0; i < cs.length; i++ ) {//Component c : cs ) {
//			if( cs[i] instanceof JCheckBox ) ((JCheckBox) cs[i]).setOpaque( false );
//			if( cs[i] instanceof JTextArea ) ((JTextArea) cs[i]).setBorder( new JTextField().getBorder() );
			if( cs.length == 1 ) {
				pH.add( cs[i], new GridBagConstraints(i,0,1,1,1,1,GridBagConstraints.LINE_START,GridBagConstraints.BOTH,new Insets(0,0,0,0),1,1) );	
			}
			else {
				pH.add( cs[i], new GridBagConstraints(i,0,1,1,0,0,GridBagConstraints.LINE_START,GridBagConstraints.NONE,new Insets(0,0,0,10),1,1) );
			}
		}
		if( cs.length > 1 )
			pH.add( new JLabel(), new GridBagConstraints(cs.length,0,1,1,1,1,GridBagConstraints.LINE_START,GridBagConstraints.BOTH,new Insets(0,0,0,0),1,1) );
		panel0.add( pH );
	}
	
	
	
	
	
}
