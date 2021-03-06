package managers;

import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import models.User;
import utils.DAO;
import utils.PwdHashGenerator;

public class ManageUser {

	private DAO db = null;

	public ManageUser() {
		try {
			db = new DAO();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void finalize() {
		try {
			super.finalize();
			db.disconnectBD();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	// Afegir un nou usuari.
	public boolean addUser(User user, String pwd)
			throws UnknownHostException {
		/* Generar salt i password+salt hashejats */
		byte[] salt = PwdHashGenerator.generateRandomSalt(12);
		String base64salt = PwdHashGenerator.convertToBase64(salt); // Salt de 16 bytes.
		String hashedPwd = PwdHashGenerator.generatePasswordSaltHash(pwd.getBytes(StandardCharsets.UTF_8), salt); // Generar
																													// pwd+salt
																	// (43																							// bytes).
		/**/
	
		String query = "INSERT INTO Users (username,firstname,lastname,mail,hashedPassword,salt,birth,tweets,followers,following,isVerified,isAdmin) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
		PreparedStatement statement = null;
		try {
			statement = db.prepareStatement(query);
			statement.setString(1, user.getUsername());
			statement.setString(2, user.getFirstname());
			statement.setString(3, user.getLastname());
			statement.setString(4, user.getMail());
			statement.setString(5, hashedPwd);
			statement.setString(6, base64salt);
			statement.setString(7, user.getBirth());
			/*Tots els parametres comencen a 0*/
			statement.setInt(8, 0); 
			statement.setInt(9, 0);
			statement.setInt(10, 0);
			/**/
			statement.setBoolean(11, false); //El usuari no esta verificat.
			statement.setBoolean(12, false); //El usuari per defecte no es admin.
			statement.executeUpdate();
			statement.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/*Comprovar el login dun usuari.*/
	public int checkLogin(String mail, String pwd) {
		String query = "SELECT hashedPassword,salt,regVerified FROM Users WHERE mail=?";
		PreparedStatement statement = null;
		try {
			statement = db.prepareStatement(query);
			statement.setString(1, mail);
			ResultSet rs = statement.executeQuery();
			String dbPwd = "", dbSalt = "";
			boolean regVerified = false;
			if (rs.next()) {
				dbPwd = rs.getString("hashedPassword");
				dbSalt = rs.getString("salt");
				regVerified = rs.getBoolean("regVerified");
			} else {
				return 2; // Usuari no existent a la db.
			}
			statement.close();
			
			if (PwdHashGenerator.checkPassword(pwd, dbPwd, dbSalt)) {
				if(!regVerified) return 4; //Correu no verificat.
				return 0; // No errors.
			}else {
				return 1; // Password incorrecte.
			}
				
		} catch (SQLException e) {
			e.printStackTrace();
			return 3; // Error al conectar amb el servidor.
		}

	}
	
	
	// Obtenir els usuaris que un usuari esta seguint.
	public List<User> getUserFollows(Integer uid, Integer start, Integer end) {
		String query = "SELECT Users.uid,Users.username FROM Follows JOIN Users ON Users.uid = Follows.uid2 WHERE Follows.uid1 = ? ORDER BY Users.username LIMIT ?,?;;";
		PreparedStatement statement = null;
		List<User> l = new ArrayList<User>();
		try {
			statement = db.prepareStatement(query);
			statement.setInt(1, uid);
			statement.setInt(2, start);
			statement.setInt(3, end);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				User user = new User();
				user.setUid(rs.getInt("uid"));
				user.setUsername(rs.getString("username"));
				l.add(user);
			}
			rs.close();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return  l;
	}
	
	// Obtenir els usuaris que segueixen a un usuari.
		public List<User> getUserFollowers(Integer uid, Integer start, Integer end) {
			String query = "SELECT Users.uid,Users.username FROM Follows JOIN Users ON Users.uid = Follows.uid1 WHERE Follows.uid2 = ? ORDER BY Users.username LIMIT ?,?;;";
			PreparedStatement statement = null;
			List<User> l = new ArrayList<User>();
			try {
				statement = db.prepareStatement(query);
				statement.setInt(1, uid);
				statement.setInt(2, start);
				statement.setInt(3, end);
				ResultSet rs = statement.executeQuery();
				while (rs.next()) {
					User user = new User();
					user.setUid(rs.getInt("uid"));
					user.setUsername(rs.getString("username"));
					l.add(user);
				}
				rs.close();
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} 
			return  l;
		}

	// Obtenir el uid dun usuari donat un mail.
	public Integer getUserID(String mail) {
		String query = "SELECT uid FROM Users WHERE mail=?";
		PreparedStatement statement = null;
		try {
			statement = db.prepareStatement(query);
			statement.setString(1, mail);
			ResultSet rs = statement.executeQuery();
			Integer uid = null;
			if (rs.next()) {
				uid = rs.getInt("uid");
			} else {
				return -1; // Usuari no existent a la db.
			}
			statement.close();
			return uid;
		} catch (SQLException e) {
			e.printStackTrace();
			return -2; // Error al conectar amb el servidor.
		}
	}

	/* Check if all the fields are filled correctly */
	public boolean isComplete(User user) {
		return (hasValue(user.getFirstname()) && hasValue(user.getLastname()) && hasValue(user.getUsername()) && hasValue(user.getMail())
				&& hasValue(user.getPwd1()) && hasValue(user.getPwd2()) && hasValue(user.getBirth()));
	}
	
	private boolean hasValue(String val) {
		return ((val != null) && (!val.equals("")));
	}

	// Deixar de seguir a un usuari.
	public boolean unfollow(Integer uid1, Integer uid2) throws Exception{
		
		//Eliminem follow a la taula de follows
		String query1 = "DELETE FROM Follows WHERE uid1 = ? and uid2 = ?";
		PreparedStatement statement1 = null; 
		int rows_deleted = 0;
		
		try {
			statement1 = db.prepareStatement(query1);
			statement1.setInt(1, uid1);
			statement1.setInt(2, uid2);
			rows_deleted = statement1.executeUpdate();
			statement1.close();

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
		if(rows_deleted == 0) return false;
		
		//Decrementem comptador de following al user 
		String query2 = "UPDATE Users SET following = following - 1 WHERE uid = ?"; //uid1
		PreparedStatement statement2 = null; 
		
		try {
			
			statement2 = db.prepareStatement(query2);
			statement2.setInt(1, uid1);
			statement2.executeUpdate();
			statement2.close();

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
		//Decrementem comptador de followers al user 
		String query3 = "UPDATE Users SET followers = followers - 1 WHERE uid = ?"; //uid2
		PreparedStatement statement3 = null; 

		try {
			
			statement3 = db.prepareStatement(query3);
			statement3.setInt(1, uid2);
			statement3.executeUpdate();
			statement3.close();

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	//Seguir a un usuari.
	public boolean follow(Integer uid1, Integer uid2) throws Exception{
		
		/*Afegeix follow a la taula de follows*/
		String query1 = "INSERT INTO Follows (uid1,uid2) VALUES (?,?)";
		PreparedStatement statement1 = null;
		
		try {
			statement1 = db.prepareStatement(query1);
			statement1.setInt(1, uid1);
			statement1.setInt(2, uid2);
			statement1.executeUpdate();
			statement1.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
		/*Incrementem comptador de following al user*/ 
		String query2 = "UPDATE Users SET following = following + 1 WHERE uid = ?"; 
		PreparedStatement statement2 = null; 
		
		try {
			statement2 = db.prepareStatement(query2);
			statement2.setInt(1, uid1);
			statement2.executeUpdate();
			statement2.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
		/*Incrementem comptador de followers al user*/
		String query3 = "UPDATE Users SET followers = followers + 1 WHERE uid = ?"; 
		PreparedStatement statement3 = null;
		
		try {
			statement3 = db.prepareStatement(query3);
			statement3.setInt(1, uid2);
			statement3.executeUpdate();
			statement3.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	//Saber si un usuari es seguit per un altre.
	public boolean userIsFollowed(int uidfollower, int uid) {
		String query = "SELECT * FROM Follows WHERE uid1=? AND uid2=?";

		PreparedStatement statement = null;
		try {
			statement = db.prepareStatement(query);
			statement.setInt(1, uidfollower);
			statement.setInt(2, uid);
			ResultSet rs = statement.executeQuery();
			
			if(rs.next()) {
				statement.close();
				return true;
			}
			else {
				statement.close();
				return false;
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	//Otenir un usuari de la base de dades donat el uid.
	public User getUser(Integer uid) throws Exception{
		User user = new User();
		
		String query = "SELECT * FROM Users WHERE uid = ?";
		
		PreparedStatement statement = null;
		
		statement = db.prepareStatement(query);
		statement.setInt(1, uid);
		
		ResultSet rs = statement.executeQuery();
		
		if(rs.next()) {
			user.setUid(rs.getInt("uid"));
			user.setUsername(rs.getString("username"));
	      	user.setFirstname(rs.getString("firstname"));
	      	user.setLastname(rs.getString("lastname"));
	      	user.setMail(rs.getString("mail"));
	      	user.setHashedPassword(rs.getString("hashedPassword"));
	      	user.setSalt(rs.getString("salt"));
	      	user.setBirth(rs.getString("birth"));
	      	user.setTweets(rs.getInt("tweets"));
	      	user.setFollowers(rs.getInt("followers"));
	      	user.setFollowing(rs.getInt("following"));
	      	user.setIsVerified(rs.getBoolean("isVerified"));
	      	user.setIsAdmin(rs.getBoolean("isAdmin"));
		}
		
		statement.close();
		return user;
	}
	
	//Obtenir un usuari donat el username.
	public User getUser(String username) throws Exception{
		User user = new User();
		
		String query = "SELECT * FROM Users WHERE username = ?";
		
		PreparedStatement statement = null;
		
		statement = db.prepareStatement(query);
		statement.setString(1, username);
		
		ResultSet rs = statement.executeQuery();
		
		if(rs.next()) {
			user.setUid(rs.getInt("uid"));
			user.setUsername(rs.getString("username"));
	      	user.setFirstname(rs.getString("firstname"));
	      	user.setLastname(rs.getString("lastname"));
	      	user.setMail(rs.getString("mail"));
	      	user.setHashedPassword(rs.getString("hashedPassword"));
	      	user.setSalt(rs.getString("salt"));
	      	user.setBirth(rs.getString("birth"));
	      	user.setTweets(rs.getInt("tweets"));
	      	user.setFollowers(rs.getInt("followers"));
	      	user.setFollowing(rs.getInt("following"));
	      	user.setIsVerified(rs.getBoolean("isVerified"));
		}
		
		statement.close();
	
		return user;
	}
	
	//Editar la informacio personal dun usuari.
	public void editUser(Integer uid, String username, String firstname, String lastname, String birth) {
		
		if(hasValue(username)) {
			String query1 = "UPDATE Users SET username = ? WHERE uid = ?";
			PreparedStatement statement1 = null; 
			try {
				statement1 = db.prepareStatement(query1);
				statement1.setString(1, username);
				statement1.setInt(2, uid);
				statement1.executeUpdate();
				statement1.close();
			} catch (SQLException e) {
				e.printStackTrace();
				return;
			}
			
		}
		
		if(hasValue(firstname)) {
			String query2 = "UPDATE Users SET firstname = ? WHERE uid = ?";
			PreparedStatement statement2 = null; 
			try {
				statement2 = db.prepareStatement(query2);
				statement2.setString(1, firstname);
				statement2.setInt(2, uid);
				statement2.executeUpdate();
				statement2.close();
			} catch (SQLException e) {
				e.printStackTrace();
				return;
			}
			
		}
		
		if(hasValue(lastname)) {
			String query3 = "UPDATE Users SET lastname = ? WHERE uid = ?";
			PreparedStatement statement3 = null; 
			try {
				statement3 = db.prepareStatement(query3);
				statement3.setString(1, lastname);
				statement3.setInt(2, uid);
				statement3.executeUpdate();
				statement3.close();
			} catch (SQLException e) {
				e.printStackTrace();
				return;
			}
			
		}
		
		
		if(hasValue(birth)) {
			String query4 = "UPDATE Users SET birth = ? WHERE uid = ?";
			PreparedStatement statement4 = null; 
			try {
				statement4 = db.prepareStatement(query4);
				statement4.setString(1, birth);
				statement4.setInt(2, uid);
				statement4.executeUpdate();
				statement4.close();
			} catch (SQLException e) {
				e.printStackTrace();
				return;
			}
			
		}
	}
	
	//Editar el password dun usuari.
	public void editUserPassword(Integer uid, String newPwd) {
		
		/* Generar salt i password+salt hashejats */
		byte[] salt = PwdHashGenerator.generateRandomSalt(12);
		String base64salt = PwdHashGenerator.convertToBase64(salt); // Salt de 16 bytes.
		String hashedPwd = PwdHashGenerator.generatePasswordSaltHash(newPwd.getBytes(StandardCharsets.UTF_8), salt);
		
		String query = "UPDATE Users SET hashedPassword=?, salt=? WHERE uid=?";
		
		PreparedStatement statement = null; 
		try {
			statement = db.prepareStatement(query);
			statement.setString(1, hashedPwd);
			statement.setString(2, base64salt);
			statement.setInt(3, uid);
			statement.executeUpdate();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//Buscar usuaris.
	public List<User> searchUsers(String searchWords) throws Exception{
		List<User> users = new ArrayList<>();
		String regex = ".*(?i)(" + searchWords + ").*";
		/*La cerca la realitzarem tant per username com per firstname i lastname*/
		String query = "SELECT uid FROM Users WHERE username REGEXP ? OR firstname REGEXP ? OR lastname REGEXP ?";
		
		PreparedStatement statement = null; 
		try {
			statement = db.prepareStatement(query);
			statement.setString(1, regex);
			statement.setString(2, regex);
			statement.setString(3, regex);
			ResultSet rs = statement.executeQuery();
			while(rs.next()) {
				User user = this.getUser(rs.getInt("uid"));
				users.add(user);
			}
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return users;
	}
	
	//Eliminar un usuari.
	public void deleteUser(int uid) {
		
		/*Eliminar follows*/
		String query = "DELETE FROM Follows WHERE uid1=? OR uid2=?;";
		PreparedStatement statement = null; 
		
		try {
			statement = db.prepareStatement(query);
			statement.setInt(1, uid);
			statement.setInt(2, uid);
			statement.executeUpdate();
			statement.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		/*Eliminar likes dels tweets*/
		query = "UPDATE Tweets SET likes = likes - 1 WHERE tweetid IN (SELECT tweetid FROM Likes WHERE uid=?);";
		statement = null; 
		
		try {
			statement = db.prepareStatement(query);
			statement.setInt(1, uid);
			statement.executeUpdate();
			statement.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		/*Eliminar likes*/
		query = "DELETE FROM Likes WHERE uid=?;";
		statement = null; 
		
		try {
			statement = db.prepareStatement(query);
			statement.setInt(1, uid);
			statement.executeUpdate();
			statement.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		/*Eliminar likes als seus tweets*/
		
		query = "DELETE FROM Likes WHERE tweetid IN (SELECT tweetid FROM Tweets WHERE uid=?);";
		statement = null; 
		
		try {
			statement = db.prepareStatement(query);
			statement.setInt(1, uid);
			statement.executeUpdate();
			statement.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		/*Eliminar retweets dels retweets*/
		query = "UPDATE Tweets SET retweets = retweets - 1 WHERE tweetid IN (SELECT tweetid FROM Retweets WHERE uid=?);";
		statement = null; 
		
		try {
			statement = db.prepareStatement(query);
			statement.setInt(1, uid);
			statement.executeUpdate();
			statement.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		/*Eliminar retweets*/
		query = "DELETE FROM Retweets WHERE uid=?;";
		statement = null; 
		
		try {
			statement = db.prepareStatement(query);
			statement.setInt(1, uid);
			statement.executeUpdate();
			statement.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		/*Eliminar retweets als seus tweets*/
		
		query = "DELETE FROM Retweets WHERE tweetid IN (SELECT tweetid FROM Tweets WHERE uid=?);";
		statement = null; 
		
		try {
			statement = db.prepareStatement(query);
			statement.setInt(1, uid);
			statement.executeUpdate();
			statement.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		/*Eliminar tweets*/
		query = "DELETE FROM Tweets WHERE uid=?;";
		
		statement = null; 
		
		try {
			statement = db.prepareStatement(query);
			statement.setInt(1, uid);
			statement.executeUpdate();
			statement.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		/*Eliminar usuari*/
		query = "DELETE FROM Users WHERE uid=?;";
		statement = null; 
		
		try {
			statement = db.prepareStatement(query);
			statement.setInt(1, uid);
			statement.executeUpdate();
			statement.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
}
	

