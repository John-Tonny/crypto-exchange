package info.lftong.ui.home.presenter;

import info.lftong.ui.home.MainContract;
import info.lftong.data.DataSource;

/**
 * Created by Administrator on 2018/2/24.
 */

public class TwoPresenter implements MainContract.TwoPresenter {
    private MainContract.TwoView view;
    private DataSource dataRepository;

    public TwoPresenter(DataSource dataRepository, MainContract.TwoView view) {
        this.view = view;
        this.dataRepository = dataRepository;
        this.view.setPresenter(this);
    }


}
