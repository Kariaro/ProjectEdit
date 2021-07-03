package com.hardcoded.window.settings;

import java.awt.Graphics;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Objects;

import javax.swing.JTextField;

public class JNumberField extends JTextField {
	private static final long serialVersionUID = 1L;
	private FieldChangeListener listener;

	// Used to compare when the text content of this field has changed
	private String lastText;
	
	public JNumberField(String text) {
		setColumns(10);
		setText(text);
		
		addActionListener((e) -> {
			if(listener != null && !listener.stateChanged()) {
				setText(lastText);
			} else {
				lastText = getText();
			}
		});
		
		addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				if(listener != null && !listener.stateChanged()) {
					setText(lastText);
				} else {
					lastText = getText();
				}
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				
			}
		});
	}
	
	
	@Override
	public void paint(Graphics g) {
		String currentText = getText();
		if(!Objects.equals(lastText, currentText)) {
			if(!currentText.isBlank() && listener != null && !listener.stateChanged()) {
				setText(lastText);
			} else {
				lastText = getText();
			}
		}
		
		super.paint(g);
	}
	
	public void setChangeListener(FieldChangeListener listener) {
		this.listener = listener;
	}
	
	public static interface FieldChangeListener {
		boolean stateChanged();
	}
}
