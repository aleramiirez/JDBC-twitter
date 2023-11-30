import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.Date;
import java.util.Scanner;

public class Main {

    public static final Scanner scan = new Scanner(System.in);
    public static final String url = "jdbc:mysql://localhost:3306/social_network";
    public static final String usuario = "root";
    public static final String password = "Root";

    public static Connection con;

    static {
        try {
            con = DriverManager.getConnection(url, usuario, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static int userID = -1;

    public static void main(String[] args) {

        int opciones = 0;
        while (opciones != 15){
        System.out.println("1. Registrarse");
        System.out.println("2. Iniciar Sesion");
        System.out.println("3. Tweets de seguidores");
        System.out.println("4. Todos los tweets");
        System.out.println("5. Tweetear");
        System.out.println("6. Tu perfil");
        System.out.println("7. Todos los perfiles");
        System.out.println("8. Tus tweets");
        System.out.println("9. Borrar tweets");
        System.out.println("10. Usuarios a los que sigues");
        System.out.println("11. Usuarios que te siguen");
        System.out.println("12. Buscar perfil");
        System.out.println("13. Seguir");
        System.out.println("14. Dejar de seguir");
        System.out.println("15. Salir del programa");
        System.out.print("Que desea hacer: ");
        opciones = scan.nextInt();
        System.out.println();

        switch (opciones) {
            case 1:
                System.out.print("Introduce tu usuario: ");
                scan.nextLine();
                String usuarioR = scan.nextLine();
                System.out.print("Introduce tu email: ");
                String emailR = scan.next();
                System.out.print("Introduce tu contraseña: ");
                String passwordR = scan.next();
                register(con, usuarioR, emailR, passwordR);
                System.out.println("-----------------------");
                break;
            case 2:
                System.out.print("Introduce tu usuario: ");
                scan.nextLine();
                String usuarioI = scan.nextLine();
                System.out.print("Introduce tu contraseña: ");
                String passwordI = scan.next();
                try {
                    login(con, usuarioI, passwordI);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("-----------------------");
                break;
            case 3:
                try {
                    showFollowedTweets(con);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("-----------------------");
                break;
            case 4:
                try {
                    showTweets(con);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("-----------------------");
                break;
            case 5:
                System.out.print("Tweet: ");
                scan.nextLine();
                String tweet = scan.nextLine();
                tweetear(con, tweet);
                System.out.println("-----------------------");
                break;
            case 6:
                showYourProfile(con);
                System.out.println("-----------------------");
                break;
            case 7:
                try {
                    showAllProfiles(con);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("-----------------------");
                break;
            case 8:
                try {
                    showYourTweets(con);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("-----------------------");
                break;
            case 9:
                try {
                    showYourTweets(con);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                System.out.print("Introduce el id del tweet que deseas borrar:");
                int tweetId = scan.nextInt();
                deleteTweet(con, tweetId);
                System.out.println("-----------------------");
                break;
            case 10:
                try {
                    showYourFollows(con);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("-----------------------");
                break;
            case 11:
                try {
                    showYourFollowers(con);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("-----------------------");
                break;
            case 12:
                System.out.print("Introduce el usuario que desea buscar: ");
                scan.nextLine();
                String username = scan.nextLine();
                try {
                    showOtherProfile(con, username);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("-----------------------");
                break;
            case 13:
                try {
                    showAllProfiles(con);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                System.out.print("Introduce el id de usuario que desea seguir: ");
                int usuarioSeguir = scan.nextInt();
                follow(con, usuarioSeguir);
                System.out.println("-----------------------");
                break;
            case 14:
                try {
                    showAllProfiles(con);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                System.out.print("Introduce el id de usuario que desea dejar de seguir: ");
                int usuarioDejarSeguir = scan.nextInt();
                unfollow(con, usuarioDejarSeguir);
                System.out.println("-----------------------");
                break;
            default:
                System.out.println("Cerrando programa...");
                break;
        }
        }
    }

    public static void register(Connection con, String usuario, String email, String password) {
        try {
            // Encriptar la contraseña utilizando jBCrypt
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            // Obtener la fecha actual
            java.sql.Date fechaActual = new java.sql.Date(new Date().getTime());

            // Preparar la consulta SQL para la inserción
            String query = "INSERT INTO users (username, email, password, createDate) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, usuario);
            statement.setString(2, email);
            statement.setString(3, hashedPassword);
            statement.setDate(4, fechaActual);

            // Ejecutar la consulta
            int filasAfectadas = statement.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("Registro exitoso.");
            } else {
                System.out.println("No se pudo realizar el registro.");
            }
        } catch (SQLException e) {
            System.err.println("Error al registrar usuario: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void login(Connection con, String usuario, String password) throws SQLException {
        try {
            String query = "SELECT * FROM users WHERE username = ?";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, usuario);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Usuario encontrado, comprobar la contraseña
                String hashedPassword = resultSet.getString("password");
                if (BCrypt.checkpw(password, hashedPassword)) {
                    System.out.println("¡Inicio de sesión exitoso!");
                    userID = resultSet.getInt("id");
                } else {
                    throw new IllegalArgumentException("Contraseña incorrecta");
                }
            } else {
                throw new IllegalArgumentException("Usuario no encontrado");
            }
        } catch (SQLException e) {
            throw new SQLException("Error al buscar usuario: " + e.getMessage());
        }
    }

    public static void showFollowedTweets(Connection con) throws SQLException {
        try {
            String query = "SELECT u.username, p.text, p.createDate " +
                    "FROM users u " +
                    "JOIN publications p ON u.id = p.userId " +
                    "WHERE u.id IN (SELECT userToFollowId FROM follows WHERE userToFollowId = ?) " +
                    "ORDER BY p.createDate DESC";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setInt(1, userID);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String usuario = resultSet.getString("username");
                String textoTweet = resultSet.getString("text");
                java.sql.Date fechaPublicacion = resultSet.getDate("createDate");

                System.out.println("Usuario: " + usuario);
                System.out.println("Tweet: " + textoTweet);
                System.out.println("Fecha de Publicación: " + fechaPublicacion);
                System.out.println();
            }
        } catch (SQLException e) {
            throw new SQLException("Error al obtener los tweets de los usuarios seguidos: " + e.getMessage());
        }
    }
    public static void showTweets(Connection con) throws SQLException {
        try {
            String query = "SELECT u.username, p.text, p.createDate " +
                    "FROM users u " +
                    "JOIN publications p ON u.id = p.userId " +
                    "ORDER BY p.createDate DESC";
            PreparedStatement statement = con.prepareStatement(query);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String usuario = resultSet.getString("username");
                String textoTweet = resultSet.getString("text");
                java.sql.Date fechaPublicacion = resultSet.getDate("createDate");

                System.out.println("Usuario: " + usuario);
                System.out.println("Tweet: " + textoTweet);
                System.out.println("Fecha de Publicación: " + fechaPublicacion);
                System.out.println();
            }
        } catch (SQLException e) {
            throw new SQLException("Error al obtener los tweets: " + e.getMessage());
        }
    }

    public static void tweetear(Connection con, String texto) {
        try {
            java.sql.Date fechaActual = new java.sql.Date(new Date().getTime());

            String query = "INSERT INTO publications (userId, text, createDate) VALUES (?, ?, ?)";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setInt(1, userID);
            statement.setString(2, texto);
            statement.setDate(3, fechaActual);

            int filasAfectadas = statement.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("Tweet enviado con éxito.");
            } else {
                System.out.println("No se pudo enviar el tweet.");
            }
        } catch (SQLException e) {
            System.err.println("Error al enviar el tweet: " + e.getMessage());
        }
    }

    public static void showYourProfile(Connection con) {
        try {
            String query = "SELECT username, email, description, createDate FROM users WHERE id = ?";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setInt(1, userID); // Asume que tienes un userID definido

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String usuario = resultSet.getString("username");
                String email = resultSet.getString("email");
                String descripcion = resultSet.getString("description");
                java.sql.Date fechaRegistro = resultSet.getDate("createDate");

                System.out.println("Usuario: " + usuario);
                System.out.println("Email: " + email);
                System.out.println("Descripción: " + descripcion);
                System.out.println("Fecha de Registro: " + fechaRegistro);
            } else {
                System.out.println("No se encontró el perfil del usuario.");
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener el perfil del usuario: " + e.getMessage());
        }
    }

    public static void showAllProfiles(Connection con) throws SQLException {
        try {
            String query = "SELECT id, username, email, description, createDate FROM users WHERE id != ?";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setInt(1, userID); // Asume que tienes un userID definido

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int idUsuario = resultSet.getInt("id");
                String usuario = resultSet.getString("username");
                String email = resultSet.getString("email");
                String descripcion = resultSet.getString("description");
                java.sql.Date fechaRegistro = resultSet.getDate("createDate");

                System.out.println("ID Usuario: " + idUsuario);
                System.out.println("Usuario: " + usuario);
                System.out.println("Email: " + email);
                System.out.println("Descripción: " + descripcion);
                System.out.println("Fecha de Registro: " + fechaRegistro);
                System.out.println();
            }
        } catch (SQLException e) {
            throw new SQLException("Error al obtener los perfiles de usuarios: " + e.getMessage());
        }
    }

    public static void showYourTweets(Connection con) throws SQLException {
        try {
            String query = "SELECT p.id, u.username, p.text, p.createDate " +
                    "FROM publications p " +
                    "JOIN users u ON p.userId = u.id " +
                    "WHERE p.userId = ?";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setInt(1, userID);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int idPublicacion = resultSet.getInt("id");
                String usuario = resultSet.getString("username");
                String texto = resultSet.getString("text");
                java.sql.Date fechaCreacion = resultSet.getDate("createDate");

                System.out.println("ID Publicación: " + idPublicacion);
                System.out.println("Usuario: " + usuario);
                System.out.println("Tweet: " + texto);
                System.out.println("Fecha de Creación: " + fechaCreacion);
                System.out.println();
            }
        } catch (SQLException e) {
            throw new SQLException("Error al obtener los tweets del usuario: " + e.getMessage());
        }
    }

    public static void deleteTweet(Connection con, int idTweet) {
        try {
            String query = "DELETE FROM publications WHERE id = ? AND userId = ?";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setInt(1, idTweet);
            statement.setInt(2, userID);

            int filasAfectadas = statement.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("Tweet eliminado con éxito.");
            } else {
                System.out.println("No se pudo eliminar el tweet. Verifica el ID del tweet o del usuario.");
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar el tweet: " + e.getMessage());
        }
    }

    public static void showYourFollows(Connection con) throws SQLException {
        try {
            String query = "SELECT u.* FROM users u JOIN follows s ON u.id = s.users_id WHERE s.userToFollowId = ?";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setInt(1, userID);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int idUsuario = resultSet.getInt("id");
                String usuario = resultSet.getString("username");
                String email = resultSet.getString("email");
                String descripcion = resultSet.getString("description");
                java.sql.Date fechaRegistro = resultSet.getDate("createDate");

                System.out.println("ID Usuario: " + idUsuario);
                System.out.println("Usuario: " + usuario);
                System.out.println("Email: " + email);
                System.out.println("Descripción: " + descripcion);
                System.out.println("Fecha de Registro: " + fechaRegistro);
                System.out.println();
            }
        } catch (SQLException e) {
            throw new SQLException("Error al obtener los usuarios seguidos: " + e.getMessage());
        }
    }

    public static void showYourFollowers(Connection con) throws SQLException {
        try {
            String query = "SELECT u.* " +
                    "FROM users u " +
                    "JOIN follows s ON u.id = s.users_id " +
                    "WHERE s.userToFollowId = ?";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setInt(1, userID);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int idUsuario = resultSet.getInt("id");
                String usuario = resultSet.getString("username");
                String email = resultSet.getString("email");
                String descripcion = resultSet.getString("description");
                java.sql.Date fechaRegistro = resultSet.getDate("createDate");

                System.out.println("ID Usuario: " + idUsuario);
                System.out.println("Usuario: " + usuario);
                System.out.println("Email: " + email);
                System.out.println("Descripción: " + descripcion);
                System.out.println("Fecha de Registro: " + fechaRegistro);
                System.out.println();
            }
        } catch (SQLException e) {
            throw new SQLException("Error al obtener los seguidores del usuario: " + e.getMessage());
        }
    }

    public static void showOtherProfile(Connection con, String nombreUsuario) throws SQLException {
        try {
            String query = "SELECT * FROM users WHERE username = ?";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, nombreUsuario);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int idUsuario = resultSet.getInt("id");
                String usuario = resultSet.getString("username");
                String email = resultSet.getString("email");
                String descripcion = resultSet.getString("description");
                java.sql.Date fechaRegistro = resultSet.getDate("createDate");

                System.out.println("ID Usuario: " + idUsuario);
                System.out.println("Usuario: " + usuario);
                System.out.println("Email: " + email);
                System.out.println("Descripción: " + descripcion);
                System.out.println("Fecha de Registro: " + fechaRegistro);
            } else {
                System.out.println("No se encontró el perfil del usuario con ese nombre de usuario.");
            }
        } catch (SQLException e) {
            throw new SQLException("Error al obtener el perfil del usuario: " + e.getMessage());
        }
    }

    public static void follow(Connection con, int idUsuarioSeguido) {
        try {
            String query = "INSERT INTO follows (users_id, userToFollowId) VALUES (?, ?)";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setInt(1, userID);
            statement.setInt(2, idUsuarioSeguido);

            int filasAfectadas = statement.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("Ahora sigues al usuario con ID " + idUsuarioSeguido);
            } else {
                System.out.println("No se pudo realizar el seguimiento. Verifica los IDs de usuario.");
            }
        } catch (SQLException e) {
            System.err.println("Error al realizar el seguimiento: " + e.getMessage());
        }
    }

    public static void unfollow(Connection con, int idUsuarioSeguido) {
        try {
            String query = "DELETE FROM follows WHERE users_id = ? AND userToFollowId = ?";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setInt(1, userID);
            statement.setInt(2, idUsuarioSeguido);

            int filasAfectadas = statement.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("Ya no sigues al usuario con ID " + idUsuarioSeguido);
            } else {
                System.out.println("No se pudo dejar de seguir al usuario. Verifica los IDs de usuario.");
            }
        } catch (SQLException e) {
            System.err.println("Error al dejar de seguir al usuario: " + e.getMessage());
        }
    }
}