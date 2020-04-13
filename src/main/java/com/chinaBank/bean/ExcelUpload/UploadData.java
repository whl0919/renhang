package com.chinaBank.bean.ExcelUpload;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class UploadData {
	private String colsData;
	private List<Object> listData = new ArrayList<Object>();
}
