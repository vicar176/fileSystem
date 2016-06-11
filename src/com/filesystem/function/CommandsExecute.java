package com.filesystem.function;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;

import com.filesystem.model.VirtualDirectory;
import com.filesystem.model.VirtualFile;

public class CommandsExecute {

	private static final String VIRTUAL_HARD_DRIVE_FILE = "virtualDrive.bin";
	private static final String ROOT_DIR = "/";

	private int totalMemory;
	private int freeMemory;

	private Map<String, VirtualDirectory> virtualDirectories;
	// private Map<String, VirtualFile> virtualFiles;

	private DefaultMutableTreeNode rootNode;

	public int getTotalMemory() {
		return totalMemory;
	}

	public int getFreeMemory() {
		return freeMemory;
	}

	public Map<String, VirtualDirectory> getVirtualDirectories() {
		return virtualDirectories;
	}

	/*
	 * public Map<String, VirtualFile> getVirtualFiles() { return virtualFiles;
	 * }
	 */

	public DefaultMutableTreeNode getRootNode() {
		return rootNode;
	}

	// sectorSize es el tamaño en Bytes,
	// ej: para un archivo de 4 kB ---> sectors=4, sectorSize=1024
	public VirtualDirectory createVirtualDrive(int sectors, int sectorSize) throws Exception {
		FileOutputStream s = null;
		totalMemory = sectors * sectorSize;
		freeMemory = totalMemory;
		VirtualDirectory root = null;
		try {
			s = new FileOutputStream(VIRTUAL_HARD_DRIVE_FILE);
			byte[] buf = new byte[totalMemory];
			s.write(buf);

			if (this.virtualDirectories == null) {
				this.virtualDirectories = new HashMap<String, VirtualDirectory>();
			}
			/*
			 * if (this.virtualFiles == null){ this.virtualFiles = new
			 * HashMap<String, VirtualFile>(); }
			 */

			root = new VirtualDirectory(ROOT_DIR);
			rootNode = new DefaultMutableTreeNode(ROOT_DIR);
			root.setNode(rootNode);
			virtualDirectories.put(root.getName(), root);

		} catch (FileNotFoundException e) {
			throw new Exception(e.getMessage());
		} catch (IOException e) {
			throw new Exception(e.getMessage());
		} finally {
			try {
				s.flush();
				s.close();
			} catch (IOException e) {
				throw new Exception(e.getMessage());
			}
		}
		return root;
	}

	public void createVirtualFile(String content, String name,
			String extension, VirtualDirectory currentDir) throws Exception {
		if (hardDriveExists()) {
			if (currentDir != null) {
				Map<String, VirtualFile> files = currentDir.getFilesList();
				if (files == null) {
					files = new HashMap<String, VirtualFile>();
				}
				String fileKey = name + "." + extension;
				if (!files.containsKey(fileKey)) {
					try {
						VirtualFile file = internalCreateFile(content, name,
								extension, currentDir);
						files.put(fileKey, file);
						currentDir.setFilesList(files);
						DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(
								fileKey);
						currentDir.getNode().add(newNode);
						file.setNode(newNode);
						// virtualFiles.put(file.getPath(), file);
					} catch (Exception e) {
						throw e;
					}
				} else {
					throw new Exception(
							"El Directorio ya contiene un archivo con el nombre "
									+ fileKey);
				}
			} else {
				throw new Exception("El Directorio Virtual no existes");
			}
		} else {
			throw new Exception("No existe Disco Virtual");
		}
	}

	public VirtualDirectory createVirtualDirectory(String name,
			VirtualDirectory currentDir) throws Exception {
		VirtualDirectory newDirectory = null;
		if (hardDriveExists()) {
			if (currentDir != null) {
				if (currentDir.getDirectoriesList() == null) {
					currentDir
							.setDirectoriesList(new HashMap<String, VirtualDirectory>());
				}
				if (!currentDir.getDirectoriesList().containsKey(name)) {
					newDirectory = new VirtualDirectory(name);
					DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(
							name);
					currentDir.getNode().add(newNode);
					newDirectory.setNode(newNode);
					currentDir.getDirectoriesList().put(name, newDirectory);
					if (!virtualDirectories.containsKey(newDirectory.getPath())) {
						virtualDirectories.put(newDirectory.getPath(),
								newDirectory);
					}
				} else {
					throw new Exception(
							"Ya existe un Directorio con el nombre " + name);
				}
			} else {
				throw new Exception("El Directorio Virtual no existes");
			}
		} else {
			throw new Exception("No existe Disco Virtual");
		}
		return newDirectory;
	}

