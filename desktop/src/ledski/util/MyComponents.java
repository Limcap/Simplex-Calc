package ledski.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.LayoutManager;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * @author Leandro Lino (Ledski)
 * @version 0.1
 */
public class MyComponents {
	
	
	
	
	
	@SuppressWarnings("serial")
	public static class MyLink extends JButton {
		public MyLink( String label ) {
			setText("<HTML><FONT color=\"#0000FF\"><U>" + label + "</U></FONT></HTML>");
			setHorizontalAlignment(SwingConstants.LEFT);
			setBorderPainted(false);
			setOpaque(false);
			setBackground(Color.WHITE);
			setBorder( null );
		}
	}
	
	
	
	
	
	@SuppressWarnings("serial")
	public static class MyLabel extends JLabel {
		public MyLabel( String label, int size ) {
			super( label );
			this.setForeground( Color.GRAY );
			this.setFont( new Font( "Sans-Serif", Font.PLAIN, size ) );
		}
	}
	
	
	
	
	
	@SuppressWarnings("serial")
	public static class MyPanel extends JPanel {
		boolean tile = true;
		Image img;
		public MyPanel( String img ) {
			try {
				// Carregar arquivo por getResourceAsStream funciona dentro de um JAR
				this.img = ImageIO.read( getClass().getResourceAsStream( img ));
				// O jeito abaixo só funciona fora do JAR
				// this.img = new ImageIcon( img ).getImage();
			} catch( IOException e ) {
				e.printStackTrace();
			}
		}
		public MyPanel( LayoutManager lm ) {
			super( lm );
			this.img = new ImageIcon( img ).getImage();
		}
		public void paintComponent( Graphics g ) {
			super.paintComponent( g );
			if (tile) {
				int iw = img.getWidth(this);
				int ih = img.getHeight(this);
				if (iw > 0 && ih > 0) {
					for (int x = 0; x < getWidth(); x += iw) {
						for (int y = 0; y < getHeight(); y += ih) {
							g.drawImage(img, x, y, iw, ih, this);
						}
					}
				}
			} else {
				g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
			}
		}

	}
	
	
	
	
	
}
