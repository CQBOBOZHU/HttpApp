package com.bobozhu.httpapp;

import android.app.Dialog;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemDragListener;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Flowable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.R.attr.onClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.http_protocol)
    RadioButton httpProtocol;
    @BindView(R.id.https_protocol)
    RadioButton httpsProtocol;
    @BindView(R.id.domain_edt)
    EditText domainEdt;
    @BindView(R.id.port_edt)
    EditText portEdt;
    @BindView(R.id.path_edt)
    EditText pathEdt;
    @BindView(R.id.url_tv)
    TextView urlTv;
    @BindView(R.id.header_key)
    EditText headerKey;
    @BindView(R.id.header_value)
    EditText headerValue;
    @BindView(R.id.header_addTv)
    TextView headerAddTv;
    @BindView(R.id.header_recyclerView)
    RecyclerView headerRecyclerView;
    @BindView(R.id.http_method_post)
    RadioButton httpMethodPost;
    @BindView(R.id.http_method_get)
    RadioButton httpMethodGet;
    @BindView(R.id.params_key)
    EditText paramsKey;
    @BindView(R.id.params_value)
    EditText paramsValue;
    @BindView(R.id.params_addTv)
    TextView paramsAddTv;
    @BindView(R.id.params_recyclerView)
    RecyclerView paramsRecyclerView;
    @BindView(R.id.sure_btn)
    Button sureBtn;
    @BindView(R.id.radio_group)
    RadioGroup radioGroup;
    @BindView(R.id.response_text)
    TextView responseTv;
    private String TAG = this.getClass().getSimpleName();
    BAlterDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        addTextWacher();
        initRecyclerView();
        urlChange();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                urlChange();
            }
        });
        responseTv.setText("请求得到的数据，长按可以选择对数据的处理");
        responseTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (dialog == null)
                    dialog = new BAlterDialog(MainActivity.this, "选择数据的处理", "复制", "清除", new DialogClickListener() {
                        @Override
                        public void doLeft() {
                            dialog.dismiss();
                            Util.copy(MainActivity.this, responseTv.getText().toString());
                        }

                        @Override
                        public void doRight() {
                            dialog.dismiss();
                            responseTv.setText("请求得到的数据，长按可以选择对数据的处理");
                        }
                    });
                dialog.show();
                return false;
            }
        });

        initText();
    }

    private void initText() {
        String params = SharePreferenceUtils.getString("params");
        Gson gson = new Gson();
        if (!TextUtils.isEmpty(params)) {
            mParams = gson.fromJson(params, new TypeToken<List<Bean>>() {
            }.getType());
            paramAdapter.setNewData(mParams);
        }
        String headers = SharePreferenceUtils.getString("header");
        if (!TextUtils.isEmpty(params)) {
            mHeaders = gson.fromJson(headers, new TypeToken<List<Bean>>() {
            }.getType());
            headerAdapter.setNewData(mHeaders);
        }
        String domain = SharePreferenceUtils.getString("domain");
        if (!TextUtils.isEmpty(domain)) {
            domainEdt.setText(domain);
        }
        String port = SharePreferenceUtils.getString("port");
        if (!TextUtils.isEmpty(port)) {
            portEdt.setText(port);
        }
        String path = SharePreferenceUtils.getString("path");
        if (!TextUtils.isEmpty(path)) {
            pathEdt.setText(path);
        }
    }

    BaseItemDraggableAdapter<Bean, BaseViewHolder> paramAdapter;
    BaseItemDraggableAdapter<Bean, BaseViewHolder> headerAdapter;

    private void initRecyclerView() {
        headerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        paramsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        paramAdapter = new BaseItemDraggableAdapter<Bean, BaseViewHolder>(R.layout.item_tv, mParams) {
            @Override
            protected void convert(BaseViewHolder helper, Bean item) {
                helper.setText(R.id.keyTv, item.getKey())
                        .setText(R.id.valueTv, item.getValue());
            }
        };
        headerAdapter = new BaseItemDraggableAdapter<Bean, BaseViewHolder>(R.layout.item_tv, mHeaders) {
            @Override
            protected void convert(BaseViewHolder helper, Bean item) {
                helper.setText(R.id.keyTv, item.getKey())
                        .setText(R.id.valueTv, item.getValue());
            }
        };
        headerRecyclerView.setAdapter(headerAdapter);
        paramsRecyclerView.setAdapter(paramAdapter);
        swip(headerAdapter, headerRecyclerView);
        swip(paramAdapter, paramsRecyclerView);
    }

    public void swip(BaseItemDraggableAdapter mAdapter, RecyclerView mRecyclerView) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(20);
        paint.setColor(Color.BLACK);
        OnItemSwipeListener onItemSwipeListener = new OnItemSwipeListener() {
            @Override
            public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int pos) {
                Log.d(TAG, "view swiped start: " + pos);
            }

            @Override
            public void clearView(RecyclerView.ViewHolder viewHolder, int pos) {
                Log.d(TAG, "View reset: " + pos);
                BaseViewHolder holder = ((BaseViewHolder) viewHolder);
            }

            @Override
            public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int pos) {
                Log.d(TAG, "View Swiped: " + pos);
            }

            @Override
            public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY, boolean isCurrentlyActive) {
                canvas.drawColor(ContextCompat.getColor(MainActivity.this, R.color.colorAccent));
            }
        };
        ItemDragAndSwipeCallback mItemDragAndSwipeCallback = new ItemDragAndSwipeCallback(mAdapter);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(mItemDragAndSwipeCallback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
        mItemDragAndSwipeCallback.setSwipeMoveFlags(ItemTouchHelper.START | ItemTouchHelper.END);
        mAdapter.enableSwipeItem();
        mAdapter.setOnItemSwipeListener(onItemSwipeListener);
        mAdapter.enableDragItem(mItemTouchHelper);
        mRecyclerView.setAdapter(mAdapter);
    }


    private void addTextWacher() {
        domainEdt.addTextChangedListener(myWacher);
        portEdt.addTextChangedListener(myWacher);
        pathEdt.addTextChangedListener(myWacher);
    }

    MyTextWacher myWacher = new MyTextWacher() {
        @Override
        public void afterTextChanged(Editable s) {
            urlChange();
        }
    };

    private void urlChange() {
        String protocol;
        if (httpProtocol.isChecked()) {
            protocol = "http://";
        } else
            protocol = "https://";
        if (Util.IsIP(domainEdt.getText().toString().trim())) {
            urlTv.setText(protocol + domainEdt.getText().toString().trim() + ":"
                    + portEdt.getText().toString().trim() + "/"
                    + pathEdt.getText().toString().trim());
        } else {
            urlTv.setText(protocol + domainEdt.getText().toString().trim() + "/"
                    + pathEdt.getText().toString().trim());
        }
    }

    @OnClick({R.id.header_addTv, R.id.params_addTv, R.id.sure_btn, R.id.clean_paramsTv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.header_addTv:
                addHeader();
                break;
            case R.id.params_addTv:
                addParam();
                break;
            case R.id.sure_btn:
                if (httpMethodPost.isChecked()) {
                    post();
                } else {
                    get();
                }
                break;
            case R.id.clean_paramsTv:
                endTime = System.currentTimeMillis();
                if (endTime - startTime < 300) {
                    mParams.clear();
                    paramAdapter.setNewData(mParams);
                }
                startTime = endTime;
                break;
        }
    }

    long endTime;
    long startTime;

    private void post() {
        Map<String, String> headerMap = getMap(mHeaders);
        Map<String, String> paramMap = getMap(mParams);
        showLoadingDialog();
        RetrofitUtil.getRetrofit("http://gank.io/").create(Api.class).post(urlTv.getText().toString(), headerMap, paramMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);

    }

    Disposable disposable;
    Observer observer = new Observer<ResponseBody>() {

        @Override
        public void onSubscribe(@NonNull Disposable d) {
            disposable = d;
        }

        @Override
        public void onNext(ResponseBody o) {
            stopLoadingDialog();
            try {
                String s = o.string();
                StringBuilder sb = new StringBuilder();
                sb.append("URL   :" + urlTv.getText().toString());
                sb.append("\n");
                sb.append("HEADERS   :" + getMapString(mHeaders));
                sb.append("\n");
                sb.append("PARAMS   :" + getMapString(mParams));
                sb.append("\n");
                sb.append("ResponseBody   :" + s);
                responseTv.setText(sb.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        @Override
        public void onError(Throwable t) {
            stopLoadingDialog();
            Log.v("onNext", t.getLocalizedMessage());
            responseTv.setText("onError: \n LocalizedMessage:" + t.getLocalizedMessage() + "\n Message: " + t.getMessage());

        }

        @Override
        public void onComplete() {
            stopLoadingDialog();
            Log.v("onComplete", "onComplete");
        }
    };

    public Map<String, String> getMap(List<Bean> mList) {
        Map<String, String> map = new HashMap<>();
        for (Bean header : mList) {
            map.put(header.getKey(), header.getValue());
        }
        return map;
    }

    public String getMapString(List<Bean> mList) {
        StringBuilder sb = new StringBuilder();
        for (Bean bean : mList) {
            sb.append(bean.getKey() + "====" + bean.getValue() + "\n");
        }
        return sb.toString();
    }


    private void get() {

        Map<String, String> headerMap = getMap(mHeaders);
        Map<String, String> paramMap = getMap(mParams);
        showLoadingDialog();
        RetrofitUtil.getRetrofit("http://gank.io/").create(Api.class).get(urlTv.getText().toString(), headerMap, paramMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);

    }

    List<Bean> mHeaders = new ArrayList<>();

    /**
     * 添加header
     */
    public void addHeader() {
        String header_key = headerKey.getText().toString();
        String header_value = headerValue.getText().toString();
        if (TextUtils.isEmpty(header_key) || TextUtils.isEmpty(header_value)) {
            Toast.makeText(this, "header key or value not null", Toast.LENGTH_SHORT).show();
            return;
        }
        Bean header = new Bean(header_key, header_value);
        for (Bean header1 : mHeaders) {
            if (header.getKey() == header1.getKey()) {
                Toast.makeText(this, String.format("header %s already exists", header_key), Toast.LENGTH_SHORT).show();
                return;
            }
        }
        mHeaders.add(header);
        headerKey.setText("");
        headerValue.setText("");
        headerAdapter.notifyItemInserted(mHeaders.size());
    }


    List<Bean> mParams = new ArrayList<>();

    /**
     * 添加params
     */
    public void addParam() {
        String param_key = paramsKey.getText().toString();
        String param_value = paramsValue.getText().toString();
        if (TextUtils.isEmpty(param_key) || TextUtils.isEmpty(param_value)) {
            Toast.makeText(this, "header key or value not null", Toast.LENGTH_SHORT).show();
            return;
        }
        Bean param = new Bean(param_key, param_value);
        for (Bean param1 : mParams) {
            if (param.getKey() == param1.getKey()) {
                Toast.makeText(this, String.format("param %s already exists", param_key), Toast.LENGTH_SHORT).show();
                return;
            }
        }
        mParams.add(param);
        paramsKey.setText("");
        paramsValue.setText("");
        paramAdapter.notifyItemInserted(mParams.size());
    }

    @Override
    protected void onStop() {
        super.onStop();
        Gson gson = new Gson();
        SharePreferenceUtils.save("params", gson.toJson(mParams));
        SharePreferenceUtils.save("header", gson.toJson(mHeaders));
        SharePreferenceUtils.save("domain", domainEdt.getText().toString());
        SharePreferenceUtils.save("port", portEdt.getText().toString());
        SharePreferenceUtils.save("path", pathEdt.getText().toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }

    Dialog loadingDialog;

    public void showLoadingDialog() {
        if (loadingDialog != null) {
            loadingDialog.show();
            return;
        }
        loadingDialog = new Dialog(this,R.style.loading_dialog);
        loadingDialog.setContentView(LayoutInflater.from(this).inflate(R.layout.dialog_loading, null));
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setCancelable(false);
        loadingDialog.show();
    }

    public void  stopLoadingDialog(){
        if (loadingDialog.isShowing())
            loadingDialog.dismiss();

    }

}