	public VirtualDirectory changeDirectory(String directoryPath)
			throws Exception {
		if (hardDriveExists()) {
			VirtualDirectory directory = virtualDirectories.get(directoryPath);
			if (directory != null) {
				return directory;
			} else {
				throw new Exception("El Directorio Virtual no existes");
			}
		} else {
			throw new Exception("No existe Disco Virtual");
		}
	}

	@SuppressWarnings("rawtypes")
	public Map<String, List<String>> listDirContent(
			VirtualDirectory currentDirectory) throws Exception {
		Map<String, List<String>> dirContent = null;
		if (hardDriveExists()) {
			if (currentDirectory != null) {
				DefaultMutableTreeNode currentNode = currentDirectory.getNode();
				Enumeration elements = currentNode.children();
				if (elements != null) {
					dirContent = new HashMap<String, List<String>>();
					List<String> directories = null;
					List<String> files = null;
					while (elements.hasMoreElements()) {
						String elementValue = (String) ((DefaultMutableTreeNode) elements
								.nextElement()).getUserObject();
						if (currentDirectory.getFilesList() != null && currentDirectory.getFilesList().containsKey(elementValue)) {
							if (files == null) {
								files = new ArrayList<String>();
							}
							files.add(elementValue);
						} if (currentDirectory.getDirectoriesList() != null && currentDirectory.getDirectoriesList().containsKey(elementValue))  {
							if (directories == null) {
								directories = new ArrayList<String>();
							}
							directories.add(elementValue);
						}
					}
					dirContent.put("files", files);
					dirContent.put("directories", directories);
				}
			} else {
				throw new Exception("El directorio no existe");
			}
		} else {
			throw new Exception("No existe Disco Virtual");
		}
		return dirContent;
	}

	public String verPropiedades(String fileName, VirtualDirectory currentDir) throws Exception {
		VirtualFile file = findFileInDir(fileName, currentDir);
		return file.toString();
	}

	public void actualizaContenido(String fileName, VirtualDirectory currentDir, String content) throws Exception {
		VirtualFile file = findFileInDir(fileName, currentDir);
		file.setContent(content);
	}

	public String verContenido(String fileName, VirtualDirectory currentDir) throws Exception {
		VirtualFile file = findFileInDir(fileName, currentDir);
		return file.getContent();
	}
	
	public String verContenido(VirtualFile file){
		return file.getContent();
	}
	
	public void mover(VirtualDirectory currentDir, String pathFrom, String pathTo) throws Exception{
		Map<String, VirtualDirectory> directories = currentDir.getDirectoriesList();
		Map<String, VirtualFile> files = currentDir.getFilesList();
		
		if(pathFrom.contains(".")){
			if(files != null){
				if(files.containsKey(pathFrom)){
					moveFile(files.get(pathFrom), pathTo);
				} else {
					throw new Exception("El archivo no existe");
				}
			} else {
				throw new Exception("El directorio no contiene archivos");
			}
		} else {
			if(directories != null) {
				if(directories.containsKey(pathFrom)){
					moveDirectory(directories.get(pathFrom), pathTo);
				} else {
					throw new Exception("El directorio no existe");
				}
			} else {
				throw new Exception("El directorio no contiene directorios");
			}
		}
	}

