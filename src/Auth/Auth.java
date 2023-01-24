package Auth;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import Connector.DatabaseConnector;
import Validator.Valid;
import courseManagement.Pages.LoginPage;
import courseManagement.Pages.dashboard.StudentDashboard;
import exception.InvalidEmail;
import exception.PasswordDonotMatch;
import exception.UserAlreadyExist;
import exception.UserNotFound;

public class Auth {
	DatabaseConnector dc=new DatabaseConnector();
    public void login(String username,String password, JPanel mainPanel,JFrame frame){
        DatabaseConnector dc=new DatabaseConnector();
        if(username.isEmpty() && password.isEmpty())
				{
					JOptionPane.showMessageDialog(null, "Please input usename or password");

				}
				else{
					try {
					
						Connection con=dc.connection("jdbc:mysql://localhost:3306/coursemanagementsystem");
						String query="SELECT role,firstname FROM `users` WHERE username=? and password=?";
						PreparedStatement pst=con.prepareStatement(query);
						pst.setString(1, username);
						pst.setString(2,password);
						ResultSet rs=pst.executeQuery();
						if(rs.next())
						{
							
							String role=rs.getString("role");
							String name=rs.getString("firstname");
							if(role.equals("student"))
							{
								mainPanel.setVisible(false);
								new StudentDashboard(frame,name);
								JOptionPane.showMessageDialog(null, "Login in as Student");
								 
							}
							else if(role.equals("teacher"))
							{
								JOptionPane.showMessageDialog(null, "Login in as Teacher");
								frame.setVisible(false);	
							}
							else if(role.equals("admin"))
							{
								JOptionPane.showMessageDialog(null, "Welcome admin");
							}
							else 
							{
								JOptionPane.showMessageDialog(null, "Something went wrong!!");
							}
						}
						else
						{
							throw new UserNotFound("User not found");
						}
						
					}
					catch(Exception ex) {
						JOptionPane.showMessageDialog(null, ex.getMessage());
					}
				}
    }


	public void Signup(String name,String password,String cPassword,String firstName,String lastName,JFrame frame,String username,JPanel panel){
		Valid validator=new Valid();
		try{
			if (!validator.checkEmail(username))
				{
					throw new InvalidEmail("Invalid Email! enter valid email");

				}
				else if (validator.checkPassword(password) && password.equals(cPassword)) {
					try {
						String query = "INSERT INTO `users`(`username`, `password`, `role`,`firstname`,`lastname`) VALUES (?,?,?,?,?)";
						Connection con = dc.connection("jdbc:mysql://localhost:3306/coursemanagementsystem");
						String query1 = "SELECT role FROM `users` WHERE username=?";
						PreparedStatement pst1;
						pst1 = con.prepareStatement(query1);
						pst1.setString(1, username);
						ResultSet rs = pst1.executeQuery();
						if (rs.next()) {
							throw new UserAlreadyExist("User Already Exist");
						} else {
							PreparedStatement pst;
							pst = con.prepareStatement(query);
							pst.setString(1, username);
							pst.setString(2, password);
							pst.setString(3, "student");
							pst.setString(4, firstName);
							pst.setString(5, lastName);
							pst.executeUpdate();
							JOptionPane.showMessageDialog(null, "You are registered.Login to continue");
							new LoginPage(frame);
							panel.setVisible(false);
						}
						con.close();
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(null, ex.getMessage());

					}
				} else if (!validator.checkPassword(password)) {
					 throw new PasswordDonotMatch("Password must have one special character,uppercase, lowercase and must be atleast 8 characters ");
				} else if (!password.equals(cPassword)) {
					throw new PasswordDonotMatch("Password do not match");
				}
		}
		catch(Exception ex)
		{
			JOptionPane.showMessageDialog(null, ex.getMessage());
		}
	}
    
}