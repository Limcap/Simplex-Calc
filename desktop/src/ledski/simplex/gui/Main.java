package ledski.simplex.gui;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;


public class Main {

	public static void main( String[] args ) {

		SwingUtilities.invokeLater( new Runnable() {	
			@Override
			public void run() {
				JFrame mainGUI = new MainGUI();
				mainGUI.setVisible( true );
			}
		});
	}

}
