package ustc.zgq.model;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class User {
    private Integer userId;
    private String userName;
    private String userPass;

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", userPass='" + userPass + '\'' +
                '}';
    }
}