import java.awt.EventQueue;
import javax.swing.JFrame;
import java.awt.CardLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import java.awt.BorderLayout;
import javax.swing.JList;
import java.awt.FlowLayout;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JSpinner;
import javax.swing.SpinnerListModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.DefaultComboBoxModel;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

// Nicholas Anderson, Don Speer, Joshua Shaffer, Jason Foster
// Assignment 10 - GUI
// CS345
// 12/10/17


public class gui {

	private JFrame frame;
	private JTextField firstInput;
	private JTextField lastInput;
	private JTextField studentInput;
	private String courseSelection = "CS112";
	
    static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/enrollment";
    static final String DB_DRV = "org.mariadb.jdbc";
    static final String DB_USER = "root";
    static final String DB_PASSWD = "root";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					gui window = new gui();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public gui() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().setFont(new Font("Dialog", Font.PLAIN, 12));
		frame.setBounds(100, 100, 305, 265);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel courseLabel = new JLabel("Courses to choose from");
		courseLabel.setVerticalAlignment(SwingConstants.TOP);
		courseLabel.setFont(new Font("Dialog", Font.BOLD, 11));
		courseLabel.setBounds(12, 12, 222, 13);
		frame.getContentPane().add(courseLabel);
		
		JComboBox<String> courseInput = new JComboBox<String>();
		courseInput.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
			    if (e.getStateChange() == ItemEvent.SELECTED) {
			    	courseSelection = courseInput.getSelectedItem().toString();
			    } else if(e.getStateChange() == ItemEvent.DESELECTED){
			    	
			    }
			}
		});
		courseInput.setModel(new DefaultComboBoxModel<String>(new String[] {"CS112", "CS345", "CS421"}));
		courseInput.setBounds(12, 30, 275, 20);
		frame.getContentPane().add(courseInput);
		
		JLabel firstLabel = new JLabel("First Name");
		firstLabel.setBounds(12, 62, 89, 20);
		frame.getContentPane().add(firstLabel);
		
		firstInput = new JTextField();
		firstInput.setBounds(102, 63, 185, 19);
		frame.getContentPane().add(firstInput);
		firstInput.setColumns(10);
		
		JLabel lastLabel = new JLabel("Last Name");
		lastLabel.setFont(new Font("Dialog", Font.BOLD, 12));
		lastLabel.setVerticalAlignment(SwingConstants.TOP);
		lastLabel.setBounds(12, 94, 76, 20);
		frame.getContentPane().add(lastLabel);
		
		lastInput = new JTextField();
		lastInput.setBounds(102, 94, 185, 19);
		frame.getContentPane().add(lastInput);
		lastInput.setColumns(10);
		
		JLabel studentField = new JLabel("Student ID");
		studentField.setFont(new Font("Dialog", Font.BOLD, 12));
		studentField.setBounds(12, 124, 89, 20);
		frame.getContentPane().add(studentField);
		
		studentInput = new JTextField();
		studentInput.setBounds(102, 125, 185, 19);
		frame.getContentPane().add(studentInput);
		studentInput.setColumns(10);
		
		JButton enrollButton = new JButton("Enroll");
		enrollButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				buttonHitActionPerformed(evt);
			}
		});
		enrollButton.setFont(new Font("Dialog", Font.BOLD, 12));
		enrollButton.setBounds(12, 156, 275, 67);
		frame.getContentPane().add(enrollButton);
	}

	
	private void buttonHitActionPerformed(java.awt.event.ActionEvent evt) {
        Connection connection = null;
        PreparedStatement statement = null;
        String courseName = "";
        Integer courseID = null;
        String firstName = "";
        String lastName = "";
        String studentID = "";
        
        courseName = courseSelection;
        courseID = getCourseID(courseName);
        firstName = firstInput.getText();
        lastName = lastInput.getText();
        studentID = studentInput.getText();
        
        if (!isEnrolled(studentID, courseID)) {
		    try {
	            if (!isStudent(studentID)) {
	                createStudent(studentID, firstName, lastName);
	            }
	            // get the connection 
		        connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWD);
	            statement = connection.prepareStatement("INSERT INTO enroll VALUES (?,?)");
	            statement.setString(1, studentID);        
	            statement.setString(2, courseID.toString());
	            statement.executeQuery();
		    } catch (SQLException ex) {
		        ex.printStackTrace();
		    } finally {
		        try {
		            statement.close();
		            connection.close();
		        } catch (SQLException ex) {
		        }
		    }
        }
	}
	
	private int getCourseID(String courseName) {
        Connection connection = null;
        ResultSet resultSet = null;
        PreparedStatement statement = null;
        Integer courseID = null;
        
        try {
            // get the connection 
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWD);
            statement = connection.prepareStatement("SELECT id FROM class WHERE short_name = ?");
            statement.setString(1, courseName); 
            resultSet = statement.executeQuery();
            
            if (resultSet.first()) {
                courseID = resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                resultSet.close();
                statement.close();
                connection.close();
            } catch (SQLException ex) {
            }
        }
        return courseID;
	}
	
	private boolean isEnrolled(String studentID, Integer courseID) {
        Connection connection = null;
        ResultSet resultSet = null;
        PreparedStatement statement = null;
        boolean status = false;
        
        try {
            // get the connection 
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWD);
            statement = connection.prepareStatement("SELECT 1 FROM enroll WHERE student_id = ? AND class_id = ?");
            statement.setString(1, studentID);
            statement.setString(2, courseID.toString());           
            resultSet = statement.executeQuery();
            status = resultSet.first();
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                resultSet.close();
                statement.close();
                connection.close();
            } catch (SQLException ex) {
            }
        }
        return status;
	}
	
	private boolean isStudent(String studentID) {
        Connection connection = null;
        ResultSet resultSet = null;
        PreparedStatement statement = null;
        boolean status = false;
        
        try {
            // get the connection 
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWD);
            statement = connection.prepareStatement("SELECT 1 FROM student WHERE id = ?");
            statement.setString(1, studentID);        
            resultSet = statement.executeQuery();        
            status = resultSet.first();
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                resultSet.close();
                statement.close();
                connection.close();
            } catch (SQLException ex) {
            }
        }
        return status;
	}
	
	private void createStudent(String studentID, String firstName, String lastName) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            // get the connection 
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWD);
            statement = connection.prepareStatement("INSERT INTO student VALUES (?,?,?)");
            statement.setString(1, studentID); 
            statement.setString(2, firstName);
            statement.setString(3, lastName);
            statement.executeQuery();
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                statement.close();
                connection.close();
            } catch (SQLException ex) {
            }
        }
	}
}
