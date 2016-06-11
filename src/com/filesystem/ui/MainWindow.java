package com.filesystem.ui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;

import com.filesystem.function.CommandsExecute;
import com.filesystem.model.VirtualDirectory;
import com.filesystem.model.VirtualFile;

public class MainWindow {

	private JFrame frame;
	private JTree tree;
	private JComboBox comboBox;
	private JTextArea lineaComandos;
	private JTextArea consola;
	private CommandsExecute commandsExecute;
	private JButton btnEjecutar;
	private JLabel path;
	private VirtualDirectory currentDir;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	public MainWindow() {
		initialize();
	}

	public void executeCommand(String command, String instruccion) {
		String [] values = null;
		switch (command) {
		case "CREATE":
			values = instruccion.split(",");
			try{
				currentDir = commandsExecute.createVirtualDrive(Integer.parseInt(values[0]), Integer.parseInt(values[1]));
				setConsoleText("Disco Virtual creado"); 
				setNodesToTree(currentDir.getNode());
				path.setText(currentDir.getPath());
				lineaComandos.setText("");
			} catch (Exception e) {
				setConsoleText("Error al crear Disco Virtual, Disco Virtual no creado");
			}
			break;
		case "FILE":
			values = instruccion.split(",");
			try {
				String name = values [0];
				String ext = values [1];
				String content = values [2];
				commandsExecute.createVirtualFile(content, name, ext, currentDir);
				setNodesToTree((DefaultMutableTreeNode)currentDir.getNode().getRoot());
				setConsoleText("Archivo creado");
				path.setText(currentDir.getPath());
				lineaComandos.setText("");
			} catch (Exception e) {
				setConsoleText(e.getMessage());
			}
			break;
		case "MKDIR":
			try{
				currentDir = commandsExecute.createVirtualDirectory(instruccion, currentDir);
				setNodesToTree((DefaultMutableTreeNode)currentDir.getNode().getRoot());
				setConsoleText("Directorio creado");
				path.setText(currentDir.getPath());
				lineaComandos.setText("");
			} catch (Exception e) {
				setConsoleText(e.getMessage());
			}
			break;
		case "CDIR":
		case "CambiarDIR":
			try{
				currentDir = commandsExecute.changeDirectory(instruccion);
				path.setText(currentDir.getPath());
				lineaComandos.setText("");
			} catch (Exception e) {
				setConsoleText(e.getMessage());
			}
			break;
		case "LDIR":
		case "ListarDIR":
			try{
				Map<String, List<String>> dirContentMap = commandsExecute.listDirContent(currentDir);
				List<String> files = dirContentMap.get("files");
				List<String> directories = dirContentMap.get("directories");
				if(files == null && directories == null){
					setConsoleText("Directorio vacio");
				}
				if(files != null && files.size() > 0){
					setConsoleText("Archivos");
					for(String name : files){
						setConsoleText(name);
					}
				}
				if(directories != null && directories.size() > 0){
					setConsoleText("Directorios");
					for(String name : directories){
						setConsoleText(name);
					}
				}
			} catch (Exception e) {
				setConsoleText(e.getMessage());
			}
			break;
		case "MFILE":
		case "ModFILE":
			values = instruccion.split(",");
			try{
				String name = values [0];
				String content = values [1];
			} catch (Exception e) {
				setConsoleText(e.getMessage());
			}
			break;
		case "VP":
		case "VerPropiedades":
			try{
				String output = commandsExecute.verPropiedades(instruccion, currentDir);
				setConsoleText(output);
				lineaComandos.setText("");
			} catch (Exception e) {
				setConsoleText(e.getMessage());
			}
			break;
		case "CF":
		case "ContFile":
			try{
				String output = commandsExecute.verContenido(instruccion, currentDir);
				setConsoleText(output);
				lineaComandos.setText("");
			} catch (Exception e) {
				setConsoleText(e.getMessage());
			}
			break;
		case "CP":
		case "CoPY":
			try{
				//para copiar hay que estar en el directorio donde esta el archivo que se quiere copiar
				//o directorio a donde se quiere copiar
				String []paths = instruccion.split(","); 
				commandsExecute.copy(currentDir, paths[0], paths[1]);
				setNodesToTree((DefaultMutableTreeNode)currentDir.getNode().getRoot());
			} catch (Exception e) {
				setConsoleText(e.getMessage());
			}
			break;
		case "MV":
		case "MoVer":
			values = instruccion.split(",");
			try{
				String from = values [0];
				String to = values [1];
				commandsExecute.mover(currentDir, from, to);
				setNodesToTree((DefaultMutableTreeNode)currentDir.getNode().getRoot());
				lineaComandos.setText("");
			} catch (Exception e) {
				setConsoleText(e.getMessage());
			}
			break;
		case "RM":
		case "ReMove":
			try{
				commandsExecute.remove(currentDir, instruccion);
				setNodesToTree((DefaultMutableTreeNode)currentDir.getNode().getRoot());
				lineaComandos.setText("");
			} catch (Exception e) {
				setConsoleText(e.getMessage());
			}
			break;
		case "FIND":
			try{
				
				
			} catch (Exception e) {
				setConsoleText(e.getMessage());
			}
			break;
		}
	}

	private void setConsoleText(String output) {
		String consoleOutput;
		consoleOutput = consola.getText();
		consola.setText(output + "\n" + consoleOutput);
	}
	
	private void ejecutarComando(){
		btnEjecutar.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				String instruction = lineaComandos.getText();
				String command = (String) comboBox.getSelectedItem();
				executeCommand(command, instruction);
			}
			
		});
	}

	private void setNodesToTree(DefaultMutableTreeNode node) {
		tree.setModel(new DefaultTreeModel(node));
	}

	@SuppressWarnings({ "unchecked", "serial", "rawtypes" })
	private void initialize() {
		
		commandsExecute = new CommandsExecute();
		
		frame = new JFrame();
		frame.setBounds(100, 100, 809, 456);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		tree = new JTree();
		tree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("----") {
			{
			}
		}));
		tree.setBounds(677, 11, 106, 396);
		tree.setAutoscrolls(true);
		tree.setExpandsSelectedPaths(true);
		frame.getContentPane().add(tree);

		path = new JLabel("");
		path.setToolTipText("Posici\u00F3n Actual");
		path.setBounds(118, 58, 543, 14);
		frame.getContentPane().add(path);

		comboBox = new JComboBox();
		comboBox.setToolTipText("Comandos");
		comboBox.setBounds(11, 9, 97, 20);
		comboBox.addItem("CREATE");
		comboBox.addItem("FILE");
		comboBox.addItem("MKDIR");
		comboBox.addItem("CambiarDIR");
		comboBox.addItem("ListarDIR");
		comboBox.addItem("ModFILE");
		comboBox.addItem("VerPropiedades");
		comboBox.addItem("ContFile");
		comboBox.addItem("CoPY");
		comboBox.addItem("MoVer");
		comboBox.addItem("ReMove");
		comboBox.addItem("FIND");
		frame.getContentPane().add(comboBox);

		consola = new JTextArea();
		consola.setEditable(false);
		consola.setBounds(10, 81, 657, 326);
		frame.getContentPane().add(consola);

		lineaComandos = new JTextArea();
		lineaComandos.setBounds(118, 7, 544, 40);
		frame.getContentPane().add(lineaComandos);

		btnEjecutar = new JButton("Ejecutar");
		btnEjecutar.setBounds(19, 49, 89, 23);
		frame.getContentPane().add(btnEjecutar);
		ejecutarComando();
	}

}
