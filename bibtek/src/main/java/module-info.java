module bibtek {
	requires javafx.fxml;
	requires transitive javafx.graphics;
	requires javafx.controls;
	requires com.google.gson;
	
	exports bibtek.core;
	exports bibtek.json;
	exports bibtek.ui;
	

	opens bibtek.ui to javafx.fxml;
}