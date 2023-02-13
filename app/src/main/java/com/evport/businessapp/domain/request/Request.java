package com.evport.businessapp.domain.request;


import androidx.lifecycle.LiveData;

import com.evport.businessapp.data.bean.ChangePassword;
import com.evport.businessapp.data.bean.ChargeSetting;
import com.evport.businessapp.data.bean.DownloadFile;
import com.evport.businessapp.data.bean.RequestCharge;
import com.evport.businessapp.data.bean.RequestChargeChange;
import com.evport.businessapp.data.bean.RequestRecord;
import com.evport.businessapp.data.bean.RecordResp;
import com.evport.businessapp.data.bean.RequestScan;
import com.evport.businessapp.data.bean.RequestStats;
import com.evport.businessapp.data.bean.StatsDataResp;
import com.evport.businessapp.data.bean.TestAlbum;
import com.evport.businessapp.data.bean.User;
import com.evport.businessapp.domain.usecase.CanBeStoppedUseCase;

import java.util.List;

/**
 * Create by KunMinX at 2020/5/21
 */
public class Request {

    public interface IAccountRequest {

        LiveData<User> getUserLiveData();

        void requestLogin(User user);

        void requestSignUp(User user);

        void resetPassword(User user);

        void changePassword(ChangePassword changePassword);

        void sendEmailCode(String email, int from);

        void checkUserName(String name);

    }

    public interface IDoMainRequest {

        LiveData<String> getDomainLiveData();

        void requestDomain();

    }


    public interface IChargeSettingRequest {

        LiveData<List<ChargeSetting>> getChargeSettingLiveData();


        void unBindFamily(RequestScan requestScan);

        void requestChargeList();

        void requestChargeSet(ChargeSetting chargeSetting);


    }

    public interface IStatsRequest {

        LiveData<StatsDataResp> getStatsLiveData();

        void requestStats(RequestStats requestStats);

    }

    public interface IRecordRequest {

        LiveData<List<RecordResp>> getRecordLiveData();

        void requestRecord(RequestRecord requestRecord);

    }


    public interface IScanRequest {

        LiveData<Boolean> getScanLiveData();

        void requestScan(RequestScan requestScan);

        void requestChargeGun(RequestScan requestScan);

        void requestChargePoint();

        void getCheckTransactions();

        void setChargingSchedule(RequestCharge requestCharge);

        void setChargingScheduleStart(RequestCharge requestCharge);

        void setChargingScheduleUpdate(RequestCharge requestCharge);

        void setChargingScheduleDelete(RequestCharge requestCharge);

        void setAutoSwitch(RequestChargeChange requestChargeChange);

        void remoteStart(RequestChargeChange requestChargeChange);

        void remoteStop(RequestChargeChange requestChargeChange);

        void bindFamily(RequestScan requestScan);


    }


//
//    public abstract class IAccountRequest {
//
//       abstract LiveData<User> getUserLiveData();
//
//       abstract void requestLogin(User user);
//
//       abstract void requestSignUp(User user);
//
//       abstract void resetPassword(User user);

    //    }
    public interface IDownloadRequest {


        LiveData<DownloadFile> getDownloadFileLiveData();

        void requestDownloadFile();

    }

    public interface ICanBeStoppedDownloadRequest {


        LiveData<DownloadFile> getDownloadFileCanBeStoppedLiveData();

        CanBeStoppedUseCase getCanBeStoppedUseCase();

        void requestCanBeStoppedDownloadFile();

    }

    public interface IMusicRequest {

        LiveData<TestAlbum> getFreeMusicsLiveData();

        void requestFreeMusics();
    }
}
