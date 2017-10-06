package main;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JScrollBar;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.ListSelectionModel;

import data.YAMLinterface;


import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;

public class TableSet extends JPanel {
	public  JTable table = null;
	public  JScrollPane scrollPane;
	public  ArrayList set;
	public  int selectedRow=0; 
	private Main main;
	
	public TableSet(Main parent) {
		super(new GridLayout(1, 0));
		main = parent;
		MyTableModel myTable = new MyTableModel();
		table = new JTable(myTable);

		
	    scrollPane = new JScrollPane(table);
		JScrollBar jj = new JScrollBar();
		jj.setOrientation(JScrollBar.HORIZONTAL);
		scrollPane.getViewport().setViewPosition(new java.awt.Point(0, 0));

		scrollPane.setHorizontalScrollBar(jj);
		//table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(new RowListener());
		table.getColumnModel().getColumn(0).setPreferredWidth(50);
		table.setSelectionBackground(Color.GRAY);//new Color(40,60,220));
		
		DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
		dtcr.setHorizontalAlignment(SwingConstants.CENTER);
		table.getColumnModel().getColumn(0).setCellRenderer(dtcr);
		
		
		// Set up renderer and editor for the Favorite Color column.
		table.setDefaultRenderer(Color.class, new ColorRenderer(true));
		//table.setDefaultEditor(Color.class, new ColorEditor(this));
		// Add the scroll pane to this panel.
		add(scrollPane);
		Color c = new Color(245,245,245);
		scrollPane.getViewport().setBackground(c);
		table.setBackground(c);
		
	}

	
	public void setSelectedRow(int row) {
		selectedRow = row;
		table.clearSelection();
		if (selectedRow<0) 
			selectedRow=0;
		//table.addRowSelectionInterval(selectedRow, selectedRow);
		table.getSelectionModel().setSelectionInterval(row, row);
		table.repaint();
	}
	private class RowListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent event) {
			if (event.getValueIsAdjusting()) {
				return;
			}
			selectedRow = table.getSelectedRow();
			if(selectedRow<0) 
				//Listener will be called when destroy last table set
				//By adding this check we can skip clear selection conditions
				return;	
			main.setSelectedSet(selectedRow +1)  ;
			main.reset();
		}
	}

	//Update table of sets while loading in specific sims
	public void updateSet(){
		set.clear();
		ArrayList sets = YAMLinterface.getSets(main.getSelectedUnit(), main.getSelectedSim());
		if (sets==null) return;
		
		for (int i=0; i<sets.size();i++){
			this.set.add(i+1);
		}
		if ( main.getTableSet() !=null){
			table.updateUI();//We need to use updateUI() to change the look of tableSet
		}		
	}
	public void reset()
	{
		selectedRow  = 0 ;
	}
	
	class MyTableModel extends AbstractTableModel {
		private String[] columnNames = {};
		
		public MyTableModel() {
			set = new ArrayList();
			columnNames = new String[1];
			columnNames[0] = "             Set";
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {
			return set.size();
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public Object getValueAt(int row, int col) {
			return set.get(row);
		}

		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

		public boolean isCellEditable(int row, int col) {
			return false;
		}

		public void setValueAt(Object value, int row, int col) {
			set.set(row, value);
			fireTableCellUpdated(row, col);
		}
	}

	/**
	 * @return the main
	 */
	public Main getMain() {
		return main;
	}

	/**
	 * @param main the main to set
	 */
	public void setMain(Main main) {
		this.main = main;
	}

	
}
