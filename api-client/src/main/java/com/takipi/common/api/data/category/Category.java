package com.takipi.common.api.data.category;

import java.util.List;

import com.takipi.common.api.data.view.ViewInfo;

public class Category {
	public String id;
	public String name;
	public List<ViewInfo> views;
	public boolean shared;
}