	public void moveFile(VirtualFile file, String destinationPath)
			throws Exception {
		if (hardDriveExists()) {
			String currentPath = file.getPath().replace("/" + file.getName(),"");
			if (currentPath.equals(""))
				currentPath = "/";
			VirtualDirectory currentDirectory = virtualDirectories
					.get(currentPath);
			String fileKey = file.getName();
			// virtualFiles.remove(file.getPath());
			if (destinationPath.contains("/")) {
				VirtualDirectory destinationDirectory = virtualDirectories
						.get(destinationPath);
				if (destinationDirectory != null) {
					// se borra el archivo del directorio actual asi como el
					// nodo del
					// archivo para que ya no apunte al nodo del directorio que
					// era el padre
					currentDirectory.getNode().remove(file.getNode());
					currentDirectory.getFilesList().remove(fileKey);
					// se agrega el archivo en el nuevo directorio y el nodo del
					// archivo apunta al nuevo padre
					Map<String, VirtualFile> destinationFilesList = destinationDirectory
							.getFilesList();
					if (destinationFilesList == null) {
						destinationFilesList = new HashMap<String, VirtualFile>();
					}
					destinationFilesList.put(fileKey, file);
					destinationDirectory.setFilesList(destinationFilesList);
					destinationDirectory.getNode().add(file.getNode());
					// /virtualFiles.put(file.getPath(), file);
				} else {
					throw new Exception("El directorio de destino no existe");
				}
			} else {
				currentDirectory.getFilesList().remove(fileKey);
				file.setName(destinationPath);
				updateFileName(file, destinationPath);
				file.getNode().setUserObject(file.getName());
				currentDirectory.getFilesList().put(destinationPath, file);
				// virtualFiles.put(file.getPath(), file);
			}
		} else {
			throw new Exception("No existe Disco Virtual");
		}
	}

	public void moveDirectory(VirtualDirectory directory, String destinationPath)
			throws Exception {
		if (hardDriveExists()) {
			String parentPath = directory.getPath().replace(
					"/" + directory.getName(), "");
			if (parentPath.equals(""))
				parentPath = "/";
			VirtualDirectory currentParent = virtualDirectories
					.get(parentPath);
			if (destinationPath.contains("/")) {
				VirtualDirectory destinationDirectory = virtualDirectories
						.get(destinationPath);
				if (destinationDirectory != null) {
					// remueve este directorio del actual padre
					currentParent.getDirectoriesList().remove(
							directory.getName());
					// remueve de directories todos las referencias del
					// directorio actual asi como todos los hijos
					removeOldDirectoriesPath(directory);
					if (destinationDirectory.getDirectoriesList() == null) {
						destinationDirectory
								.setDirectoriesList(new HashMap<String, VirtualDirectory>());
					}
					destinationDirectory.getDirectoriesList().put(
							directory.getName(), directory);
					destinationDirectory.getNode().add(directory.getNode());
					addNewDirectoriesPath(directory);
					Map<String, VirtualFile> files = directory.getFilesList();
					int cantFiles = 0;
					if (files != null) {
						cantFiles = files.size();
						for (VirtualFile file : files.values()) {
							moveFile(file, directory.getPath());
						}
					}
				} else {
					throw new Exception("El directorio de destino no existe");
				}
			} else {
				removeOldDirectoriesPath(directory);
				currentParent.getDirectoriesList().remove(
						directory.getName());
				directory.setName(destinationPath);
				directory.getNode().setUserObject(destinationPath);
				currentParent.getDirectoriesList().put(directory.getName(), directory);
				addNewDirectoriesPath(directory);
			}
		} else {
			throw new Exception("No existe Disco Virtual");
		}
	}
	
