import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class StartFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	public static final String NAME_DB = "testdb";

	private JPanel panel;
	private JLabel labelUrl, labelLogin, labelPassword;
	private JTextField tfUrl, tfLogin, tfPassword;
	private JButton create, delete, connect;

	{
		panel = new JPanel();
		labelUrl = new JLabel("URL");
		labelLogin = new JLabel("Login");
		labelPassword = new JLabel("Password");
		tfUrl = new JTextField("localhost", 20);
		tfLogin = new JTextField("root", 20);
		tfPassword = new JTextField("root", 20);
		create = new JButton("create");
		delete = new JButton("delete");
		connect = new JButton("connect");

		panel.add(labelUrl);
		panel.add(tfUrl);
		panel.add(labelLogin);
		panel.add(tfLogin);
		panel.add(labelPassword);
		panel.add(tfPassword);
		panel.add(create);
		panel.add(delete);
		panel.add(connect);

		add(panel);
	}

	public StartFrame() {
		setSize(260, 220);
		setTitle("Init Database");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		setVisible(true);
		initialize();
	}

	private void initialize() {

		create.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					MysqlDataSource dataSource = getMySQLDataSource(tfUrl.getText(), tfLogin.getText(),
							tfPassword.getText());
					createDB(dataSource);

					JOptionPane.showMessageDialog(panel, "The database has been successfully created", "Message",
							JOptionPane.INFORMATION_MESSAGE);

				} catch (Exception e) {
					JOptionPane.showMessageDialog(panel, "the database has not been successfully created", "",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					MysqlDataSource dataSource = getMySQLDataSource(
							tfUrl.getText(),
							tfLogin.getText(),
							tfPassword.getText());
					deleteDB(dataSource);

					JOptionPane.showMessageDialog(panel, "The database has been successfully deleted", "Message",
							JOptionPane.INFORMATION_MESSAGE);

				} catch (Exception e) {
					JOptionPane.showMessageDialog(panel, "the database has not been successfully deleted", "",
							JOptionPane.ERROR_MESSAGE);

				}
			}
		});

		connect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {

				MysqlDataSource dataSource = getMySQLDataSource(
						tfUrl.getText(),
						tfLogin.getText(),
						tfPassword.getText());
				
				try (Connection cn = dataSource.getConnection();
						Statement st = cn.createStatement()) {

					st.executeQuery("USE " + NAME_DB);

					EventQueue.invokeLater(new Runnable() {
						public void run() {

							new LoginFrame(dataSource);
						}
					});

					dispose();

				} catch (SQLException e) {
					JOptionPane.showMessageDialog(panel, "no database connection", "", JOptionPane.ERROR_MESSAGE);

				}
			}
		});
	}

	private void createDB(MysqlDataSource dataSource) {

		try (Connection cn = dataSource.getConnection();
				Statement st = cn.createStatement()) {

			st.executeUpdate("CREATE DATABASE " + NAME_DB);

			st.executeUpdate("USE " + NAME_DB);

			st.executeUpdate("CREATE TABLE users (" 
						   + "id INT PRIMARY KEY AUTO_INCREMENT," 
						   + " username VARCHAR(50),"
						   + " password VARCHAR(50)," 
						   + " `last name` VARCHAR(50)," 
						   + " `first name` VARCHAR(50),"
						   + " email VARCHAR(50)," 
						   + " grade INT)");

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private void deleteDB(MysqlDataSource dataSource) {

		try (Connection cn = dataSource.getConnection();
				Statement st = cn.createStatement()) {

			st.executeUpdate("DROP DATABASE " + NAME_DB);

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static MysqlDataSource getMySQLDataSource(String url, String user, String password) {
		MysqlDataSource dataSource = new MysqlDataSource();
		dataSource.setServerName(url);
		dataSource.setUser(user);
		dataSource.setPassword(password);
		return dataSource;
	}
}
