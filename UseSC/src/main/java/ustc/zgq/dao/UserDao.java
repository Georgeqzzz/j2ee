package ustc.zgq.dao;

import java.sql.SQLException;
import java.util.List;

import ustc.zgq.model.User;

public interface UserDao {

    User query(String sql);
    User getUserById(Integer id);
    User getUserByName(String name);
    Boolean insertUser(User user) throws SQLException;
    Boolean updateUser(User user);
    Boolean deleteUserByName(String userName);
    Boolean deleteUserById(Integer id);

    List<User> getUsers();
}