	public void copy(VirtualDirectory currentDir, String from, String to) throws Exception{
		if(from.contains(".") || to.contains(".")){
			if(from.contains(":") && !to.contains(":")){
				if(currentDir.getDirectoriesList() != null){
					VirtualDirectory dir = currentDir.getDirectoriesList().get(to);
					File fileEntry = new File(from);
					copyRealFileToVirtual(fileEntry, dir);
				} else if(virtualDirectories.containsKey(to)){
					VirtualDirectory dir = virtualDirectories.get(to);
					File fileEntry = new File(from);
					copyRealFileToVirtual(fileEntry, dir);
				} else {
					throw new Exception("El directorio no existe");
				}
			} else if(!from.contains(":") && to.contains(":")) {
				if(currentDir.getDirectoriesList() != null){
					VirtualFile file = currentDir.getFilesList().get(from);
					if(file != null){
						copyVirtualFileToRealPath(file, to);
					} else {
						throw new Exception("El archivo no existe");
					}
				}
			} else {
				if(currentDir.getDirectoriesList() != null){
					VirtualFile file = currentDir.getFilesList().get(from);
					if(file != null){
						copyVirtualFiles(file, to);
					} else {
						throw new Exception("El archivo no existe");
					}
				}
			}
		} else {
			if(from.contains(":") && !to.contains(":")){
				if(currentDir.getDirectoriesList() != null){
					VirtualDirectory dir = currentDir.getDirectoriesList().get(to);
					File fileEntry = new File(from);
					copyRealDirToVirtual(fileEntry, dir);
				} else if(virtualDirectories.containsKey(to)){
					VirtualDirectory dir = virtualDirectories.get(to);
					File fileEntry = new File(from);
					copyRealDirToVirtual(fileEntry, dir);
				} else {
					throw new Exception("El directorio no existe");
				}
			} else if(!from.contains(":") && to.contains(":")) {
				if(currentDir.getDirectoriesList() != null){
					VirtualDirectory dir = currentDir.getDirectoriesList().get(from);
					if(dir != null){
						copyVirtualDirToRealPath(dir, to);
					} else {
						throw new Exception("El archivo no existe");
					}
				}
			} else {
				if(currentDir.getDirectoriesList() != null){
					VirtualDirectory dir = currentDir.getDirectoriesList().get(from);
					if(dir != null){
						copyVirtualDirectory(dir, to);
					} else {
						throw new Exception("El archivo no existe");
					}
				}
			}
		}
	}

