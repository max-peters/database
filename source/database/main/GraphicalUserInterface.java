package database.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import database.main.date.Date;
import database.main.date.Time;

public class GraphicalUserInterface {
	public JFrame			frame						= new JFrame("Database");
	private Object			synchronizerInputConfirm	= new Object();
	private Object			synchronizerNewInput		= new Object();
	private Object			synchronizerKeyInput		= new Object();
	private List<String>	linesToWrite				= new ArrayList<String>();
	private List<String>	requestsToWrite				= new ArrayList<String>();
	private JPanel			panel						= new JPanel();
	private JTextField		input						= new JTextField();
	private JTextField		time						= new JTextField();
	private JTextArea		output						= new JTextArea();
	private JTextArea		outputChangeable			= new JTextArea();
	private Timer			timer						= new Timer();
	private Font			font;
	private Image			icon;
	private int				pressedKey;
	private String			inputText;

	public GraphicalUserInterface() throws IOException, FontFormatException {
		ClassLoader classLoader = this.getClass().getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream("DejaVuSansMono.ttf");
		font = Font.createFont(Font.PLAIN, inputStream);
		font = font.deriveFont(Font.PLAIN, 15);
		inputStream.close();
		icon = new ImageIcon(classLoader.getResource("icon.png")).getImage();
		ActionListener inputListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				inputText = input.getText();
				input.setText(null);
				synchronized (synchronizerInputConfirm) {
					synchronizerInputConfirm.notify();
				}
			}
		};
		KeyListener keyListener = new KeyListener() {
			public void keyPressed(KeyEvent arg0) {
				synchronized (synchronizerNewInput) {
					synchronizerNewInput.notify();
				}
			}

			public void keyReleased(KeyEvent e) {
			}

			public void keyTyped(KeyEvent e) {
			}
		};
		KeyListener keyListenerDetail = new KeyListener() {
			public void keyPressed(KeyEvent arg0) {
				synchronized (synchronizerKeyInput) {
					pressedKey = arg0.getKeyCode();
					synchronizerKeyInput.notify();
				}
			}

			public void keyReleased(KeyEvent e) {
			}

			public void keyTyped(KeyEvent e) {
			}
		};
		panel.setLayout(new BorderLayout(0, 0));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(670, 330, 670, 450);
		frame.setContentPane(panel);
		frame.setIconImage(icon);
		frame.setResizable(false);
		time.setEditable(false);
		time.setEnabled(false);
		time.setFont(font);
		time.setBorder(null);
		time.setBackground(Color.BLACK);
		time.setDisabledTextColor(Color.WHITE);
		panel.add(time, BorderLayout.NORTH);
		output.setEditable(false);
		output.setEnabled(false);
		output.setFont(font);
		output.setBorder(null);
		output.setBackground(Color.BLACK);
		output.setDisabledTextColor(Color.WHITE);
		panel.add(output, BorderLayout.CENTER);
		input.setBorder(null);
		input.setFont(font);
		input.setCaretColor(Color.WHITE);
		input.setBackground(Color.BLACK);
		input.setForeground(Color.WHITE);
		input.setBounds(0, 0, 670, 20);
		input.addActionListener(inputListener);
		input.addKeyListener(keyListener);
		output.add(input, BorderLayout.AFTER_LAST_LINE);
		outputChangeable.setBorder(null);
		outputChangeable.setFont(font);
		outputChangeable.setCaretColor(Color.BLACK);
		outputChangeable.setBackground(Color.BLACK);
		outputChangeable.setForeground(Color.WHITE);
		outputChangeable.setEditable(false);
		outputChangeable.setVisible(false);
		outputChangeable.addKeyListener(keyListenerDetail);
		output.add(outputChangeable, BorderLayout.AFTER_LAST_LINE);
		timer.scheduleAtFixedRate(new UpdateTime(time), 0, 500);
	}

	private void moveTextField(int steps) {
		input.setBounds(0, steps * 18, 670, 20);
	}

	public void show() {
		frame.setVisible(true);
	}

	public void clear() {
		linesToWrite.clear();
		requestsToWrite.clear();
	}

	public void printLine(String line) {
		linesToWrite.add(line);
		output.setText(write(linesToWrite));
		moveTextField(linesToWrite.size());
	}

	public void printRequest(String line) {
		requestsToWrite.clear();
		requestsToWrite.addAll(linesToWrite);
		requestsToWrite.add(line);
		output.setText(write(requestsToWrite));
		moveTextField(requestsToWrite.size());
	}

	public void printSolution(String line) {
		if (!requestsToWrite.containsAll(linesToWrite)) {
			requestsToWrite.addAll(linesToWrite);
		}
		requestsToWrite.add(line);
		output.setText(write(requestsToWrite));
		moveTextField(requestsToWrite.size());
	}

	public int check(ArrayList<String> strings) throws InterruptedException {
		int position = -1;
		printRequest("check:");
		if (strings.isEmpty()) {
			printSolution("no entries");
			waitForInput();
			return position;
		}
		int current = 0;
		input.setVisible(false);
		outputChangeable.setBounds(0, 18 * requestsToWrite.size(), 670, 20 * strings.size());
		outputChangeable.setVisible(true);
		outputChangeable.grabFocus();
		while (pressedKey != 10) {
			outputChangeable.setText(getOutput(strings, current));
			synchronized (synchronizerKeyInput) {
				synchronizerKeyInput.wait();
			}
			if (pressedKey == 40) {
				current++;
			}
			if (pressedKey == 38) {
				current--;
			}
			if (current < 0) {
				current = 0;
			}
			if (current > strings.size()) {
				current = strings.size();
			}
		}
		if (current != 0) {
			position = current - 1;
		}
		outputChangeable.setVisible(false);
		outputChangeable.setText("");
		pressedKey = 0;
		input.setVisible(true);
		input.grabFocus();
		return position;
	}

	private String getOutput(ArrayList<String> strings, int currentLine) {
		String output = "";
		int counter = 1;
		for (String string : strings) {
			if (counter == currentLine) {
				output = output + "\u2611 ";
			}
			else {
				output = output + "\u2610 ";
			}
			output = output + string + "\r\n";
			counter++;
		}
		return output;
	}

	private String write(List<String> stringList) {
		String toReturn = "";
		for (String current : stringList) {
			toReturn = toReturn + current + "\r\n";
		}
		return toReturn;
	}

	public String in() throws InterruptedException {
		synchronized (synchronizerInputConfirm) {
			synchronizerInputConfirm.wait();
		}
		return inputText;
	}

	public void waitForInput() throws InterruptedException {
		input.setEditable(true);
		input.setFocusable(true);
		input.grabFocus();
		input.setCaretColor(Color.BLACK);
		synchronized (synchronizerNewInput) {
			synchronizerNewInput.wait();
		}
		input.setCaretColor(Color.WHITE);
	}

	public void blockInput() {
		input.setEditable(false);
		input.setFocusable(false);
		input.setCaretColor(Color.BLACK);
	}
}

class UpdateTime extends TimerTask {
	private JTextField	timeTextfield;

	public UpdateTime(JTextField timeTextfield) {
		this.timeTextfield = timeTextfield;
	}

	public void run() {
		timeTextfield.setText(Date.getDateAsString() + " " + Time.getTime());
	}
}