package database.main.userInterface;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
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
	private int currentLineNumber;
	private DocumentListener documentListener;
	private Font font;
	private JFrame frame;
	private int frameHeight;
	private int frameWidht;
	private Image icon;
	private JTextField input;
	private KeyListener keyListener;
	private JTextPane output;
	private JPanel panel;
	private JPanel placeholder;
	private int pressedKey;
	private boolean removeSelectionAndLastKey;
	private JScrollPane scrollPane;
	private StyledDocument styledDocument;
	private Object synchronizerKeyInput;
	private Object synchronizerKeyPressed;
	private JTextField time;
	private Timer timer;
	private TimerTask timerTask;

	public void initialise() throws FontFormatException, IOException {
		frameHeight = 530;
		frameWidht = 800;
		frame = new JFrame("Database");
		input = new JTextField();
		output = new JTextPane();
		panel = new JPanel();
		placeholder = new JPanel();
		styledDocument = output.getStyledDocument();
		synchronizerKeyInput = new Object();
		synchronizerKeyPressed = new Object();
		time = new JTextField();
		timer = new Timer();
		documentListener = new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {}

			@Override
			public void insertUpdate(DocumentEvent e) {
				synchronized (synchronizerKeyInput) {
					synchronizerKeyInput.notify();
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {}
		};
		keyListener = new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				pressedKey = e.getExtendedKeyCode();
				if (removeSelectionAndLastKey && e.getExtendedKeyCode() == KeyEvent.VK_BACK_SPACE) {
					input.replaceSelection("");
				}
				if (e.getExtendedKeyCode() == KeyEvent.VK_BACK_SPACE) {
					String inputString = input.getText();
					if (!inputString.isEmpty()) {
						input.setText(inputString.substring(0, inputString.length() - 1));
					}
				}
				if (e.getExtendedKeyCode() == KeyEvent.VK_ENTER || e.getExtendedKeyCode() == KeyEvent.VK_ESCAPE) {
					synchronized (synchronizerKeyInput) {
						synchronizerKeyInput.notify();
					}
				}
				synchronized (synchronizerKeyPressed) {
					synchronizerKeyPressed.notify();
				}
				if (e.getExtendedKeyCode() != KeyEvent.VK_UP && e.getExtendedKeyCode() != KeyEvent.VK_DOWN) {
					e.consume();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {}

			@Override
			public void keyTyped(KeyEvent e) {}
		};
		timerTask = new TimerTask() {
			@Override
			public void run() {
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
				SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
				time.setText(dateFormat.format(Calendar.getInstance().getTime()) + " "
						+ timeFormat.format(Calendar.getInstance().getTime()));
			}
		};
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		ClassLoader classLoader = this.getClass().getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream("DejaVuSansMono.ttf");
		font = Font.createFont(Font.TRUETYPE_FONT, inputStream);
		font = font.deriveFont(Font.PLAIN, 15);
		inputStream.close();
		icon = new ImageIcon(classLoader.getResource("icon.png")).getImage();
		time.setEditable(false);
		time.setEnabled(false);
		time.setFont(font);
		time.setBorder(BorderFactory.createEmptyBorder());
		time.setBackground(Color.BLACK);
		time.setDisabledTextColor(Color.WHITE);
		time.setMaximumSize(new Dimension(frameWidht, input.getFontMetrics(font).getHeight()));
		output.setEditable(false);
		output.setEnabled(false);
		output.setFont(font);
		output.setBorder(BorderFactory.createEmptyBorder());
		output.setBackground(Color.BLACK);
		output.setDisabledTextColor(Color.WHITE);
		output.setMinimumSize(new Dimension(frameWidht, 1));
		output.setMaximumSize(new Dimension(frameWidht, 1));
		input.setBorder(BorderFactory.createEmptyBorder());
		input.setFont(font.deriveFont(Font.ITALIC));
		input.setCaretColor(Color.WHITE);
		input.setBackground(Color.BLACK);
		input.setForeground(Color.WHITE);
		input.setSelectedTextColor(Color.BLACK);
		input.setSelectionColor(Color.WHITE);
		input.addKeyListener(keyListener);
		input.getDocument().addDocumentListener(documentListener);
		input.setFocusable(false);
		input.setMaximumSize(new Dimension(frameWidht, input.getFontMetrics(font).getHeight()));
		placeholder.setBackground(Color.BLACK);
		timer.scheduleAtFixedRate(timerTask, 0, 500);
		for (StringFormat format : StringFormat.values()) {
			format.initialise(styledDocument);
		}
		panel.setLayout(new BoxLayout(panel, 1));
		panel.add(time);
		panel.add(output);
		panel.add(input);
		panel.add(placeholder);
		scrollPane = new JScrollPane(panel);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
		verticalScrollBar.setPreferredSize(new Dimension(0, 0));
		verticalScrollBar.setUnitIncrement(10);
		JScrollBar horizontalScrollBar = scrollPane.getHorizontalScrollBar();
		horizontalScrollBar.setPreferredSize(new Dimension(0, 0));
		horizontalScrollBar.setUnitIncrement(10);
		frame.setSize(frameWidht, frameHeight);
		frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);
		frame.setContentPane(scrollPane);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setIconImage(icon);
		frame.setResizable(false);
	}

	public void setVisible(boolean visible) {
		frame.setVisible(visible);
	}

	protected void blockInput() {
		input.setEditable(false);
		input.setCaretColor(Color.BLACK);
	}

	protected void clearOutput() throws BadLocationException {
		output.setText("");
		currentLineNumber = 0;
	}

	protected int getInputCaretPosition() {
		return input.getCaretPosition();
	}

	protected String getInputText() {
		return input.getText();
	}

	protected int getLastKeyInput() {
		return pressedKey;
	}

	protected int getNumberOfCharsPerLine(char character) {
		return frame.getWidth() / output.getFontMetrics(font).charWidth(character);
	}

	protected String getSelectedText() {
		String selectedText = input.getSelectedText();
		if (selectedText == null) {
			return "";
		}
		else {
			return selectedText;
		}
	}

	protected boolean isScrollable() {
		return output.getSize().getHeight() > frameHeight;
	}

	protected void printLine(Object object, OutputType outputType, StringFormat stringFormat)
			throws BadLocationException {
		String outputString;
		if (object == null) {
			return;
		}
		outputString = object.toString();
		if (!outputType.equals(OutputType.ADD)) {
			styledDocument.remove(currentLineNumber, styledDocument.getLength() - currentLineNumber);
		}
		styledDocument.insertString(styledDocument.getLength(), outputString,
				styledDocument.getStyle(stringFormat.toString()));
		if (outputType.equals(OutputType.MAIN)) {
			currentLineNumber = currentLineNumber + outputString.length();
		}
	}

	protected void releaseInput() {
		input.setEditable(true);
		input.setFocusable(true);
		input.grabFocus();
		input.setCaretColor(Color.WHITE);
	}

	protected void selectInputText(int first, int last) {
		input.select(first, last);
	}

	protected void setInputCaretPosition(int caretPosition) {
		input.setCaretPosition(caretPosition);
	}

	protected void setInputColor(Color color) {
		input.setForeground(color);
		input.setCaretColor(color);
	}

	protected void setInputText(String inputText) {
		input.setText(inputText);
	}

	protected void setRemoveSelectionAndLastKey(boolean removeSelectionAndLastKey) {
		this.removeSelectionAndLastKey = removeSelectionAndLastKey;
	}

	protected void waitForDocumentInput() throws InterruptedException {
		synchronized (synchronizerKeyInput) {
			synchronizerKeyInput.wait();
		}
	}

	protected void waitForKeyInput() throws InterruptedException {
		synchronized (synchronizerKeyPressed) {
			synchronizerKeyPressed.wait();
		}
	}
}
