package com.jstyle.test2025.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.jstyle.blesdk2025.Util.BleSDK;
import com.jstyle.blesdk2025.model.Notifier;
import com.jstyle.test2025.R;
import com.jstyle.test2025.adapter.NotifyAdapter;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NotifyActivity extends BaseActivity {

    @BindView(R.id.RecyclerView_notify)
    RecyclerView RecyclerViewNotify;
    @BindArray(R.array.nofityarray)
    String[] array;
    NotifyAdapter notifyAdapter;
    @BindView(R.id.ed_content)
    EditText edContent;
    @BindView(R.id.bt_send)
    Button btSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        //new AlertDialog.Builder(this).setSingleChoiceItems()
        GridLayoutManager linearLayoutManager = new GridLayoutManager(this, 3);
      //  linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        RecyclerViewNotify.setLayoutManager(linearLayoutManager);
        notifyAdapter = new NotifyAdapter(array);
        RecyclerViewNotify.setAdapter(notifyAdapter);
        //  DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        // RecyclerViewNotify.addItemDecoration(dividerItemDecoration);

    }


    @OnClick(R.id.bt_send)
    public void onViewClicked() {
        int type=getNotifyType();
        String content=edContent.getText().toString();
        if(TextUtils.isEmpty(content))return;
        Notifier notifier=new Notifier();
        notifier.setInfo(content);
        notifier.setType(type);
        sendValue(BleSDK.setNotifyData(notifier));
    }

    public int getNotifyType() {
        int type=notifyAdapter.getCheck()==array.length-1?0xff:notifyAdapter.getCheck();
        return type;
    }
}
