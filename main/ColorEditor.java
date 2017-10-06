package main;

import javax.swing.AbstractCellEditor;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableCellEditor;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTable;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ColorEditor extends AbstractCellEditor
                         implements TableCellEditor,
			            ActionListener,ChangeListener {
	Color currentColor;
    JColorChooser colorChooser;
    JDialog dialog;
    protected static final String EDIT = "edit";
    private JPanel tableView;

    public ColorEditor(JPanel parent) {
    	tableView = parent;
            colorChooser = new JColorChooser();
      dialog = JColorChooser.createDialog(null,
                                        "Pick a Color",
                                        true,  //modal
                                        colorChooser,
                                        this,  //OK button handler
                                        this); //CANCEL button handler
        colorChooser.getSelectionModel().addChangeListener(this);
       
    }
    //Implement the one CellEditor method that AbstractCellEditor doesn't.
    public Object getCellEditorValue() {
    	 return currentColor;
    }
 
    //Implement the one method defined by TableCellEditor.
    public Component getTableCellEditorComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 int row,
                                                 int column) {
    	// Can not do TableView.UpdateUI 
    	getTableView().stopUpdating =true;
    	getTableView().colorChangingRow =row;
    	getTableView().table.addRowSelectionInterval(row, row);
    	dialog.setLocation(((TableView)tableView).getMain().mainFrame.getSize().width-430-300, 80);
    	dialog.setVisible(true);
        currentColor = (Color)value;
        return null;
    }

	public void stateChanged(ChangeEvent e) {
		currentColor = colorChooser.getColor();
		getTableView().table.setValueAt(currentColor, getTableView().colorChangingRow, 1);
	
	}

	public void actionPerformed(ActionEvent e) {
		// Release TableView.UpdateUI 
		getTableView().stopUpdating =false;

	}
	private TableView getTableView()
	{
		return (TableView)tableView;
	}
}

