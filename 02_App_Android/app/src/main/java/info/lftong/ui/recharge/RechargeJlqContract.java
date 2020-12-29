package info.lftong.ui.recharge;


import info.lftong.base.Contract;

import java.util.List;

/**
 * Created by Administrator on 2017/9/25.
 */

public interface RechargeJlqContract {
    interface View extends Contract.BaseView<Presenter> {

        void depositJLQSuccess(String obj);

        void depositJLQFail(Integer code, String toastMessage);
    }

    interface Presenter extends Contract.BasePresenter {
        void depositJLQ(String token, String amount, String address, String password);
    }
}
