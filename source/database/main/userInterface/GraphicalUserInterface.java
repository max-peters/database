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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

public class GraphicalUserInterface {
	private int				currentLineNumber;
	private Font			font;
	private JFrame			frame;
	private Image			icon;
	private JTextField		input;
	private JTextPane		output;
	private JPanel			panel;
	private int				pressedKey;
	private JScrollPane		scrollPane;
	private StyledDocument	styledDocument;
	private Object			synchronizerInputConfirm;
	private Object			synchronizerKeyInput;
	private Object			synchronizerKeyPressed;
	private JTextField		time;
	private Timer			timer;

	public void initialise() throws FontFormatException, IOException {
		frame = new JFrame("Database");
		input = new JTextField();
		output = new JTextPane();
		panel = new JPanel();
		scrollPane = new JScrollPane(panel);
		styledDocument = output.getStyledDocument();
		synchronizerInputConfirm = new Object();
		synchronizerKeyInput = new Object();
		synchronizerKeyPressed = new Object();
		time = new JTextField();
		timer = new Timer();
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		ClassLoader classLoader = this.getClass().getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream("DejaVuSansMono.ttf");
		font = Font.createFont(Font.TRUETYPE_FONT, inputStream);
		font = font.deriveFont(Font.PLAIN, 15);
		inputStream.close();
		icon = new ImageIcon(classLoader.getResource("icon.png")).getImage();
		ActionListener inputListener = new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				synchronized (synchronizerInputConfirm) {
					synchronizerInputConfirm.notify();
				}
			}
		};
		KeyListener keyListener = new KeyListener() {
			@Override public void keyPressed(KeyEvent e) {
				pressedKey = e.getExtendedKeyCode();
				if (pressedKey == 8) {
					input.replaceSelection("");
				}
				else if (pressedKey == 10) {
					synchronized (synchronizerKeyInput) {
						synchronizerKeyInput.notify();
					}
				}
				synchronized (synchronizerKeyPressed) {
					synchronizerKeyPressed.notify();
				}
			}

			@Override public void keyReleased(KeyEvent e) {}

			@Override public void keyTyped(KeyEvent e) {}
		};
		DocumentListener documentListener = new DocumentListener() {
			@Override public void changedUpdate(DocumentEvent e) {}

			@Override public void insertUpdate(DocumentEvent e) {
				synchronized (synchronizerKeyInput) {
					synchronizerKeyInput.notify();
				}
			}

			@Override public void removeUpdate(DocumentEvent e) {}
		};
		input.getDocument().addDocumentListener(documentListener);
		panel.setLayout(new BorderLayout(0, 0));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setIconImage(icon);
		frame.setResizable(false);
		time.setEditable(false);
		time.setEnabled(false);
		time.setFont(font);
		time.setBorder(BorderFactory.createEmptyBorder());
		time.setBackground(Color.BLACK);
		time.setDisabledTextColor(Color.WHITE);
		panel.add(time, BorderLayout.NORTH);
		output.setEditable(false);
		output.setEnabled(false);
		output.setFont(font);
		output.setBorder(BorderFactory.createEmptyBorder());
		output.setBackground(Color.BLACK);
		output.setDisabledTextColor(Color.WHITE);
		panel.add(output, BorderLayout.CENTER);
		input.setBorder(BorderFactory.createEmptyBorder());
		input.setFont(font.deriveFont(Font.ITALIC));
		input.setCaretColor(Color.WHITE);
		input.setBackground(Color.BLACK);
		input.setForeground(Color.WHITE);
		input.addActionListener(inputListener);
		input.addKeyListener(keyListener);
		input.setSelectedTextColor(Color.BLACK);
		input.setSelectionColor(Color.WHITE);
		output.add(input, BorderLayout.AFTER_LAST_LINE);
		timer.scheduleAtFixedRate(new UpdateTime(time), 0, 500);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
		verticalScrollBar.setPreferredSize(new Dimension(0, 0));
		verticalScrollBar.setUnitIncrement(10);
		JScrollBar horizontalScrollBar = scrollPane.getHorizontalScrollBar();
		horizontalScrollBar.setPreferredSize(new Dimension(0, 0));
		horizontalScrollBar.setUnitIncrement(10);
		frame.setContentPane(scrollPane);
		panel.add(output);
		for (StringFormat format : StringFormat.values()) {
			format.initialise(styledDocument);
		}
		frame.setSize(800, 530);
		input.setSize(770, input.getFontMetrics(font).getHeight());
		frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);
	}

	public void setVisible(boolean visible) {
		frame.setVisible(visible);
	}

	protected String autocomplete(Completeable completeable) throws InterruptedException {
		String inputString = null;
		pressedKey = 0;
		while (pressedKey != 10) {
			synchronized (synchronizerKeyInput) {
				synchronizerKeyInput.wait();
			}
			inputString = input.getText();
			setInputText(completeable.getNewInput(inputString));
		}
		return inputString;
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
		if (collection.isEmpty()) {
			printLine("check:", StringType.REQUEST, StringFormat.ITALIC);
			printLine("no entries", StringType.SOLUTION, StringFormat.STANDARD);
			waitForInput();
			return position;
		}
		input.setEditable(false);
		input.setCaretColor(Color.BLACK);
		while (pressedKey != 10) {
			printLine("check:", StringType.REQUEST, StringFormat.ITALIC);
			printLine(formatCheckLine(collection, current), StringType.SOLUTION, StringFormat.STANDARD);
			synchronized (synchronizerKeyPressed) {
				synchronizerKeyPressed.wait();
			}
			if (pressedKey == 40) {
				if (!(current == collection.size())) {
					current++;
				}
			}
			else if (pressedKey == 38) {
				if (!(current == 0)) {
					current--;
				}
			}
			else if (pressedKey != 10) {
				int temp = findString(current, collection);
				current = temp == current ? findString(0, collection) : temp;
			}
		}
		if (current != 0) {
			position = current - 1;
		}
		releaseInput();
		return position;
	}

	protected void clearInput() {
		input.setText("");
	}

	protected synchronized void clearOutput() throws BadLocationException {
		output.setText("");
		currentLineNumber = 0;
	}

	protected void getLineOfCharacters(char character, StringType stringType) throws BadLocationException {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < frame.getWidth() / output.getFontMetrics(font).charWidth(character); i++) {
			builder.append(character);
		}
		printLine(builder.toString(), stringType, StringFormat.STANDARD);
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

	protected String readKey() throws InterruptedException {
		synchronized (synchronizerKeyInput) {
			synchronizerKeyInput.wait();
		}
		return input.getText();
	}

	protected String readLine() throws InterruptedException {
		synchronized (synchronizerInputConfirm) {
			synchronizerInputConfirm.wait();
		}
		return input.getText();
	}

	protected void releaseInput() {
		input.setEditable(true);
		input.setFocusable(true);
		input.grabFocus();
		input.setCaretColor(Color.WHITE);
	}

	protected void setInputText(String string) {
		String inputText = input.getText();
		input.setText(inputText + string);
		input.select(inputText.length(), (inputText + string).length());
	}

	protected void showMessageDialog(Throwable e) {
		String stackTrace = "";
		for (StackTraceElement element : e.getStackTrace()) {
			stackTrace += System.getProperty("line.separator") + element;
		}
		JOptionPane.showMessageDialog(frame, stackTrace, e.getClass().getName(), JOptionPane.INFORMATION_MESSAGE);
	}

	protected void waitForInput() throws InterruptedException {
		releaseInput();
		input.setCaretColor(Color.BLACK);
		do {
			synchronized (synchronizerKeyPressed) {
				synchronizerKeyPressed.wait();
			}
		}
		while ((pressedKey == 38 || pressedKey == 40) && input.getY() > 520);
		input.setCaretColor(Color.WHITE);
	}

	private int findString(int index, Collection<String> collection) {
		int i = 1;
		for (String string : collection) {
			if (i > index && Character.compare(Character.toUpperCase(string.charAt(0)), Character.toUpperCase((char) pressedKey)) == 0) {
				return i;
			}
			i++;
		}
		return index;
	}

	private String formatCheckLine(Collection<String> collection, int currentLine) {
		String output = "";
		int counter = 1;
		for (String string : collection) {
			if (counter == currentLine) {
				output += " \u2611 ";
			}
			else {
				output += " \u2610 ";
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
	private SimpleDateFormat	dateFormat	= new SimpleDateFormat("dd.MM.yyyy");
	private SimpleDateFormat	timeFormat	= new SimpleDateFormat("HH:mm:ss");
	private JTextField			timeTextfield;

	UpdateTime(JTextField timeTextfield) {
		this.timeTextfield = timeTextfield;
	}

	@Override public void run() {
		timeTextfield.setText(dateFormat.format(Calendar.getInstance().getTime()) + " " + timeFormat.format(Calendar.getInstance().getTime()));
	}
}
