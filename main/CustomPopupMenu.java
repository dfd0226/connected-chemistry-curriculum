
package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSeparatorUI;

import Util.ColorCollection;

import net.miginfocom.swing.MigLayout;

public class CustomPopupMenu extends JPopupMenu implements ActionListener {
	private static final long	serialVersionUID	= 1;
	private JPanel				panel			= new JPanel();
	private JScrollPane			scroll				= null;
	public static final Icon EMPTY_IMAGE_ICON = new ImageIcon("menu_spacer.gif");
	public static ArrayList panelList =  new ArrayList();
	public static ArrayList buttonList =  new ArrayList();
	public static ArrayList additionalList = new ArrayList();; // Contain newly added molecule to the default Set
	private Main main;
	
	public CustomPopupMenu(Main parent) {
		super();
		main = parent;
		this.setLayout(new BorderLayout());
		panel.setLayout(new MigLayout("insets 0, gap 0", "[200.00,grow]", "[grow][grow][grow][grow][grow]"));
		panel.setBackground(UIManager.getColor("MenuItem.background"));
		//		panelMenus.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		init();
	}

	private void init() {
		super.removeAll();
		scroll = new JScrollPane();
		scroll.setViewportView(panel);
		scroll.setBorder(null);
		scroll.setMinimumSize(new Dimension(240, 40));

		scroll.setMaximumSize(new Dimension(340,690));
		super.add(scroll, BorderLayout.CENTER);
		//		super.add(scroll);
	}

	public void show(Component invoker, int x, int y, int h) {
		panel.validate();
		int maxsize = scroll.getMaximumSize().height;
		int realsize = panel.getPreferredSize().height;

		int sizescroll = 0;

		if (maxsize < realsize) {
			sizescroll = scroll.getVerticalScrollBar().getPreferredSize().width;
		}
		scroll.setMaximumSize(new Dimension(340,h));
		this.pack();
		this.setInvoker(invoker);
		if (sizescroll != 0) {
			//Set popup size only if scrollbar is visible
			this.setPopupSize(new Dimension(scroll.getPreferredSize().width + 20, scroll.getMaximumSize().height - 26));
		}
		//        this.setMaximumSize(scroll.getMaximumSize());
		Point invokerOrigin = invoker.getLocationOnScreen();
		this.setLocation((int) invokerOrigin.getX() + x, (int) (invokerOrigin.getY() + y));
		this.setVisible(true);
		
		
		//Check if molecule is in the default set
		for (int i =0; i<buttonList.size();i++){
			CustomButton b = (CustomButton) buttonList.get(i);
			JPanel p = (JPanel) panelList.get(i);
			if (isDefaultSetMolecule(b.getName())){
				p.setBackground(ColorCollection.getColorMainBackground());
				b.setStatus(CustomButton.SIMULATION_DEFAULT);
			}
			else{
				//p.setBackground(Main.selectedColor);
			}
		}
	}

	public void hidemenu() {
		if (this.isVisible()) {
			this.setVisible(false);
		}
	}

	public boolean isDefaultSetMolecule(String name) {
		for (int i =0; i<main.defaultSetMolecules.size();i++){
			String mName = main.defaultSetMolecules.get(i).toString();
			if (mName.equals(name) ){
				return true;
			}
		}
		return false;
	}
	

	public int getIndex(String name) {
		for (int i =0; i<additionalList.size();i++){
			String mName = additionalList.get(i).toString();
			if (mName.equals(name) ){
				return i;
			}
		}
		return -100;
	}

	/*
	public void add(final CustomButton customButton, int id) {
		//		menuItem.setMargin(new Insets(0, 20, 0 , 0));
		if (customButton == null) {
			return;
		}
		
		final JPanel panel_2 = new JPanel();
		panel.add(panel_2, "cell 0 "+id);
		panel_2.setLayout(new MigLayout("insets 0, gap 0", "10[][][300.00]", "[][]"));
		panel_2.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (customButton.getStatus()==CustomButton.SIMULATION_DEFAULT)
					return;
				if (customButton.getStatus()==CustomButton.DEFAULT){
					additionalList.add(customButton.getName());
					customButton.setStatus(CustomButton.SELECTED);
					customButton.getContainerPanel().setBackground(main.selectedColor);
					customButton.setForeground(CustomButton.MENUITEM_FG_COLOR);
					main.addAdditionalMolecule();
				}
				else if (customButton.getStatus()==CustomButton.SELECTED){
					//additionalList.add(b.getName());
					customButton.setStatus(CustomButton.DEFAULT);
					customButton.getContainerPanel().setBackground(CustomButton.MENUITEM_BG_COLOR);
					customButton.setForeground(CustomButton.MENUITEM_FG_COLOR);
					main.removeAdditionalMolecule(getIndex(customButton.getName()));
					additionalList.remove(customButton.getName());
				}
			}
				
			public void mouseEntered(MouseEvent e) {
				if (customButton.getStatus()==CustomButton.SIMULATION_DEFAULT)
					return;
				panel_2.setBackground(CustomButton.MENU_HIGHLIGHT_BG_COLOR);
				customButton.setForeground(CustomButton.MENU_HIGHLIGHT_FG_COLOR);
			}	
			public void mouseExited(MouseEvent e) {
				if (customButton.getStatus()==CustomButton.SIMULATION_DEFAULT)
					return;
				if (customButton.getStatus()==CustomButton.SELECTED){
					panel_2.setBackground(main.selectedColor);
					customButton.setForeground(CustomButton.MENUITEM_FG_COLOR);
				}
				else{
					panel_2.setBackground(CustomButton.MENUITEM_BG_COLOR);
					customButton.setForeground(CustomButton.MENUITEM_FG_COLOR);
				}
				
			}

		});
		customButton.setContainerPanel(panel_2);
		panel_2.add(customButton, "cell 0 0");
		customButton.removeActionListener(this);
		customButton.addActionListener(this);
		if (customButton.getIcon() == null) {
			customButton.setIcon(EMPTY_IMAGE_ICON);
		}
		
		panelList.add(panel_2);
		buttonList.add(customButton);
	}*/
	
		
	/*
	public void actionPerformed(ActionEvent e) {
		//this.hidemenu();
		CustomButton b = (CustomButton) e.getSource();
		if (b.getStatus()==CustomButton.SIMULATION_DEFAULT)
			return;
		if (b.getStatus()==CustomButton.DEFAULT){
			additionalList.add(b.getName());
			b.setStatus(CustomButton.SELECTED);
			b.getContainerPanel().setBackground(main.selectedColor);
			b.setForeground(CustomButton.MENUITEM_FG_COLOR);
			main.addAdditionalMolecule();
		}
		else if (b.getStatus()==CustomButton.SELECTED){
			//additionalList.add(b.getName());
			b.setStatus(CustomButton.DEFAULT);
			b.getContainerPanel().setBackground(CustomButton.MENUITEM_BG_COLOR);
			b.setForeground(CustomButton.MENUITEM_FG_COLOR);
			main.removeAdditionalMolecule(getIndex(b.getName()));
			additionalList.remove(b.getName());
		}
		main.leftPanel.updateUI();
	}
*/
	public Component[] getComponents() {
		return panel.getComponents();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
