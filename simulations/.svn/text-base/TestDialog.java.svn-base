package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import net.miginfocom.swing.MigLayout;
import java.sql.*;
import java.sql.DriverManager;
import java.util.logging.Logger;
import model.DBinterface;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import model.YAMLinterface;

public class TestDialog extends JDialog {

	private JPanel panel = new JPanel();
	DBinterface dbInterface = new DBinterface();
	YAMLinterface yamlInterface = new YAMLinterface();

	ArrayList compoundNames = dbInterface.getCompoundNames();
	String pngRoot = "/resources/compoundsPng50/";


	/**
	 * Launch the application.
	 */
	public static void main(/*String[] args*/) {
		try {
			TestDialog dialog = new TestDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public TestDialog() {
		setBounds(100, 100, 450, 600);
		getContentPane().setLayout(new MigLayout("", "[grow][150px]", "[grow]"));
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, "cell 0 0,grow");
		
		
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		String panelBrackets = ""; // adds rows to MigLayout
		for (int i = 0; i<compoundNames.size(); i++) {
			panelBrackets.concat("[]");
		}
		
		panel.setLayout(new MigLayout("", "[50%,fill][grow,fill]", "[]"));
		scrollPane.setViewportView(panel);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		getContentPane().add(scrollPane_1, "cell 1 0,grow");
		
		JTextArea txtrThisIsA = new JTextArea();
		
		
		
		
		
		System.out.println(yamlInterface.getSetCompounds(1, 5, 1));
		
		
		
		
		
		/*
		txtrThisIsA.setText(yamlInterface.getSampleText());
		txtrThisIsA.setLineWrap(true);
		txtrThisIsA.setEditable(false);
		scrollPane_1.setViewportView(txtrThisIsA);
		*/
		
		for (int i=0; i<compoundNames.size(); i++) {
			String compoundName = "";
			try {
				compoundName = (String)compoundNames.get(i);
				JLabel label = new JLabel(compoundName);
				panel.add(label, "cell 1 " + Integer.toString(i));
			} catch (Exception e) {
				System.out.println(e);
				System.out.println("Missing compoundName: " + Integer.toString(i));
			}
			try {
				JButton button = new JButton();
				button.setIcon(new ImageIcon(TestDialog.class.getResource(pngRoot + compoundName + ".png")));
				panel.add(button, "cell 0 " + Integer.toString(i));
			} catch (Exception e) {
				System.out.println(e);
				System.out.println("Missing PNG: " + Integer.toString(i));
			}
		}
	}
}
