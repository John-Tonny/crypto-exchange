package info.lftong.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import info.lftong.R;
import info.lftong.entity.CoinInfo;

import java.util.List;

/**
 * Created by Administrator on 2018/3/1.
 */

public class DialogCoinInfoAdapter extends BaseQuickAdapter<CoinInfo, BaseViewHolder> {
    public DialogCoinInfoAdapter(int layoutResId, @Nullable List<CoinInfo> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, CoinInfo item) {
        helper.setText(R.id.tvCoinName, item.getUnit());
        helper.setVisible(R.id.ivSellect, item.isSelected());
    }
}
