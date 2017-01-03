package com.gameduell.jira.authentication;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.gameduell.jira.domain.Config;
import com.gameduell.jira.util.Context;

public class Authenticator {
	private String login;
	private String password;
	private AsynchronousJiraRestClientFactory clientFactory = new AsynchronousJiraRestClientFactory();

	private Config config;
	private Context context;

	public void setConfig(Config config) {
		this.config = config;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public void login(String login, String password) {
		this.login = login;
		this.password = password;

		authenticate();
	}

	private void authenticate() {
		if (login == null || password == null)
			askLoginPasswordFromUser();

		JiraRestClient jiraClient = clientFactory.createWithBasicHttpAuthentication(config.getJiraUri(),
				config.getLogin(), config.getPassword());
		context.setJiraRestClient(jiraClient);
	}

	public void login() {
		login(config.getLogin(), config.getPassword());
	}

	public void askLoginPasswordFromUser() {
		JPanel panel = new JPanel(new BorderLayout(5, 5));

		JPanel label = new JPanel(new GridLayout(0, 1, 2, 2));
		label.add(new JLabel("Login", SwingConstants.RIGHT));
		label.add(new JLabel("Password", SwingConstants.RIGHT));
		panel.add(label, BorderLayout.WEST);

		JPanel controls = new JPanel(new GridLayout(0, 1, 2, 2));
		JTextField username = new JTextField();
		controls.add(username);
		username.setText(login);
		
		JPasswordField password = new JPasswordField();
		controls.add(password);
		password.requestFocus();
		panel.add(controls, BorderLayout.CENTER);

		JOptionPane.showConfirmDialog(new JFrame("Login dialog"), panel, "login", JOptionPane.OK_CANCEL_OPTION);

		config.setLogin(username.getText());
		config.setPassword(new String(password.getPassword()));
	}
}
