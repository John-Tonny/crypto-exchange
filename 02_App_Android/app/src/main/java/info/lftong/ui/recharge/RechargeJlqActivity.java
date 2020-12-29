package info.lftong.ui.recharge;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.*;
import butterknife.BindView;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.gyf.barlibrary.ImmersionBar;
import info.lftong.R;
import info.lftong.app.Injection;
import info.lftong.app.MyApplication;
import info.lftong.app.UrlFactory;
import info.lftong.base.BaseActivity;
import info.lftong.entity.Coin;
import info.lftong.ui.extract.ExtractPresenter;
import info.lftong.ui.recharge.RechargeJlqContract;
import info.lftong.utils.*;
import info.lftong.utils.okhttp.StringCallback;
import info.lftong.utils.okhttp.WonderfulOkhttpUtils;
import okhttp3.Request;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class RechargeJlqActivity extends BaseActivity implements RechargeJlqContract.View {

    @BindView(R.id.llTitle)
    LinearLayout llTitle;
    @BindView(R.id.ibBack)
    ImageButton ibBack;
    @BindView(R.id.tvTitle)
    TextView tvTitle;

    @BindView(R.id.etAddress)
    EditText etAddress;
    @BindView(R.id.etCount)
    EditText etCount;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.tvReCharge)
    TextView tvReCharge;

    @BindView(R.id.yan)
    ImageView yan;
    private boolean isYan=false;

    @BindView(R.id.text_deposit)
    TextView text_deposit;
    @BindView(R.id.view_back)
    View view_back;

    private Coin coin;
    private int biaoshi=0;

    private RechargeJlqContract.Presenter presenter;

    public static void actionStart(Context context, Coin coin) {
        Intent intent = new Intent(context, RechargeJlqActivity.class);
        intent.putExtra("coin", coin);
        context.startActivity(intent);
    }

    @Override
    protected int getActivityLayoutId() {
        return R.layout.activity_recharge_jlq;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        new RechargeJlqPresenter(Injection.provideTasksRepository(getApplicationContext()), this);
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        view_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvReCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recharge();
            }
        });

        this.coin = (Coin) getIntent().getSerializableExtra("coin");
        etAddress.setText("正在连接" + this.coin.getUnit() + "节点...");

        yan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isYan=!isYan;
                Drawable no = getResources().getDrawable(R.drawable.yan_no);
                Drawable yes = getResources().getDrawable(R.drawable.yan_yes);
                if (isYan){
                    //显示
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    yan.setImageDrawable(no);

                }else {
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    yan.setImageDrawable(yes);
                }
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (biaoshi==0){
            biaoshi=1;
            if(coin.getCoin().getAccountType() == 0) {
                if (coin.getAddress() == null || "".equals(coin.getAddress())) {
                    displayLoadingPopup();
                    Timer timer = new Timer();
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    huoqu();
                                }
                            });
                        }
                    };
                    timer.schedule(task, 5000);
                } else {
                    text_deposit.setText(String.format("• 提交充值请求后，将会扣除商城中的余额转入到交易平台JLQ账户。\n• 最小充值金额：%s %s，小于最小金额的充值将不会上账。\n• 请勿向上述地址充值任何非币种资产，否则资产将不可找回。\n• 您充值至上述地址后，需要整个区块链网络节点的确认，一般区块链主网3次网络确认后到账。\n• 您的充值地址不会经常改变，可以重复充值；如有更改，我们会尽量通过网站公告或邮件通知您。\n• 请务必确认操作环境安全，防止信息被篡改或泄露。", coin.getCoin().getMinRechargeAmount(), coin.getCoin().getUnit()));
                    erciLoad();
                }
            }else{
                if(coin.getCoin().getDepositAddress() == null || coin.getCoin().getDepositAddress().equals("")) {
                    WonderfulToastUtils.showToast("暂无充值地址，请不要充值！");
                }else{
                    text_deposit.setText(String.format("• 提交充值请求后，将会扣除商城中的余额转入到交易平台JLQ账户。\n• 最小充值金额：%s %s，小于最小金额的充值将不会上账。\n• 请勿向上述地址充值任何非币种资产，否则资产将不可找回。\n• 您充值至上述地址后，需要整个区块链网络节点的确认，一般区块链主网3次网络确认后到账。\n• 您的充值地址不会经常改变，可以重复充值；如有更改，我们会尽量通过网站公告或邮件通知您。\n• 请务必确认操作环境安全，防止信息被篡改或泄露。", coin.getCoin().getMinRechargeAmount(), coin.getCoin().getUnit()));
                    erciLoad();
                }
            }
        }

    }

    @Override
    protected void obtainData() {

    }

    @Override
    protected void fillWidget() {

    }

    private void erciLoad(){
        tvReCharge.setEnabled(false);
        if(tvTitle==null){
            return;
        }

        tvTitle.setText(coin.getCoin().getUnit() + WonderfulToastUtils.getString(R.string.chargeMoney));
        if(coin.getCoin().getAccountType() == 0){
            etAddress.setText(coin.getAddress());
        }else{
            etAddress.setText(coin.getCoin().getDepositAddress());
        }

        if(coin == null){
            etAddress.setText(WonderfulToastUtils.getString(R.string.unChargeMoneyTip1));
            return;
        }
        if(coin.getCoin() == null){
            etAddress.setText(WonderfulToastUtils.getString(R.string.unChargeMoneyTip1));
            return;
        }
        if(coin.getCoin().getAccountType() == 0 && coin.getAddress() == null){
            etAddress.setText(WonderfulToastUtils.getString(R.string.unChargeMoneyTip1));
            return;
        }

        if(coin.getCoin().getAccountType() == 1 && coin.getCoin().getDepositAddress() == null){
            etAddress.setText(WonderfulToastUtils.getString(R.string.unChargeMoneyTip1));
            return;
        }

        tvReCharge.setEnabled(true);
    }

    @Override
    protected void loadData() {
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        if (!isSetTitle) {
            ImmersionBar.setTitleBar(this, llTitle);
            isSetTitle = true;
        }
    }

    private void huoqu(){
        WonderfulOkhttpUtils.post().url(UrlFactory.getWalletUrl()).addHeader("x-auth-token", SharedPreferenceInstance.getInstance().getTOKEN()).build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                super.onError(request,e);
                WonderfulLogUtils.logi("获取所有钱包出错", "获取所有钱包出错：" + e.getMessage());
                hideLoadingPopup();
            }

            @Override
            public void onResponse(String response) {
                WonderfulLogUtils.logi("获取所有钱包回执：", "获取所有钱包回执：" + response.toString());
                hideLoadingPopup();
                try {
                    JSONObject object = new JSONObject(response);
                    String formatStr = "• 提交充值请求后，将会扣除商城中的余额转入到交易平台JLQ账户。\n• 最小充值金额：%s %s，小于最小金额的充值将不会上账。\n• 请勿向上述地址充值任何非币种资产，否则资产将不可找回。\n• 您充值至上述地址后，需要整个区块链网络节点的确认，一般区块链主网3次网络确认后到账。\n• 您的充值地址不会经常改变，可以重复充值；如有更改，我们会尽量通过网站公告或邮件通知您。\n• 请务必确认操作环境安全，防止信息被篡改或泄露。";
                    if (object.optInt("code") == 0) {
                        List<Coin> objs = gson.fromJson(object.getJSONArray("data").toString(), new TypeToken<List<Coin>>() {
                        }.getType());
                        for (int i=0;i<objs.size();i++){
                            Coin coin1=objs.get(i);
                            WonderfulLogUtils.logi("miao",coin1.getId()+"-----"+coin.getId());
                            if (coin.getId()==coin1.getId()){
                                WonderfulLogUtils.logi("miao",coin1.getAddress()+"-----");
                                if(coin1.getCoin().getAccountType() == 1){
                                    // EOS类型账户地址
                                    coin.setAddress(coin1.getCoin().getDepositAddress());
                                    text_deposit.setText(String.format(formatStr, coin1.getCoin().getMinRechargeAmount(), coin1.getCoin().getUnit()));
                                    erciLoad();
                                    break;
                                }else {
                                    coin.setAddress(coin1.getAddress());
                                    text_deposit.setText(String.format(formatStr, coin1.getCoin().getMinRechargeAmount(), coin1.getCoin().getUnit()));
                                    erciLoad();
                                    break;
                                }
                            }
                        }
                        if (coin.getAddress()==null||"".equals(coin.getAddress())){
                            WonderfulToastUtils.showToast("区块链正在生成地址，请稍候退出我的资产界面重新进入。");
                        }
                    } else {
                        WonderfulToastUtils.showToast(""+object.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void recharge() {
        String address = etAddress.getText().toString();
        if (WonderfulStringUtils.isEmpty(address)){
            return;
        }
        String amount = etCount.getText().toString();
        String password = etPassword.getText().toString();
        if (WonderfulStringUtils.isEmpty(amount) || WonderfulStringUtils.isEmpty(password)) {
            WonderfulToastUtils.showToast(R.string.Incomplete_information);
            return;
        }else {
            WonderfulLogUtils.logi("发送转入JLQ回执：", "发送转入JLQ回执：" );
            presenter.depositJLQ(SharedPreferenceInstance.getInstance().getTOKEN(), amount, address, password);
        }
    }

    @Override
    public void setPresenter(RechargeJlqContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void depositJLQSuccess(String obj) {
        WonderfulToastUtils.showToast(obj);
        finish();
    }

    @Override
    public void depositJLQFail(Integer code, String toastMessage) {
        WonderfulCodeUtils.checkedErrorCode(this, code, toastMessage);
    }
}
