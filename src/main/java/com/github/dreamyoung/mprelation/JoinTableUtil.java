package com.github.dreamyoung.mprelation;

public class JoinTableUtil {
	public static String getRefColumnProperty(String refColumn) {
		if (refColumn.indexOf("_") != -1) {
			String[] columnNameArr = refColumn.split("_");
			refColumn = "";
			for (int k = 0; k < columnNameArr.length; k++) {
				if (k == 0) {
					refColumn += columnNameArr[k];
				} else {
					refColumn += columnNameArr[k].substring(0, 1).toUpperCase() + columnNameArr[k].substring(1);
				}
			}
		}
		return refColumn;
	}

	public static String getInverseRefColumnProperty(String InverseRefColumn) {
		if (InverseRefColumn.indexOf("_") != -1) {
			String[] columnNameArr = InverseRefColumn.split("_");
			InverseRefColumn = "";
			for (int k = 0; k < columnNameArr.length; k++) {
				if (k == 0) {
					InverseRefColumn += columnNameArr[k];
				} else {
					InverseRefColumn += columnNameArr[k].substring(0, 1).toUpperCase() + columnNameArr[k].substring(1);
				}
			}
		}
		return InverseRefColumn;
	}
}
