package com.filesystem.model;

import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

public class VirtualDirectory {

	private String name;
	private String path;
	private Map<String, VirtualFile> filesList;
	private Map<String, VirtualDirectory> directoriesList;
	private DefaultMutableTreeNode node;
	
	public VirtualDirectory(String name){
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getPath() {
		path = "";
		TreeNode [] array = node.getPath();
		for(int i=0; i<array.length; i++){
			path += (String)((DefaultMutableTreeNode) array[i]).getUserObject();
			if(i>0 && i<array.length -1){
				path += "/";
			}
		}
		return path;
	}
	
	/*public void setPath(String path) {
		this.path = path;
	}*/

	public Map<String, VirtualFile> getFilesList() {
		return filesList;
	}

	public void setFilesList(Map<String, VirtualFile> filesList) {
		this.filesList = filesList;
	}
	
	public Map<String, VirtualDirectory> getDirectoriesList() {
		return directoriesList;
	}
	
	public void setDirectoriesList(Map<String, VirtualDirectory> directoriesList) {
		this.directoriesList = directoriesList;
	}
	
	public DefaultMutableTreeNode getNode() {
		return node;
	}
	
	public void setNode(DefaultMutableTreeNode node) {
		this.node = node;
	}

}
