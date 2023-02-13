package com.evport.businessapp.domain.request;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kunminx.architecture.domain.usecase.UseCase;
import com.kunminx.architecture.domain.usecase.UseCaseHandler;
import com.evport.businessapp.data.bean.DownloadFile;
import com.evport.businessapp.data.repository.DataRepository;
import com.evport.businessapp.domain.usecase.CanBeStoppedUseCase;

/**
 * 信息列表 Request
 * <p>
 * TODO tip：Request 通常按业务划分
 * 一个项目中通常存在多个 Request
 * <p>
 * request 的职责仅限于 数据请求，不建议在此处理 UI 逻辑，
 * UI 逻辑只适合在 Activity/Fragment 等视图控制器中完成，是 “数据驱动” 的一部分，
 * 将来升级到 Jetpack Compose 更是如此。
 * <p>
 * 如果这样说还不理解的话，详见 https://xiaozhuanlan.com/topic/6257931840
 * <p>
 * <p>
 * Create by KunMinX at 20/03/18
 */
public class DownloadRequest implements Request.IDownloadRequest, Request.ICanBeStoppedDownloadRequest {

    private MutableLiveData<DownloadFile> mDownloadFileLiveData;

    private MutableLiveData<DownloadFile> mDownloadFileCanBeStoppedLiveData;

    private CanBeStoppedUseCase mCanBeStoppedUseCase;

    //TODO tip 向 ui 层提供的 request LiveData，使用抽象的 LiveData 而不是 MutableLiveData
    // 如此是为了来自数据层的数据，在 ui 层中只读，以避免团队新手不可预期的误用

    @Override
    public LiveData<DownloadFile> getDownloadFileLiveData() {
        if (mDownloadFileLiveData == null) {
            mDownloadFileLiveData = new MutableLiveData<>();
        }
        return mDownloadFileLiveData;
    }

    @Override
    public LiveData<DownloadFile> getDownloadFileCanBeStoppedLiveData() {
        if (mDownloadFileCanBeStoppedLiveData == null) {
            mDownloadFileCanBeStoppedLiveData = new MutableLiveData<>();
        }
        return mDownloadFileCanBeStoppedLiveData;
    }

    @Override
    public CanBeStoppedUseCase getCanBeStoppedUseCase() {
        if (mCanBeStoppedUseCase == null) {
            mCanBeStoppedUseCase = new CanBeStoppedUseCase();
        }
        return mCanBeStoppedUseCase;
    }

    @Override
    public void requestDownloadFile() {
        DataRepository.Companion.getInstance().downloadFile(mDownloadFileLiveData);
    }

    //TODO tip2：
    // 同样是“下载”，我不是在数据层分别写两个方法，
    // 而是遵循开闭原则，在 vm 和 数据层之间，插入一个 UseCase，来专门负责可叫停的情况，
    // 除了开闭原则，使用 UseCase 还有个考虑就是避免内存泄漏，
    // 具体缘由可详见 https://xiaozhuanlan.com/topic/6257931840 评论区 15 楼

    @Override
    public void requestCanBeStoppedDownloadFile() {
        UseCaseHandler.getInstance().execute(getCanBeStoppedUseCase(),
                new CanBeStoppedUseCase.RequestValues(mDownloadFileCanBeStoppedLiveData),
                new UseCase.UseCaseCallback<CanBeStoppedUseCase.ResponseValue>() {
                    @Override
                    public void onSuccess(CanBeStoppedUseCase.ResponseValue response) {
                        mDownloadFileCanBeStoppedLiveData.setValue(response.getLiveData().getValue());
                    }

                    @Override
                    public void onError() {

                    }
                });
    }
}
