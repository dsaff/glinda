/**
 * 
 */
package net.saff.glinda.ideas.correspondent;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicOptionPaneUI;

import sun.swing.DefaultLookup;

public class UserKeyboardCorrespondent implements Correspondent {
	public String getAnswer(String question, final String... choices) {
		final JOptionPane pane = createOptionPane(question, choices);
		final JDialog dialog = pane.createDialog(null, "TITLE");
		addButtonListener(pane, dialog, choices);
		return interpretSelection(getSelection(pane, dialog), choices);
	}

	private String interpretSelection(Object selectedValue,
			final String... choices) {
		int result = interpretSelectionAsInt(choices, selectedValue);
		return choices[result];
	}

	private Object getSelection(final JOptionPane pane, final JDialog dialog) {
		pane.selectInitialValue();
		dialog.setVisible(true);
		dialog.dispose();

		Object selectedValue = pane.getValue();
		return selectedValue;
	}

	public void addButtonListener(final JOptionPane pane, final JDialog dialog,
			final String... choices) {
		addListenerToButtons(dialog.getComponents(), new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				for (String option : choices) {
					if (option.charAt(0) == (e.getKeyChar())) {
						pane.setValue(option);
						dialog.setVisible(false);
						return;
					}
				}
			}
		});
	}

	public JOptionPane createOptionPane(String question,
			final String... choices) {
		Object initialValue = choices[0];
		final JOptionPane pane = new JOptionPane(question,
				JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION,
				null, choices, initialValue) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void updateUI() {
				UserKeyboardCorrespondent.this.updateUI(this);
			}
		};

		return setUpPane(pane, initialValue);
	}

	protected void updateUI(JOptionPane optionPane) {
		optionPane.setUI(new BasicOptionPaneUI() {
			@Override
			protected Container createButtonArea() {
				JPanel bottom = new JPanel();
				Border border = (Border) DefaultLookup.get(optionPane, this,
						"OptionPane.buttonAreaBorder");
				bottom.setName("OptionPane.buttonArea");
				if (border != null) {
					bottom.setBorder(border);
				}

				final GridLayout grid = new GridLayout(0, 1);

				bottom.setLayout(new ButtonAreaLayout(DefaultLookup.getBoolean(
						optionPane, this, "OptionPane.sameSizeButtons", true),
						DefaultLookup.getInt(optionPane, this,
								"OptionPane.buttonPadding", 6)) {
					@Override
					public void layoutContainer(Container container) {
						grid.layoutContainer(container);
					}

					@Override
					public Dimension minimumLayoutSize(Container c) {
						return grid.minimumLayoutSize(c);
					}
				});
				addButtonComponents(bottom, getButtons(),
						getInitialValueIndex());
				return bottom;
			}
		});
	}

	public JOptionPane setUpPane(final JOptionPane pane, Object initialValue) {
		pane.setInitialValue(initialValue);
		pane.setComponentOrientation(JOptionPane.getRootFrame()
				.getComponentOrientation());
		return pane;
	}

	public int interpretSelectionAsInt(final String[] options, Object selectedValue) {
		if (selectedValue == null)
			return JOptionPane.CLOSED_OPTION;
		if (options == null) {
			if (selectedValue instanceof Integer)
				return ((Integer) selectedValue).intValue();
			return JOptionPane.CLOSED_OPTION;
		}
		for (int counter = 0, maxCounter = options.length; counter < maxCounter; counter++)
			if (options[counter].equals(selectedValue))
				return counter;
		return JOptionPane.CLOSED_OPTION;
	}

	private void addListenerToButtons(Component[] components,
			KeyListener listener) {
		for (Component component : components) {
			if (component instanceof JButton)
				component.addKeyListener(listener);
			if (component instanceof Container)
				addListenerToButtons(((Container) component).getComponents(),
						listener);
		}
	}

}