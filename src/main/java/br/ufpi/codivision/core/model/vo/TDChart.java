package br.ufpi.codivision.core.model.vo;

public class TDChart {

	private String id;
	private String name; 
	private String color;
	private String parent;
	public Object value;


	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	public int getValue() {
		return (int) value;
	}
	public void setValue(int value) {
		this.value = value;
	}
}
