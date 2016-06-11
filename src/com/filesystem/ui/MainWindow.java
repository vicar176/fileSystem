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
import javax.swing.JScrollPane;
import java.awt.Font;

public class MainWindow {

	private JFrame frame;
	private JTree tree;
	private JTextArea lineaComandos;
	private JTextArea consola;
	private CommandsExecute commandsExecute;
	private JButton btnEjecutar;
	private JLabel path;
	private VirtualDirectory currentDir;
	private JScrollPane scrollPane;
	private JScrollPane scrollPane_1;
	private JScrollPane scrollPane_2;

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
		case "CD":
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
				StringBuilder sb = new StringBuilder();
				Map<String, List<String>> dirContentMap = commandsExecute.listDirContent(currentDir);
				List<String> files = dirContentMap.get("files");
				List<String> directories = dirContentMap.get("directories");
				if(files == null && directories == null){
					sb.append("Directorio vacio");
				}
				if(files != null && files.size() > 0){
					for(String name : files){
						sb.append(name + "\n");
					}
				}
				if(directories != null && directories.size() > 0){
					for(String name : directories){
						sb.append(name + "\n");
					}
				}
				setConsoleText(sb.toString());
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
				lineaComandos.setText("");
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
				lineaComandos.setText("");
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
				lineaComandos.setText("");
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
				lineaComandos.setText("");
			} catch (Exception e) {
				lineaComandos.setText("");
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
				lineaComandos.setText("");
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
				lineaComandos.setText("");
				setConsoleText(e.getMessage());
			}
			break;
		case "FIND":
			try{
				StringBuilder sb = new StringBuilder();
				List<String> resultados = commandsExecute.find(instruccion);
				if(resultados != null) {
					for(String res : resultados){
						sb.append(res + "\n");
					}
				}
				setConsoleText(sb.toString());
				lineaComandos.setText("");
			} catch (Exception e) {
				lineaComandos.setText("");
				setConsoleText(e.getMessage());
			}
			break;
		case "clear":
			setConsoleText("");
			break;
		default:
			setConsoleText("El comando no existe o esta mal escrito");
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
				try{
					String [] instruction = lineaComandos.getText().split(";");
					String command = instruction[0];
					executeCommand(command, instruction[1]);
				}catch(Exception ex){
					setConsoleText("Faltan parametros");
				}
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
		frame.setBounds(100, 100, 858, 477);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		path = new JLabel("");
		path.setToolTipText("Posici\u00F3n Actual");
		path.setBounds(145, 58, 543, 14);
		frame.getContentPane().add(path);

		btnEjecutar = new JButton("Ejecutar Linea");
		btnEjecutar.setBounds(10, 9, 126, 23);
		frame.getContentPane().add(btnEjecutar);
		
		JLabel lblPathActual = new JLabel("Path actual :");
		lblPathActual.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblPathActual.setBounds(54, 58, 81, 14);
		frame.getContentPane().add(lblPathActual);
				
				scrollPane_1 = new JScrollPane();
				scrollPane_1.setBounds(698, 9, 134, 419);
				frame.getContentPane().add(scrollPane_1);
		
				tree = new JTree();
				scrollPane_1.setViewportView(tree);
				tree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("----") {
					{
					}
				}));
				tree.setAutoscrolls(true);
				tree.setExpandsSelectedPaths(true);
						
						scrollPane_2 = new JScrollPane();
						scrollPane_2.setBounds(146, 9, 532, 38);
						frame.getContentPane().add(scrollPane_2);
				
						lineaComandos = new JTextArea();
						scrollPane_2.setViewportView(lineaComandos);
								
								scrollPane = new JScrollPane();
								scrollPane.setBounds(23, 83, 655, 345);
								frame.getContentPane().add(scrollPane);
						
								consola = new JTextArea();
								scrollPane.setViewportView(consola);
								consola.setEditable(false);
		ejecutarComando();
	}
}
