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
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import database.main.date.Date;

public class GraphicalUserInterface {
	private int				currentLineNumber			= 0;
	private Font			font;
	private JFrame			frame						= new JFrame("Database");
	// private int frameWidth;
	private Image			icon;
	private JTextField		input						= new JTextField();
	private String			inputText;
	private JTextPane		output						= new JTextPane();
	private JPanel			panel						= new JPanel();
	private int				pressedKey					= 0;
	private JScrollPane		scrollPane					= new JScrollPane(panel);
	private StyledDocument	styledDocument				= output.getStyledDocument();
	private Object			synchronizerInputConfirm	= new Object();
	private Object			synchronizerKeyInput		= new Object();
	private JTextField		time						= new JTextField();
	private Timer			timer						= new Timer();

	public GraphicalUserInterface() throws FontFormatException, IOException {
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
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
		frame.setSize(780, 520);
		input.setSize(770, input.getFontMetrics(font).getHeight());
		frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);
	}

	public void setVisible(boolean visible) {
		frame.setVisible(visible);
	}

	protected void blockInput() {
		input.setEditable(false);
		input.setFocusable(false);
		input.setCaretColor(Color.BLACK);
	}

	protected int checkRequest(Collection<String> collection) throws InterruptedException, BadLocationException {
		int position = -1;
		int current = 0;
		pressedKey = 0;
		printLine("check:", StringType.MAIN, StringFormat.ITALIC);
		if (collection.isEmpty()) {
			printLine("no entries", StringType.SOLUTION, StringFormat.STANDARD);
			waitForInput();
			return position;
		}
		input.setEditable(false);
		input.setCaretColor(Color.BLACK);
		while (pressedKey != 10) {
			printLine(formatCheckLine(collection, current), StringType.REQUEST, StringFormat.STANDARD);
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
			if (current > collection.size()) {
				current = collection.size();
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

	protected void getLineOfCharacters(char character) throws BadLocationException {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < frame.getWidth() / output.getFontMetrics(font).charWidth(character); i++) {
			builder.append(character);
		}
		printLine(builder.toString(), StringType.REQUEST, StringFormat.STANDARD);
	}

	protected void printLine(Object object, StringType stringType, StringFormat stringFormat) throws BadLocationException {
		String outputString;
		if (object == null) {
			return;
		}
		outputString = object.toString();
		if (!outputString.endsWith(System.getProperty("line.separator"))) {
			outputString += System.getProperty("line.separator");
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

	protected void printLine(OutputInformation output) throws BadLocationException {
		printLine(output.getOutput(), output.getStringType(), output.getStringFormat());
	}

	protected String readLine() throws InterruptedException {
		synchronized (synchronizerInputConfirm) {
			synchronizerInputConfirm.wait();
		}
		return inputText;
	}

	protected void setInputText(String string) {
		input.setText(string);
		input.setCaretPosition(string.length());
		input.setSelectionStart(0);
		input.setSelectionEnd(string.length());
		input.setSelectedTextColor(Color.BLACK);
		input.setSelectionColor(Color.WHITE);
	}

	protected void releaseInput() {
		input.setEditable(true);
		input.setFocusable(true);
		input.grabFocus();
		input.setCaretColor(Color.WHITE);
	}

	protected void showMessageDialog(Throwable e) {
		String stackTrace = "";
		for (StackTraceElement element : e.getStackTrace()) {
			stackTrace += System.getProperty("line.separator") + element;
		}
		JOptionPane.showMessageDialog(frame, stackTrace, e.getClass().getName(), JOptionPane.INFORMATION_MESSAGE);
	}

	protected void update() throws BadLocationException {
		output.setText("");
		currentLineNumber = 0;
		blockInput();
		Terminal.initialOutput();
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

	private String formatCheckLine(Collection<String> collection, int currentLine) {
		String output = "";
		int counter = 1;
		for (String string : collection) {
			if (counter == currentLine) {
				output += "\u2611 ";
			}
			else {
				output += "\u2610 ";
			}
			output += string + System.getProperty("line.separator");
			counter++;
		}
		return output;
	}

	private void moveTextField(int steps) {
		input.setLocation(0, steps * input.getFontMetrics(font).getHeight());
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
