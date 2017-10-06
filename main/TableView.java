package main;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JScrollBar;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.ListSelectionModel;

import Util.ColorCollection;

import simulations.P5Canvas;
import simulations.models.Compound;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class TableView extends JPanel {
	public JTable table = null;
	public boolean stopUpdating = false;

	public JScrollPane scrollPane;
	public ArrayList[] data = new ArrayList[3];
	public Color[] colors; 
	private int[] selectedRows;
	public int colorChangingRow;
	//public int selectedRow=-1;
	private Main main;
	private P5Canvas p5Canvas;
	private MyTableModel myTable;
	
	
	public TableView(Main parent) {
		super(new GridLayout(1, 0));
		this.main = parent;
		p5Canvas = main.getP5Canvas();
		 myTable = new MyTableModel();
		
		
		table = new JTable(myTable);
	    scrollPane = new JScrollPane(table);
		JScrollBar jj = new JScrollBar();
		jj.setOrientation(JScrollBar.HORIZONTAL);
		scrollPane.getViewport().setViewPosition(new java.awt.Point(0, 0));

		scrollPane.setHorizontalScrollBar(jj);
		//table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		//table.getSelectionModel().addListSelectionListener(new RowListener());
		table.addMouseListener(new MouseAdapter()
		{
		   public void mouseReleased(MouseEvent evt)
		   {
			   if(evt.getSource() == table)
               {
               
               	int [] tempRows = table.getSelectedRows();
               	if(tempRows.length==1) //If there is only one Selected Row, select it or deselect it
               	{
               		if(selectedRows==null || !selectedRowsContain(tempRows[0]))
               		{
               			selectedRows = tempRows;
               		}
               		else
               		{
               			selectedRows = null;
               			table.clearSelection();
               		}
               	}
               	else //If there is multiple rows get selected
               	{
               		selectedRows = table.getSelectedRows();
               	}
               	}
		   }
		});
		table.getColumnModel().getColumn(0).setPreferredWidth(10);
		table.getColumnModel().getColumn(1).setPreferredWidth(40);
		table.getColumnModel().getColumn(2).setPreferredWidth(120);
		table.setSelectionBackground(Color.GRAY);//new Color(40,60,220));
		
		DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
		dtcr.setHorizontalAlignment(SwingConstants.RIGHT);
		table.getColumnModel().getColumn(0).setCellRenderer(dtcr);
		
		
		// Set up renderer and editor for the Favorite Color column.
		table.setDefaultRenderer(Color.class, new ColorRenderer(true));
		//table.setDefaultEditor(Color.class, new ColorEditor(this));
		
		
		// Add the scroll pane to this panel.
		add(scrollPane);
		scrollPane.getViewport().setBackground(ColorCollection.getColorTableViewBackground());
		table.setBackground(ColorCollection.getColorTableViewBackground());
		
		colors = ColorCollection.getColorGraphLine();

		
		
	}

	//Update tableView, which is presenting molecule legends below chart
	//Reture false if values are not ready yet
	public boolean updateTableView(){
		
		if(data[0].isEmpty())
			return false;
		int unit = p5Canvas.getUnit()	;
		String name = null;
		DecimalFormat myFormatter = outputFormat(unit);
		String output = null;
	
		for(int i = 0;i<getItemNum();i++)
		{
			double value = dataConversion(unit, i);
			output = myFormatter.format(value);
			data[0].set(i, output);
		}
//		for (int i=0; i<Compound.names.size();i++){
//			name = (String)Compound.names.get(i);
//			output = myFormatter.format(dataConversion(unit,i));
//			data[0].set(i, output);
//		}

		if (this!=null && !stopUpdating){
			//myTable.fireTableDataChanged();
			for (int i=0; i<getItemNum();i++){
				myTable.fireTableCellUpdated(i, 0);
			}
		}	
		return true;
	}


	public Color getColor(int index) {
		if (index<colors.length){
			return colors[index];
		}
		return Color.BLACK;
	}
	/*
	public void setSelectedRow(int [] rows) {
		selectedRows = rows;
		table.clearSelection();
		if (selectedRows!=null && selectedRows.length>0){
		for( int row:selectedRows)
		{
			table.addRowSelectionInterval(row, row);
		}
		table.updateUI();
		}
	}*/

	public boolean addSelectedRow( int [] rows)
	{
		if( rows==null || rows.length<=0)
			return false;
		else
		{
				if(selectedRows!=null)
				{
					List<Integer> tempRows = new LinkedList<Integer>();
					for(int index:selectedRows)
						tempRows.add(index);
					for(int addIndex:rows)
						if(!tempRows.contains(addIndex))
							tempRows.add(addIndex);
					selectedRows = new int [tempRows.size()];
					int i = 0;
					for( i = 0;i<tempRows.size();i++)
					{
						selectedRows[i] = tempRows.get(i).intValue();
					}
				}
				else //selectedRows ==null
				{
					selectedRows = new int [rows.length];
					for(int i =0;i<rows.length;i++)
						selectedRows[i] = rows[i];
				}
				table.clearSelection();
				for( int selectedIndex:selectedRows)
				table.getSelectionModel().addSelectionInterval(selectedIndex, selectedIndex);
				
				return true;
		}
		
	}

	public void deselectRows(int [] rows)
	{
		if(selectedRows!=null)
		{
			if(selectedRows.length>0)
			{
				List<Integer> newSelectedRows = new LinkedList<Integer>();
				List<Integer> deselectRows = new LinkedList<Integer>();
				for(int deselectRow:rows) //Translate Rows to list
				{
					deselectRows.add(deselectRow);
				}
				
				for(int rowIndex:selectedRows) //Remove those rows which we want to deselect
				{
					if(!deselectRows.contains(rowIndex))
						newSelectedRows.add((Integer)rowIndex);
				}
				//Make a new int array
				selectedRows = new int[newSelectedRows.size()];
				for (int i = 0; i < newSelectedRows.size(); i++) {
					selectedRows[i] = ((Integer)newSelectedRows.get(i)).intValue();
					
				}
				
				//Clear table selection
				table.getSelectionModel().clearSelection();
				//Add selected rows to table
				for( int i = 0;i<selectedRows.length;i++)
				{
					//table.setRowSelectionInterval(selectedRows[i], selectedRows[i]);
					table.getSelectionModel().addSelectionInterval(selectedRows[i], selectedRows[i]);
				}
				
			}
		}
	}
	public void selectAllRows()
	{
		table.selectAll();
		selectedRows = table.getSelectedRows();
	}
	public boolean selectedRowsContain(int row)
	{
		boolean res = false;
		if(selectedRows!=null)
			if(selectedRows.length>0)
				for( int r:selectedRows)
				{
					if(r == row)
					{
						res = true;
						break;
					}
				}
		return res;
	}
	
	//Return molecule name when that molecule is being selected on legends
	public String [] getSelectedMolecule(){
		if(!selectedRowsIsEmpty())
		{
			int [] selectedRows = getSelectedRows();
			String [] molecules = new String [selectedRows.length];
			
			for(int i = 0;i<selectedRows.length;i++)
			{
				if(selectedRows[i]<Compound.names.size())
				molecules[i] = new String(Compound.names.get(selectedRows[i]));
			}
			
			return molecules;
		}
		return null;
	}

	public boolean selectedRowsIsEmpty()
	{
		if(selectedRows!=null)
			if(selectedRows.length>0)
				return false;
		return true;
	}
	public int selectedRowsCount()
	{
		return selectedRows.length;
	}
	public int [] getSelectedRows()
	{
		return selectedRows.clone();
	}
	public void increaseRowCount(int index, int count)
	{
		
		if(index>=0 && index<data[0].size())
		{
			Integer newCount = new Integer(((Integer)data[0].get(index)).intValue()+count);
			if(newCount.intValue()<0)
				newCount=0;
			data[0].set(index, newCount);
		}
	}
	
	public boolean contains(String name)
	{
		return (indexOf(name)==-1)?false:true;
	}

	public int indexOf(String name)
	{
		String moleculeName = null;
		for(int i =0;i<myTable.getRowCount();i++)
		{
			moleculeName = new String((String) myTable.getValueAt(i, 2)); //col is molecule name
			if(moleculeName.equals(name))
			{
				return i;
			}
		}
		return -1;
	}
	
	public void reset()
	{
		int unit = p5Canvas.getUnit();
		int sim = p5Canvas.getSim();
		int set = p5Canvas.getSet();
		
		p5Canvas.unitList.resetTableView(unit, sim, set);
		
		clearData();
		
		DecimalFormat myFormatter = outputFormat(unit);
		String output = null;
		String name = null;
		
		ArrayList<String> names = p5Canvas.getNameTableView();
		
		for(int i =0;i<names.size();i++){
				name = new String(names.get(i));
				output = myFormatter.format(dataConversion(unit,i));
				data[0].add(output);
				data[1].add((Color)colors[i]);
				data[2].add(name);
			}
		
			table.updateUI(); //We need to use updateUI() To redraw table in Unit 3 Sim2
			this.updateTableView();
			
		//Make sure on rows are selected
		clearSelection();
	}
	//Clear previous data
	public void clearData()
	{
		//Add Rows corresponding to compounds
		data[0].clear();
		data[1].clear();
		data[2].clear();
	}
	
	//The first time add molecule to table view to guarantee it works well
	//Called by Main only
	public void addData(String compoundName)
	{
		int unit = p5Canvas.getUnit();
		String output = null;
		DecimalFormat myFormatter = outputFormat(unit);

		if(!data[2].contains(compoundName)) //If no name found in table, add it
		{
			int i = data[2].size();
			output = myFormatter.format(0);
			data[0].add(output);
			data[1].add((Color)colors[i]);
			data[2].add(compoundName);
		}
	}
	

	
	private float dataConversion(int unit,int indexOfCompound)
	{
		if(indexOfCompound>=Compound.counts.size())
			return 0;
		float res=0;
		res = p5Canvas.getDataTableView(indexOfCompound);
//		String name = null;
//		switch(unit)
//		{
//			default:
//				res = Compound.counts.get(indexOfCompound);
//				break;
////			case 3:
////				name = (String)Compound.names.get(indexOfCompound);
////				res =  p5Canvas.getUnit3().getMassByName(name);
////				break;
//			case 5:
//			    name = (String)Compound.names.get(indexOfCompound);
//				res = p5Canvas.getUnit5().getConByName(name);
//				break;
//			case 6:
//				name = (String)Compound.names.get(indexOfCompound);
//				res = p5Canvas.getUnit6().getConByName(name);
//				break;
//			case 7:
//				if(p5Canvas.getSim()==2) //Sim 2 output mass
//				{
//					name = (String)Compound.names.get(indexOfCompound);
//					res =  p5Canvas.getUnit7().getMassByName(name);	
//				}
//				else if(p5Canvas.getSim()==8||p5Canvas.getSim()==7) // Sim 8 output mole
//				{
//					name = (String)Compound.names.get(indexOfCompound);
//					res = p5Canvas.getUnit7().getMoleByName(name);
//				}
//				else //output number of molecules
//				{
//					res = Compound.counts.get(indexOfCompound);
//
//				}
//				break;
//			
//		}
		return res;
	}
	
	private DecimalFormat outputFormat(int unit)
	{
		DecimalFormat myFormatter = null;
		int sim = p5Canvas.getSim();
		switch(unit)
		{
		default:
			myFormatter = new DecimalFormat("###");
			break;
		case 3:
			myFormatter = new DecimalFormat("###.##");
			break;
		case 7:
			myFormatter = new DecimalFormat("###.###");
			break;
		case 5:
			if(sim==2 || sim==3)
				myFormatter = new DecimalFormat("###.###");
			else
				myFormatter = new DecimalFormat("###.##");

			break;
		case 6:
			if(sim == 2 ) //mass
				myFormatter = new DecimalFormat("###.#");
			else if(sim ==8||sim==7) //mol
				myFormatter = new DecimalFormat("###.#");
			else 
				myFormatter = new DecimalFormat("###");
			break;
			
		}
		return myFormatter;
	}
	
	
	
	public void clearSelection()
	{
		table.clearSelection();
		this.selectedRows = null;
	}
	
	public int getIndexByName(String compoundName)
	{
		int res = data[2].indexOf(compoundName);
		if(res==-1)
			res = data[2].indexOf(compoundName.replace('-',' '));
		return res;
	}
	
	public float getCountByName(String compoundName)
	{
		int index = data[2].indexOf(compoundName);
		if(index>=0 && index< data[2].size())
			return Float.parseFloat((String)data[0].get(index));
		else return 0;
	}
	
	
	class MyTableModel extends AbstractTableModel implements TableModelListener {
		private String[] columnNames = {};
		
		public MyTableModel() {
			data[0] = new ArrayList<Integer>();
			data[1] = new ArrayList<Color>();
			data[2] = new ArrayList<String>();

			columnNames = new String[3];
			columnNames[0] = "    #";
			columnNames[1] = "Color";
			columnNames[2] = "Molecule";
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {
			return data[0].size();
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}
		
//		public void setColumnName( int col, String s){
//			columnNames[col] = new String(s);
//			
//		}

		public Object getValueAt(int row, int col) {
			return data[col].get(row);
		}

		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

		public boolean isCellEditable(int row, int col) {
			if (col == 1) {
				return true;
			} else {
				return false;
			}
		}
/*
		public void setValueAt(Object value, int row, int col) {
			if( col==1)
				data[col].set(row, (Color)value);
			else
			data[col].set(row, value);
			//colors[row] = (Color) value;
			fireTableCellUpdated(row, col);
		}*/

		@Override
		public void tableChanged(TableModelEvent arg0) {
			// TODO Auto-generated method stub
			
		}
	}

	public Main getMain() {
		return main;
	}
	
	public void setColumnName(int col, String s)
	{
		table.getColumnModel().getColumn(col).setHeaderValue(s);
	}
	
	//Return the name list of compounds
	public ArrayList<String> getCompoundNames()
	{
		ArrayList<String> names = new ArrayList<String>();
		for(int i = 0;i<data[2].size();i++)
		{
			names.add((String)data[2].get(i));
		}
		return names;
	}
	
	public int getItemNum()
	{
		return data[2].size();
	}
	 
	public void setColumnWidth(int col, int w)
	{
		table.getColumnModel().getColumn(col).setPreferredWidth(w);
	}

}
