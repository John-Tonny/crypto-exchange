package info.lftong.ui.recharge;


import info.lftong.data.DataSource;
import info.lftong.ui.recharge.RechargeJlqContract;

import java.util.List;

/**
 * Created by Administrator on 2017/9/25.
 */

public class RechargeJlqPresenter implements RechargeJlqContract.Presenter {
    private final DataSource dataRepository;
    private final RechargeJlqContract.View view;

    public RechargeJlqPresenter(DataSource dataRepository, RechargeJlqContract.View view) {
        this.dataRepository = dataRepository;
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public void depositJLQ(String token, String amount, String address, String password) {
        view.displayLoadingPopup();
        dataRepository.depositJLQ(token, amount, address, password, new DataSource.DataCallback() {
            @Override
            public void onDataLoaded(Object obj) {
                view.hideLoadingPopup();
                view.depositJLQSuccess((String) obj);
            }

            @Override
            public void onDataNotAvailable(Integer code, String toastMessage) {
                view.hideLoadingPopup();
                view.depositJLQFail(code, toastMessage);

            }
        });
    }
}