	public void copyVirtualFileToRealPath(VirtualFile file, String to) throws Exception {
		/*
		String[] dirArray = to.split("\\");
		
		String pathFisico = "";
		for(int i=0; i<dirArray.length; i++){
			pathFisico += dirArray[i] + File.separator;
		}
		
		String[] splitArray = from.split("/");
		String fileName = splitArray[splitArray.length - 1];
		String dirName = from.replace("/" + fileName, "");
		if (dirName.equals("")) {
			dirName = "/";
		}
		VirtualDirectory current = virtualDirectories.get(dirName);
		VirtualFile file = current.getFilesList().get(fileName);*/
		InputStream is = null;
		OutputStream os = null;
		try {
			//File f = new File(pathFisico + fileName);
			File f = new File(to + File.separator + file.getName());
			is = file.getContentStream();
			os = new FileOutputStream(f);
			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = is.read(bytes)) != -1) {
				os.write(bytes, 0, read);
			}
		} catch (FileNotFoundException e) {
			System.out.println(e);
			throw new Exception(e);
		} catch (IOException e) {
			System.out.println(e);
			throw new Exception(e);
		} finally {
			try {
				is.close();
				os.close();
			} catch (IOException e) {
				System.out.println(e);
				throw new Exception(e);
			}
		}
	}

	public void copyVirtualFiles(VirtualFile file, String to) throws Exception {
		/*String[] splitArray = from.split("/");
		String fileName = splitArray[splitArray.length - 1];
		String dirName = from.replace("/" + fileName, "");
		if (dirName.equals("")) {
			dirName = "/";
		}
		VirtualDirectory current = virtualDirectories.get(dirName);*/
		//VirtualFile file = currentDir.getFilesList().get(fileName);
		VirtualDirectory directory = virtualDirectories.get(to);
		String fileName = file.getSimpleName();
		if (directory.getFilesList() != null && directory.getFilesList().containsKey(file.getName())) {
			fileName += "-copy";
		} 
		createVirtualFile(file.getContent(), fileName, file.getExtension(), directory);
	}
	
	public void copyRealFileToVirtual(File fileEntry, VirtualDirectory currentDir) throws Exception {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(fileEntry);
			String [] fileName = fileEntry.getName().split("\\.");
        	
        	int content;
        	StringBuilder sb = new StringBuilder();
        	try {
				while ((content = fis.read()) != -1) {
					sb.append((char) content);
				}
			} catch (IOException e) {
				System.out.println(e);
				e.printStackTrace();
			}
        	createVirtualFile(sb.toString(), fileName[0], fileName[1], currentDir); 
		} catch (FileNotFoundException e) {
			System.out.println(e);
			throw new Exception(e);
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				System.out.println(e);
			}
		}
	}
	
	public void copyVirtualDirToRealPath(VirtualDirectory currentDir, String to) throws Exception{
		
		File realDir = new File(to + File.separator + currentDir.getName());
		realDir.mkdir();
		
		if(currentDir.getFilesList() != null){
			List<VirtualFile> filesList = new ArrayList<VirtualFile>(currentDir.getFilesList().values());
			for (VirtualFile file : filesList) {
				copyVirtualFileToRealPath(file, realDir.getPath());
			}
		}
		
		if(currentDir.getDirectoriesList() != null){
			List<VirtualDirectory> directoriesList = new ArrayList<VirtualDirectory>(currentDir.getDirectoriesList().values());
			for(VirtualDirectory directory : directoriesList){
				copyVirtualDirToRealPath(directory, realDir.getPath());
			}
		}
	}
	
	public void copyRealDirToVirtual(final File folder, VirtualDirectory currentDir) throws Exception {
		for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	        	copyRealDirToVirtual(fileEntry, createVirtualDirectory(fileEntry.getName(), currentDir));
	        } else {
	        	copyRealFileToVirtual(fileEntry, currentDir);
	        }
	    }
	}

	public void copyVirtualDirectory(VirtualDirectory currentDir, String to) throws Exception {
		VirtualDirectory dirTo = virtualDirectories.get(to);
		if (dirTo.getDirectoriesList().containsKey(currentDir.getName())) {
			String dirName = currentDir.getName() + "-copy";
			createDirectoriesInDirectory(currentDir, createVirtualDirectory(dirName, dirTo));
		} else {
			createDirectoriesInDirectory(currentDir, dirTo);
		}
	}

	private void createDirectoriesInDirectory(VirtualDirectory dirForCopy,
			VirtualDirectory parentDir) throws Exception {
		VirtualDirectory newDir = createVirtualDirectory(dirForCopy.getName(),
				parentDir);

		List<VirtualDirectory> directoriesList = null;
		List<VirtualFile> filesList = null;
		if (dirForCopy.getDirectoriesList() != null) {
			directoriesList = new ArrayList<VirtualDirectory>(dirForCopy
					.getDirectoriesList().values());
		}
		if (dirForCopy.getFilesList() != null) {
			filesList = new ArrayList<VirtualFile>(dirForCopy.getFilesList()
					.values());
		}
		if (directoriesList != null) {
			for (VirtualDirectory dir : directoriesList) {
				createDirectoriesInDirectory(dir, newDir);
			}
		}
		if (filesList != null) {
			for (VirtualFile file : filesList) {
				copyVirtualFiles(file, newDir.getPath());
			}
		}
	}
	
	public void remove(VirtualDirectory currentDirectory, String name) throws Exception{
		if(name.contains(".")){
			removeVirtualFile(currentDirectory, name);
		} else {
			removeVirtualDirectory(currentDirectory, name);
		}
	}
	
	private void removeVirtualFile(VirtualDirectory currentDirectory, String fileName) throws Exception{
		if(currentDirectory.getFilesList() != null){
			VirtualFile file = currentDirectory.getFilesList().get(fileName);
			removeVirtualFile(file, currentDirectory);
		} else {
			throw new Exception("El archivo no existe");
		}
	}
	
	private void removeVirtualFile(VirtualFile file, VirtualDirectory currentDirectory) {
		currentDirectory.getFilesList().remove(file.getName());
		currentDirectory.getNode().remove(file.getNode());
		freeMemory += file.getSize();		
	}
	
	private void removeVirtualDirectory(VirtualDirectory currentDir, String dirName) throws Exception{
		VirtualDirectory dir = null;
		if(currentDir.getDirectoriesList().containsKey(dirName)){
			dir = currentDir.getDirectoriesList().get(dirName);
			removeDirectoriesFiles(dir);
			removeOldDirectoriesPath(dir);
			currentDir.getDirectoriesList().remove(dirName);
			currentDir.getNode().remove(dir.getNode());
		} else {
			throw new Exception("El directorio no existe");
		}
	}
	
	private void removeDirectoriesFiles(VirtualDirectory dirToRemove) throws Exception{
		List<VirtualDirectory> directoriesList = null;
		List<VirtualFile> filesList = null;
		if (dirToRemove.getFilesList() != null) {
			filesList = new ArrayList<VirtualFile>(dirToRemove.getFilesList().values());
			for (VirtualFile file : filesList) {
				removeVirtualFile(file, dirToRemove);
			}
		}
		if (dirToRemove.getDirectoriesList() != null) {
			directoriesList = new ArrayList<VirtualDirectory>(dirToRemove.getDirectoriesList().values());
			for (VirtualDirectory dir : directoriesList) {
				removeDirectoriesFiles(dir);
			}
		}
	}

	private void removeOldDirectoriesPath(VirtualDirectory directory) {
		virtualDirectories.remove(directory.getPath());
		List<VirtualDirectory> directoriesList = null;
		if (directory.getDirectoriesList() != null) {
			directoriesList = new ArrayList<VirtualDirectory>(directory
					.getDirectoriesList().values());
		}
		if (directoriesList != null) {
			for (VirtualDirectory dir : directoriesList) {
				removeOldDirectoriesPath(dir);
			}
		}
	}

	private void addNewDirectoriesPath(VirtualDirectory directory) {
		virtualDirectories.put(directory.getPath(), directory);
		List<VirtualDirectory> directoriesList = null;
		if (directory.getDirectoriesList() != null) {
			directoriesList = new ArrayList<VirtualDirectory>(directory
					.getDirectoriesList().values());
		}
		if (directoriesList != null) {
			for (VirtualDirectory dir : directoriesList) {
				addNewDirectoriesPath(dir);
			}
		}
	}

	private boolean hardDriveExists() {
		boolean exists = false;
		File f = new File(VIRTUAL_HARD_DRIVE_FILE);
		if (f.exists() && !f.isDirectory()) {
			exists = true;
		}
		return exists;
	}

	private VirtualFile internalCreateFile(String content, String name,
			String extension, VirtualDirectory currentDir) throws Exception {
		VirtualFile file = null;
		try {
			int size = fileSize(new ByteArrayInputStream(
					content.getBytes(StandardCharsets.UTF_8)));
			if (size < freeMemory) {
				this.freeMemory -= size;
				Date currentDate = new Date();
				file = new VirtualFile();
				file.setName(name + "." + extension);
				file.setSimpleName(name);
				file.setExtension(extension);
				file.setCreatedDate(currentDate);
				file.setUpdatedDate(currentDate);
				file.setContent(content);
				file.setSize(size);
			} else {
				System.out.println("No hay suficiente espacio en el Disco Virtual");
				throw new Exception(
						"No hay suficiente espacio en el Disco Virtual");
			}
		} catch (IOException e) {
			System.out.println(e);
			throw new Exception(e.getMessage());
		}
		return file;
	}

	private int fileSize(InputStream stream) throws IOException {
		int size = 0;
		if (stream instanceof ByteArrayInputStream) {
			size = stream.available();
		}
		return size;
	}

	private void updateFileName(VirtualFile file, String newName) {
		file.setName(newName);
		String[] nameWithExt = newName.split("\\.");
		file.setSimpleName(nameWithExt[0]);
		file.setExtension(nameWithExt[1]);
	}
	
	private VirtualFile findFileInDir(String fileName, VirtualDirectory currentDir) throws Exception{
		VirtualFile file = null;
		if (hardDriveExists()) {
			if (currentDir != null) {
				if(currentDir.getFilesList() != null){
					file = currentDir.getFilesList().get(fileName);
					if(file == null){
						throw new Exception("El archivo no existe");
					}
				}
			} else {
				throw new Exception("El directorio no existe");
			}
		} else {
			throw new Exception("No existe Disco Virtual");
		}
		return file;
	}
}