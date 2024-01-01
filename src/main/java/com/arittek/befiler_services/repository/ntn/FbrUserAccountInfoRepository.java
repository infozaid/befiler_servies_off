package com.arittek.befiler_services.repository.ntn;

import com.arittek.befiler_services.model.enums.FbrUserAccountInfoStatus;
import com.arittek.befiler_services.model.ntn.FbrUserAccountInfo;
import com.arittek.befiler_services.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FbrUserAccountInfoRepository extends JpaRepository<FbrUserAccountInfo, Integer>, RevisionRepository<FbrUserAccountInfo, Integer, Integer> {
    FbrUserAccountInfo findOneByUser(User user);
    List<FbrUserAccountInfo> findAllByFbrUserAccountInfoStatus(FbrUserAccountInfoStatus fbrUserAccountInfoStatus);
    List<FbrUserAccountInfo> findAllByUser(User user);

//    @Query("SELECT COUNT(d.fbrUserAccountInfoStatus) FROM FbrUserAccountInfo d WHERE d.fbrUserAccountInfoStatus='6'")
//    Integer findNTNCount() throws Exception;

}
