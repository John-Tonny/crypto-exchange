package info.lftong.ui.extract;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;
import info.lftong.R;
import info.lftong.adapter.TextWatcher;
import info.lftong.app.MyApplication;
import info.lftong.base.BaseActivity;
import info.lftong.entity.Address;
import info.lftong.entity.Coin;
import info.lftong.entity.ExtractInfo;
import info.lftong.app.UrlFactory;
import info.lftong.entity.User;
import info.lftong.utils.SharedPreferenceInstance;
import info.lftong.utils.WonderfulCodeUtils;
import info.lftong.utils.WonderfulLogUtils;
import info.lftong.utils.WonderfulMathUtils;
import info.lftong.utils.WonderfulStringUtils;
import info.lftong.utils.WonderfulToastUtils;
import info.lftong.utils.okhttp.StringCallback;
import info.lftong.utils.okhttp.WonderfulOkhttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.List;

import butterknife.BindView;
import info.lftong.app.Injection;
import okhttp3.Request;

import static info.lftong.ui.extract.AddressActivity.RETURN_ADDRESS;

public class ExtractActivity extends BaseActivity implements ExtractContract.View {

    @BindView(R.id.ibBack)
    ImageButton ibBack;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.llTitle)
    LinearLayout llTitle;
    @BindView(R.id.tvCanUse)
    TextView tvCanUse;
    @BindView(R.id.tvUnit1)
    TextView tvUnit1;
    @BindView(R.id.tvAddress)
    TextView tvAddress;
    @BindView(R.id.etAddress)
    EditText etAddress;
    @BindView(R.id.ivInto)
    ImageView ivInto;
    @BindView(R.id.etCount)
    EditText etCount;
    @BindView(R.id.etServiceFee)
    EditText etServiceFee;
    @BindView(R.id.tvUnit2)
    TextView tvUnit2;
    @BindView(R.id.tvAll)
    TextView tvAll;
    @BindView(R.id.tvUnit3)
    TextView tvUnit3;
    @BindView(R.id.tvExtract)
    TextView tvExtract;
    @BindView(R.id.tvFinalCount)
    TextView tvFinalCount;
    @BindView(R.id.etPassword)
    EditText etPassword;

    // john
    @BindView(R.id.shopPassword)
    EditText shopPassword;
    @BindView(R.id.shopView)
    View shopView;
    @BindView(R.id.shopText)
    TextView shopText;
    @BindView(R.id.shopLayout)
    LinearLayout shopLayout;


    @BindView(R.id.text_remark)
    TextView text_remark;

    @BindView(R.id.layout_memo)
    LinearLayout layout_memo;

    @BindView(R.id.etext_remark)
    EditText etRemark;
    private Coin coin;
    private ExtractInfo extractInfo;
    private ExtractContract.Presenter presenter;
    @BindView(R.id.yan)
    ImageView yan;
    private boolean isYan=false;
    // john
    @BindView(R.id.yan1)
    ImageView yan1;
    private boolean isYan1=false;
    @BindView(R.id.tvGetCode)
    TextView tvGetCode;
    @BindView(R.id.etCode)
    EditText etCode;
    private CountDownTimer timer;
    @BindView(R.id.view_back)
    View view_back;
    public static void actionStart(Context context, Coin coin) {
        Intent intent = new Intent(context, ExtractActivity.class);
        intent.putExtra("coin", coin);
        context.startActivity(intent);
    }

    private void fillCodeView(long time) {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        timer = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (tvGetCode!=null){
                    tvGetCode.setText(getResources().getString(R.string.re_send) + "（" + millisUntilFinished / 1000 + "）");
                    tvGetCode.setEnabled(false);
                }
            }

            @Override
            public void onFinish() {
                tvGetCode.setText(R.string.send_code);
                tvGetCode.setEnabled(true);
                timer.cancel();
                timer = null;
            }
        };
        timer.start();
    }

    @Override
    protected int getActivityLayoutId() {
        return R.layout.activity_extract;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        new ExtractPresenter(Injection.provideTasksRepository(getApplicationContext()), this);
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
        ivInto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (extractInfo != null)
                    AddressActivity.actionStart(ExtractActivity.this, extractInfo.getAddresses());
                else WonderfulToastUtils.showToast(WonderfulToastUtils.getString(R.string.noAddAddressTip));
            }
        });
        etCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                calcuFinalCount();
            }
        });
        etServiceFee.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                calcuFinalCount();
            }
        });
        tvExtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extract();
            }
        });
        tvAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (extractInfo != null) etCount.setText(extractInfo.getBalance() + "");
            }
        });
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
        // john
        yan1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isYan1=!isYan1;
                Drawable no = getResources().getDrawable(R.drawable.yan_no);
                Drawable yes = getResources().getDrawable(R.drawable.yan_yes);
                if (isYan1){
                    //显示
                    shopPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    yan1.setImageDrawable(no);

                }else {
                    shopPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    yan1.setImageDrawable(yes);
                }
            }
        });


        tvGetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        });
    }

    private void send(){
        tvGetCode.setEnabled(false);
        WonderfulOkhttpUtils.post().url(UrlFactory.getCode()).addHeader("x-auth-token", SharedPreferenceInstance.getInstance().getTOKEN()).build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                super.onError(request,e);
                WonderfulLogUtils.logi("发送提币出错", "发送提币出错：" + e.getMessage());
                tvGetCode.setEnabled(true);
            }

            @Override
            public void onResponse(String response) {
                WonderfulLogUtils.logi("发送提币回执：", "发送提币回执：" + response.toString());
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.optInt("code") == 0) {
                        WonderfulToastUtils.showToast("发送成功");
                        fillCodeView(90 * 1000);
                    } else {
                        WonderfulToastUtils.showToast("发送失败");
                        tvGetCode.setEnabled(true);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    tvGetCode.setEnabled(true);
                }
            }
        });
    }

    private void extract() {
        if (extractInfo == null) return;
        String address = etAddress.getText().toString();
        String unit = extractInfo.getUnit();
        String amount = etCount.getText().toString();
        String fee = etServiceFee.getText().toString();
        String code=etCode.getText().toString();
        String remark = etRemark.getText().toString();
        if (WonderfulStringUtils.isEmpty(address) || WonderfulStringUtils.isEmpty(unit) ||
                WonderfulStringUtils.isEmpty(amount) || WonderfulStringUtils.isEmpty(fee)||WonderfulStringUtils.isEmpty(code)) {
            WonderfulToastUtils.showToast(R.string.Incomplete_information);
            return;
        }else {
            if (Double.valueOf(fee) < extractInfo.getMinTxFee() || Double.valueOf(fee) > extractInfo.getMaxTxFee()) {
                WonderfulToastUtils.showMyViewToast(WonderfulToastUtils.getString(R.string.addMoneyTip)+extractInfo.getMinTxFee()+"~"+extractInfo.getMaxTxFee());
                return;
            }
            if(extractInfo.getAccountType() == 1 && WonderfulStringUtils.isEmpty(remark)){
                WonderfulToastUtils.showToast("该币种需要输入转入地址Memo，请确认！");
                return;
            }
            if (Double.valueOf(amount) > Double.valueOf(tvCanUse.getText().toString())) {
                WonderfulToastUtils.showMyViewToast(WonderfulToastUtils.getString(R.string.lackOfBalance));
                return;
            }
            String jyPassword = etPassword.getText().toString();
            String spPassword = shopPassword.getText().toString();
            presenter.extract(SharedPreferenceInstance.getInstance().getTOKEN(), unit, amount, fee, remark, jyPassword, address, code, spPassword);
        }
    }

    private void calcuFinalCount() {
        if (extractInfo == null) return;
        String countStr = etCount.getText().toString();
        String serviceStr = etServiceFee.getText().toString();
        if (WonderfulStringUtils.isEmpty(countStr, serviceStr)) return;
        double count = Double.parseDouble(countStr);
        double service = Double.parseDouble(serviceStr);
        double finalCount = count - service;
        if (finalCount < 0) finalCount = 0;
        tvFinalCount.setText(WonderfulMathUtils.getRundNumber(finalCount, 4, null));
    }

    @Override
    protected void obtainData() {
        this.coin = (Coin) getIntent().getSerializableExtra("coin");
    }

    @Override
    protected void fillWidget() {
        tvTitle.setText(coin.getCoin().getUnit() + WonderfulToastUtils.getString(R.string.mentionMoney));
        tvUnit1.setText(coin.getCoin().getUnit());
        tvUnit2.setText(coin.getCoin().getUnit());
        tvUnit3.setText(coin.getCoin().getUnit());
        tvCanUse.setText( new BigDecimal(coin.getBalance()).setScale(8,BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString() + "");

        // john
        if(coin.getCoin().getUnit().compareToIgnoreCase("JLQ")==0){
            User user = MyApplication.getApp().getCurrentUser();
            etAddress.setText(user.getUsername());
            etAddress.setEnabled(false);
            tvAddress.setText("商城账号");
            ivInto.setVisibility(View.GONE);

            shopView.setVisibility(View.VISIBLE);
            shopText.setVisibility(View.VISIBLE);
            shopLayout.setVisibility(View.VISIBLE);
        }else {
            etAddress.setEnabled(true);
            tvAddress.setText("提币地址");
            ivInto.setVisibility(View.VISIBLE);

            shopView.setVisibility(View.GONE);
            shopText.setVisibility(View.GONE);
            shopLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void loadData() {
        presenter.extractinfo(getToken());
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        if (!isSetTitle) {
            ImmersionBar.setTitleBar(this, llTitle);
            isSetTitle = true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RETURN_ADDRESS) {
            if (resultCode == RESULT_OK && data != null) {
                etAddress.setText(((Address) data.getSerializableExtra("address")).getAddress());
            }
        }
    }

    @Override
    public void setPresenter(ExtractContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void extractinfoFail(Integer code, String toastMessage) {
        WonderfulCodeUtils.checkedErrorCode(this, code, toastMessage);
    }

    @Override
    public void extractinfoSuccess(List<ExtractInfo> obj) {
        if (obj == null) return;
        for (ExtractInfo extractInfo : obj) {
            if (coin.getCoin().getUnit().equals(extractInfo.getUnit())) {
                this.extractInfo = extractInfo;
                break;
            }
        }
        if (extractInfo == null) return;
        fillView();
    }

    @Override
    public void extractSuccess(String obj) {
        WonderfulToastUtils.showToast(obj);
        finish();
    }

    @Override
    public void extractFail(Integer code, String toastMessage) {
        WonderfulCodeUtils.checkedErrorCode(this, code, toastMessage);
    }

    private void fillView() {
        tvCanUse.setText(new BigDecimal(extractInfo.getBalance()).setScale(8,BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString() + "");
        etCount.setHint(WonderfulToastUtils.getString(R.string.addMoneyTip2) + new BigDecimal(String.valueOf(extractInfo.getMinAmount())).toPlainString());
        if(extractInfo.getAccountType() == 1) {
            text_remark.setVisibility(View.VISIBLE);
            layout_memo.setVisibility(View.VISIBLE);
        }else{
            text_remark.setVisibility(View.GONE);
            layout_memo.setVisibility(View.GONE);
        }
        if (extractInfo.getMinTxFee() == extractInfo.getMaxTxFee()) {
            etServiceFee.setText(new BigDecimal(String.valueOf(extractInfo.getMaxTxFee())).toPlainString());
            etServiceFee.setEnabled(false);
        }else etServiceFee.setHint(new BigDecimal(String.valueOf(extractInfo.getMinTxFee())).toPlainString()
                +" ~ "+new BigDecimal(String.valueOf(extractInfo.getMaxTxFee())).toPlainString());
    }

}
