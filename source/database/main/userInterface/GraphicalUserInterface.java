package database.main.userInterface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CancellationException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import database.main.PluginContainer;
import database.main.date.Date;
import database.plugin.Plugin;

public class GraphicalUserInterface {
	private List<OutputInformation>	collectedLines				= new ArrayList<OutputInformation>();
	private int						currentLineNumber			= 0;
	private Font					font;
	private JFrame					frame						= new JFrame("Database");
	private int						frameWidth;
	private Image					icon;
	private JTextField				input						= new JTextField();
	private String					inputText;
	private JTextPane				output						= new JTextPane();
	private JPanel					panel						= new JPanel();
	private PluginContainer			pluginContainer;
	private int						pressedKey					= 0;
	private JScrollPane				scrollPane					= new JScrollPane(panel);
	private StyledDocument			styledDocument				= output.getStyledDocument();
	private Object					synchronizerInputConfirm	= new Object();
	private Object					synchronizerKeyInput		= new Object();
	private JTextField				time						= new JTextField();
	private Timer					timer						= new Timer();

	public GraphicalUserInterface(PluginContainer pluginContainer) throws IOException, FontFormatException {
		this.pluginContainer = pluginContainer;
		ClassLoader classLoader = this.getClass().getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream("DejaVuSansMono.ttf");
		font = Font.createFont(Font.TRUETYPE_FONT, inputStream);
		font = font.deriveFont(Font.PLAIN, 15);
		inputStream.close();
		icon = new ImageIcon(classLoader.getResource("icon.png")).getImage();
		ActionListener inputListener = new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				inputText = input.getText();
				input.setText(null);
				synchronized (synchronizerInputConfirm) {
					synchronizerInputConfirm.notify();
				}
			}
		};
		KeyListener keyListener = new KeyListener() {
			@Override public void keyPressed(KeyEvent arg0) {
				synchronized (synchronizerKeyInput) {
					pressedKey = arg0.getKeyCode();
					synchronizerKeyInput.notify();
				}
			}

			@Override public void keyReleased(KeyEvent e) {}

			@Override public void keyTyped(KeyEvent e) {}
		};
		panel.setLayout(new BorderLayout(0, 0));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
		input.setFont(font.deriveFont(Font.ITALIC));
		input.setCaretColor(Color.WHITE);
		input.setBackground(Color.BLACK);
		input.setForeground(Color.WHITE);
		input.addActionListener(inputListener);
		input.addKeyListener(keyListener);
		output.add(input, BorderLayout.AFTER_LAST_LINE);
		timer.scheduleAtFixedRate(new UpdateTime(time), 0, 500);
		scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
		scrollPane.getViewport().setBorder(null);
		scrollPane.setViewportBorder(null);
		scrollPane.setBorder(null);
		scrollPane.getVerticalScrollBar().setUnitIncrement(10);
		frame.setContentPane(scrollPane);
		panel.add(output);
		for (StringFormat format : StringFormat.values()) {
			format.initialise(styledDocument);
		}
	}

	public void setLocation() {
		if (frameWidth == 0) {
			setBounds("");
		}
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);
	}

	public void setVisible(boolean visible) {
		frame.setVisible(visible);
	}

	private void clear() {
		output.setText("");
		currentLineNumber = 0;
	}

	private String formatCheckLine(ArrayList<String> strings, int currentLine) {
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

	private void moveTextField(int steps) {
		input.setLocation(0, steps * input.getFontMetrics(font).getHeight());
	}

	private void setBounds(String longestString) {
		if (longestString == null || longestString.isEmpty()) {
			frameWidth = 800;
		}
		else if (output.getFontMetrics(font).stringWidth(longestString) + 25 > frameWidth) {
			frameWidth = output.getFontMetrics(font).stringWidth(longestString) + 25;
		}
		frame.setSize(frameWidth, frameWidth * 2 / 3);
		input.setSize(frameWidth - 10, input.getFontMetrics(font).getHeight());
	}

	protected void blockInput() {
		input.setEditable(false);
		input.setFocusable(false);
		input.setCaretColor(Color.BLACK);
	}

	protected int checkRequest(ArrayList<String> strings) throws InterruptedException, BadLocationException {
		int position = -1;
		int current = 0;
		pressedKey = 0;
		printLine("check:", StringType.MAIN, StringFormat.ITALIC);
		if (strings.isEmpty()) {
			printLine("no entries", StringType.SOLUTION, StringFormat.STANDARD);
			waitForInput();
			return position;
		}
		input.setEditable(false);
		input.setCaretColor(Color.BLACK);
		while (pressedKey != 10) {
			printLine(formatCheckLine(strings, current), StringType.REQUEST, StringFormat.STANDARD);
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
		update();
		pressedKey = 0;
		releaseInput();
		return position;
	}

	protected void collectLine(Object output, StringFormat stringFormat) {
		collectedLines.add(new OutputInformation(output, StringType.SOLUTION, stringFormat));
	}

	protected void errorMessage() throws InterruptedException, BadLocationException {
		printLine("invalid input", StringType.REQUEST, StringFormat.ITALIC);
		waitForInput();
	}

	protected void initialOutput() throws BadLocationException {
		for (Plugin plugin : pluginContainer.getPlugins()) {
			if (plugin.getDisplay()) {
				plugin.initialOutput();
			}
		}
	}

	protected void printCollectedLines() throws InterruptedException, BadLocationException {
		if (!collectedLines.isEmpty()) {
			for (OutputInformation output : collectedLines) {
				printLine(output);
			}
			waitForInput();
		}
	}

	protected void printLine(Object object, StringType stringType, StringFormat stringFormat) throws BadLocationException {
		if (object != null) {
			String outputString = object.toString();
			if (!outputString.endsWith(System.getProperty("line.separator"))) {
				outputString += System.getProperty("line.separator");
			}
			if (stringType.equals(StringType.MAIN)) {
				String[] array = outputString.split(System.getProperty("line.separator"));
				for (int i = 0; i < array.length; i++) {
					setBounds(array[i]);
				}
			}
			if (!stringType.equals(StringType.SOLUTION)) {
				styledDocument.remove(currentLineNumber, styledDocument.getLength() - currentLineNumber);
			}
			styledDocument.insertString(styledDocument.getLength(), outputString, styledDocument.getStyle(stringFormat.toString()));
			if (stringType.equals(StringType.MAIN)) {
				currentLineNumber = currentLineNumber + outputString.length();
			}
			moveTextField(output.getText().contains(System.getProperty("line.separator")) ? output.getText().split(System.getProperty("line.separator")).length : 0);
		}
	}

	protected void printLine(OutputInformation output) throws BadLocationException {
		printLine(output.getOutput(), output.getStringType(), output.getStringFormat());
	}

	protected String readLine() throws InterruptedException {
		synchronized (synchronizerInputConfirm) {
			synchronizerInputConfirm.wait();
		}
		return inputText;
	}

	protected void refresh(Object object, StringType stringType, StringFormat stringFormat) throws BadLocationException {
		if (object != null) {
			String outputString = object.toString();
			if (!outputString.endsWith(System.getProperty("line.separator"))) {
				outputString += System.getProperty("line.separator");
			}
			String text = styledDocument.getText(0, styledDocument.getLength());
			String[] linesOnScreen = text.split(System.getProperty("line.separator"));
			List<String> linesToPrint = Arrays.asList(outputString.split(System.getProperty("line.separator")));
			for (int i = 0; i < linesOnScreen.length; i++) {
				if (linesOnScreen[i].equals(linesToPrint.get(0))) {
					for (int j = 1; j < linesToPrint.size(); j++) {
						if (!linesOnScreen[i++].equals(linesToPrint.get(j))) {}
					}
				}
			}
		}
	}

	protected void releaseInput() {
		input.setEditable(true);
		input.setFocusable(true);
		input.grabFocus();
		input.setCaretColor(Color.WHITE);
	}

	protected String request(String printOut, String regex) throws InterruptedException, BadLocationException, CancellationException {
		boolean request = true;
		String result = null;
		String input = null;
		while (request) {
			printLine(printOut + ":", StringType.REQUEST, StringFormat.ITALIC);
			input = readLine();
			if (input.equals("back")) {
				throw new CancellationException();
			}
			else if (regex != null && input.matches(regex)) {
				result = input;
				request = false;
			}
			else if (regex == null && Date.testDateString(input)) {
				result = input;
				request = false;
			}
			else {
				errorMessage();
			}
		}
		return result;
	}

	protected void showMessageDialog(Throwable e) {
		String stackTrace = "";
		for (StackTraceElement element : e.getStackTrace()) {
			stackTrace = stackTrace + "\r\n" + element;
		}
		JOptionPane.showMessageDialog(frame, stackTrace, e.getClass().getName(), JOptionPane.INFORMATION_MESSAGE);
	}

	protected void update() throws BadLocationException {
		clear();
		blockInput();
		initialOutput();
		releaseInput();
	}

	protected void waitForInput() throws InterruptedException {
		releaseInput();
		input.setCaretColor(Color.BLACK);
		synchronized (synchronizerKeyInput) {
			synchronizerKeyInput.wait();
		}
		input.setCaretColor(Color.WHITE);
	}
}

class UpdateTime extends TimerTask {
	private JTextField timeTextfield;

	UpdateTime(JTextField timeTextfield) {
		this.timeTextfield = timeTextfield;
	}

	@Override public void run() {
		timeTextfield.setText(Date.getCurrentDate() + " " + Date.getCurrentTime());
	}
}
