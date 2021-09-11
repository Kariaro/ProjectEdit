package com.hardcoded.window.settings;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import com.hardcoded.lwjgl.icon.WindowIcons;
import com.hardcoded.main.ProjectEdit;
import com.hardcoded.settings.NumberRange;
import com.hardcoded.settings.ProjectSettings;
import com.hardcoded.settings.SettingKey;

public class LwjglSettingsWindow {
	private static final Logger LOGGER = LogManager.getLogger(LwjglSettingsWindow.class);
	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.#####");
	private JFrame frame;
	private boolean init;
	
	public LwjglSettingsWindow() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e) {
			LOGGER.error(e);
			e.printStackTrace();
		}
		
		frame = new JFrame();
		frame.setTitle("ProjectEdit - Settings");
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.setSize(640, 640);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setFocusable(false);
		frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		addSettingsTab(tabbedPane);
		addResourcePackTab(tabbedPane);
	}
	
	private void addSettingsTab(JTabbedPane tabbedPane) {
		JPanel panel = new JPanel();
		tabbedPane.addTab("Settings", null, panel, null);
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		for(SettingKey key : SettingKey.keys()) {
			if(key.getFieldType() == boolean.class) {
				addBooleanField(panel, key);
			} else if(key.getFieldType() == int.class) {
				addIntegerField(panel, key);
			} else if(key.getFieldType() == float.class) {
				addFloatField(panel, key);
			} else {
//				LOGGER.warn("Unknown setting type '{}'", key.getFieldType());
			}
		}
	}
	
	private void addResourcePackTab(JTabbedPane tabbedPane) {
		final java.util.List<String> list_paths = new ArrayList<>();
		
		String packs = (String)ProjectSettings.getKeyValue(SettingKey.ResourcePacks);
		if(!packs.isBlank()) {
			list_paths.addAll(Arrays.asList(packs.split("\\|")));
		}
		
//		ProjectSettings.setKeyValue(SettingKey.MaxFps, -12304);
		
		final JList<String> list;
		final JButton btnAddPath = new JButton("Add Path");
		final JButton btnRemove = new JButton("Remove");
		final JButton btnMoveUp = new JButton("Move Up");
		final JButton btnMoveDown = new JButton("Move Down");
		
		// TODO: Drag and Drop
		JPanel panel = new JPanel();
		tabbedPane.addTab("Resource Packs", null, panel, null);
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		panel.setLayout(new BorderLayout(0, 0));
		{
			list = new JList<>();
			list.setDoubleBuffered(true);
			list.setFocusable(false);
			list.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
			list.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
			list.setModel(new AbstractListModel<String>() {
				private static final long serialVersionUID = 1L;

				@Override
				public int getSize() {
					return list_paths.size();
				}
				
				@Override
				public String getElementAt(int index) {
					return list_paths.get(index);
				}
			});
			list.addListSelectionListener((e) -> {
				int index = list.getSelectedIndex();

				int from = list.getMinSelectionIndex();
				int to = list.getMaxSelectionIndex();
				
				btnRemove.setEnabled(index != -1);
				if(index != -1) {
					btnMoveUp.setEnabled(from > 0);
					btnMoveDown.setEnabled(to < list_paths.size() - 1);
				} else {
					btnMoveUp.setEnabled(false);
					btnMoveDown.setEnabled(false);
				}
			});
			panel.add(list, BorderLayout.CENTER);
		}
		{
			JPanel panel_1 = new JPanel();
			panel_1.setBorder(new EmptyBorder(5, 5, 5, 5));
			panel.add(panel_1, BorderLayout.EAST);
			panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.Y_AXIS));
			
			Dimension size = new Dimension(100, 24);
			
			btnAddPath.setFocusable(false);
			btnAddPath.setMaximumSize(size);
			btnAddPath.addActionListener((e) -> {
				try(MemoryStack stack = MemoryStack.stackPush()) {
					PointerBuffer filters = stack.mallocPointer(1);
					filters.put(stack.UTF8("*.zip"));
					filters.flip();
					
					String currentPath = (String)ProjectSettings.getKeyValue(SettingKey.LastResourcePackPath);
					if(!currentPath.isBlank()) {
						currentPath += File.separatorChar;
					}
					String pathString = TinyFileDialogs.tinyfd_openFileDialog("Add ResourcePack", currentPath, filters, "Resource Pack", true);
					
					if(pathString != null && !pathString.isBlank()) {
						String[] results = pathString.split("\\|");
						for(String result : results) {
							String resourcePack = new File(result).getAbsolutePath();
							if(!list_paths.contains(resourcePack)) {
								list_paths.add(resourcePack);
							}
						}
						
						if(results.length > 0) {
							String firstPath = new File(results[0]).getParentFile().getAbsolutePath();
							ProjectSettings.setKeyValue(SettingKey.LastResourcePackPath, firstPath);
						}
						
						int index = list.getSelectedIndex();
						int to = list.getMaxSelectionIndex();
						
						btnRemove.setEnabled(index != -1);
						if(index != -1) {
							btnMoveUp.setEnabled(index > 0);
							btnMoveDown.setEnabled(to < list_paths.size() - 1);
						} else {
							btnMoveUp.setEnabled(false);
							btnMoveDown.setEnabled(false);
						}
						
						list.ensureIndexIsVisible(list_paths.size());
						list.updateUI();
						
						ProjectSettings.setKeyValue(SettingKey.ResourcePacks, String.join("|", list_paths));
					}
				}
			});
			panel_1.add(btnAddPath);
			
			btnRemove.setFocusable(false);
			btnRemove.setEnabled(false);
			btnRemove.setMaximumSize(size);
			btnRemove.addActionListener((e) -> {
				int index = list.getSelectedIndex();
				if(index != -1) {
					int[] removeList = list.getSelectedIndices();
					
					for(int i = removeList.length - 1; i >= 0; i--) {
						list_paths.remove(removeList[i]);
					}
					
					list.ensureIndexIsVisible(list_paths.size());
					list.setSelectedIndex(index);
					list.updateUI();
					
					ProjectSettings.setKeyValue(SettingKey.ResourcePacks, String.join("|", list_paths));
					
					btnMoveUp.setEnabled(false);
					btnMoveDown.setEnabled(false);
				}
				
				if(list_paths.isEmpty()) {
					btnRemove.setEnabled(false);
				}
			});
			panel_1.add(btnRemove);
			
			btnMoveUp.setFocusable(false);
			btnMoveUp.setEnabled(false);
			btnMoveUp.setMaximumSize(size);
			btnMoveUp.setMnemonic(KeyEvent.VK_UP);
			btnMoveUp.addActionListener((e) -> {
				int index = list.getSelectedIndex();
				
				if(index != -1 && index > 0) {
					int[] moveList = list.getSelectedIndices();
					
					for(int i = 0, len = moveList.length; i < len; i++) {
						int index2 = moveList[i];
						moveList[i]--;
						String item_a = list_paths.get(index2);
						list_paths.remove(index2);
						list_paths.add(index2 - 1, item_a);
					}
					
					list.setSelectedIndices(moveList);
					list.updateUI();
					
					ProjectSettings.setKeyValue(SettingKey.ResourcePacks, String.join("|", list_paths));
				}
			});
			panel_1.add(btnMoveUp);
			
			btnMoveDown.setFocusable(false);
			btnMoveDown.setEnabled(false);
			btnMoveDown.setMaximumSize(size);
			btnMoveDown.addActionListener((e) -> {
				int index = list.getSelectedIndex();
				if(index != -1 && index < list_paths.size() - 1) {
					int[] moveList = list.getSelectedIndices();
					
					for(int i = moveList.length - 1; i >= 0; i--) {
						int index2 = moveList[i];
						moveList[i]++;
						String item_a = list_paths.get(index2);
						list_paths.remove(index2);
						list_paths.add(index2 + 1, item_a);
					}
					
					list.setSelectedIndices(moveList);
					list.updateUI();
					
					ProjectSettings.setKeyValue(SettingKey.ResourcePacks, String.join("|", list_paths));
				}
			});
			panel_1.add(btnMoveDown);
		}
		
		
	}
	
	private void addBooleanField(JPanel panel, SettingKey key) {
		JPanel fieldBox = new JPanel();
		fieldBox.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		Dimension size = new Dimension(Short.MAX_VALUE, 24);
		fieldBox.setMaximumSize(size);
		fieldBox.setPreferredSize(size);
		
		JLabel key_label = new JLabel(key.name());
		key_label.setPreferredSize(new Dimension(160, 24));
		fieldBox.add(key_label);
		
		JCheckBox key_checkbox = new JCheckBox();
		key_checkbox.setSelected((Boolean)ProjectSettings.getKeyValue(key));
		key_checkbox.setFocusable(false);
		key_checkbox.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				ProjectSettings.setKeyValue(key, key_checkbox.isSelected());
			}
		});
		
		fieldBox.add(key_checkbox);
		panel.add(fieldBox);
	}
	
	private void addIntegerField(JPanel panel, SettingKey key) {
		JPanel fieldBox = new JPanel();
		fieldBox.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		Dimension size = new Dimension(Short.MAX_VALUE, 24);
		fieldBox.setMaximumSize(size);
		fieldBox.setPreferredSize(size);
		
		JLabel key_label = new JLabel(key.name());
		key_label.setPreferredSize(new Dimension(160, 24));
		fieldBox.add(key_label);

		@SuppressWarnings("unchecked")
		NumberRange<Integer> range = (NumberRange<Integer>)key.getNumberRange();
		Integer value = (Integer)ProjectSettings.getKeyValue(key);
		JNumberField numberField = new JNumberField(Integer.toString(value));
		numberField.setChangeListener(() -> {
			String text = numberField.getText();
			try {
				long last = Long.parseLong(text);
				if(range.contains(last)) {
					
				} else if(last < range.getMinimum()) {
					numberField.setText(Integer.toString(range.getMinimum()));
				} else if(last > range.getMaximum()) {
					numberField.setText(Integer.toString(range.getMaximum()));
				}
				
			} catch(NumberFormatException ignored) { 
				numberField.setText(ProjectSettings.getKeyValue(key).toString());
			}
			
			ProjectSettings.setKeyValue(key, numberField.getText());
			
			return true;
		});
		fieldBox.add(numberField);
		
		panel.add(fieldBox);
	}
	
	private void addFloatField(JPanel panel, SettingKey key) {
		JPanel fieldBox = new JPanel();
		fieldBox.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		Dimension size = new Dimension(Short.MAX_VALUE, 24);
		fieldBox.setMaximumSize(size);
		fieldBox.setPreferredSize(size);
		
		JLabel key_label = new JLabel(key.name());
		key_label.setPreferredSize(new Dimension(160, 24));
		fieldBox.add(key_label);

		@SuppressWarnings("unchecked")
		NumberRange<Float> range = (NumberRange<Float>)key.getNumberRange();
		Float value = (Float)ProjectSettings.getKeyValue(key);
		JNumberField numberField = new JNumberField(DECIMAL_FORMAT.format(value));
		numberField.setChangeListener(() -> {
			String text = numberField.getText();
			try {
				double last = Double.parseDouble(text);
				if(range.contains(last)) {
					
				} else if(last < range.getMinimum()) {
					numberField.setText(DECIMAL_FORMAT.format(range.getMinimum()));
				} else if(last > range.getMaximum()) {
					numberField.setText(DECIMAL_FORMAT.format(range.getMaximum()));
				}
				
			} catch(NumberFormatException ignored) { 
				numberField.setText(DECIMAL_FORMAT.format(ProjectSettings.getKeyValue(key)));
			}
			
			ProjectSettings.setKeyValue(key, numberField.getText());
			
			return true;
		});
		fieldBox.add(numberField);
		
		panel.add(fieldBox);
	}
	
	public void show() {
		if(!init) {
			init = true;
			frame.setIconImage(ProjectEdit.getInstance().getTextureManager().getWindowIcons().getIcon(WindowIcons.ICON_16));
		}
		
		frame.setVisible(true);
		frame.toFront();
		frame.requestFocus();
	}
	
	public void hide() {
		frame.setVisible(false);
	}
	
	public void cleanup() {
		frame.dispose();
	}
}
