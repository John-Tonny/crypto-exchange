package info.lftong.ui.home;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.BindView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import info.lftong.R;
import info.lftong.adapter.Homes2Adapter;
import info.lftong.app.GlobalConstant;
import info.lftong.app.Injection;
import info.lftong.app.MyApplication;
import info.lftong.entity.Currency;
import info.lftong.ui.home.presenter.CommonPresenter;
import info.lftong.ui.home.presenter.ICommonView;
import info.lftong.ui.login.LoginActivity;
import info.lftong.utils.WonderfulCodeUtils;
import info.lftong.utils.WonderfulLogUtils;
import info.lftong.utils.WonderfulToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/29.
 * JLQMarketFragment
 */

public class JLQMarket2Fragment extends MarketBaseFragment implements ICommonView {
    @BindView(R.id.rvContent)
    RecyclerView rvContent;
   // private HomeTwoAdapter adapter;
    private Homes2Adapter mAdapter;
    private List<Currency> currencies = new ArrayList<>();
    private CommonPresenter commonPresenter;
    private int type;

    public static JLQMarket2Fragment getInstance(int type) {
        JLQMarket2Fragment jlqMarketFragment = new JLQMarket2Fragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        jlqMarketFragment.setArguments(bundle);
        return jlqMarketFragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_jlq_market;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        WonderfulLogUtils.loge("INFO", "执行了" + isVisibleToUser);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        type = getArguments().getInt("type");
        new CommonPresenter(Injection.provideTasksRepository(getActivity().getApplicationContext()), this);
    }

    @Override
    protected void obtainData() {

    }

    @Override
    protected void fillWidget() {
        initRvContent();
    }

    private void initRvContent() {
        LinearLayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvContent.setLayoutManager(manager);
        mAdapter = new Homes2Adapter( currencies, 1);
//        adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
//        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
//            @Override
//            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
//                collectClick(position);
//            }
//        });
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ((MarketOperateCallback) getActivity()).itemClick(JLQMarket2Fragment.this.mAdapter.getItem(position), type);
            }
        });
        mAdapter.isFirstOnly(true);
        mAdapter.setLoad(true);
        rvContent.setAdapter(mAdapter);
    }

    private void collectClick(int position) {
        if (!MyApplication.getApp().isLogin()) {
            WonderfulToastUtils.showToast("请先登录！");
            return;
        }
        String symbol = currencies.get(position).getSymbol();
        if (currencies.get(position).isCollect()) commonPresenter.delete(getmActivity().getToken(), symbol, position);
        else commonPresenter.add(getmActivity().getToken(), symbol, position);
    }

    @Override
    protected void loadData() {
        notifyData();
    }

    @Override
    protected boolean isImmersionBarEnabled() {
        return false;
    }

    public void dataLoaded(List<Currency> baseUsdt) {
        this.currencies.clear();
        this.currencies.addAll(baseUsdt);
        if (mAdapter != null) mAdapter.notifyDataSetChanged();
    }
    public void isChange(boolean isLoad){
        if(mAdapter!=null){
            mAdapter.setLoad(isLoad);
            mAdapter.notifyDataSetChanged();
        }
    }
    @Override
    public void setPresenter(CommonPresenter presenter) {
        this.commonPresenter = presenter;
    }

    @Override
    public void deleteFail(Integer code, String toastMessage) {
        if (code == GlobalConstant.TOKEN_DISABLE1) LoginActivity.actionStart(getActivity());
        else WonderfulCodeUtils.checkedErrorCode(getmActivity(), code, toastMessage);
    }

    @Override
    public void deleteSuccess(String obj, int position) {
        this.currencies.get(position).setCollect(false);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void addFail(Integer code, String toastMessage) {
        if (code == GlobalConstant.TOKEN_DISABLE1) LoginActivity.actionStart(getActivity());
        else WonderfulCodeUtils.checkedErrorCode(getmActivity(), code, toastMessage);
    }

    @Override
    public void addSuccess(String obj, int position) {
        this.currencies.get(position).setCollect(true);
        mAdapter.notifyDataSetChanged();
    }

    public void notifyData() {
        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }

    public void tcpNotify() {
        if (getUserVisibleHint() && mAdapter != null) mAdapter.notifyDataSetChanged();
    }
}
