package govind.dao;

import govind.dao.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserDao {
	List<UserEntity> findAll();
}
