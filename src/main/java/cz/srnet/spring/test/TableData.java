package cz.srnet.spring.test;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.ARRAY)
public class TableData {

	private List<List<String>> data;

	public TableData(List<List<String>> data) {
		this.setData(data);
	}

	public List<List<String>> getData() {
		return data;
	}

	public void setData(List<List<String>> data) {
		this.data = data;
	}

}
