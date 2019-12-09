package ledski.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 * @author Leandro Lino (Ledski)
 * @version 0.1
 */
@SuppressWarnings("serial")
public class EasyMenuBar extends JMenuBar{
	
	
	
	
	
	private Color bgColor;
	public final Deque<JMenu> stack;
	private final Map<String, ItemAction> actions;
	private final Map<Component, Object[]> argsMap;
	private JMenuItem lastItem;
	
	
	
	
	
	public EasyMenuBar() {
		super();
		this.setOpaque( false );
		this.bgColor = new Color( 0,0,0,0 );
		this.actions = new HashMap<String, ItemAction>();
		this.stack = new ArrayDeque<JMenu>();
		this.argsMap = new HashMap<Component, Object[]>();
	}
	
	
	
	
	
	public void setColor(Color color) {
		this.bgColor = color;
	}
	
	
	
	
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(bgColor);
		g2d.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
	}
	
	
	
	
	
	public EasyMenuBar action( String key, ItemAction action ) {
		this.actions.put( key, action );
		return this;
	}
	
	
	
	
	
	public EasyMenuBar menu( String nome ) {
		JMenu menu = new JMenu( "<html><p style='margin:4px'>" + nome + "</p></html>" );
		// adiciona à barra de menu, na raiz
		this.add( menu );
		// esvazia o stack e adiciona o menu
		stack.clear();
		stack.addFirst( menu );
		return this;
	}
	
	
	
	
	
	public EasyMenuBar submenu( String nome ) {
		JMenu submenu = new JMenu( "<html><p style='margin:4px'>" + nome + "</p></html>" );
		// adiciona à barra de menu, sob o menu especificado no stack
		if( stack.size() == 0 )
			this.add( submenu );
		else
			stack.peek().add( submenu );
		// acrescenta o menu ao stack
		stack.addFirst( submenu );
		return this;
	}
	
	
	
	
	
	public EasyMenuBar item( Object acaoNome, String itemNome, Object ...args ) {
		JMenuItem item = new JMenuItem( "<html><p style='margin:4px'>" + itemNome + "</p></html>" );
		lastItem = item;
		stack.peek().add( item );
		argsMap.put( item, args );
		item.addActionListener( new ActionListener() { public void actionPerformed( ActionEvent e ) {
			ItemAction itemAction = actions.get( (String) acaoNome );
			if( itemAction != null ) {
				Object[] itemArgs = argsMap.get( e.getSource() );
				itemAction.onClick( e, itemArgs );
			}
		}});
		return this;
	}
	public EasyMenuBar item( String itemNome ) {
		JMenuItem item = new JMenuItem( "<html><p style='margin:4px'>" + itemNome + "</p></html>" );
		lastItem = item;
		stack.peek().add( item );
		return this;
	}
	
	
	
	
	
	public EasyMenuBar onClick( String acaoNome, Object ...args ) {
		argsMap.put( lastItem, args );
		lastItem.addActionListener( new ActionListener() { public void actionPerformed( ActionEvent e ) {
			ItemAction itemAction = actions.get( (String) acaoNome );
			if( itemAction != null ) {
				Object[] itemArgs = argsMap.get( e.getSource() );
				itemAction.onClick( e, itemArgs );
			}
		}});
		return this;
	}
	
	
	
	
	
	public EasyMenuBar button( Object acaoNome, String itemNome, Object ...args ) {
//		JButton button = new JButton( "<html><p style='margin:5px'>" + itemNome + "</p></html>" );
//		MyButton button = new MyButton( itemNome );
		
//		button.setBorderPainted(false);
//		button.setMargin( new JMenuItem( "<html><p style='margin:5px'>" + itemNome + "</p></html>" ).getMargin()  );
//		button.setOpaque(false);
//		button.setBackground( new Color( 0, 0, 0, 0 ));
//		button.setBackground( new JMenuItem().getBackground() );
//		button.setBorder( null );
//		button.setFocusable( false );
//		button.setContentAreaFilled(false);
		
		JMenuItem button = new JMenuItem( itemNome );
		JMenu m = new JMenu( itemNome );
//		button.setMinimumSize( m.getMaximumSize() );
		button.setPreferredSize( m.getPreferredSize() );
//		button.setMaximumSize( m.getSize() );
		button.setActionCommand("ActionText");
		
		this.add( button );
		argsMap.put( button, args );
		button.addActionListener( new ActionListener() { public void actionPerformed( ActionEvent e ) {
			ItemAction itemAction = actions.get( (String) acaoNome );
			Object[] itemArgs = argsMap.get( e.getSource() );
			if( itemAction != null ) itemAction.onClick( e, itemArgs );
		}});
		return this;
	}
	
	
	
	
	public EasyMenuBar parent() {
		stack.pollFirst();
		return this;
	}
	
	
	
	
	public EasyMenuBar root() {
		stack.clear();
		return this;
	}
	
	
	
	
	
	public static abstract class ItemAction {
		public abstract void onClick( ActionEvent e, Object[] args );
	}
	
	
	
	
	
	class MyButton extends JButton {

        private Color hoverBackgroundColor;
        private Color pressedBackgroundColor = new JMenuItem().getBackground();

        public MyButton() {
            this(null);
        }

        public MyButton(String text) {
            super(text);
            super.setContentAreaFilled(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (getModel().isPressed()) {
                g.setColor(pressedBackgroundColor);
            } else if (getModel().isRollover()) {
                g.setColor(hoverBackgroundColor);
            } else {
                g.setColor(getBackground());
            }
            g.fillRect(0, 0, getWidth(), getHeight());
            super.paintComponent(g);
        }

        @Override
        public void setContentAreaFilled(boolean b) {
        }

        public Color getHoverBackgroundColor() {
            return hoverBackgroundColor;
        }

        public void setHoverBackgroundColor(Color hoverBackgroundColor) {
            this.hoverBackgroundColor = hoverBackgroundColor;
        }

        public Color getPressedBackgroundColor() {
            return pressedBackgroundColor;
        }

        public void setPressedBackgroundColor(Color pressedBackgroundColor) {
            this.pressedBackgroundColor = pressedBackgroundColor;
        }
    }
}
