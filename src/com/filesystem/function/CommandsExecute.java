package com.filesystem.function;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.ArrayList;
import java.util.Collection;
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
	//private Map<String, VirtualFile> virtualFiles;

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
	
	/*public Map<String, VirtualFile> getVirtualFiles() {
		return virtualFiles;
	}*/

	public DefaultMutableTreeNode getRootNode() {
		return rootNode;
	}

	// sectorSize es el tamaño en Bytes,
	// ej: para un archivo de 4 kB ---> sectors=4, sectorSize=1024
	public VirtualDirectory createVirtualDrive(int sectors, int sectorSize) {
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
			/*if (this.virtualFiles == null){
				this.virtualFiles = new HashMap<String, VirtualFile>();
			}*/

			root = new VirtualDirectory(ROOT_DIR);
			rootNode = new DefaultMutableTreeNode(ROOT_DIR);
			root.setNode(rootNode);
			virtualDirectories.put(root.getName(), root);

		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				s.flush();
				s.close();
			} catch (IOException e) {
				System.out.println(e.getMessage());
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
					try{
						VirtualFile file = internalCreateFile(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)),name, extension, currentDir);
						files.put(fileKey, file);
						currentDir.setFilesList(files);
						DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(fileKey);
						currentDir.getNode().add(newNode);
						file.setNode(newNode);
						//virtualFiles.put(file.getPath(), file);
					} catch (Exception e) {
						throw e;
					}
				} else {
					throw new Exception("El Directorio ya contiene un archivo con el nombre " + fileKey);
				}
			} else {
				throw new Exception("El Directorio Virtual no existes");
			}
		} else {
			throw new Exception("No existe Disco Virtual");
		}
	}

	public VirtualDirectory createVirtualDirectory(String name, VirtualDirectory currentDir)
			throws Exception {
		VirtualDirectory newDirectory = null;
		if (hardDriveExists()) {
			if (currentDir != null) {
				if(currentDir.getDirectoriesList() == null){
					currentDir.setDirectoriesList(new HashMap<String, VirtualDirectory>());
				}
				if(!currentDir.getDirectoriesList().containsKey(name)){
					newDirectory = new VirtualDirectory(name);
					DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(name);
					currentDir.getNode().add(newNode);
					newDirectory.setNode(newNode);
					currentDir.getDirectoriesList().put(name, newDirectory);
					if (!virtualDirectories.containsKey(newDirectory.getPath())) {
						virtualDirectories.put(newDirectory.getPath(), newDirectory);
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

	public VirtualDirectory changeDirectory(String directoryPath) throws Exception {
		if(hardDriveExists()) {
			VirtualDirectory directory = virtualDirectories.get(directoryPath);
			if(directory != null) {
				return directory;
			} else {
				throw new Exception("El Directorio Virtual no existes");
			}
		} else {
			throw new Exception("No existe Disco Virtual");
		}
	}

	@SuppressWarnings("rawtypes")
	public Map<String, List<String>> listDirContent(String directoryName) throws Exception {
		Map<String, List<String>> dirContent = null;
		if (hardDriveExists()) {
			VirtualDirectory currentDirectory = virtualDirectories.get(directoryName);
			if (currentDirectory != null) {
				DefaultMutableTreeNode currentNode = currentDirectory.getNode();
				Enumeration elements = currentNode.children();
				if(elements != null){
					dirContent = new HashMap<String, List<String>>();
					List<String> directories = null;
					List<String> files = null;
					while(elements.hasMoreElements()){
						String elementValue = (String) elements.nextElement();
						if(currentDirectory.getFilesList().containsKey(elementValue)){
							if(files == null){
								files = new ArrayList<String>();
							}
							files.add(elementValue);
						} else{
							if(directories == null){
								directories = new ArrayList<String>();
							}
							directories.add(elementValue);
						}
					}
					dirContent.put("files", files);
					dirContent.put("directories", directories);
				}
			} else {
				System.out.println("El directorio no existe");
			}
		} else {
			throw new Exception("No existe Disco Virtual");
		}
		return dirContent;
	}
	
	public String verPropiedades(VirtualFile file){
		return file.toString();
	}
	
	public void actualizaContenido(VirtualFile file, String content){
		file.setContent(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
	}
	
	public String verContenido(VirtualFile file){
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		String line;
		try {
			br = new BufferedReader(new InputStreamReader(file.getContent()));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}
	
	public void moveFile(VirtualFile file, String destinationPath) throws Exception{
		if (hardDriveExists()) {
			String currentPath = file.getPath().replace("/" + file.getName(), "");
			VirtualDirectory currentDirectory = virtualDirectories.get(currentPath);
			String fileKey = file.getName();
			//virtualFiles.remove(file.getPath());
			if(destinationPath.contains("/")){
				VirtualDirectory destinationDirectory = virtualDirectories.get(destinationPath);
				if(destinationDirectory != null){
					// se borra el archivo del directorio actual asi como el nodo del 
					// archivo para que ya no apunte al nodo del directorio que era el padre
					currentDirectory.getNode().remove(file.getNode());
					currentDirectory.getFilesList().remove(fileKey);
					// se agrega el archivo en el nuevo directorio y el nodo del archivo apunta al nuevo padre
					Map<String, VirtualFile> destinationFilesList = destinationDirectory.getFilesList();
					if(destinationFilesList == null){
						destinationFilesList = new HashMap<String, VirtualFile>();
					}
					destinationFilesList.put(fileKey, file);
					destinationDirectory.setFilesList(destinationFilesList);
					destinationDirectory.getNode().add(file.getNode());
					///virtualFiles.put(file.getPath(), file);
				} else {
					System.out.println("El directorio de destino no existe");
				}
			} else {
				currentDirectory.getFilesList().remove(fileKey);
				file.setName(destinationPath);
				updateFileName(file, destinationPath);
				file.getNode().setUserObject(file.getName());
				currentDirectory.getFilesList().put(destinationPath, file);
				//virtualFiles.put(file.getPath(), file);
			}
		} else {
			throw new Exception("No existe Disco Virtual");
		}
	}
	
	public void moveDirectory(VirtualDirectory directory, String destinationPath) throws Exception{
		if (hardDriveExists()) {
			if(destinationPath.contains("/")){
				VirtualDirectory destinationDirectory = virtualDirectories.get(destinationPath);
				if(destinationDirectory != null){
					// remueve este directorio del actual padre
					String parentPath = directory.getPath().replace("/" + directory.getName(), "");
					if(parentPath.equals(""))
						parentPath = "/";
					VirtualDirectory currentParent = virtualDirectories.get(parentPath);
					currentParent.getDirectoriesList().remove(directory.getName());
					// remueve de directories todos las referencias del directorio actual asi como todos los hijos
					removeOldDirectoriesPath(directory);
					if(destinationDirectory.getDirectoriesList() == null){
						destinationDirectory.setDirectoriesList(new HashMap<String, VirtualDirectory>());
					}
					destinationDirectory.getDirectoriesList().put(directory.getName(), directory);
					destinationDirectory.getNode().add(directory.getNode());
					addNewDirectoriesPath(directory);
					Map<String, VirtualFile> files = directory.getFilesList();
					int cantFiles = 0;
					if(files != null){
						cantFiles = files.size();
						for(VirtualFile file : files.values()){
							moveFile(file, directory.getPath());
						}
					}
				} else {
					System.out.println("El directorio de destino no existe");
				}
			} else {
				
			}
		} else {
			throw new Exception("No existe Disco Virtual");
		}
	}
	
	public void copyFile(String from, String to){
		
		/*if(virtualFiles.containsKey(from) && virtualDirectories.containsKey(to)){
		
		} else if(virtualFiles.containsKey(from) && !virtualDirectories.containsKey(to)){
			
		} else if(!virtualFiles.containsKey(from) && !virtualDirectories.containsKey(to)) {
			
		}*/
	}
	
	public void copyDirectory(String from, String to){
		
	}

	// / PRIVATE METHODS
	
	public void copyVirtualFiles(String from, String to) throws Exception{
		String [] splitArray = from.split("/");
		String fileName = splitArray[splitArray.length - 1];
		String dirName = from.replace("/" + fileName, "");
		if(dirName.equals("")){
			dirName = "/";
		}
		VirtualDirectory current = virtualDirectories.get(dirName);
		VirtualFile file = current.getFilesList().get(fileName);
		VirtualDirectory directory = virtualDirectories.get(to);
		if(directory.getFilesList() != null && directory.getFilesList().containsKey(file.getName())){
			
		} else {
			String content = verContenido(file);
			createVirtualFile(content, file.getSimpleName(), file.getExtension(), directory);
		}
	}
	
	public void copyVirtualDirectory(String from, String to) throws Exception{
		VirtualDirectory dirFrom = virtualDirectories.get(from);
		VirtualDirectory dirTo = virtualDirectories.get(to);
		if(dirTo.getDirectoriesList().containsKey(dirFrom.getName())){
			
		} else {
			createDirectoriesInDirectory(dirFrom, dirTo);
		}	
	}
	
	private void createDirectoriesInDirectory(VirtualDirectory dirForCopy, VirtualDirectory parentDir) throws Exception{
		VirtualDirectory newDir = createVirtualDirectory(dirForCopy.getName(), parentDir);
		
		List<VirtualDirectory> directoriesList = null;
		List<VirtualFile> filesList = null;
		if(dirForCopy.getDirectoriesList() != null){
			directoriesList = new ArrayList<VirtualDirectory>(dirForCopy.getDirectoriesList().values());
		}
		if(dirForCopy.getFilesList() != null){
			filesList = new ArrayList<VirtualFile>(dirForCopy.getFilesList().values());
		}
		if(directoriesList != null){
			for(VirtualDirectory dir : directoriesList){
				createDirectoriesInDirectory(dir, newDir);
			}
		}
		if(filesList != null){
			for(VirtualFile file : filesList){
				copyVirtualFiles(file.getPath(), newDir.getPath());
			}
		}
	}
	
	private void removeOldDirectoriesPath(VirtualDirectory directory){
		virtualDirectories.remove(directory.getPath());
		List<VirtualDirectory> directoriesList = null;
		if(directory.getDirectoriesList() != null){
			directoriesList = new ArrayList<VirtualDirectory>(directory.getDirectoriesList().values());
		}
		if(directoriesList != null){
			for(VirtualDirectory dir : directoriesList){
				removeOldDirectoriesPath(dir);
			}
		}
	}
	
	private void addNewDirectoriesPath(VirtualDirectory directory){
		virtualDirectories.put(directory.getPath(), directory);
		List<VirtualDirectory> directoriesList = null;
		if(directory.getDirectoriesList() != null){
			directoriesList = new ArrayList<VirtualDirectory>(directory.getDirectoriesList().values());
		}
		if(directoriesList != null){
			for(VirtualDirectory dir : directoriesList){
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

	private VirtualFile internalCreateFile(InputStream contentStream,
			String name, String extension, VirtualDirectory currentDir) throws Exception {
		VirtualFile file = null;
		try {
			int size = fileSize(contentStream);
			if(size < freeMemory){
				this.freeMemory -= size;
				Date currentDate = new Date();
				file = new VirtualFile();
				file.setName(name + "." + extension);
				file.setSimpleName(name);
				file.setExtension(extension);
				file.setCreatedDate(currentDate);
				file.setUpdatedDate(currentDate);
				file.setContent(contentStream);
				file.setSize(size);
			} else {
				throw new Exception(
						"No hay suficiente espacio en el Disco Virtual");
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
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
	
	private void updateFileName(VirtualFile file, String newName){
		file.setName(newName);
		String [] nameWithExt = newName.split("\\.");
		file.setSimpleName(nameWithExt[0]);
		file.setExtension(nameWithExt[1]);
	}
	
}
