package com.example.camera_ruler;

import android.os.Bundle;

import java.util.HashMap;
import java.util.Map;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.TextView;

public class OptionActivity extends Activity implements SeekBar.OnSeekBarChangeListener, OnCheckedChangeListener {

    private OptionParam op;
    private CheckBox cbShowGrid;
    SeekBar sbLineWidth;
    SeekBar sbScaleLen;
    SeekBar sbShadowDeep;
    SeekBar sbAlpha;
    SeekBar sbGridCnt;

    Map<SeekBar, TextView> maps = new HashMap<SeekBar, TextView>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        Intent intent = this.getIntent();
        op = (OptionParam) intent.getSerializableExtra("option");

        // show grid
        cbShowGrid = (CheckBox) findViewById(R.id.checkBox1);
        sbGridCnt = (SeekBar) findViewById(R.id.SeekBarGridCnt);
        sbGridCnt.setMax(10);
        maps.put(sbGridCnt, (TextView) findViewById(R.id.textGridCnt));
        sbGridCnt.setOnSeekBarChangeListener(this);
        sbGridCnt.setProgress(op.gridLineCnt);
        cbShowGrid.setOnCheckedChangeListener(this);
        cbShowGrid.setChecked(op.showBaseline);
        sbGridCnt.setEnabled(op.showBaseline);

        // line width of ruler
        sbLineWidth = (SeekBar) findViewById(R.id.seekBarLineWidth);
        maps.put(sbLineWidth, (TextView) findViewById(R.id.textLineWidth));
        sbLineWidth.setOnSeekBarChangeListener(this);
        sbLineWidth.setMax(100);
        if (op.lineWidth > 100)
            op.lineWidth = 100;
        sbLineWidth.setProgress(op.lineWidth);

        // ScaleLen
        sbScaleLen = (SeekBar) findViewById(R.id.SeekBarScaleLen);
        sbScaleLen.setMax(100);
        maps.put(sbScaleLen, (TextView) findViewById(R.id.textScaleWidth));
        sbScaleLen.setOnSeekBarChangeListener(this);

        if (op.scaleWidth > 100)
            op.scaleWidth = 100;
        sbScaleLen.setProgress(op.scaleWidth);

        sbShadowDeep = (SeekBar) findViewById(R.id.seekBarShadowDeep);
        sbShadowDeep.setMax(10);
        maps.put(sbShadowDeep, (TextView) findViewById(R.id.textShadow));

        sbShadowDeep.setOnSeekBarChangeListener(this);
        if (op.shadowDeep > 10)
            op.shadowDeep = 10;
        sbShadowDeep.setProgress(op.shadowDeep);

        sbAlpha = (SeekBar) findViewById(R.id.SeekBarAlpha);
        sbAlpha.setMax(255);
        maps.put(sbAlpha, (TextView) findViewById(R.id.textTransparent));

        sbAlpha.setOnSeekBarChangeListener(this);
        if (op.draw_alpha >= 255)
            op.draw_alpha = 255;
        sbAlpha.setProgress(255 - op.draw_alpha);

        for (Map.Entry<SeekBar, TextView> entry : maps.entrySet()) {
            entry.getValue().setText(String.valueOf(entry.getKey().getProgress()));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.option, menu);
        return true;
    }

    public void OnBackBtnClicked(View v) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        // super.onBackPressed();

        Intent intent = new Intent();
        Bundle bundle = new Bundle();

        op.showBaseline = cbShowGrid.isChecked();
        op.draw_alpha = 255 - sbAlpha.getProgress();
        op.lineWidth = sbLineWidth.getProgress();
        op.scaleWidth = sbScaleLen.getProgress();
        op.shadowDeep = sbShadowDeep.getProgress();
        op.gridLineCnt = sbGridCnt.getProgress();

        bundle.putSerializable("option", op);
        intent.putExtras(bundle);
        setResult(1, intent);
        finish();

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        // TODO Auto-generated method stub
        TextView v = maps.get(seekBar);
        v.setText(String.valueOf(progress));

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // TODO Auto-generated method stub
        if (buttonView == cbShowGrid) {
            sbGridCnt.setEnabled(isChecked);
        }
    }
}
