package com.filesystem.model;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import com.ibm.icu.text.SimpleDateFormat;

public class VirtualFile {

	private String name;
	private String simpleName;
	private String path;
	private String extension;
	private InputStream contentStream;
	private String content;
	private int size;
	private Date createdDate;
	private Date updatedDate;
	private DefaultMutableTreeNode node;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getSimpleName() {
		return simpleName;
	}
	
	public void setSimpleName(String simpleName) {
		this.simpleName = simpleName;
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

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public InputStream getContentStream() {
		return contentStream;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
		this.contentStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}
	
	public DefaultMutableTreeNode getNode() {
		return node;
	}
	
	public void setNode(DefaultMutableTreeNode node) {
		this.node = node;
	}
	
	@Override
	public String toString() {
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
		StringBuilder sb = new StringBuilder();
		sb.append("Nombre: " + this.name + "\n");
		sb.append("Extension: " + this.extension + "\n");
		sb.append("Fecha creacion: " + format.format(this.createdDate) + "\n");
		sb.append("Fecha modificacion: " + format.format(this.updatedDate) + "\n");
		sb.append("Tamaño: " + this.size + " bytes");
		return sb.toString();
	}

}
