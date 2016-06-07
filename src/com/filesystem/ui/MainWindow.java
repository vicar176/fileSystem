package com.filesystem.ui;

import java.awt.EventQueue;

import javax.swing.JFrame;

import com.filesystem.function.CommandsExecute;
import com.filesystem.model.VirtualDirectory;
import com.filesystem.model.VirtualFile;

import javax.swing.JTextArea;

import java.awt.BorderLayout;
import java.util.Enumeration;

import javax.swing.JComboBox;
import javax.swing.JTree;
import javax.swing.JPanel;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class MainWindow {

	private JFrame frame;

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
		CommandsExecute ce = new CommandsExecute();
		
		try {
			/*ce.createVirtualDrive(3, 100);
			System.out.println("1. Free memory " + ce.getFreeMemory() + " of " + ce.getTotalMemory());
			ce.createVirtualFile("this is an example", "test", "txt", ce.getVirtualDirectories().get("/"));
			System.out.println("2. Free memory " + ce.getFreeMemory());
			ce.createVirtualFile("this is an example", "test3", "txt", ce.getVirtualDirectories().get("/"));
			System.out.println("3. Free memory " + ce.getFreeMemory());
			ce.createVirtualFile("this is another example", "test2", "txt", ce.getVirtualDirectories().get("/"));
			System.out.println("4. Free memory " + ce.getFreeMemory());
			ce.listDirContent("/");*/
		} catch (Exception e) {
			System.out.print(e.getMessage());
		}
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 717, 429);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		CommandsExecute ce = new CommandsExecute();
		ce.createVirtualDrive(3, 50);
		
		VirtualDirectory rootDir = ce.getVirtualDirectories().get("/");
		
		try {
			ce.createVirtualDirectory("home", rootDir);
			ce.createVirtualDirectory("var", rootDir);
			VirtualDirectory home = ce.getVirtualDirectories().get("/home");
			ce.createVirtualDirectory("varias", home);
			ce.createVirtualDirectory("test", home);
			VirtualDirectory varias = ce.getVirtualDirectories().get("/home/varias");
			ce.createVirtualDirectory("one", varias);
			ce.createVirtualFile("hola esto es un test para ver como guarda sdfad", "test1", "txt", home);
			VirtualFile test1 = home.getFilesList().get("test1.txt");
			ce.moveFile(test1, "/home/varias");
			System.out.println(test1.getPath()); // /home/varias/test1.txt
			
			ce.moveDirectory(varias, "/var"); // /var/varias
			ce.moveFile(test1, "ejemplo.txt"); 
			System.out.println(test1.getPath()); // /var/varias/ejemplo.txt
			VirtualDirectory var = ce.getVirtualDirectories().get("/var");
			ce.moveDirectory(var, "/home");  // /home/var
			System.out.println(test1.getPath()); // /home/var/varias/ejemplo.txt
			System.out.println(var.getPath());
			VirtualDirectory one = ce.getVirtualDirectories().get("/home/var/varias/one");
			System.out.println(one.getPath());
			////REVISAR
			System.out.println("contenido 1: " + ce.verContenido(test1));
			ce.copyVirtualFiles("/home/var/varias/ejemplo.txt", "/home/var/varias/one");
			System.out.println("contenido 2: " + ce.verContenido(test1));
			/*VirtualFile ejemplo = one.getFilesList().get("ejemplo.txt");
			ce.copyVirtualDirectory("/home/var", "/");
			
			System.out.println(ejemplo.getPath());*/
			ce.copyVirtualFileToRealPath(test1.getPath(), "/test");
			System.out.println("contenido 3: " + ce.verContenido(test1));
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		JTree tree = new JTree(rootDir.getNode());
		tree.setSize(200, 100);
		frame.getContentPane().add(tree, BorderLayout.WEST);
	
	}

}
